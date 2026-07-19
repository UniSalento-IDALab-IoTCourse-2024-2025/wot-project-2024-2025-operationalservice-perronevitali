package it.unisalento.faro.configuration.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkus.runtime.StartupEvent;
import it.unisalento.faro.dto.messagesDTO.DirectMessage;
import it.unisalento.faro.dto.messagesDTO.FaroMessage;
import it.unisalento.faro.dto.messagesDTO.PositionUpdateMessage;
import it.unisalento.faro.dto.otherDTO.AreaAuthorizationDTO;
import it.unisalento.faro.service.UserService;
import it.unisalento.faro.service.WorkerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class RabbitMQManager {

    @Inject
    RabbitMQClient rabbitMQClient;

    @Inject
    WorkerService workerService;

    @Inject
    UserService userService;

    private Channel channel;
    private final ObjectMapper mapper = new ObjectMapper();

    void onStart(@Observes StartupEvent ev) {
        try {
            Connection connection = rabbitMQClient.connect();
            channel = connection.createChannel();
            declareExchanges();
            declareAuthorizationQueue();
        } catch (IOException e) {
            throw new RuntimeException("Errore init RabbitMQ", e);
        }
    }

    private void declareExchanges() throws IOException {
        // app → backend
        channel.exchangeDeclare(RabbitMQConstants.EXCHANGE_OUTBOX, "direct", true);
        // backend → app (inbox personali worker)
        channel.exchangeDeclare(RabbitMQConstants.EXCHANGE_INBOX, "direct", true);
        // inter-servizio verso OperationalService
        channel.exchangeDeclare(RabbitMQConstants.EXCHANGE_AREA_UPDATES, "direct", true);
        // topic exchange per notifiche per area (area.{areaId})
        channel.exchangeDeclare(RabbitMQConstants.EXCHANGE_AREAS, "topic", true);
    }

    private void declareAuthorizationQueue() throws IOException {
        channel.queueDeclare(RabbitMQConstants.QUEUE_AUTHORIZATION, true, false, false, null);
        channel.queueBind(
                RabbitMQConstants.QUEUE_AUTHORIZATION,
                RabbitMQConstants.EXCHANGE_AREA_UPDATES,
                RabbitMQConstants.ROUTING_KEY_AUTHORIZATION
        );
        channel.basicConsume(RabbitMQConstants.QUEUE_AUTHORIZATION, false,
                buildConsumer((type, body) -> {
                    AreaAuthorizationDTO dto = mapper.readValue(body, AreaAuthorizationDTO.class);
                    switch (type) {
                        case RabbitMQMessageTypes.AUTHORIZE_AREA ->
                                workerService.addAuthorizedArea(dto.getWorkerId(), dto.getAreaId());
                        case RabbitMQMessageTypes.REVOKE_AREA ->
                                workerService.removeAuthorizedArea(dto.getWorkerId(), dto.getAreaId());
                        default ->
                                System.err.println("Tipo messaggio sconosciuto su QUEUE_AUTHORIZATION: " + type);
                    }
                })
        );
    }

    public void declareUserQueue(String userId) throws IOException {
        String inboxQueue  = RabbitMQConstants.QUEUE_INBOX_PREFIX  + userId;
        String outboxQueue = RabbitMQConstants.QUEUE_OUTBOX_PREFIX + userId;

        channel.queueDeclare(inboxQueue,  true,  false, false, null);
        channel.queueBind(inboxQueue,  RabbitMQConstants.EXCHANGE_INBOX,   userId);

        channel.queueDeclare(outboxQueue, false, false, true, null);
        channel.queueBind(outboxQueue, RabbitMQConstants.EXCHANGE_OUTBOX, userId);

        subscribeToUserOutbox(outboxQueue, userId);
    }

    private void subscribeToUserOutbox(String queueName, String userId) throws IOException {
        channel.basicConsume(queueName, false, buildConsumer((messageType, body) -> {
            switch (messageType) {
                case RabbitMQMessageTypes.POSITION_UPDATE -> handlePositionUpdate(userId, body);
                case RabbitMQMessageTypes.DIRECT_MESSAGE  -> handleDirectMessage(userId, body);
                default -> System.err.println("Tipo messaggio sconosciuto: " + messageType);
            }
        }));
    }

    private void handlePositionUpdate(String userId, byte[] body) throws Exception {
        FaroMessage message = mapper.readValue(body, FaroMessage.class);
        PositionUpdateMessage positionUpdate = mapper.convertValue(
                message.getPayload(), PositionUpdateMessage.class
        );
        userService.updateCurrentArea(
                userId,
                positionUpdate.getAreaId(),
                positionUpdate.getPreviousAreaId()
        );
    }

    private void handleDirectMessage(String userId, byte[] body) throws Exception {
        FaroMessage message = mapper.readValue(body, FaroMessage.class);
        DirectMessage directMessage = mapper.convertValue(
                message.getPayload(), DirectMessage.class
        );
        publish(
                RabbitMQConstants.EXCHANGE_INBOX,
                directMessage.getRecipientId(),
                RabbitMQMessageTypes.DIRECT_MESSAGE,
                new FaroMessage(
                        RabbitMQMessageTypes.DIRECT_MESSAGE,
                        new DirectMessage(userId, directMessage.getText())
                )
        );
    }

    public void publish(String exchange, String routingKey,
                        String messageType, Object dto) throws IOException {
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .type(messageType)
                .contentType("application/json")
                .build();

        String payload = mapper.writeValueAsString(dto);
        channel.basicPublish(
                exchange,
                routingKey,
                properties,
                payload.getBytes(StandardCharsets.UTF_8)
        );
    }

    private DefaultConsumer buildConsumer(MessageHandler handler) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println(">>> MESSAGGIO RICEVUTO: type=" + properties.getType() + " body=" + new String(body));
                try {
                    handler.handle(properties.getType(), body);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    channel.basicNack(envelope.getDeliveryTag(), false, false);
                }
            }
        };
    }

    @FunctionalInterface
    private interface MessageHandler {
        void handle(String messageType, byte[] body) throws Exception;
    }
}
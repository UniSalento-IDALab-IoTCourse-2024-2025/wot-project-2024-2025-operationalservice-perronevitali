package it.unisalento.faro.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkus.runtime.StartupEvent;
import it.unisalento.faro.dto.otherDTO.AreaStatusUpdateDTO;
import it.unisalento.faro.dto.otherDTO.DangerIndexUpdateDTO;
import it.unisalento.faro.dto.otherDTO.PositionUpdateDTO;
import it.unisalento.faro.dto.otherDTO.SensorReadingUpdateDTO;
import it.unisalento.faro.service.AreaService;
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
    AreaService areaService;

    private Channel channel;
    private final ObjectMapper mapper = new ObjectMapper();

    void onStart(@Observes StartupEvent ev) {
        try {
            Connection connection = rabbitMQClient.connect();
            channel = connection.createChannel();
            declareExchanges();
            declareInterServiceQueues();
            subscribeToPositions();
            subscribeToInterServiceMessages();
        } catch (IOException e) {
            throw new RuntimeException("Errore init RabbitMQ", e);
        }
    }

    // ---------------------------------------------------------------
    // Dichiarazione exchange e code
    // ---------------------------------------------------------------

    private void declareExchanges() throws IOException {
        channel.exchangeDeclare("faro.position", "direct", true);
        channel.exchangeDeclare("faro.inbox",    "direct", true);
        channel.exchangeDeclare("faro.alerts",   "topic",  true);
    }

    private void declareInterServiceQueues() throws IOException {
        // code in ingresso da altri microservizi (durable)
        channel.queueDeclare("faro.area.update.dangerindex", true, false, false, null);
        channel.queueDeclare("faro.area.update.status",      true, false, false, null);
        channel.queueDeclare("faro.area.update.sensors",     true, false, false, null);
    }

    // ---------------------------------------------------------------
    // Consumer posizioni worker (app → backend)
    // ---------------------------------------------------------------

    private void subscribeToPositions() throws IOException {
        String queueName = "backend.position.all";
        channel.queueDeclare(queueName, false, false, true, null);
        channel.queueBind(queueName, "faro.position", "#");

        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String userId = envelope.getRoutingKey();
                PositionUpdateDTO dto = mapper.readValue(body, PositionUpdateDTO.class);
                try {
                    workerService.updateCurrentArea(userId, dto.getAreaId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ---------------------------------------------------------------
    // Consumer messaggi inter-servizio
    // ---------------------------------------------------------------

    private void subscribeToInterServiceMessages() throws IOException {
        subscribeToDangerIndexUpdates();
        subscribeToStatusUpdates();
        subscribeToSensorReadings();
    }

    private void subscribeToDangerIndexUpdates() throws IOException {
        channel.basicConsume("faro.area.update.dangerindex", true,
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body) throws IOException {
                        DangerIndexUpdateDTO dto = mapper.readValue(body, DangerIndexUpdateDTO.class);
                        try {
                            areaService.updateDangerIndex(dto.getAreaId(), dto.getDangerIndex());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private void subscribeToStatusUpdates() throws IOException {
        channel.basicConsume("faro.area.update.status", true,
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body) throws IOException {
                        AreaStatusUpdateDTO dto = mapper.readValue(body, AreaStatusUpdateDTO.class);
                        try {
                            areaService.updateStatus(dto.getAreaId(), dto.getStatus());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private void subscribeToSensorReadings() throws IOException {
        channel.basicConsume("faro.area.update.sensors", true,
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body) throws IOException {
                        SensorReadingUpdateDTO dto = mapper.readValue(body, SensorReadingUpdateDTO.class);
                        try {
                            areaService.updateSensorReadings(
                                    dto.getAreaId(),
                                    dto.getTemperature(),
                                    dto.getHumidity()
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    // ---------------------------------------------------------------
    // Publisher
    // ---------------------------------------------------------------

    public void declareUserQueue(String userId) throws IOException {
        String queueName = "faro.inbox." + userId;
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, "faro.inbox", userId);
    }

    public void publishToInbox(String userId, String payload) throws IOException {
        channel.basicPublish(
                "faro.inbox",
                userId,
                null,
                payload.getBytes(StandardCharsets.UTF_8)
        );
    }

    public void publishAlert(String areaId, String payload) throws IOException {
        channel.basicPublish(
                "faro.alerts",
                areaId,
                null,
                payload.getBytes(StandardCharsets.UTF_8)
        );
    }
}
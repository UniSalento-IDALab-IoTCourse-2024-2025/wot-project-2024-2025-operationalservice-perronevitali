package it.unisalento.faro.configuration.rabbitmq;

import io.quarkus.runtime.StartupEvent;
import it.unisalento.faro.configuration.rabbitmq.RabbitMQManager;
import it.unisalento.faro.domain.User;
import it.unisalento.faro.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class QueueSeeder {

    @Inject
    UserRepository userRepository;

    @Inject
    RabbitMQManager rabbitMQManager;

    void onStart(@Observes StartupEvent event) {
        List<User> users = userRepository.listAll();
        for (User user : users) {
            try {
                rabbitMQManager.declareUserQueue(user.getId());
                System.out.println("FARO: coda ricreata per " + user.getEmail());
            } catch (IOException e) {
                System.err.println("FARO: errore coda per " + user.getId() + ": " + e.getMessage());
            }
        }
    }
}
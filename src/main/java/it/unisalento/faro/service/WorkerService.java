package it.unisalento.faro.service;

import it.unisalento.faro.configuration.rabbitmq.RabbitMQConstants;
import it.unisalento.faro.configuration.rabbitmq.RabbitMQManager;
import it.unisalento.faro.configuration.rabbitmq.RabbitMQMessageTypes;
import it.unisalento.faro.domain.User;
import it.unisalento.faro.domain.Worker;
import it.unisalento.faro.dto.messagesDTO.AreaUnauthorizedMessage;
import it.unisalento.faro.dto.messagesDTO.FaroMessage;
import it.unisalento.faro.dto.login_and_registration.WorkerRegistrationDTO;
import it.unisalento.faro.dto.main.WorkerDTO;
import it.unisalento.faro.dto.otherDTO.PositionUpdateDTO;
import it.unisalento.faro.exceptions.EmailAlreadyExistsException;
import it.unisalento.faro.repositories.UserRepository;
import org.bson.Document;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WorkerService {

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    RabbitMQManager rabbitMQManager;

    public List<WorkerDTO> getAllWorkers() {
        List<User> workers = userRepository.list("_t", "worker");
        List<WorkerDTO> result = new ArrayList<>();
        for (User user : workers) {
            result.add(toWorkerDTO(user));
        }
        return result;
    }

    public List<WorkerDTO> getAllWorkersByArea(String areaId) {
        List<User> workers = userRepository.list(
                new Document("_t", "worker").append("currentAreaId", areaId)
        );
        List<WorkerDTO> result = new ArrayList<>();
        for (User user : workers) {
            result.add(toWorkerDTO(user));
        }
        return result;
    }

    public WorkerDTO getWorkerById(String id) throws Exception {
        User user = userRepository.findById(id);
        if (user == null || !(user instanceof Worker)) {
            throw new Exception("Worker non trovato");
        }
        return toWorkerDTO(user);
    }

    public WorkerDTO getWorkerByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !(user instanceof Worker)) {
            throw new Exception("Worker non trovato");
        }
        return toWorkerDTO(user);
    }

    public WorkerDTO register(WorkerRegistrationDTO registrationDTO) throws EmailAlreadyExistsException {
        Optional<User> existingUser = userRepository.findByEmail(registrationDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        Worker worker = new Worker();
        worker.setNome(registrationDTO.getNome());
        worker.setCognome(registrationDTO.getCognome());
        worker.setEmail(registrationDTO.getEmail());
        worker.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        worker.setAuthorizedAreaIds(new ArrayList<>());

        userRepository.persist(worker);

        try {
            rabbitMQManager.declareUserQueue(worker.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return toWorkerDTO(worker);
    }

    public WorkerDTO updateWorkerById(String id, WorkerDTO workerDto) throws Exception {
        User user = userRepository.findById(id);
        if (user == null || !(user instanceof Worker)) {
            throw new Exception("Worker non trovato");
        }

        Worker worker = (Worker) user;
        worker.setNome(workerDto.getNome());
        worker.setCognome(workerDto.getCognome());

        userRepository.update(worker);
        return toWorkerDTO(worker);
    }

    public WorkerDTO assignAreas(String id, ArrayList<String> areaIds) throws Exception {
        User user = userRepository.findById(id);
        if (user == null || !(user instanceof Worker)) {
            throw new Exception("Worker non trovato");
        }

        Worker worker = (Worker) user;
        worker.setAuthorizedAreaIds(areaIds);

        userRepository.update(worker);
        return toWorkerDTO(worker);
    }

    public WorkerDTO updateCurrentArea(String userId, String areaId,
                                       String previousAreaId) throws Exception {
        User user = userRepository.findById(userId);
        if (user == null || !(user instanceof Worker)) {
            throw new Exception("Worker non trovato");
        }

        Worker worker = (Worker) user;
        worker.setCurrentAreaId(areaId);
        userRepository.update(worker);

        // verifica autorizzazione inoltre notifica worker se area non autorizzata
        if (worker.getAuthorizedAreaIds() == null || !worker.getAuthorizedAreaIds().contains(areaId)) {
            try {
                rabbitMQManager.publish(
                        RabbitMQConstants.EXCHANGE_INBOX,
                        worker.getId(),
                        RabbitMQMessageTypes.AREA_UNAUTHORIZED,
                        new FaroMessage(RabbitMQMessageTypes.AREA_UNAUTHORIZED, new AreaUnauthorizedMessage(areaId))
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // notifica OperationalService dell'aggiornamento posizione
        try {
            PositionUpdateDTO positionUpdate = new PositionUpdateDTO(
                    userId, areaId, previousAreaId
            );
            rabbitMQManager.publish(
                    RabbitMQConstants.EXCHANGE_AREA_UPDATES,
                    RabbitMQConstants.ROUTING_KEY_POSITION,
                    RabbitMQMessageTypes.POSITION_UPDATE,
                    positionUpdate
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return toWorkerDTO(worker);
    }

    public WorkerDTO deleteWorkerById(String id) throws Exception {
        User user = userRepository.findById(id);
        if (user == null || !(user instanceof Worker)) {
            throw new Exception("Worker non trovato");
        }

        userRepository.deleteById(id);
        return toWorkerDTO(user);
    }

    public List<WorkerDTO> getAuthorizedWorkersByArea(String areaId) {
        List<User> all = userRepository.list("_t", "worker");
        List<WorkerDTO> result = new ArrayList<>();
        for (User user : all) {
            Worker worker = (Worker) user;
            if (worker.getAuthorizedAreaIds() != null
                    && worker.getAuthorizedAreaIds().contains(areaId)) {
                result.add(toWorkerDTO(worker));
            }
        }
        return result;
    }

    public List<String> getAuthorizedAreaIds(String workerId) throws Exception {
        User user = userRepository.findById(workerId);
        if (user == null || !(user instanceof Worker)) {
            throw new Exception("Worker non trovato");
        }
        Worker worker = (Worker) user;
        if (worker.getAuthorizedAreaIds() == null) {
            return new ArrayList<>();
        }
        return worker.getAuthorizedAreaIds();
    }

    public WorkerDTO toWorkerDTO(User user) {
        Worker worker = (Worker) user;
        WorkerDTO dto = new WorkerDTO();
        dto.setId(worker.getId());
        dto.setNome(worker.getNome());
        dto.setCognome(worker.getCognome());
        dto.setEmail(worker.getEmail());
        dto.setCurrentAreaId(worker.getCurrentAreaId());
        if (worker.getAuthorizedAreaIds() != null) {
            dto.setAuthorizedAreaIds(worker.getAuthorizedAreaIds());
        } else {
            dto.setAuthorizedAreaIds(new ArrayList<>());
        }
        return dto;
    }
}
package it.unisalento.faro.service.areas;

import it.unisalento.faro.configuration.rabbitmq.RabbitMQConstants;
import it.unisalento.faro.configuration.rabbitmq.RabbitMQManager;
import it.unisalento.faro.configuration.rabbitmq.RabbitMQMessageTypes;
import it.unisalento.faro.domain.Area;
import it.unisalento.faro.dto.main.AreaDTO;
import it.unisalento.faro.dto.messagesDTO.AreaAlertMessage;
import it.unisalento.faro.dto.messagesDTO.AreaSafeMessage;
import it.unisalento.faro.dto.messagesDTO.FaroMessage;
import it.unisalento.faro.exceptions.AreaNotFoundException;
import it.unisalento.faro.repositories.AreaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AreaService {

    @Inject
    AreaRepository areaRepository;

    @Inject
    RabbitMQManager rabbitMQManager;

    public List<AreaDTO> getAllAreas() {
        List<Area> areas = areaRepository.listAll();
        List<AreaDTO> result = new ArrayList<>();
        for (Area area : areas) {
            result.add(toAreaDTO(area));
        }
        return result;
    }

    public AreaDTO getAreaById(String id) throws AreaNotFoundException {
        Area area = areaRepository.findById(id);
        if (area == null) {
            throw new AreaNotFoundException();
        }
        return toAreaDTO(area);
    }

    public AreaDTO createArea(AreaDTO areaDto) {
        Area area = new Area();
        area.setName(areaDto.getName());
        area.setBeaconMAC(areaDto.getBeaconMAC());
        area.setThresholdTemperature(areaDto.getThresholdTemperature());
        area.setThresholdHumidity(areaDto.getThresholdHumidity());
        area.setIpRaspberry(areaDto.getIpRaspberry());
        area.setStatus(Area.OK);
        area.setTotalDangerIndex(0);
        area.setWorkerIdsInArea(new ArrayList<>());

        areaRepository.persist(area);
        return toAreaDTO(area);
    }

    public AreaDTO updateAreaById(String id, AreaDTO areaDto) throws AreaNotFoundException {
        Area area = areaRepository.findById(id);
        if (area == null) {
            throw new AreaNotFoundException();
        }

        area.setName(areaDto.getName());
        area.setBeaconMAC(areaDto.getBeaconMAC());
        area.setThresholdTemperature(areaDto.getThresholdTemperature());
        area.setThresholdHumidity(areaDto.getThresholdHumidity());
        area.setIpRaspberry(areaDto.getIpRaspberry());

        areaRepository.update(area);
        return toAreaDTO(area);
    }

    public AreaDTO deleteAreaById(String id) throws AreaNotFoundException {
        Area area = areaRepository.findById(id);
        if (area == null) {
            throw new AreaNotFoundException();
        }
        areaRepository.deleteById(id);
        return toAreaDTO(area);
    }

    public AreaDTO toAreaDTO(Area area) {
        AreaDTO dto = new AreaDTO();
        dto.setId(area.getId());
        dto.setName(area.getName());
        dto.setBeaconMAC(area.getBeaconMAC());
        dto.setThresholdTemperature(area.getThresholdTemperature());
        dto.setThresholdHumidity(area.getThresholdHumidity());
        dto.setIpRaspberry(area.getIpRaspberry());
        dto.setStatus(area.getStatus());
        dto.setTotalDangerIndex(area.getTotalDangerIndex());
        dto.setCurrentTemperature(area.getCurrentTemperature());
        dto.setCurrentHumidity(area.getCurrentHumidity());

        if (area.getWorkerIdsInArea() != null) {
            dto.setWorkerIdsInArea(area.getWorkerIdsInArea());
        } else {
            dto.setWorkerIdsInArea(new ArrayList<>());
        }

        return dto;
    }

    public void updateDangerIndex(String areaId, double dangerIndex) throws AreaNotFoundException {
        Area area = areaRepository.findById(areaId);
        if (area == null) {
            throw new AreaNotFoundException();
        }
        area.setTotalDangerIndex(dangerIndex);
        areaRepository.update(area);
    }

    // updateStatus — quando l'area cambia stato
    // se diventa ALERT o DANGER → AREA_ALERT a tutti i worker presenti nell'area
    // se torna OK → AREA_SAFE a tutti i worker presenti nell'area

    public void updateStatus(String areaId, int status) throws AreaNotFoundException {
        Area area = areaRepository.findById(areaId);
        if (area == null) throw new AreaNotFoundException();

        area.setStatus(status);
        areaRepository.update(area);

        if (status == Area.ALERT || status == Area.DANGER) {
            FaroMessage message = new FaroMessage(
                    RabbitMQMessageTypes.AREA_ALERT,
                    new AreaAlertMessage(areaId, area.getName(), status,
                            area.getCurrentTemperature(), area.getCurrentHumidity())
            );
            // manda a tutti i worker presenti nell'area
            if (area.getWorkerIdsInArea() != null) {
                for (String workerId : area.getWorkerIdsInArea()) {
                    try {
                        rabbitMQManager.publish(
                                RabbitMQConstants.EXCHANGE_INBOX,
                                workerId,
                                RabbitMQMessageTypes.AREA_ALERT,
                                message
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // broadcast sull'exchange alerts
            try {
                rabbitMQManager.publish(
                        RabbitMQConstants.EXCHANGE_ALERTS,
                        areaId,
                        RabbitMQMessageTypes.AREA_ALERT,
                        message
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (status == Area.OK) {
            FaroMessage message = new FaroMessage(
                    RabbitMQMessageTypes.AREA_SAFE,
                    new AreaSafeMessage(areaId, area.getName())
            );

            if (area.getWorkerIdsInArea() != null) {
                for (String workerId : area.getWorkerIdsInArea()) {
                    try {
                        rabbitMQManager.publish(
                                RabbitMQConstants.EXCHANGE_INBOX,
                                workerId,
                                RabbitMQMessageTypes.AREA_SAFE,
                                message
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                rabbitMQManager.publish(
                        RabbitMQConstants.EXCHANGE_ALERTS,
                        areaId,
                        RabbitMQMessageTypes.AREA_SAFE,
                        message
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateSensorReadings(String areaId, double temperature, double humidity) throws AreaNotFoundException {
        Area area = areaRepository.findById(areaId);
        if (area == null) {
            throw new AreaNotFoundException();
        }
        area.setCurrentTemperature(temperature);
        area.setCurrentHumidity(humidity);
        areaRepository.update(area);
    }

    public int getWorkerCount(String areaId) throws AreaNotFoundException {
        Area area = areaRepository.findById(areaId);
        if (area == null) {
            throw new AreaNotFoundException();
        }
        if (area.getWorkerIdsInArea() == null) {
            return 0;
        }
        return area.getWorkerIdsInArea().size();
    }
}
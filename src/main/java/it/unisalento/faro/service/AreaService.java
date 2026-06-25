package it.unisalento.faro.service;

import it.unisalento.faro.domain.Area;
import it.unisalento.faro.dto.main.AreaDTO;
import it.unisalento.faro.exceptions.AreaNotFoundException;
import it.unisalento.faro.repositories.AreaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AreaService {

    @Inject
    AreaRepository areaRepository;

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

    public void updateStatus(String areaId, int status) throws AreaNotFoundException {
        Area area = areaRepository.findById(areaId);
        if (area == null) {
            throw new AreaNotFoundException();
        }
        area.setStatus(status);
        areaRepository.update(area);
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
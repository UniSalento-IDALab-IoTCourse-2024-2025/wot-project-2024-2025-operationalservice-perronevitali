package it.unisalento.faro.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import it.unisalento.faro.domain.Area;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AreaRepository implements PanacheMongoRepositoryBase<Area, String> {

    public Optional<Area> findByName(String name) {
        return find("name", name).firstResultOptional();
    }

    public Optional<Area> findByBeaconMAC(String beaconMAC) {
        return find("beaconMAC", beaconMAC).firstResultOptional();
    }
}
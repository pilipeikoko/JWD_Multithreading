package org.epam.multithreading.creator;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.multithreading.entity.Ship;
import org.epam.multithreading.entity.Task;
import org.epam.multithreading.exception.CustomException;

import java.util.List;
import java.util.stream.Collectors;

public class ShipCreator {
    private static final Logger LOGGER = LogManager.getLogger();

    public List<Ship> createShips(List<String> ships) throws CustomException {
        List<Ship> shipList;
        try {
            shipList = ships.stream()
                    .map(Task::valueOf)
                    .map(Ship::new)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.ERROR, "Couldn't create ships");
            throw new CustomException("Couldn't create ships");
        }
        LOGGER.log(Level.INFO, "Ships created successfully: Amount of ships: " + shipList.size());
        return shipList;
    }
}

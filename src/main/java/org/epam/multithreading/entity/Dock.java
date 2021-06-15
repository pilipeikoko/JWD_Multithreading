package org.epam.multithreading.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.multithreading.exception.CustomException;
import org.epam.multithreading.util.DockIdGenerator;

import java.util.concurrent.TimeUnit;

public class Dock {
    private final Logger LOGGER = LogManager.getLogger();

    private final long id;
    private int capacity;
    private int currentValue;

    {
        id = DockIdGenerator.getId();
    }

    public Dock(int capacity, int currentValue) {
        this.capacity = capacity;
        this.currentValue = currentValue;
    }

    public Dock(){

    }

    public void handleShip(Ship ship) throws CustomException {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
            Thread.currentThread().interrupt();
        }
        Port seaPort = Port.getInstance();
        Task task = ship.getShipTask();

        switch (task) {
            case LOAD -> seaPort.loadContainer();
            case UNLOAD -> seaPort.unloadContainer();
            default -> {
                LOGGER.log(Level.ERROR, "Unknown task type" + task);
                throw new CustomException("Unknown task type" + task);
            }
        }
        LOGGER.log(Level.INFO, "Ship handled: " + ship.toString());
    }

    public long getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Dock dock = (Dock) obj;

        boolean result = id == dock.id
                && capacity == dock.capacity
                && currentValue == dock.currentValue;

        return result;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 11 * result + capacity;
        result = 17 * result + currentValue;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("Dock{");
        stringBuilder.append("LOGGER=").append(LOGGER);
        stringBuilder.append(", id=").append(id);
        stringBuilder.append(", capacity=").append(capacity);
        stringBuilder.append(", currentValue=").append(currentValue);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}

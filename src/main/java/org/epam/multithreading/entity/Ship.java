package org.epam.multithreading.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.multithreading.exception.CustomException;
import org.epam.multithreading.util.ShipIdGenerator;

public class Ship implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();

    private final long id;
    private int capacity;
    private boolean isLoading;
    private int currentValue;
    private Task task;

    {
        this.id = ShipIdGenerator.getId();
        task = Task.LOAD;
    }

    public Ship(Task task){
        this.task = task;
    }

    public Ship(int capacity, boolean isLoading, int currentValue) {
        this.capacity = capacity;
        this.isLoading = isLoading;
        this.currentValue = currentValue;
    }


    @Override
    public void run() {
        try {
            Port port = Port.getInstance();
            Dock dock = port.obtainDock();
            dock.handleShip(this);
            port.releaseDock(dock);
        } catch (CustomException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "Ship handled " + this);
    }

    public long getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public Task getShipTask() {
        return task;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Ship ship = (Ship) obj;

        boolean result = id == ship.id
                && capacity == ship.capacity
                && isLoading == ship.isLoading
                && currentValue == ship.currentValue
                && (task != null && task == ship.task);

        return result;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 7 * result + capacity;
        result = 11 * result + (isLoading ? 1 : 0);
        result = 17 * result + currentValue;
        result = 31 * result + (task != null ? task.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("Ship{");
        stringBuilder.append("id=").append(id);
        stringBuilder.append(", capacity=").append(capacity);
        stringBuilder.append(", isLoading=").append(isLoading);
        stringBuilder.append(", currentValue=").append(currentValue);
        stringBuilder.append(", task=").append(task);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}

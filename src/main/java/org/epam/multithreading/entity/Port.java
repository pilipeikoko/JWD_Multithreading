package org.epam.multithreading.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String PROPERTY_FILE_PATH = "port.properties";

    private static final AtomicBoolean isInstanceInitialized = new AtomicBoolean(false);
    private static Port instance;

    private final Deque<Dock> freeDocks;
    private final Deque<Dock> busyDocks;

    private final Lock dockLocking;
    private final Lock storageLocking;

    private final Condition freeDockCondition;
    private final Condition unloadAvailableCondition;
    private final Condition loadAvailableCondition;

    private final int DOCK_AMOUNT;
    private final int CAPACITY;

    private int currentAmount;

    private int loadAmount;
    private int unloadAmount;

    {
        freeDocks = new ArrayDeque<>();
        busyDocks = new ArrayDeque<>();

        dockLocking = new ReentrantLock(true);
        storageLocking = new ReentrantLock(true);

        freeDockCondition = dockLocking.newCondition();
        unloadAvailableCondition = storageLocking.newCondition();
        loadAvailableCondition = storageLocking.newCondition();
    }

    private Port() {
        InputStream propertyFileStream = getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_PATH);
        Properties properties = new Properties();
        try {
            properties.load(propertyFileStream);
        } catch (IOException e) {
            LOGGER.log(Level.WARN, "Input stream is valid");
        }

        CAPACITY = Integer.parseInt(properties.getProperty("value"));
        DOCK_AMOUNT = Integer.parseInt(properties.getProperty("docks"));
        currentAmount = Integer.parseInt(properties.getProperty("containers"));

        for (int i = 0; i < DOCK_AMOUNT; i++) {
            freeDocks.addLast(new Dock());
        }
        testTask();
    }

    public static Port getInstance() {
        while (instance == null) {
            if (isInstanceInitialized.compareAndSet(false, true)) {
                instance = new Port();
            }
        }
        return instance;
    }

    public Dock obtainDock() {
        LOGGER.log(Level.INFO, "Obtaining new dock");
        try {
            dockLocking.lock();
            try {
                if (freeDocks.isEmpty()) {
                    freeDockCondition.await();
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.ERROR, "Couldn't obtain dock");
                Thread.currentThread().interrupt();
            }
            Dock dock = freeDocks.removeLast();
            busyDocks.addLast(dock);
            LOGGER.log(Level.INFO, "Obtained new dock! " + dock.getId());
            return dock;
        } finally {
            dockLocking.unlock();
        }
    }

    public void releaseDock(Dock dock) {
        try {
            dockLocking.lock();
            busyDocks.remove(dock);
            freeDocks.addLast(dock);
            freeDockCondition.signal();
            LOGGER.log(Level.INFO, "Dock released");
        } finally {
            dockLocking.unlock();
        }
    }

    public void loadContainer() {
        try {
            storageLocking.lock();
            LOGGER.log(Level.INFO, "Loading started");
            if (currentAmount == 0) {
                try {
                    LOGGER.log(Level.DEBUG, "Loading...");
                    loadAmount++;
                    loadAvailableCondition.await();
                    loadAmount--;
                } catch (InterruptedException e) {
                    LOGGER.log(Level.ERROR, "Couldn't load container!");
                    Thread.currentThread().interrupt();
                }
            }
            currentAmount--;
            LOGGER.log(Level.INFO, "Container are loaded. Amount: " + currentAmount);
        } finally {
            storageLocking.unlock();
        }
    }

    public void unloadContainer() {
        try {
            storageLocking.lock();
            LOGGER.log(Level.INFO, "Unloading container");
            if (currentAmount == CAPACITY) {
                try {
                    unloadAmount++;
                    unloadAvailableCondition.await();
                    unloadAmount--;
                } catch (InterruptedException e) {
                    LOGGER.log(Level.ERROR, "Couldn't unload container!");
                    Thread.currentThread().interrupt();
                }
            }
            currentAmount++;
            LOGGER.log(Level.INFO, "Container unloaded! Amount: " + currentAmount);
        } finally {
            storageLocking.unlock();
        }
    }

    private void testTask() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                performTestTask();
            }
        }, 1000, 100);
    }

    private void performTestTask() {
        final double maxLoad = 0.8;
        final double minLoad = 0.2;

        try {
            storageLocking.lock();
            double currentLoad = (double) currentAmount / CAPACITY;

            if (currentLoad < minLoad) {
                currentAmount += (int) (minLoad * CAPACITY + 1);
            } else if (currentLoad > maxLoad) {
                currentAmount -= (int) (minLoad * CAPACITY + 1);
            }

            int loadSignalCount = Math.min(currentAmount, loadAmount);
            for (int i = 0; i < loadSignalCount; i++) {
                loadAvailableCondition.signal();
            }

            int unloadSignalCount = Math.min(CAPACITY - currentAmount, unloadAmount);
            for (int i = 0; i < unloadSignalCount; i++) {
                unloadAvailableCondition.signal();
            }
        } finally {
            storageLocking.unlock();
        }
    }
}

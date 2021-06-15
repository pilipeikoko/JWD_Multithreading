package org.epam.multithreading.main;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.multithreading.creator.ShipCreator;
import org.epam.multithreading.entity.Ship;
import org.epam.multithreading.exception.CustomException;
import org.epam.multithreading.reader.PortFileReader;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SHIP_FILE_PATH = "files/shipData.txt";

    public static void main(String[] args) {
        URL fileURL = Main.class.getClassLoader().getResource(SHIP_FILE_PATH);
        File shipFile = new File(fileURL.getFile());
        PortFileReader reader = new PortFileReader();
        List<String> fileLines;
        List<Ship> ships;
        try {
            fileLines = reader.readAll(shipFile.getAbsolutePath());
            ShipCreator creator = new ShipCreator();
            ships = creator.createShips(fileLines);
        } catch (CustomException e) {
            LOGGER.log(Level.FATAL, e.getMessage());
            return;
        }
        ExecutorService service = Executors.newFixedThreadPool(ships.size());
        ships.forEach(service::execute);
        service.shutdown();
    }
}
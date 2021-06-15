package org.epam.multithreading.reader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.multithreading.exception.CustomException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PortFileReader {
    private static final Logger LOGGER = LogManager.getLogger();

    public List<String> readAll(String filePath) throws CustomException {
        Path path = Paths.get(filePath);
        List<String> textLines;
        try (Stream<String> fileLines = Files.lines(path)) {
            textLines = fileLines.collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Couldn't read file: " + filePath);
            throw new CustomException("Couldn't read file: " + filePath);
        }
        LOGGER.log(Level.INFO, "File was readen: " + filePath);
        return textLines;
    }
}

package org.epam.multithreading.util;

public class DockIdGenerator {
    private static long id;

    public static long getId(){
        return id++;
    }
}

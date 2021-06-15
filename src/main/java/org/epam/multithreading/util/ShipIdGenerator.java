package org.epam.multithreading.util;

public class ShipIdGenerator {
    private static long id;

    public static long getId(){
        return id++;
    }
}

package com.gupta.compact_headers;

import org.openjdk.jol.info.ClassLayout;

public class InspectHeader {
    static class Shipment {
        int trackingNumber = 123;
        int status = 2;
    }

    public static void main(String[] args) {
        Shipment shipment = new Shipment();

        System.out.println("Before identity hashing:");
        System.out.println(ClassLayout.parseInstance(shipment).toPrintable());

        int hash = System.identityHashCode(shipment);
        System.out.printf("Identity hash: 0x%x%n", hash);

        System.out.println("After identity hashing:");
        System.out.println(ClassLayout.parseInstance(shipment).toPrintable());
    }
}
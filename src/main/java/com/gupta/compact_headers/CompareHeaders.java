package com.gupta.compact_headers;

import org.openjdk.jol.info.ClassLayout;

public class CompareHeaders {

    static class Shipment {
        int trackingNumber = 123;
        int status = 2;
    }

    public static void main(String[] args) {
        Shipment shipment = new Shipment();

        System.out.println(
                ClassLayout.parseInstance(shipment).toPrintable()
        );
    }
}
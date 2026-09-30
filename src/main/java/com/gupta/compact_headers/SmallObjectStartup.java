package com.gupta.compact_headers;

import java.lang.management.ManagementFactory;
import java.util.Locale;
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.ThreadMXBean;

public class SmallObjectStartup {
    
    static final int COUNT = 2_000_000;
    static volatile Shipment[] retained;
    
    static class Shipment {
        int trackingNumber;
        int status;
        Shipment(int trackingNumber, int status) {
            this.trackingNumber = trackingNumber;
            this.status = status;
        }
    }
    
    public static void main(String[] args) {
        var bean = ManagementFactory.getPlatformMXBean(ThreadMXBean.class);
        
        if (bean == null || !bean.isThreadAllocatedMemorySupported()) {
            throw new UnsupportedOperationException("Allocation counter unavailable");
        }
        
        bean.setThreadAllocatedMemoryEnabled(true);
        var vm = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println("Compact headers: "
                + vm.getVMOption("UseCompactObjectHeaders").getValue());
        
        long allocatedBefore = bean.getCurrentThreadAllocatedBytes();
        long start = System.nanoTime();
        
        Shipment[] shipments = new Shipment[COUNT];
        for (int i = 0; i < COUNT; i++) {
            shipments[i] = new Shipment(i + 1, i & 3);
        }
        
        retained = shipments; // Keep the objects reachable.
        
        long elapsed = System.nanoTime() - start;
        long allocated = bean.getCurrentThreadAllocatedBytes() - allocatedBefore;
        
        System.out.printf(Locale.ROOT,
                "Initialization: %.3f ms%nMain-thread allocation: %.2f MiB%n",
                elapsed / 1_000_000.0, allocated / (1024.0 * 1024));
        
        Shipment last = retained[COUNT - 1];
        System.out.println("Last shipment: " + last.trackingNumber + ", " + last.status);
    }
}
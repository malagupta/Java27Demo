package com.gupta.compact_headers;

import java.lang.management.ManagementFactory;
import java.util.Locale;
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.ThreadMXBean;

public class ArrayStartup {
    static final int COUNT = 2_000_000;
    static volatile int[] retained;
    
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
        
        int[] shipments = new int[COUNT * 2];
        
        for (int i = 0; i < COUNT; i++) {
            shipments[2 * i] = i + 1;
            shipments[2 * i + 1] = i & 3;
        }
        
        retained = shipments; // One array holding all the primitive values.
        
        long elapsed = System.nanoTime() - start;
        long allocated = bean.getCurrentThreadAllocatedBytes() - allocatedBefore;
        
        System.out.printf(Locale.ROOT,
                "Initialization: %.3f ms%nMain-thread allocation: %.2f MiB%n",
                elapsed / 1_000_000.0, allocated / (1024.0 * 1024));
        
        int last = (COUNT - 1) * 2;
        
        System.out.println("Last shipment: " + retained[last] + ", " + retained[last + 1]);
    }
}
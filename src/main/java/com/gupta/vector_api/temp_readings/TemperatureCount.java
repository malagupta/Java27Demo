package com.gupta.vector_api.temp_readings;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class TemperatureCount {
    private static final VectorSpecies<Float> SPECIES =
            FloatVector.SPECIES_PREFERRED;

    static int countAbove(float[] readings, float threshold) {
        int count = 0;
        int bound = SPECIES.loopBound(readings.length);
        int i = 0;
        for (; i < bound; i += SPECIES.length()) {
            var values = FloatVector.fromArray(SPECIES, readings, i);
            var hot = values.compare(VectorOperators.GT, threshold);
            count += hot.trueCount();
        }
        for (; i < readings.length; i++) {
            if (readings[i] > threshold) {
                count++;
            }
        }
        return count;
    }
    public static void main(String[] args) {
        float[] readings = new float[SPECIES.length() * 2 + 3];
        for (int i = 0; i < readings.length; i++) {
            readings[i] = (i % 3 == 0) ? 35.0f : 22.0f;
        }
        int expected = (readings.length + 2) / 3;
        System.out.println("Readings above 30: " + countAbove(readings, 30.0f));
        System.out.println("Expected: " + expected);
    }
}
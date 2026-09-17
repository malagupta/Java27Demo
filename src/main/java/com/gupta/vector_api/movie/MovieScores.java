package com.gupta.vector_api.movie;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class MovieScores {
    // Eight 32-bit floats fill 256 bits.
    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_256;

    static float score(float[] userPreferences, float[] movie) {
        if (userPreferences.length != SPECIES.length()
                || movie.length != SPECIES.length()) {
            throw new IllegalArgumentException("Expected eight attributes");
        }

        // Create Vector using arrays
        var user = FloatVector.fromArray(SPECIES, userPreferences, 0);
        var attributes = FloatVector.fromArray(SPECIES, movie, 0);

        // mul() produces eight products.
        // reduceLanes(ADD) adds them.
        return user.mul(attributes).reduceLanes(VectorOperators.ADD);
    }

    public static void main(String[] args) {
        // Scores in the following order:
        // Action, Comedy, SciFi, Drama, Romance, Thriller, Adventure, Mystery
        float[] user = {0.9f, 0.3f, 0.85f, 0.45f, 0.2f, 0.8f, 0.9f, 0.55f};

        String[] titles = {"When Harry Met Sally",
                           "Alien",
                           "The Empire Strikes Back"};

        // Scores in the following order:
        // Action, Comedy, SciFi, Drama, Romance, Thriller, Adventure, Mystery
        float[][] movies = {
            {0.0f, 0.9f, 0.0f, 0.5f, 1.0f, 0.0f, 0.1f, 0.1f},
            {0.6f, 0.0f, 1.0f, 0.5f, 0.0f, 1.0f, 0.6f, 0.7f},
            {0.8f, 0.2f, 1.0f, 0.6f, 0.3f, 0.7f, 1.0f, 0.4f}
        };

        int best = 0;
        float bestScore = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < movies.length; i++) {
            float result = score(user, movies[i]);

            System.out.printf("%s: %.3f%n", titles[i], result);

            if (result > bestScore) {
                best = i;
                bestScore = result;
            }
        }

        System.out.println("Recommended: " + titles[best]);
    }
}
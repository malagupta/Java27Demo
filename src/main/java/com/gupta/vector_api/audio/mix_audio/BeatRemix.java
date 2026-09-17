package com.gupta.vector_api.audio.mix_audio;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;
import javax.sound.sampled.*;
import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

public class BeatRemix {
    static final int RATE = 44_100;
    static final AudioFormat FORMAT = new AudioFormat(RATE, 16, 1, true, false);
    static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    public static void main(String[] args) throws Exception {
        System.out.println("Choose a beat: 1 = rock, 2 = house, 3 = hip-hop");
        int choice = new Scanner(System.in).nextInt();
        if (choice < 1 || choice > 3) {
            System.out.println("Please choose 1, 2 or 3.");
            return;
        }

        float[] song = readWav("/com/gupta/vector_api/audio/mix_audio/the-entertainer.wav");
        float[] beat = createBeat(choice, song.length);
        float[] mixed = mix(song, beat);

        Path outputDir = Path.of("output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve("mixed.wav");

        writeWav(outputFile.toString(), mixed);
        System.out.println("Wrote mixed.wav using beat " + choice + ". Close and reopen it in your player.");
    }

    // Mix several samples at a time using the Vector API.
    static float[] mix(float[] song, float[] beat) {
        if (song.length != beat.length) throw new IllegalArgumentException("Lengths differ");
        float[] output = new float[song.length];
        int bound = SPECIES.loopBound(song.length);
        int i = 0;

        for (; i < bound; i += SPECIES.length()) {
            var music = FloatVector.fromArray(SPECIES, song, i);
            var drums = FloatVector.fromArray(SPECIES, beat, i);
            music.mul(0.45f).add(drums.mul(0.55f)).intoArray(output, i);
        }
        for (; i < song.length; i++) {
            output[i] = song[i] * 0.45f + beat[i] * 0.55f;
        }
        return output;
    }

    // Each string is a repeating 16-step pattern: x = hit, . = silence.
    // At 120 BPM, each step lasts 0.125 seconds. The bundled song uses this tempo.
    static float[] createBeat(int choice, int length) {
        String[] pattern = switch (choice) {
            case 1 -> new String[]{"x.....x.x.x.....", "....x.......x...", "x.x.x.x.x.x.x.x."};
            case 2 -> new String[]{"x...x...x...x...", "....x.......x...", "..x...x...x...x."};
            case 3 -> new String[]{"x......x..x.....", "........x.......", "x.x.x.x.x.x.x.xx"};
            default -> throw new IllegalArgumentException("Choose 1, 2 or 3");
        };

        float[] beat = new float[length];
        Random random = new Random(42);
        double[] lastHit = {-10, -10, -10};
        int previousStep = -1;

        for (int i = 0; i < length; i++) {
            double seconds = i / (double) RATE;
            int step = (int) (seconds * 8);
            if (step != previousStep) {
                for (int drum = 0; drum < 3; drum++) {
                    if (pattern[drum].charAt(step % 16) == 'x') lastHit[drum] = seconds;
                }
                previousStep = step;
            }
            double k = seconds - lastHit[0];
            double s = seconds - lastHit[1];
            double h = seconds - lastHit[2];
            double noise = random.nextDouble() * 2 - 1;

            // Rock: higher, short kick. House: firm pulse. Hip-hop: longer bass tail.
            double frequency = choice == 1 ? 95 : choice == 2 ? 65 : 45;
            double decay = choice == 1 ? 24 : choice == 2 ? 16 : 7;
            double kick = Math.sin(2 * Math.PI * frequency * k) * Math.exp(-decay * k);

            // Rock gets a noisy snare, house a three-burst clap, hip-hop a low snare.
            double snare = switch (choice) {
                case 1 -> noise * Math.exp(-18 * s);
                case 2 -> noise * (Math.exp(-90 * s)
                        + (s >= 0.025 ? Math.exp(-90 * (s - 0.025)) : 0)
                        + (s >= 0.050 ? Math.exp(-35 * (s - 0.050)) : 0)) / 1.5;
                default -> (0.5 * noise + 0.5 * Math.sin(2 * Math.PI * 160 * s))
                        * Math.exp(-14 * s);
            };

            // House has longer, open hats; the other choices use shorter ticks.
            double hat = noise * Math.exp(-(choice == 2 ? 12 : choice == 1 ? 65 : 110) * h);
            beat[i] = (float) (0.55 * kick + 0.30 * snare + 0.15 * hat);
            beat[i] *= Math.min(1f, (length - 1 - i) / (float) RATE);
        }
        return beat;
    }

    // The bundled file is mono, 44.1 kHz, 16-bit PCM WAV.
    static float[] readWav(String resourcePath) throws Exception {
        InputStream resource = BeatRemix.class.getResourceAsStream(resourcePath);
        if (resource == null) {
            throw new FileNotFoundException("Resource not found on classpath: " + resourcePath + " (make sure it exists under src/main/resources)");
        }

        // AudioSystem needs a stream that supports mark/reset.
        try (AudioInputStream input = AudioSystem.getAudioInputStream(new BufferedInputStream(resource))) {
            if (!input.getFormat().matches(FORMAT)) {
                throw new IllegalArgumentException("Expected mono 44.1 kHz 16-bit PCM WAV");
            }

            ShortBuffer pcm = ByteBuffer.wrap(input.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();

            float[] samples = new float[pcm.remaining()];
            for (int i = 0; i < samples.length; i++) samples[i] = pcm.get() / 32768f;
            return samples;
        }
    }

    static void writeWav(String name, float[] samples) throws Exception {
        ByteBuffer pcm = ByteBuffer.allocate(samples.length * 2).order(ByteOrder.LITTLE_ENDIAN);
        for (float sample : samples) {
            int value = Math.max(-32768, Math.min(32767, Math.round(sample * 32768f)));
            pcm.putShort((short) value);
        }
        try (AudioInputStream output = new AudioInputStream(new ByteArrayInputStream(pcm.array()), FORMAT, samples.length)) {
            AudioSystem.write(output, AudioFileFormat.Type.WAVE, new File(name));
        }
    }
}

# Java 27 examples

Source code and supporting files for Mala Gupta’s blog posts on Java 27. Each example accompanies a post explaining a Java feature through practical code. More topics and links will be added as the posts are published.

## Getting started

Use JDK 27 for examples that require Java 27 features. Check the instructions for each topic for any additional compiler options, runtime flags, or input files. Some examples also work on earlier JDKs; this does not mean that every example in the repository does.

The instructions below apply specifically to the Vector API examples.

## Vector API

These examples cover vector arithmetic, reductions, comparisons, and masks.

### Blog posts

1. [Before You Dive into Java’s Vector API](https://medium.com/@mala.gupta/before-you-dive-into-javas-vector-api-86b5655278f0): vectors, lanes, SIMD, and species.
2. [Java’s Vector API in Practice: Four Problems You Can Solve](https://medium.com/@mala.gupta/javas-vector-api-in-practice-four-problems-you-can-solve-8e1adacfc239): movie scoring, audio mixing, temperature checks, and image thresholding.

### Examples

- **MovieScores.java:** Multiply eight movie attributes by a user’s preferences, then reduce the products to one score. The values are illustrative, not a trained recommendation model.
- **BeatRemix.java:** Add a rock, house, or hip-hop-style beat to The Entertainer. Generate percussion in memory, mix corresponding samples using the Vector API, and save a playable WAV.
- **TemperatureCount.java:** Compare temperature readings with a threshold and count the matching mask lanes.
- **ImageThreshold.java:** Read a grayscale PNG, use a mask to select black or white for each pixel, and save the result for comparison.

### Run the Vector API examples

Use a JDK that includes the `jdk.incubator.vector` module. No third-party libraries, Maven, or Gradle are needed for these standalone examples. Incubator warnings are expected.

### Music and image credits

The supplied WAV is a synthesized rendition of Scott Joplin’s *The Entertainer* (1902), prepared at 120 BPM from the [Mutopia Project’s public-domain MIDI, maintained by Chris Sawer](https://www.mutopiaproject.org/cgibin/piece-info.cgi?id=263). No commercial recording or drum samples are used. See [Mutopia’s reuse information](https://www.mutopiaproject.org/legal.html).

The grayscale test chart was created for this example. 

### Vector API references

- [JEP 537: Vector API (Twelfth Incubator)](https://openjdk.org/jeps/537)
- [Vector API documentation for JDK 26](https://docs.oracle.com/en/java/javase/26/docs/api/jdk.incubator.vector/jdk/incubator/vector/package-summary.html)

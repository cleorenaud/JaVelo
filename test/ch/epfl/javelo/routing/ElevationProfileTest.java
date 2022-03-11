package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTest {

    @Test
    void lengthWorksOnKnownInput() {
        float[] sample = new float[2];
        ElevationProfile profile1 = new ElevationProfile(1, sample);
        assertEquals(1, profile1.length());
        ElevationProfile profile2 = new ElevationProfile(1000, sample);
        assertEquals(1000, profile2.length());
    }

    @Test
    void elevationProfileThrowsOnInvalidLength() {
        float[] sample = new float[2];
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(0, sample);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(-4, sample);
        });
    }

    @Test
    void elevationProfileThrowsOnInvalidElevationSamples() {
        float[] sample1 = new float[0];
        float[] sample2 = new float[1];

        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(2, sample1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(2, sample2);
        });
    }

    @Test
    void minElevationWorksWithKnownInput() {
        float[] sample1 = {0, 1, 50, 3, 4};
        assertEquals(0, (new ElevationProfile(4, sample1)).minElevation());
        float[] sample2 = {0, 1, -50, 3, 4};
        assertEquals(-50, (new ElevationProfile(4, sample2)).minElevation());
    }

    @Test
    void maxElevationWorksWithKnownInput() {
        float[] sample1 = {0, 1, 50, 3, 4};
        assertEquals(50, (new ElevationProfile(4, sample1)).maxElevation());
        float[] sample2 = {0, 1, -50, 3, 4};
        assertEquals(4, (new ElevationProfile(4, sample2)).maxElevation());
    }

    @Test
    void totalAscentWorksWithKnownInput() {
        float[] sample1 = {0, 1, 50, 3, 4};
        assertEquals(51, (new ElevationProfile(4, sample1)).totalAscent());
        float[] sample2 = {0, 1, -50, 3, 4};
        assertEquals(55, (new ElevationProfile(4, sample2)).totalAscent());
    }

    @Test
    void totalDescentWorksWithKnownInput() {
        float[] sample1 = {0, 1, 50, 3, 4};
        assertEquals(-47, (new ElevationProfile(4, sample1)).totalDescent());
        float[] sample2 = {0, 1, -50, 3, 4};
        assertEquals(-51, (new ElevationProfile(4, sample2)).totalDescent());
    }

    @Test
    void ElevationAtWorksOnKnownInput() {
        float[] sample = {0, 1, 50, 3, 4};
        assertEquals(50, (new ElevationProfile(4, sample)).elevationAt(2));
        assertEquals(3.5, (new ElevationProfile(4, sample)).elevationAt(3.5));

    }
}
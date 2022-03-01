package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebMercatorTest {
    @Test
    void TestTout() {
        assertEquals(0.659154943, WebMercator.x(1), 10e-6);
        assertEquals(0, WebMercator.x(-Math.PI));
        assertEquals(((Math.PI- Math2.asinh(1))/(2*Math.PI)), WebMercator.y(Math.PI/4));
        assertEquals(Math.PI, WebMercator.lon(1));
        assertEquals(0, WebMercator.lon(0.5));
        assertEquals(Math.atan(Math.sinh(Math.PI - 2 * Math.PI * 1)), WebMercator.lat(1));
    }

}
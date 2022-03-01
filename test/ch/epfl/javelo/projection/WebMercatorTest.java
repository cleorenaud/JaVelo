/*
 * Author : Roxanne Chevalley
 * Date : 01.03.22
 */
package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebMercatorTest {

    @Test
   void Test(double lon) {
        assertEquals(0.659154943, WebMercator.x(1), 10e-6);

    }
}

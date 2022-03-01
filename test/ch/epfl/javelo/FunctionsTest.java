package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {
    @Test
    void sampledException (){
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(new ,1);
        });
    }



}
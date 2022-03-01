package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {
    @Test
    void sampledException (){
        float [] tab = {1};
        float [] tab2 = {1,2};
        float [] tab3={};
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(tab,3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(tab3,3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(tab2,0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(tab2,-5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(tab,0);
        });
    }

    @Test
    void testConstant(){
        DoubleUnaryOperator f = Functions.constant(4);
        assertEquals(4, f.applyAsDouble(1000));
        DoubleUnaryOperator f2= Functions.constant(0);
        assertEquals(0, f2.applyAsDouble(-5));
    }

    @Test
    void testSampled(){
        float [] tab = {0,1,2,3,4,5};
        DoubleUnaryOperator f = Functions.sampled(tab, 5);
        assertEquals(5, f.applyAsDouble(7));
        DoubleUnaryOperator f2= Functions.sampled(tab, 10);
        assertEquals(2, f.applyAsDouble(2));
        assertEquals(0,f.applyAsDouble(-5));
        assertEquals(5, f2.applyAsDouble(12));
        assertEquals(2, f2.applyAsDouble(4));
        float [] tab2 = {0,2,10,30};
        float [] tab3= {0,2,10,30,80};
        DoubleUnaryOperator f3= Functions.sampled(tab2,3 );
        assertEquals(20, f3.applyAsDouble(2.5));
        DoubleUnaryOperator f4 = Functions.sampled (tab3, 2);
        assertEquals(15, f4.applyAsDouble(1.125));


    }





}
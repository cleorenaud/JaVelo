package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {
    @Test
    void throwExceptionIfNotInBound(){
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-1,0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0.5,1.2);
        });
    }

    @Test
    void testOf(){
        //assertEquals(new PointWebMercator(0.518275214444,0.353664894749), PointWebMercator.of(19, 69561722, 47468099));
        PointCh pt= new PointCh(2600000,1100000 );
        double x= WebMercator.x(pt.lon());
        double y=WebMercator.y(pt.lat());
        assertEquals(new PointWebMercator(x,y),PointWebMercator.ofPointCh(pt));
        PointWebMercator pt2 = new PointWebMercator (1,1);
        assertEquals(16384, pt2.xAtZoomLevel(6));
        assertEquals(16384, pt2.yAtZoomLevel(6));

    }
}
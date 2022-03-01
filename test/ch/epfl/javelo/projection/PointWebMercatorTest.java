package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;
import org.junit.jupiter.api.Test;

import java.awt.*;

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

   @Test
    void testToPointChException(){
        double e=2500000;
        double n=1000000;
        double lon= Ch1903.lon(e,n);
        double lat = Ch1903.lat(e,n);
        double x=WebMercator.x(lon);
        double y= WebMercator.y(lat);
        PointWebMercator pt2 =new PointWebMercator(x,y);
        assertEquals(null, pt2.toPointCh());
    }

    @Test
    void testToPointCh(){
        double e=2500000;
        double n=1100000;
        double lon= Ch1903.lon(e,n);
        double lat = Ch1903.lat(e,n);
        double x=WebMercator.x(lon);
        double y= WebMercator.y(lat);
        PointWebMercator pt2 =new PointWebMercator(x,y);
        assertEquals(e, pt2.toPointCh().e(), 2);
        assertEquals(n, pt2.toPointCh().n(), 2); 
    }
}
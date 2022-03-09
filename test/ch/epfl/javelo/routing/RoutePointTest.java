package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutePointTest {
    @Test
    void routePointMinTest(){
        RoutePoint point1= new RoutePoint(new PointCh(2500000,1100000), 5, 7);
        RoutePoint point2 = new RoutePoint(new PointCh(2500000,1100000), 5, 6);
        assertEquals(point2, point1.min(point2));
        RoutePoint point4 = new RoutePoint(new PointCh(2500000,1100000), 5, 6);
        RoutePoint point3 = new RoutePoint(new PointCh(2500000,1100000), 5, 5);
        assertEquals(point4, point4.min(point2));
        assertEquals(point3,point3.min(point2));
        RoutePoint point5 = new RoutePoint(new PointCh(2500000,1100000), 5, -6);
        RoutePoint point6 = new RoutePoint(new PointCh(2500000,1100000), 5, 5);
        assertEquals(point6,point5.min(point6));
    }

    @Test
    void routePointMinTest2(){
        PointCh pointCh= new PointCh(2500000,1100000);
        RoutePoint point1= new RoutePoint(pointCh, 5, 7);
        RoutePoint point2=point1.min(pointCh,3,2);
        RoutePoint point3= new RoutePoint(pointCh, 3, 2);
        boolean b= point2.equals(point3);
        assertEquals(true,b );
    }

}
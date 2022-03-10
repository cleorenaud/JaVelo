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
        RoutePoint point7= RoutePoint.NONE;
        assertEquals(point6,point7.min(point6));
    }

    @Test
    void routePointMinTest2(){
        PointCh pointCh= new PointCh(2500000,1100000);
        RoutePoint point1= new RoutePoint(pointCh, 5, 7);
        RoutePoint point2=point1.min(pointCh,3,2);
        RoutePoint point3= new RoutePoint(pointCh, 3, 2);
        RoutePoint point5= RoutePoint.NONE;
        RoutePoint point4=point5.min(pointCh,3,2);
        boolean c= point4.equals(point3);
        boolean b= point2.equals(point3);
        assertEquals(true,b);
        assertEquals(true,c);
    }

    @Test
    void withPositionShiftedByTest(){
        PointCh pointCh= new PointCh(2500000,1100000);
        RoutePoint point1= new RoutePoint(pointCh, 5, 7);
        RoutePoint point2= new RoutePoint(pointCh, 8, 7);
        RoutePoint point3= point1.withPositionShiftedBy(3);
        RoutePoint point4= new RoutePoint(pointCh, -12, 7);
        RoutePoint point5= point2.withPositionShiftedBy(-20);
        RoutePoint point6= point4.withPositionShiftedBy(0);
        boolean a= point2.equals(point3);
        boolean b= point4.equals(point5);
        boolean c= point6.equals(point4);
        assertTrue(a);
        assertTrue(b);
        assertTrue(c);
    }

}
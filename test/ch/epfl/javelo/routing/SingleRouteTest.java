package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTest {

    @Test
    void SingleRouteTestGeneral() throws IOException {
        List<Edge> edges= new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            new SingleRoute(edges);
        });

    }

    @Test
    void lengthTest(){
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        float [] tab= {0,1,2,3,};
        float [] tab2= {0,1,2};
        DoubleUnaryOperator function= Functions.sampled(tab, 3);
        DoubleUnaryOperator function2= Functions.sampled(tab2, 3);
        Edge edge1=new Edge(1,4,point,point2, 3, function);
        Edge edge2=new Edge(4,6,point,point2, 2, function2);
        List<Edge> edges= new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        assertEquals(5,route.length() );
    }

    @Test
    void edgesTest(){
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        float [] tab= {0,1,2,3,};
        float [] tab2= {0,1,2};
        DoubleUnaryOperator function= Functions.sampled(tab, 3);
        DoubleUnaryOperator function2= Functions.sampled(tab2, 3);
        Edge edge1=new Edge(1,4,point,point2, 3, function);
        Edge edge2=new Edge(4,6,point,point2, 2, function2);
        List<Edge> edges= new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        boolean b=edges.equals(route.edges());
        assertTrue(b);
    }

    @Test
    void pointsTest(){
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        float [] tab= {0,1,2,3,};
        float [] tab2= {0,1,2};
        DoubleUnaryOperator function= Functions.sampled(tab, 3);
        DoubleUnaryOperator function2= Functions.sampled(tab2, 3);
        Edge edge1=new Edge(1,4,point,point2, 3, function);
        Edge edge2=new Edge(4,6,point,point2, 2, function2);
        List<Edge> edges= new ArrayList<>();
        List<PointCh> points = new ArrayList<>();
        points.add(point);
        points.add(point2);
        points.add(point);
        points.add(point2);
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        boolean b=points.equals(route.points());
        assertTrue(b);
    }

    @Test
    void PointAtTest(){
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        PointCh point3= new PointCh(2500005,1100000);
        PointCh point4= new PointCh(2500004,1100000);
        float [] tab= {0,1,2,3};
        float [] tab2= {3,4,5};
        DoubleUnaryOperator function= Functions.sampled(tab, 3);
        DoubleUnaryOperator function2= Functions.sampled(tab2, 3);
        Edge edge1=new Edge(1,4,point,point2, 3, function);
        Edge edge2=new Edge(4,6,point2,point3, 2, function2);
        List<Edge> edges= new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        boolean b= point.equals(route.pointAt(0));
        boolean c=point.equals(route.pointAt(-2));
        boolean d=point2.equals(route.pointAt(3));
        boolean e=point3.equals(route.pointAt(5));
        boolean f=point3.equals(route.pointAt(8));
        boolean g=point4.equals(route.pointAt(4));
        assertTrue(b);
        assertTrue(c);
        assertTrue(d);
        assertTrue(e);
        assertTrue(f);
        assertTrue(g);
    }

    @Test
    void elevationAtTest(){
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        PointCh point3= new PointCh(2500005,1100000);
        float [] tab= {0,1,2,3};
        float [] tab2= {3,4,5};
        DoubleUnaryOperator function= Functions.sampled(tab, 3);
        DoubleUnaryOperator function2= Functions.sampled(tab2, 2);
        Edge edge1=new Edge(1,4,point,point2, 3, function);
        Edge edge2=new Edge(4,6,point2,point3, 2, function2);
        List<Edge> edges= new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        assertEquals(0,route.elevationAt(0));
        assertEquals(0,route.elevationAt(-2));
        assertEquals(3,route.elevationAt(3));
        assertEquals(5,route.elevationAt(5));
        assertEquals(5,route.elevationAt(8));
        assertEquals(4,route.elevationAt(4));
    }

    @Test
    void nodeClosestToTest(){
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        PointCh point3= new PointCh(2500005,1100000);
        float [] tab= {0,1,2,3};
        float [] tab2= {3,4,5};
        DoubleUnaryOperator function= Functions.sampled(tab, 3);
        DoubleUnaryOperator function2= Functions.sampled(tab2, 2);
        Edge edge1=new Edge(1,4,point,point2, 3, function);
        Edge edge2=new Edge(4,6,point2,point3, 2, function2);
        List<Edge> edges= new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        assertEquals(1,route.nodeClosestTo(0));
        assertEquals(1,route.nodeClosestTo(-2));
        assertEquals(4,route.nodeClosestTo(3));
        assertEquals(6,route.nodeClosestTo(5));
        assertEquals(6,route.nodeClosestTo(8));
        assertEquals(4,route.nodeClosestTo(4));
        assertEquals(1, route.nodeClosestTo(1.5));
        assertEquals(4,route.nodeClosestTo(2.5));
        assertEquals(6,route.nodeClosestTo(4.5));
    }

    @Test
    void pointClosestTo(){
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        PointCh point3= new PointCh(2500005,1100000);
        PointCh point5= new PointCh(2500003, 1100001);
        PointCh point6= new PointCh(2499998,1100000);
        PointCh point7 = new PointCh(2500008,1100004);
        float [] tab= {0,1,2,3};
        float [] tab2= {3,4,5};
        DoubleUnaryOperator function= Functions.sampled(tab, 3);
        DoubleUnaryOperator function2= Functions.sampled(tab2, 2);
        Edge edge1=new Edge(1,4,point,point2, 3, function);
        Edge edge2=new Edge(4,6,point2,point3, 2, function2);
        List<Edge> edges= new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);

        RoutePoint routePoint1 = new RoutePoint(point,0,0);
        boolean b1 =routePoint1.point().equals(route.pointClosestTo(point).point());
        boolean b2= routePoint1.position()==route.pointClosestTo(point).position();
        boolean b3= routePoint1.distanceToReference()==route.pointClosestTo(point).distanceToReference();
        assertTrue(b1);
        assertTrue(b2);
        assertTrue(b3);

        RoutePoint routePoint2 = new RoutePoint(point2,3,1);
        boolean c1 =routePoint2.point().equals(route.pointClosestTo(point5).point());
        boolean c2= routePoint2.position()==route.pointClosestTo(point5).position();
        boolean c3= routePoint2.distanceToReference()==route.pointClosestTo(point5).distanceToReference();
        assertTrue(c1);
        assertTrue(c2);
        assertTrue(c3);

        RoutePoint routePoint3 = new RoutePoint(point3,5,0);
        boolean d1 =routePoint3.point().equals(route.pointClosestTo(point3).point());
        boolean d2= routePoint3.position()==route.pointClosestTo(point3).position();
        boolean d3= routePoint3.distanceToReference()==route.pointClosestTo(point3).distanceToReference();
        assertTrue(d1);
        assertTrue(d2);
        assertTrue(d3);

        RoutePoint routePoint4 = new RoutePoint(point,0,2);
        boolean e1 =routePoint4.point().equals(route.pointClosestTo(point6).point());
        boolean e2= routePoint4.position()==route.pointClosestTo(point6).position();
        boolean e3= routePoint4.distanceToReference()==route.pointClosestTo(point6).distanceToReference();
        assertTrue(e1);
        assertTrue(e2);
        assertTrue(e3);

        RoutePoint routePoint5 = new RoutePoint(point3,5,5);
        boolean f1 =routePoint5.point().equals(route.pointClosestTo(point7).point());
        boolean f2= routePoint5.position()==route.pointClosestTo(point7).position();
        boolean f3= routePoint5.distanceToReference()==route.pointClosestTo(point7).distanceToReference();
        assertTrue(f1);
        assertTrue(f2);
        assertTrue(f3);
    }

}
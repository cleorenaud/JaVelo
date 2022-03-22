package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTest {


    //DONE
    @Test
    void multiRouteTestThrowsException(){
        List<Route> routes= new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            new MultiRoute(routes);
        });

    }

    //TO DO
    @Test
    void indexOfSegmentAtTest(){
        MultiRoute multiRoute= this.getMulti();
        assertEquals(0,multiRoute.indexOfSegmentAt(0));
        assertEquals(0,multiRoute.indexOfSegmentAt(1));
        assertEquals(0,multiRoute.indexOfSegmentAt(2));
        assertEquals(0,multiRoute.indexOfSegmentAt(3));
        assertEquals(0,multiRoute.indexOfSegmentAt(4));
        assertEquals(1,multiRoute.indexOfSegmentAt(5));
        assertEquals(1,multiRoute.indexOfSegmentAt(6));
        assertEquals(1,multiRoute.indexOfSegmentAt(7));
        assertEquals(1,multiRoute.indexOfSegmentAt(8));
        assertEquals(1,multiRoute.indexOfSegmentAt(9));
        assertEquals(2,multiRoute.indexOfSegmentAt(10));
        assertEquals(2,multiRoute.indexOfSegmentAt(11));
        assertEquals(2,multiRoute.indexOfSegmentAt(12));
        assertEquals(2,multiRoute.indexOfSegmentAt(13));
        assertEquals(2,multiRoute.indexOfSegmentAt(14));
        assertEquals(3,multiRoute.indexOfSegmentAt(15));
        assertEquals(3,multiRoute.indexOfSegmentAt(16));
        assertEquals(3,multiRoute.indexOfSegmentAt(17));
        assertEquals(3,multiRoute.indexOfSegmentAt(18));
        assertEquals(3,multiRoute.indexOfSegmentAt(19));
        assertEquals(3,multiRoute.indexOfSegmentAt(20));

    }

    //DONE
    @Test
    void lengthTest(){
        MultiRoute multiRoute= this.getMulti();
        assertEquals(20,multiRoute.length());
    }

    //DONE
    @Test
    void edgesTest(){
        MultiRoute multiRoute= this.getMulti();
        List<Edge> edges = this.getEdges();
        List<Edge> edges2 = multiRoute.edges();
        assertEquals(edges2.size(),edges.size());
        for (int i = 0; i < edges2.size(); i++) {

        }
        for (int i = 0; i <edges.size() ; i++) {
            boolean b=edges.get(i).fromNodeId()==edges2.get(i).fromNodeId();
            boolean c=edges.get(i).toNodeId()==edges2.get(i).toNodeId();
            boolean d=edges.get(i).fromPoint().equals(edges2.get(i).fromPoint());
            boolean e=edges.get(i).toPoint().equals(edges2.get(i).toPoint());
            boolean f=edges.get(i).length()==edges2.get(i).length();
            boolean g=edges.get(i).profile().applyAsDouble(i)==edges2.get(i).profile().applyAsDouble(i);
            assertTrue(b);
            assertTrue(c);
            assertTrue(d);
            assertTrue(e);
            assertTrue(f);
            assertTrue(g);
        }

    }

    //DONE
    @Test
    void pointsTest(){
        MultiRoute multiRoute= this.getMulti();
        List<PointCh> points1= this.getPoints();
        List<PointCh> points2= multiRoute.points();
        assertEquals(points1.size(),points2.size());
        for (int i = 0; i <points1.size() ; i++) {
            boolean b= points1.get(i).equals(points2.get(i));
            assertTrue(b);
        }

    }

    //TO DO
    @Test
    void PointAtTest(){
        MultiRoute multiRoute= this.getMulti();
        for (int i = 0; i < 21; i++) {
            int e= 2500000 + i;
            int n = 1100000;
            PointCh point1= new PointCh(e, n);
            PointCh point2 = multiRoute.pointAt(i);
            boolean b = point1.equals(point2);
        }

    }

    //TO DO
    @Test
    void elevationAtTest(){
        MultiRoute multiRoute= this.getMulti();
        for (int i = 0; i < 11; i++) {
            assertEquals(i, multiRoute.elevationAt(i));
        }
        for (int i = 11; i <21 ; i++) {
            assertEquals(20-i,multiRoute.elevationAt(i));
        }

    }

    //TO DO
    @Test
    void nodeClosestToTest(){
        MultiRoute multiRoute= this.getMulti();
        for (double i = -2; i <=1.5 ; i=i+0.5) {
            assertEquals(1,multiRoute.nodeClosestTo(i));
        }
        for (double i = 2; i <=4 ; i=i+0.5) {
            assertEquals(4,multiRoute.nodeClosestTo(i));
        }
        for (double i = 4.5; i <=6.5 ; i=i+0.5) {
            assertEquals(6,multiRoute.nodeClosestTo(i));
        }
        for (double i = 7; i <=9 ; i=i+0.5) {
            assertEquals(9,multiRoute.nodeClosestTo(i));
        }
        for (double i = 9.5; i <=11.5 ; i=i+0.5) {
            assertEquals(11,multiRoute.nodeClosestTo(i));
        }
        for (double i = 12; i <=14 ; i=i+0.5) {
            assertEquals(14,multiRoute.nodeClosestTo(i));
        }
        for (double i = 14.5; i <=16.5 ; i=i+0.5) {
            assertEquals(16,multiRoute.nodeClosestTo(i));
        }
        for (double i = 17; i <=19 ; i=i+0.5) {
            assertEquals(19,multiRoute.nodeClosestTo(i));
        }
        for (double i = 19.5; i <=24 ; i=i+0.5) {
            assertEquals(21,multiRoute.nodeClosestTo(i));
        }


    }

    //TO DO
    @Test
    void pointClosestTo(){
        MultiRoute multiRoute= this.getMulti();
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        PointCh point3= new PointCh(2500012,1100000);
        PointCh point5= new PointCh(2500003, 1100001);
        PointCh point6= new PointCh(2499998,1100000);
        PointCh point7 = new PointCh(2500023,1100004);
        PointCh point8= new PointCh(2500020,1100000);
        PointCh point9=new PointCh(2500026,1100000);

        //point égaux
        RoutePoint routePoint1 = new RoutePoint(point,0,0);
        boolean b1 =routePoint1.point().equals(multiRoute.pointClosestTo(point).point());
        boolean b2= routePoint1.position()==multiRoute.pointClosestTo(point).position();
        boolean b3= routePoint1.distanceToReference()==multiRoute.pointClosestTo(point).distanceToReference();
        assertTrue(b1);
        assertTrue(b2);
        assertTrue(b3);

        RoutePoint routePoint2 = new RoutePoint(point3,12,0);
        boolean c1 =routePoint2.point().equals(multiRoute.pointClosestTo(point3).point());
        boolean c2= routePoint2.position()==multiRoute.pointClosestTo(point3).position();
        boolean c3= routePoint2.distanceToReference()==multiRoute.pointClosestTo(point3).distanceToReference();
        assertTrue(c1);
        assertTrue(c2);
        assertTrue(c3);

        RoutePoint routePoint3 = new RoutePoint(point8,20,0);
        boolean d1 =routePoint3.point().equals(multiRoute.pointClosestTo(point8).point());
        boolean d2= routePoint3.position()==multiRoute.pointClosestTo(point8).position();
        boolean d3= routePoint3.distanceToReference()==multiRoute.pointClosestTo(point8).distanceToReference();
        assertTrue(d1);
        assertTrue(d2);
        assertTrue(d3);

        //points plus loins qu'extrémité
        RoutePoint routePoint4 = new RoutePoint(point8,20,6);
        boolean e1 =routePoint4.point().equals(multiRoute.pointClosestTo(point9).point());
        boolean e2= routePoint4.position()==multiRoute.pointClosestTo(point9).position();
        boolean e3= routePoint4.distanceToReference()==multiRoute.pointClosestTo(point9).distanceToReference();
        assertTrue(e1);
        assertTrue(e2);
        assertTrue(e3);

        RoutePoint routePoint5 = new RoutePoint(point,0,2);
        boolean f1 =routePoint5.point().equals(multiRoute.pointClosestTo(point6).point());
        boolean f2= routePoint5.position()==multiRoute.pointClosestTo(point6).position();
        boolean f3= routePoint5.distanceToReference()==multiRoute.pointClosestTo(point6).distanceToReference();
        assertTrue(f1);
        assertTrue(f2);
        assertTrue(f3);

        //points avec distance orthogonole
        RoutePoint routePoint6 = new RoutePoint(point2,3,1);
        boolean g1 =routePoint6.point().equals(multiRoute.pointClosestTo(point5).point());
        boolean g2= routePoint6.position()==multiRoute.pointClosestTo(point5).position();
        boolean g3= routePoint6.distanceToReference()==multiRoute.pointClosestTo(point5).distanceToReference();
        assertTrue(g1);
        assertTrue(g2);
        assertTrue(g3);

        RoutePoint routePoint7 = new RoutePoint(point8,20,5);
        boolean h1 =routePoint7.point().equals(multiRoute.pointClosestTo(point7).point());
        boolean h2= routePoint7.position()==multiRoute.pointClosestTo(point7).position();
        boolean h3= routePoint7.distanceToReference()==multiRoute.pointClosestTo(point7).distanceToReference();
        assertTrue(h1);
        assertTrue(h2);
        assertTrue(h3);



    }

    public MultiRoute getMulti(){
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        PointCh point3= new PointCh(2500005,1100000);
        PointCh point4= new PointCh(2500008,1100000);
        PointCh point5= new PointCh(2500010,1100000);
        float [] tab= {0,1,2,3};
        float [] tab2= {3,4,5};
        DoubleUnaryOperator function= Functions.sampled(tab, 3);
        DoubleUnaryOperator function2= Functions.sampled(tab2, 2);
        float [] tab3= {5,6,7,8};
        float [] tab4= {8,9,10};
        DoubleUnaryOperator function3= Functions.sampled(tab3, 3);
        DoubleUnaryOperator function4= Functions.sampled(tab4, 2);
        Edge edge1=new Edge(1,4,point,point2, 3, function);
        Edge edge2=new Edge(4,6,point2,point3, 2, function2);
        Edge edge3=new Edge(6,9,point3,point4, 3, function3);
        Edge edge4=new Edge(9,11,point4,point5, 2, function4);

        List<Edge> edges= new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        List<Edge> edges2= new ArrayList<>();
        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2= new SingleRoute(edges2);
        List<Route> listRoute1= new ArrayList<>();
        listRoute1.add(route);
        listRoute1.add(route2);
        MultiRoute multiRoute1= new MultiRoute(listRoute1);


        PointCh point6= new PointCh(2500013,1100000);
        PointCh point7 =new PointCh(2500015,1100000);
        PointCh point8= new PointCh(2500018,1100000);
        PointCh point9= new PointCh(2500020,1100000);
        float [] tab5= {10,9,8,7};
        float [] tab6= {7,6,5};
        DoubleUnaryOperator function5= Functions.sampled(tab5, 3);
        DoubleUnaryOperator function6= Functions.sampled(tab6, 2);
        float [] tab7= {5,4,3,2};
        float [] tab8= {2,1,0};
        DoubleUnaryOperator function7= Functions.sampled(tab7, 3);
        DoubleUnaryOperator function8= Functions.sampled(tab8, 2);
        Edge edge5=new Edge(11,14,point5,point6, 3, function5);
        Edge edge6=new Edge(14,16,point6,point7, 2, function6);
        Edge edge7=new Edge(16,19,point7,point8, 3, function7);
        Edge edge8=new Edge(19,21,point8,point9, 2, function8);

        List<Edge> edges3= new ArrayList<>();
        edges3.add(edge5);
        edges3.add(edge6);
        SingleRoute route3 = new SingleRoute(edges3);
        List<Edge> edges4= new ArrayList<>();
        edges4.add(edge7);
        edges4.add(edge8);
        SingleRoute route4= new SingleRoute(edges4);
        List<Route> listRoute2= new ArrayList<>();
        listRoute2.add(route3);
        listRoute2.add(route4);
        MultiRoute multiRoute2= new MultiRoute(listRoute2);

        List<Route> listRoute3= new ArrayList<>();
        listRoute3.add(multiRoute1);
        listRoute3.add(multiRoute2);
        MultiRoute multiRouteFin= new MultiRoute(listRoute3);
        return multiRouteFin;
    }

    public List<Edge> getEdges(){
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        PointCh point3= new PointCh(2500005,1100000);
        PointCh point4= new PointCh(2500008,1100000);
        PointCh point5= new PointCh(2500010,1100000);
        float [] tab= {0,1,2,3};
        float [] tab2= {3,4,5};
        DoubleUnaryOperator function= Functions.sampled(tab, 3);
        DoubleUnaryOperator function2= Functions.sampled(tab2, 2);
        float [] tab3= {5,6,7,8};
        float [] tab4= {8,9,10};
        DoubleUnaryOperator function3= Functions.sampled(tab3, 3);
        DoubleUnaryOperator function4= Functions.sampled(tab4, 2);
        Edge edge1=new Edge(1,4,point,point2, 3, function);
        Edge edge2=new Edge(4,6,point2,point3, 2, function2);
        Edge edge3=new Edge(6,9,point3,point4, 3, function3);
        Edge edge4=new Edge(9,11,point4,point5, 2, function4);

        List<Edge> edges= new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);



        PointCh point6= new PointCh(2500013,1100000);
        PointCh point7 =new PointCh(2500015,1100000);
        PointCh point8= new PointCh(2500018,1100000);
        PointCh point9= new PointCh(2500020,1100000);
        float [] tab5= {10,9,8,7};
        float [] tab6= {7,6,5};
        DoubleUnaryOperator function5= Functions.sampled(tab5, 3);
        DoubleUnaryOperator function6= Functions.sampled(tab6, 2);
        float [] tab7= {5,4,3,2};
        float [] tab8= {2,1,0};
        DoubleUnaryOperator function7= Functions.sampled(tab7, 3);
        DoubleUnaryOperator function8= Functions.sampled(tab8, 2);
        Edge edge5=new Edge(11,14,point5,point6, 3, function5);
        Edge edge6=new Edge(14,16,point6,point7, 2, function6);
        Edge edge7=new Edge(16,19,point7,point8, 3, function7);
        Edge edge8=new Edge(19,21,point8,point9, 2, function8);

        edges.add(edge5);
        edges.add(edge6);
        edges.add(edge7);
        edges.add(edge8);
        return edges;
    }

    public List<PointCh> getPoints(){
        PointCh point= new PointCh(2500000,1100000);
        PointCh point2 =new PointCh(2500003,1100000);
        PointCh point3= new PointCh(2500005,1100000);
        PointCh point4= new PointCh(2500008,1100000);
        PointCh point5= new PointCh(2500010,1100000);
        PointCh point6= new PointCh(2500013,1100000);
        PointCh point7 =new PointCh(2500015,1100000);
        PointCh point8= new PointCh(2500018,1100000);
        PointCh point9= new PointCh(2500020,1100000);
        List<PointCh> points = new ArrayList<>();
        points.add(point);
        points.add(point2);
        points.add(point3);
        points.add(point4);
        points.add(point5);
        points.add(point6);
        points.add(point7);
        points.add(point8);
        points.add(point9);
        return points;
    }

}
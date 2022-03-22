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
            System.out.println("ok");
        }

    }

    //TO DO
    @Test
    void elevationAtTest(){

    }

    //TO DO
    @Test
    void nodeClosestToTest(){

    }

    //TO DO
    @Test
    void pointClosestTo(){

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
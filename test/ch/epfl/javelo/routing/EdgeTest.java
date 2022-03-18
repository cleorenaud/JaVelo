package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void constructorTest() throws IOException {
        Graph graph= Graph.loadFrom(Path.of("Lausanne"));
        System.out.println(graph.nodeCount());
        graph.nodePoint(200000);
        float[] tab= {0,1,2,3};
        Edge edge = Edge.of(graph,5,2, 3);
        Edge edge2= new Edge(2,3,graph.nodePoint(2), graph.nodePoint(3),graph.edgeLength(5),graph.edgeProfile(5));
        assertEquals(edge.fromNodeId(), edge2.fromNodeId());
        assertEquals(edge.toNodeId(),edge2.toNodeId());
        assertEquals(edge.fromPoint(),edge2.fromPoint());
        assertEquals(edge.toPoint(), edge2.toPoint());
        assertEquals(edge.length(),edge2.length());
        boolean b=(edge.profile().applyAsDouble(5))==(edge2.profile().applyAsDouble(5));
        assertTrue(b);
    }

    @Test
    void projectionLength() throws IOException {
        Graph graph= Graph.loadFrom(Path.of("Lausanne"));
        Edge edge = Edge.of(graph,5,2, 3);
        PointCh point= (graph.nodePoint(2));
        PointCh point2=(graph.nodePoint(3));
        PointCh point3= (graph.nodePoint(500));
        assertEquals(0,edge.positionClosestTo(point));
        assertEquals(edge.positionClosestTo(point2), Math2.projectionLength(point.e(),point.n(),point2.e(),point2.n(),point2.e(),point2.n()));
        assertEquals(edge.positionClosestTo(point3), Math2.projectionLength(point.e(),point.n(),point2.e(),point2.n(),point3.e(),point3.n()));

        PointCh point4 = new PointCh(2500000,1100000);
        PointCh point5= new PointCh(2500003,1100000);
        float[] samples= {0,1,2,3,4};
        DoubleUnaryOperator function= Functions.sampled(samples, 4);
        Edge edge1 = new Edge(1,2,point4,point5, 7, function);
        PointCh point6= new PointCh(2500003,1100003);
        PointCh point7= new PointCh(2499995,1100000);
        PointCh point8= new PointCh(2500008,1100000);
        assertEquals(3,edge1.positionClosestTo(point6));
        assertEquals(-5,edge1.positionClosestTo(point7));
        assertEquals(8,edge1.positionClosestTo(point8));

    }

    @Test
    void elevationAtTest() throws IOException {
        Graph graph= Graph.loadFrom(Path.of("Lausanne"));
        PointCh point= (graph.nodePoint(2));
        PointCh point2=(graph.nodePoint(3));
        float[] samples= {0,1,2,3,4};
        DoubleUnaryOperator function= Functions.sampled(samples, 4);
        Edge edge = new Edge(1, 2, point,point2, 3,function);
        assertEquals(3,edge.elevationAt(3));
    }

    @Test
    void pointAtTest(){
        PointCh point4 = new PointCh(2500000,1100000);
        PointCh point5= new PointCh(2500003,1100000);
        float[] samples= {0,1,2,3,4};
        DoubleUnaryOperator function= Functions.sampled(samples, 4);
        Edge edge1 = new Edge(1,2,point4,point5, 3, function);
        assertEquals(new PointCh(2500002,1100000) ,edge1.pointAt(2));
        assertEquals(new PointCh(2499999,1100000) ,edge1.pointAt(-1));
        assertEquals(new PointCh(2500007,1100000) ,edge1.pointAt(7));
        assertEquals(2,edge1.elevationAt(2));
        assertEquals(0,edge1.elevationAt(-1));
        assertEquals(4,edge1.elevationAt(7));



    }

}
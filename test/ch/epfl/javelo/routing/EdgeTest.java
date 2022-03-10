package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void constructorTest() throws IOException {
        Graph graph= Graph.loadFrom(Path.of("Lausanne"));
        float[] tab= {0,1,2,3};
        Edge edge = Edge.of(graph,5,46,59);
        Edge edge2= new Edge(2,3,graph.nodePoint(2), graph.nodePoint(3),graph.edgeLength(1),graph.edgeProfile(1));
        assertEquals(edge, edge2);
    }

}
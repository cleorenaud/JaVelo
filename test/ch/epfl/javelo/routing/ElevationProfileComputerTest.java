package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.AttributeSet;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphEdges;
import ch.epfl.javelo.data.GraphSectors;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;
import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {


    @Test
    void elevationProfileComputerThrowsIllegalArgumentException() throws IOException {
        List<Edge> edges = new ArrayList<>();
        Graph graph= Graph.loadFrom(Path.of("Lausanne"));
        Edge edge = Edge.of(graph,5,2, 3);
        edges.add(edge);
        SingleRoute route = new SingleRoute(edges);

        // On vérifie qu'une exception est levée si maxStepLength vaut zéro
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile profile = elevationProfile(route, 0);
        });
        // On vérifie qu'une exception est levée si maxStepLength est strictement négative
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile profile = elevationProfile(route, -10);
        });
    }

    @Test
    void elevationProfileComputerWorksWithFullProfile() throws IOException {
        PointCh point = new PointCh(2500000, 1100000);
        PointCh point2 = new PointCh(2500003, 1100000);
        PointCh point3 = new PointCh(2500003, 1100003);
        float[] tab = {0, 1, 2, 3};
        float[] tab2 = {3, 5, 4};
        float[] tab3 = {4, 6};
        DoubleUnaryOperator function = Functions.sampled(tab, 3);
        DoubleUnaryOperator function2 = Functions.sampled(tab2, 2);
        DoubleUnaryOperator function3 = Functions.sampled(tab3, 1);
        Edge edge1 = new Edge(1, 4, point, point2, 3, function);
        Edge edge2 = new Edge(4, 6, point, point2, 2, function2);
        Edge edge3 = new Edge(6, 7, point2, point3, 1, function3);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        SingleRoute route = new SingleRoute(edges);
        double maxStepLength = 1; // L'espacement maximal entre les échantillons du profil
        float[] elevationSample = {0, 1, 2, 3, 5, 4, 6};
        ElevationProfile example = new ElevationProfile(6, elevationSample);
        ElevationProfile example2 = ElevationProfileComputer.elevationProfile(route, maxStepLength);

        assertEquals(example.length(), example2.length());
        assertEquals(example.minElevation(), example2.minElevation());
        assertEquals(example.maxElevation(), example2.maxElevation());
        assertEquals(example.totalAscent(), example2.totalAscent());
        assertEquals(example.totalDescent(), example2.totalDescent());
        assertEquals(example.elevationAt(0), example2.elevationAt(0));
    }

    @Test
    void elevationProfileComputerWorksWithUncompleteProfile() throws IOException {
        PointCh point = new PointCh(2500000, 1100000);
        PointCh point2 = new PointCh(2500003, 1100000);
        PointCh point3 = new PointCh(2500003, 1100003);
        float[] tab = {0, 1, 2, 3};
        float[] tab2 = {Float.NaN, Float.NaN, Float.NaN};
        float[] tab3 = {5, 6};
        DoubleUnaryOperator function = Functions.sampled(tab, 3);
        DoubleUnaryOperator function2 = Functions.sampled(tab2, 2);
        DoubleUnaryOperator function3 = Functions.sampled(tab3, 1);
        Edge edge1 = new Edge(1, 4, point, point2, 3, function);
        Edge edge2 = new Edge(4, 6, point, point2, 2, function2);
        Edge edge3 = new Edge(6, 7, point2, point3, 1, function3);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        SingleRoute route = new SingleRoute(edges);
        double maxStepLength = 1; // L'espacement maximal entre les échantillons du profil
        float[] elevationSample = {0, 1, 2, 3, 4, 5, 6};
        ElevationProfile example = new ElevationProfile(6, elevationSample);
        ElevationProfile example2 = ElevationProfileComputer.elevationProfile(route, maxStepLength);

        assertEquals(example.length(), example2.length());
        assertEquals(example.minElevation(), example2.minElevation());
        assertEquals(example.maxElevation(), example2.maxElevation());
        assertEquals(example.totalAscent(), example2.totalAscent());
        assertEquals(example.totalDescent(), example2.totalDescent());
        assertEquals(example.elevationAt(4), example2.elevationAt(4));
    }

}
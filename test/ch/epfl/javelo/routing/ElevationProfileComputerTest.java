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
    void elevationProfileComputerThrowsIllegalArgumentException() {
        List<Edge> edges = new ArrayList<>();
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
    void elevationProfileComputerWorksWithKnownInput() throws IOException {
        PointCh point = new PointCh(2500000, 1100000);
        PointCh point2 = new PointCh(2500003, 1100000);
        float[] tab = {0, 1, 2, 3};
        float[] tab2 = {3, 5, 4};
        DoubleUnaryOperator function = Functions.sampled(tab, 3);
        DoubleUnaryOperator function2 = Functions.sampled(tab2, 2);
        Edge edge1 = new Edge(1, 4, point, point2, 3, function);
        Edge edge2 = new Edge(4, 6, point, point2, 2, function2);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        double maxStepLength = 1; // L'espacement maximal entre les échantillons du profil
        float[] elevationSample = {0, 1, 2, 3, 5, 4};
        ElevationProfile example = new ElevationProfile(5, elevationSample);
        ElevationProfile example2 = ElevationProfileComputer.elevationProfile(route, maxStepLength);

        //assertEquals(example.length(), example2.length());
        assertEquals(example.minElevation(), example2.minElevation());
        assertEquals(example.maxElevation(), example2.maxElevation());
        assertEquals(example.totalAscent(), example2.totalAscent());
        assertEquals(example.totalDescent(), example2.totalDescent());
        assertEquals(example.elevationAt(0), example2.elevationAt(0));

    }

}
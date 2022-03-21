package ch.epfl.javelo.routing;

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

        /*
        List<Edge> edges = new ArrayList<>(); // Implémenter la liste pour pouvoir tester proprement
        edges.add(new Edge(0,1));
        edges.add(new Edge(1,2));
        edges.add(new Edge())
        SingleRoute route = new SingleRoute(edges);
        double maxStepLength = 1; // L'espacement maximal entre les échantillons du profil
        assertEquals(0, ElevationProfileComputer.elevationProfile(route, maxStepLength));
        
         */
    }

}
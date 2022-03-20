package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.LongBuffer;
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
        Path filePath = Path.of("lausanne");
        Graph graph = Graph.loadFrom(filePath);

        /*
        Path path = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(path)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        System.out.println(osmIdBuffer.get(0));
        System.out.println(osmIdBuffer.get(1));
        System.out.println(osmIdBuffer.get(2));
        */

        Edge edge1 = Edge.of(graph,0,0,1);
        Edge edge2 = Edge.of(graph,0,1,2);
        List<Edge> edges = new ArrayList<>(); // Implémenter la liste pour pouvoir tester proprement
        edges.add(edge1);
        edges.add(edge2);
        SingleRoute route = new SingleRoute(edges);
        double maxStepLength = 1; // L'espacement maximal entre les échantillons du profil
        assertEquals(0, ElevationProfileComputer.elevationProfile(route, maxStepLength));
    }

}
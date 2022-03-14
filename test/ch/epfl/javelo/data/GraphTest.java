package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {



    @Test
    void loadFromDoesNotThrowsIOException() throws IOException{
        Graph graph= Graph.loadFrom(Path.of("Lausanne"));

    }

    @Test
    void loadFromThrowsIOException() {
        assertThrows(IOException.class, () -> {
            Graph graph= Graph.loadFrom(Path.of("src"));
        });
    }

    @Test
    void nodeCountWorksOnKnownValues() throws IOException {
        Graph graph= Graph.loadFrom(Path.of("Lausanne"));
        //assertEquals(,graph.nodeCount());
    }

    @Test
    void nodePointWorksOnKnownInput() throws IOException {
        Graph graph= Graph.loadFrom(Path.of("Lausanne"));
        assertEquals(new PointCh(), graph.nodePoint(2022));

        46.6326106, 6.6013034
    }

    @Test
    void nodeOutDegreeWorksOnKnownInput() {

    }

    @Test
    void nodeOutEdgeIdWorksOnKnownInput() {

    }

    @Test
    void nodeClosestToWorksOnKnownInput() {

    }

    @Test
    void edgeTargetNodeIdWorksOnKnownInput() {

    }

    @Test
    void edgeIsInvertedWorksOnKnownInput() {

    }

    @Test
    void edgeAttributesWorksOnKnownInput() {

    }

    @Test
    void edgeLengthWorksOnKnownInput() {

    }

    @Test
    void edgeElevationGainWorksOnKnownInput() {

    }

    @Test
    void edgeProfileWorksOnKnownInput() {

    }
}
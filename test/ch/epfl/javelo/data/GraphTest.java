package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void loadFromDoesNotThrowsIOException() {
        Path basePath = FileSystems.getDefault().getPath("lausanne");
        assertThrows(IOException.class, () -> {
            Graph.loadFrom(basePath);
        });
    }

    @Test
    void loadFromThrowsIOException() {
        Path basePath = FileSystems.getDefault().getPath("src");        assertThrows(IOException.class, () -> {
            Graph.loadFrom(basePath);
        });
    }

    /*
    @Test
    void loadFromWorksWithKnownInput() {
        Path basePath = FileSystems.getDefault().getPath("lausanne");
        Graph.loadFrom(basePath);
    }
    */

    @Test
    void nodeCountWorksOnKnownValues() {
    }

    @Test
    void nodePointWorksOnKnownInput() {

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
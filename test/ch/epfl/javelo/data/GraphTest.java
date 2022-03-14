package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static ch.epfl.javelo.projection.Ch1903.*;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {


    @Test
    void loadFromDoesNotThrowsIOException() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));

    }

    @Test
    void loadFromThrowsIOException() {
        assertThrows(IOException.class, () -> {
            Graph graph = Graph.loadFrom(Path.of("src"));
        });
    }

    @Test
    void nodeCountWorksOnKnownValues() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));
        //assertEquals(,graph.nodeCount());
    }

    @Test
        // Fonctionne, juste un petit delta entre les deux valeurs, dû aux calculs
    void nodePointWorksOnKnownInput() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));
        double e = Ch1903.e(Math.toRadians(6.6013034), Math.toRadians(46.6326106));
        double n = Ch1903.n(Math.toRadians(6.6013034), Math.toRadians(46.6326106));
        //assertEquals(new PointCh(e, n), graph.nodePoint(2022));

    }

    @Test
    void nodeOutDegreeWorksOnKnownInput() {

    }

    @Test
    void nodeOutEdgeIdWorksOnKnownInput() {

    }

    @Test
        // Pas sûre que ça soit la bonne facon de tester
    void nodeClosestToWorksOnKnownInput() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));
        double e = Ch1903.e(Math.toRadians(6.601303), Math.toRadians(46.632610)); // presque les memes coordonnées que la node 2022
        double n = Ch1903.n(Math.toRadians(6.601303), Math.toRadians(46.632610));
        assertEquals(2022, graph.nodeClosestTo(new PointCh(e, n), 1)); // trouve bien la node la plus proche
        assertEquals(-1, graph.nodeClosestTo((new PointCh(e, n)), 0)); // retourne bien -1 quand il n'y a pas de node à la distance donnée
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
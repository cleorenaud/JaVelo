package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.projection.Ch1903.*;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {


    @Test
        //DONE
    void loadFromDoesNotThrowsIOException() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));

    }

    @Test
        //DONE
    void loadFromThrowsIOException() {
        assertThrows(IOException.class, () -> {
            Graph graph = Graph.loadFrom(Path.of("src"));
        });
    }

    @Test
        //DONE
    void nodeCountWorksOnKnownValues() throws IOException {
        Path filePath = Path.of("lausanne");
        Graph graph = Graph.loadFrom(filePath);

        Path path = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(path)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        assertEquals(osmIdBuffer.capacity(), graph.nodeCount());
    }

    @Test
        //DONE
    void nodePointWorksOnKnownInput() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));
        double e = Ch1903.e(Math.toRadians(6.6013034), Math.toRadians(46.6326106));
        double n = Ch1903.n(Math.toRadians(6.6013034), Math.toRadians(46.6326106));
        PointCh p1 = graph.nodePoint(2022);
        PointCh p2 = new PointCh(e, n);
        assertTrue(p2.squaredDistanceTo(p1) < 0.01); // On calcule la distance entre le vrai point et celui attendu
        // Du au arrondis il n'y a pas d'égalité parfaite donc on cherche à être précis à 0,1 mètre près
    }

    @Test
        // Résultats normaux ?
    void nodeOutDegreeWorksOnKnownInput() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));
        assertEquals(2, graph.nodeOutDegree(2020));
        assertEquals(2, graph.nodeOutDegree(2021));
        assertEquals(2, graph.nodeOutDegree(2022));

        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        System.out.println("Id noeud 2020 : " + osmIdBuffer.get(2020));
        System.out.println("Id noeud 2021 : " + osmIdBuffer.get(2021));
        System.out.println("Id noeud 2022 : " + osmIdBuffer.get(2022));
    }

    // TO DO
    @Test
    void nodeOutEdgeIdWorksOnKnownInput() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));

        //assertEquals(, graph.nodeOutEdgeId(2021,0));
    }

    @Test
        //DONE
    void nodeClosestToWorksOnKnownInput() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));
        double e = Ch1903.e(Math.toRadians(6.601303), Math.toRadians(46.632610)); // presque les memes coordonnées que la node 2022
        double n = Ch1903.n(Math.toRadians(6.601303), Math.toRadians(46.632610));
        assertEquals(2022, graph.nodeClosestTo(new PointCh(e, n), 1)); // trouve bien la node la plus proche
        assertEquals(-1, graph.nodeClosestTo((new PointCh(e, n)), 0)); // retourne bien -1 quand il n'y a pas de node à la distance donnée
    }


    @Test
        //TO DO
    void edgeTargetNodeIdWorksOnKnownInput() throws IOException {

    }

    @Test
        //TO DO
    void edgeIsInvertedWorksOnKnownInput() {

    }

    @Test
        //TO DO
    void edgeAttributesWorksOnKnownInput() {

    }

    @Test
        //TO DO
    void edgeLengthWorksOnKnownInput() {

    }

    @Test
        //TO DO
    void edgeElevationGainWorksOnKnownInput() {

    }

    @Test
        //TO DO
    void edgeProfileWorksOnKnownInput() throws IOException {
        //Buffer pour les nodes
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes gn = new GraphNodes(b);

        //Buffers pour les edges
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });
        GraphEdges ge = new GraphEdges(edgesBuffer, profileIds, elevations);

        //Buffer pour le secteur
        ByteBuffer buffer1 = ByteBuffer.wrap(new byte[]{0b000,
                0b000, 0b000, 0b001, 0b000, 0b001

        });
        GraphSectors gs = new GraphSectors(buffer1);

        //Liste d'attributs
        List<AttributeSet> attributeSet = new ArrayList<>();
        Graph testGraph = new Graph(gn, gs, ge, attributeSet);

        Graph graph = Graph.loadFrom(Path.of("Lausanne"));

        System.out.println(graph.edgeProfile(7).applyAsDouble(1));
        System.out.println(graph.edgeProfile(7).applyAsDouble(2));
        System.out.println(graph.edgeProfile(7).applyAsDouble(3));
        System.out.println(graph.edgeProfile(7).applyAsDouble(30));
        ;
    }
}
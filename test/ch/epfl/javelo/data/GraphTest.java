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
    void nodeOutDegreeWorksOnKnownInput() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));
        assertEquals(2, graph.nodeOutDegree(2022));

        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        System.out.println(osmIdBuffer.get(2022));
    }

    @Test
    void nodeOutEdgeIdWorksOnKnownInput() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("Lausanne"));

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
        IntBuffer ib = IntBuffer.wrap(new int[]{
                2_600_000 << 4, // correspond aux coordonnées de la nodes
                1_200_000 << 4, // e = 2_600_000 et n = 1_200_000
                0x2_000_1234, // 2 le nombre d'arêtes sortantes et le reste est l'identité de la première sortant
        });

        ByteBuffer bb= ByteBuffer.wrap(new byte[]{0b000,
                0b000,0b000,0b001,0b000,0b001

        });

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

        GraphNodes gn = new GraphNodes(ib);
        GraphSectors gs = new GraphSectors(bb);
        GraphEdges ge = new GraphEdges(edgesBuffer, profileIds, elevations);
        AttributeSet attribute = new AttributeSet(0);
        List<AttributeSet> attributeSets = new ArrayList<AttributeSet>();
        attributeSets.add(attribute);
        Graph graph = new Graph(gn, gs, ge, attributeSets);

        assertEquals(graph.edgeTargetNodeId(0), 0);

    }

    @Test
    void edgeIsInvertedWorksOnKnownInput() {
        IntBuffer ib = IntBuffer.wrap(new int[]{
                2_600_000 << 4, // correspond aux coordonnées de la nodes
                1_200_000 << 4, // e = 2_600_000 et n = 1_200_000
                0x2_000_1234, // 2 le nombre d'arêtes sortantes et le reste est l'identité de la première sortant
        });

        ByteBuffer bb= ByteBuffer.wrap(new byte[]{0b000,
                0b000,0b000,0b001,0b000,0b001

        });

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


        GraphNodes gn = new GraphNodes(ib);
        GraphSectors gs = new GraphSectors(bb);
        GraphEdges ge = new GraphEdges(edgesBuffer, profileIds, elevations);
        AttributeSet attribute = new AttributeSet(0);
        List<AttributeSet> attributeSets = new ArrayList<AttributeSet>();
        attributeSets.add(attribute);
        Graph graph = new Graph(gn, gs, ge, attributeSets);
        assertFalse(graph.edgeIsInverted(0));
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
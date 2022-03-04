package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodesTest {

    IntBuffer b = IntBuffer.wrap(new int[]{
            2_600_000 << 4,
            1_200_000 << 4,
            0x2_000_1234
    });
    GraphNodes ns = new GraphNodes(b);

    IntBuffer c = IntBuffer.wrap(new int[]{
            2_600_000 << 4,
            1_200_000 << 4,
            0
    });
    GraphNodes ns2 = new GraphNodes(c);

    @Test
    void graphNodesCountWorksOnKnownBuffer() {
        assertEquals(1, ns.count());

    }

    @Test
    void nodeEWorksOnKnownNode() {
        assertEquals(2_600_000 << 4, ns.nodeE(0));
    }

    @Test
    void nodeNWorksOnKnownNode() {
        assertEquals(1_200_000 << 4, ns.nodeN(0));
    }

    @Test
    void outDegreeWorksOnKnownNode() {
        // Node avec des arrêtes sortantes
        assertEquals(2, ns.outDegree(0));

        // Node avec aucune arrête sortante
        assertEquals(0, ns2.outDegree(0));

    }

    @Test
    void edgeidWorksOnKnownNode() {
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }


}
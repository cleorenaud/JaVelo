package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphEdgesTest {

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

    GraphEdges edges =
            new GraphEdges(edgesBuffer, profileIds, elevations);

    @Test
    void isInvertedWorksOnKnownEdge() {
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);
        assertTrue(edges.isInverted(0));
    }

    @Test
    void targetNodeIdWorksOnKnownEdge() {
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);
        assertEquals(12, edges.targetNodeId(0));
    }

    @Test
    void lengthWorksOnKnownEdge() {
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);
        assertEquals(16.6875, edges.length(0));
    }

    @Test
    void elevationGainWorksOnKnownEdge() {
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);
        assertEquals(16.0, edges.elevationGain(0));
    }

    @Test
    void hasProfileWorksWithProfil() {
        // Arrête possédant un profil
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        assertTrue(edges.hasProfile(0));
    }

    @Test
    void profileSamplesWorksOnKnownEdge() {
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

    @Test
    void attributesIndexWorksOnKnownEdge() {
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);
        assertEquals(2022, edges.attributesIndex(0));
    }
}
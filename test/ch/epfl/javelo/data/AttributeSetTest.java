package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;

import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTest {
    @Test
    void attributeSetThrowsOnInvalid() {
        // Vérifie lance un IllegalArgumentException()
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet((long) Math.scalb(1, 62));
        });
    }

    @Test
    void ofWorksWithKnownAttributes() {
        AttributeSet set = new AttributeSet(0);
        set = set.of(Attribute.HIGHWAY_TRACK, Attribute.HIGHWAY_UNCLASSIFIED);
        // Vérifie que l'AttributeSet set contient bien les attributs attribués grace à la méthode of
        assertTrue((set.contains(Attribute.HIGHWAY_TRACK)) && (set.contains(Attribute.HIGHWAY_UNCLASSIFIED)));
        // Vérifie que l'AttributeSet set contient bien les memes attributs que l'AttributeSet défini avec les bits
        // correspondants
        assertEquals(set, new AttributeSet(34));
    }

    @Test
    void containsWorksWithKnownSet() {
        AttributeSet set = new AttributeSet(34);
        assertTrue(set.contains(Attribute.HIGHWAY_TRACK));
        assertTrue(set.contains(Attribute.HIGHWAY_UNCLASSIFIED));
    }

    @Test
    void intersectWorksWithKnownSets() {
        AttributeSet set1 = new AttributeSet(1);
        AttributeSet set2 = new AttributeSet(0);
        AttributeSet set3 = new AttributeSet(3);
        // Test quand les deux set sont identiques
        assertTrue(set1.intersects(set1));
        // Test quand l'intersection est vide
        assertFalse(set1.intersects(set2));
        // Test quand l'intersection n'est pas vide
        assertTrue(set1.intersects(set3));
    }

    @Test
    void toStringWorksOnRandomAttributeSet() {
        AttributeSet set = new AttributeSet(34);
        assertEquals("{highway=track,highway=unclassified}", set.toString());
    }

    @Test
    void toStringWorksOnEmptyAttributeSet() {
        AttributeSet set = new AttributeSet(0);
        assertEquals("{}", set.toString());
    }

}
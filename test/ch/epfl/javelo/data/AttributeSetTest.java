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

}
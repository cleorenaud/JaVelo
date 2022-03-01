package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTest {
    @Test
    void attributeSetThrowsOnInvalid() {
        // Vérifie lance un IllegalArgumentException()
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet();
        });
    }

    @Test
    void ofWorksWithKnownAttributes() {

    }

}
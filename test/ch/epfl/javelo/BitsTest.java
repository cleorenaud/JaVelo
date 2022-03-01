package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitsTest {
    @Test
    void bitsExtractSignedThrowsOnInvalidInputs() {
        // Vérifie que start + length > 32 lance un IllegalArgumentException()
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, 31, 2);
        });
        // Vérifie que start > 31 lance une IllegalArgumentException()
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, 32, 2);
        });
        // Vérifie que length < 0 lance un IllegalArgumentException()
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, 10, -1);
        });
        // Vérifie que start < 0 lance un IllegalArgumentException()
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, -1, 10);
        });
    }

    @Test
    void bitsExtractSignedWorksOnValidInputs() {
        // Test sur valeur simple
        assertEquals(0, Bits.extractSigned(0, 3, 4));
        // Test sur la valeur donnée dans le sujet
        // assertEquals(10, Bits.extractSigned(-889275714, 8, 4));
        // Test quand on garde le meme nombre
        assertEquals(-1, Bits.extractSigned(-1, 0, 31));
    }


    @Test
    void bitsExtractUnsignedThrowsOnInvalidInputs() {
        // Vérifie que start + length > 31 lance un IllegalArgumentException()
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, 30, 2);
        });
        // Vérifie que length < 0 lance un IllegalArgumentException()
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, 10, -1);
        });
        // Vérifie que start < 0 lance un IllegalArgumentException()
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, -1, 2);
        });
    }

    @Test
    void bitsExtractUnsignedWorksOnValidInput() {
        // Test sur valeur simple
        assertEquals(0, Bits.extractSigned(0, 3, 4));
        // Test sur la valeur donnée dans le sujet
        assertEquals(10, Bits.extractSigned(-889275714, 8, 4));
        // Test quand on garde le meme nombre
        // assertEquals(4294967295, Bits.extractSigned(-1, 0, 31));
    }

}
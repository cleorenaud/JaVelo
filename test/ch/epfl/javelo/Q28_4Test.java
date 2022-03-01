package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {
    @Test
    void OfIntWorksWithKnowValues() {
        // Test avec la valeur 0
        assertEquals(0, Q28_4.ofInt(0));
        // Test avec une valeur limite
        assertEquals(-16, Q28_4.ofInt(2147483647));
        // Test avec la valeur de l'énoncé
        assertEquals(-1343509536, Q28_4.ofInt(-889275714));
    }

    @Test
    void AsDoubleWorksWithKnownValues() {
        // Test avec la valeur 0
        assertEquals(0, Q28_4.asDouble(0));
        // Test avec une valeur limite
        assertEquals(-16, Q28_4.asDouble(2147483647));
        // Test avec la valeur de l'énoncé
        assertEquals(-1343509536, Q28_4.asDouble(-889275714));
    }

    @Test
    void AsFloatWorkWithKnownValues() {
        // Test avec la valeur 0
        assertEquals(0, Q28_4.asFloat(0));
        // Test avec une valeur limite
        assertEquals(-16, Q28_4.asFloat(2147483647));
        // Test avec la valeur de l'énoncé
        assertEquals(-1343509536, Q28_4.asFloat(-889275714));
    }
}
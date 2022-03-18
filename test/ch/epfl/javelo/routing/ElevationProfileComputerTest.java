package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;
import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {

    @Test
    void elevationProfileComputerThrowsIllegalArgumentException() {
        List<Edge> edges = new ArrayList<>();
        SingleRoute route = new SingleRoute(edges);

        // On vérifie qu'une exception est levée si maxStepLength vaut zéro
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile profile = elevationProfile(route, 0);
        });
        // On vérifie qu'une exception est levée si maxStepLength est strictement négative
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile profile = elevationProfile(route, -10);
        });
    }

    @Test
    void elevationProfileComputerWorksWithKnownInput() {
        List<Edge> edges = new ArrayList<>(); // Implémenter la liste pour pouvoir tester proprement
        SingleRoute route = new SingleRoute(edges);
    }

}
package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import com.sun.javafx.geom.Point2D;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapViewParametersTest {

    @Test
    public void mapViewParametersIsCorrect() {
        MapViewParameters mapViewParameters = new MapViewParameters(10, 135735, 92327);
        assertEquals(mapViewParameters.x(), 135735);
        assertEquals(mapViewParameters.y(), 92327);
        assertEquals(mapViewParameters.zoomLevel(), 10);
    }

    @Test
    public void topLeftIsCorrect() {
        MapViewParameters mapViewParameters = new MapViewParameters(10, 135735, 92327);
        assertEquals(mapViewParameters.topLeft(), new Point2D(135735, 92327));
    }

    @Test
    public void withMinXYIsCorrect() {
        MapViewParameters mvp = new MapViewParameters(10, 135735, 92327);
        MapViewParameters mvp2 = new MapViewParameters(10, 10, 23);
        assertEquals(mvp.withMinXY(10, 23), mvp2);
        assertEquals(mvp.zoomLevel(), mvp2.zoomLevel());
    }

    @Test
    public void pointAtIsCorrect() {
        MapViewParameters mvp = new MapViewParameters(10, 135735, 92327);
        assertEquals(mvp.pointAt(0,0), PointWebMercator.of(mvp.zoomLevel(), mvp.x(), mvp.y()));
        assertEquals(mvp.pointAt(10, 10), new PointWebMercator(0.2,0.2));
    }

    @Test
    public void viewXIsCorrect() {
        PointWebMercator p = PointWebMercator.of(10, 135735, 92327);

    }

    @Test
    public void viewYIsCorrect() {
    }


}
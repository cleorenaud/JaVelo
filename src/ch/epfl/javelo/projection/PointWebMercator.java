package ch.epfl.javelo.projection;

/**
 * Représente un point dans le système Web Mercator
 * @author Cléo Renaud (325156)
 */
public record PointWebMercator(double x, double y) {
    public PointWebMercator { //constructeur compact
        if (!((x <= 1) && (x >= 0) && (y <= 1) && (y >= 0))) {
            throw new IllegalArgumentException(); //lance une exception si le point n'est pas dans le territoire Suisse
        }
    }
}

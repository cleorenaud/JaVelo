package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * Non instanciable, offre des méthodes permettant de convertir entre des coordonnées WGS84 et une projection WebMercator
 *
 * @author Cléo Renaud (325156)
 */
public final class WebMercator {
    /**
     *
     */
    private WebMercator() {
    }

    /**
     * Méthode permettant de trouver la coordonnée x de la projection d'un point
     *
     * @param lon (double) la longitude du point
     * @return (double) la coordonée x du point
     */
    static double x(double lon) {
        return (lon + Math.PI) / 2 * Math.PI;
    }

    /**
     * Méthode permettant de trouver la coordonée y de la projection d'un point
     *
     * @param lat (double) la latitude du point
     * @return (double) la coordonnée y du point
     */
    static double y(double lat) {
        return ((Math.PI - Math2.asinh(Math.tan(lat))) / (2 * Math.PI));
    }

    /**
     * Méthode permettant de trouver la longitude d'un point dont on a la projection
     *
     * @param x (double) la coordonnée x du point projeté
     * @return (double) la longitude en radians
     */
    static double lon(double x) {
        return 2 * Math.PI * x - Math.PI;
    }

    /**
     * Méthode permettant de retrouver la latitude d'un point dont on a la projection
     *
     * @param y (double) la coordonnée y du point projeté
     * @return (double) la latitude en radians
     */
    static double lat(double y) {
        return (Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y)));
    }

}

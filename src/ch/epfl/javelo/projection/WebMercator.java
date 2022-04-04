package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * Non instantiable, offre des méthodes permettant de convertir entre des coordonnées WGS84 et une projection WebMercator
 *
 * @author Cléo Renaud (325156)
 */
public final class WebMercator {

    /**
     * Constructeur privé pour que la classe ne soit pas instantiable
     */
    private WebMercator() {
    }

    /**
     * Méthode permettant de trouver la coordonnée x de la projection d'un point
     *
     * @param lon (double) : la longitude du point en radians
     * @return (double) : la coordonnée x du point
     */
    public static double x(double lon) {
        return (lon + Math.PI) / (Math.PI*2);
    }

    /**
     * Méthode permettant de trouver la coordonnée y de la projection d'un point
     *
     * @param lat (double) : la latitude du point en radians
     * @return (double) : la coordonnée y du point
     */
    public static double y(double lat) {
        return ((Math.PI - Math2.asinh(Math.tan(lat))) / (2 * Math.PI));
    }

    /**
     * Méthode permettant de trouver la longitude d'un point dont on a la projection
     *
     * @param x (double) : la coordonnée x du point projeté
     * @return (double) : la longitude en radians
     */
    public static double lon(double x) {
        return 2 * Math.PI * x - Math.PI;
    }

    /**
     * Méthode permettant de retrouver la latitude d'un point dont on a la projection
     *
     * @param y (double) : la coordonnée y du point projeté
     * @return (double) : la latitude en radians
     */
    public static double lat(double y) {
        return (Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y)));
    }

}

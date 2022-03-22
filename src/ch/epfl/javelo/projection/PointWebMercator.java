package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * Représente un point dans le système Web Mercator
 *
 * @param x (double) la coordonnée x de point dans le système Web Mercator
 * @param y (double) la coordonnée y du point dans le système Web Mercator
 * @author Cléo Renaud (325156)
 */
public record PointWebMercator(double x, double y) {

    /**
     * Constructeur compact validant les coordinates qu'il reçoit et levant une exception si elles n'appartiennent pas à
     * l'intervalle [0;1]
     *
     * @param x (double) la coordonnée x reçue
     * @param y (double) la coordonnée y reçue
     * @throws IllegalArgumentException si les coordonnées qu'il reçoit ne sont pas toutes comprises dans l'intervalle [0,1]
     */
    public PointWebMercator { //constructeur compact
        Preconditions.checkArgument((x <= 1) && (x >= 0) && (y <= 1) && (y >= 0));
    }

    /**
     * Méthode de construction retournant le point Web Mercator dont les coordonnées sont x et y au niveau de zoom donné
     *
     * @param zoomLevel (int) le niveau de zoom donné
     * @param x         (double) la coordonnée x du point au niveau de zoom donné
     * @param y         (double) la coordonnée y du point au niveau de zoom donné
     * @return (PointWebMercator) le point Web Mercator correspondant aux paramètres donnés
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        x = x / Math.scalb(256, zoomLevel);
        y = y / Math.scalb(256, zoomLevel);
        return new PointWebMercator(x, y);
    }

    /**
     * Méthode de construction retournant le point Web Mercator correspondant au point du système de coordonnées suisse
     * donné
     *
     * @param pointCh (PointCh) le point exprimé dans le système de coordinates suisse
     * @return (PointWebMercator) le point Web Mercator correpondant aux paramètres donnés
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double x = WebMercator.x(pointCh.lon());
        double y = WebMercator.y(pointCh.lat());
        return new PointWebMercator(x, y);
    }

    /**
     * Méthode retournant la coordonnée x au niveau de zoom donné
     *
     * @param zoomLevel (int) le niveau de zoom
     * @return (double) la coordonnée x
     */
    public double xAtZoomLevel(int zoomLevel) {
        return (x * Math.scalb(256, zoomLevel));
    }

    /**
     * Méthode retournant la coordonnée y au niveau de zoom donné
     *
     * @param zoomLevel (int) le niveau de zoom
     * @return (double) la coordonnée y
     */
    public double yAtZoomLevel(int zoomLevel) {
        return (y * Math.scalb(256, zoomLevel));
    }

    /**
     * Méthode retournant la longitude du point en radians
     *
     * @return (double) la longitude du point
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * Méthode retournant la latitude du point en radians
     *
     * @return (double) la latitude du point
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * Méthode retournant le point de coordonnées suisses se trouvant a la même position que le recepteur
     *
     * @return (PointCh) le point de coordonnées suisses
     */
    public PointCh toPointCh() {
        double n=Ch1903.n(lon(),lat());
        double e= Ch1903.e(lon(),lat());
        if (SwissBounds.containsEN(e,n)) {
            return new PointCh(e,n);
        }
        return null;
    }

}
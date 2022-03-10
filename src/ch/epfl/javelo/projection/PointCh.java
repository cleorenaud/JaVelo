package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * Un enregistrement d'un point sur la carte suisse
 *
 * @author Roxanne Chevalley (339716)
 */

public record PointCh(double e, double n) {

    /**
     * constructeur de PointCh
     * @param e (double) : la coordonnée est du point
     * @param n (double) : la coordonnée nord du point
     * @throws IllegalArgumentException si les coordonnées fournies ne sont pas dans les limites de la Suisse
     */
    public PointCh { //constructeur compact
        if (!SwissBounds.containsEN(e, n)) {
            System.out.println(e);
            System.out.println(n);
            throw new IllegalArgumentException(); //lance une exception si le point n'est pas dans le territoire Suisse
        }
    }

    /**
     * Retourne la distance au carré entre le recepteur (this) et un argument (that)
     *
     * @param that (PointCh) : un autre point
     * @return le carrée de ladite distance (double)
     */
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(that.e() - this.e(), that.n() - this.n());
    }

    /**
     * Retourne la distance entre le récepteur (this) et un argument (that)
     *
     * @param that (PointCh) : un autre point
     * @return ladite distance (double)
     */
    public double distanceTo(PointCh that) {
        return Math2.norm(that.e() - this.e(), that.n() - this.n());
    }

    /**
     * Retourne la longitude du point, dans le système WGS84, en radians
     *
     * @return la longitude du point, dans le système WGS84, en radians (double)
     */
    public double lon() {
        return Ch1903.lon(e(), n());
    }

    /**
     * Retourne la latitude du point, dans le système WGS84, en radians
     *
     * @return retourne la latitude du point, dans le système WGS84, en radians (double)
     */
    public double lat() {
        return Ch1903.lat(e(), n());
    }


}

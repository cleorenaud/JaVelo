package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * Un enregistrement d'un point sur la carte suisse
 *
 * @param e (double) : la coordonnée est du point
 * @param n (double) : la coordonnée nord du point
 * @author Roxanne Chevalley (339716)
 */

public record PointCh(double e, double n) {
    /**
     * Constructeur de PointCh
     *
     * @param e (double) : la coordonnée est du point
     * @param n (double) : la coordonnée nord du point
     * @throws IllegalArgumentException si les coordonnées fournies ne sont pas dans les limites de la Suisse
     */
    public PointCh { //constructeur compact
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Retourne la distance au carré entre le récepteur (this) et un argument (that)
     *
     * @param that (PointCh) : un autre point
     * @return (double) : le carré de ladite distance
     */
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(that.e() - this.e(), that.n() - this.n());
    }

    /**
     * Retourne la distance entre le récepteur (this) et un argument (that)
     *
     * @param that (PointCh) : un autre point
     * @return (double) : ladite distance
     */
    public double distanceTo(PointCh that) {
        return Math2.norm(that.e() - this.e(), that.n() - this.n());
    }

    /**
     * Retourne la longitude du point, dans le système WGS84, en radians
     *
     * @return (double) : la longitude du point, dans le système WGS84, en radians
     */
    public double lon() {
        return Ch1903.lon(e(), n());
    }

    /**
     * Retourne la latitude du point, dans le système WGS84, en radians
     *
     * @return (double) : retourne la latitude du point, dans le système WGS84, en radians
     */
    public double lat() {
        return Ch1903.lat(e(), n());
    }


}

package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * Enregistrement représentant un point dans le système de coordonnées suisse
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
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Méthode retournant le carré de la distance en mètres séparant le récepteur (this) et l'argument that
     *
     * @param that (PointCh) : un autre point
     * @return (double) : le carré de la distance
     */
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(that.e() - e, that.n() - n);
    }

    /**
     * Méthode retournant la distance en mètres séparant le récepteur (this) et l'argument that
     *
     * @param that (PointCh) : un autre point
     * @return (double) : la distance
     */
    public double distanceTo(PointCh that) {
        return Math2.norm(that.e() - e, that.n() - n);
    }

    /**
     * Méthode retournant la longitude du point, dans le système WGS84, en radians
     *
     * @return (double) : la longitude du point, dans le système WGS84, en radians
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * Méthode retournant la latitude du point, dans le système WGS84, en radians
     *
     * @return (double) : retourne la latitude du point, dans le système WGS84, en radians
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }

}

/*
 * Author : Roxanne Chevalley
 * Date : 08.03.22
 */
package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

/**
 * Enregistrement représentant le point d'un itinéraire le plus proche d'un point de référence donné, qui se trouve dans
 * le voisinage de l'itinéraire
 *
 * @param point (pointCh) le point sur l'itinéraire
 * @param position (double) la position du point le long de l'itinéraire (en mètres)
 * @param distanceToReference (double) la distance entre le point et la référence (en mètres)
 * @author Cléo Renaud (325156)
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    // Constante représentant un point inexistant
    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     * Méthode retournant un point identique au récepteur (this) mais dont la position est décalée d'une différence
     * donnée (qui peut être positive ou négative)
     *
     * @param positionDifference (double) la différence donnée
     * @return (RoutePoint) le point décalé
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
      return new RoutePoint(this.point(), this.position() + positionDifference, this.distanceToReference());
    }

    /**
     * Méthode retournant this si sa distance à la référence est inférieure ou égale à celle de that, et that sinon
     *
     * @param that (RoutePoint) le point passé en argument
     * @return (RoutePoint) le point le plus proche de la référence
     */
    public RoutePoint min(RoutePoint that) {
        return (this.distanceToReference() <= that.distanceToReference()) ? this : that;
    }

    /**
     * Méthode retournant this si sa distance à la référence est inférieure ou égale à thatDistanceToReference, et une
     * nouvelle instance de RoutePoint dont les attributs sont les arguments passés à min sinon
     *
     * @param thatPoint               (PointCh) le point passé en argument
     * @param thatPosition            (double) la position de thatPoint
     * @param thatDistanceToReference (double) la distance entre la référence et thatPoint
     * @return (RoutePoint) le point le plus proche de la référence
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return (this.distanceToReference() <= thatDistanceToReference) ? this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}

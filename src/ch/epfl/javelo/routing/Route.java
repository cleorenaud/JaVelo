/*
 * Author : Roxanne Chevalley
 * Date : 08.03.22
 */
package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.awt.*;
import java.util.List;

/**
 * Interface représentant un itinéraire
 *
 * @author Cléo Renaud (325156)
 */
public interface Route {

    /**
     * Méthode retournant l'index du segment à la position donné
     *
     * @param position (double) la position donnée (en mètres)
     * @return (int) l'index du segment
     */
    int indexOfSegmentAt(double position);

    /**
     * Méthode retournant la longueur de l'itinéraire
     *
     * @return (double) la longueur de l'itinéraire (en mètres)
     */
    double length();

    /**
     * Méthode retournant la totalité des arêtes de l'itinéraire
     *
     * @return (List < Edge >) la liste contenant toutes les arêtes
     */
    List<Edge> edges();

    /**
     * Méthode retournant la totalité des points situés aux extrémités des arêtes de l'itinéraire
     *
     * @return (List < PointCh >) la liste contenant tous les points
     */
    List<PointCh> points();

    /**
     * Méthode retournant le point se trouvant à la position donnée le long de l'itinéraire
     *
     * @param position (double) la position donnée le long de l'itinéraire
     * @return (PointCh) le point se trouvant à la position donnée
     */
    PointCh pointAt(double position);

    /**
     * Méthode retournant l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position
     * donnée
     *
     * @param position (double) la position donnée
     * @return (int) l'identité du nœud
     */
    int nodeClosestTo(double position);

    /**
     * Méthode retournant le point de l'itinéraire se trouvant le plus proche du point de référence donné
     *
     * @param point (PointCh) le point de référence
     * @return (RoutePoint) le point de l'itinéraire
     */
    RoutePoint pointClosestTo(PointCh point);
}

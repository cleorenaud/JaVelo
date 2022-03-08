/*
 * Author : Roxanne Chevalley
 * Date : 08.03.22
 */
package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Enregistrement représentant une arête d'un itinéraire
 *
 * @param fromNodeId (int) l'identité du nœud de départ de l'arête
 * @param toNodeId   (int) l'identité du nœud d'arrivée de l'arête
 * @param fromPoint  (PointCh) le point de départ de l'arête
 * @param toPoint    (PointCh) le point d'arrivée de l'arête
 * @param length     (double) la longueur de l'arête (en mètres)
 * @param profile    (DoubleUnaryOperator) le profil en long de l'arête
 * @author Cléo Renaud (325156)
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    /**
     * Méthode statique retournant une instance de Edge dont les attributs fromNodeId et toNodeId sont ceux donnés, les
     * autres étant ceux de l'arête d'identité edgeId dans le graphe Graph
     *
     * @param graph      (Graph)
     * @param edgeId     (int)
     * @param fromNodeId (int)
     * @param toNodeId   (int)
     * @return (Edge) une instance de Edge
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {

        //return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId));
    }

    /**
     * Méthode retournant la position le long de l'arête qui se trouve la plus proche du point donné
     *
     * @param point (Pointh) le point donné
     * @return (double) la position le long de l'arête la plus proche (en mètres)
     */
    public double positionClosestToPoint(PointCh point) {
    }

    /**
     * Méthode retournant le point se trouvant à la position donnée sur l'arête
     *
     * @param position (double) la position donnée (en mètres)
     * @return (PointCh) le point se trouvant à la position donnée
     */
    public PointCh pointAt(double position) {

    }

    /**
     * Méthode retournant l'altitude à la position donnée sur l'arête
     *
     * @param position (double) la position donnée
     * @return (double) l'altitude à la position donnée (en mètres)
     */
    public double elevationAt(double position) {

    }
}

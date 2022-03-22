package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;

/**
 * Classe publique et immuable représentant un planificateur d'itinéraire
 * @author : Roxanne Chevalley (339716)
 */
public class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    /**
     * Construit in planificateur d'itinéraire pour le graphe et la fonction de coût donnée
     *
     * @param graph        (Graph) le graphe donné
     * @param costFunction (CostFunction) la fonction de coût donnée
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph=graph;
        this.costFunction=costFunction;

    }

    /**
     * Méthode retournant l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité
     * endNodeId dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
     *
     * @param startNodeId (int)
     * @param endNodeId   (int)
     * @return (Route) l'itinéraire de coût total minimal
     * @throws IllegalArgumentException si le nœud de départ et d'arrivée sont identiques
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) throws IllegalArgumentException {
        return null;
    }
}

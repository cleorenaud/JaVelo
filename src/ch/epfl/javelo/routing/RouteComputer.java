package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;


/**
 * Classe publique et immuable représentant un planificateur d'itinéraire
 *
 * @author : Roxanne Chevalley (339716)
 */
public final class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;
    private final float INITIATE= Float.POSITIVE_INFINITY;
    private final float MARK= Float.NEGATIVE_INFINITY;

    /**
     * Construit un planificateur d'itinéraire pour le graphe et la fonction de coût donnée
     *
     * @param graph        (Graph) : le graphe donné
     * @param costFunction (CostFunction) : la fonction de coût donnée
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;

    }

    /**
     * Méthode retournant l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud d'identité
     * endNodeId dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
     *
     * @param startNodeId (int) : l'identité du nœud de départ
     * @param endNodeId   (int) : l'identité du nœud d'arrivée
     * @return (Route) : l'itinéraire de coût total minimal
     * @throws IllegalArgumentException si le nœud de départ et d'arrivée sont identiques
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) throws IllegalArgumentException {
        Preconditions.checkArgument(startNodeId != endNodeId);

        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {

            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        // Pour chaque nœud de graphe, définir une distance et un nœud prédecesseur
        float[] distance = new float[graph.nodeCount()];
        int[] predecesseur = new int[graph.nodeCount()];
        int [] arrete = new int [graph.nodeCount()];

        Arrays.fill(distance,0,graph.nodeCount(),INITIATE);


        distance[startNodeId] = 0;
        PriorityQueue<WeightedNode> enExploration = new PriorityQueue<>();
        enExploration.add(new WeightedNode(startNodeId, distance[startNodeId]));

        while (!enExploration.isEmpty()) {
            int retenir=enExploration.remove().nodeId;

            if (retenir == endNodeId) { // On a trouvé le chemin et on construit maintenant la route correspondante
                List<Integer> noeudsTrajet = new ArrayList<>(); // Création d'une liste des nœuds du chemin
                int k = endNodeId;
                noeudsTrajet.add(k);
                while (k != startNodeId) { // Remplissage des nœuds du trajet (de la fin vers le début)
                    k = predecesseur[k];
                    noeudsTrajet.add(k);
                }

                List<Edge> edges = new ArrayList<>(); // On crée une liste d'arêtes

                int noeud1 = noeudsTrajet.remove(noeudsTrajet.size() - 1);
                while (noeudsTrajet.size() >= 1) { // Construction des arêtes à partir des nœuds
                    int noeud2 = noeudsTrajet.remove(noeudsTrajet.size() - 1);
                    Edge edge = Edge.of(graph, arrete[noeud2], noeud1, noeud2);
                    edges.add(edge);
                    noeud1=noeud2;
                }
                return new SingleRoute(edges);
            }

            // Si on n'a pas encore trouvé le chemin
            int[] nodesOut = new int[graph.nodeOutDegree(retenir)]; // Création d'un tableau avec les arêtes sortant de "retenir"
            for (int i = 0; i < nodesOut.length; i++) {
                int outEdge = graph.nodeOutEdgeId(retenir, i); // Recherche de l'arête
                int nPrime = graph.edgeTargetNodeId(outEdge); // Nœud associé à l'arête
                if(distance[nPrime]!=MARK){
                    float minimum = distance[nPrime];
                    float distanceN = (float) (distance[retenir] + graph.edgeLength(outEdge) * costFunction.costFactor(retenir, outEdge)); //(Dijkstra)
                    if (distanceN < minimum) {
                        distance[nPrime] = distanceN;
                        predecesseur[nPrime] = retenir;
                        arrete[nPrime]= outEdge;
                        // On calcule la distance à vol d'oiseau entre le nœud d'arrivée et le nœud considéré
                        float volOiseau = (float) graph.nodePoint(nPrime).distanceTo(graph.nodePoint(endNodeId));
                        float weightedDis= Math.abs(volOiseau + distance[nPrime]);
                        enExploration.add(new WeightedNode(nPrime, weightedDis));
                    }
                }
            }
            distance[retenir]=MARK; // Marque le nœud visité

        }
        return null;
    }

}
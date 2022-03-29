package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
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
     * @param graph        (Graph) le graphe donné
     * @param costFunction (CostFunction) la fonction de coût donnée
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;

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
        Preconditions.checkArgument(startNodeId != endNodeId);
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }
        //pour chaque noeud de graphe, définir une distance et un noeud prédecesseur
        float[] distance = new float[graph.nodeCount()];
        int[] predecesseur = new int[graph.nodeCount()];
        int [] arrete = new int [graph.nodeCount()];

        Arrays.fill(distance,0,graph.nodeCount(),INITIATE);


        distance[startNodeId] = 0;
        PriorityQueue<WeightedNode> enExploration = new PriorityQueue<>();
        enExploration.add(new WeightedNode(startNodeId, distance[startNodeId]));

        while (!enExploration.isEmpty()) {
            int retenir=enExploration.remove().nodeId;

            if (retenir == endNodeId) {//on a trouvé le chemin, on construit maintentant la route
                List<Integer> noeudsTrajet = new ArrayList<>();//création d'une liste des noeuds du chemin
                int k = endNodeId;
                noeudsTrajet.add(k);
                while (k != startNodeId) { //remplissage des noeuds du trajet (de la fin vers le début)
                    k = predecesseur[k];
                    noeudsTrajet.add(k);
                }

                List<Edge> edges = new ArrayList<>(); //création d'une liste d'arrêt

                int noeud1 = noeudsTrajet.remove(noeudsTrajet.size() - 1);
                while (noeudsTrajet.size() >= 1) { //construction des arrêtes à partir des noeuds
                    int noeud2 = noeudsTrajet.remove(noeudsTrajet.size() - 1);
                    Edge edge = Edge.of(graph, arrete[noeud2], noeud1, noeud2);
                    edges.add(edge);
                    noeud1=noeud2;
                }
                return new SingleRoute(edges);
            }


            //si on a pas encore trouvé le chemin
            int[] nodesOut = new int[graph.nodeOutDegree(retenir)]; // création d'un tableau avec les arrêtes sortant de "retenir"
            for (int i = 0; i < nodesOut.length; i++) {
                int outEdge = graph.nodeOutEdgeId(retenir, i);//recherche de l'arrête
                int nPrime = graph.edgeTargetNodeId(outEdge); //noeud associé à l'arrête
                if(distance[nPrime]!=MARK){
                    float minimum = distance[nPrime];
                    float distanceN = (float) (distance[retenir] + graph.edgeLength(outEdge) * costFunction.costFactor(retenir, outEdge)); //(Dijkstra)
                    if (distanceN < minimum) {
                        distance[nPrime] = distanceN;
                        predecesseur[nPrime] = retenir;
                        arrete[nPrime]= outEdge;
                        //on calcule la distance à vol d'oiseau entre le noeud d'arrivée et le noeud considéré
                        float volOiseau = (float) graph.nodePoint(nPrime).distanceTo(graph.nodePoint(endNodeId));
                        float weightedDis= Math.abs(volOiseau + distance[nPrime]);
                        enExploration.add(new WeightedNode(nPrime, weightedDis));
                    }
                }
            }
            distance[retenir]=MARK; //marque le noeud visité

        }
        return null;
    }

}
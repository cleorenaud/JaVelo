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
        int[] arrete = new int[graph.nodeCount()];

        for (int i = 0; i < graph.nodeCount(); i++) {
            distance[i] = Float.POSITIVE_INFINITY;
            predecesseur[i] = 0;
            arrete[i]=0;
        }

        distance[startNodeId] = 0;
        PriorityQueue<WeightedNode> enExploration = new PriorityQueue<>();
        //Set<Integer> enExploration = new TreeSet<Integer>();
        //enExploration.add(startNodeId);
        enExploration.add(new WeightedNode(startNodeId, distance[startNodeId]));

        while (!enExploration.isEmpty()) {
            int retenir=enExploration.remove().nodeId;
            //double mini = Double.POSITIVE_INFINITY;
            //int retenir = -1;
            //for (WeightedNode l : enExploration) {
            //on calcule la distance à vol d'oiseau entre le noeuds d'arrivé et les noeuds en exploration
            //double volOiseau = graph.nodePoint(l.nodeId).distanceTo(graph.nodePoint(endNodeId));
                /*if (Math.abs(distance[l.nodeId] + volOiseau )< mini) {//on trouve le noeuds avec la plus petite distance pour l'instant
                    mini = distance[l.nodeId] + volOiseau;
                    retenir = l.nodeId;
                }*/
            //}

            //enExploration.remove(retenir);

            if (retenir == endNodeId) {//on a trouvé le chemin, on construit maintentant la route
                List<Integer> noeudsTrajet = new ArrayList<>();//création d'une liste des noeuds du chemin
                int k = endNodeId;
                while (k != 0) { //remplissage des noeuds du trajet (de la fin vers le début)
                    noeudsTrajet.add(k);
                    k = predecesseur[k];
                }

                List<Edge> edges = new ArrayList<>(); //création d'une liste d'arrêt

                int noeud1= noeudsTrajet.remove(noeudsTrajet.size() - 1);
                while(noeudsTrajet.size()>=1){
                    int noeud2=noeudsTrajet.remove(noeudsTrajet.size() - 1);
                    int edgeId= arrete[noeud2];
                    /*int edgeId = -1;
                    int outEdge;
                    for (int i = 0; i < graph.nodeOutDegree(noeud1); i++) { //trouver l'index de l'arrête

                        outEdge = graph.nodeOutEdgeId(noeud1, i);
                        if (graph.edgeTargetNodeId(outEdge) == noeud2) {
                            edgeId = outEdge;
                        }

                    }*/
                    Edge edge = Edge.of(graph, edgeId, noeud1, noeud2);
                    edges.add(edge);
                    noeud1=noeud2;
                }
                return new SingleRoute(edges);
            }


            int[] nodesOut = new int[graph.nodeOutDegree(retenir)]; // création d'un tableau avec les arrêtes sortant de "retenir"
            for (int i = 0; i < nodesOut.length; i++) {
                int outEdge = graph.nodeOutEdgeId(retenir, i);//recherche de l'arrête
                int nPrime = graph.edgeTargetNodeId(outEdge); //noeud associé à l'arrête
                if(distance[nPrime]!=Float.NEGATIVE_INFINITY){
                    float distanceN = (float) (distance[retenir] + graph.edgeLength(outEdge) * costFunction.costFactor(retenir, outEdge));
                    if (distanceN < distance[nPrime]) {
                        distance[nPrime] = distanceN;
                        predecesseur[nPrime] = retenir;
                        arrete[nPrime] =outEdge;
                        //on calcule la distance à vol d'oiseau entre le noeuds d'arrivé et les noeuds en exploration
                        float volOiseau = (float) graph.nodePoint(nPrime).distanceTo(graph.nodePoint(endNodeId));
                        float weightedDis= volOiseau + distance[nPrime];
                        enExploration.add(new WeightedNode(nPrime, weightedDis));

                    }
                }
            }
            distance[retenir]=Float.NEGATIVE_INFINITY; //marque le noeud visité

        }
        return null;
    }

}

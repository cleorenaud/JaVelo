package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import java.util.*;


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
        //pour chaque noeud de graphe, définir une distance et un noeud prédecesseur
        double [] distance= new double[graph.nodeCount()];
        int [] predecesseur = new int [graph.nodeCount()];
        for (int i = 0; i < graph.nodeCount() ; i++) {
            distance[i]=Double.POSITIVE_INFINITY;
            predecesseur[i]=0;
        }
        distance[startNodeId]=0;
        Set<Integer> enExploration= new TreeSet<Integer>();
        enExploration.add(startNodeId);
        while(!enExploration.isEmpty()){
            double mini= Double.POSITIVE_INFINITY;
            int retenir=-1;
            for (int l : enExploration) {
                if (distance[l]<mini){
                    mini=distance[l];
                    retenir=l;
                }
            }
            enExploration.remove(retenir);

            if(retenir==endNodeId){ //on a trouvé on construit maintentant la route
                List<Integer> noeudsTrajet= new ArrayList<>();
                int k=endNodeId;
                while(k!=0){ //remplissage des noeuds du trajet (de la fin vers le début)
                    noeudsTrajet.add(k);
                    k=predecesseur[k];
                }
                List<Edge> edges = new ArrayList<>();
                while(noeudsTrajet.size()>=2){ //construction des arrêtes à partir des noeuds
                    int noeud1=noeudsTrajet.get(noeudsTrajet.size()-1);
                    int noeud2=noeudsTrajet.get(noeudsTrajet.size()-2);
                    int edgeId=-1;
                    for (int i = 0; i < graph.nodeOutDegree(noeud1); i++) { //trouver l'index de l'arrête
                        int outEdge=graph.nodeOutEdgeId(retenir,i);
                        if(graph.edgeTargetNodeId(outEdge)==noeud2){
                            edgeId= outEdge;
                        }
                    }
                    Edge edge = Edge.of(graph, edgeId, noeud1, noeud2);
                    edges.add(edge);
                }
                return new SingleRoute(edges);
            }


            int [] nodesOut = new int [graph.nodeOutDegree(retenir)];
            for (int i = 0; i <nodesOut.length ; i++) {
                int outEdge=graph.nodeOutEdgeId(retenir,i);
                int nPrime= graph.edgeTargetNodeId(outEdge);
                double minimum = distance[nPrime];
                double distanceN = distance[retenir] + graph.edgeLength(outEdge)* costFunction.costFactor(retenir,outEdge);
                if(distanceN<minimum){
                    distance[nPrime]=distanceN;
                    predecesseur[nPrime]= retenir;
                    enExploration.add(nodesOut[i]);

                }
            }

        }
        return null;
    }

}

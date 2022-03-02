/*
 * Author : Roxanne Chevalley
 * Date : 02.03.22
 */
package ch.epfl.javelo.data;

import java.nio.*;

/**
 * enregistrement représentant le tableau de tous les nœuds du graphe JaVelo
 * avec comme unique argument buffer (IntBuffer), la mémoire tampon
 * contenant la valeur des attributs de la totalité des nœuds du graphe.
 *
 * @author: Roxanne Chevalley (339716)
 */

public record GraphNodes(IntBuffer buffer) {
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * méthode qui retourne le nombre total de nœuds
     * @return le nombre total de nœuds (int)
     */
    public int count(){
        return buffer.capacity()/NODE_INTS;
    }

    /**
     * méthode qui retourne la coordonnée E du nœud d'identité donnée.
     * @param nodeId (int) : l'identité du nœud
     * @return la coordonnée E du nœud d'identité donnée (double)
     */
    public double nodeE(int nodeId){
        int indexE =NODE_INTS*nodeId + OFFSET_E;
        return buffer.get(indexE);
    }


    /**
     * méthode qui retourne la coordonnée N du nœud d'identité donnée.
     * @param nodeId (int) : l'identité du nœud
     * @return la coordonnée N du nœud d'identité donnée (double)
     */
    public double nodeN(int nodeId){
        int indexN=NODE_INTS*nodeId +OFFSET_N;
        return buffer.get(indexN);
    }

    /**
     * méthode qui retourne le nombre d'arêtes sortant du nœud d'identité donnée
     * @param nodeId (int) : l'identité du nœud
     * @return le nombre d'arêtes sortant du nœud d'identité donnée (int)
     */
    public int outDegree(int nodeId){
        int indexD=NODE_INTS*nodeId + OFFSET_OUT_EDGES;
        int d= (buffer.get(indexD))>>>28;
        return d;
    }

    /**
     * méthode qui retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId
     * @param nodeId (int) : l'identité du nœud
     * @param edgeIndex (int) : l'index de l'arrête recherchée
     * @return l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId
     */
    public int edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int indexD=NODE_INTS*nodeId + OFFSET_OUT_EDGES;
        int d=((buffer.get(indexD))<<4)>>>4;
        return d + edgeIndex;

    }


}

/*
 * Author : Roxanne Chevalley
 * Date : 02.03.22
 */
package ch.epfl.javelo.data;

import java.nio.*;

/**
 * enregistrement représentant le tableau de tous les noeuds du graphe JaVelo
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

    public int count(){
        return buffer.capacity()/NODE_INTS;
    }

    public double nodeE(int nodeId){
        int indexE =NODE_INTS*nodeId + OFFSET_E;
        return buffer.get(indexE);
    }

}

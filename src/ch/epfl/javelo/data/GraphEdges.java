/*
 * Author : Roxanne Chevalley
 * Date : 02.03.22
 */
package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.*;

/**
 * Enregistrement qui représente le tableau de toutes les arêtes du graphe JaVelo
 *
 * @author : Roxanne Chevalley (339716)
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_W = 0;
    private static final int OFFSET_L = OFFSET_W + Integer.BYTES;
    private static final int OFFSET_E = OFFSET_L + Short.BYTES;
    private static final int OFFSET_ATT = OFFSET_E + Short.BYTES;
    private static final int EDGES_INTS = OFFSET_ATT + Short.BYTES;

    /**
     * méthode qui retourne vrai si l'arête d'identité donnée va dans le sens inverse
     * de la voie OSM dont elle provient et retourne faux sinon.
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return vrai si l'arête d'identité donnée va dans le sens inverse
     * de la voie OSM dont elle provient et retourne faux sinon (boolean)
     */
    public boolean isInverted(int edgeId) {
        return (edgesBuffer.getInt(edgeId * EDGES_INTS + OFFSET_W) < 0);
    }

    /**
     * méthode qui retourne l'identité du nœud destination de l'arête d'identité donnée
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return l'identité du nœud destination de l'arête d'identité donnée (int)
     */
    public int targetNodeId(int edgeId) {
        if (isInverted(edgeId)) {
            return ~edgesBuffer.getInt(edgeId * EDGES_INTS + OFFSET_W);
        }
        return edgesBuffer.getInt(edgeId * EDGES_INTS + OFFSET_W);
    }

    /**
     * méthode qui retourne la longueur, en mètres, de l'arête d'identité donnée.
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return la longueur, en mètres, de l'arête d'identité donnée
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_L));
    }

    /**
     * méthode qui retourne le dénivelé positif, en mètres, de l'arête d'identité donnée
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return le dénivelé positif, en mètres, de l'arête d'identité donnée
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_E));
    }

    /**
     * méthode qui retourne vrai si l'arête d'identité donnée possède un profil et faux sinon.
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return vrai si l'arête d'identité donnée possède un profil et faux sinon
     */
    public boolean hasProfile(int edgeId) {
        double m = Bits.extractUnsigned(profileIds.get(edgeId),31,2);
        return (m != 0);
    }

    /**
     * méthode qui retourne le tableau des échantillons du profil de l'arête d'identité donnée,
     * qui est vide si l'arête ne possède pas de profil
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return le tableau des échantillons du profil de l'arête d'identité donnée
     */
    public float[] profileSamples(int edgeId) {
        if (!hasProfile(edgeId)) {
            return new float[0];
        }
        int nbEch = 1 + (int) Math.ceil(length(edgeId) / 2.0); //calcul le nombre d'échantillons
        float[] tabTemp = new float[nbEch]; //créations de deux tableaux
        float[] tabFin = new float[nbEch]; //tableau au cas ou la route est inversée
        int firstIndex = Bits.extractUnsigned(profileIds.get(edgeId),29,30); //savoir le type de profil
        double m = Bits.extractUnsigned(profileIds.get(edgeId),31,2);; //identité du premier échantillon
        tabTemp[0] = Q28_4.asFloat(elevations.get(firstIndex)); //remplit avec le premier échantillon
        for (int i = firstIndex + 1; i < firstIndex + nbEch; ++i) { //remplissage du tableau
            int n = i - firstIndex;
            if (m == 1) {
                tabTemp[n] = Q28_4.asFloat(elevations.get(i));
            }
            if (m == 2) {
                double k = Math.ceil(((double) n) / 2.0); //savoir quel index chercher dans elevations
                int fact = n % 2 + 1; //permettra de savoir ce qu'il faut extraire de "info"
                short info = elevations.get(firstIndex + (int) k);
                short dif = (short) Bits.extractUnsigned(info, 8 * fact-1, 8);
                dif = (short) Q28_4.asFloat(dif);
                tabTemp[n] = tabTemp[n - 1] + dif; //remplissage du tableau
            }
            if (m == 3) {
                double k = Math.ceil(((double) n) / 4.0); //savoir quel index chercher dans elevations
                int fact = 1;
                if (n % 4 == 1) { //permettra de savoir ce qu'il faut extraire de "info"
                    fact = 4;
                }
                if (n % 4 == 2) {
                    fact = 3;
                }
                if (n % 4 == 3) {
                    fact = 2;
                }
                short info = elevations.get(firstIndex + (int) k);
                short dif = (short) Bits.extractUnsigned(info, 4 * fact-1, 4);
                dif = (short) Q28_4.asFloat(dif);
                tabTemp[n] = tabTemp[n - 1] + dif; //remplissage du tableau
            }

        }
        if (!isInverted(edgeId)) {
            return tabTemp; //tabTemp est dans le bon ordre si la route n'est pas inversée
        }
        for (int i = 0; i < nbEch; ++i) {
            tabFin[i] = tabTemp[nbEch - 1 - i]; //inverse les indices si la route est inversée
        }
        return tabFin;

    }

    /**
     * méthode qui retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     */
    public int attributesIndex(int edgeId) {
        return edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_ATT);
    }


}

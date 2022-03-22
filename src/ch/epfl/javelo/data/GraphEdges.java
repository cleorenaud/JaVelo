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
 * @param edgesBuffer (ByteBuffer) la mémoire tampon contenant le sens de l'arête, l'identité du nœud de destination,
 *                    la longueur, le dénivelé positif et l'identité de l'ensemble d'attributs OSM
 * @param profileIds (IntBuffer) le type de profil et l'identité du premier échantillon du profil
 * @param elevations (ShortBuffer) la mémoire tampon contenant la totalité des échantillons des profils, compressés ou non
 * @author : Roxanne Chevalley (339716)
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_W = 0;
    private static final int OFFSET_L = OFFSET_W + Integer.BYTES;
    private static final int OFFSET_E = OFFSET_L + Short.BYTES;
    private static final int OFFSET_ATT = OFFSET_E + Short.BYTES;
    private static final int EDGES_INTS = OFFSET_ATT + Short.BYTES;

    /**
     * Méthode qui retourne vrai si l'arête d'identité donnée va dans le sens inverse
     * de la voie OSM dont elle provient et retourne faux sinon.
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (boolean) vrai si l'arête d'identité donnée va dans le sens inverse
     * de la voie OSM dont elle provient et retourne faux sinon (boolean)
     */
    public boolean isInverted(int edgeId) {
        return (edgesBuffer.getInt(edgeId * EDGES_INTS + OFFSET_W) < 0);
    }

    /**
     * Méthode qui retourne l'identité du nœud destination de l'arête d'identité donnée
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (int) l'identité du nœud destination de l'arête d'identité donnée (int)
     */
    public int targetNodeId(int edgeId) {
        if (isInverted(edgeId)) {
            return ~edgesBuffer.getInt(edgeId * EDGES_INTS + OFFSET_W);
        }
        return edgesBuffer.getInt(edgeId * EDGES_INTS + OFFSET_W);
    }

    /**
     * Méthode qui retourne la longueur, en mètres, de l'arête d'identité donnée.
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (double) la longueur, en mètres, de l'arête d'identité donnée
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(Bits.extractUnsigned(edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_L),0,16));
    }

    /**
     * Méthode qui retourne le dénivelé positif, en mètres, de l'arête d'identité donnée
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (double) le dénivelé positif, en mètres, de l'arête d'identité donnée
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(Bits.extractUnsigned(edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_E),0,16));
    }

    /**
     * Méthode qui retourne vrai si l'arête d'identité donnée possède un profil et faux sinon.
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (boolean) vrai si l'arête d'identité donnée possède un profil et faux sinon
     */
    public boolean hasProfile(int edgeId) {
        double m = Bits.extractUnsigned(profileIds.get(edgeId) ,30,2);
        return (m != 0);
    }

    /**
     * Méthode qui retourne le tableau des échantillons du profil de l'arête d'identité donnée,
     * qui est vide si l'arête ne possède pas de profil
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (float[]) le tableau des échantillons du profil de l'arête d'identité donnée
     */
    public float[] profileSamples(int edgeId) {
        if (!hasProfile(edgeId)) {
            return new float[0];
        }
        int nbEch = 1 + (int) Math.ceil(length(edgeId) / 2.0); //calcul le nombre d'échantillons dont on aura besoin
        float[] tabTemp = new float[nbEch]; //créations de deux tableaux
        float[] tabFin = new float[nbEch]; //tableau au cas où la route est inversée
        int firstIndex = Bits.extractUnsigned(profileIds.get(edgeId),0,30); //savoir le type de profil
        double m = Bits.extractUnsigned(profileIds.get(edgeId),30,2);; //identité du premier échantillon
        tabTemp[0] = Q28_4.asFloat(Bits.extractUnsigned(elevations.get(firstIndex),0,16)); //remplit avec le premier échantillon
        for (int i = firstIndex + 1; i < firstIndex + nbEch; ++i) { //remplissage du tableau
            int n = i - firstIndex;
            if (m == 1) {
                tabTemp[n] = Q28_4.asFloat(Bits.extractUnsigned(elevations.get(i),0,16));
            }
            if (m == 2) {
                double k = Math.ceil(((double) n) / 2.0); //savoir quel index chercher dans elevations
                int fact = n % 2; //permettra de savoir ce qu'il faut extraire de "info"
                short info = elevations.get(firstIndex + (int) k);
                float dif = (float) Bits.extractSigned(info, 8 * fact, 8);
                dif = (float) Q28_4.asDouble((int)dif);
                tabTemp[n] = tabTemp[n - 1] + dif; //remplissage du tableau
            }
            if (m == 3) {
                double k = Math.ceil(((double) n) / 4.0); //savoir quel index chercher dans elevations
                int fact = 0;
                if (n % 4 == 1) { //permettra de savoir ce qu'il faut extraire de "info"
                    fact = 3;
                }
                if (n % 4 == 2) {
                    fact = 2;
                }
                if (n % 4 == 3) {
                    fact = 1;
                }
                short info = elevations.get(firstIndex + (int) k);
                float dif = (float) Bits.extractSigned(info, 4 * fact, 4);
                dif = (float) Q28_4.asDouble((int)dif);
                tabTemp[n] = tabTemp[n - 1] + dif; //remplissage du tableau
            }

        }
        if (!isInverted(edgeId)) {
            return tabTemp; //tabTemp est dans le bon ordre si la route n'est pas inversée
        }
        for (int i = 0; i < nbEch; ++i) {
            tabFin[i] = tabTemp[nbEch - 1 - i];//inverse les indices si la route est inversée
        }
        return tabFin;

    }

    /**
     * Méthode qui retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     *
     * @param edgeId (int) : l'identité de l'arête
     * @return (int) l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     */
    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_ATT));
    }


}

/*
 * Author : Roxanne Chevalley
 * Date : 02.03.22
 */
package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Enregistrement représentant un tableau contenant les 16384 secteurs de JaVelo avec pour seul attribut buffer
 * (ByteBuffer), la mémoire tampon contenant la valeur des attributs de la totalité des secteurs
 *
 * @author Cléo Renaud (325156)
 */
public record GraphSectors(ByteBuffer buffer) {
    // Donne l'index à partir duquel on accède à l'identité du premier nœud du secteur, exprimé en octets
    private static final int OFFSET_FIRST = 0;
    // Donne l'index à partir duquel on accède à l'identité du nœud situé juste après le dernier nœud du secteur,
    // exprimé en octets
    private static final int OFFSET_SECOND = OFFSET_FIRST + Integer.BYTES;
    // Donne le nombre d'octets nécessaire pour représenter un secteur
    private static final int SECTORS_INTS = OFFSET_SECOND + Short.BYTES;

    /**
     * Enregistrement représentant un secteur uniquement doté de deux attributs : l'identité du premier nœud du secteur
     * et l'identité du nœud situé après le dernier nœud du secteur
     */
    public record Sector(int startNodeId, int endNodeId) {
        // Les deux attributs d'un secteur doivent être interpretés de façon non signée (U32 pour l'identité du premier
        // nœud et U16 pour le nombre de nœuds

    }

    /**
     * Méthode retournant la liste de tous les secteurs ayant une intersection avec le carré centré au point donné et
     * de côté égal au double de la distance donnée
     *
     * @param center   (PointCh) le point donné
     * @param distance (double) la moitié de la taille du côté du carré centré sur center
     * @return (List < Sector >) la liste de tous les secteurs ayant une intersection avec le carré centré au point donné
     */
    /*
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        // La liste de tous les secteurs ayant une intersection avec le carré défini dans les paramètres
        ArrayList<Sector> intersect = new ArrayList<>();

        // On cherche a calculer les coordonnées des 4 sommets du carré (en coordonées N et E
        // Ensuite on convertira nos nodes des secteurs deja definis en coordonnées N et E pour voir si ils sont inclus
        // Cela se fait au moyen des methodes nodeE et nodeN

        // Les quatres sommets de notre carré
        PointCh hautGauche = new PointCh(center.e() - distance, center.n() + distance);
        PointCh basGauche = new PointCh(center.e() - distance, center.n() - distance);
        PointCh hautDroit = new PointCh(center.e() + distance, center.n() + distance);
        PointCh basDroit = new PointCh(center.e() + distance, center.n() - distance);



        // Il y a une intersection si le carré et le secteur possedent au moins un point commun
        for (int i = 0; i < buffer.capacity(); i++) {

            // La premiere et la derniere node pour chaque secteur
            int startNode = buffer.getInt(SECTORS_INTS * i);
            int endNode = Short.toUnsignedInt(buffer.getShort(OFFSET_SECOND * i + 1));

            for (int j = startNode; j < endNode; j++) {
                // On cherche maintenant les coordonnée de nos nodes
                int node = j;
                double eNode = GraphNodes.nodeE(node);
                double nNode = nodeN(node);

            }

            intersect.add(new Sector(startNode, endNode));


        }
        return intersect;
    }
    */


}

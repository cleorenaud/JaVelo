/*
 * Author : Roxanne Chevalley
 * Date : 02.03.22
 */
package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.sql.Array;
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
    }

    /**
     * Méthode retournant la liste de tous les secteurs ayant une intersection avec le carré centré au point donné et
     * de côté égal au double de la distance donnée
     *
     * @param center   (PointCh) le point donné
     * @param distance (double) la moitié de la taille du côté du carré centré sur center
     * @return (List < Sector >) la liste de tous les secteurs ayant une intersection avec le carré centré au point donné
     */

    public List<Sector> sectorsInArea(PointCh center, double distance) {
        // On déclare la liste de tous les secteurs ayant une intersection avec le carré défini dans les paramètres
        // Elle sera implémentée a la fin de la méthode
        ArrayList<Sector> intersect = new ArrayList<>();

        ArrayList<PointCh> sommets = new ArrayList<>();
        // On cherche a calculer les coordonnées des 4 sommets du carré (en coordonées suisses).
        // On les stock dans un ArrayList pour simplifier les calculs suivants au moyen d'une boucle for
        double estMoinDis= center.e()-distance;
        if(estMoinDis<SwissBounds.MIN_E){
            estMoinDis= SwissBounds.MIN_E;
        }
        double estPlusDis = center.e()+distance;
        if(estPlusDis>SwissBounds.MAX_E){
            estPlusDis= SwissBounds.MAX_E;
        }
        double nordMoinDis= center.n()-distance;
        if(nordMoinDis<SwissBounds.MIN_N){
            nordMoinDis= SwissBounds.MIN_N;
        }
        double nordPlusDis = center.n()+distance;
        if(nordPlusDis>SwissBounds.MAX_N){
            nordPlusDis= SwissBounds.MAX_N;
        }

        sommets.add(new PointCh(estMoinDis, nordPlusDis)); // Sommet en haut à gauche (assurément dans SwissBounds)
        sommets.add(new PointCh(estPlusDis, nordPlusDis)); // Sommet en haut à droite (assurément dans SwissBounds)
        sommets.add(new PointCh(estMoinDis, nordMoinDis)); // Sommet en bas à gauche (assurément dans SwissBounds)
        sommets.add(new PointCh(estPlusDis, nordMoinDis)); // Sommet en bas à droite (assurément dans SwissBounds)

        int[] secteurs = new int[4];
        // Un point de coordonnées (n, e) se trouve dans le secteur (x, y) (x appartient à [0, 128] et y à [0, 128]
        // comme il y a 128x128 secteurs dans notre repère en coordonnées suisses)
        // Pour determiner le secteur contenant notre point on doit prendre la partie entiere de notre calcul
        // On stock le numéro du secteur des sommets du carré dans un tableau (plus simple comme dans une boucle fort)
        for (int i = 0; i < sommets.size(); i++) {
            int x = (int) Math.floor(((sommets.get(i)).e() - SwissBounds.MIN_E) / ((SwissBounds.MAX_E - SwissBounds.MIN_E) / 128));
            int y = (int) Math.floor(((sommets.get(i)).n() - SwissBounds.MIN_N) / ((SwissBounds.MAX_N - SwissBounds.MIN_N) / 128));
            // on passe de coordonnées suisses du secteur aux numéros de celui ci, qu'on stocke dans un tableau
            secteurs[i] = x + 128 * y;
        }

        // Maintenant on cherche
        int hauteur = (secteurs[0] - secteurs[2])/128;
        int largeur = secteurs[3] - secteurs[2];
        for (int i = 0; i < hauteur; i++) {
            for (int j = 0; j < largeur; j++) {
                // Numéro du secteur qu'on va ajouter a l'ArrayList intersect
                int secteur = secteurs[2] + i * 128 + j;
                // On cherche la première et la derniere node du secteur grace au ByteBuffer buffer
                int startNode = buffer.getInt(SECTORS_INTS * secteur);
                int numberNode = Short.toUnsignedInt(buffer.getShort(SECTORS_INTS * secteur + OFFSET_SECOND));
                int endNode= startNode + numberNode;
                // On peut maintenant créer un secteur et l'ajouter à notre ArrayList contenant tous les secteurs ayant
                // une intersection avec le carré passé en paramètres
                intersect.add(new Sector(startNode, endNode));
            }
        }

        return intersect;
    }


}

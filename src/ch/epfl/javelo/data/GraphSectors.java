/*
 * Author : Roxanne Chevalley
 * Date : 02.03.22
 */
package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Enregistrement représentant un tableau contenant les 16384 secteurs de JaVelo
 *
 * @author Cléo Renaud (325156)
 */
public record GraphSectors() {
    private static ByteBuffer buffer;

    public GraphSectors() {
    }

    record Sector() {
        static int startNodeId;
        static int endNodeId;
    }

    /**
     * Méthode retournant la liste de tous les secteurs ayant une intersection avec le carré centré au point donné et
     * de côté égal au double de la distance donnée
     *
     * @param center   (PointCh) le point donné
     * @param distance (double) la moitié de la taille du côté du carré centré sur center
     * @return (Lise < Sector >) la liste de tous les secteurs ayant une intersection avec le carré centré au point donné
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        return null;
    }


}

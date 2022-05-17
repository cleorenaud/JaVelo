package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Enregistrement représentant un point de passage
 *
 * @param point  (PointCh) : la position du point de passage dans le système de coordonnées suisse
 * @param nodeId (int) : l'identité du nœud JaVelo le plus proche de ce point de passage
 * @author Cléo Renaud (325156)
 */
public record Waypoint(PointCh point, int nodeId) { }
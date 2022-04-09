/*
 * Author : Roxanne Chevalley
 * Date : 05.04.22
 */
package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import com.sun.javafx.geom.Point2D;

/**
 * Enregistrement représentant les paramètres du fond de carte
 * présenté dans l'interface graphique.
 *
 * @param zoomLevel (int) : le niveau de zoom
 * @param x         (int) : la coordonnée x du coin haut-gauche de la portion de carte affichée
 * @param y         (int) : a coordonnée y du coin haut-gauche de la portion de carte affichée
 */
public record MapViewParameters(int zoomLevel, int x, int y) {

    /**
     * Méthode qui retourne les coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D
     *
     * @return (Point2D) : les coordonnées du coin haut-gauche
     */
    public Point2D topLeft() {
        return new Point2D(x, y);
    }

    /**
     * Méthode qui retourne une instance de MapViewParameters identique au récepteur,
     * si ce n'est que les coordonnées du coin haut-gauche sont celles passées en arguments à la méthode.
     *
     * @param x (int) : la coordonnée x du coin haut-gauche de la nouvelle instance de MapViewParameters
     * @param y (int) : la coordonnée y du coin haut-gauche de la nouvelle instance de MapViewParameters
     * @return (MapViewParameters) : une instance de MapViewParameters identique au récepteur
     * mais avec les cordonnées passées en arguments pour le coin haut-gauche.
     */
    public MapViewParameters withMinXY(int x, int y) {
        return new MapViewParameters(this.zoomLevel, x, y);
    }

    /**
     * Méthode qui prend en arguments les coordonnées x et y d'un point,
     * exprimées par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     * et retourne ce point sous la forme d'une instance de PointWebMercator
     *
     * @param x (double) : coordonnée x d'un point exprimée par raapport au coin haut-gauche de la portion de carte
     * @param y (double) : coordonnée y d'un point exprimée par raapport au coin haut-gauche de la portion de carte
     * @return (PointWebMercator) : un point avec comme coordonnées x et y
     * (exprimées par rapport au coin haut-gauche de notre portion de carte)
     */
    public PointWebMercator pointAt(double x, double y) {
        double xCor = x / 256.0;
        double yCor = y / 256.0;
        return PointWebMercator.of(zoomLevel, this.x + xCor, this.y + yCor);
    }

    /**
     * Méthode qui retourne la position x correspondant du point donné en argument
     * (exprimées par rapport au coin haut-gauche de notre portion de carte)
     *
     * @param point (PointWeMercator) : le point de référence
     * @return (double) : la position x correspondant au point donné en argument
     */
    public double viewX(PointWebMercator point) {
        return (point.xAtZoomLevel(zoomLevel) - this.x) * 256;
    }

    /**
     * Méthode qui retourne la position y correspondant du point donné en argument
     * (exprimées par rapport au coin haut-gauche de notre portion de carte)
     *
     * @param point (PointWeMercator) : le point de référence
     * @return (double) : la position y correspondant au point donné en argument
     */
    public double viewY(PointWebMercator point) {
        return (point.yAtZoomLevel(zoomLevel) - this.y) * 256;
    }
}

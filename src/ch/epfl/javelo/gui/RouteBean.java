package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

/**
 * Classe publique et finale représentant un bean JavaFX regroupant les propriétés relatives aux points de passage et à
 * l'itinéraire correspondant
 *
 * @author Cléo Renaud (325156)
 */
public final class RouteBean {

    private ObservableList<Waypoint> waypoints; // La liste (observable) des points de passage
    private ReadOnlyObjectProperty<Route> route; // L'itinéraire permettant de relier les points de passage
    private DoubleProperty highlightedPosition; // La position mise en évidence
    private ReadOnlyObjectProperty<ElevationProfile> elevationProfile; // Le profil de l'itinéraire

    /**
     * Constructeur public de la classe
     *
     * @param routeComputer (RouteComputer) : calculateur d'itinéraire pour déterminer le meilleur itinéraire reliant
     *                      deux points de passage
     */
    public RouteBean(RouteComputer routeComputer) {

    }

    /**
     * Méthode retournant la propriété représentant la position mise en évidence
     *
     * @return (DoubleProperty) : la propriété
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * Méthode retournant le contenu de la propriété de la position mise en évidence
     *
     * @return (double) : le contenu de la propriété
     */
    public double highlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * Méthode stockant la valeur passée en argument dans la propriété highlightedPosition
     *
     * @param x (double) : la valeur à stocker
     */
    public void setHighlightedPosition(double x) {
        this.highlightedPosition.set(x);

    }
}

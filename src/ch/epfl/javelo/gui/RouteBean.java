package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Classe publique et finale représentant un bean JavaFX regroupant les propriétés relatives aux points de passage et à
 * l'itinéraire correspondant
 *
 * @author Cléo Renaud (325156)
 */
public final class RouteBean {

    /**
     * Constructeur public de la classe
     *
     * @param routeComputer (RouteComputer) : calculateur d'itinéraire pour déterminer le meilleur itinéraire reliant
     *                      deux points de passage
     */
    public RouteBean(RouteComputer routeComputer) {

    }


    public ObservableList<Waypoint> waypoints; // La liste (observable) des points de passage
    public ReadOnlyObjectProperty<Route> route; // L'itinéraire permettant de relier les points de passage
    public DoubleProperty highlightedPosition; // La position mise en évidence
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile; // Le profil de l'itinéraire

    /**
     * Méthode retournant la propriété représentant la liste (observable) des points de passage
     *
     * @return (ObservableListe < Waypoint >) : la propriété
     */
    public ObservableList<Waypoint> waypointsProperty() {
        return waypoints;
    }

    /**
     * Méthode retournant la liste (observable) des points de passage
     *
     * @return (List < Waypoint >) : la liste (observable) des points de passage
     */
    public List<Waypoint> waypoints() {
        return waypoints.stream().toList();
    }

    /**
     * Méthode stockant la valeur passée en argument dans la propriété waypoints
     */
    public void setWaypoints(List<Waypoint> list) {
        waypoints.setAll(list);
    }

    /**
     * Méthode retournant la propriété représentant l'itinéraire permettant de relier les points de passage
     *
     * @return (ReadOnlyObjectProperty < Route >) : la propriété
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    /**
     * Méthode retournant l'itinéraire permettant de relier les points de passage
     *
     * @return (Route) : l'itinéraire permettant de relier les points de passage
     */
    public Route route() {
        return route.get();
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
     * Méthode retournant la position mise en évidence
     *
     * @return (double) : la position mise en évidence
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

    /**
     * Méthode retournant la propriété représentant le profil de l'itinéraire
     *
     * @return (ReadOnlyObjectProperty < ElevationProfile >) : la propriété
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }
}

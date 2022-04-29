package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Classe publique et finale représentant un bean JavaFX regroupant les propriétés relatives aux points de passage et à
 * l'itinéraire correspondant
 *
 * @author Cléo Renaud (325156)
 */
public final class RouteBean {

    public ObservableList<Waypoint> waypoints; // La liste (observable) des points de passage
    public ObjectProperty<Route> route; // L'itinéraire permettant de relier les points de passage
    public DoubleProperty highlightedPosition; // La position mise en évidence
    public ObjectProperty<ElevationProfile> elevationProfile; // Le profil de l'itinéraire

    private final RouteComputer routeComputer;
    private LinkedHashMap<List<Waypoint>, SingleRoute> cacheMemoire;


    private final static double MAX_STEP_LENGTH = 5;


    /**
     * Constructeur public de la classe
     *
     * @param routeComputer (RouteComputer) : calculateur d'itinéraire pour déterminer le meilleur itinéraire reliant
     *                      deux points de passage
     */
    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;

        // On installe un auditeur sur la liste contenant les points de passage
        //waypoints.addListener(e -> update());


        // Si aucune position ne doit être mise en évidence, la propriété contenant la position contient NaN
        highlightedPosition.set(Double.NaN);

        // On installe un auditeur sur la liste contenant les points de passage pour que l'itinéraire et son profil
        // soient recalculés à chaque changement de cette liste
        waypoints.addListener((ListChangeListener<? super Waypoint>) e -> updateRoute());
        // On calcule le meilleur itinéraire avec les points de passage actuels
        updateRoute();

        // TODO: gestion si il n y a pas d'itinéraire entre deux points de passage
    }

    /**
     * Méthode permettant de recalculer l'itinéraire et son profil à chaque fois que la liste des points de passage
     * est modifiée
     */
    private void updateRoute() {
        // Lorsque la liste des points de passage ne contient pas au moins deux éléments, alors ni l'itinéraire ni
        // son profil n'existent (les propriétés correspondantes contiennent alors null)
        if (waypoints().size() < 2) {
            route.set(null);
            elevationProfile.set(null);
            return;
        }

        // Pour éviter que le cache mémoire ne grossisse de manière incontrôlée on y stocke uniquement les itinéraires
        // simples correspondant à l'itinéraire multiple courant


        // Pour chaque paire de Waypoints se suivant on détermine le meilleur itinéraire simple les reliant
        // Les itinéraires simples sont ensuite combinés en un unique itinéraire multiple
        List<Route> segments = new ArrayList<>(); // La liste dans laquelle on stocke tous les itinéraires simples

        for (int i = 0; i <= waypoints().size(); i++) {
            // On regarde dans le cache mémoire si la route entre les deux waypoints existe déjà
            if (cacheMemoire.get(waypoints().subList(i, i + 1)) != null) {
                // Si elle existe déjà on y accède et on l'ajoute à notre itinéraire
                segments.add(cacheMemoire.get(waypoints().subList(i, i + 1)));
            } else {
                // Si ce n'est pas le cas on la crée et on l'ajoute au cache mémoire et à notre itinéraire
                segments.add(routeComputer.bestRouteBetween(waypoints().get(i).nodeId(), waypoints().get(i + 1).nodeId()));
            }
        }

        // S'il existe au moins une paire de points de passage entre lesquels aucun itinéraire ne peut être trouvé,
        // alors ni son l'itinéraire ni son profil n'existent
        if(cacheMemoire.containsValue(null)) {
            route.set(null);
            elevationProfile.set(null);
            return;
        }

        route.set(new MultiRoute(segments));
        elevationProfile.set(ElevationProfileComputer.elevationProfile(route(), MAX_STEP_LENGTH));

        // Si la position mise en évidence n'est pas valide, on la définit comme NaN
        if(!((0 <= highlightedPosition()) && (highlightedPosition() <= route().length()))) {
            highlightedPosition.set(Double.NaN);
        }
    }


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

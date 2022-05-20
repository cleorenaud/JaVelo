package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
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

    private final static int CAPACITY = 100; // capacité du cache mémoire
    private final static double MAX_STEP_LENGTH = 5; // l'espacement maximal entre les échantillons du profil

    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final RouteComputer routeComputer;
    private final LinkedHashMap<List<Waypoint>, Route> cacheMemory;

    /**
     * Constructeur public de la classe
     *
     * @param routeComputer (RouteComputer) : calculateur d'itinéraire pour déterminer le meilleur itinéraire reliant
     *                      deux points de passage
     */
    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;

        // Si aucune position ne doit être mise en évidence, la propriété contenant la position contient NaN
        this.highlightedPosition = new SimpleDoubleProperty();
        setHighlightedPosition(Double.NaN);

        // On installe un auditeur sur la liste contenant les points de passage pour que l'itinéraire et son profil
        // soient recalculés à chaque changement de cette liste
        this.waypoints = FXCollections.observableArrayList();
        waypoints.addListener((ListChangeListener<? super Waypoint>) e -> updateRoute());
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();
        this.cacheMemory = new LinkedHashMap<>(CAPACITY, 0.75f, true);

        // On calcule le meilleur itinéraire avec les points de passage actuels
        updateRoute();
    }

    /**
     * Méthode privée permettant de recalculer l'itinéraire et son profil à chaque fois que la liste des points de
     * passage est modifiée
     */
    private void updateRoute() {
        // Lorsque la liste des points de passage ne contient pas au moins deux éléments, alors ni l'itinéraire ni
        // son profil n'existent (les propriétés correspondantes contiennent alors null)
        if (waypoints().size() < 2) {
            route.set(null);
            elevationProfile.set(null);
            return;
        }

        // Pour chaque paire de Waypoints se suivant on détermine le meilleur itinéraire simple les reliant
        // Les itinéraires simples sont ensuite combinés en un unique itinéraire multiple
        List<Route> segments = new ArrayList<>();
        for (int i = 0; i < waypoints().size() - 1; i++) {
            List<Waypoint> subList = new ArrayList<>(2);
            subList.add(waypoints().get(i));
            subList.add(waypoints().get(i + 1));

            // On regarde dans le cache mémoire si la route entre les deux waypoints existe déjà
            if (cacheMemory.get(subList) != null) {
                // Si elle existe déjà on y accède et on l'ajoute à notre itinéraire
                segments.add(cacheMemory.get(subList));
            } else {
                // Si ce n'est pas le cas on la crée et on l'ajoute au cache mémoire et à notre itinéraire
                // Si deux points de passage successifs sont associés au même nœud alors on ne calcule pas l'itinéraire
                // entre ces deux points
                if (waypoints().get(i).nodeId() != waypoints().get(i + 1).nodeId()) {
                    Route bestRoute = routeComputer.bestRouteBetween(waypoints().get(i).nodeId(), waypoints().get(i + 1).nodeId());
                    segments.add(bestRoute);
                    addToCacheMemory(subList, bestRoute);
                }
            }
        }

        // S'il existe au moins une paire de points de passage entre lesquels aucun itinéraire ne peut être trouvé ou si
        // notre liste ne contient aucune route, alors ni l'itinéraire ni son profil n'existent
        if (segments.isEmpty() || segments.contains(null)) {
            route.set(null);
            elevationProfile.set(null);
        } else {
            route.set(new MultiRoute(segments));
            elevationProfile.set(ElevationProfileComputer.elevationProfile(route(), MAX_STEP_LENGTH));
        }

    }

    /**
     * Méthode privée permettant d'ajouter au cache mémoire une route entre deux points de passage
     *
     * @param waypoints (List<Waypoint>) : la liste contenant deux points de passage consécutifs
     * @param route     (Route) : la route les reliant
     */
    private void addToCacheMemory(List<Waypoint> waypoints, Route route) {
        if (cacheMemory.size() == CAPACITY) {
            cacheMemory.remove(cacheMemory.keySet().iterator().next());
        }
        cacheMemory.put(waypoints, route);
    }

    /**
     * Méthode retournant l'index du segment à la position donnée (variante de la méthode indexOfSegmentAt)
     *
     * @param position (double) : la position donnée
     * @return (int) :
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    /**
     * Méthode retournant la propriété représentant la liste (observable) des points de passage
     *
     * @return (ObservableListe < Waypoint >) : la propriété
     */
    public ObservableList<Waypoint> waypoints() {
        return waypoints;
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

    /**
     * Méthode retournant le profil de l'itinéraire
     *
     * @return (ElevationProfile) : le profil
     */
    public ElevationProfile elevationProfile() {
        return elevationProfile.get();
    }

}

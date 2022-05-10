package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe publique et finale représentant un bean JavaFX regroupant les propriétés relatives aux points de passage et à
 * l'itinéraire correspondant
 *
 * @author Cléo Renaud (325156)
 */
public final class RouteBean {

    // TODO: 09/05/2022 enlever les méthodes qui ne sont pas appelées
    // TODO: 10/05/2022 vérifier si il faut mettre en private plutôt que public

    public ObservableList<Waypoint> waypoints; // La liste (observable) des points de passage
    public ObjectProperty<Route> route; // L'itinéraire permettant de relier les points de passage
    public DoubleProperty highlightedPosition; // La position mise en évidence
    public ObjectProperty<ElevationProfile> elevationProfile; // Le profil de l'itinéraire

    private final RouteComputer routeComputer;
    private LinkedHashMap<List<Waypoint>, Route> cacheMemory;
    private final int CAPACITY = 100;

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
        //waypoints.addListener((ListChangeListener<? super Waypoint>) e -> updateRoute());
        // TODO: 09/05/2022 verifier ligne au dessus

        // Si aucune position ne doit être mise en évidence, la propriété contenant la position contient NaN
        this.highlightedPosition = new SimpleDoubleProperty();
        highlightedPosition.set(Double.NaN);

        // On installe un auditeur sur la liste contenant les points de passage pour que l'itinéraire et son profil
        // soient recalculés à chaque changement de cette liste
        this.waypoints = FXCollections.observableArrayList();
        waypoints.addListener((ListChangeListener<? super Waypoint>) e -> updateRoute());
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();
        this.cacheMemory = new LinkedHashMap<>();
        // On calcule le meilleur itinéraire avec les points de passage actuels
        updateRoute();

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

        for (int i = 0; i < waypoints().size() - 1; i++) {
            // On regarde dans le cache mémoire si la route entre les deux waypoints existe déjà
            if (cacheMemory.get(waypoints().subList(i, i + 2)) != null) {
                // Si elle existe déjà on y accède et on l'ajoute à notre itinéraire
                segments.add(cacheMemory.get(waypoints().subList(i, i + 2)));
            } else {
                // Si ce n'est pas le cas on la crée et on l'ajoute au cache mémoire et à notre itinéraire
                // Si deux points de passage successifs sont associés au même nœud alors on ne calcule pas l'itinéraire
                // entre ces deux points
                if (waypoints().get(i).nodeId() != waypoints().get(i + 1).nodeId()) {
                    Route bestRoute = routeComputer.bestRouteBetween(waypoints().get(i).nodeId(), waypoints().get(i + 1).nodeId());
                    segments.add(bestRoute);
                    addToCacheMemory(waypoints().subList(i, i + 2), bestRoute);
                }
            }
        }

        // S'il existe au moins une paire de points de passage entre lesquels aucun itinéraire ne peut être trouvé,
        // alors ni son l'itinéraire ni son profil n'existent
        if (cacheMemory.containsValue(null)) {
            route.set(null);
            elevationProfile.set(null);
            return;
        }

        if(segments.isEmpty()){
            System.out.println("c'est nul");
            route.set(null);
            elevationProfile.set(null);
        }else{
            route.set(new MultiRoute(segments));
            elevationProfile.set(ElevationProfileComputer.elevationProfile(route(), MAX_STEP_LENGTH));
        }


    }

    private void addToCacheMemory(List<Waypoint> waypoints, Route route) {
        List<Waypoint> key = null;
        if (cacheMemory.size() == CAPACITY) {
            for (Map.Entry<List<Waypoint>, Route> entry : cacheMemory.entrySet()) {
                key = entry.getKey();
                continue;
            }
            cacheMemory.remove(key);
        }
        cacheMemory.put(waypoints, route);
    }

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

    /**
     * Méthode retournant le profil de l'itinéraire
     *
     * @return (ElevationProfile) : le profil
     */
    public ElevationProfile elevationProfile() {
        return elevationProfile.get();
    }
}

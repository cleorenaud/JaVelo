package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.awt.geom.Point2D;
import java.util.function.Consumer;

/**
 * Classe publique et instantiable qui gère l'affichage de la carte "annotée"
 */
public final class AnnotatedMapManager {

    private final Graph graph; // Le graphe du réseau routier
    private final TileManager tileManager; // Le gestionnaire de tuiles OpenStreetMap
    private final RouteBean routeBean; // Le bean de l'itinéraire
    private final Consumer<String> errorConsumer; // Un "consommateur d'erreurs" permettant de signaler une erreur

    private final StackPane annotatedMap; // Le panneau contenant la carte annotée
    // private final ObjectProperty<Point2D> mousePositionOnRoute; // Propriété contenant la position du pointeur de la souris le long de l'itinéraire
    // TODO: 09/05/2022 déterminer quel type de property utiliser pour la souris et uniformiser avec les autres classes

    /**
     * Constructeur public de la classe
     */
    //public AnnotatedMapManager(Graph graph,) {

    //}
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.tileManager = tileManager;
        this.routeBean = routeBean;
        this.errorConsumer = errorConsumer;


        ObjectProperty<MapViewParameters> mapViewParametersProperty = new SimpleObjectProperty<>();
        //mapViewParametersProperty.addListener();
        ObservableList<Waypoint> waypoints = routeBean.waypointsProperty();
        WaypointsManager waypointsManager = new WaypointsManager(graph, mapViewParametersProperty, waypoints, errorConsumer);
        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersProperty);
        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersProperty, errorConsumer);

        // Empilement des panneaux contenant le fond de carte, l'itinéraire et les points de passage
        annotatedMap = new StackPane();
        Pane baseMapPane = baseMapManager.pane(); // Panneau contenant le fond de carte
        Pane routePane = routeManager.pane(); // Panneau contenant l'itinéraire
        Pane waypointsPane = waypointsManager.pane(); // Panneau contenant les points de passage
        annotatedMap.setStyle("map.css");
    }

    /**
     * Méthode retournant le panneau contenant la carte annotée
     *
     * @return (Pane) : le panneau
     */
    public StackPane pane() {
        return annotatedMap;
    }


    /**
     * Méthode retournant la propriété contenant la position du pointeur le long de l'itinéraire
     *
     * @return (DoubleProperty) : la propriété
     */
    public ObjectProperty<Point2D> mousePositionOnRouteProperty() {
        //return mousePositionOnRoute;
        return null;
    }
}

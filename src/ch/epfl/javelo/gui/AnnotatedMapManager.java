package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * Classe publique et instantiable qui gère l'affichage de la carte "annotée"
 *
 * @author Cléo Renaud (325156)
 */
public final class AnnotatedMapManager {

    private final Graph graph; // Le graphe du réseau routier
    private final TileManager tileManager; // Le gestionnaire de tuiles OpenStreetMap
    private final RouteBean routeBean; // Le bean de l'itinéraire
    private final Consumer<String> errorConsumer; // Un "consommateur d'erreurs" permettant de signaler une erreur

    private final StackPane annotatedMap; // Le panneau contenant la carte annotée
    private final DoubleProperty mousePositionOnRoute; // Propriété contenant la position du pointeur de la souris le long de l'itinéraire
    private final ObjectProperty<Point2D> mouseActualPosition; // Propriété contenant la position actuelle de la souris
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;

    /**
     * Constructeur public de la classe
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.tileManager = tileManager;
        this.routeBean = routeBean;
        this.errorConsumer = errorConsumer;

        this.mousePositionOnRoute = new SimpleDoubleProperty();
        mouseActualPosition = new SimpleObjectProperty();

        this.mapViewParametersProperty = new SimpleObjectProperty<>();
        ObservableList<Waypoint> waypoints = routeBean.waypointsProperty();
        WaypointsManager waypointsManager = new WaypointsManager(graph, mapViewParametersProperty, waypoints, errorConsumer);
        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersProperty);
        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersProperty, errorConsumer);

        // Empilement des panneaux contenant le fond de carte, l'itinéraire et les points de passage
        annotatedMap = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        annotatedMap.setStyle("map.css");

        installHandler();
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
    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRoute;
    }

    /**
     * Méthode installant les gestionnaires d'événement
     */
    private void installHandler() {
        annotatedMap.setOnMouseMoved(e -> {

            PointWebMercator PWMMouse= mapViewParametersProperty.get().pointAt(e.getX(), e.getY());
            PointCh pointMouse  = PWMMouse.toPointCh();
            RoutePoint pointClosestToMouse = routeBean.route().pointClosestTo(pointMouse);
            PointCh closestMousePointCh = pointClosestToMouse.point();
            PointWebMercator PWMClosest = PointWebMercator.ofPointCh(closestMousePointCh);
            double deltaX = mapViewParametersProperty.get().viewX(PWMMouse)-
                    mapViewParametersProperty.get().viewX(PWMClosest);
            double deltaY = mapViewParametersProperty.get().viewY(PWMMouse)-
                    mapViewParametersProperty.get().viewY(PWMClosest);
            if (Math2.norm(deltaX, deltaY)<=15){
                mousePositionOnRoute.set(pointClosestToMouse.position());
            }else{
                mousePositionOnRoute.set(Double.NaN);
            }
        });

        annotatedMap.setOnMouseExited(e -> {
            mousePositionOnRoute.set(Double.NaN);
        });
    }

}

package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * Classe publique et instantiable qui gère l'affichage de la carte "annotée"
 *
 * @author Cléo Renaud (325156)
 */
public final class AnnotatedMapManager {

    private final RouteBean routeBean; // Le bean de l'itinéraire

    private final StackPane annotatedMap; // Le panneau contenant la carte annotée
    private final DoubleProperty mousePositionOnRoute; // Propriété contenant la position du pointeur de la souris le long de l'itinéraire
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;

    /**
     * Constructeur public de la classe
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> errorConsumer) {
        this.routeBean = routeBean;
        this.mousePositionOnRoute = new SimpleDoubleProperty();
        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);

        this.mapViewParametersProperty =  new SimpleObjectProperty<>(mapViewParameters);

        ObservableList<Waypoint> waypoints = routeBean.waypoints();
        WaypointsManager waypointsManager = new WaypointsManager(graph, mapViewParametersProperty, waypoints, errorConsumer);
        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersProperty);
        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersProperty);

        // Empilement des panneaux contenant le fond de carte, l'itinéraire et les points de passage
        annotatedMap = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        annotatedMap.getStylesheets().add("map.css");

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
            if(routeBean.route()!= null){
                PointWebMercator PWMMouse= mapViewParametersProperty.get().pointAt(e.getX(), e.getY());
                double east = Ch1903.e(PWMMouse.lon(), PWMMouse.lat());
                double north = Ch1903.n(PWMMouse.lon(), PWMMouse.lat());
                if(SwissBounds.containsEN(east,north)){
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
                }else{
                    mousePositionOnRoute.set(Double.NaN);
                }
            }else{
                mousePositionOnRoute.set(Double.NaN);
            }
        });

        annotatedMap.setOnMouseExited(e -> {
            mousePositionOnRoute.set(Double.NaN);
        });
    }

}

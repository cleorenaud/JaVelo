package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe gérant l'affichage de l'itinéraire et (une partie de) l'interaction avec lui
 *
 * @author : Roxanne Chevalley (339716)
 */
public final class RouteManager {
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;
    private final Pane pane;
    private int currentZoom;
    private final Polyline route;
    private final Circle highlightedPos;
    private List<PointCh> points;
    private static final int RADIUS = 5;

    /**
     * Constructeur public de la classe RouteManager
     *
     * @param routeBean         (RouteBean) : le bean de l'itinéraire
     * @param mapViewParameters (ReadOnlyObjectProperty<MapViewParameters>) : une propriété JavaFX,
     *                          contenant les paramètres de la carte affichée,
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters) {
        this.routeBean = routeBean;
        this.mapViewParameters = mapViewParameters;
        this.points = new ArrayList();
        this.pane = new Pane();
        currentZoom = mapViewParameters.get().zoomLevel();
        this.route = new Polyline();
        route.setId("route");
        this.highlightedPos = new Circle(RADIUS);
        highlightedPos.setId("highlight");
        pane.getChildren().add(route);
        pane.getChildren().add(highlightedPos);
        pane.setPickOnBounds(false);

        installListeners(); // Méthode s'occupant d'ajouter les auditeurs
        installHandlers(); // Méthode s'occupant d'ajouter la gestion d'événements du cercle
        recreate();
    }

    /**
     * Méthode publique permettant d'obtenir le pane qui contient le dessin de l'itinéraire
     *
     * @return (Pane) : le pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Méthode privée permettant le re-dessin de l'itinéraire
     */
    private void recreate() {
        route.getPoints().clear();

        if (routeBean.route() == null) {
            route.setVisible(false);
            highlightedPos.setVisible(false);
        } else {
            this.points = new ArrayList(routeBean.route().points());
            PointWebMercator point1 = PointWebMercator.ofPointCh(points.get(0));
            double posXInit = mapViewParameters.get().viewX(point1);
            double posYInit = mapViewParameters.get().viewY(point1);

            for (PointCh point : points) { // Crée un itinéraire commençant à (0,0) qu'on va par la suite replacer
                PointWebMercator pointWM = PointWebMercator.ofPointCh(point);
                double posX = mapViewParameters.get().viewX(pointWM);
                double posY = mapViewParameters.get().viewY(pointWM);
                route.getPoints().add(posX - posXInit);
                route.getPoints().add(posY - posYInit);
            }
            replace();
        }

    }

    /**
     * Méthode privée permettant de replacer l'itinéraire
     */
    private void replace() {
        if (routeBean.route() != null) {
            route.setVisible(true);
        } else {
            return; // Inutile de chercher la position si on ne doit pas voir la route
        }
        PointWebMercator debut = PointWebMercator.ofPointCh(points.get(0));
        route.setLayoutX(mapViewParameters.get().viewX(debut));
        route.setLayoutY(mapViewParameters.get().viewY(debut));
        replaceCircle();

    }

    /**
     * Méthode privée s'occupant d'ajouter les auditeurs
     */
    private void installListeners() {

        // Auditeur permettant de récréer le dessin de l'itinéraire lorsque la liste des points de passage change
        routeBean.waypoints().addListener((ListChangeListener<? super Waypoint>) e -> {
            recreate();
        });

        // Auditeur permettant de recréer le dessin de l'itinéraire lorsque les paramètres de la carte changent
        // ou juste le replacer si le zoom n'a pas changé
        mapViewParameters.addListener(e -> {
            int newZoom = mapViewParameters.get().zoomLevel();
            if (newZoom != currentZoom) {
                currentZoom = newZoom;
                recreate();
            } else {
                replace();
            }
        });

        // Auditeur permettant de replacer le dessin du cercle lorsque sa position change
        routeBean.highlightedPositionProperty().addListener(e -> replaceCircle());

    }

    /**
     * Méthode privée permettant d'ajouter la gestion d'événement sur le cercle qui marque une position
     */
    private void installHandlers() {

        highlightedPos.setOnMouseClicked((MouseEvent mouseEvent) -> { // Gère l'ajout d'un nouveau point de passage
            Point2D point = highlightedPos.localToParent(mouseEvent.getX(), mouseEvent.getY());
            PointCh location = mapViewParameters.get().pointAt(point.getX(), point.getY()).toPointCh();

            int node = routeBean.route().nodeClosestTo(routeBean.highlightedPosition());
            int indexSegment = routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition());
            Waypoint newWaypoint = new Waypoint(location, node);

            List<Waypoint> allWaypoints = new ArrayList<>(routeBean.waypoints());
            allWaypoints.add(indexSegment + 1, newWaypoint);
            routeBean.setWaypoints(allWaypoints);

        });
    }

    private void replaceCircle() {

        if (routeBean.route() != null && !Double.isNaN(routeBean.highlightedPosition())) {
            highlightedPos.setVisible(true);
        } else {
            highlightedPos.setVisible(false);
            return; // Inutile de chercher la position si pour l'instant elle ne doit pas être visible
        }
        double posCercle = routeBean.highlightedPosition();
        PointWebMercator pointCercle = PointWebMercator.ofPointCh(routeBean.route().pointAt(posCercle));

        highlightedPos.setLayoutX(mapViewParameters.get().viewX(pointCercle));
        highlightedPos.setLayoutY(mapViewParameters.get().viewY(pointCercle));
    }

}

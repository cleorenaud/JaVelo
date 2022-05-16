package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.SwissBounds;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Classe publique et finale qui gère l'affichage et l'interaction avec les points de passage
 *
 * @author : Roxanne Chevalley (339716)
 */
public final class WaypointsManager {

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;
    private final ObservableList<Waypoint> waypointsList;
    private final Consumer<String> errorConsumer;

    private final Pane waypointsPane;
    private final Map<Group, Waypoint> pinsToWaypoint = new HashMap<>();
    private javafx.geometry.Point2D mousePos;
    private javafx.geometry.Point2D newPlace;

    private final String SON_CONTENT1 = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final String SON_CONTENT2 = "M0-23A1 1 0 000-29 1 1 0 000-23";

    private final double SEARCH_DISTANCE = 500;

    /**
     * Constructeur public de la classe
     *
     * @param graph                     (Graph) : le graphe du réseau routier
     * @param mapViewParametersProperty (ObjectProperty) : une propriété JavaFX contenant les paramètres de la carte affichée
     * @param wayPoints                 (List<Waypoint>) : la liste de tous les points de passage
     * @param errorConsumer (Consumer<String>) : message d'erreur sous la forme d'une chaîne de caractères
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersProperty,
                            ObservableList<Waypoint> wayPoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.mapViewParametersProperty = mapViewParametersProperty;
        this.waypointsList = wayPoints;
        this.errorConsumer = errorConsumer;
        this.waypointsPane = new Pane();

        waypointsPane.setPickOnBounds(false);

        installListeners();
        recreate();
    }

    /**
     * Méthode permettant d'obtenir le pane qui contient le dessin des marqueurs
     *
     * @return (Pane) : le pane
     */
    public Pane pane() {
        return waypointsPane;
    }

    /**
     * Méthode permettant d'ajouter un nouveau point de passage au nœud du graphe qui en est le plus proche
     *
     * @param x (double) : la coordonnée x du point
     * @param y (double) : la coordonnée y du point
     */
    public void addWaypoint(double x, double y) {
        PointWebMercator pWM = mapViewParametersProperty.get().pointAt(x, y);

        if (inSwissBounds(pWM)) { // On vérifie que le point est dans les limites suisses
            PointCh pointOfXY = pWM.toPointCh();
            int closestNode = graph.nodeClosestTo(pointOfXY, SEARCH_DISTANCE);
            if (closestNode == -1) { // S'il n'y a pas de nœuds proches du point
                nodeError();
                return;
            }
            Waypoint wayPoint = new Waypoint(pointOfXY, closestNode);
            waypointsList.add(wayPoint);
        } else {
            nodeError(); // Si le point n'est pas dans les limites suisses
        }

    }
    
    /**
     * Méthode privée installant la gestion d'événement pour chaque marqueur
     *
     * @param pin (Group) : le marqueur sur lequel il faut installer la gestion d'événement
     */
    private void installHandlers(Group pin) {
        pin.setOnMousePressed((MouseEvent mouseEvent) -> {
            // On crée deux Point2D contenant la position à laquelle se trouvaient le marqueur
            // et la souris au moment où elle est pressée
            this.mousePos = new javafx.geometry.Point2D(mouseEvent.getX(), mouseEvent.getY());
        });

        //déplace le marqueur sans déplacer le wayPoint
        pin.setOnMouseDragged((MouseEvent mouseEvent) -> {
            javafx.geometry.Point2D newMousePos = new javafx.geometry.Point2D(mouseEvent.getX(), mouseEvent.getY());
            Point2D dif = newMousePos.subtract(mousePos);
            Point2D posMarqueur = new javafx.geometry.Point2D(pin.getLayoutX(), pin.getLayoutY());
            newPlace = dif.add(posMarqueur);
            pin.setLayoutX(newPlace.getX());
            pin.setLayoutY(newPlace.getY());

        });

        pin.setOnMouseReleased((MouseEvent mouseEvent) -> { //gère le déplacement et la suppression d'un marqueur
            Waypoint pointPassage = pinsToWaypoint.get(pin); //le wayPoint associé au marqueur

            if (!mouseEvent.isStillSincePress()) { //si la souris s'est déplacée on déplace le marqueur
                PointWebMercator pWM = mapViewParametersProperty.get().pointAt
                        (newPlace.getX(), newPlace.getY());

                if (inSwissBounds(pWM)) {//vérifie que le point est dans les limites suisses
                    PointCh newPCh = pWM.toPointCh();
                    int i = waypointsList.indexOf(pointPassage);
                    int node = graph.nodeClosestTo(newPCh, SEARCH_DISTANCE);

                    if (node == -1) { // On n'a pas trouvé de nœud proche -> erreur
                        nodeError();
                        replace(); // On remet le nœud là où on l'a pris au début
                    } else { // On change le waypoint
                        Waypoint newWaypoint = new Waypoint(newPCh, node);
                        waypointsList.set(i, newWaypoint);
                        pinsToWaypoint.put(pin, newWaypoint);
                    }
                } else {
                    nodeError();
                    replace(); // On remet le nœud là où on l'a pris au début
                }

            } else { // Si on ne s'est pas déplacé
                waypointsList.remove(pinsToWaypoint.get(pin));
            }
        });

    }

    /**
     * Méthode privée recréant les marqueurs
     */
    private void recreate() {
        waypointsPane.getChildren().clear();
        pinsToWaypoint.clear();

        for (int i = 0; i < waypointsList.size(); i++) {

            // Dessin des marqueurs grâce aux SVG Paths
            SVGPath child1 = new SVGPath();
            child1.setContent(SON_CONTENT1);
            child1.getStyleClass().add("pin_outside");
            SVGPath child2 = new SVGPath();
            child2.getStyleClass().add("pin_inside");
            child2.setContent(SON_CONTENT2);
            Group pin = new Group(child1, child2);
            pin.getStyleClass().add("pin");

            // Coloriage des marqueurs
            if (i == 0) {
                pin.getStyleClass().add("first");
            } else if (i == waypointsList.size() - 1) {
                pin.getStyleClass().add("last");
            } else {
                pin.getStyleClass().add("middle");
            }

            // Installation du gestionnaire d'événement des marqueurs et placement de ceux-ci
            pinsToWaypoint.put(pin, waypointsList.get(i));
            waypointsPane.getChildren().add(pin);
            installHandlers(pin);
        }

        replace(); // Replace les marqueurs

    }

    /**
     * Méthode privée replaçant les marqueurs
     */
    private void replace() {
        for (Node pin : waypointsPane.getChildren()) {
            PointCh pointCh = pinsToWaypoint.get(pin).point();
            PointWebMercator webMercator = PointWebMercator.ofPointCh(pointCh);

            pin.setLayoutX(mapViewParametersProperty.get().viewX(webMercator));
            pin.setLayoutY(mapViewParametersProperty.get().viewY(webMercator));
        }
    }

    /**
     * Méthode privée s'occupant d'ajouter les auditeurs nécessaires
     */
    private void installListeners() {
        waypointsList.addListener((ListChangeListener<? super Waypoint>) e -> recreate());
        mapViewParametersProperty.addListener(e -> replace());
    }

    /**
     * Méthode privée se chargeant de l'affichage d'erreur
     */
    private void nodeError() {
        errorConsumer.accept("Aucune route à proximité !");
    }

    /**
     * Méthode privée permettant de vérifier si un point est bien dans les limites suisses
     *
     * @param pWM (PointWebMercator) : le point dont on vérifie les coordonnées
     * @return (boolean) : true si le point est dans les limites et false autrement
     */
    private boolean inSwissBounds(PointWebMercator pWM) {
        double corE = Ch1903.e(pWM.lon(), pWM.lat());
        double corN = Ch1903.n(pWM.lon(), pWM.lat());
        return SwissBounds.containsEN(corE, corN);
    }

}

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
    private final ObjectProperty<MapViewParameters> mapViewParametersObjectProperty;
    private final ObservableList<Waypoint> wayPointsList;
    private final Consumer<String> errorConsumer;
    private final Pane pane;
    private final Map<Group, Waypoint> pinsToWaypoint = new HashMap<>();
    private final String SON_CONTENT1 = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final String SON_CONTENT2 = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private javafx.geometry.Point2D mousePosition;
    private javafx.geometry.Point2D newPlace;
    private final double SEARCH_DISTANCE = 500;

    /**
     * Constructeur public de la classe
     *
     * @param graph          (Graph) : le graphe du réseau routier
     * @param mapViewParametersObjectProperty (ObjectProperty) : une propriété JavaFX contenant les paramètres de la carte affichée
     * @param wayPoints (List<Waypoint>) : la liste de tous les points de passage
     *                       //@param erreurConsumer (Consumer<String>) :
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersObjectProperty,
                            ObservableList<Waypoint> wayPoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.mapViewParametersObjectProperty = mapViewParametersObjectProperty;
        this.wayPointsList=wayPoints;
        this.errorConsumer = errorConsumer;
        this.pane = new Pane();


        pane.setPickOnBounds(false);
        installListeners(); //méthode s'occupant d'ajouter les auditeurs
        recreate();

    }

    /**
     * méthode publique permettant d'obtenir le pane qui contient le dessin des marqueurs
     *
     * @return (Pane) : le pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Méthode permettant d'ajouter un nouveau point de passage au nœud du graphe qui en est le plus proche
     *
     * @param x (double) : la coordonnée x du point
     * @param y (double) : la coordonnée y du point
     */
    public void addWaypoint(double x, double y) {
        PointWebMercator pWM= mapViewParametersObjectProperty.get().pointAt(x, y);

        if (inSwissBounds(pWM)){ //vérifie que le point est dans les limites suisses
            PointCh pointOfXY = pWM.toPointCh();
            int closestNode = graph.nodeClosestTo(pointOfXY, SEARCH_DISTANCE);
            if (closestNode == -1) { //s'il n'y a pas de noeuds proches du points
                nodeError();
                return;
            }
            Waypoint wayPoint = new Waypoint(pointOfXY, closestNode);
            wayPointsList.add(wayPoint);
        }else{
            nodeError(); //si le point n'est pas dans les limites suisses
        }

    }


    /**
     * méthode privée installant la gestion d'événement pour chaque marqueur
     *
     * @param pin (Group) : le marqueur sur lequel il faut installer la gestion d'événement
     */
    private void installHandlers(Group pin) {
        // On doit installer trois gestionnaires d'événement gérant les marqueurs

        pin.setOnMousePressed((MouseEvent mouseEvent) -> {
            // On crée deux Point2D contenant la position à laquelle se trouvaient le marqueur
            // et la souris au moment où elle est pressée
            this.mousePosition = new javafx.geometry.Point2D(mouseEvent.getX(), mouseEvent.getY());
        });


        //déplace le marqueur sans déplacer le wayPoint
        pin.setOnMouseDragged((MouseEvent mouseEvent) -> {
            javafx.geometry.Point2D newMousePos = new javafx.geometry.Point2D(mouseEvent.getX(), mouseEvent.getY());
            Point2D dif = newMousePos.subtract(mousePosition);
            Point2D posMarqueur = new javafx.geometry.Point2D(pin.getLayoutX(), pin.getLayoutY());
            newPlace = dif.add(posMarqueur);
            pin.setLayoutX(newPlace.getX());
            pin.setLayoutY(newPlace.getY());

        });

        pin.setOnMouseReleased((MouseEvent mouseEvent) -> { //gère le déplacement et la suppression d'un marqueur

            Waypoint pointPassage = pinsToWaypoint.get(pin); //le wayPoint associé au marqueur

            if (!mouseEvent.isStillSincePress()) { //si la souris s'est déplacée on déplace le marqueur
                PointWebMercator pWM= mapViewParametersObjectProperty.get().pointAt
                        (newPlace.getX(), newPlace.getY());

                if(inSwissBounds(pWM)){//vérifie que le point est dans les limites suisses
                    PointCh newPCh = pWM.toPointCh();
                    int i = wayPointsList.indexOf(pointPassage);
                    int node = graph.nodeClosestTo(newPCh, SEARCH_DISTANCE);

                    if (node == -1) { //on a pas trouvé de noeud proche -> erreur
                        nodeError();
                        replace(); //on remet le noeud là où on l'a pris au début
                    } else { //on change le waypoint
                        Waypoint newWaypoint = new Waypoint(newPCh, node);
                        wayPointsList.set(i, newWaypoint);
                        pinsToWaypoint.put(pin, newWaypoint);
                    }
                }else{
                    nodeError();
                    replace(); //on remet le noeud là où on l'a pris au début
                }


            } else {//si on ne s'est pas déplacé
                wayPointsList.remove(pinsToWaypoint.get(pin));
            }

        });

    }

    /**
     * méthode privée recréant les marqueurs
     */
    private void recreate() {

        pane.getChildren().clear();
        pinsToWaypoint.clear();


        for (int i = 0; i < wayPointsList.size(); i++) {

            //dessin des marqueurs grâce aux SVG Paths
            SVGPath child1 = new SVGPath();
            child1.setContent(SON_CONTENT1);
            child1.getStyleClass().add("pin_outside");
            SVGPath child2 = new SVGPath();
            child2.getStyleClass().add("pin_inside");
            child2.setContent(SON_CONTENT2);
            Group pin = new Group(child1, child2);
            pin.getStyleClass().add("pin");

            //coloriage des marqueurs
            if (i == 0) {
                pin.getStyleClass().add("first");
            } else if (i == wayPointsList.size() - 1) {
                pin.getStyleClass().add("last");
            } else {
                pin.getStyleClass().add("middle");
            }


            //installation du gestionnaire d'événement des marqueurs et placement de ceux ci
            pinsToWaypoint.put(pin, wayPointsList.get(i));
            pane.getChildren().add(pin);
            installHandlers(pin);
        }


        replace(); //replace les marqueurs

    }

    /**
     * méthode privée replaçant les marqueurs
     */
    private void replace() {
        for (Node pin : pane.getChildren()) {
            PointCh pointCh = pinsToWaypoint.get(pin).point();
            PointWebMercator webMercator = PointWebMercator.ofPointCh(pointCh);

            pin.setLayoutX(mapViewParametersObjectProperty.get().viewX(webMercator));
            pin.setLayoutY(mapViewParametersObjectProperty.get().viewY(webMercator));
        }

    }

    /**
     * méthode privée s'occupant d'ajouter les auditeurs nécessaires
     */
    private void installListeners() {
        wayPointsList.addListener((ListChangeListener<? super Waypoint>) e -> recreate());
        mapViewParametersObjectProperty.addListener(e -> replace());
    }

    /**
     * méthode se chargeant de l'affichage d'erreur
     */
    private void nodeError() {
        errorConsumer.accept("Aucune route à proximité !");
    }

    private boolean inSwissBounds(PointWebMercator pWM){
        double corE= Ch1903.e(pWM.lon(),pWM.lat());
        double corN=Ch1903.n(pWM.lon(),pWM.lat());
        return SwissBounds.containsEN(corE,corN);
    }


}

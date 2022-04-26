package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Classe publique et finale qui gère l'affichage et l'interaction avec les points de passage
 */
public final class WaypointsManager {

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> objectProperty;
    private final List<Waypoint> pointDePassage;
    private final MapViewParameters mapViewParameters;
    private final Consumer<String> errorConsumer;
    private Pane carte;
    private final SVGPath fils1;
    private final SVGPath fils2;
    private Map<Group, Waypoint> marqueurs = new HashMap<>();
    private boolean addPin = true;
    private final String FILS_CONTENT1 = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final String FILS_CONTENT2 =  "M0-23A1 1 0 000-29 1 1 0 000-23";

    /**
     * Constructeur public de la classe
     *
     * @param graph          (Graph) : le graphe du réseau routier
     * @param objectProperty (ObjectProperty) : une propriété JavaFX contenant les paramètres de la carte affichée
     * @param pointDePassage (List<Waypoint>) : la liste de tous les points de passage
     *                       //@param erreurConsummer (Consumer<String>) :
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> objectProperty, List<Waypoint> pointDePassage,
                            Consumer<String> errorConsumer) {
        this.graph = graph;
        this.objectProperty = objectProperty;
        mapViewParameters = objectProperty.get();
        this.pointDePassage = new ArrayList<>(pointDePassage);
        this.errorConsumer = errorConsumer;
        this.carte = new Pane();

        SVGPath fils1 = new SVGPath();
        fils1.setContent(FILS_CONTENT1);
        fils1.getStyleClass().add("pin_outside");
        this.fils1 = fils1;
        SVGPath fils2 = new SVGPath();
        fils2.getStyleClass().add("pin_inside");
        fils2.setContent(FILS_CONTENT2);
        this.fils2= fils2;


        carte.setPickOnBounds(false);
        redraw();


    }

    public Pane pane() {
        return carte;
    }

    /**
     * Méthode permettant d'ajouter un nouveau point de passage au nœud du graphe qui en est le plus proche
     *
     * @param x (int) : la coordonnée x du point
     * @param y (int) : la coordonnée y du point
     */
    public void addWaypoint(int x, int y) {
        PointCh pointOfXY = mapViewParameters.pointAt(x, y).toPointCh();
        int closestNode = graph.nodeClosestTo(pointOfXY, 1000);
        if(closestNode==-1){
            return;
        }
        Waypoint wayPoint = new Waypoint(graph.nodePoint(closestNode), closestNode);
        Waypoint lastPoint= pointDePassage.remove(pointDePassage.size()-1);
        pointDePassage.add(wayPoint);
        pointDePassage.add(lastPoint);
        redraw();

    }

    private void installHandlers(Group marqueur) {
        // On doit installer trois gestionnaires d'événement gérant les marqueurs

        marqueur.setOnMouseClicked((MouseEvent mouseEvent) -> {
            if(mouseEvent.isStillSincePress()){
                pointDePassage.remove(marqueurs.get(marqueur));
                System.out.println("slt");
                redraw();
            }
        });


        marqueur.setOnMousePressed((MouseEvent mouseEvent) -> {
            marqueur.setLayoutY(mouseEvent.getY());
            marqueur.setLayoutX(mouseEvent.getX());

        });

        marqueur.setOnMouseDragged((MouseEvent mouseEvent) -> {
                marqueur.setLayoutY(mouseEvent.getY());
                marqueur.setLayoutX(mouseEvent.getX());

        });

        marqueur.setOnMouseReleased((MouseEvent mouseEvent) -> {
            if(!mouseEvent.isStillSincePress()){
              Waypoint pointPassage =  marqueurs.get(marqueur);
              System.out.println("hello");
              PointCh newPCh = mapViewParameters.pointAt(mouseEvent.getX(), mouseEvent.getY()).toPointCh();
              int i = pointDePassage.indexOf(pointPassage);
              pointDePassage.set(i,new Waypoint(newPCh, graph.nodeClosestTo(newPCh, 1000)));
              redraw();
            }



        });

    }

    private void redraw() {
        carte.getChildren().removeAll();
        marqueurs.clear();
        System.out.println("yes");
        System.out.println(pointDePassage.size());

        for (int i = 0; i < pointDePassage.size(); i++) {
            Group marqueur = new Group(fils1, fils2);
            marqueur.getStyleClass().add("pin");
            if(i==0){
                marqueur.getStyleClass().add("first");
            }else if (i==pointDePassage.size()-1){
                marqueur.getStyleClass().add("last");
            }else{
                marqueur.getStyleClass().add("middle");
            }

            PointCh pointCh=  pointDePassage.get(i).point();
            PointWebMercator webMercator = PointWebMercator.ofPointCh(pointCh);

            marqueur.setLayoutX(objectProperty.get().viewX(webMercator));
            marqueur.setLayoutY(objectProperty.get().viewY(webMercator));
            marqueurs.put(marqueur, pointDePassage.get(i));
            carte.getChildren().add(marqueur);

            installHandlers(marqueur);


        }
        System.out.println(carte.getChildren().stream().map(p -> p.getLayoutX()).toList());

    }

    private void installBindings() {
    }


}

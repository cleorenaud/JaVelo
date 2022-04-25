package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Classe publique et finale qui gère l'affichage et l'interaction avec les points de passage
 */
public final class WaypointsManager {

    private final Graph graph;
    private final ObjectProperty objectProperty;
    private final List<Waypoint> pointDePassage;
    private final MapViewParameters mapViewParameters;
    private final Consumer<String> errorConsumer;
    private Pane carte;

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
        this.pointDePassage = pointDePassage;
        this.errorConsumer = errorConsumer;
        this.carte = new Pane();

        SVGPath fils1 = new SVGPath();
        fils1.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        fils1.getStyleClass().add("pin_outside");
        SVGPath fils2 = new SVGPath();
        fils2.getStyleClass().add("pin_inside");
        fils2.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");

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

           marqueur.setLayoutX(mapViewParameters.viewX(webMercator));
           marqueur.setLayoutY(mapViewParameters.viewY(webMercator));
           carte.getChildren().add(marqueur);
           carte.setPickOnBounds(false);

           //List<Waypoint> pointDePassageSansI= new ArrayList<>(pointDePassage);
           //pointDePassageSansI.remove(i);
           //marqueur.setOnMouseClicked(e-> );
        }
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
        Waypoint wayPoint = new Waypoint(graph.nodePoint(closestNode), closestNode);
        pointDePassage.add(wayPoint);

    }
}

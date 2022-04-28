package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.SwissBounds;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
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
    private final ObjectProperty<MapViewParameters> objectProperty;
    private final ObservableList<Waypoint> pointDePassage;
    private final Consumer<String> errorConsumer;
    private final Pane carte;
    private final Map<Group, Waypoint> marqueurs = new HashMap<>();
    private final String FILS_CONTENT1 = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final String FILS_CONTENT2 = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private javafx.geometry.Point2D posSouris;
    private javafx.geometry.Point2D posMarqueur;
    private javafx.geometry.Point2D newPlace;
    private final double SEARCH_DISTANCE = 500;

    /**
     * Constructeur public de la classe
     *
     * @param graph          (Graph) : le graphe du réseau routier
     * @param objectProperty (ObjectProperty) : une propriété JavaFX contenant les paramètres de la carte affichée
     * @param pointDePassage (List<Waypoint>) : la liste de tous les points de passage
     *                       //@param erreurConsumer (Consumer<String>) :
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> objectProperty, ObservableList<Waypoint> pointDePassage,
                            Consumer<String> errorConsumer) {
        this.graph = graph;
        this.objectProperty = objectProperty;
        this.pointDePassage=pointDePassage;
        this.errorConsumer = errorConsumer;
        this.carte = new Pane();


        carte.setPickOnBounds(false);
        installListeners(); //méthode s'occupant d'ajouter les auditeurs
        recreate();

    }

    /**
     * méthode publique permettant d'obtenir le pane qui contient le dessin des marqueurs
     *
     * @return (Pane) : le pane
     */
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
        PointWebMercator pWM=objectProperty.get().pointAt(x, y);

        if (inSwissBounds(pWM)){ //vérifie que le point est dans les limites suisses
            PointCh pointOfXY = pWM.toPointCh();
            int closestNode = graph.nodeClosestTo(pointOfXY, SEARCH_DISTANCE);
            if (closestNode == -1) { //s'il n'y a pas de noeuds proches du points
                nodeError();
                return;
            }
            Waypoint wayPoint = new Waypoint(pointOfXY, closestNode);
            if (pointDePassage.size() < 2) {
                pointDePassage.add(wayPoint);
            } else { //s'il y a plus que deux points insérer le nouveau point au milieu
                Waypoint lastPoint = pointDePassage.remove(pointDePassage.size() - 1);
                pointDePassage.add(wayPoint);
                pointDePassage.add(lastPoint);
            }
        }else{
            nodeError(); //si le point n'est pas dans les limites suisses
        }

    }


    /**
     * méthode privée installant la gestion d'événement pour chaque marqueur
     *
     * @param marqueur (Group) : le marqueur sur lequel il faut installer la gestion d'événement
     */
    private void installHandlers(Group marqueur) {
        // On doit installer trois gestionnaires d'événement gérant les marqueurs

        marqueur.setOnMousePressed((MouseEvent mouseEvent) -> {
            // On crée deux Point2D contenant la position à laquelle se trouvaient le marqueur
            // et la souris au moment où elle est pressée
            this.posSouris = new javafx.geometry.Point2D(mouseEvent.getX(), mouseEvent.getY());
        });


        //déplace le marqueur sans déplacer le wayPoint
        marqueur.setOnMouseDragged((MouseEvent mouseEvent) -> {
            javafx.geometry.Point2D newPosSouris = new javafx.geometry.Point2D(mouseEvent.getX(), mouseEvent.getY());
            Point2D dif = newPosSouris.subtract(posSouris);
            Point2D posMarqueur = new javafx.geometry.Point2D(marqueur.getLayoutX(), marqueur.getLayoutY());
            newPlace = dif.add(posMarqueur);
            marqueur.setLayoutX(newPlace.getX());
            marqueur.setLayoutY(newPlace.getY());

        });

        marqueur.setOnMouseReleased((MouseEvent mouseEvent) -> { //gère le déplacement et la suppression d'un marqueur

            Waypoint pointPassage = marqueurs.get(marqueur); //le wayPoint associé au marqueur

            if (!mouseEvent.isStillSincePress()) { //si la souris s'est déplacée on déplace le marqueur
                PointWebMercator pWM=objectProperty.get().pointAt
                        (newPlace.getX(), newPlace.getY());

                if(inSwissBounds(pWM)){//vérifie que le point est dans les limites suisses
                    PointCh newPCh = pWM.toPointCh();
                    int i = pointDePassage.indexOf(pointPassage);
                    int node = graph.nodeClosestTo(newPCh, SEARCH_DISTANCE);

                    if (node == -1) { //on a pas trouvé de noeud proche -> erreur
                        nodeError();
                        replace(); //on remet le noeud là où on l'a pris au début
                    } else { //on change le waypoint
                        Waypoint newWaypoint = new Waypoint(newPCh, node);
                        pointDePassage.set(i, newWaypoint);
                        marqueurs.put(marqueur, newWaypoint);
                    }
                }else{
                    nodeError();
                    replace(); //on remet le noeud là où on l'a pris au début
                }


            } else {//si on ne s'est pas déplacé
                pointDePassage.remove(marqueurs.get(marqueur));
            }

        });

    }

    /**
     * méthode privée recréant les marqueurs
     */
    private void recreate() {

        carte.getChildren().clear();
        marqueurs.clear();


        for (int i = 0; i < pointDePassage.size(); i++) {

            //dessin des marqueurs grâce aux SVG Paths
            SVGPath fils1 = new SVGPath();
            fils1.setContent(FILS_CONTENT1);
            fils1.getStyleClass().add("pin_outside");
            SVGPath fils2 = new SVGPath();
            fils2.getStyleClass().add("pin_inside");
            fils2.setContent(FILS_CONTENT2);
            Group marqueur = new Group(fils1, fils2);
            marqueur.getStyleClass().add("pin");

            //coloriage des marqueurs
            if (i == 0) {
                marqueur.getStyleClass().add("first");
            } else if (i == pointDePassage.size() - 1) {
                marqueur.getStyleClass().add("last");
            } else {
                marqueur.getStyleClass().add("middle");
            }


            //installation du gestionnaire d'événement des marqueurs et placement de ceux ci
            marqueurs.put(marqueur, pointDePassage.get(i));
            carte.getChildren().add(marqueur);
            installHandlers(marqueur);
        }


        replace(); //replace les marqueurs

    }

    /**
     * méthode privée replaçant les marqueurs
     */
    private void replace() {
        for (Node marqueur : carte.getChildren()) {
            PointCh pointCh = marqueurs.get(marqueur).point();
            PointWebMercator webMercator = PointWebMercator.ofPointCh(pointCh);

            marqueur.setLayoutX(objectProperty.get().viewX(webMercator));
            marqueur.setLayoutY(objectProperty.get().viewY(webMercator));
        }

    }

    /**
     * méthode privée s'occupant d'ajouter les auditeurs nécessaires
     */
    private void installListeners() {
        pointDePassage.addListener((ListChangeListener<? super Waypoint>) e -> recreate());
        objectProperty.addListener(e -> replace());
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

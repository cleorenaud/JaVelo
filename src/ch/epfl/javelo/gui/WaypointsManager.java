package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
 * @author : Roxanne Chevalley (339716)
 */
public final class WaypointsManager {

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> objectProperty;
    private final ObservableList<Waypoint> pointDePassage;
    private final Consumer<String> errorConsumer;
    private final Pane carte;
    private final SVGPath fils1;
    private final SVGPath fils2;
    private final Map<Group, Waypoint> marqueurs = new HashMap<>();
    private final String FILS_CONTENT1 = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final String FILS_CONTENT2 =  "M0-23A1 1 0 000-29 1 1 0 000-23";
    private double initialPosX=0;
    private double initialPosY=0;
    private boolean canMove=false;
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
        this.pointDePassage = FXCollections.observableArrayList(pointDePassage);
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
        installBindings();
        recreate();

    }

    /**
     * méthode publique permettant d'obtenir le pane qui contient le dessin des marqueurs
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
        PointCh pointOfXY = objectProperty.get().pointAt(x, y).toPointCh();
        int closestNode = graph.nodeClosestTo(pointOfXY, SEARCH_DISTANCE);
        if(closestNode==-1){
            nodeError();
            return;
        }
        Waypoint wayPoint = new Waypoint(pointOfXY, closestNode);
        if(pointDePassage.size()<2){
            pointDePassage.add(wayPoint);
        }else{ //s'il y a plus que deux points insérer le nouveau point au milieu
            Waypoint lastPoint= pointDePassage.remove(pointDePassage.size()-1);
            pointDePassage.add(wayPoint);
            pointDePassage.add(lastPoint);
        }
    }


    /**
     * méthode privée installant la gestion d'événement pour chaque marqueur
     * @param marqueur (Group) : le marqueur sur lequel il faut installer la gestion d'événement
     */
    private void installHandlers(Group marqueur) {
        // On doit installer trois gestionnaires d'événement gérant les marqueurs

        /*marqueur.setOnMouseClicked((MouseEvent mouseEvent) -> {
            System.out.println("j'ai cliqué");
            if(mouseEvent.isStillSincePress()){
                pointDePassage.remove(marqueurs.get(marqueur));
                System.out.println("slt");
                //redraw();
            }
        });
         */

        //permet d'enregistrer la position intiale avant de déplacer un marqueur
        marqueur.setOnDragDetected(e->{
            setInitialPos(marqueur.getLayoutX(), marqueur.getLayoutY());
            System.out.println("pos enregistré " + initialPosX + " "  + initialPosY);
        });


        //déplace le marqueur sans déplacer le wayPoint
        marqueur.setOnMouseDragged((MouseEvent mouseEvent) -> {
            if(canMove){
                marqueur.setLayoutX(initialPosX + mouseEvent.getX());
                marqueur.setLayoutY(initialPosY + mouseEvent.getY());
            }

        });

        marqueur.setOnMouseReleased((MouseEvent mouseEvent) -> { //gère le déplacement et la suppression d'un marqueur

            Waypoint pointPassage =  marqueurs.get(marqueur); //le wayPoint associé au marqueur

            if(!mouseEvent.isStillSincePress() && canMove){ //si la souris s'est déplacée on déplace le marqueur
              canMove= false;
              PointCh newPCh = objectProperty.get().pointAt
                      (initialPosX + mouseEvent.getX(), initialPosY+ mouseEvent.getY()).toPointCh();
              int i = pointDePassage.indexOf(pointPassage);
              int node = graph.nodeClosestTo(newPCh, SEARCH_DISTANCE);

              if(node==-1){ //on a pas trouvé de noeud proche -> erreur
                  nodeError();
                  replace(); //on remet le noeud là où on l'a pris au début
              }else{
                  pointDePassage.set(i,new Waypoint(newPCh,node));
              }

            }else{//si on ne s'est pas déplacé
                pointDePassage.remove(marqueurs.get(marqueur));
            }

        });

    }

    private void recreate() {
        carte.getChildren().clear();
        marqueurs.clear();
        System.out.println("dessine");
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


            marqueurs.put(marqueur, pointDePassage.get(i));
            carte.getChildren().add(marqueur);

            installHandlers(marqueur);


        }
        replace();
        System.out.println("nombre d'enfants : " + carte.getChildren().size());
        System.out.println(carte.getChildren().stream().map(p -> p.getLayoutX()).toList());

    }

    private void replace(){
        for (Node marqueur:carte.getChildren()) {
            PointCh pointCh=  marqueurs.get(marqueur).point();
            PointWebMercator webMercator = PointWebMercator.ofPointCh(pointCh);

            marqueur.setLayoutX(objectProperty.get().viewX(webMercator));
            marqueur.setLayoutY(objectProperty.get().viewY(webMercator));
        }

    }

    private void installBindings() {
        pointDePassage.addListener((ListChangeListener<? super Waypoint>) e->recreate());
        objectProperty.addListener(e->replace());
    }

    private void nodeError(){
        errorConsumer.accept("Aucune route à proximité !");
    }

    private void setInitialPos(double x, double y) {
        this.initialPosX=x;
        this.initialPosY=y;
        canMove=true;
    }



}

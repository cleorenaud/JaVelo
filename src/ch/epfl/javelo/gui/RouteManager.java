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
import java.util.function.Consumer;

/**
 *classe gérant l'affichage de l'itinéraire et (une partie de) l'interaction avec lui
 * @author : Roxanne Chevalley (339716)
 */
public final class RouteManager {
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParameters;
    private final Consumer<String> errorConsumer;
    private final Pane pane;
    private int zoomActu;
    private final Polyline itineraire;
    private final Circle circle;
    private List<PointCh> points;
    private static final int RADIUS = 5;

    // TODO: 09/05/2022 relire classe

    /**
     * Constructeur public de la classe RouteManager
     * @param routeBean (RouteBean) : le bean de l'itinéraire
     * @param mapViewParameters (ReadOnlyObjectProperty<MapViewParameters>) : une propriété JavaFX,
     *                         contenant les paramètres de la carte affichée,
     * @param errorConsumer (Consumer<String>) : un consommateur d'erreurs permettant de signaler une erreur.
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParameters,
                        Consumer<String> errorConsumer){
        this.routeBean= routeBean;
        this.mapViewParameters= mapViewParameters;
        this.errorConsumer= errorConsumer;
        this.points = new ArrayList();
        this.pane = new Pane();
        zoomActu=mapViewParameters.get().zoomLevel();
        this.itineraire= new Polyline();
        itineraire.setId("route");
        this.circle = new Circle(RADIUS);
        circle.setId("highlight");
        pane.getChildren().add(itineraire);
        pane.getChildren().add(circle);
        pane.setPickOnBounds(false);
        installListeners(); //méthode s'occupant d'ajouter les auditeurs
        installHandlers(); //méthode s'occupant d'ajouter la gestion dévénement du cercle
        recreate();
    }

    /**
     * méthode publique permettant d'obtenir le pane qui contient le dessin de l'itinéraire
     *
     * @return (Pane) : le pane
     */
    public Pane pane(){
        return pane;
    }

    /**
     * méthode privée permettant la recréation du dessin de l'itinéraire
     */
    private void recreate(){
        itineraire.getPoints().clear();

        if(routeBean.route.get()==null){
            itineraire.setVisible(false);
            circle.setVisible(false);
        }else{
            this.points=new ArrayList(routeBean.route.get().points());
            PointWebMercator point1 = PointWebMercator.ofPointCh(points.get(0));
            double posXInit=mapViewParameters.get().viewX(point1);
            double posYInit=mapViewParameters.get().viewY(point1);


            for (PointCh point: points){//crée un itinéraire commençant à (0,0) qu'on va par la suite replacer
                PointWebMercator pointWM = PointWebMercator.ofPointCh(point);
                double posX=mapViewParameters.get().viewX(pointWM);
                double posY=mapViewParameters.get().viewY(pointWM);
                itineraire.getPoints().add(posX-posXInit);
                itineraire.getPoints().add(posY-posYInit);
            }

            replace();
        }



    }

    /**
     * méthode privée permettant de replacer l'itinéraire
     */
    private void replace(){
        if(routeBean.route.get() != null){
            itineraire.setVisible(true);
        }else{
            return; //inutile de chercher la position si on ne doit pas voir la route
        }
        PointWebMercator debut = PointWebMercator.ofPointCh(points.get(0));
        itineraire.setLayoutX(mapViewParameters.get().viewX(debut));
        itineraire.setLayoutY(mapViewParameters.get().viewY(debut));
        replaceCircle();

    }

    /**
     * méthode privée s'occupant d'ajouter les auditeurs
     */
    private void installListeners(){

        //auditeur qui permet de récréer le dessin de l'itinéraire lorsque la liste des points de passage change
        routeBean.waypoints.addListener((ListChangeListener<? super Waypoint>) e -> {
            recreate();
        });

        //auditeur qui permet de recréer le dessin de l'itinéraire lorsque les paramètres de la carte changent
        //ou juste le replacer si le zoom n'a pas changé
        mapViewParameters.addListener(e->{
            int nouvZoom=mapViewParameters.get().zoomLevel();
            if(nouvZoom!=zoomActu){
                zoomActu=nouvZoom;
                recreate();
            }else{
                replace();
            }
        });

        //auditeur qui permet de replacer le dessin du cercle lorsque sa position change
        routeBean.highlightedPosition.addListener(e->replaceCircle());

    }

    /**
     * méthode privée permettant d'ajouter la gestion d'événement sur le cercle qui marque une position
     */
    private void installHandlers(){

        circle.setOnMouseClicked((MouseEvent mouseEvent) -> { //gère l'ajout d'un point de passage
            double x= mouseEvent.getX();
            double y= mouseEvent.getY();
            Point2D point = circle.localToParent(x,y);

            PointCh location = mapViewParameters.get().pointAt(point.getX(), point.getY()).toPointCh();
            int noeud=routeBean.route.get().nodeClosestTo(routeBean.highlightedPosition());


            int indexSegment=routeBean.route.get().indexOfSegmentAt(routeBean.highlightedPosition());
            int noeudAvant = routeBean.waypoints.get(indexSegment).nodeId();
            int noeudApres= routeBean.waypoints.get(indexSegment+1).nodeId();

            if(noeud == noeudAvant || noeud == noeudApres){
                error();
            }else{
                Waypoint newWaypoint = new Waypoint(location,noeud);
                List<Waypoint> demiListe1 = routeBean.waypoints.subList(0, indexSegment + 1);
                demiListe1.add(newWaypoint);
            }

        });
    }

    private void replaceCircle(){
        if(routeBean.waypoints.size() >= 2){
            circle.setVisible(true);
        }else{
            return; //ça ne sert à rien de chercher la position si pour l'instant elle ne doit pas être visible
        }
        double posCercle = routeBean.highlightedPosition.get();
        PointWebMercator pointCercle = PointWebMercator.ofPointCh(routeBean.route.get().pointAt(posCercle));

        circle.setLayoutX(mapViewParameters.get().viewX(pointCercle));
        circle.setLayoutY(mapViewParameters.get().viewY(pointCercle));

    }

    private void error(){
        errorConsumer.accept("Un point de passage est déjà présent à cet endroit !");
    }


}

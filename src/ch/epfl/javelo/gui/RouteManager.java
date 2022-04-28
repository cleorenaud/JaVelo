package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *classe gérant l'affichage de l'itinéraire et (une partie de) l'interaction avec lui
 * @author : Roxanne Chevalley (339716)
 */
public final class RouteManager {
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> objectProperty;
    private final Consumer<String> errorConsumer;
    private final Pane carte;
    private int currentZoom;
    private final Polyline itineraire;
    private final Circle cercle;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> objectProperty,
                        Consumer<String> errorConsumer){
        this.routeBean= routeBean;
        this.objectProperty= objectProperty;
        this.errorConsumer= errorConsumer;
        this.carte = new Pane();
        carte.setPickOnBounds(false);
        currentZoom= objectProperty.get().zoomLevel();
        this.itineraire= new Polyline();
        itineraire.setId("route");
        this.cercle = new Circle(5);
        cercle.setId("highlight");
        carte.getChildren().add(itineraire);
        installListeners(); //méthode s'occupant d'ajouter les auditeurs
        recreate();
    }

    /**
     * méthode publique permettant d'obtenir le pane qui contient le dessin de l'itinéraire
     *
     * @return (Pane) : le pane
     */
    public Pane pane(){
        return carte;
    }

    private void recreate(){
        List<PointCh> points = new ArrayList(routeBean.route.get().points());
        itineraire.getPoints().clear();


        for (PointCh point: points){
            PointWebMercator webMercator = PointWebMercator.ofPointCh(point);


            itineraire.getPoints().add(PointWebMercator.ofPointCh(point).x());

        }

       replace();
    }

    private void replace(){

        itineraire.setVisible(true);
        itineraire.setLayoutX(100);
        itineraire.setLayoutY(50);

    }

    private void installListeners(){
        //auditeur qui permet de récréer le dessin de l'itinéraire lorsque la liste des points de passage change
        routeBean.waypoints.addListener((ListChangeListener<? super Waypoint>) e -> recreate());

        //auditeurs qui permet de recréer le dessin de l'itinéraire lorsque les paramètres de la carte changent
        //ou juste le replacer si le zoom n'a pas changé
        objectProperty.addListener(e->{
            int actualZoom= objectProperty.get().zoomLevel();
            if(actualZoom!=currentZoom){
                currentZoom=actualZoom;
                recreate();
            }else{
                replace();
            }
        });

    }

}

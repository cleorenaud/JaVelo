package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Transform;

/**
 * Classe publique et finale qui gère l'affichage et l'interaction avec le profil en long d'un itinéraire
 *
 * @author Roxanne Chevalley (339716)
 * @author Cléo Renaud (325156)
 */
public final class ElevationProfileManager {

    private ObjectProperty<Rectangle2D> profileRect; // Propriété contenant le rectangle englobant le dessin du profil
    private ObjectProperty<Transform> transformObjectProperty; // Propriété contenant les transformations screenToWorld et worldToScreen

    private BorderPane borderPane;
    private Pane pane;
    private VBox vBox;
    private ObjectProperty<Point2D> mousePosition;


    private Insets edgeDistances = new Insets(10, 10, 20, 40);

    /**
     * Constructeur public de la classe
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty doubleProperty) {
        //TODO : faire un appel à installHandlers et redraw

    }

    /**
     * Méthode retournant le panneau contenant le dessin du profil
     *
     * @return (Pane) : le panneau
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Méthode retournant une propriété en lecture seule contenant la position du pointeur de la souris le long
     * du profil (en mètres, arrondie à l'entier le plus proche) ou NaN si le pointeur de la souris ne se trouve pas
     * au-dessus du profil
     *
     * @return (ReadOnlyObjectProperty < Point2D >) : la propriété
     */
    public ReadOnlyObjectProperty<Point2D> mousePositionOnProfileProperty() {
        return mousePosition;
    }

    /**
     * Méthode permettant le passage de coordonnées du panneau JavaFX aux coordonnées du "monde réel"
     */
    private void screenToWorld() {
        // TODO: quel type de paramètre et quel type de retour

    }

    /**
     * Méthode permettant le passage de coordonnées du "monde réel" aux coordonnées du panneau JavaFX
     */
    private void worldToScreen() {
        // TODO: quel type de paramètre et quel type de retour
    }

    private void redraw(){
        // TODO : cette méthode devra appeler drawLines, drawProfile et writeText
    }

    private void drawLines(){

    }

    private void drawProfile(){

    }

    private void writeText(){

    }

    private void installHandlers(){

    }
}

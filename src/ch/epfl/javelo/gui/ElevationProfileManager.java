package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;

import static java.lang.Double.NaN;

/**
 * Classe publique et finale qui gère l'affichage et l'interaction avec le profil en long d'un itinéraire
 *
 * @author Roxanne Chevalley (339716)
 * @author Cléo Renaud (325156)
 */
public final class ElevationProfileManager {

    private final ObjectProperty<Rectangle2D> profileRect; // Propriété contenant le rectangle englobant le dessin du profil
    private final ObjectProperty<Transform> transformObjectProperty; // Propriété contenant les transformations screenToWorld et worldToScreen

    private final BorderPane borderPane;
    private final Pane pane;
    private final Path path;
    private final Group groupForText;
    private final Polygon polygon;
    private final Line line;
    private final VBox vBox;
    private final DoubleProperty mousePosition;
    private final Text textBottom;

    ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    ReadOnlyDoubleProperty position;


    private Insets edgeDistances = new Insets(10, 10, 20, 40);

    /**
     * Constructeur public de la classe
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty position) {
        this.position = position;
        this.elevationProfileProperty = elevationProfileProperty;
        this.mousePosition= new SimpleDoubleProperty(NaN);
        this.profileRect= new SimpleObjectProperty<>();
        this.transformObjectProperty= new SimpleObjectProperty<>();

        this.textBottom = new Text();
        this.vBox= new VBox(textBottom);
        vBox.setId("profile data");

        this.line = new Line();
        this.polygon= new Polygon();
        this.polygon.setId("profile");
        this.groupForText = new Group();
        this.path= new Path();
        this.path.setId("grid");
        this.pane= new Pane(path,groupForText,polygon,line);

        this.borderPane= new BorderPane(pane);
        borderPane.setBottom(vBox);
        borderPane.getStylesheets().add("elevation profile.ess");

        installHandlers();
        redraw();

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
    public DoubleProperty mousePositionOnProfileProperty() {
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

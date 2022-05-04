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
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
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
    private final ObjectProperty<Transform> screenToWorld; // Propriété contenant les transformations screenToWorld et worldToScreen
    private final ObjectProperty<Transform> worldToScreen;

    private final BorderPane borderPane;
    private final Pane pane;
    private final Path path;
    private final Group groupForText;
    private final Polygon polygon;
    private final Line line;
    private final VBox vBox;
    private final DoubleProperty mousePosition;
    private final Text textBottom;


    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty position;


    private double maxElevation;
    private double minElevation;
    private double length;
    private double rectWidth;
    private double rectHeight;

    private final static Insets rectInsets = new Insets(10, 10, 20, 40);
    private final static int[] POS_STEPS = {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    private final static int[] ELE_STEPS = {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

    /**
     * Constructeur public de la classe
     *
     * @param elevationProfileProperty (ReadOnlyObjectProperty<ElevationProfile>) : une propriété contenant
     *                                 le profil à afficher
     * @param position                 (ReadOnlyDoubleProperty) : une propriété contenant
     *                                 la position le long du profil à mettre en évidence
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty position) {
        this.position = position;
        this.elevationProfileProperty = elevationProfileProperty;
        this.mousePosition = new SimpleDoubleProperty(NaN);
        this.profileRect = new SimpleObjectProperty<>();
        this.screenToWorld = new SimpleObjectProperty<>();
        this.worldToScreen =  new SimpleObjectProperty<>();

        this.textBottom = new Text();
        this.vBox = new VBox(textBottom);
        vBox.setId("profile_data");

        this.line = new Line();
        this.polygon = new Polygon();
        this.polygon.setId("profile");
        this.groupForText = new Group();
        this.path = new Path();
        this.path.setId("grid");
        this.pane = new Pane(path, groupForText, polygon, line);


        this.borderPane = new BorderPane(pane);
        borderPane.setBottom(vBox);
        borderPane.getStylesheets().add("elevation_profile.css");

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
     * Méthode permettant de créer les transformations
     */
    private void setTransforms() {
        double slopeX = length / rectWidth;
        double bX = -40 * slopeX;
        double slopeY = -(maxElevation - minElevation) / rectHeight;
        double bY = 10 * slopeY + maxElevation;
        screenToWorld.set(new Affine(slopeX, 0, bX, 0, slopeY, bY));

        try {
            worldToScreen.set(screenToWorld.get().createInverse());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }

    }

    private void redraw() {
        profileRect.get() = new Rectangle2D(rectInsets.getLeft(), rectInsets.getTop(), )
        maxElevation = elevationProfileProperty.get().maxElevation();
        minElevation = elevationProfileProperty.get().minElevation();
        length = elevationProfileProperty.get().length();
        rectWidth = pane.getWidth() - rectInsets.getRight()-rectInsets.getLeft();
        rectHeight = pane.getHeight() - - rectInsets.getTop() - rectInsets.getBottom();

        setTransforms();


        // TODO : cette méthode devra appeler drawLines, drawProfile et writeText
    }

    private void drawLines() {
        path.getElements().clear(); // Nœud représentant la totalité des lignes verticale et horizontales composant la grille du profil
        
        // On itère sur POS_STEPS pour déterminer quel est l'espacement entre les lignes verticales
        double vertLinesSpacing = 0;
        for (int i : POS_STEPS) {
            // On calcule la distance entre chaque ligne horizontale pour chaque valeur de POS_STEPS dans le "monde réel"
            // puis on la convertit dans le système de coordonnées JavaFX
            Point2D vertInterval = worldToScreen.get().deltaTransform(length / i, 0);
            // Si l'intervalle entre deux lignes verticales est supérieur ou égal à 25 on a notre valeur de POS_STEPS à utiliser
            if (vertInterval.getX() >= 25) {
                break;
            }
            vertLinesSpacing = vertInterval.getX();
        }
        double numVLines = Math.floor(rectWidth / vertLinesSpacing);

        for (int i = 0; i < numVLines; i++) {
            path.getElements().add(
                    new MoveTo(rectInsets.getLeft() + (i * vertLinesSpacing), rectInsets.getTop()));
            path.getElements().add(
                    new LineTo(rectInsets.getLeft(), rectInsets.getTop() + rectHeight));
        }

        // On itère sur ELE_STEPS pour déterminer quel est l'espacement entre les lignes horizontales
        double horLinesSpacing = 0;
        for (int i : ELE_STEPS) {
            // On calcule la distance entre chaque ligne verticale pour chaque valeur de ELE_STEPS dans le "monde réel"
            // puis on la convertit dans le système de coordonnées JavaFX
            Point2D horInterval = worldToScreen.get().deltaTransform(0, (maxElevation - minElevation) / i);
            // Si l'intervalle entre deux lignes horizontales est supérieur ou égal à 50 on a notre valeur de ELE_STEPS à utiliser
            if (horInterval.getY() >= 50) {
                break;
            }
            horLinesSpacing = horInterval.getY();
        }
        double numHLines = Math.floor(rectHeight / horLinesSpacing);

        for (int i = 0; i < numHLines; i++) {
            path.getElements().add(
                    new MoveTo(rectInsets.getLeft(), rectInsets.getTop() + rectHeight - (i * horLinesSpacing)));
            path.getElements().add(
                    new LineTo(rectInsets.getLeft() + rectWidth, rectInsets.getTop() + rectHeight));
        }

    }

    private void drawProfile() {

    }

    private void writeText() {
        double totalDescent = elevationProfileProperty.get().totalDescent();
        double totalAscent = elevationProfileProperty.get().totalAscent();
        String text = String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",
                length / 1000.0, totalAscent, totalDescent, minElevation, maxElevation
        );

        textBottom.setText(text);
    }

    private void installHandlers() {
        pane.setOnMouseMoved(e->{
            if()


        });

        pane.setOnMouseExited(e->{

        });


    }

}

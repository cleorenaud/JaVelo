package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
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
        this.mousePosition = new SimpleDoubleProperty();
        this.profileRect = new SimpleObjectProperty<>();
        this.screenToWorld = new SimpleObjectProperty<>();
        this.worldToScreen = new SimpleObjectProperty<>();

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


        this.borderPane = new BorderPane();
        borderPane.setCenter(pane);
        borderPane.setBottom(vBox);
        borderPane.getStylesheets().add("elevation_profile.css");


        redraw();
        installHandlers();
        installBindings();
        installListeners();


    }

    /**
     * Méthode retournant le panneau contenant le dessin du profil
     *
     * @return (BorderPane) : le panneau
     */
    public BorderPane pane() {
        return borderPane;
    }

    /**
     * Méthode retournant une propriété en lecture seule contenant la position du pointeur de la souris le long
     * du profil (en mètres, arrondie à l'entier le plus proche) ou NaN si le pointeur de la souris ne se trouve pas
     * au-dessus du profil
     *
     * @return (ReadOnlyObjectProperty < Point2D >) : la propriété
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePosition;
    }



    private void redraw() {
        maxElevation = elevationProfileProperty.get().maxElevation();
        minElevation = elevationProfileProperty.get().minElevation();
        length = elevationProfileProperty.get().length();
        rectWidth = pane.getWidth() - rectInsets.getRight() - rectInsets.getLeft();
        rectHeight = pane.getHeight() - -rectInsets.getTop() - rectInsets.getBottom();

        setTransforms();
        setProfileRect();
        //drawLines();
        drawProfile();
        writeText();

    }

    /**
     * Méthode permettant de créer les transformations
     */
    private void setTransforms() {
        Affine STW = new Affine();
        double slopeX = length / rectWidth;
        double slopeY = -(maxElevation - minElevation) / rectHeight;
        STW.prependTranslation(-rectInsets.getLeft(),-rectHeight-rectInsets.getTop());
        STW.prependScale(slopeX,slopeY);
        STW.prependTranslation(0,minElevation);

        screenToWorld.set(STW);

        try {
            worldToScreen.set(screenToWorld.get().createInverse());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }

    }

    /**
     * Méthode permettant de créer les transformations
     */
    private void setProfileRect() {
        if(rectWidth>0 && rectHeight>0){
            profileRect.set(new Rectangle2D(rectInsets.getLeft(), rectInsets.getRight(), rectWidth, rectHeight));
        }
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

        for (int i = 0; i <= numHLines; i++) {
            path.getElements().add(
                    new MoveTo(rectInsets.getLeft(), rectInsets.getTop() + rectHeight - (i * horLinesSpacing)));
            path.getElements().add(
                    new LineTo(rectInsets.getLeft() + rectWidth, rectInsets.getTop() + rectHeight));
        }

    }

    private void drawProfile() {
        polygon.getPoints().clear();
        polygon.getPoints().add(rectInsets.getLeft());
        polygon.getPoints().add(rectHeight + rectInsets.getTop());
        for (int i = 0; i <rectWidth ; i++) {
            double worldPos = screenToWorld.get().transform(i + rectInsets.getLeft(),0).getX();
            double worldHeight = elevationProfileProperty.get().elevationAt(worldPos);
            double screenHeight = worldToScreen.get().transform(0,worldHeight).getY();
            polygon.getPoints().add((double) i + rectInsets.getLeft());
            polygon.getPoints().add(screenHeight);
        }
        polygon.getPoints().add(rectWidth + rectInsets.getLeft());
        polygon.getPoints().add(rectHeight + rectInsets.getTop());


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
        pane.setOnMouseMoved(e -> {
            if (profileRect.get().contains(e.getX(), e.getY())){
                Point2D newMousePosition = screenToWorld.get().transform(e.getX(), e.getY());
                mousePosition.set(newMousePosition.getX());
            }else{
                mousePosition.set(NaN);
            }
        });

        pane.setOnMouseExited(e -> {
            mousePosition.set(NaN);
        });


    }

    private void installListeners(){
        pane.widthProperty().addListener(e->redraw());
        pane.heightProperty().addListener(e->redraw());
        elevationProfileProperty.addListener(e->redraw());

    }

    private void installBindings(){
        line.layoutXProperty().bind
                (Bindings.createDoubleBinding(()->worldToScreen.get().transform(position.get(),0).getX(),
                        position));

        line.startYProperty().bind(Bindings.select(profileRect, "minY"));
        line.endYProperty().bind(Bindings.select(profileRect, "maxY"));
        line.visibleProperty().bind(position.greaterThanOrEqualTo(0));

    }

}

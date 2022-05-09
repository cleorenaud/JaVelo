package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
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

    private final BorderPane mainPane; // borderPane représentant la fenêtre
    private final Pane centerArea; // pane représentant la partie centrale de l'écran contenant le profil
    private final Path grid; // chemin représentant la grille
    private final Group labelsText; // groupe représentant les étiquettes de la grille
    private final Polygon graphProfile; // polygone représentant le graphe du profil
    private final Line highlightedPosLine; // ligne représentant la position mise en évidence
    private final DoubleProperty mousePosition;
    private final VBox bottomArea; // vbox représentant la partie basse de l'écran contenant les statistiques du profil
    private final Text profileStats; // texte contenant les statistiques du profil

    private final ObjectProperty<Rectangle2D> profileRect; // Propriété contenant le rectangle englobant le dessin du profil
    private final ObjectProperty<Transform> screenToWorld; // Propriété contenant la transformation screenToWorld
    private final ObjectProperty<Transform> worldToScreen; // Propriété contenant la transformation worldToScreen

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty; // Propriété contenant le profil à afficher
    private final ReadOnlyDoubleProperty position; // Propriété contenant la position le long du profil à mettre en évidence

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

        this.profileStats = new Text();
        this.bottomArea = new VBox(profileStats);
        bottomArea.setId("profile_data");

        this.highlightedPosLine = new Line();
        this.graphProfile = new Polygon();
        this.graphProfile.setId("profile");
        this.labelsText = new Group();
        this.grid = new Path();
        this.grid.setId("grid");
        this.centerArea = new Pane(grid, labelsText, graphProfile, highlightedPosLine);

        this.mainPane = new BorderPane();
        mainPane.setCenter(centerArea);
        mainPane.setBottom(bottomArea);
        mainPane.getStylesheets().add("elevation_profile.css");

        installHandlers();
        installBindings();
        installListeners();
        redraw();
    }

    /**
     * Méthode retournant le panneau contenant le dessin du profil et ses statistiques
     *
     * @return (BorderPane) : le panneau
     */
    public BorderPane pane() {
        return mainPane;
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
        rectWidth = centerArea.getWidth() - rectInsets.getRight() - rectInsets.getLeft();
        rectHeight = centerArea.getHeight() - -rectInsets.getTop() - rectInsets.getBottom();

        setTransforms();
        if (rectWidth > 0 && rectHeight > 0) {
            profileRect.set(new Rectangle2D(rectInsets.getLeft(), rectInsets.getRight(), rectWidth, rectHeight));
        }
        drawProfile();
        drawLines();
        writeText();

    }

    /**
     * Méthode permettant de créer les transformations
     */
    private void setTransforms() {
        Affine STW = new Affine();
        double slopeX = length / rectWidth;
        double slopeY = -(maxElevation - minElevation) / rectHeight;
        STW.prependTranslation(-rectInsets.getLeft(), -rectHeight - rectInsets.getTop());
        STW.prependScale(slopeX, slopeY);
        STW.prependTranslation(0, minElevation);

        screenToWorld.set(STW);

        try {
            worldToScreen.set(screenToWorld.get().createInverse());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }

    }

    /**
     * Méthode permettant le (re)dessin de la grille derrière le profil
     */
    private void drawLines() {
        // On commence par "réinitialiser" la totalité des lignes composant la grille du profil et les étiquettes
        // représentant les graduations
        grid.getElements().clear();
        labelsText.getChildren().clear();

        // Pour dessiner les lignes verticales on itère sur les valeurs de POS_STEPS pour trouver la plus petite nous
        // permettant d'avoir au moins 25 unités JavaFX entre deux lignes verticales
        double posStepsX = POS_STEPS[POS_STEPS.length - 1];
        double pixelIntX = 0;
        for (int i : POS_STEPS) {
            pixelIntX = i * rectWidth / length;
            if (pixelIntX >= 25) {
                posStepsX = i;
                break;
            }
        }
        double numVLines = Math.floor(length / posStepsX); // le nombre de lignes à dessiner (exceptée celle à l'origine)

        for (int i = 0; i <= numVLines; i++) {
            grid.getElements().add(
                    new MoveTo(rectInsets.getLeft() + (i * pixelIntX), rectInsets.getTop()));
            grid.getElements().add(
                    new LineTo(rectInsets.getLeft() + (i * pixelIntX), rectInsets.getTop() + rectHeight));
            // On crée maintenant les étiquettes correspondant aux graduations
            Text label = new Text(String.valueOf((int) (i * posStepsX / 1000)));
            label.textOriginProperty().set(VPos.TOP);
            label.setX(rectInsets.getLeft() + (i * pixelIntX) - label.prefWidth(0) / 2);
            label.setY(rectInsets.getTop() + rectHeight);
            label.setFont(Font.font("Avenir", 10));
            //label.getStyleClass().add(grid_label, horizontal);
            labelsText.getChildren().add(label);
        }


        // Pour dessiner les lignes horizontales on itère sur les valeurs de ELE_STEPS pour trouver la plus petite nous
        // permettant d'avoir au moins 50 unités JavaFX entre deux lignes horizontales
        double posStepsY = ELE_STEPS[ELE_STEPS.length - 1];
        double pixelIntY = 0;
        for (int i : ELE_STEPS) {
            pixelIntY = i * rectHeight / (maxElevation - minElevation);
            if (pixelIntY >= 50) {
                posStepsY = i;
                break;
            }
        }
        double numHLines = Math.floor((maxElevation - minElevation) / posStepsY); // le nombre de lignes à dessiner (exceptée celle à l'origine)

        for (int i = 1; i <= numHLines; i++) {
            grid.getElements().add(
                    new MoveTo(rectInsets.getLeft(), rectInsets.getTop() + rectHeight - (i * pixelIntY)));
            grid.getElements().add(
                    new LineTo(rectInsets.getLeft() + rectWidth, rectInsets.getTop() + rectHeight - (i * pixelIntY)));
            // On crée maintenant les étiquettes correspondant aux graduations
            Text label = new Text(String.valueOf((int) (i * posStepsY)));
            label.textOriginProperty().set(VPos.CENTER);
            label.setX(rectInsets.getLeft() - label.prefWidth(0) - 2);
            label.setY(rectInsets.getTop() + rectHeight - (i * pixelIntY));
            label.setFont(Font.font("Avenir", 10));
            //label.getStyleClass().add(grid_label, horizontal);
            labelsText.getChildren().add(label);
        }


    }

    private void drawProfile() {
        graphProfile.getPoints().clear();
        graphProfile.getPoints().add(rectInsets.getLeft());
        graphProfile.getPoints().add(rectHeight + rectInsets.getTop());
        for (int i = 0; i < rectWidth; i++) {
            double worldPos = screenToWorld.get().transform(i + rectInsets.getLeft(), 0).getX();
            double worldHeight = elevationProfileProperty.get().elevationAt(worldPos);
            double screenHeight = worldToScreen.get().transform(0, worldHeight).getY();
            graphProfile.getPoints().add((double) i + rectInsets.getLeft());
            graphProfile.getPoints().add(screenHeight);
        }
        graphProfile.getPoints().add(rectWidth + rectInsets.getLeft());
        graphProfile.getPoints().add(rectHeight + rectInsets.getTop());
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
        profileStats.setText(text);
    }

    private void installHandlers() {
        centerArea.setOnMouseMoved(e -> {
            if (profileRect.get().contains(e.getX(), e.getY())) {
                Point2D newMousePosition = screenToWorld.get().transform(e.getX(), e.getY());
                mousePosition.set(newMousePosition.getX());
            } else {
                mousePosition.set(NaN);
            }
        });

        centerArea.setOnMouseExited(e -> {
            mousePosition.set(NaN);
        });


    }

    private void installListeners() {
        centerArea.widthProperty().addListener(e -> redraw());
        centerArea.heightProperty().addListener(e -> redraw());
        elevationProfileProperty.addListener(e -> redraw());

    }

    private void installBindings() {
        highlightedPosLine.layoutXProperty().bind(Bindings.createDoubleBinding(() -> 
                        worldToScreen.get().transform(position.get(),0).getX(), position));
        highlightedPosLine.startYProperty().bind(Bindings.select(profileRect, "minY"));
        highlightedPosLine.endYProperty().bind(Bindings.select(profileRect, "maxY"));
        highlightedPosLine.visibleProperty().bind(position.greaterThanOrEqualTo(0));
    }

}

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

    // distances entre le rectangle contenant le profil en long de l'itinéraire et les bords de MainPane
    private final static Insets RECTANGLE_INSETS = new Insets(10, 10, 20, 40);
    // les différentes valeurs utilisables pour séparer les lignes verticales
    private final static int[] POS_STEPS = {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    // les différentes valeurs utilisables pour séparer les lignes horizontales
    private final static int[] ELE_STEPS = {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};
    private final static Font LABEL_FONT = Font.font("Avenir", 10);
    private final static int MAX_DIS_VERT_LINES = 50;
    private final static int MAX_DIS_HOR_LINES = 25;
    private final static int KM_TO_M_RATIO = 1000;

    private final BorderPane mainPane;
    private final Pane centerArea; // panneau contenant le dessin du profil et les graduations
    private final Path grid; // la grille
    private final Group labelsText; // les étiquettes de la grille
    private final Polygon graphProfile;
    private final Line highlightedPosLine;
    private final DoubleProperty mousePosition;
    private final Text profileStats; // les statistiques du profil

    private final ObjectProperty<Rectangle2D> profileRect;
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty positionProperty;

    private double maxElevation;
    private double minElevation;
    private double length;
    private double rectWidth;
    private double rectHeight;

    /**
     * Constructeur public de la classe
     *
     * @param elevationProfileProperty (ReadOnlyObjectProperty<ElevationProfile>) : une propriété contenant le profil
     *                                 à afficher
     * @param positionProperty         (ReadOnlyDoubleProperty) : une propriété contenant la position le long du profil
     *                                 à mettre en évidence
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty positionProperty) {

        this.positionProperty = positionProperty;
        this.elevationProfileProperty = elevationProfileProperty;

        this.mousePosition = new SimpleDoubleProperty();
        mousePosition.set(NaN);
        this.profileRect = new SimpleObjectProperty<>();
        this.screenToWorld = new SimpleObjectProperty<>();
        this.worldToScreen = new SimpleObjectProperty<>();

        this.profileStats = new Text();
        VBox bottomArea = new VBox(profileStats);
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

        redraw();
        installHandlers();
        installBindings();
        installListeners();
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


    /**
     * Méthode privée permettant le re-dessin de la fenêtre contenant le profil en long de l'itinéraire
     */
    private void redraw() {
        if (elevationProfileProperty.get() == null) {
            worldToScreen.set(new Affine(0, 0, 0, 0, 0, 0));
            mousePosition.set(NaN);
            return;
        }

        maxElevation = elevationProfileProperty.get().maxElevation();
        minElevation = elevationProfileProperty.get().minElevation();
        length = elevationProfileProperty.get().length();
        rectWidth = centerArea.getWidth() - RECTANGLE_INSETS.getRight() - RECTANGLE_INSETS.getLeft();
        rectHeight = centerArea.getHeight() - RECTANGLE_INSETS.getTop() - RECTANGLE_INSETS.getBottom();

        setTransforms();

        if (rectWidth > 0 && rectHeight > 0) {
            profileRect.set(new Rectangle2D(RECTANGLE_INSETS.getLeft(), RECTANGLE_INSETS.getTop(), rectWidth, rectHeight));
            drawProfile();
            drawLines();
        }
    }

    /**
     * Méthode privée permettant de créer les transformations
     */
    private void setTransforms() {
        Affine STW = new Affine();
        double slopeX = length / rectWidth;
        double slopeY = -(maxElevation - minElevation) / rectHeight;
        STW.prependTranslation(-RECTANGLE_INSETS.getLeft(), -rectHeight - RECTANGLE_INSETS.getTop());
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
     * Méthode privée permettant le re-dessin de la grille derrière le profil
     */
    private void drawLines() {
        grid.getElements().clear();
        labelsText.getChildren().clear();

        int posStepsX = getStep(POS_STEPS, length, rectWidth, MAX_DIS_VERT_LINES);
        double pixelIntX = posStepsX * rectWidth/length;
        double numVLines = Math.floor(length / posStepsX) + 1; // le nombre de lignes verticales à dessiner

        for (int i = 0; i < numVLines; i++) {
            grid.getElements().add(new MoveTo(RECTANGLE_INSETS.getLeft() + (i * pixelIntX),
                            RECTANGLE_INSETS.getTop()));
            grid.getElements().add(new LineTo(RECTANGLE_INSETS.getLeft() + (i * pixelIntX),
                            RECTANGLE_INSETS.getTop() + rectHeight));

            // On crée maintenant les étiquettes correspondant aux graduations
            labels(i, posStepsX, 0, KM_TO_M_RATIO, VPos.TOP, (i * pixelIntX),0, "horizontal");
        }

        int posStepsY = getStep(ELE_STEPS, (maxElevation-minElevation), rectHeight, MAX_DIS_HOR_LINES);
        double pixelIntY = posStepsY * rectHeight / (maxElevation - minElevation);
        // le nombre de lignes horizontales à dessiner
        double numHLines = Math.floor((maxElevation - minElevation + minElevation % posStepsY) / posStepsY);

        // On cherche l'écart entre la première ligne à dessiner et l'origine 
        int firstStepReal = (int) (minElevation % posStepsY);
        double firstStepScreen = firstStepReal * pixelIntY / posStepsY;

        for (int i = 1; i <= numHLines; i++) {
            grid.getElements().add(new MoveTo(RECTANGLE_INSETS.getLeft(),
                    RECTANGLE_INSETS.getTop() + rectHeight - (i * pixelIntY) + firstStepScreen));
            grid.getElements().add(new LineTo(RECTANGLE_INSETS.getLeft() + rectWidth,
                    RECTANGLE_INSETS.getTop() + rectHeight - (i * pixelIntY) + firstStepScreen));

            // On crée maintenant les étiquettes correspondant aux graduations
            double deltaY = - (i * pixelIntY) + firstStepScreen;
            double deltaLabel = minElevation-firstStepReal;
            labels(i, posStepsY,deltaLabel, 1, VPos.CENTER, 0, deltaY, "vertical");
        }
    }

    /**
     * Méthode privée permettant le re-dessin du profil de l'itinéraire
     */
    private void drawProfile() {
        graphProfile.getPoints().clear();
        graphProfile.getPoints().add(RECTANGLE_INSETS.getLeft());
        graphProfile.getPoints().add(rectHeight + RECTANGLE_INSETS.getTop());
        for (int i = 0; i < rectWidth; i++) {
            double worldPos = screenToWorld.get().transform(i + RECTANGLE_INSETS.getLeft(), 0).getX();
            double worldHeight = elevationProfileProperty.get().elevationAt(worldPos);
            double screenHeight = worldToScreen.get().transform(0, worldHeight).getY();
            graphProfile.getPoints().add((double) i + RECTANGLE_INSETS.getLeft());
            graphProfile.getPoints().add(screenHeight);
        }
        graphProfile.getPoints().add(rectWidth + RECTANGLE_INSETS.getLeft());
        graphProfile.getPoints().add(rectHeight + RECTANGLE_INSETS.getTop());
    }

    /**
     * Méthode privée permettant d'afficher les statistiques du profil à l'écran
     */
    private void writeText() {
        double totalDescent = elevationProfileProperty.get().totalDescent();
        double totalAscent = elevationProfileProperty.get().totalAscent();
        String text = String.format(
                "Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m",
                length / KM_TO_M_RATIO, totalAscent, totalDescent, minElevation, maxElevation);
        profileStats.setText(text);
    }

    /**
     * Méthode privée installant les gestionnaires d'événements
     */
    private void installHandlers() {
        // Gestionnaire du déplacement de souris
        centerArea.setOnMouseMoved(e -> {
            if (profileRect.get() != null && profileRect.get().contains(e.getX(), e.getY())) {
                Point2D newMousePosition = screenToWorld.get().transform(e.getX(), e.getY());
                mousePosition.set(newMousePosition.getX());
            } else {
                mousePosition.set(NaN);
            }
        });

        // Gestionnaire de souris hors du cadre
        centerArea.setOnMouseExited(e -> mousePosition.set(NaN));
    }

    /**
     * Méthode privée installant les auditeurs
     */
    private void installListeners() {
        centerArea.widthProperty().addListener(e -> redraw());
        centerArea.heightProperty().addListener(e -> redraw());
        elevationProfileProperty.addListener(e -> {
            redraw();
            if(elevationProfileProperty.isNotNull().get()){
                writeText();
            }
        });
    }

    /**
     * Méthode privée installant les liens
     */
    private void installBindings() {
        highlightedPosLine.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                worldToScreen.get().transform(positionProperty.get(), 0).getX(), positionProperty));
        highlightedPosLine.startYProperty().bind(Bindings.select(profileRect, "minY"));
        highlightedPosLine.endYProperty().bind(Bindings.select(profileRect, "maxY"));
        highlightedPosLine.visibleProperty().bind(positionProperty.greaterThanOrEqualTo(0));
    }

    private int getStep(int [] steps, double realLength, double screenLength, int max){
        for (int i : steps) {
            double pixelIntX = i * screenLength / realLength;
            if (pixelIntX >= max) {
                return i;
            }
        }
        return steps[steps.length-1];
    }

    private void labels(int i, int step, double deltaLabel, int fact, VPos pos, double deltaX, double deltaY, String orientation){
        Text label = new Text(String.valueOf((int) ((i * step + deltaLabel)/fact) ));
        label.setFont(LABEL_FONT);
        label.textOriginProperty().set(pos);
        double deltaX2 = orientation.equals("horizontal") ?
                - label.prefWidth(0) / 2 : - label.prefWidth(0) - 2;
        label.setX(RECTANGLE_INSETS.getLeft() +  deltaX + deltaX2);
        label.setY(RECTANGLE_INSETS.getTop() + rectHeight + deltaY);
        label.getStyleClass().add("grid_label");
        label.getStyleClass().add(orientation);
        labelsText.getChildren().add(label);
    }

}

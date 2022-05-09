package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

import static java.lang.Float.NaN;

/**
 * Classe publique et instantiable qui est la classe principale de l'application
 */
public final class JaVelo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);
        CostFunction costFunction = new CityBikeCF(graph);
        RouteBean routeBean = new RouteBean(new RouteComputer(graph,new CityBikeCF(graph)));
        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = (s -> errorManager.displayError(s));

        AnnotatedMapManager mapPane  = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);

        ElevationProfile profile = ElevationProfileComputer
                .elevationProfile(routeBean.route(), 5);
        ObjectProperty<ElevationProfile> profileProperty = new SimpleObjectProperty<>(profile);
        DoubleProperty highlightProperty = new SimpleDoubleProperty(NaN);
        ElevationProfileManager profileManager = new ElevationProfileManager(profileProperty, highlightProperty);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getChildrenUnmodifiable().add(mapPane.pane());

        if(routeBean.route != null){
            splitPane.getChildrenUnmodifiable().add(profileManager.pane());
            SplitPane.setResizableWithParent(profileManager.pane(), false);
        }

        StackPane mainPane = new StackPane(splitPane, errorManager.pane());
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();


    }

}

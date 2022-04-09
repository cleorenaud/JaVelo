package ch.epfl.javelo.routing;

import ch.epfl.javelo.KmlPrinter;
import ch.epfl.javelo.data.Graph;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GpxGeneratorTest {
    public static void main(String[] args) throws IOException, TransformerException {
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        long t0 = System.nanoTime();
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(2046055 , 2694240);
        ElevationProfile ele= ElevationProfileComputer.elevationProfile(r,1);
        GpxGenerator.writeGpx("Javelo.Gpx",r,ele );
    }

}
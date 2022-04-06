package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;


/**
 * Classe non instantiable représentant un générateur d'itinéraire au format GPX
 *
 * @author Cléo Renaud (325156)
 */
public final class GpxGenerator {

    /**
     * Constructeur privé pour que la classe ne soit pas instantiable
     */
    private GpxGenerator() {
    }

    /**
     * Méthode retournant le document GPX correspondant à l'itinéraire passé en paramètre
     *
     * @param route            (Route) : l'itinéraire
     * @param elevationProfile (ElevationProfile) : le profil de cet itinéraire
     * @return (Document) : le document GPX correspondant à l'itinéraire
     */
    public static Document createGpx(Route route, ElevationProfile elevationProfile) {

        Document doc = newDocument(); // voir plus bas

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild((rte));

        // On itère sur tous les points de notre route pour pouvoir ajouter leur représentation à notre document
        for (PointCh point : route.points()) {
            // On commence par créer un élément rtept représentant le point de la route sur lequel on itère
            // On lui associe ensuite deux attributs étant sa latitude et sa longitude
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lat", String.valueOf(point.lat()));
            rtept.setAttribute("lon", String.valueOf(point.lon()));
            // On définit maintenant rtept comme ayant pour "parent" l'élément rte
            rte.appendChild(rtept);

            // On crée un élément ele représentant l'élévation correspondant au point rtept
            // On lui associe ensuite un attribut textuel contenant l'élévation
            Element ele = doc.createElement("ele");
            RoutePoint routePoint = route.pointClosestTo(point);
            double elevation = elevationProfile.elevationAt(routePoint.position());
            rtept.setTextContent(String.valueOf(elevation));
            // On définit maintenant ele comme ayant pour "parent" l'élément rtept
            rtept.appendChild(ele);
        }

        return doc; // TODO: vérifier que la méthode semble correcte
    }

    /**
     * Méthode permettant de créer un nouveau Document
     * (donnée à l'étape 7 du projet)
     *
     * @return (Document) : un nouveau Document
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }

    /**
     * Méthode permettant d'écrire le document GPX correspondant aux arguments passés en paramètre
     *
     * @param fileName         (String) : le nom du fichier dans lequel on écrit le document GPX
     * @param route            (Route) : l'itinéraire passé en argument
     * @param elevationProfile (ElevationProfile) : le profil de cet itinéraire
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile elevationProfile) {
        // TODO: voir si le type String est le bon pour le nom du fichier

        /*
        Document doc = createGpx(route, elevationProfile);
        Writer w = new Writer();

        Transformer transformer = newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc),
                new StreamResult(w));

         */
    }

    /**
     * Méthode permettant de créer un nouveau Transformer
     * (inspirée de la méthode newDocument() donnée à l'étape 7 du projet)
     *
     * @return (Transformer) : un nouveau Transformer
     */
    private static Transformer newTransformer() {
        try {
            return TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }


}

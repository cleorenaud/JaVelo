package ch.epfl.javelo.routing;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

        Element rtept = doc.createElement("rtept");
        rte.appendChild(rtept);

        return doc; // TODO: compléter la méthode
    }

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
     * @param fileName         (String)
     * @param route            (Route) : l'itinéraire passé en argument
     * @param elevationProfile (ElevationProfile) : le profil de cet itinéraire
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile elevationProfile) {
        // TODO: voir si le type String est le bon pour le nom du fichier
    }


}

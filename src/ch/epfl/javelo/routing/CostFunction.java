package ch.epfl.javelo.routing;

public interface CostFunction {

    /**
     * Méthode retournant le facteur par lequel la longueur de l'arête d'identité edgeId, partant du nœud d'identité
     * nodeId, doit être multipliée; ce facteur doit impérativement être supérieur ou égal à 1
     * @param nodeId (int) l'identité du nœud
     * @param edgeId (int) l'identité de l'arête
     * @return (double) la facteur par lequel la longueur de l'arête doit être multipliée
     */
    double costFactor(int nodeId, int edgeId);
}

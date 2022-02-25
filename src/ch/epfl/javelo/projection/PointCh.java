package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * un enregistrement d'un point sur la carte suisse
 * @author Roxanne Chevalley (339716)
 */

public record PointCh(double e, double n) {
    public PointCh{ //constructeur compact
      if(!SwissBounds.containsEN(e,n)){
          throw new IllegalArgumentException(); //lance une exception si le point n'est pas dans le territoire Suisse
      }
    }

    /**
     * retourne la distance au carré entre le recepteur (this) et un argument (that)
     * @param that (PointCh) : un autre point
     * @return le carrée de ladite distance (double)
     */
   public double squaredDistanceTo(PointCh that){
       return Math2.squaredNorm(that.e()-this.e(),that.n()-this.n());
    }

    /**
     * retourne la distance entre le récepteur (this) et un argument (that)
     * @param that (PointCh) : un autre point
     * @return ladite distance (double)
     */
   public double distanceTo(PointCh that){
        return Math2.norm(that.e()-this.e(),that.n()-this.n());
   }

    /**
     * retourne la longitude du point, dans le système WGS84, en radians
     * @return la longitude du point, dans le système WGS84, en radians (double)
     */
   public double lon(){
        return Ch1903.lon(e(),n());
   }

    /**
     * retourne la latitude du point, dans le système WGS84, en radians
     * @return retourne la latitude du point, dans le système WGS84, en radians (double)
     */
   public double lat(){
        return Ch1903.lat(e(),n());
   }





}

package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

public record PointCh(double e, double n) {
    public PointCh{ //constructeur compact
      if(!SwissBounds.containsEN(e,n)){
          throw new IllegalArgumentException();
      }
    }

   public double squaredDistanceTo(PointCh that){
       return Math2.squaredNorm(that.e()-this.e(),that.n()-this.n());
    }

   public double distanceTo(PointCh that){
        return Math2.norm(that.e()-this.e(),that.n()-this.n());
   }

   public double lon(){
        return Ch1903.lon(e(),n());
   }

   public double lat(){
        return Ch1903.lat(e(),n());
   }





}

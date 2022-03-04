package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphSectorsTest {
    ByteBuffer buffer1= ByteBuffer.wrap(new byte[]{0b000,
            0b000,0b000,0b001,0b000,0b001

    });
    PointCh center= new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N );
    GraphSectors graph1 = new GraphSectors(buffer1);
    GraphSectors.Sector sector1 = new GraphSectors.Sector(1,2);


    @Test
    void gSTest(){
        List<GraphSectors.Sector> list1 = new ArrayList<>();
        List<GraphSectors.Sector> list2 = graph1.sectorsInArea(center,1500);
        boolean b= sector1.equals(list2.get(0));
        assertEquals(true,b);
    }


    @Test
    void testLong(){
        byte[] tab= new byte[1700];
        for (int i = 0; i <1700 ; ++i) {
            tab[i]=0b000;
        }
        tab[128*6 + 3] =3;
        tab[128*6 + 5]=6;
        tab[4]=1;
        tab[5]=4;
        tab[2]=1;
        ByteBuffer buffer2= ByteBuffer.wrap(tab);
        PointCh center2= new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        GraphSectors graph1 = new GraphSectors(buffer2);
        GraphSectors.Sector sector2 = new GraphSectors.Sector(3,9);
        GraphSectors.Sector sector3 = new GraphSectors.Sector(256,516);
        List<GraphSectors.Sector> list2 = graph1.sectorsInArea(center2,1800);
        System.out.println(list2.size());
        boolean b=sector2.equals(list2.get(1));
        boolean c= sector3.equals(list2.get(0));
        System.out.println(list2.get(0).endNodeId());
        assertEquals(true,b);
        assertEquals(true,c);

    }

}
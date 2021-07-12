package Main;

import LBSGeo.LBSGeoAPI;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        LBSGeoAPI.Pair<ArrayList<LBSGeoAPI.Pair<Double, Double>>, LBSGeoAPI.Pair<Double, Double>> p =
                LBSGeoAPI.getCoordinates("second.json");
        System.out.println("Average:\n" + p.getSecond());
    }

}


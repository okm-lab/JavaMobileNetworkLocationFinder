package LBSGeo;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.FileReader;
import java.util.Scanner;

public class JSONLoader {
    public static JSONData.Measurements[] getInformation(String filename) throws IOException {
        Scanner in = new Scanner(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        while(in.hasNext())
            sb.append(in.next());
        in.close();
        String json = sb.toString();
        Gson g = new Gson();
        return g.fromJson(json, JSONData.Measurements[].class);
    }
}
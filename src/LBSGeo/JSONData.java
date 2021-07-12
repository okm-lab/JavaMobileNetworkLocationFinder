package LBSGeo;

import java.util.List;

public class JSONData {
    public static class Measurements {
        public double timestamp;
        public int mcc;
        public int mnc;
        public List<Cells> cells;
    }
    public static class Cells {
        public int lac;
        public int cellid;
        public int power;
    }
}

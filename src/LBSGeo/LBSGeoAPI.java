package LBSGeo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LBSGeoAPI {
    public static Pair<ArrayList<Pair<Double, Double>>, Pair<Double, Double>> getCoordinates(String filename)
            throws IOException {
        JSONData.Measurements[] measurements = JSONLoader.getInformation(filename);
        HashMap<String, HashSet<Integer>> ops = parseJSON(measurements);
        String query = generateQuery(ops);
//        System.out.println(query);
        QueryManager qm = new QueryManager();
        HashMap<Long, HashMap<String, Number>> towers = qm.sendQuery(query);
//        System.out.println("-----Towers-----");
//        for (HashMap.Entry<Long, HashMap<String, Number>> entry : towers.entrySet()) {
//            Long key = entry.getKey();
//            Object value = entry.getValue();
//            System.out.println(key + " " + value);
//        }
//        System.out.println("----------------");
        ArrayList<Pair<Double, Double>> results = new ArrayList<>();

        Long key;
        double averageX = 0, averageY = 0, cnt = 0;
        for (JSONData.Measurements m: measurements){
            cnt += 1;
            if (m.mnc == 255)
                m.mnc = 1;
            double x = 0, y = 0, reversed_squared_sum = 0;
            for (JSONData.Cells c: m.cells)
                reversed_squared_sum += 1. / (c.power * c.power);

            for (JSONData.Cells c: m.cells) {
                key = m.mnc * (long) 1e16 + m.mcc * (long) 1e13 + c.lac * (long) 1e7 + c.cellid;
                if (towers.containsKey(key)){
                    double points = c.power * c.power * reversed_squared_sum;
                    x += 1 / points * (double)towers.get(key).get("lat");
                    y += 1 / points * (double)towers.get(key).get("lon");
                } else {
                    System.out.println("Error: not found tower with code" + key + "!");
                }
            }
            averageX += x;
            averageY += y;
            results.add(new Pair<>(x, y));
        }
        averageX /= cnt;
        averageY /= cnt;
        Pair<Double, Double> average = new Pair<>(averageX, averageY);
        return new Pair<>(results, average);

//        double minDiff = 100000, xDiff, yDiff, bestx = -1, besty = -1;
//        for (HashMap<Character, Double> cds: results){
//            xDiff = cds.get('x') - 54.842572;
//            yDiff = cds.get('y') - 83.092238;
//            if (Math.sqrt(xDiff*xDiff + yDiff * yDiff) < minDiff){
//                minDiff = Math.sqrt(xDiff*xDiff + yDiff * yDiff);
//                bestx = cds.get('x');
//                besty = cds.get('y');
//            }
//
//        }
//        System.out.println("\n\n\n" + bestx + " " + besty);
    }


    private static HashMap<String, HashSet<Integer>> parseJSON(JSONData.Measurements[] measurements) {
        HashMap<String, HashSet<Integer>> ops = new HashMap<>(){{
            put("mnc", new HashSet<>());
            put("mcc", new HashSet<>());
            put("cellid", new HashSet<>());
            put("lac", new HashSet<>());
        }};
        for (JSONData.Measurements m: measurements){
            ops.get("mnc").add(m.mnc);
            ops.get("mcc").add(m.mcc);
            for (JSONData.Cells c: m.cells){
                ops.get("cellid").add(c.cellid);
                ops.get("lac").add(c.lac);
            }
        }
        return ops;
    }

    private static String generateQuery(HashMap<String, HashSet<Integer>> ops){
        StringBuilder query = new StringBuilder("SELECT cellid, lac, mcc, mnc, lat, lon from cell.towers WHERE ");
        String[] vars = {"mnc", "mcc", "lac", "cellid"};
        for (String v : vars) {
            query.append(v).append(" IN (");
            for (int op : ops.get(v)) {
                if (v.equals("mnc") && op == 255)
                    op = 1;
                query.append(op).append(",");
            }
            query.setCharAt(query.length()-1, ')');
            query.append(" AND ");
        }
        query.setLength(query.length()-5);
        return query.toString();
    }

    public static class Pair<e1, e2> {
        private final e1 first;
        private final e2 second;
        public Pair(e1 _first, e2 _second) {
            first = _first;
            second = _second;
        }
        public e1 getFirst(){
            return first;
        }
        public e2 getSecond(){
            return second;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "first=" + first +
                    ", second=" + second +
                    '}';
        }
    }

}


package dev.taway.data;

import dev.taway.tutil.data.Pair;
import dev.taway.tutil.data.Trio;
import dev.taway.tutil.logging.Logger;
import lombok.Getter;

import java.lang.reflect.Type;

@Getter
public class Map {
    private static final Logger log = new Logger();
    /**
     * 1) Path characters <br>
     * 2) Distance to end <br>
     * 3) Path
     */
    private Trio<char[][], int[][], String[][]> map;

    // Meta
    // Size rows (first) and columns (second)
    private Pair<Integer, Integer> size;
    // S location x y
    private Pair<Integer, Integer> start;
    // E location x y
    private Pair<Integer, Integer> end;

    public Map(String mapString) {
        // Split mapString by semicolon into char[][]
        String[] rows = mapString.split(";");
        char[][] charMap = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            charMap[i] = rows[i].toCharArray();
        }
        this.map = new Trio<>(charMap, null, null);

        // Save meta
        this.size = new Pair<>(charMap.length, charMap[0].length);
        this.start = findCharInMap('S');
        this.end = findCharInMap('E');

        // Distance map
        int[][] distanceMap = calculateDistanceMap();

        // init empty path map
        String[][] stringMap = new String[size.getFirst()][size.getSecond()];
        for (int i = 0; i < size.getFirst(); i++) {
            for (int j = 0; j < size.getSecond(); j++) {
                stringMap[i][j] = "+";
            }
        }

        stringMap[start.getFirst()][start.getSecond()] = "o";
        stringMap[end.getFirst()][end.getSecond()] = "X";

        char[][] simplifiedMap = simplifyMap();

        simplifiedMap[start.getFirst()][start.getSecond()] = 'S';
        simplifiedMap[end.getFirst()][end.getSecond()] = 'E';

        this.map = new Trio<>(simplifiedMap, distanceMap, stringMap);
    }

    private int[][] calculateDistanceMap() {
//        using Euclidean distance we can calculate distance from end to each point in map and save it to a new int[][] array
        int[][] distanceMap = new int[size.getFirst()][size.getSecond()];
        for (int i = 0; i < size.getFirst(); i++) {
            for (int j = 0; j < size.getSecond(); j++) {
                distanceMap[i][j] = (int)calculateEuclideanDistance(end, new Pair<>(i, j));
            }
        }
        return distanceMap;
    }

    private static double calculateEuclideanDistance(Pair<Integer, Integer> point1, Pair<Integer, Integer> point2) {
        // Calculate the square of the differences in x and y coordinates
        double squareDifferenceX = Math.pow(point1.getFirst() - point2.getFirst(), 2);
        double squareDifferenceY = Math.pow(point1.getSecond() - point2.getSecond(), 2);

        // Sum the squares
        double sumOfSquares = squareDifferenceX + squareDifferenceY;

        // Take the square root of the sum of squares to get the Euclidean distance
        return Math.sqrt(sumOfSquares);
    }

    private Pair<Integer, Integer> findCharInMap(char ch) {
        for (int i = 0; i < map.getFirst().length; i++) {
            for (int j = 0; j < map.getFirst()[0].length; j++) {
                if (map.getFirst()[i][j] == ch) {
                    return new Pair<>(i, j);
                }
            }
        }
        log.error("Character \"" + ch + "\" not found in map!");
        return new Pair<>(-1, -1);
    }

    public void printData() {
        System.out.println("MAP:");
        for (char[] dat : map.getFirst()) {
            for (int j = 0; j < dat.length; j++) {
                System.out.print(dat[j] + " ");
            }
            System.out.println();
        }

        System.out.println("DISTANCE:");
        for (int[] dat : map.getSecond()) {
            for (int j = 0; j < dat.length; j++) {
                System.out.print(dat[j] + " ");
            }
            System.out.println();
        }

        System.out.println("PATH:");
        for (String[] dat : map.getThird()) {
            for (int j = 0; j < dat.length; j++) {
                System.out.print(dat[j] + " ");
            }
            System.out.println();
        }
    }

    public char[][] simplifyMap() {
        char[][] newMap = new char[size.getFirst()][size.getSecond()];
        for (int i = 0; i < size.getFirst(); i++) {
            for (int j = 0; j < size.getFirst(); j++) {
                char[][] surr = getSurroundings(new Pair<>(i,j));
                boolean keep = false;
                for(Direction d : Direction.values()) {
                    int comp = surr[1+d.getDeltaX()][1+d.getDeltaY()];
                    int center = surr[1][1];
                    if (Math.abs(comp - center) == 1 || Math.abs(comp - center) == 0) {
                        keep = true;
                        break;
                    }
                }
                newMap[i][j] = keep ? map.getFirst()[i][j] : Character.MAX_VALUE;
            }
        }
        return newMap;
    }

    public char[][] getSurroundings(Pair<Integer, Integer> point) {
        char[][] surroundings = new char[3][3];
        for (int i = point.getFirst() - 1, x = 0; i <= point.getFirst() + 1; i++, x++) {
            for (int j = point.getSecond() - 1, y = 0; j <= point.getSecond() + 1; j++, y++) {
                if (i >= 0 && i < size.getFirst() && j >= 0 && j < size.getSecond()) {
                    surroundings[x][y] = map.getFirst()[i][j];
                } else {
                    surroundings[x][y] = Character.MAX_VALUE;
                }
            }
        }
        return surroundings;
    }
}

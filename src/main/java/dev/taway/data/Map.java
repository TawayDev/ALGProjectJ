package dev.taway.data;

import dev.taway.data.history.Path;
import dev.taway.tutil.data.Pair;
import dev.taway.tutil.data.Trio;
import dev.taway.tutil.logging.Logger;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;

@Getter
public class Map {
    private static final Logger log = new Logger();
    /**
     * 1) Path characters <br>
     * 2) Distance to end <br>
     * 3) Path
     */
    @Setter
    private Trio<char[][], int[][], Path> map;

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

        this.map = new Trio<>(charMap, distanceMap, new Path(stringMap));
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
        map.getThird().printPath();
    }

    public Trio<char[][], int[][], String[][]> getSurroundings(Pair<Integer, Integer> point) {
        char[][] surrChars = new char[3][3];
        int[][] surrDist = new int[3][3];
        String[][] surrPath = new String[3][3];

        for (int i = point.getFirst() - 1, x = 0; i <= point.getFirst() + 1; i++, x++) {
            for (int j = point.getSecond() - 1, y = 0; j <= point.getSecond() + 1; j++, y++) {
                if (i >= 0 && i < size.getFirst() && j >= 0 && j < size.getSecond()) {
                    surrChars[x][y] = map.getFirst()[i][j];
                    surrDist[x][y] = map.getSecond()[i][j];
                    surrPath[x][y] = map.getThird().getPathAtPosition(new Pair<>(i,j)).getFirst();
                } else {
                    surrChars[x][y] = Character.MAX_VALUE;
                    surrDist[x][y] = Integer.MAX_VALUE;
                    surrPath[x][y] = String.valueOf(Character.MAX_VALUE);
                }
            }
        }
        return new Trio<>(surrChars, surrDist, surrPath);
    }
}

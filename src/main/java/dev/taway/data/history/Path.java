package dev.taway.data.history;

import dev.taway.tutil.data.Pair;
import dev.taway.tutil.logging.ConsoleColor;

public class Path {
    private Pair<String, String>[][] path;

    public Path(String[][] path) {
        this.path = new Pair[path.length][path[0].length];
        for (int i = 0; i < path.length; i++) {
            for (int j = 0; j < path[i].length; j++) {
                this.path[i][j] = new Pair<>(path[i][j], "");
            }
        }
    }

    public Path(Pair<String, String>[][] path) {
        this.path = path;
    }

    /**
     * Returns the path stored in the getPath variable.
     *
     * @return the path stored in the getPath variable
     */
    public Pair<String, String>[][] getPath() {
        return path;
    }

    /**
     * Returns the path represented as a two-dimensional array of strings.
     * Each element of the array represents the first element of the corresponding Pair object in the path array.
     *
     * @return the path represented as a two-dimensional array of strings
     */
    public String[][] getPathStringMap() {
        String[][] pathStringMap = new String[path.length][path[0].length];
        for (int i = 0; i < path.length; i++) {
            for (int j = 0; j < path[i].length; j++) {
                pathStringMap[i][j] = path[i][j].getFirst();
            }
        }
        return pathStringMap;
    }

    /**
     * Sets the path at the specified position in the path grid.
     *
     * @param position the position where the path should be set
     * @param data the path data to set
     */
    public void setPathAtPosition(Pair<Integer, Integer> position, Pair<String, String> data) {
        if (position.getFirst() >= 0 && position.getFirst() < path.length && position.getSecond() >= 0 && position.getSecond() < path[0].length) {
            path[position.getFirst()][position.getSecond()] = data;
        }
    }

    /**
     * Returns the path at the given position.
     *
     * @param position the position to retrieve the path from
     * @return the path at the given position, or a special pair with default values if the position is out of bounds
     */
    public Pair<String, String> getPathAtPosition(Pair<Integer, Integer> position) {
        if (position.getFirst() >= 0 && position.getFirst() < path.length && position.getSecond() >= 0 && position.getSecond() < path[0].length) {
            return path[position.getFirst()][position.getSecond()];
        } else {
            return new Pair<>(String.valueOf(Character.MAX_VALUE), ConsoleColor.RED_BACKGROUND.COLOR);
        }
    }

    public void printPath() {
        for (int i = 0; i < path.length; i++) {
            for (int j = 0; j < path[i].length; j++) {
                System.out.print(path[i][j].getSecond() + path[i][j].getFirst() + ConsoleColor.RESET  + " ");
            }
            System.out.println();
        }
    }
}

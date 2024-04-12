package dev.taway;

import dev.taway.data.Direction;
import dev.taway.data.Map;
import dev.taway.tutil.data.Pair;
import dev.taway.tutil.data.Trio;
import dev.taway.tutil.logging.ConsoleColor;
import dev.taway.tutil.logging.Logger;

import java.util.ArrayList;
import java.util.Random;

public class Pathfinder {
    Logger log = new Logger();
    private Map map;

    public Pathfinder(Map map) {
        this.map = map;
    }

    public void findPath() {
        log.debug("Pathfinding started.");
        char[][] climbMap = map.getMap().getFirst();
        int[][] distanceMap = map.getMap().getSecond();
        String[][] pathMap = map.getMap().getThird();

        Pair<Integer, Integer> startPos = map.getStart();
        Pair<Integer, Integer> endPos = map.getEnd();
        Pair<Integer, Integer> mapSize = map.getSize();

        Pair<Integer,Integer> currentPos = new Pair<>(startPos.getFirst(), startPos.getSecond());
        boolean terminatePathfinding = false;

        int iteration = 0;
        int exitIteration = (int)Math.pow(mapSize.getFirst() * mapSize.getSecond(), 2);
//        int exitIteration = 3;
        boolean done = false;
        while (!currentPos.equals(endPos) && !terminatePathfinding && iteration != exitIteration && !done) {
            log.trace("Iteration " + (iteration+1) + "/" + exitIteration + " (max)");
            Direction bestMove = getBestMoveForPosition(currentPos);

            if (bestMove == Direction.NONE) {
                terminatePathfinding = true;
                break;
            }

            if(bestMove == Direction.DONE) {
                done = true;
                log.debug("Pathfinding done in " + (iteration+1) + " iterations.");
            }

            Pair<Integer, Integer> nextPos = new Pair<>(currentPos.getFirst() + bestMove.getDeltaX(), currentPos.getSecond() + bestMove.getDeltaY());
            log.trace("Old=" + currentPos.getFirst() + "," + currentPos.getSecond() + " New=" + nextPos.getFirst() + "," + nextPos.getSecond() + " Delta=" + bestMove.getDeltaX() + "," + bestMove.getDeltaY() + " bestMove=" + bestMove);
            pathMap[currentPos.getFirst()][currentPos.getSecond()] = bestMove.getArrow();
            currentPos = nextPos;
            iteration++;
        }

        if (terminatePathfinding) {
            log.error("Pathfinder could not find a correct move on iteration " + iteration + "/" + exitIteration);
        }

        if(iteration == exitIteration) {
            log.error("Pathfinder has run the maximum amount of iterations. To avoid infinity loop the search has been cancelled." +
                    "For map size " + mapSize.getFirst() + "x" + mapSize.getSecond() + " the max iteration count is " + exitIteration);
        }

        pathMap[startPos.getFirst()][startPos.getSecond()] = ConsoleColor.YELLOW.COLOR + "o" + ConsoleColor.RESET.COLOR;
        pathMap[endPos.getFirst()][endPos.getSecond()] = ConsoleColor.YELLOW.COLOR + "X" + ConsoleColor.RESET.COLOR;

        map.setMap(new Trio<>(climbMap, distanceMap, pathMap));
    }

    private Direction getBestMoveForPosition(Pair<Integer, Integer> currentPos) {
        Trio<char[][], int[][], String[][]> surroundings = map.getSurroundings(currentPos);
        Pair<Direction, Integer> bestMove = new Pair<>(Direction.NONE, 0);
        ArrayList<Direction> possibleMoves = new ArrayList<>();
        // treat start as 'a' otherwise it really fucks with the heat calculations
        char centerChar = surroundings.getFirst()[1][1];
        centerChar = centerChar == 'S' ? 'a' : centerChar;
        if(centerChar == 'E') return Direction.DONE;

        for (Direction dir : Direction.values()) {
            // Skip this bullshit:
            if(dir == Direction.NONE || dir == Direction.DONE) continue;
            // Get directional character:
            char dirChar = surroundings.getFirst()[1 + dir.getDeltaX()][1 + dir.getDeltaY()];
            // Return if in this direction is the end
            if(dirChar == 'E') return dir;
            // I don't know. its just how likely the algorithm should be to take that specific route. Bigger = better
            int heat = 0;
            // The difference between the one being currently compared and the middle one
            int charDiff = Math.abs((int)dirChar - (int)centerChar);
            // This works against back-tracking:
            String s = surroundings.getThird()[1 + dir.getDeltaX()][1 + dir.getDeltaY()];
            boolean wasPrevMove = s.equals(Direction.UP.getArrow()) ||  s.equals(Direction.DOWN.getArrow()) ||
                    s.equals(Direction.LEFT.getArrow()) || s.equals(Direction.RIGHT.getArrow());
            // Now that we know the move is valid aka the char increase is only 1 or 0, and we are not back-tracking we can calculate heat of move.
            if(!wasPrevMove && (charDiff == 1 || charDiff == 0)) {
//            if(charDiff == 1 || charDiff == 0) {
                // Is the possible movement closer to end ? If yes then add 1 to heat
                heat += surroundings.getSecond()[1 + dir.getDeltaX()][1 + dir.getDeltaY()] < surroundings.getSecond()[1][1] ? 1 : 0;
                // Add one if it is the same elevation as center:
                heat += dirChar == centerChar ? 1 : 0;
                // Add two if it is one higher:
                heat += dirChar == centerChar + 1 ? 2 : 0;
                // Remove one if it is one lower:
                heat -= dirChar == centerChar - 1 ? 1 : 0;
                if(heat > -1) possibleMoves.add(dir);
            }

            if (heat > bestMove.getSecond()) {
                bestMove = new Pair<>(dir, heat);
                log.trace(dir + " heat=" + heat + " centerChar='"+ centerChar + "'("+((int)centerChar)+") dirChar='" + dirChar + "'("+((int)dirChar)+") charDiff=" + charDiff + " wasPrevMove=" + wasPrevMove + " possibleMoves=" + possibleMoves.size() + " BEST!");
            } else {
                log.trace(dir + " heat=" + heat + " centerChar='"+ centerChar + "'("+((int)centerChar)+") dirChar='" + dirChar + "'("+((int)dirChar)+") charDiff=" + charDiff + " wasPrevMove=" + wasPrevMove + " possibleMoves=" + possibleMoves.size());
            }
        }

        if(bestMove.getFirst() == Direction.NONE) {
            log.warn("No best move was selected. Selecting randomly from possible moves.");
            if(possibleMoves.isEmpty()) {
                log.error("List of possible moves is empty! Unable to select move!");
            } else {
                return possibleMoves.get(new Random().nextInt(possibleMoves.size()));
            }
        }

        return bestMove.getFirst();
    }
}

package dev.taway;

import dev.taway.data.Direction;
import dev.taway.data.DirectionColor;
import dev.taway.data.Map;
import dev.taway.data.history.History;
import dev.taway.data.history.Move;
import dev.taway.data.history.Path;
import dev.taway.data.history.Step;
import dev.taway.tutil.data.Pair;
import dev.taway.tutil.data.Trio;
import dev.taway.tutil.logging.ConsoleColor;
import dev.taway.tutil.logging.Logger;

import java.util.ArrayList;
import java.util.Random;

public class Pathfinder {
    Logger log = new Logger();
    private final Map map;

    public Pathfinder(Map map) {
        this.map = map;
    }

    char[][] climbMap;
    int[][] distanceMap;
    String[][] pathMap;

    public void findPath() {
        log.debug("Pathfinding started.");
        climbMap = map.getMap().getFirst();
        distanceMap = map.getMap().getSecond();
        Path path = map.getMap().getThird();

        Pair<Integer, Integer> startPos = map.getStart();
        Pair<Integer, Integer> endPos = map.getEnd();
        Pair<Integer, Integer> mapSize = map.getSize();

        Pair<Integer,Integer> currentPos = new Pair<>(startPos.getFirst(), startPos.getSecond());
        boolean terminatePathfinding = false;

        int iteration = 0;
        int exitIteration = (int)Math.pow(mapSize.getFirst() * mapSize.getSecond(), 2);
//        int exitIteration = 3;
        boolean done = false;

        /*
          1) List of possible moves
          2) Direction
               1) The best one
               2) The one that the algorithm took
          3) Where the pointer is
         */
//        ArrayList<Trio<ArrayList<Direction>, Pair<Direction, Direction>, Pair<Integer, Integer>>> history = new ArrayList<>();
        History history = new History(path);
        while (!currentPos.equals(endPos) && !terminatePathfinding && iteration != exitIteration && !done) {
            log.trace("Iteration " + (iteration+1) + "/" + exitIteration + " (max)");
            Pair<ArrayList<Direction>, Direction> pathing = getBestMoveForPosition(currentPos);
            Direction bestMove = pathing.getSecond();
            // Save data to history:
            history.add(new Trio<>(pathing.getFirst(), new Pair<>(pathing.getSecond(), pathing.getSecond()), currentPos));
            ArrayList<Move> moves = new ArrayList<>();

            history.addStep(new Step(currentPos, ));

            Pair<Integer, Integer> nextPos = new Pair<>(currentPos.getFirst() + bestMove.getDeltaX(), currentPos.getSecond() + bestMove.getDeltaY());
            log.trace("Old=" + currentPos.getFirst() + "," + currentPos.getSecond() + " New=" + nextPos.getFirst() + "," + nextPos.getSecond() + " Delta=" + bestMove.getDeltaX() + "," + bestMove.getDeltaY() + " bestMove=" + bestMove);
            pathMap[currentPos.getFirst()][currentPos.getSecond()] = bestMove.getArrow();
            currentPos = nextPos;

            if (bestMove == Direction.NONE) {
                log.error("Pathfinder could not find a correct move on iteration " + iteration + "/" + exitIteration);
                pathMap[currentPos.getFirst()][currentPos.getSecond()] = DirectionColor.ERROR.getCOLOR() + "/" + ConsoleColor.RESET.COLOR;

//                terminatePathfinding = true;
//                break;
            }

            if(bestMove == Direction.DONE) {
                done = true;
                log.debugSuccess("Pathfinding done in " + (iteration+1) + " iterations.");
            }
            iteration++;
        }

        if(iteration == exitIteration) {
            log.error("Pathfinder has run the maximum amount of iterations. To avoid infinity loop the search has been cancelled." +
                    "For map size " + mapSize.getFirst() + "x" + mapSize.getSecond() + " the max iteration count is " + exitIteration);
            pathMap[currentPos.getFirst()][currentPos.getSecond()] = DirectionColor.ERROR.getCOLOR() + "/" + ConsoleColor.RESET.COLOR;
        }

        pathMap[startPos.getFirst()][startPos.getSecond()] = DirectionColor.OBJECTIVE.getCOLOR() + "o" + ConsoleColor.RESET.COLOR;
        pathMap[endPos.getFirst()][endPos.getSecond()] = DirectionColor.OBJECTIVE.getCOLOR() + "X" + ConsoleColor.RESET.COLOR;

        map.setMap(new Trio<>(climbMap, distanceMap, pathMap));
    }

//    /**
//     * JESUS CHRIST PLEASE FORGIVE ME FOR THIS METHOD SIGNATURE WHAT THE FUCK HAVE I DONE ????
//     * @param history
//     * @return
//     */
//    private ArrayList<Trio<ArrayList<Direction>, Pair<Direction, Direction>, Pair<Integer, Integer>>>
//    findOptimalBacktrack(ArrayList<Trio<ArrayList<Direction>, Pair<Direction, Direction>, Pair<Integer, Integer>>> history) {
//        while(!history.isEmpty() || history.getLast().getFirst().size() > 1) {
//
//            history.removeLast();
//            Trio<ArrayList<Direction>, Pair<Direction, Direction>, Pair<Integer, Integer>> newLast = history.getLast();
//
//            // The optimal move is now ignored
//            if(newLast.getFirst().size() > 1) {
//
//            } else {
//                // Otherwise Place the current movement in history as previously used:
//                this.pathMap[newLast.getThird().getFirst()][newLast.getThird().getSecond()] =
//            }
//        }
//        return history;
//    }

    private Pair<ArrayList<Pair<Direction, Integer>>, Pair<Direction,Integer>> getBestMoveForPosition(Pair<Integer, Integer> currentPos) {
        Trio<char[][], int[][], String[][]> surroundings = map.getSurroundings(currentPos);
        Pair<Direction, Integer> bestMove = new Pair<>(Direction.NONE, 0);
        ArrayList<Direction> possibleMoves = new ArrayList<>();
        // treat start as 'a' otherwise it really fucks with the heat calculations
        char centerChar = surroundings.getFirst()[1][1];
        centerChar = centerChar == 'S' ? 'a' : centerChar;
        if(centerChar == 'E') return new Pair<>(null, new Pair<>(Direction.DONE, 999));

        for (Direction dir : Direction.values()) {
            // Skip this bullshit:
            if(dir == Direction.NONE || dir == Direction.DONE) continue;
            // Get directional character:
            char dirChar = surroundings.getFirst()[1 + dir.getDeltaX()][1 + dir.getDeltaY()];
            // Return if in this direction is the end
            if(dirChar == 'E' && centerChar == 'z') return new Pair<>(null, new Pair<>(dir, 999));
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
            if(!possibleMoves.isEmpty()) {
                return new Pair<>(possibleMoves, possibleMoves.get(new Random().nextInt(possibleMoves.size())));
            } else {
                log.error("List of possible moves is empty! Unable to select move!");
            }
        }

        return new Pair<>(possibleMoves, bestMove.getFirst());
    }
}

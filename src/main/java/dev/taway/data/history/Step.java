package dev.taway.data.history;

import dev.taway.data.Direction;
import dev.taway.tutil.data.Pair;
import dev.taway.tutil.logging.Logger;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Represents multiple possible moves for one position.
 */
@Getter
public class Step {
    private static final Logger log = new Logger();
    /**
     * Current position for which this is being calculated
     */
    private Pair<Integer, Integer> position;
    /**
     * List of other possible directions, their heat and if they are valid (used = false or unused = true)
     */
    private ArrayList<Move> moves;
    private boolean depleted;

    public Step(Pair<Integer, Integer> position, ArrayList<Move> moves) {
        this.position = position;
        this.moves = moves;
        this.moves.sort(Comparator.comparingInt(Move::getHeat));
        depleted = moves.size() > 1;
    }

    /**
     * Retrieves another ideal Move based on the current position and list of available moves.
     * The method will iterate through the list of available moves and return the first move that is not marked as used.
     * If all moves have been used, the depleted flag will be set to true.
     * The method will return a new Move object with Direction.NONE, heat -1, and used flag set to false when there are no more valid moves available.
     * If the depleted flag is true, a log message will be recorded to indicate that all moves have been depleted.
     *
     * @return another ideal Move (if possible)
     */
    public Move getAnotherIdealMove() {
        if(!depleted) {
            for(Move move : moves) {
                if(!move.isUsed()) {
                    move.setUsed(true);
                    return move;
                }
            }

            depleted = true;
            log.debugFailure("Failed to find more valid moves at position " + position.getFirst() + "," + position.getSecond());
        } else {
            log.debugWarn("Depleted all moves at " + position.getFirst() + "," + position.getSecond());
        }
        return new Move(Direction.NONE, -1 , false);
    }
}

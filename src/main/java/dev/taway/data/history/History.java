package dev.taway.data.history;

import dev.taway.data.Direction;
import dev.taway.data.DirectionColor;
import dev.taway.tutil.data.Pair;
import dev.taway.tutil.logging.Logger;
import lombok.Getter;

import java.util.ArrayList;

public class History {
    private static final Logger log = new Logger();
    /**
     * A list of steps that have been taken
     */
    private ArrayList<Step> steps;
    @Getter
    private Path path;

    public History(Path path) {
        this.path = path;
    }

    /**
     * Adds a Step to the list of steps.
     *
     * @param step the Step to be added
     */
    public void addStep(Step step) {
        steps.add(step);
    }

    /**
     * Retrieves the last step that has been taken.
     *
     * @return the last step
     */
    public Step getLastStep() {
        return steps.getLast();
    }

    /**
     * Retrieves the last valid step from the list of steps. A step is considered valid if it has another ideal move.
     * This method iterates through the steps in reverse order and checks if the move associated with the step has a direction other than NONE and a heat value greater than -1.
     * If a valid step is found, it is returned. If all steps have been checked and no valid step is found, an error log is generated and the last step in the list is returned.
     *
     * @return the last valid step
     */
    public Step getLastGoodStep() {
        for (int i = steps.size() - 1; i >= 0; i--) {
            Step step = steps.get(i);
            Move move;
            do {
                 move = step.getAnotherIdealMove();
                if (move.getDirection() != Direction.NONE && move.getHeat() > -1) {
                    return step;
                }
            } while(!move.isUsed());
            // Backtrack on these moves
            for (int j = steps.size() - 1; j >= i; j--) {
                path.setPathAtPosition(step.getPosition(), new Pair<>("", DirectionColor.BACKTRACKING));
                steps.remove(j);
            }
        }
        log.error("Unable to find a step with other possible moves!");
        // FIXME: This will crash lmao!
        return steps.getLast();
    }
}

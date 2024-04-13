package dev.taway.data.history;

import dev.taway.data.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a move for a specific position.
 */
@AllArgsConstructor
@Getter
public class Move {
    private Direction direction;
    private int heat;
    @Setter
    private boolean used;
}

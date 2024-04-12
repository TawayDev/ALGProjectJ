package dev.taway.data;

import dev.taway.tutil.logging.ConsoleColor;
import lombok.Getter;

@Getter
public enum Direction {
    UP(-1, 0, ConsoleColor.YELLOW.COLOR + "↑" + ConsoleColor.RESET.COLOR),
    DOWN(1, 0, ConsoleColor.YELLOW.COLOR + "↓" + ConsoleColor.RESET.COLOR),
    LEFT(0, -1, ConsoleColor.YELLOW.COLOR + "←" + ConsoleColor.RESET.COLOR),
    RIGHT(0, 1, ConsoleColor.YELLOW.COLOR + "→" + ConsoleColor.RESET.COLOR),
    NONE(0,0, ""),
    DONE(0,0, "");

    private final int deltaX;
    private final int deltaY;
    private final String arrow;

    Direction(int deltaX, int deltaY, String arrow) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.arrow = arrow;
    }
}

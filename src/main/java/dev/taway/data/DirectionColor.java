package dev.taway.data;

import dev.taway.tutil.logging.ConsoleColor;
import lombok.Getter;

@Getter
public enum DirectionColor {
    GOING(ConsoleColor.YELLOW_BRIGHT.COLOR),
    COMPLETED(ConsoleColor.GREEN_BRIGHT.COLOR),
    BACKTRACKING(ConsoleColor.BLACK_BRIGHT.COLOR),
    ERROR(ConsoleColor.RED_BOLD.COLOR),
    OBJECTIVE(ConsoleColor.PURPLE_BRIGHT.COLOR);
    private final String COLOR;

    DirectionColor(String color) {
        COLOR = color;
    }
}

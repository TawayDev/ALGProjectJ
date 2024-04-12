package dev.taway.data;

import lombok.Getter;

@Getter
public enum Direction {
    UP(0, -1, "↑"),
    DOWN(0, 1, "↓"),
    LEFT(-1, 0, "←"),
    RIGHT(1, 0, "→");

    private final int deltaX;
    private final int deltaY;
    private final String arrow;

    Direction(int deltaX, int deltaY, String arrow) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.arrow = arrow;
    }
}

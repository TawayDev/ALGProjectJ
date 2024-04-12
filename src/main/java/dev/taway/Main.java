package dev.taway;

import dev.taway.data.Map;
import dev.taway.tutil.RuntimeConfig;
import dev.taway.tutil.logging.LogLevel;

public class Main {
    public static void main(String[] args) {
        // Requires Tutil v0.2.2 => https://github.com/TawayDev/Tutil/releases
        RuntimeConfig.LOGGING.DISABLE_LOG_FILE = true;
        RuntimeConfig.LOGGING.MINIMUM_LOG_LEVEL_CONSOLE = LogLevel.DEBUG;

        Map map = new Map("Sabqponm;abcryxxl;accszExk;acctuvwj;abdefghi;");
        Pathfinder pathfinder = new Pathfinder(map);

        pathfinder.findPath();
        map.printData();
    }
}
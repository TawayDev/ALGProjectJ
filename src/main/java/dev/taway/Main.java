package dev.taway;

import dev.taway.data.Map;
import dev.taway.tutil.RuntimeConfig;
import dev.taway.tutil.logging.ConsoleColor;
import dev.taway.tutil.logging.LogLevel;

public class Main {
    public static final String color = ConsoleColor.YELLOW.COLOR;
    public static void main(String[] args) {
        // Requires Tutil v0.2.2 => https://github.com/TawayDev/Tutil/releases
        RuntimeConfig.LOGGING.DISABLE_LOG_FILE = true;
        RuntimeConfig.LOGGING.MINIMUM_LOG_LEVEL_CONSOLE = LogLevel.DEBUG;

        // This one works:
        Map map = new Map("Sabqponm;abcryxxl;accszExk;acctuvwj;abdefghi;");
        // This one does not. Should it ? :>
//        Map map = new Map("Sabcdefghijklmnopqrstuvwxyz;abcdefghijklmnopqrsttvwxyzz;bcdefghijklmnopqrstutwxyzzz;cdefghijklmnopqrstuvtxtuzxy;defghijklmnopqrstuvwtytuzwy;efghijklmnopqrstuvwxtttuzwy;fghijklmnopqrstuvwxyzzzvvwy;ghijklmnopqrstuvwxaartaEzzz;hijklmnopqrstuvwxyteartzate;ijklmnopqrstuvwxyyyyyyyzbas;");
        Pathfinder pathfinder = new Pathfinder(map);

        pathfinder.findPath();
        map.printData();
    }
}
package dev.taway;

import dev.taway.data.Map;
import dev.taway.tutil.RuntimeConfig;

public class Main {
    public static void main(String[] args) {
        RuntimeConfig.LOGGING.DISABLE_LOG_FILE = true;
        Map map = new Map("Sabqponm;abcryxxl;accszExk;acctuvwj;abdefghi;");
        map.printData();
    }
}
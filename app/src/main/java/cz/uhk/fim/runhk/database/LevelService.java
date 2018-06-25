package cz.uhk.fim.runhk.database;

import java.util.HashMap;

public class LevelService {

    public HashMap<Integer, Integer> getLevelMap() {
        HashMap<Integer, Integer> levelMap = new HashMap();
        levelMap.put(1, 100);
        levelMap.put(2, 200);
        levelMap.put(3, 400);
        levelMap.put(4, 800);
        levelMap.put(5, 1600);
        levelMap.put(6, 3200);
        levelMap.put(7, 6400);
        levelMap.put(8, 12800);
        levelMap.put(9, 25600);
        levelMap.put(10, 51200);
        levelMap.put(11, 102400);
        levelMap.put(12, 204800);
        levelMap.put(13, 405600);
        levelMap.put(14, 811200);
        levelMap.put(15, 1622000);
        levelMap.put(16, 3244000);
        levelMap.put(17, 64000000);
        return levelMap;
    }
}

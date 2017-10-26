package gamemechanics.battlefield.map;

import gamemechanics.battlefield.Tile;
import gamemechanics.globals.DigitsPairIndices;
import gamemechanics.globals.Directions;

import java.util.*;

public final class BattleMapGenerator {
    private static final Map<Integer, List<Integer>> movementsMap = initDirectionsMap();

    private BattleMapGenerator() {}

    public static List<List<Tile>> generateBattleMap(Integer width, Integer height, Integer passableTilesCount) {
        final List<Integer> mapSize = new ArrayList<>(DigitsPairIndices.PAIR_SIZE);
        mapSize.set(DigitsPairIndices.X_COORD_INDEX, height);
        mapSize.set(DigitsPairIndices.Y_COORD_INDEX, width);
        List<List<Tile>> map = new ArrayList<>(height);
        for (Integer i = 0; i < map.size(); ++i) {
            map.set(i, new ArrayList<>(width));
            for (Integer j = 0; j < map.get(i).size(); ++j) {
                map.get(i).set(j, new Tile());
            }
        }
        List<Integer> coords = new ArrayList<>(DigitsPairIndices.PAIR_SIZE);
        Random random = new Random(System.currentTimeMillis());
        coords.set(DigitsPairIndices.X_COORD_INDEX, random.nextInt() % height);
        coords.set(DigitsPairIndices.Y_COORD_INDEX, random.nextInt() % width);
        map.get(coords.get(DigitsPairIndices.X_COORD_INDEX)).get(DigitsPairIndices.Y_COORD_INDEX).setIsPassable(true);
        Integer tilesMadePassable = 1;
        while (tilesMadePassable < passableTilesCount) {
            Integer direction = Directions.DIRECTIONS_COUNT;
            while (!isDirecionValid(coords, mapSize, direction)) {
                direction = random.nextInt() % Directions.DIRECTIONS_COUNT;
            }
            List<Integer> movement = movementsMap.get(direction);
            for (Integer i = 0; i < coords.size(); ++i) {
                coords.set(i, coords.get(i) + movement.get(i));
            }
            if (!map.get(coords.get(DigitsPairIndices.X_COORD_INDEX))
                    .get(coords.get(DigitsPairIndices.Y_COORD_INDEX)).getIsPassable()) {
                map.get(coords.get(DigitsPairIndices.X_COORD_INDEX))
                        .get(coords.get(DigitsPairIndices.Y_COORD_INDEX)).setIsPassable(true);
                ++tilesMadePassable;
            }
        }
        return map;
    }

    private static Boolean isDirecionValid(List<Integer> coords, List<Integer> mapSize, Integer direction) {
        switch (direction) {
            case Directions.UP:
                return coords.get(DigitsPairIndices.Y_COORD_INDEX) > 0;
            case Directions.RIGHT:
                return (coords.get(DigitsPairIndices.X_COORD_INDEX) + 1)
                        < mapSize.get(DigitsPairIndices.X_COORD_INDEX);
            case Directions.DOWN:
                return (coords.get(DigitsPairIndices.Y_COORD_INDEX) + 1)
                        < mapSize.get(DigitsPairIndices.Y_COORD_INDEX);
            case Directions.LEFT:
                return coords.get(DigitsPairIndices.X_COORD_INDEX) > 0;
        }
        return false;
    }

    private static Map<Integer, List<Integer>> initDirectionsMap() {
        Map<Integer, List<Integer>> directionsMap = new HashMap<>();

        List<Integer> upMovement = new ArrayList<>(DigitsPairIndices.PAIR_SIZE);
        upMovement.set(DigitsPairIndices.X_COORD_INDEX, 0);
        upMovement.set(DigitsPairIndices.Y_COORD_INDEX, -1);
        directionsMap.put(Directions.UP, upMovement);

        List<Integer> rightMovement = new ArrayList<>(DigitsPairIndices.PAIR_SIZE);
        rightMovement.set(DigitsPairIndices.X_COORD_INDEX, 1);
        rightMovement.set(DigitsPairIndices.Y_COORD_INDEX, 0);
        directionsMap.put(Directions.RIGHT, rightMovement);

        List<Integer> downMovement = new ArrayList<>(DigitsPairIndices.PAIR_SIZE);
        downMovement.set(DigitsPairIndices.X_COORD_INDEX, 0);
        downMovement.set(DigitsPairIndices.Y_COORD_INDEX, 1);
        directionsMap.put(Directions.DOWN, downMovement);

        List<Integer> leftMovement = new ArrayList<>(DigitsPairIndices.PAIR_SIZE);
        leftMovement.set(DigitsPairIndices.X_COORD_INDEX, -1);
        leftMovement.set(DigitsPairIndices.Y_COORD_INDEX, 0);
        directionsMap.put(Directions.LEFT, leftMovement);

        return directionsMap;
    }
}

package day15;

import intcodeComputer.IntcodeComputer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day15 {

    private static String resourceDirectory = "src/main/resources/day15/";
    private static String inputFile = resourceDirectory + "day15Input.txt";
    public static void main(String[] args) throws FileNotFoundException {
        String instructionSet = new Scanner(new FileReader(inputFile)).nextLine();

        ChristopherColumbusBot christopherColumbusBot = new ChristopherColumbusBot(instructionSet);

        christopherColumbusBot.discoverTheNewWorld();
        System.out.println("shortest Path to oxygen: " + christopherColumbusBot.findShortestPathToOxygen());

        System.out.println("hours for oxygen to spread: " + christopherColumbusBot.fillNewWorldWithOxygen());
    }


    public static class ChristopherColumbusBot {

        private IntcodeComputer intcodeComputer;
        private Map<Position, Space> map;

        Position currentPosition = new Position(0,0);

        Stack<Direction> travelledList;

        ChristopherColumbusBot(String instructionSet) {
            intcodeComputer = new IntcodeComputer(instructionSet);
            map = new HashMap<>();
            map.put(currentPosition, Space.EMPTY);
            travelledList = new Stack<>();
        }


        void discoverTheNewWorld() {
            boolean done = false;
            while  (!done) {
                boolean breadCrumbTravel = false;
                Direction toTravel = findUnexploredDirectionFromCurrentPosition();
                if(toTravel == null) {
                    breadCrumbTravel = true;
                    toTravel = Direction.findOpposite(travelledList.pop());
                }

                intcodeComputer.addInput(toTravel.code);
                intcodeComputer.runProgram();
                Space result = Space.fromCode(intcodeComputer.getOutputs().remove());

                Position position = null;
                switch (toTravel) {
                    case NORTH:
                        position = new Position(currentPosition.x, currentPosition.y + 1);
                        break;
                    case SOUTH:
                        position = new Position(currentPosition.x, currentPosition.y - 1);
                        break;
                    case EAST:
                        position = new Position(currentPosition.x + 1, currentPosition.y);
                        break;
                    case WEST:
                        position = new Position(currentPosition.x - 1, currentPosition.y);
                        break;
                    default:
                        throw new RuntimeException("unexpectedDirection");
                }

                map.put(position, result);
                if(result != Space.WALL ) {
                    if(!breadCrumbTravel) {
                        travelledList.push(toTravel);
                    }
                    currentPosition = position;
                }

                if (travelledList.isEmpty() && findUnexploredDirectionFromCurrentPosition() == null) {
                    done = true;
                }
            }

            printMap();
        }

        Direction findUnexploredDirectionFromCurrentPosition() {
            if ( null == map.getOrDefault(new Position(currentPosition.x, currentPosition.y +1), null)) {
                return Direction.NORTH;
            } else if (null == map.getOrDefault(new Position(currentPosition.x, currentPosition.y -1), null)){
                return Direction.SOUTH;
            } else if (null == map.getOrDefault(new Position(currentPosition.x + 1, currentPosition.y), null)) {
                return Direction.EAST;
            } else if (null == map.getOrDefault(new Position(currentPosition.x - 1, currentPosition.y), null)) {
                return Direction.WEST;
            } else {
                return null;
            }
        }


        int findShortestPathToOxygen() {
            Set<Position> exploredPositions = new HashSet<>();
            List<Position> currentSearching = new ArrayList<>();
            currentSearching.add(new Position(0,0));

            int countFound = 0;
            while(true) {
                countFound++;
                List<Position> nextSearching = new ArrayList<>();
                for(Position position : currentSearching) {
                    for (Position positionNear : getSearchablePathsFromPosition(position)) {
                        if (map.get(positionNear) == Space.OXYGEN) {
                            return countFound;
                        }
                        else if (map.get(positionNear) != Space.WALL) {
                            if(exploredPositions.add(positionNear)) {
                                nextSearching.add(positionNear);
                            }
                        }
                    }
                }
                currentSearching = nextSearching;

            }
        }

        private List<Position> getSearchablePathsFromPosition(Position position) {
            return List.of(
                    new Position(position.x + 1, position.y),
                    new Position(position.x - 1, position.y),
                    new Position(position.x, position.y - 1),
                    new Position(position.x, position.y + 1)
                );
        }


        void printMap() {
            System.out.println("\n\nMap Currently");

            int minX = map.keySet().stream().map(Position::getX).min(Integer::compareTo).get();
            int maxX = map.keySet().stream().map(Position::getX).max(Integer::compareTo).get();
            int minY = map.keySet().stream().map(Position::getY).min(Integer::compareTo).get();
            int maxY = map.keySet().stream().map(Position::getY).max(Integer::compareTo).get();

            for(int i = maxY; i >= minY; i--) { // Y is upside down because max x is lowest value so have to flip it
                StringBuilder stringBuilder = new StringBuilder();
                for(int j = minX; j <= maxX; j++) {
                    if (Space.WALL == map.get(new Position(j,i))) {
                        stringBuilder.append("#");
                    }
                    else if (Space.EMPTY == map.get(new Position(j,i))) {
                        stringBuilder.append(".");
                    }

                    else if (Space.OXYGEN == map.get(new Position(j,i))) {
                        stringBuilder.append("0");
                    }
                    else {
                        stringBuilder.append("U");
                    }
                }
                System.out.println(stringBuilder.toString());
            }
        }

        public int fillNewWorldWithOxygen() {
            int countHoursToFill = 0;
            List<Position> oxygenPointsToSpread =  map.entrySet().stream().filter(entrySet -> entrySet.getValue() == Space.OXYGEN).map(Map.Entry::getKey).collect(Collectors.toList());

            while (oxygenPointsToSpread.size() > 0) {
                List<Position> nextToSpread = new ArrayList<>();

                for (Position position : oxygenPointsToSpread) {
                    for (Position positionNear : getSearchablePathsFromPosition(position)) {
                        if (map.get(positionNear) == Space.EMPTY) {
                            map.put(positionNear, Space.OXYGEN);
                            nextToSpread.add(positionNear);
                        }
                    }
                }

                oxygenPointsToSpread = nextToSpread;
                if(oxygenPointsToSpread.size() > 0) {
                    countHoursToFill++;
                }
            }

            return countHoursToFill;
        }
    }

    public enum Space {
        WALL(0L),
        EMPTY(1L),
        OXYGEN(2L);
        private long code;
        Space(long code) {
            this.code = code;
        }

        public static Space fromCode(long code) {
            return Arrays.stream(Space.values()).filter(x-> x.code == code).findFirst().get();
        }
    }

    public enum Direction {
        NORTH(1L),
        SOUTH(2L),
        WEST(3L),
        EAST(4L);

        private long code;
        Direction(long code) {
            this.code = code;
        }

        private static Direction findOpposite(Direction direction) {
            switch (direction) {
                case NORTH:
                    return SOUTH;
                case SOUTH:
                    return NORTH;
                case EAST:
                    return WEST;
                case WEST:
                    return EAST;
                default:
                    throw new RuntimeException("no opposite of :" + direction);
            }
        }
    }

    public static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object obj) {
            Position other = (Position) obj;
            return this.x == other.x && this.y == other.y;
        }

        @Override
        public int hashCode() {
            return x + y;
        }

        public String toString(){
            return "(" + x + "," + y + ")";
        }
    }
}

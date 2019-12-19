package day17;

import intcodeComputer.IntcodeComputer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day17 {
    private static String resourceDirectory = "src/main/resources/day17/";
    private static String inputFile = resourceDirectory + "day17Input.txt";
    public static void main(String[] args) throws FileNotFoundException {
        String instructionSet = new Scanner(new FileReader(inputFile)).nextLine();
        HashMap<Position, Character> map = new HashMap<>();

        IntcodeComputer intcodeComputer = new IntcodeComputer(instructionSet);

        intcodeComputer.runProgram();
        Queue<Long> mapOutputs = intcodeComputer.getOutputs();

        int currentX=0;
        int currentY=0;

        while(mapOutputs.size() > 0) {
            long output = mapOutputs.remove();
            if(output == 10) {
                currentY++;
                currentX=0;
            }
            else {
                map.put(new Position(currentX, currentY), Character.valueOf((char)output));
                currentX++;
            }
        }

        List<Position> interSections = detectIntersectionsInMap(map, '#');
        int x = interSections.stream().map(position -> position.getX() * position.getY()).reduce(Integer::sum).get();
        printMap(map);
        System.out.println("calibration param: " + x);

        List<String> inputs = findInputsForPart2(map, new ArrayList<>());

        System.out.println("\n\nInputs To Computer for part two");
        System.out.println("Main: " + inputs.get(0));
        System.out.println("A: " + inputs.get(1));
        System.out.println("B: " + inputs.get(2));
        System.out.println("C: " + inputs.get(3));

        List<List<Long>> computerReadableInputs = inputs.stream().map(Day17::stringToLongRepresentation).collect(Collectors.toList());

        computerReadableInputs.add(List.of((long) 'n', (long) '\n'));

        String goMode =  "2" + instructionSet.substring(1);
        IntcodeComputer goComputer = new IntcodeComputer(goMode);
        for(List<Long> inputsList : computerReadableInputs) {
            for (Long input : inputsList) {
                goComputer.addInput(input);
            }
        }

        String exitReason = goComputer.runProgram(); // takes main program

        System.out.println(((LinkedList<Long>)goComputer.getOutputs()).getLast());
    }

    private static List<Long> stringToLongRepresentation(String x) {
        List<Long> inputs = new ArrayList<>();

        for(Character character : x.toCharArray()) {
            inputs.add( (long) character);
        }
        inputs.add((long)'\n');
        return inputs;
    }

    private static List<String> findInputsForPart2(HashMap<Position, Character> map, List<List<String>> bannedStartingAList) {
        String pathToEnd = findStringPathToEnd(map);

        List<String> a = findHigestValuedSectionOfStringToReplace(pathToEnd, bannedStartingAList);
        StringBuilder stringBuilder = new StringBuilder();
        a.forEach(aentry -> stringBuilder.append(aentry + ",") );
        pathToEnd = pathToEnd.replace(stringBuilder.toString(), "A,");

        List<String> b = findHigestValuedSectionOfStringToReplace(pathToEnd, new ArrayList<>());
        StringBuilder stringBuilder2 = new StringBuilder();
        b.forEach(aentry -> stringBuilder2.append(aentry + ",") );
        pathToEnd = pathToEnd.replace(stringBuilder2.toString(), "B,");

        List<String> c = findHigestValuedSectionOfStringToReplace(pathToEnd, new ArrayList<>());
        StringBuilder stringBuilder3 = new StringBuilder();
        c.forEach(aentry -> stringBuilder3.append(aentry + ",") );
        pathToEnd = pathToEnd.replace(stringBuilder3.toString(), "C,");


        boolean finished = true;
        for (String string :  pathToEnd.split(",")) {
            if(string.length() != 1) {
                finished = false;
                }
        }

        if (finished) {
            // removing comma at the end of the path because its there for reasons.
            return List.of(
                pathToEnd.substring(0, pathToEnd.length() - 1),
                joinElementsWithCommaBetween(a).replace(" ", ","),
                joinElementsWithCommaBetween(b).replace(" ", ","),
                joinElementsWithCommaBetween(c).replace(" ", ",")
            );

        }
        else {
            bannedStartingAList.add(a);
            return findInputsForPart2(map, bannedStartingAList);
        }
    }

    private static String joinElementsWithCommaBetween(List<String> input) {
        StringBuilder output = new StringBuilder();

        for(int i = 0; i < input.size(); i++) {
            if(i == input.size()-1) {
                output.append(input.get(i));
            }
            else {
                output.append(input.get(i)).append(",");
            }
        }
        return output.toString();
    }

    private static List<String> findHigestValuedSectionOfStringToReplace(String path, List<List<String>> bannedStartingAList) {
        List<String> pathTurnGoHeadCombos = Arrays.asList(path.split(","));

        Map<List<String>, Integer> subsetsInCommon = findCommonSubsectionsOfListBest(pathTurnGoHeadCombos);

        List<List<String>> sortedValueList =
            subsetsInCommon
                .entrySet()
                .stream()
                .filter(listIntegerEntry -> listIntegerEntry.getKey().size() < 6 && listIntegerEntry.getKey().size() > 1)
                .filter(listIntegerEntry -> !bannedStartingAList.contains(listIntegerEntry.getKey()))
                .sorted((o1, o2) -> Integer.compare(o2.getValue() * o2.getKey().size(), o1.getValue() * o1.getKey().size()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return sortedValueList.get(0);
    }

    private static Map<List<String>, Integer> findCommonSubsectionsOfListBest(List<String> pathTurnGoHeadCombos) {
        int indexToStart = 0;
        for(int i = 0; i< pathTurnGoHeadCombos.size(); i++) {
            if (pathTurnGoHeadCombos.get(i).length() != 1) {
                indexToStart = i;
                break;
            }
        }

        Map<List<String>, Integer> subsectionMatchCount = new HashMap<>();
        for(int j = indexToStart + 1; j < pathTurnGoHeadCombos.size(); j++) {

            List<String> subSection = pathTurnGoHeadCombos.subList(indexToStart, j);

            int matches = 1;
            for(int startOfNextSection = indexToStart + subSection.size();
                startOfNextSection +subSection.size() <= pathTurnGoHeadCombos.size();
                startOfNextSection++) {



                if(subSection.equals(pathTurnGoHeadCombos.subList(startOfNextSection, startOfNextSection + subSection.size()))) {
                    matches++;
                }
            }
            //this is here to avoid getting a B/List that referes to A/B list
            if (subSection.get(subSection.size() -1).length() == 1) {
                matches = 0;
                break;
            }
            subsectionMatchCount.put(subSection, matches);

        }
        return subsectionMatchCount;
    }

    /*private static Map<List<String>, Integer> findCommonSubsectionsOfList(List<String> pathTurnGoHeadCombos) {
        Map<List<String>, Integer> matchesSlices = new HashMap<>();
        for (int i = 0; i < pathTurnGoHeadCombos.size(); i++) {
            for (int j = 1; (i + j * 2) + 1 <= pathTurnGoHeadCombos.size(); j++) {
                List<String> patternToMatch = pathTurnGoHeadCombos.subList(i, i+j);
                if(!matchesSlices.containsKey(patternToMatch)) {
                    int matches = 0;
                    for(int startOfCheck = i; startOfCheck + patternToMatch.size() < pathTurnGoHeadCombos.size(); startOfCheck++) {
                        List<String> subSection = pathTurnGoHeadCombos.subList(startOfCheck, startOfCheck + patternToMatch.size());
                        if(patternToMatch.equals(subSection)) {
                            matches++;
                        }
                    }
                    if(matches > 2) {
                        matchesSlices.put(patternToMatch, matches);
                    }
                }
            }
        }
        return matchesSlices;
    }*/

    private static String findStringPathToEnd(HashMap<Position, Character> map) {
        Position startPosition = map.entrySet().stream().filter(entry -> entry.getValue().equals('^')).map(Map.Entry::getKey).findFirst().get();
        DirectionFacing facing = DirectionFacing.UP;
        boolean notDone =true;
        Position position = new Position(startPosition.getX(), startPosition.getY());
        StringBuilder builder = new StringBuilder();
        int aheadCount = 0;
        while(notDone) {
            Position ahead = getNextPositionAheadFromCurrent(position, facing);

            if(map.getOrDefault(ahead, 'Å') == '#') {
                aheadCount++;
                position = ahead;
            }
            else {
                if(aheadCount != 0) {
                    builder.append(aheadCount + ",");
                    aheadCount = 0;
                }
                char turnDirection = findTurnDirectionIfThereIsOne(map, position, facing);
                if(turnDirection == ' ') {
                    notDone = false;
                } else {
                    facing = updateFacingBasedOnTurn(facing, turnDirection);
                    builder.append(turnDirection + " ");
                }
            }
        }

        return builder.toString();
    }

    private static DirectionFacing updateFacingBasedOnTurn(DirectionFacing facing, char turnDirection) {
        switch (turnDirection) {
            case 'R':
                return DirectionFacing.valueOf(facing.toRight);
            case 'L':
                return DirectionFacing.valueOf(facing.toLeft);
            default:
                throw new RuntimeException("CANT TURN THIS WAY: " + turnDirection );

        }
    }

    //return R or L if there is a turn available.
    private static char findTurnDirectionIfThereIsOne(Map<Position, Character> map, Position position, DirectionFacing facing) {
        if(facing == DirectionFacing.UP || facing == DirectionFacing.DOWN) {
            return findDirectionTurnWhenFacingVertical(map, position, facing);
        }
        else {
            return findDirectionTurnWhenFacingHorizontal(map, position, facing);
        }
    }

    private static char findDirectionTurnWhenFacingHorizontal(Map<Position, Character> map, Position position, DirectionFacing facing) {
        char plusY = map.getOrDefault(new Position(position.getX(), position.getY() + 1), 'Å');
        char minusY = map.getOrDefault(new Position(position.getX(), position.getY() - 1), 'Å');

        if(plusY == '#') {
            if(facing == DirectionFacing.LEFT) {
                return 'L';
            } else {
                return 'R';
            }
        }
        else if (minusY == '#') {
            if(facing == DirectionFacing.LEFT) {
                return 'R';
            } else {
                return 'L';
            }
        }
        else {
            return ' ';
        }
    }

    private static char findDirectionTurnWhenFacingVertical(Map<Position, Character> map, Position position, DirectionFacing facing) {
        char plusX = map.getOrDefault(new Position(position.getX() + 1, position.getY()), 'Å');
        char minusX = map.getOrDefault(new Position(position.getX() - 1, position.getY()), 'Å');

        if(plusX == '#') {
            if(facing == DirectionFacing.UP) {
                return 'R';
            } else {
                return 'L';
            }
        }
        else if (minusX == '#') {
            if(facing == DirectionFacing.UP) {
                return 'L';
            } else {
                return 'R';
            }
        }
        else {
            return ' ';
        }
    }

    private static Position getNextPositionAheadFromCurrent(Position position, DirectionFacing facing) {
        switch (facing) {
            case UP:
                return new Position(position.getX(), position.getY() - 1);
            case DOWN:
                return new Position(position.getX(), position.getY() + 1);
            case LEFT:
                return new Position(position.getX() - 1, position.getY());
            case RIGHT:
                return new Position(position.getX() + 1, position.getY());
            default:
                throw new RuntimeException("Facing is weird");
        }
    }

    //JAVA enums are dumb and you can not do forward references so deal with this
    public enum DirectionFacing{
        UP("LEFT", "RIGHT"),
        RIGHT("UP", "DOWN"),
        LEFT("DOWN", "UP"),
        DOWN("RIGHT","LEFT");

        String toLeft;
        String toRight;
        DirectionFacing(String left, String right) {
            this.toLeft = left;
            this.toRight = right;
        }
    }


    private static List<Position> detectIntersectionsInMap(HashMap<Position, Character> map, Character c) {
        return map.keySet().stream()
            .filter(x -> map.get(x).equals(c))
            .filter(x -> map.getOrDefault(new Position(x.getX() + 1, x.getY()), 'Q').equals(c))
            .filter(x -> map.getOrDefault(new Position(x.getX() - 1, x.getY()),'Q').equals(c))
            .filter(x -> map.getOrDefault(new Position(x.getX(), x.getY() + 1),'Q').equals(c))
            .filter(x -> map.getOrDefault(new Position(x.getX(), x.getY() - 1),'Q').equals(c))
            .collect(Collectors.toList());
    }

    static void printMap(Map<Position, Character> map) {
        System.out.println("\n\nMap Currently");

        int minX = map.keySet().stream().map(Position::getX).min(Integer::compareTo).get();
        int maxX = map.keySet().stream().map(Position::getX).max(Integer::compareTo).get();
        int minY = map.keySet().stream().map(Position::getY).min(Integer::compareTo).get();
        int maxY = map.keySet().stream().map(Position::getY).max(Integer::compareTo).get();

        for(int i = minY; i <= maxY; i++) { // Y is upside down because max x is lowest value so have to flip it
            StringBuilder stringBuilder = new StringBuilder();
            for(int j = minX; j <= maxX; j++) {
                stringBuilder.append(map.get(new Position(j, i)));
            }
            System.out.println(stringBuilder.toString());
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

package day11;

import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day11 {

    private static String resourceDirectory = "src/main/resources/day11/";
    public static String inputFile = resourceDirectory + "day11Input.txt";
    public static void main(String[] args) throws Exception{
        String instructionSet = new Scanner(new FileReader(inputFile)).nextLine();

        LilPaintyRobot paintyBoy = new LilPaintyRobot(instructionSet, true);
        paintyBoy.paintAway();

        System.out.println("Part 1: painty boy painted :" + paintyBoy.paintBoard.keySet().size() + " squares");

        LilPaintyRobot paintyBoyPart2 = new LilPaintyRobot(instructionSet, false);
        paintyBoyPart2.paintAway();

        System.out.println("Part 2 starting to print");
        printRobotsPaintBoard(paintyBoyPart2);
    }

    private static void printRobotsPaintBoard(LilPaintyRobot lilPaintyRobot) {
        int minX = lilPaintyRobot.paintBoard.keySet().stream().map(Cord::getX).min(Integer::compareTo).get();
        int maxX = lilPaintyRobot.paintBoard.keySet().stream().map(Cord::getX).max(Integer::compareTo).get();
        int miny = lilPaintyRobot.paintBoard.keySet().stream().map(Cord::getY).min(Integer::compareTo).get();
        int maxy = lilPaintyRobot.paintBoard.keySet().stream().map(Cord::getY).max(Integer::compareTo).get();
        
        for(int i = minX; i <= maxX; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for(int j = miny; j <= maxy; j++) {
                if (PaintColor.WHITE == lilPaintyRobot.paintBoard.get(new Cord(i, j))) {
                    stringBuilder.append("#");
                }
                else {
                    stringBuilder.append(" ");
                }
            }
            System.out.println(stringBuilder.toString());
        }
    }

    public static class LilPaintyRobot {
        private IntcodeComputer intcodeComputer;
        private HashMap<Cord, PaintColor> paintBoard;
        private Cord currentLocation = new Cord(0,0);
        private Direction facing = Direction.UP;

        LilPaintyRobot(String instructionSet, boolean isPart1) {
            intcodeComputer = new IntcodeComputer(instructionSet);
            paintBoard = new HashMap<>();
            if (!isPart1) {
                paintBoard.put(currentLocation, PaintColor.WHITE);
            }

        }

        void paintAway() {
            boolean done = false;
            while (!done) {
                PaintColor standingOn = paintBoard.getOrDefault(currentLocation, PaintColor.BLACK);
                Long input = standingOn == PaintColor.BLACK ? 0L : 1L;
                intcodeComputer.addInput(input);

                String exitCode = intcodeComputer.runProgram();

                if(exitCode.equals("EXITED")) {
                    done = true;
                }

                Long colorToPaint = intcodeComputer.getOutputs().poll();
                Long directionTurn = intcodeComputer.getOutputs().poll();

                if(colorToPaint == 0) {
                    paintBoard.put(currentLocation, PaintColor.BLACK);
                } else {
                    paintBoard.put(currentLocation, PaintColor.WHITE);
                }

                if(directionTurn == 0) {
                    turnLeft();
                }
                else {
                    turnRight();
                }

                moveForward();
            }
        }

        private void turnLeft() {
            switch (facing) {
                case UP:
                    facing = Direction.LEFT;
                    break;
                case DOWN:
                    facing = Direction.RIGHT;
                    break;
                case LEFT:
                    facing = Direction.DOWN;
                    break;
                case RIGHT:
                    facing = Direction.UP;
                    break;
            }
        }

        private void turnRight() {
            switch (facing) {
                case UP:
                    facing = Direction.RIGHT;
                    break;
                case DOWN:
                    facing = Direction.LEFT;
                    break;
                case LEFT:
                    facing = Direction.UP;
                    break;
                case RIGHT:
                    facing = Direction.DOWN;
                    break;
            }
        }

        private void moveForward() {
            switch (facing) {
                case UP:
                    currentLocation = new Cord(currentLocation.x, currentLocation.y + 1);
                    break;
                case DOWN:
                    currentLocation = new Cord(currentLocation.x, currentLocation.y - 1);
                    break;
                case RIGHT:
                    currentLocation = new Cord(currentLocation.x + 1, currentLocation.y);
                    break;
                case LEFT:
                    currentLocation = new Cord(currentLocation.x - 1, currentLocation.y);
                    break;

            }
        }
    }

    public enum Direction{
        UP,
        LEFT,
        DOWN,
        RIGHT
    }

    public enum PaintColor{
        BLACK,
        WHITE
    }

    public static class Cord{
        private int x;
        private int y;

        Cord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        @Override
        public boolean equals(Object obj) {
            Cord other = (Cord)obj;
            return this.x == other.x && this.y == other.y;
        }
        @Override
        public int hashCode() {
            return  x + y;
        }
    }


    public static class IntcodeComputer {

        private List<Long> programMem;
        private Map<Long, Long> extraTerrestrialMemory;
        private int programCount;

        long relativeBase = 0;

        private Queue<Long> inputs = new LinkedList<>();
        private Queue<Long> outputs = new LinkedList<>();


        IntcodeComputer(String instructionSet) {
            programMem =  List.of(instructionSet.split(","))
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

            extraTerrestrialMemory = new HashMap<>();

        }

        void addInput(Long input) {
            inputs.add(input);
        }

        Queue<Long> getOutputs() {
            return outputs;
        }


        private String runProgram() {
            while (true) {
                long instruction = programMem.get(programCount++);

                long operationCode = instruction % 100;
                long modes = instruction / 100;
                long[] parameterModes = new long[3];
                int modesCount = 0;
                while (modes > 0) {
                    parameterModes[modesCount++] = modes % 10;
                    modes = modes / 10;
                }

                if(operationCode == 99) {
                    return "EXITED";
                }
                else if (isThreeParameterOpCode(operationCode)) {
                    long firstParameter = getParameterValue(parameterModes[0], programCount++);
                    long secondParameter = getParameterValue(parameterModes[1], programCount++);
                    long finalPosition = parameterModes[2] == 2 ? relativeBase + getParameterValueFromMemory((long) programCount++) : getParameterValueFromMemory((long)programCount ++);

                    long valueToSetToFinalPosition;
                    if (operationCode == 1) {
                        valueToSetToFinalPosition = firstParameter + secondParameter;
                    }
                    else if (operationCode == 2) {
                        valueToSetToFinalPosition =  firstParameter * secondParameter;
                    }
                    else if(operationCode == 7) {
                        valueToSetToFinalPosition = firstParameter < secondParameter ? 1 : 0;
                    }
                    else if (operationCode == 8) {
                        valueToSetToFinalPosition = firstParameter == secondParameter ? 1 : 0;
                    }
                    else {
                        throw new RuntimeException("unexpected 3 param opCode...");
                    }

                    setParameterValueToMemory(finalPosition, valueToSetToFinalPosition);
                }
                else if (operationCode == 3 || operationCode == 4) {
                    if (operationCode == 3) {
                        if (inputs.size() == 0) {
                            programCount -= 1; //try this again if this gets ran again!
                            return "NEED_INPUT";
                        }
                        else {
                            long parameter1 = programMem.get(programCount++);
                            if (parameterModes[0] == 2) {
                                parameter1 = relativeBase + parameter1;
                            }
                            setParameterValueToMemory(parameter1, inputs.remove());
                        }
                    }
                    else if (operationCode == 4) {
                        long output = getParameterValue(parameterModes[0], programCount++);
                        outputs.add(output);
                    }
                }
                else if (operationCode == 5 || operationCode == 6) {
                    long parameter1 = getParameterValue(parameterModes[0], programCount++);
                    long parameter2 = getParameterValue(parameterModes[1], programCount++);

                    if (operationCode == 5) {
                        if (parameter1 != 0) {
                            programCount = (int)parameter2;
                        }
                    }
                    else if (operationCode == 6){
                        if (parameter1 == 0) {
                            programCount = (int)parameter2;
                        }
                    }
                }
                else if (operationCode == 9) {
                    relativeBase += getParameterValue(parameterModes[0], programCount++);
                }
                else {
                    throw new RuntimeException("unexpected Opcode " + operationCode);
                }
            }
        }

        private static boolean isThreeParameterOpCode(long opCode) {
            return opCode == 1 || opCode == 2 || opCode == 7 || opCode == 8;
        }

        long getParameterValue(long parameterMode, long paramValue) {
            if(parameterMode == 0) {
                return getParameterValueFromMemory((getParameterValueFromMemory(paramValue)));
            }
            else if (parameterMode == 1) {
                return getParameterValueFromMemory(paramValue);
            }
            else if (parameterMode == 2) {
                return getParameterValueFromMemory(relativeBase + getParameterValueFromMemory(paramValue));
            }
            else {
                throw new RuntimeException("unexpected parameterMode");
            }
        }

        long getParameterValueFromMemory(Long index) {
            if(index >= programMem.size()) {
                return extraTerrestrialMemory.getOrDefault(index, 0L);
            }
            else {
                return programMem.get(index.intValue());
            }
        }

        void setParameterValueToMemory(Long index, Long value){
            if(index >= programMem.size()) {
                extraTerrestrialMemory.put(index,value);
            }
            else {
                programMem.set(index.intValue(), value);
            }
        }
    }
}

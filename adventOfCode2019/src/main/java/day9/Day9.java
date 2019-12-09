package day9;

import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day9 {

    private static String resourceDirectory = "src/main/resources/day9/";
    public static String inputFile = resourceDirectory + "day9Input.txt";

    public static void main (String[] args) throws Exception {

        String instructionSet = new Scanner(new FileReader(inputFile)).nextLine();

        IntcodeComputer part1IntcodeComputer = new IntcodeComputer(instructionSet);
        part1IntcodeComputer.addInput(1L);
        part1IntcodeComputer.runProgramUntilBlocksOrExits();
        System.out.println("Part one output : " + part1IntcodeComputer.getOutputs().remove());

        IntcodeComputer part2IntcodeComputer = new IntcodeComputer(instructionSet);
        part2IntcodeComputer.addInput(2L);
        part2IntcodeComputer.runProgramUntilBlocksOrExits();
        System.out.println("Part one output : " + part2IntcodeComputer.getOutputs().remove());

    }

    public static class IntcodeComputer {

        private List<Long> programMem;
        private Map<Long, Long> extraTerestrialMemory;
        private int programCount;

        long relativeBase = 0;

        private Queue<Long> inputs = new LinkedList<>();
        private Queue<Long> outputs = new LinkedList<>();


        IntcodeComputer(String instructionSet) {
            programMem =  List.of(instructionSet.split(","))
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

            extraTerestrialMemory = new HashMap<>();

        }

        void addInput(Long input) {
            inputs.add(input);
        }

        Queue<Long> getOutputs() {
            return outputs;
        }

        void runProgramUntilBlocksOrExits() {
            runProgram();
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
                            programCount -= 2; //try this again if this gets ran again!
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
                    throw new RuntimeException("unexpected Opcode");
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
                return extraTerestrialMemory.getOrDefault(index, 0L);
            }
            else {
                return programMem.get(index.intValue());
            }
        }

        void setParameterValueToMemory(Long index, Long value){
            if(index >= programMem.size()) {
                extraTerestrialMemory.put(index,value);
            }
            else {
                programMem.set(index.intValue(), value);
            }
        }
    }
}
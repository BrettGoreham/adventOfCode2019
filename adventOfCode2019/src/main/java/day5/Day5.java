package day5;

import java.util.List;
import java.util.stream.Collectors;

public class Day5 {

    public static int intCodeBase = 100; //Base of Opcode 0-99 anything larger determines parameter modes.

    public static void main(String[] args) {
        String instructionSet = "3,225,1,225,6,6,1100,1,238,225,104,0,1101,11,91,225,1002,121,77,224,101,-6314,224,224,4,224,1002,223,8,223,1001,224,3,224,1,223,224,223,1102,74,62,225,1102,82,7,224,1001,224,-574,224,4,224,102,8,223,223,1001,224,3,224,1,224,223,223,1101,28,67,225,1102,42,15,225,2,196,96,224,101,-4446,224,224,4,224,102,8,223,223,101,6,224,224,1,223,224,223,1101,86,57,225,1,148,69,224,1001,224,-77,224,4,224,102,8,223,223,1001,224,2,224,1,223,224,223,1101,82,83,225,101,87,14,224,1001,224,-178,224,4,224,1002,223,8,223,101,7,224,224,1,223,224,223,1101,38,35,225,102,31,65,224,1001,224,-868,224,4,224,1002,223,8,223,1001,224,5,224,1,223,224,223,1101,57,27,224,1001,224,-84,224,4,224,102,8,223,223,1001,224,7,224,1,223,224,223,1101,61,78,225,1001,40,27,224,101,-89,224,224,4,224,1002,223,8,223,1001,224,1,224,1,224,223,223,4,223,99,0,0,0,677,0,0,0,0,0,0,0,0,0,0,0,1105,0,99999,1105,227,247,1105,1,99999,1005,227,99999,1005,0,256,1105,1,99999,1106,227,99999,1106,0,265,1105,1,99999,1006,0,99999,1006,227,274,1105,1,99999,1105,1,280,1105,1,99999,1,225,225,225,1101,294,0,0,105,1,0,1105,1,99999,1106,0,300,1105,1,99999,1,225,225,225,1101,314,0,0,106,0,0,1105,1,99999,1008,677,226,224,1002,223,2,223,1006,224,329,101,1,223,223,8,226,677,224,102,2,223,223,1005,224,344,101,1,223,223,1107,226,677,224,102,2,223,223,1006,224,359,101,1,223,223,1007,226,226,224,102,2,223,223,1006,224,374,101,1,223,223,7,677,677,224,102,2,223,223,1005,224,389,1001,223,1,223,108,677,677,224,1002,223,2,223,1005,224,404,101,1,223,223,1008,226,226,224,102,2,223,223,1005,224,419,1001,223,1,223,1107,677,226,224,102,2,223,223,1005,224,434,1001,223,1,223,1108,677,677,224,102,2,223,223,1006,224,449,1001,223,1,223,7,226,677,224,102,2,223,223,1005,224,464,101,1,223,223,1008,677,677,224,102,2,223,223,1005,224,479,101,1,223,223,1007,226,677,224,1002,223,2,223,1006,224,494,101,1,223,223,8,677,226,224,1002,223,2,223,1005,224,509,101,1,223,223,1007,677,677,224,1002,223,2,223,1006,224,524,101,1,223,223,107,226,226,224,102,2,223,223,1006,224,539,101,1,223,223,107,226,677,224,102,2,223,223,1005,224,554,1001,223,1,223,7,677,226,224,102,2,223,223,1006,224,569,1001,223,1,223,107,677,677,224,1002,223,2,223,1005,224,584,101,1,223,223,1107,677,677,224,102,2,223,223,1005,224,599,101,1,223,223,1108,226,677,224,102,2,223,223,1006,224,614,101,1,223,223,8,226,226,224,102,2,223,223,1006,224,629,101,1,223,223,108,226,677,224,102,2,223,223,1005,224,644,1001,223,1,223,108,226,226,224,102,2,223,223,1005,224,659,101,1,223,223,1108,677,226,224,102,2,223,223,1006,224,674,1001,223,1,223,4,223,99,226";
        int input = 5; //1 for part 1. 5 for part 2

        List<Integer> program =  List.of(instructionSet.split(","))
            .stream()
            .map(x -> Integer.valueOf(x))
            .collect(Collectors.toList());

        int programCount = 0;
        boolean doExit= false;
        while (!doExit) {

            int instruction = program.get(programCount++);

            int operationCode = instruction % intCodeBase;

            int modes = instruction / intCodeBase;
            int[] parameterModes = new int[3];
            int modesCount = 0;
            while (modes > 0) {
                parameterModes[modesCount++] = modes % 10;
                modes = modes / 10;
            }

            if(operationCode == 99) {
                doExit = true;
            }
            else if (isThreeParameterOpCode(operationCode)) {
                int firstParameter = parameterModes[0] == 1 ? program.get(programCount++) : program.get(program.get(programCount++));
                int secondParameter = parameterModes[1] == 1 ? program.get(programCount++) : program.get(program.get(programCount++));
                int finalPosition = program.get(programCount++);

                int valueToSetToFinalPosition;
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

                program.set(finalPosition, valueToSetToFinalPosition);
            }
            else if (operationCode == 3 || operationCode == 4) {
                int parameter1 = program.get(programCount++);

                if (operationCode == 3) {
                    program.set(parameter1, input);
                }
                else if (operationCode == 4) {
                   System.out.println("output: " + (parameterModes[0] == 1 ? parameter1 : program.get(parameter1)));
                }
            }
            else if (operationCode == 5 || operationCode == 6) {
                int parameter1 = parameterModes[0] == 1 ? program.get(programCount++) : program.get(program.get(programCount++));
                int parameter2 = parameterModes[1] == 1 ? program.get(programCount++) : program.get(program.get(programCount++));

                if (operationCode == 5) {
                    if (parameter1 != 0) {
                        programCount = parameter2;
                    }
                }
                else if (operationCode == 6){
                    if (parameter1 == 0) {
                        programCount = parameter2;
                    }
                }
            }
            else {
                throw new RuntimeException("unexpected Opcode");
            }
        }
    }

    private static boolean isThreeParameterOpCode(int opCode) {
        return opCode == 1 || opCode == 2 || opCode == 7 || opCode == 8;
    }
}

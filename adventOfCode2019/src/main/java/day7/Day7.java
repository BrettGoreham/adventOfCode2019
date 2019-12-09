package day7;

import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day7 {

    private static String resourceDirectory = "src/main/resources/day7/";
    private static String inputFile = resourceDirectory + "day7Program.txt";
    public static String testFile1 = resourceDirectory + "day7Test.txt";
    public static String testFile2 = resourceDirectory + "day7Test2.txt";
    public static String testFile3 = resourceDirectory + "day7Test3.txt";
    public static String testFilePart2 = resourceDirectory + "day7Testpart2.txt";
    public static String testFilePart2Test2 = resourceDirectory + "day7TestPart2Test2.txt";

    public static void main(String[] args) throws Exception {

        String instructionSet = new Scanner(new FileReader(inputFile)).nextLine();
        boolean feedbackMode = true; //true for part 2. false for part 1;

        List<Integer> phases;
        if(feedbackMode) {
            phases = new ArrayList<>(Arrays.asList(5,6,7,8,9));
        }
        else {
            phases = new ArrayList<>(Arrays.asList(0,1,2,3,4));
        }

        List<List<Integer>> listOfPossiblePhases = createAllPermutationsOf(phases);
        List<Integer> thrustOutputsPossible = new ArrayList<>();

        for (List<Integer> phaseSet : listOfPossiblePhases) {
            List<Amplifier> amplifiers = createAmplifiersForPhaseSet(phaseSet, instructionSet);

            int amplfiersRan = 0;// this is only important for non feedback mode to exit after all amps ran once
            int amplifierToRun = 0;
            do {
                Amplifier amp = amplifiers.get(amplifierToRun);
                amp.runUntilBlockedOrExited();

                amplifierToRun = (amplifierToRun + 1) % amplifiers.size();

                amplifiers.get(amplifierToRun).addInputs(amp.getAvailableOutputs());
                amplfiersRan++;

            } while (
                shouldRunNextAmplifier(amplifiers.get(amplifierToRun).inputs.size(),
                                                amplifiers.get(amplifierToRun).lastExitReason,
                                                feedbackMode,
                                                amplfiersRan,
                                                phaseSet.size()
                                            )
            );

            thrustOutputsPossible.addAll(amplifiers.get(amplifiers.size() -1 ).totalOutputs);
        }

        System.out.println(thrustOutputsPossible);
        System.out.println(thrustOutputsPossible.stream().reduce(Integer::max).get());
    }

    private static <E> List<List<E>> createAllPermutationsOf(List<E> list) {
        if (list.size() == 1) {
           return List.of(list);
        }
        else {
            E element = list.remove(0);
            List<List<E>> toReturn = new ArrayList<>();
            List<List<E>> currentPermutations = createAllPermutationsOf(list);

            for(List<E> perm : currentPermutations) {
                for (int i  = 0; i <= perm.size(); i++) {
                    List<E> tempArray = new ArrayList<>(perm);
                    tempArray.add(i, element);
                    toReturn.add(tempArray);
                }
            }
            return toReturn;
        }
    }

    private static List<Amplifier> createAmplifiersForPhaseSet(List<Integer> phaseSet, String instructionSet) {
        List<Amplifier> amplifiers = new ArrayList<>();
        for (int i = 0; i < phaseSet.size(); i++) {

            Amplifier amplifier = new Amplifier(instructionSet);
            amplifier.addInput(phaseSet.get(i));
            amplifiers.add(amplifier);
            if (i == 0) {
                amplifier.addInput(0);
            }
        }
        return amplifiers;
    }

    private static boolean shouldRunNextAmplifier(
        int numNextAmpInputs,
        ExitReason nextAmpsExitReason,
        boolean feedbackMode,
        int numOfAmpsRan,
        int numberOfAmpsAvailable) {

        boolean exitIfNextAmpIsStuckOrExited = numNextAmpInputs == 0 || nextAmpsExitReason == ExitReason.EXITED;
        boolean exitIfFeedbackOffAndAllAmpsRan = !feedbackMode && numOfAmpsRan >= numberOfAmpsAvailable;

        return !(exitIfFeedbackOffAndAllAmpsRan || exitIfNextAmpIsStuckOrExited);

    }

    private enum ExitReason {
        NEED_INPUT,
        EXITED
    }

    private static class Amplifier {
        private List<Integer> programMem;
        private int programCount;

        private Queue<Integer> inputs = new LinkedList<>();
        private Queue<Integer> outputs = new LinkedList<>();
        private List<Integer> totalOutputs;

        private ExitReason lastExitReason;

        public Amplifier(String instructionSet) {
            programMem =  List.of(instructionSet.split(","))
                            .stream()
                            .map(x -> Integer.valueOf(x))
                            .collect(Collectors.toList());

            totalOutputs = new ArrayList<>();

        }


        public void addInput(int input) {
            inputs.add(input);
        }
        public void addInputs(List<Integer> toInputs) {
            for (Integer integer : toInputs) {
                inputs.add(integer);
            }
        }

        public List<Integer> getAvailableOutputs() {
            List<Integer> outputsAvailable = new ArrayList<>();

            while (!outputs.isEmpty()) {
                outputsAvailable.add(outputs.remove());
            }

            return outputsAvailable;
        }

        public void runUntilBlockedOrExited() {
            lastExitReason = runProgram();
        }

        private ExitReason runProgram() {

            while (true) {
                int instruction = programMem.get(programCount++);

                int operationCode = instruction % 100;

                int modes = instruction / 100;
                int[] parameterModes = new int[3];
                int modesCount = 0;
                while (modes > 0) {
                    parameterModes[modesCount++] = modes % 10;
                    modes = modes / 10;
                }

                if(operationCode == 99) {
                    return ExitReason.EXITED;
                }
                else if (isThreeParameterOpCode(operationCode)) {
                    int firstParameter = parameterModes[0] == 1 ? programMem.get(programCount++) : programMem.get(programMem.get(programCount++));
                    int secondParameter = parameterModes[1] == 1 ? programMem.get(programCount++) : programMem.get(programMem.get(programCount++));
                    int finalPosition = programMem.get(programCount++);

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

                    programMem.set(finalPosition, valueToSetToFinalPosition);
                }
                else if (operationCode == 3 || operationCode == 4) {
                    int parameter1 = programMem.get(programCount++);

                    if (operationCode == 3) {
                        if (inputs.size() == 0) {
                            programCount -= 2; //try this again if this gets ran again!
                            return ExitReason.NEED_INPUT;
                        }
                        else {
                            programMem.set(parameter1, inputs.remove());
                        }
                    }
                    else if (operationCode == 4) {
                        int output = (parameterModes[0] == 1 ? parameter1 : programMem.get(parameter1));
                        outputs.add(output);
                        totalOutputs.add(output);
                    }
                }
                else if (operationCode == 5 || operationCode == 6) {
                    int parameter1 = parameterModes[0] == 1 ? programMem.get(programCount++) : programMem.get(programMem.get(programCount++));
                    int parameter2 = parameterModes[1] == 1 ? programMem.get(programCount++) : programMem.get(programMem.get(programCount++));

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
}
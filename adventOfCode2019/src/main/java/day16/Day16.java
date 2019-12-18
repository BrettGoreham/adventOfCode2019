package day16;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day16 {

    private static String resourceDirectory = "src/main/resources/day16/";
    private static String inputFile = resourceDirectory + "day16Input.txt";
    private static String inputTestFile = resourceDirectory + "day16TestInput.txt";
    private static String inputTestFile2 = resourceDirectory + "day16TestInput2.txt";
    private static String inputTestFile3 = resourceDirectory + "day16TestInput3.txt";
    private static String inputTestFile4 = resourceDirectory + "day16TestInput4.txt";
    private static String inputTestFilePart2 = resourceDirectory + "day16TestInputPart2.txt";

    private static int[] base = new int[]{0,1,0,-1};

    public static void main(String[] args) throws FileNotFoundException {
        String input = new Scanner(new FileReader(inputFile)).nextLine();

        System.out.println("Part1:");
        part1(input);
        System.out.println("Part2:");
        part2(input);
    }

    private static void part1(String input) {
        List<Integer> numbers = splitToNChar(input,1).stream().map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> outputAfterPhases = calculateNthPhase(numbers, 100);

        System.out.println("Output Of Part 2: " + getFirst8IntsOfOutput(outputAfterPhases.stream().mapToInt(i->i).toArray()));
    }

    private static List<Integer> calculateNthPhase(List<Integer> numbers, int numPhases) {
        for(int rounds = 0; rounds < numPhases; rounds++) {
            List<Integer> phaseOutput = new ArrayList<>();

            for(int i = 1; i < numbers.size() + 1; i++) {
                int total = 0;
                for(int j = 1; j < numbers.size() + 1; j++) {
                    total += base[(j / i) % base.length] * numbers.get(j - 1);
                }
                if(total < 0) {
                    phaseOutput.add((-1 * total) % 10);
                }
                else {
                    phaseOutput.add(total % 10);
                }
            }
            numbers = phaseOutput;
        }
        return numbers;
    }

    private static void part2(String input) {
        String totalSet = input.repeat(10000);

        String mattersSet = totalSet.substring(Integer.parseInt(totalSet.substring(0, 7)));
        List<Integer> numbers = splitToNChar(mattersSet,1).stream().map(Integer::parseInt).collect(Collectors.toList());

        int[] inputArray = numbers.stream().mapToInt(i->i).toArray();
        int[] outputAfterPhases = calculateNthPhaseWithCheating(inputArray, 100);
        System.out.println("Output Of Part 2: " + getFirst8IntsOfOutput(outputAfterPhases));
    }

    private static String getFirst8IntsOfOutput(int... outputAfterPhases) {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < 8; i++) {
            s.append(outputAfterPhases[i]);
        }
        return s.toString();
    }

    // since offset is soo high all things after index will have a multipler of 1 so just add together.
    // since n+1 of next phase is sum of all digits of the current phase we can use just that alone to get n's next phase
    private static int[] calculateNthPhaseWithCheating(int[] numbers, int phases) {
        for(int i = 0; i < phases; i++) {
            int[] phaseNums = new int[numbers.length];
            for(int index = numbers.length - 1 ; index >= 0 ; index--) {
                if (index == numbers.length -1) {
                    phaseNums[index] = numbers[index];
                }
                else {
                     phaseNums[index] = (numbers[index] + phaseNums[index + 1]) % 10;
                }
            }
            numbers = phaseNums;
        }
        return numbers;
    }

    //split string into all its parts.
    private static List<String> splitToNChar(String text, int size) {
        List<String> parts = new ArrayList<>();

        int length = text.length();
        for (int i = 0; i < length; i += size) {
            parts.add(text.substring(i, Math.min(length, i + size)));
        }
        return parts;
    }
}
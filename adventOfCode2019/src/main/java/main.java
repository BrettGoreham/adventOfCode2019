import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class main {

    public static void mainDay1(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String input = "125860,66059,147392,64447,72807,136018,144626,68233,130576,92645,52805,79642,74361,98270,110796,62578,58421,125079,52683,144885,148484,113638,125026,112534,125479,51539,122007,60048,67923,76115,144822,115991,133505,85249,142441,90211,87022,68196,117577,58112,116865,108253,127674,93302,58817,126794,89824,134386,99700,125855,119753,64456,68167,88047,127864,146890,71912,128375,134365,91544,104179,84700,95937,78409,94604,130423,98348,87489,105103,94794,123723,134298,88283,59543,53645,89325,109301,143668,96250,130371,140436,95857,98543,91372,137056,142578,116185,96588,93025,122275,99201,110492,109700,106755,120979,60957,134983,130840,132329,65057";
        //String input = "14";
        Integer fuelNeeded = List.of(input.split(","))
                                    .stream()
                                    .map(x -> Integer.valueOf(x))
                                    .map(x -> (Math.floorDiv(x, 3) - 2))
                                    .map(x -> x + calculateFuelNeededForFuel(x))
                                    .reduce(0, Integer::sum);


        //int extraFuelNeededToCarryFuel = calculateFuelNeededForFuel(fuelNeeded);

        System.out.println("final amount of fuel = " + (fuelNeeded));
        System.out.println("enter to exit...");
        scanner.nextLine();
    }

    private static int calculateFuelNeededForFuel(int fuelNeeded) {
        int extraFuelNeededToCarryFuelNeeded =  Math.floorDiv(fuelNeeded, 3) - 2;
        System.out.println("fuel Needed : " + fuelNeeded + " extraFuelFor that fuel : " + extraFuelNeededToCarryFuelNeeded);

        if(extraFuelNeededToCarryFuelNeeded <= 0) {
            return 0;
        }
        else {
            return extraFuelNeededToCarryFuelNeeded + calculateFuelNeededForFuel(extraFuelNeededToCarryFuelNeeded);
        }
    }


    public static void mainDay2(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String input = "1,12,2,3,1,1,2,3,1,3,4,3,1,5,0,3,2,13,1,19,1,5,19,23,2,10,23,27,1,27,5,31,2,9,31,35,1,35,5,39,2,6,39,43,1,43,5,47,2,47,10,51,2,51,6,55,1,5,55,59,2,10,59,63,1,63,6,67,2,67,6,71,1,71,5,75,1,13,75,79,1,6,79,83,2,83,13,87,1,87,6,91,1,10,91,95,1,95,9,99,2,99,13,103,1,103,6,107,2,107,6,111,1,111,2,115,1,115,13,0,99,2,0,14,0";

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                List<Integer> program =  List.of(input.split(","))
                    .stream()
                    .map(x -> Integer.valueOf(x))
                    .collect(Collectors.toList());

                program.set(1, i);
                program.set(2, j);

                int programcount = 0;
                boolean doExit= false;
                while (!doExit) {
                    int operationCode = program.get(programcount++);
                    if(operationCode == 99) {
                        doExit = true;
                    }
                    else if (operationCode == 1 || operationCode == 2) {
                        int firstPosition = program.get(programcount++);
                        int secondPosition = program.get(programcount++);
                        int finalPosition = program.get(programcount++);

                        if(operationCode == 1) {
                            program.set(finalPosition, (program.get(firstPosition) + program.get(secondPosition)));
                        }
                        else {
                            program.set(finalPosition, (program.get(firstPosition) * program.get(secondPosition)));
                        }
                    }
                }

                if(program.get(0) == 19690720) {
                    System.out.println("noun = " + program.get(1) + ",verb = " + program.get(2));
                    System.out.println("100 * noun + verb = " + (100 * program.get(1) + program.get(2)));
                    break;
                }
            }
        }
        System.out.println("enter to exit...");
        scanner.nextLine();
    }
}

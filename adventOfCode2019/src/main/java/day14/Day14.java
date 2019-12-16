package day14;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Day14 {

    private static String resourceDirectory = "src/main/resources/day14/";
    private static String inputFile = resourceDirectory + "day14Input.txt";
    public static String testInputFile = resourceDirectory + "day14TestInput.txt";
    public static String testInputFile2 = resourceDirectory + "day14TestInput2.txt";
    public static String testInputFile3 = resourceDirectory + "day14TestInput3.txt";
    public static String testInputFile4 = resourceDirectory + "day14TestInput4.txt";
    public static String testInputFile5 = resourceDirectory + "day14TestInput5.txt";

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileReader(inputFile));
        Map<FormulaPart, List<FormulaPart>> formulas = new HashMap<>(); // Key = output, value = requirements;
        while(scanner.hasNext()) {
            addToFormulas(formulas, scanner.nextLine());
        }
        scanner.close();

        long oreRequiredForOne = findRequirementsForOutputForBase("ORE", "FUEL", 1L, formulas, new ArrayList<>());
        System.out.println("Part one: ore required for 1 Fuel: " + oreRequiredForOne);

        long amountOfOreWeHave = 1000000000000L;
        long minAmount = amountOfOreWeHave / oreRequiredForOne;
        long maxAmount = minAmount * 3;// random amount i assumed

        List<Long> amounts = LongStream.range(minAmount, maxAmount).boxed().collect(Collectors.toList());

        // do some binary search ish since these are required to be in order. and there is a metric fuck tonne of options.
        while (amounts.size() > 1) {
            long min = 0;
            long max = amounts.size();
            long middle = (max - min) / 2;
            long toTest = amounts.get((int)middle);
            formulas.keySet().stream().filter(fuelPart -> fuelPart.name.equals("FUEL")).findFirst().get().amount = (int)toTest;

            long oreRequiredForX = findRequirementsForOutputForBase("ORE", "FUEL", toTest, formulas, new ArrayList<>());
            if (oreRequiredForX <= amountOfOreWeHave) {
                amounts = amounts.subList((int) middle, (int)max);
            }
            else {
                amounts = amounts.subList((int) min, (int) middle);
            }
        }

        System.out.println("Part two: " + amountOfOreWeHave + "(1 trillion) can generate " + amounts.get(0) + " Fuel");
    }

    private static long findRequirementsForOutputForBase(String base, String want, long NumberOfWanted, Map<FormulaPart, List<FormulaPart>> formulas, List<FormulaPart> excessAmounts) {
        List<FormulaPart> topLevelFormula = formulas.get(formulas.keySet().stream().filter(fuelPart -> fuelPart.name.equals(want)).findFirst().get());

        List<FormulaPart> needs = new ArrayList<>(); //do this to avoid removing from actualFormulaList
        for (FormulaPart formulaPart : topLevelFormula) {
            needs.add(new FormulaPart(formulaPart.amount * NumberOfWanted, formulaPart.name ));
        }

        long requiredBase = 0;

        while (needs.size() > 0) {
            FormulaPart need = needs.remove(0);

            Optional<FormulaPart> excessOfNeed = excessAmounts.stream().filter(x -> x.name.equals(need.name)).findFirst();
            if(excessOfNeed.isPresent() && excessOfNeed.get().amount >= need.amount) {
                excessOfNeed.get().amount -= need.amount;
            }
            else {

                long amountNeeded = need.amount;
                if (excessOfNeed.isPresent()) {
                    amountNeeded -= excessOfNeed.get().amount;
                    excessOfNeed.get().amount = 0;
                }

                FormulaPart key = findFormulaOutputFromNeed(need, formulas);
                long numOfKeyNeeded = (long) Math.ceil(((double)amountNeeded) / key.amount);
                long excessGenerated = (key.amount * numOfKeyNeeded) - amountNeeded;
                if (excessGenerated > 0 ) {
                    addToExcessAmounts(excessAmounts, excessGenerated, key.name);
                }

                for(FormulaPart formulaPart : formulas.get(key)) {
                    if(formulaPart.name.equals(base)) {
                        requiredBase += formulaPart.amount * numOfKeyNeeded;
                    }
                    else {
                        needs.add(new FormulaPart(formulaPart.amount * numOfKeyNeeded, formulaPart.name));
                    }
                }

                combineLikeTermsInNeeds(needs);
            }
        }

        return requiredBase;
    }

    private static void addToExcessAmounts(List<FormulaPart> excessAmounts, long excessGenerated, String name) {
        Optional<FormulaPart> excessOfNeed = excessAmounts.stream().filter(x -> x.name.equals(name)).findFirst();
        if(excessOfNeed.isPresent()) {
            excessOfNeed.get().amount += excessGenerated;
        } else {
            excessAmounts.add(new FormulaPart(excessGenerated, name));
        }
    }

    private static void combineLikeTermsInNeeds(List<FormulaPart> needs) {
        Map<String, Long> amountsNeeded = new HashMap<>();

        for(FormulaPart formulaPart : needs) {
            long amountCurrent = amountsNeeded.getOrDefault(formulaPart.name, 0L);
            amountsNeeded.put(formulaPart.name, amountCurrent + formulaPart.amount);
        }

        needs.clear();

        for(String string :  amountsNeeded.keySet()) {
            needs.add(new FormulaPart(amountsNeeded.get(string), string));
        }
    }

    private static FormulaPart findFormulaOutputFromNeed(FormulaPart need, Map<FormulaPart, List<FormulaPart>> formulas) {
        return formulas.keySet().stream().filter(fuelPart -> fuelPart.name.equals(need.name)).findFirst().get();
    }

    private static void addToFormulas(Map<FormulaPart, List<FormulaPart>> formulas, String nextLine) {
        FormulaPart outputFormula = createFormulaPartFromString(nextLine.substring(nextLine.indexOf(">") + 1));

        List<FormulaPart> formulaParts = new ArrayList<>();
        String[] inputs = nextLine.substring(0, nextLine.indexOf("=")).split(",");
        for(String input : inputs) {
            formulaParts.add(createFormulaPartFromString(input));
        }

        formulas.put(outputFormula, formulaParts);
    }

    private static FormulaPart createFormulaPartFromString(String string) {
        string = string.trim();
        return new FormulaPart(Long.parseLong(string.substring(0, string.indexOf(" "))), string.substring(string.indexOf(" ") + 1 ));
    }


    public static class FormulaPart {
        private String name;
        private long amount;

        public FormulaPart(long amount, String name) {
            this.name = name;
            this.amount = amount;
        }

        public String toString() {
            return amount + " " + name;
        }
    }
}

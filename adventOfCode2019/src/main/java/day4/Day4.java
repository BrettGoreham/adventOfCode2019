package day4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day4 {

    public static boolean exactlyADouble = true; //true for part 2 false for part one.

    public static void main(String[] args) {
        int minValue = 234208;
        int maxValue = 765869;

        List<Integer> validAnswers = new ArrayList<>();
        for (int i = minValue; i <= maxValue; i++) {
            if (validateNumber(i)) {
               validAnswers.add(i);
            }
        }

        System.out.println("Number of Valid Answers : " + validAnswers.size());
    }

    public static boolean validateNumber(int i) {
        Map<Character, Integer> numberCount = new HashMap<>();

        char lastNumber = ' ';
        for(char number : String.valueOf(i).toCharArray()) {

            if (number < lastNumber){
                return false;
            }

            numberCount.put(number, numberCount.getOrDefault(number, 0) + 1);
            lastNumber = number;
        }

        for(int numOfNumber : numberCount.values()) {
            if ((exactlyADouble && numOfNumber == 2) || (!exactlyADouble && numOfNumber >= 2)) {
                return true;
            }
        }

        return false;
    }
}

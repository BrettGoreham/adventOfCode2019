package day10;

import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day10 {

    private static String resourceDirectory = "src/main/resources/day10/";
    public static String inputFile = resourceDirectory + "day10Input.txt";
    public static String testFile = resourceDirectory + "day10TestInput.txt";
    public static String testFile2 = resourceDirectory + "day10TestInput2.txt";
    public static String testFile3 = resourceDirectory + "day10TestInput3.txt";
    public static String testFile4 = resourceDirectory + "day10TestInput4.txt";
    public static String testFile5 = resourceDirectory + "day10TestInput5.txt";
    public static String testFile6 = resourceDirectory + "day10TestInputPart2.txt";

    public static boolean debugMode = false;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(new FileReader(inputFile));
        List<Asteroid> asteroids = getAsteroidFromInput(scanner);


        for (Asteroid asteroid : asteroids) {
            asteroid.populateAsteroidSlopeChart(asteroids);
        }

        Asteroid maxSightLines = asteroids.stream().max(Comparator.comparing(Asteroid::getSightLinesToOtherAsteroids)).get();

        System.out.println("Asteroid " + maxSightLines.toString() + " has the most sight lines with : " + maxSightLines.sightLinesToOtherAsteroids); //part1
        List<Asteroid> destroyedAsteroids = maxSightLines.destroyAllAsteroidsInSlopeMapInOrder(debugMode);

        System.out.println("Asteroid " + destroyedAsteroids.get(200 - 1) + " was the 200th to be destroyed" ); //part2
    }

    private static List<Asteroid> getAsteroidFromInput(Scanner scanner){
        List<Asteroid> asteroids = new ArrayList<>();
        int yCount = 0;

        List<Integer> lineLengths = new ArrayList<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            int xCount = 0;
            for (Character character : line.toCharArray())  {
                if (character == '#') {
                    asteroids.add(new Asteroid(xCount,yCount));
                }
                xCount++;
            }

            lineLengths.add(line.length());
            yCount++;
        }

        assertLineLengthsAreAllTheSame(lineLengths);
        return asteroids;
    }

    private static void assertLineLengthsAreAllTheSame(List<Integer> lineLengths) {
        int firstLineLength = lineLengths.get(0);

        for(int i = 1; i < lineLengths.size(); i++) {
            if(firstLineLength != lineLengths.get(i)) {
                throw new RuntimeException("Input lines are not a rectangle.");
            }
        }
    }

    public static class Asteroid {
        private int xCord;
        private int yCord;
        private Map<Double, List<Asteroid>> asteroidSlopeMap;
        private int sightLinesToOtherAsteroids;

        Asteroid(int xCord, int yCord) {
            this.xCord = xCord;
            this.yCord = yCord;
        }

        int getSightLinesToOtherAsteroids() {
            return sightLinesToOtherAsteroids;
        }

        void populateAsteroidSlopeChart(List<Asteroid> asteroids) {
            asteroidSlopeMap = new HashMap<>();

            for(Asteroid asteroid : asteroids) {
                if(!this.equals(asteroid)){
                    double slope = ((double) (this.yCord - asteroid.yCord)) / (this.xCord - asteroid.xCord);
                    if(slope == Double.POSITIVE_INFINITY) {
                        slope = Double.NEGATIVE_INFINITY; //this doesnt matter but helps for part 2
                    }
                    List<Asteroid> slopeList = asteroidSlopeMap.getOrDefault(slope, new ArrayList<>());
                    slopeList.add(asteroid);
                    asteroidSlopeMap.putIfAbsent(slope, slopeList);
                }
            }

            for(List<Asteroid> slopeList : asteroidSlopeMap.values()) {
                slopeList.add(this); //this is important to figure out which ones in this slope the asteroid can find.
                sortSlopList(slopeList);
            }

            sightLinesToOtherAsteroids = findSightLinesToOtherAsteroids();
        }

        private void sortSlopList(List<Asteroid> slopList){
            slopList.sort((o1, o2) -> {
                if(o1.xCord != o2.xCord) {
                    return Integer.compare(o1.xCord, o2.xCord);
                }
                else {
                    return Integer.compare(o1.yCord, o2.yCord);
                }
            });
        }

        private int findSightLinesToOtherAsteroids(){
            int countOfAsteroidsThatCanBeSeen = 0;
            for(List<Asteroid> asteroids : asteroidSlopeMap.values()) {
                int indexOfThisInSlopeList = asteroids.indexOf(this);
                if(indexOfThisInSlopeList > 0) {
                    countOfAsteroidsThatCanBeSeen++;
                }
                if(indexOfThisInSlopeList < asteroids.size() -1) {
                    countOfAsteroidsThatCanBeSeen++;
                }
            }
            return countOfAsteroidsThatCanBeSeen;
        }

        private List<Asteroid> destroyAllAsteroidsInSlopeMapInOrder(boolean debugMode) {
            List<Asteroid> removedSet = new ArrayList<>();
            List<Double> keySet = asteroidSlopeMap.keySet().stream().sorted(Comparator.comparingDouble(o -> o)).collect(Collectors.toList()); // up in problem text is - infinity :D straight right is 0.

            int destructionNum = 1;
            boolean belowMode = false;
            boolean done = false;
            while (!done) {
                done = true;

                for(double key : keySet) {
                    List<Asteroid> asteroidsInSlope = asteroidSlopeMap.get(key);
                    int thisIndex = asteroidsInSlope.indexOf(this);

                    if(belowMode) {
                        if(thisIndex != 0) {
                            Asteroid removed = asteroidsInSlope.remove(thisIndex - 1);
                            printDestroyedAsteroid(removed, destructionNum++, debugMode);
                            removedSet.add(removed);
                        }
                    }
                    else {
                        if(thisIndex != asteroidsInSlope.size() - 1) {
                            Asteroid removed = asteroidsInSlope.remove(thisIndex + 1);
                            printDestroyedAsteroid(removed, destructionNum++, debugMode);
                            removedSet.add(removed);
                        }
                    }
                    if(asteroidsInSlope.size() > 1) {
                        done = false;
                    }
                }

                belowMode = !belowMode;
            }
            return removedSet;
        }

        private void printDestroyedAsteroid(Asteroid remove, int i, boolean debugMode) {
            if (debugMode) {
                System.out.println("Asteroid " + remove.toString() + "it was number " + i + " To Be Destroyed");
            }
        }

        @Override
        public boolean equals(Object obj) {
            Asteroid other = (Asteroid) obj;
            return this.xCord == other.xCord && this.yCord == other.yCord;
        }

        @Override
        public String toString(){
            return "(" + xCord + "," + yCord + ")";
        }
    }
}

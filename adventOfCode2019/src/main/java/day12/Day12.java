package day12;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day12 {

    private static String resourceDirectory = "src/main/resources/day12/";
    public static String inputFile = resourceDirectory + "day12Input.txt";
    public static String testFile = resourceDirectory + "day12TestInput.txt";
    public static String testFile2 = resourceDirectory + "day12TestInput2.txt";


    public static void main(String[] args) throws Exception{
        System.out.println("Starting Part 1:");
        findPart1();

        System.out.println("\nStarting Part 2: ");
        findPart2();
    }

    private static List<Moon> getMoonsFromFile() throws FileNotFoundException{
        List<Moon> moonList = new ArrayList<>();
        Scanner scanner = new Scanner(new FileReader(inputFile));

        while (scanner.hasNext()) {
            moonList.add(getMoonFromLine(scanner.nextLine()));
        }
        return moonList;
    }

    private static void compareAllMoonsToEachOtherAndUpdate(List<Moon> moons) {
        for(int i = 0; i < moons.size() ; i++ ) {
            Moon toCompare = moons.get(i);

            for (int j = i+1; j < moons.size(); j++) {
                toCompare.updateMoonsVelocityComparatively(moons.get(j));
            }

            toCompare.updateMoonsPositionBasedOnVelocity();
        }
    }

    private static void findPart1() throws FileNotFoundException {
        List<Moon> moonList = getMoonsFromFile();
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("How Many Rounds Shall we go (Part 1)? :");
        int rounds = Integer.valueOf(inputScanner.nextLine());

        for(int i = 0; i < rounds; i++) {
            compareAllMoonsToEachOtherAndUpdate(moonList);
        }

        System.out.println("\nPart 1 Results: ");
        System.out.println("total energy in system after " + rounds + "rounds is : " + moonList.stream().map(Moon::getTotalEnergyOfMoon).reduce(0, Integer::sum));
    }

    private static void findPart2() throws FileNotFoundException {
        List<Moon> moonList = getMoonsFromFile();

        List<Integer> initialXState = moonList.stream().map(moon -> moon.position.x).collect(Collectors.toUnmodifiableList());
        List<Integer> initialXVeloState = moonList.stream().map(moon -> moon.xVelocity).collect(Collectors.toUnmodifiableList());

        List<Integer> initialYState = moonList.stream().map(moon -> moon.position.y).collect(Collectors.toUnmodifiableList());
        List<Integer> initialYVeloState = moonList.stream().map(moon -> moon.yVelocity).collect(Collectors.toUnmodifiableList());

        List<Integer> initialZState = moonList.stream().map(moon -> moon.position.z).collect(Collectors.toUnmodifiableList());
        List<Integer> initialZVeloState = moonList.stream().map(moon -> moon.zVelocity).collect(Collectors.toUnmodifiableList());

        long round = 0;
        long xCycleFreq = -1;
        long yCycleFreq = -1;
        long zCycleFreq = -1;

        boolean cyclesFound = false;
        while(!cyclesFound) {
            round++;
            compareAllMoonsToEachOtherAndUpdate(moonList);

            if(xCycleFreq == -1) {
                xCycleFreq = backToInitialSet(
                    initialXState,
                    initialXVeloState,
                    moonList.stream().map(moon -> moon.position.x).collect(Collectors.toUnmodifiableList()),
                    moonList.stream().map(moon -> moon.xVelocity).collect(Collectors.toUnmodifiableList()),
                    round
                );
            }
            if(yCycleFreq == -1) {
                yCycleFreq = backToInitialSet(
                    initialYState,
                    initialYVeloState,
                    moonList.stream().map(moon -> moon.position.y).collect(Collectors.toUnmodifiableList()),
                    moonList.stream().map(moon -> moon.yVelocity).collect(Collectors.toUnmodifiableList()),
                    round
                );
            }
            if(zCycleFreq == -1) {
                zCycleFreq = backToInitialSet(
                    initialZState,
                    initialZVeloState,
                    moonList.stream().map(moon -> moon.position.z).collect(Collectors.toUnmodifiableList()),
                    moonList.stream().map(moon -> moon.zVelocity).collect(Collectors.toUnmodifiableList()),
                    round
                );
            }

            if (xCycleFreq != -1 && yCycleFreq != -1 && zCycleFreq != -1) {
                cyclesFound = true;
            }
        }

        System.out.println("\nPart 2 Results :");
        System.out.println("Cycles found : x,y,z : " + xCycleFreq + ", " + yCycleFreq + ", " + zCycleFreq);
        System.out.println("When will the moons align again: " + calcLeastCommonMultiple(xCycleFreq,yCycleFreq,zCycleFreq) );
    }

    private static long backToInitialSet(List<Integer> initialSet, List<Integer> initialSetVelo, List<Integer> currentSet, List<Integer> currentSetVelo, long round) {
        if(initialSet.equals(currentSet) && currentSetVelo.equals(initialSetVelo)) {
            return round;
        }
        return -1;
    }

    private static long calcLeastCommonMultiple(Long... values) {
        long val = values[0];
        for(int i = 1; i < values.length; i++) {
            val =  (val * values[i]) / calcGreatestCommonDivisor(val, values[i]);
        }
        return val;
    }

    private static long calcGreatestCommonDivisor(long x, long y) {
        if (x == 0 || y== 0) {
            return x + y ;
        } else {
            long biggerValue = Math.max(x, y);
            long smallerValue = Math.min(x, y);
            return calcGreatestCommonDivisor(biggerValue % smallerValue, smallerValue);
        }
    }

    public static class Moon {
        Position position;
        int xVelocity = 0;
        int yVelocity = 0;
        int zVelocity = 0;

        Moon(Position position) {
            this.position = position;
        }

        void updateMoonsPositionBasedOnVelocity() {
            position.updatePosition(xVelocity, yVelocity, zVelocity);
        }

        void updateMoonsVelocityComparatively(Moon otherMoon) {
            updateXVelocities(otherMoon);
            updateYVelocities(otherMoon);
            updateZVelocities(otherMoon);
        }

        int getTotalEnergyOfMoon() {
            return getPotentialEnergy() * getKineticEnergy();
        }

        private int getPotentialEnergy() {
            return Math.abs(position.x) + Math.abs(position.y) +  Math.abs(position.z);
        }

        private int getKineticEnergy() {
            return Math.abs(xVelocity) + Math.abs(yVelocity) + Math.abs(zVelocity);
        }

        private void updateXVelocities(Moon otherMoon) {
            if(this.position.x > otherMoon.position.x) {
                this.xVelocity--;
                otherMoon.xVelocity++;

            } else if (this.position.x < otherMoon.position.x ) {
                this.xVelocity++;
                otherMoon.xVelocity--;
            }
        }

        private void updateYVelocities(Moon otherMoon) {
            if(this.position.y > otherMoon.position.y) {
                this.yVelocity--;
                otherMoon.yVelocity++;

            } else if (this.position.y < otherMoon.position.y ) {
                this.yVelocity++;
                otherMoon.yVelocity--;
            }
        }

        private void updateZVelocities(Moon otherMoon) {
            if(this.position.z > otherMoon.position.z) {
                this.zVelocity--;
                otherMoon.zVelocity++;

            } else if (this.position.z < otherMoon.position.z ) {
                this.zVelocity++;
                otherMoon.zVelocity--;
            }
        }

        public String toString(){
            return "Moon at: " + position.toString() +
                " velocitys: x:" + xVelocity + ", y" + yVelocity + ", z" + zVelocity;
        }
    }

    public static class Position {
        int x;
        int y;
        int z;

        Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        void updatePosition(int addX, int addY, int addZ) {
            x += addX;
            y += addY;
            z += addZ;
        }

        public String toString() {
            return "(" + x + "," + y + "," + z + ")";
        }
    }

    private static Moon getMoonFromLine(String nextLine) {
        String coords = nextLine.substring(1, nextLine.length() -1).replace(" ", "");// remove < and >
        String[] positions = coords.split(","); // get three x y and z split
        Position position = new Position(
            Integer.valueOf(positions[0].substring(2)),
            Integer.valueOf(positions[1].substring(2)),
            Integer.valueOf(positions[2].substring(2))
        );

        return  new Moon(position);
    }
}

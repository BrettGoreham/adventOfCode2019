package day13;

import intcodeComputer.IntcodeComputer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Day13 {

    private static String resourceDirectory = "src/main/resources/day13/";
    public static String inputFilePart1 = resourceDirectory + "day13InputPart1.txt";
    public static String inputFilePart2 = resourceDirectory + "day13InputPart2.txt";

    public static void main(String[] args) throws Exception{

        String instructionSet = new Scanner(new FileReader(inputFilePart1)).nextLine();
        IntcodeComputer computer = new IntcodeComputer(instructionSet);

        System.out.println(computer.runProgram());

        Map<Position, Tile> board = new HashMap<>();
        addTilesToBoard(computer.getOutputs(), board, null);
        System.out.println(board);

        System.out.println("num of blocks : " +  board.values().stream().filter(x -> x == Tile.BLOCK).count());

        doPartTwo();
    }

    private static void doPartTwo() throws FileNotFoundException {
        String instructionSet = new Scanner(new FileReader(inputFilePart2)).nextLine();
        IntcodeComputer computer = new IntcodeComputer(instructionSet);

        boolean notDone = true;
        ScoreBoard scoreBoard = new ScoreBoard();
        Map<Position, Tile> board = new HashMap<>();
        while(notDone) {
            String exitCode = computer.runProgram();
            addTilesToBoard(computer.getOutputs(), board, scoreBoard);

            boolean blocksAllBroken = board.values().stream().noneMatch(x -> x == Tile.BLOCK);
            boolean systemExit = exitCode.equals("EXITED");

            if(systemExit && !blocksAllBroken) {
                throw new RuntimeException("EXITED BUT NOT BROKEN");
            }
            else if (blocksAllBroken) {
                notDone = false;
            }
            else {
                computer.addInput(findInputForComputer(board));
            }
        }

        System.out.println("Final Score After All blocks Are Broken: " + scoreBoard.getScore());

    }

    private static long findInputForComputer(Map<Position, Tile> board) {
        long paddleXPosition = board.entrySet().stream().filter(positionTileEntry -> positionTileEntry.getValue() == Tile.HORIZONTAL_PADDLE).map(entry -> entry.getKey().x).findFirst().get();
        long ballXPosition = board.entrySet().stream().filter(positionTileEntry -> positionTileEntry.getValue() == Tile.BALL).map(entry -> entry.getKey().x).findFirst().get();

        if(paddleXPosition < ballXPosition) {
            return 1;
        }
        else if (paddleXPosition > ballXPosition){
            return -1;
        }
        else {
            return 0;
        }
    }


    public static void addTilesToBoard(Queue<Long> tiles, Map<Position, Tile> board, ScoreBoard scoreBoard) {
        while(tiles.size() > 0) {
            Position position = new Position(tiles.remove(), tiles.remove());
            if(position.x != -1) {
                Tile tile = Tile.getFromId(tiles.remove());
                board.put(position, tile);
            } else {
                scoreBoard.setScore(tiles.remove());
            }
        }
    }

    public static class ScoreBoard{
        long score = 0;
        public void setScore(long score) {
            this.score = score;
        }

        public long getScore() {
            return score;
        }

    }

    public enum Tile{
        EMPTY(0),
        WALL(1),
        BLOCK(2),
        HORIZONTAL_PADDLE(3),
        BALL(4);

        private long id;
        Tile(long id) {
            this.id = id;
        }

        public static Tile getFromId(long idToGet){
            return Arrays.stream(Tile.values()).filter(x -> x.id == idToGet).findFirst().get();
        }

    }

    public static class Position {
        long x;
        long y;

        Position(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            Position other = (Position) obj;
            return this.x == other.x && this.y == other.y;
        }

        @Override
        public int hashCode() {
            Integer integer = Math.toIntExact(x + y);
            return integer;
        }

        public String toString(){
            return "(" + x + "," + y + ")";
        }
    }
}

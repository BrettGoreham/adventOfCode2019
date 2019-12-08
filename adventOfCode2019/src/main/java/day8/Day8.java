package day8;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day8 {

    private static String resourceDirectory = "src/main/resources/day8/";
    public static String inputFile = resourceDirectory + "day8Input.txt";
    public static String testFile = resourceDirectory + "day8Test.txt";

    public static void main(String[] args) throws Exception {
        int rows = 6;
        int columns = 25;

        String pictureData = new Scanner(new FileReader(inputFile)).nextLine();
        List<Integer> picturePixelList = splitToNChar(pictureData, 1).stream().map(Integer::parseInt).collect(Collectors.toList());

        Picture picture = new Picture(picturePixelList, rows, columns);

        int part1Solution = picture.findNumberOfXPixelsMultipliedByNumberOfYPixelsForLayerWithMinimumZPixels(1, 2, 0);
        System.out.println("Part 1: " + part1Solution);

        System.out.println("\nPart 2 Picture Starting: \n");
        picture.printOutPictureOfRealizedLayer();
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

    public static class Picture {

        private List<Layer> layers;
        private int rows;
        private int columns;

        Picture(List<Integer> picture, int rows, int columns){
            layers = new ArrayList<>();
            this.rows = rows;
            this.columns = columns;

            int layerInfoSize = rows * columns;
            int count = 0;
            while (count < picture.size()) {
                Layer layer = new Layer(picture.subList(count, count + layerInfoSize), rows, columns);
                count += layerInfoSize;

                layers.add(layer);
            }
        }

        int findNumberOfXPixelsMultipliedByNumberOfYPixelsForLayerWithMinimumZPixels(int x, int y, int z){
            Layer layerToCalc = findLayerWithMinimumXPixels(z);

            return layerToCalc.findNumberOfXPixelsInLayer(x) * layerToCalc.findNumberOfXPixelsInLayer(y);
        }

        Layer findLayerWithMinimumXPixels(int xPixel){
            return layers.stream()
                .min(Comparator.comparingInt(o -> o.findNumberOfXPixelsInLayer(xPixel))).get();
        }

        void printOutPictureOfRealizedLayer() {
            Layer realizedLayer = findResultingLayerBasedOnFirstNonTransparentInPosition();

            for(int row = 0; row<rows; row++) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int column = 0; column < columns; column++) {
                    int pixel = realizedLayer.findPixelInLayerAt(row, column);

                    if (pixel == 0) {
                        stringBuilder.append("⬛");
                    }
                    else if (pixel == 1) {
                        stringBuilder.append("⬜");
                    }
                    else {
                        stringBuilder.append("▁"); //This just looks the same width to me. didnt matter those as realized layer had no 2s
                    }
                }
                System.out.println(stringBuilder.toString());
            }
            createImageFileFromLayer(realizedLayer);
        }

        Layer findResultingLayerBasedOnFirstNonTransparentInPosition() {
            List<Integer> realizedLayerInfo = new ArrayList<>();

            for(int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    int lambdaRow =row;
                    int lambdaColumn = column;
                    List<Integer> layersValues = layers.stream().map(x -> x.findPixelInLayerAt(lambdaRow, lambdaColumn)).collect(Collectors.toList());

                    realizedLayerInfo.add(findFirstNon2(layersValues));
                }
            }

            return new Layer(realizedLayerInfo, rows, columns);
        }

        void createImageFileFromLayer(Layer layer) {
            BufferedImage image = new BufferedImage(layer.columns, layer.rows, BufferedImage.TYPE_BYTE_GRAY );

            for(int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    image.setRGB(column, row, getPixelARGBValue(layer.findPixelInLayerAt(row, column)));
                }
            }
            try {
                ImageIO.write(image, "jpg", new File(resourceDirectory + "/day8OutputImage.jpg"));
                System.out.println("Image Created And put in the Day 8 resource folder");
            }
            catch (Exception e) {
                System.out.println("imageFailedToBeCreated");
            }
        }

        int getPixelARGBValue(int value) {

            if (value == 0) {
                return Color.BLACK.getRGB();
            }
            else if (value == 1) {
                return Color.WHITE.getRGB();
            }
            else {
                return Color.TRANSLUCENT;
            }
        }

        //2 is transparent so first non 2 is the one that is displayed.
        private int findFirstNon2(List<Integer> values){
            for(int i : values) {
                if(i != 2) {
                    return i;
                }
            }
            return 2;
        }

        private class Layer {
            private int rows;
            private int columns;
            int[][] pixelArrays;

            Layer(List<Integer> layerInfo, int rows, int columns) {
                pixelArrays = new int[rows][columns];
                this.rows = rows;
                this.columns = columns;

                int pixelCount = 0;
                for(int row = 0; row < rows; row++) {
                    for (int column = 0; column < columns; column++) {
                        pixelArrays[row][column] = layerInfo.get(pixelCount++);
                    }
                }
            }

            int findNumberOfXPixelsInLayer(int pixelToFind) {
                int count = 0;
                for(int row = 0; row < rows; row++) {
                    for (int column = 0; column < columns; column++) {
                        if (pixelArrays[row][column] == pixelToFind) {
                            count++;
                        }
                    }
                }
                return count;
            }

            int findPixelInLayerAt(int row, int column) {
                return pixelArrays[row][column];
            }
        }
    }
}
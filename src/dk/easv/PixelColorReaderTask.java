package dk.easv;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PixelColorReaderTask extends Task<Image> {
    private Image image;
    private int redPixels;
    private int greenPixels;
    private int bluePixels;

    public PixelColorReaderTask(Image image) {
        this.image = image;
        redPixels = 0;
        greenPixels = 0;
        bluePixels = 0;
    }

    @Override
    protected Image call() throws Exception {
        loopOverPixels();

        updateMessage("Red pixels: " + redPixels + " Green pixels: " + greenPixels + " Blue pixels: " + bluePixels);

        return image;
    }

    private void loopOverPixels() {
        for (int x = 0; x < image.getWidth(); x++) {

            for (int y = 0; y < image.getHeight(); y++) {

                 Color pixelColor = image.getPixelReader().getColor(x, y);

                 countPixelColor(pixelColor);
            }
        }
    }

    private void countPixelColor(Color color) {
        String red = "red";
        String green = "green";
        String blue = "blue";

        Map<String, Double> rgbMap = new HashMap<>();
        rgbMap.put(red, color.getRed());
        rgbMap.put(green, color.getGreen());
        rgbMap.put(blue, color.getBlue());

        List<Double> highestColors = new ArrayList<>();
        highestColors.add(color.getRed());
        for (String key : rgbMap.keySet()) {

            double keyValue = rgbMap.get(key);

            if (highestColors.get(0) < keyValue) {

                highestColors.clear();
                highestColors.add(keyValue);
            }
            else if (highestColors.get(0) == keyValue) {
                highestColors.add(keyValue);
            }
        }

        for (double highestColor : highestColors) {
            if (highestColor == rgbMap.get(red)) {
                redPixels++;
                break;
            }

            if (highestColor == rgbMap.get(green)) {
                greenPixels++;
                break;
            }

            if (highestColor == rgbMap.get(blue)) {
                bluePixels++;
            }
        }
    }
}

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * The ImageEditor class applies various image processing techniques to an image,
 * including blurring (to get the smudged/bleeding effect), adding circular blurs, and adding grass-like cracks.
 */
public class ImageEditor {

    /**
     * The main method that executes the image processing tasks.
     *
     * @param args command-line arguments (not used).
     * @throws IOException if an error occurs during image processing.
     */
    public static void main(String[] args) { // Throwing exceptions caused by read and write methods
        /*
        Where I learned about exceptions: 
        https://www.youtube.com/watch?v=1XAfapkBQjk
        https://www.youtube.com/watch?v=_nmm0nZqIIY
        */

        // Load the original image (of type BufferedImage)
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(new File("inputRainyNightImage.png"));
        } catch (IOException e) {
            System.out.println("Image cannot be found.");
            return;
        }

        // Apply a strong blur to the entire image to create the base for the "bleeding" effect
        BufferedImage blurredImage = applyStrongBlur(originalImage);

        // Add progressively darker circular blurs across the image in a row-by-row manner
        addGradualCircularBlurs(blurredImage, 30, 20); // Adjust grid size and circle size as needed

        // Add grass-like cracks randomly across the image, now in dark red
        addGrassLikeCracks(blurredImage, 50); // Adjust the number of cracks as needed

        // Save the edited image to the name EDITEDinputRainyNightImage in my downloads folder
        try {
            ImageIO.write(blurredImage, "png", new File("EDITEDinputRainyNightImage.png"));
        } catch (Exception e) {
            System.out.println("Image cannot be downloaded.");
        }
    }

    /**
     * Applies a strong blur filter across the whole image.
     *
     * @param image the original BufferedImage to be blurred.
     * @return a new BufferedImage that has been blurred.
     */
    private static BufferedImage applyStrongBlur(BufferedImage image) {
        // 7x7 kernel for a stronger blur effect, creating a smoother background
        // Source where I discovered blurring an image using kernels: https://stackoverflow.com/questions/77781880/how-to-use-convolution-kernel-to-blur-image 
        float[] blurKernel = new float[49];
        for (int i = 0; i < blurKernel.length; i++) {
            blurKernel[i] = 1.0f / blurKernel.length;
        }

        BufferedImageOp blurOp = new ConvolveOp(new Kernel(7, 7, blurKernel));
        return blurOp.filter(image, null);
    }

    /**
     * Adds circular blurs in a grid pattern recursively, with each blur darker and more opaque than the last.
     *
     * @param image      the BufferedImage to draw the circular blurs onto.
     * @param gridSize   the distance between the centers of the circles in the grid.
     * @param circleSize the diameter of each circular blur.
     */
    private static void addGradualCircularBlurs(BufferedImage image, int gridSize, int circleSize) {
        Graphics2D g2d = image.createGraphics();

        // Define the initial color and transparency settings
        int initialAlpha = 20;   // The starting alpha, making the top-left blur nearly transparent
        int initialRed = 255;    // Start with a bright red color
        int initialGreen = 140;  // A moderate green for a red-pink starting color
        int initialBlue = 140;   // A constant blue value to maintain the red-pink tone
        int alphaIncrement = 5;  // Increment for alpha, gradually making each blur more opaque
        int colorDecrement = 5;  // Decrement for red and green to darken each successive blur

        // Start the recursive function
        drawCircularBlurs(g2d, image, 0, 0, initialAlpha, initialRed, initialGreen, initialBlue, gridSize, circleSize,
                alphaIncrement, colorDecrement);

        // Clean up graphics object after drawing is complete
        g2d.dispose();
    }

    /**
     * Recursively draws circular blurs across the image.
     *
     * @param g2d            the Graphics2D object used for drawing.
     * @param image          the BufferedImage to draw onto.
     * @param x              the current x-coordinate.
     * @param y              the current y-coordinate.
     * @param alpha          the current alpha value.
     * @param red            the current red color value.
     * @param green          the current green color value.
     * @param blue           the current blue color value.
     * @param gridSize       the distance between the centers of the circles in the grid.
     * @param circleSize     the diameter of each circular blur.
     * @param alphaIncrement the increment for the alpha value.
     * @param colorDecrement the decrement for the red and green color values.
     */
    private static void drawCircularBlurs(Graphics2D g2d, BufferedImage image, int x, int y, int alpha, int red,
            int green, int blue, int gridSize, int circleSize, int alphaIncrement, int colorDecrement) {
        // Base case for recursion
        // if the y value >= image height, then stop drawing circular blurs
        if (y >= image.getHeight()) {
            return;
        }

        // If x >= image width, move to next row
        if (x >= image.getWidth()) {
            // At the end of each row, reset alpha and color for the next row
            int rowNum = y / gridSize;
            alpha = Math.min(255, 20 + (rowNum) * alphaIncrement); // Reset alpha with row progression
            red = Math.max(0, 255 - (rowNum) * colorDecrement);     // Reset red progressively for each row
            green = Math.max(0, 140 - (rowNum) * colorDecrement);   // Reset green progressively for each row
            // Recurse with x = 0, y + gridSize
            // This is recursion! I made the method call itself every time a new row is beginning
            drawCircularBlurs(g2d, image, 0, y + gridSize, alpha, red, green, blue, gridSize, circleSize,
                    alphaIncrement, colorDecrement);
            return;
        }

        // Set the color for the current circular blur using the calculated alpha and color values
        Color color = new Color(Math.max(0, red), Math.max(0, green), Math.max(0, blue), Math.min(255, alpha));
        g2d.setColor(color);

        // Draw the circular blur centered at (x, y)
        g2d.fillOval(x - circleSize / 2, y - circleSize / 2, circleSize, circleSize);

        // Update alpha, red, and green values for the next blur in this row
        alpha = Math.min(255, alpha + alphaIncrement); // Ensure alpha doesn’t exceed 255
        red = Math.max(0, red - colorDecrement);       // Darken red gradually toward black
        green = Math.max(0, green - colorDecrement);   // Darken green gradually toward black

        // Recurse with x + gridSize (gridSize is the increment by which I move in the x and y direction when drawing a new row or new blur)
        drawCircularBlurs(g2d, image, x + gridSize, y, alpha, red, green, blue, gridSize, circleSize,
                alphaIncrement, colorDecrement);
    }

    /**
     * Adds grass-like cracks randomly across the image.
     *
     * @param image          the BufferedImage to draw the cracks onto.
     * @param numberOfCracks the number of cracks to generate.
     */
    private static void addGrassLikeCracks(BufferedImage image, int numberOfCracks) {
        Graphics2D g2d = image.createGraphics();

        // Set color and stroke for cracks
        g2d.setColor(new Color(139, 0, 0, 200)); // Dark red color, semi-transparent
        g2d.setStroke(new BasicStroke(1));       // Thin line

        // For each crack
        for (int i = 0; i < numberOfCracks; i++) {
            // Random starting point
            int startX = (int) (Math.random() * image.getWidth());
            int startY = (int) (Math.random() * image.getHeight());

            // Setting a random initial direction for each crack
            double angle = Math.random() * 2 * Math.PI; // Creating a random angle in radians to set the random direction for each crack

            // Creating the length of the crack
            int length = 50 + (int) (Math.random() * 50); // Random length between 50 and 100 pixels

            drawCrack(g2d, startX, startY, angle, length, 0, image.getWidth(), image.getHeight());
        }

        g2d.dispose();
    }

    /**
     * Recursively draws a single crack, allowing for branching.
     *
     * @param g2d    the Graphics2D object used for drawing.
     * @param x      the current x-coordinate.
     * @param y      the current y-coordinate.
     * @param angle  the current direction in radians.
     * @param length the remaining length of the crack.
     * @param depth  the current depth of recursion.
     * @param width  the width of the image to keep drawing within bounds.
     * @param height the height of the image to keep drawing within bounds.
     */
    private static void drawCrack(Graphics2D g2d, int x, int y, double angle, int length, int depth, int width,
            int height) {
        // base case for my recursion
        // the recursion stops when either the length of the crack segment is less than or equal to 0 OR the depth exceeds 5 (the program calls itself 5 times) 
        if (length <= 0 || depth > 5) // stopping the depth at 5 prevents a crack from branching infinitely
            return; 

        // Calculating the end point of the crack
        int dx = (int) (Math.cos(angle) * length);
        int dy = (int) (Math.sin(angle) * length);
        int x2 = x + dx;
        int y2 = y + dy;

        // Check if the line is within image boundaries
        if (x2 < 0 || x2 >= width || y2 < 0 || y2 >= height)
            return;

        // Draw line segment
        g2d.drawLine(x, y, x2, y2);

        // Randomly decide to branch
        if (Math.random() < 0.3 && depth < 5) { // giving the crack a 30% chance of branching either left or right
            // Branch to the left
            double newAngle1 = angle + (Math.random() * Math.PI / 4); // if branching occurs, the direction for the new branch is created by randomly changing the angle by a random number from -45 to +45 degrees
            int newLength1 = length / 2;
            drawCrack(g2d, x2, y2, newAngle1, newLength1, depth + 1, width, height);
        }
        if (Math.random() < 0.3 && depth < 5) {
            // Branch to the right
            double newAngle2 = angle - (Math.random() * Math.PI / 4);
            int newLength2 = length / 2;
            drawCrack(g2d, x2, y2, newAngle2, newLength2, depth + 1, width, height);
        }

        // Continue in the same direction
        double newAngle = angle + (Math.random() - 0.5) * (Math.PI / 8); // Slightly adjust angle
        int newLength = length - (int) (Math.random() * 10);
        drawCrack(g2d, x2, y2, newAngle, newLength, depth, width, height);
    }
}

# ImageEditor: Advanced Image Processing in Java

The `ImageEditor` class provides a set of image processing techniques applied to an image, including strong blurring for a smudged effect, circular blurs, and randomly generated grass-like cracks.

## Project Overview

This project uses Java’s `BufferedImage` class for image manipulation. The program loads an image, applies various effects, and then saves the edited image.

### Key Features

- **Strong Blur**: Applies a 7x7 convolution kernel for a strong blur effect, creating a smudged or bleeding appearance.
- **Circular Blurs**: Adds circular blurs in a grid pattern, with each blur progressively darker to create a layered effect.
- **Grass-Like Cracks**: Adds dark red cracks resembling grass or tree branches randomly across the image with recursive branching.

## Code Structure

### Main Class: `ImageEditor`

The `ImageEditor` class contains a `main` method, which loads an image, applies the effects, and saves the edited version.

#### Methods

- **applyStrongBlur**: Applies a 7x7 blur kernel to create a strong blur effect across the image.
- **addGradualCircularBlurs**: Adds circular blurs in a grid pattern across the image, with each successive blur appearing darker.
- **addGrassLikeCracks**: Adds randomized cracks resembling grass or tree branches across the image.

### Method Descriptions

1. **applyStrongBlur(BufferedImage image)**

   - Applies a 7x7 convolution kernel to produce a strong blur effect.
   - Utilizes `ConvolveOp` for image filtering.

2. **addGradualCircularBlurs(BufferedImage image, int gridSize, int circleSize)**

   - Adds circular blurs in a grid pattern.
   - Uses a recursive helper method, `drawCircularBlurs`, to adjust color and opacity for each blur in the grid.

3. **addGrassLikeCracks(BufferedImage image, int numberOfCracks)**

   - Adds random cracks to the image.
   - Uses recursive branching in `drawCrack` to vary crack length and direction.

### Example Code Snippet

The following snippet shows how to add circular blurs to the image:

```java
private static void addGradualCircularBlurs(BufferedImage image, int gridSize, int circleSize) {
    Graphics2D g2d = image.createGraphics();
    int initialAlpha = 20;
    int initialRed = 255;
    int initialGreen = 140;
    int initialBlue = 140;
    int alphaIncrement = 5;
    int colorDecrement = 5;

    drawCircularBlurs(g2d, image, 0, 0, initialAlpha, initialRed, initialGreen, initialBlue, gridSize, circleSize,
            alphaIncrement, colorDecrement);

    g2d.dispose();
}

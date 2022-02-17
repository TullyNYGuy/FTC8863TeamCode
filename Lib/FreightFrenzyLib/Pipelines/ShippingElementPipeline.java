/*
 * Copyright (c) 2020 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines;


import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyMatchInfo;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;


/*
 * This sample demonstrates a basic (but battle-tested and essentially
 * 100% accurate) method of detecting the skystone when lined up with
 * the sample regions over the first 3 stones.
 */


public class ShippingElementPipeline extends OpenCvPipeline {
    /*
     * An enum to define the shipping element position
     */
    public enum ShippingPosition {
        LEFT,
        CENTER,
        RIGHT,
        UNKNOWN
    }

    // default, change this is EOCVSim to test other modes
    private FreightFrenzyMatchInfo.StartLocation startLocation = FreightFrenzyMatchInfo.getStartLocation();

    /*
     * Some color constants
     */
    static final Scalar BLUE = new Scalar(0, 0, 255);
    static final Scalar GREEN = new Scalar(0, 255, 0);

    /*
     * The core values which define the location and size of the sample regions
     */
    static final Point REGION1_TOPLEFT_ANCHOR_POINT_BLUE_WALL = new Point(140, 390);
    static final Point REGION2_TOPLEFT_ANCHOR_POINT_BLUE_WALL = new Point(650, 390);
    static final Point REGION3_TOPLEFT_ANCHOR_POINT_BLUE_WALL = new Point(1100, 390);
    static final Point REGION1_TOPLEFT_ANCHOR_POINT_RED_WALL = new Point(160, 390);
    static final Point REGION2_TOPLEFT_ANCHOR_POINT_RED_WALL = new Point(590, 390);
    static final Point REGION3_TOPLEFT_ANCHOR_POINT_RED_WALL = new Point(1000, 390);
    static final Point REGION1_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE = new Point(1100, 390);
    static final Point REGION2_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE = new Point(1100, 390);
    static final Point REGION3_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE = new Point(1100, 390);
    static final Point REGION1_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE = new Point(220, 390);
    static final Point REGION2_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE = new Point(650, 390);
    static final Point REGION3_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE = new Point(1050, 390);

    static final int REGION_WIDTH = 50;
    static final int REGION_HEIGHT = 50;

    /*
     * Points which actually define the sample region rectangles, derived from above values
     *
     * Example of how points A and B work to define a rectangle
     *
     *   ------------------------------------
     *   | (0,0) Point A                    |
     *   |                                  |
     *   |                                  |
     *   |                                  |
     *   |                                  |
     *   |                                  |
     *   |                                  |
     *   |                  Point B (70,50) |
     *   ------------------------------------
     *
     */



        Point region1_pointA;
        Point region1_pointB;
        Point region2_pointA;
        Point region2_pointB;
        Point region3_pointA;
        Point region3_pointB;

    /*
     * Working variables
     */
    Mat region1_L, region2_L, region3_L;
    Mat Lab = new Mat();
    Mat L = new Mat();
    int avg1, avg2, avg3;

    // Volatile since accessed by OpMode thread w/o synchronization
    private volatile ShippingPosition position = ShippingPosition.UNKNOWN;

    /*
     * This function takes the RGB frame, converts to YCrCb,
     * and extracts the Cb channel to the 'Cb' variable
     */
    void inputToLab(Mat input) {
        Imgproc.cvtColor(input, Lab, Imgproc.COLOR_RGB2Lab);
        Core.extractChannel(Lab, L, 0);
    }

    @Override
    public void init(Mat firstFrame) {
        switch(PersistantStorage.getStartSpot()) {
            case BLUE_WAREHOUSE:
                region1_pointA = new Point(
                        REGION1_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.x,
                        REGION1_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.y);
                region1_pointB = new Point(
                        REGION1_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.x + REGION_WIDTH,
                        REGION1_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.y + REGION_HEIGHT);
                region2_pointA = new Point(
                        REGION2_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.x,
                        REGION2_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.y);
                region2_pointB = new Point(
                        REGION2_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.x + REGION_WIDTH,
                        REGION2_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.y + REGION_HEIGHT);
                region3_pointA = new Point(
                        REGION3_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.x,
                        REGION3_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.y);
                region3_pointB = new Point(
                        REGION3_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.x + REGION_WIDTH,
                        REGION3_TOPLEFT_ANCHOR_POINT_BLUE_WAREHOUSE.y + REGION_HEIGHT);
                break;
            case BLUE_WALL:
                region1_pointA = new Point(
                        REGION1_TOPLEFT_ANCHOR_POINT_BLUE_WALL.x,
                        REGION1_TOPLEFT_ANCHOR_POINT_BLUE_WALL.y);
                region1_pointB = new Point(
                        REGION1_TOPLEFT_ANCHOR_POINT_BLUE_WALL.x + REGION_WIDTH,
                        REGION1_TOPLEFT_ANCHOR_POINT_BLUE_WALL.y + REGION_HEIGHT);
                region2_pointA = new Point(
                        REGION2_TOPLEFT_ANCHOR_POINT_BLUE_WALL.x,
                        REGION2_TOPLEFT_ANCHOR_POINT_BLUE_WALL.y);
                region2_pointB = new Point(
                        REGION2_TOPLEFT_ANCHOR_POINT_BLUE_WALL.x + REGION_WIDTH,
                        REGION2_TOPLEFT_ANCHOR_POINT_BLUE_WALL.y + REGION_HEIGHT);
                region3_pointA = new Point(
                        REGION3_TOPLEFT_ANCHOR_POINT_BLUE_WALL.x,
                        REGION3_TOPLEFT_ANCHOR_POINT_BLUE_WALL.y);
                region3_pointB = new Point(
                        REGION3_TOPLEFT_ANCHOR_POINT_BLUE_WALL.x + REGION_WIDTH,
                        REGION3_TOPLEFT_ANCHOR_POINT_BLUE_WALL.y + REGION_HEIGHT);
                break;
            case RED_WAREHOUSE:
                region1_pointA = new Point(
                        REGION1_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.x,
                        REGION1_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.y);
                region1_pointB = new Point(
                        REGION1_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.x + REGION_WIDTH,
                        REGION1_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.y + REGION_HEIGHT);
                region2_pointA = new Point(
                        REGION2_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.x,
                        REGION2_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.y);
                region2_pointB = new Point(
                        REGION2_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.x + REGION_WIDTH,
                        REGION2_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.y + REGION_HEIGHT);
                region3_pointA = new Point(
                        REGION3_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.x,
                        REGION3_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.y);
                region3_pointB = new Point(
                        REGION3_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.x + REGION_WIDTH,
                        REGION3_TOPLEFT_ANCHOR_POINT_RED_WAREHOUSE.y + REGION_HEIGHT);
                break;
            case RED_WALL:
                region1_pointA = new Point(
                        REGION1_TOPLEFT_ANCHOR_POINT_RED_WALL.x,
                        REGION1_TOPLEFT_ANCHOR_POINT_RED_WALL.y);
                region1_pointB = new Point(
                        REGION1_TOPLEFT_ANCHOR_POINT_RED_WALL.x + REGION_WIDTH,
                        REGION1_TOPLEFT_ANCHOR_POINT_RED_WALL.y + REGION_HEIGHT);
                region2_pointA = new Point(
                        REGION2_TOPLEFT_ANCHOR_POINT_RED_WALL.x,
                        REGION2_TOPLEFT_ANCHOR_POINT_RED_WALL.y);
                region2_pointB = new Point(
                        REGION2_TOPLEFT_ANCHOR_POINT_RED_WALL.x + REGION_WIDTH,
                        REGION2_TOPLEFT_ANCHOR_POINT_RED_WALL.y + REGION_HEIGHT);
                region3_pointA = new Point(
                        REGION3_TOPLEFT_ANCHOR_POINT_RED_WALL.x,
                        REGION3_TOPLEFT_ANCHOR_POINT_RED_WALL.y);
                region3_pointB = new Point(
                        REGION3_TOPLEFT_ANCHOR_POINT_RED_WALL.x + REGION_WIDTH,
                        REGION3_TOPLEFT_ANCHOR_POINT_RED_WALL.y + REGION_HEIGHT);
                break;


        }
        /*
         * We need to call this in order to make sure the 'Cb'
         * object is initialized, so that the submats we make
         * will still be linked to it on subsequent frames. (If
         * the object were to only be initialized in processFrame,
         * then the submats would become delinked because the backing
         * buffer would be re-allocated the first time a real frame
         * was crunched)
         */
        inputToLab(firstFrame);

        /*
         * Submats are a persistent reference to a region of the parent
         * buffer. Any changes to the child affect the parent, and the
         * reverse also holds true.
         */
        region1_L = L.submat(new Rect(region1_pointA, region1_pointB));
        region2_L = L.submat(new Rect(region2_pointA, region2_pointB));
        region3_L = L.submat(new Rect(region3_pointA, region3_pointB));
    }

    @Override
    public Mat processFrame(Mat input) {
        /*
         * Overview of what we're doing:
         *
         * We first convert to YCrCb color space, from RGB color space.
         * Why do we do this? Well, in the RGB color space, chroma and
         * luma are intertwined. In YCrCb, chroma and luma are separated.
         * YCrCb is a 3-channel color space, just like RGB. YCrCb's 3 channels
         * are Y, the luma channel (which essentially just a B&W image), the
         * Cr channel, which records the difference from red, and the Cb channel,
         * which records the difference from blue. Because chroma and luma are
         * not related in YCrCb, vision code written to look for certain values
         * in the Cr/Cb channels will not be severely affected by differing
         * light intensity, since that difference would most likely just be
         * reflected in the Y channel.
         *
         * After we've converted to YCrCb, we extract just the 2nd channel, the
         * Cb channel. We do this because stones are bright yellow and contrast
         * STRONGLY on the Cb channel against everything else, including SkyStones
         * (because SkyStones have a black label).
         *
         * We then take the average pixel value of 3 different regions on that Cb
         * channel, one positioned over each stone. The brightest of the 3 regions
         * is where we assume the SkyStone to be, since the normal stones show up
         * extremely darkly.
         *
         * We also draw rectangles on the screen showing where the sample regions
         * are, as well as drawing a solid rectangle over top the sample region
         * we believe is on top of the SkyStone.
         *
         * In order for this whole process to work correctly, each sample region
         * should be positioned in the center of each of the first 3 stones, and
         * be small enough such that only the stone is sampled, and not any of the
         * surroundings.
         */

        /*
         * Get the Cb channel of the input frame after conversion to YCrCb
         */
        inputToLab(input);

        /*
         * Compute the average pixel value of each submat region. We're
         * taking the average of a single channel buffer, so the value
         * we need is at index 0. We could have also taken the average
         * pixel value of the 3-channel image, and referenced the value
         * at index 2 here.
         */
        avg1 = (int) Core.mean(region1_L).val[0];
        avg2 = (int) Core.mean(region2_L).val[0];
        avg3 = (int) Core.mean(region3_L).val[0];

        /*
         * Draw a rectangle showing sample region 1 on the screen.
         * Simply a visual aid. Serves no functional purpose.
         */
        Imgproc.rectangle(
                input, // Buffer to draw on
                region1_pointA, // First point which defines the rectangle
                region1_pointB, // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines

        /*
         * Draw a rectangle showing sample region 2 on the screen.
         * Simply a visual aid. Serves no functional purpose.
         */
        Imgproc.rectangle(
                input, // Buffer to draw on
                region2_pointA, // First point which defines the rectangle
                region2_pointB, // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines

        /*
         * Draw a rectangle showing sample region 3 on the screen.
         * Simply a visual aid. Serves no functional purpose.
         */
        Imgproc.rectangle(
                input, // Buffer to draw on
                region3_pointA, // First point which defines the rectangle
                region3_pointB, // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines


        /*
         * Find the max of the 3 averages
         */
        int minOneTwo = Math.min(avg1, avg2);
        int min = Math.min(minOneTwo, avg3);
        double maxL = 100;

        /*
         * Now that we found the max, we actually need to go and
         * figure out which sample region that value was from
         */
        if (min == avg1) // Was it from region 1?
        {
            position = ShippingPosition.LEFT; // Record our analysis

            /*
             * Draw a solid rectangle on top of the chosen region.
             * Simply a visual aid. Serves no functional purpose.
             */
            Imgproc.rectangle(
                    input, // Buffer to draw on
                    region1_pointA, // First point which defines the rectangle
                    region1_pointB, // Second point which defines the rectangle
                    GREEN, // The color the rectangle is drawn in
                    -1); // Negative thickness means solid fill
        } else if (min == avg2) // Was it from region 2?
        {
            position = ShippingPosition.CENTER; // Record our analysis

            /*
             * Draw a solid rectangle on top of the chosen region.
             * Simply a visual aid. Serves no functional purpose.
             */
            Imgproc.rectangle(
                    input, // Buffer to draw on
                    region2_pointA, // First point which defines the rectangle
                    region2_pointB, // Second point which defines the rectangle
                    GREEN, // The color the rectangle is drawn in
                    -1); // Negative thickness means solid fill
        } else if (min == avg3) // Was it from region 3?
        {
            position = ShippingPosition.RIGHT; // Record our analysis

            /*
             * Draw a solid rectangle on top of the chosen region.
             * Simply a visual aid. Serves no functional purpose.
             */
            Imgproc.rectangle(
                    input, // Buffer to draw on
                    region3_pointA, // First point which defines the rectangle
                    region3_pointB, // Second point which defines the rectangle
                    GREEN, // The color the rectangle is drawn in
                    -1); // Negative thickness means solid fill
        }

        /*
         * Render the 'input' buffer to the viewport. But note this is not
         * simply rendering the raw camera feed, because we called functions
         * to add some annotations to this buffer earlier up.
         */
        return input;
    }

    /*
     * Call this from the OpMode thread to obtain the latest analysis
     */
    public ShippingPosition getAnalysis() {
        return position;
    }
}


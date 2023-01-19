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

package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.Pipelines;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class SignalConePipeline extends OpenCvPipeline {
    /*
     * An enum to define the cone images (just colors really)
     */
    public enum ConeColor {
        RED,
        BLUE,
        GREEN,
        UNKNOWN
    }

    // / Volatile since accessed by OpMode thread w/o synchronization
    private volatile ConeColor coneColor = ConeColor.UNKNOWN;

    private enum StageToSendToViewport{
        RAW_IMAGE,
        BLUE_TEST,
        GREEN_TEST,
        RED_TEST
    }

    private StageToSendToViewport stageToRenderToViewport = StageToSendToViewport.RAW_IMAGE;

    /*
     * Some color constants
     */
    static final Scalar BLUE = new Scalar(0, 0, 255);
    static final Scalar GREEN = new Scalar(0, 255, 0);
    static final Scalar RED = new Scalar(255, 0, 0);

    /*
     * These are our variables that will be
     * modifiable from the variable tuner.
     *
     * Scalars in OpenCV are generally used to
     * represent color. So our values in the
     * lower and upper Scalars here represent
     * the Y, Cr and Cb values respectively.
     *
     * YCbCr, like most color spaces, range
     * from 0-255.
     */
    public Scalar filterForBlueLower = new Scalar(0, 0, 137);
    public Scalar filterForBlueUpper = new Scalar(255, 255, 255);

    public Scalar filterForGreenLower = new Scalar(0, 113, 122);
    public Scalar filterForGreenUpper = new Scalar(255, 147, 134);

    public Scalar filterForRedLower = new Scalar(0, 158, 104);
    public Scalar filterForRedUpper = new Scalar(255, 255, 255);

    /*
     * The core values which define the location and size of the sample region
     */
    // Since these values are public they will show up as input boxes in EOCV-Sim, allowing you to
    // adjust the values and move the rectangle around to where you want it. This is a way to easily
    // adjust your sample region, then grab the values for use in a real pipeline.
    public double rectangleXOrigin = 504;
    public double rectangleYOrigin = 178;
    public double rectangleWidth = 50;
    public double rectangleHeight = 100;

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
    private Point rectangleTopLeftPoint = new Point(rectangleXOrigin, rectangleYOrigin);
    private Point rectangleBottomRightPoint = new Point(
            rectangleTopLeftPoint.x + rectangleWidth,
            rectangleTopLeftPoint.y + rectangleHeight);
    private Rect croppingArea = new Rect(rectangleTopLeftPoint, rectangleBottomRightPoint);

    /*
     * Working variables
     */
    private Telemetry telemetry = null;

    // Volatile since accessed by OpMode thread w/o synchronization
    private volatile ConeColor position = ConeColor.UNKNOWN;

    private Mat ycrcbMat = new Mat();
    private Mat testForBlueMat = new Mat();
    private Mat testForGreenMat = new Mat();
    private Mat testForRedMat = new Mat();
    private Mat croppedTestForBlueMat = new Mat();
    private Mat croppedTestForGreenMat = new Mat();
    private Mat croppedTestForRedMat = new Mat();
    private Mat maskedInputMat = new Mat();
    int redAverage = 0;
    int blueAverage = 0;
    int greenAverage = 0;

    /**
     * Constructor for the class
     *
     * @param telemetry
     */
    public SignalConePipeline(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    @Override
    public void init(Mat firstFrame) {
        /*
         * We need to call this in order to make sure the mats are initialized,
         * so that the submats we make
         * will still be linked to it on subsequent frames. (If
         * the object were to only be initialized in processFrame,
         * then the submats would become delinked because the backing
         * buffer would be re-allocated the first time a real frame
         * was crunched)
         */
        Imgproc.cvtColor(firstFrame, ycrcbMat, Imgproc.COLOR_RGB2YCrCb);

        // initialize each of the three separate mats used to test for each
        // color
        Core.inRange(ycrcbMat, filterForBlueLower, filterForBlueUpper, testForBlueMat);
        Core.inRange(ycrcbMat, filterForGreenLower, filterForGreenUpper, testForGreenMat);
        Core.inRange(ycrcbMat, filterForRedLower, filterForRedUpper, testForRedMat);

        /*
         * Submats are a persistent reference to a region of the parent
         * buffer. Any changes to the child affect the parent, and the
         * reverse also holds true.
         */
        croppedTestForBlueMat = testForBlueMat.submat(croppingArea);
        croppedTestForGreenMat = testForGreenMat.submat(croppingArea);
        croppedTestForRedMat = testForRedMat.submat(croppingArea);
    }

    @Override
    public Mat processFrame(Mat input) {
        rectangleTopLeftPoint = new Point(rectangleXOrigin, rectangleYOrigin);
        rectangleBottomRightPoint = new Point(
                rectangleTopLeftPoint.x + rectangleWidth,
                rectangleTopLeftPoint.y + rectangleHeight);
        croppingArea = new Rect(rectangleTopLeftPoint, rectangleBottomRightPoint);
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
         */
        Imgproc.cvtColor(input, ycrcbMat, Imgproc.COLOR_RGB2YCrCb);
        // Now apply thresholds to the yCrCb mat, creating 3 new mats:
        // One filtered for blue
        // One filtered for green
        // One filtered for red
        /*
         * This is where our thresholding actually happens.
         * Takes our "ycrcbMat" as input and outputs a "binary"
         * Mat of the same size as our input.
         * "Discards" all the pixels outside the bounds specified
         * by the scalars (and modifiable with EOCV-Sim's
         * live variable tuner.)
         *
         * Binary meaning that we have either a 0 or 255 value
         * for every pixel.
         *
         * 0 represents our pixels that were outside the bounds
         * 255 represents our pixels that are inside the bounds
         */
        Core.inRange(ycrcbMat, filterForBlueLower, filterForBlueUpper, testForBlueMat);
        Core.inRange(ycrcbMat, filterForGreenLower, filterForGreenUpper, testForGreenMat);
        Core.inRange(ycrcbMat, filterForRedLower, filterForRedUpper, testForRedMat);

        croppedTestForBlueMat = testForBlueMat.submat(croppingArea);
        croppedTestForGreenMat = testForGreenMat.submat(croppingArea);
        croppedTestForRedMat = testForRedMat.submat(croppingArea);

        /* The mat that results from the inRange is a single channel with 0 where the pixel
         * did not fall within the ranges. If it did fall in the range then it has 255 (all ones).
         * 255 is white
         * 0 is black
         * I can calculate the average of all of the pixels in submat (cropped area) for each of
         * the three color test mats. The mat with the highest average is the one that we will say
         * is the color of the cone. So if the average pixel value in the testForBlueMat in the
         * cropped area is the highest value, then we say the cone is blue.
         */

        /*
         * Compute the average pixel value of each submat region. We're
         * taking the average of a single channel buffer, so the value
         * we need is at index 0.
         */
        blueAverage = (int) Core.mean(croppedTestForBlueMat).val[0];
        greenAverage = (int) Core.mean(croppedTestForGreenMat).val[0];
        redAverage = (int) Core.mean(croppedTestForRedMat).val[0];
        // there may be a problem if two averages are equal
        if ((redAverage > greenAverage) && (redAverage > blueAverage)) {
            coneColor = ConeColor.RED;
        }
        if ((greenAverage > redAverage) && greenAverage > blueAverage) {
            coneColor = ConeColor.GREEN;
        }
        if ((blueAverage > redAverage) && (blueAverage > greenAverage)) {
            coneColor = ConeColor.BLUE;
        }

        telemetry.addData("Cone color    = ", coneColor.toString());
        telemetry.addData("blue average  = ", blueAverage);
        telemetry.addData("green average = ", greenAverage);
        telemetry.addData("red average   = ", redAverage);
        telemetry.addData("viewport shows  ", stageToRenderToViewport.toString());
        telemetry.update();

        /*
         * Render the 'input' buffer to the viewport. But note this is not
         * simply rendering the raw camera feed, because we called functions
         * to add some annotations to this buffer earlier up.
         */
        maskedInputMat.release();

        switch (stageToRenderToViewport) {
            case RAW_IMAGE: {
                // just copy the input mat to the maskedInputMat since maskedInputMat is the one
                // that is displayed in the viewport
                maskedInputMat = input.clone();
            }
            break;
            case BLUE_TEST: {
                Core.bitwise_and(input, input, maskedInputMat, testForBlueMat);
            }
            break;
            case GREEN_TEST: {
                Core.bitwise_and(input, input, maskedInputMat, testForGreenMat);
            }
            break;
            case RED_TEST: {
                Core.bitwise_and(input, input, maskedInputMat, testForRedMat);
            }
            break;
        }

        // add a rectangle to show the area of the image being processed to determine the color
        Imgproc.rectangle(
                maskedInputMat, // Buffer to draw on
                rectangleTopLeftPoint, // First point which defines the rectangle
                rectangleBottomRightPoint, // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines
        return maskedInputMat;
    }

    /**
     * When the user taps on the screen of the driver station, switch the image that is being
     * viewed between the different filters.
     */
    @Override
    public void onViewportTapped()
    {
        /*
         * Note that this method is invoked from the UI thread
         * so whatever we do here, we must do quickly.
         */

        switch (stageToRenderToViewport) {
            case RAW_IMAGE:
                stageToRenderToViewport = StageToSendToViewport.BLUE_TEST;
                break;
            case BLUE_TEST:
                stageToRenderToViewport = StageToSendToViewport.GREEN_TEST;
                break;
            case GREEN_TEST:
                stageToRenderToViewport = StageToSendToViewport.RED_TEST;
                break;
            case RED_TEST:
                stageToRenderToViewport = StageToSendToViewport.RAW_IMAGE;
                break;
        }
    }

    /*
     * Call this from the OpMode thread to obtain the latest analysis
     */

    public ConeColor getConeColor() {
        return coneColor;
    }

    public void displayDebugTelemetry() {
        telemetry.addData("blue average   = ", blueAverage);
        telemetry.addData("green average  = ", greenAverage);
        telemetry.addData("red average    = ", redAverage);
        telemetry.addData("viewport shows   ", stageToRenderToViewport.toString());
    }
}


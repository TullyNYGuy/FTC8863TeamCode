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

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class RectangleAdjustmentPipeline extends OpenCvPipeline {

    /*
     * Some color constants
     */
    static final Scalar BLUE = new Scalar(0, 0, 255);
    static final Scalar GREEN = new Scalar(0, 255, 0);

    /*
     * The core values which define the location and size of the sample region
     */
    // Since these values are public they will show up as input boxes in EOCV-Sim, allowing you to
    // adjust the values and move the rectangle around to where you want it. This is a way to easily
    // adjust your sample region, then grab the values for use in a real pipeline.
    public double rectangleXOrigin = 0;
    public double rectangleYOrigin = 0;
    public double rectangleWidth = 20;
    public double rectangleHeight = 20;

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
    Point rectangleBottomRightPoint = new Point(
            rectangleTopLeftPoint.x + rectangleWidth,
            rectangleTopLeftPoint.y + rectangleHeight);

    @Override
    public Mat processFrame(Mat input) {
        /*
         * Overview of what we're doing:
         *
         * Draw a rectangle on the image. The origin of the rectangle can be adjusted by
         * entering new values. I'm sure you can use the same technique to dynamically change
         * the rectangle size. Or maybe even setup the rectangles for all 3 regions.
         */
        rectangleTopLeftPoint = new Point(rectangleXOrigin, rectangleYOrigin);
        rectangleBottomRightPoint = new Point(
                rectangleTopLeftPoint.x + rectangleWidth,
                rectangleTopLeftPoint.y + rectangleHeight);

        /*
         * Draw a rectangle showing sample region 1 on the screen.
         * Simply a visual aid. Serves no functional purpose.
         */
        Imgproc.rectangle(
                input, // Buffer to draw on
                rectangleTopLeftPoint, // First point which defines the rectangle
                rectangleBottomRightPoint, // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines

        return input;
    }
}
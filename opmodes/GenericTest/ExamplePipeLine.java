/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.opmodes.GenericTest;




import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;


public class ExamplePipeLine extends OpenCvPipeline {


    // These are the mats we need, I will be explaining them as we go
    private Mat matYCrCb = new Mat();
    private Mat matCb_left = new Mat();
    private Mat matCb_Right = new Mat();
    private Mat left_block = new Mat();
    private Mat right_block = new Mat();

    //These will store the Cb values
    public double left;
    public double right;

    //These will be the points for our rectangle
    int[] left_rect = {
            1,
            2,
            3,
            4
    };

    int[] right_rect = {
            1,
            2,
            3,
            4
    };

    /**
     * This will create the rectangles
     *
     * @param frame     the input mat
     * @param points    the points for the rectangle
     * @param color     the color of the rectangle when it is displayed on screen
     * @param thickness the thickness of the rectangle
     */
    public Mat drawRectangle(Mat frame, int[] points, Scalar color, int thickness) {

        Imgproc.rectangle(
                frame,
                new Point(
                        points[0],
                        points[1]),

                new Point(
                        points[2],
                        points[3]),
                color, thickness);

        //submat simply put is cropping the mat
        return frame.submat(points[1], points[3], points[0], points[2]);

    }

    @Override
    public Mat processFrame(Mat input) {
        /**
         *input which is in RGB is the frame the camera gives
         *We convert the input frame to the color space matYCrCb
         *Then we store this converted color space in the mat matYCrCb
         *For all the color spaces go to
         *https://docs.opencv.org/3.4/d8/d01/group__imgproc__color__conversions.html
         */
        Imgproc.cvtColor(input, matYCrCb, Imgproc.COLOR_RGB2YCrCb);

        left_block = drawRectangle(matYCrCb, left_rect, new Scalar(0, 255, 0), 1);
        right_block = drawRectangle(matYCrCb, right_rect, new Scalar(0, 0, 255), 1);

        /**
         *This will extract the value of the CB channel in both rectangles
         *0 is the Y channel, 1 is the Cr, 2 is Cb
         */
        Core.extractChannel(left_block, matCb_left, 2);
        Core.extractChannel(right_block, matCb_Right, 2);

        /**
         *We now average value and extract it
         * so now left is the Cb value of the left rectangle and
         * right is the Cb value of the right rectangle
         */
        Scalar left_mean = Core.mean(matCb_left);
        Scalar right_mean = Core.mean(matCb_Right);
        left = left_mean.val[0];
        right = right_mean.val[0];

        return input;
    }


}
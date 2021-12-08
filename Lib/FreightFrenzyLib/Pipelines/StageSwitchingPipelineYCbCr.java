package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * With this pipeline, we demonstrate how to change which stage of
 * is rendered to the viewport when the viewport is tapped. This is
 * particularly useful during pipeline development. We also show how
 * to get data from the pipeline to your OpMode.
 */
public class StageSwitchingPipelineYCbCr extends OpenCvPipeline {
    Mat input;
    Mat yCbCrMat = new Mat();
    Mat yChanMat = new Mat();
    Mat cBChanMat = new Mat();
    Mat cRChanMat = new Mat();
    Mat thresholdMat = new Mat();
    Mat contoursOnFrameMat = new Mat();
    List<MatOfPoint> contoursList = new ArrayList<>();
    int numContoursFound;



    ElapsedTime timer = new ElapsedTime();

    enum Stage {
        YCrCb,
        YCrCb_CHAN_Y,
        YCrCb_CHAN_CB,
        YCrCb_CHAN_CR,
        THRESHOLD,
        CONTOURS_OVERLAYED_ON_FRAME,
        RAW_IMAGE
    }

    private Stage stageToRenderToViewport = Stage.YCrCb_CHAN_Y;
    private Stage[] stages = Stage.values();

    @Override
    public void onViewportTapped() {
        /*
         * Note that this method is invoked from the UI thread
         * so whatever we do here, we must do quickly.
         */

        int currentStageNum = stageToRenderToViewport.ordinal();

        int nextStageNum = currentStageNum + 1;

        if (nextStageNum >= stages.length) {
            nextStageNum = 0;
        }

        stageToRenderToViewport = stages[nextStageNum];
    }

    @Override
    public Mat processFrame(Mat input) {
        contoursList.clear();

        /*
         * This pipeline finds the contours of yellow blobs such as the Gold Mineral
         * from the Rover Ruckus game.
         */
        Imgproc.cvtColor(input, yCbCrMat, Imgproc.COLOR_RGB2YCrCb);
        Core.extractChannel(yCbCrMat, yChanMat, 0);
        yChanMat = labelMat(yChanMat, "Y");
        Core.extractChannel(yCbCrMat, cRChanMat, 1);
        cRChanMat = labelMat(cRChanMat, "Cr");
        Core.extractChannel(yCbCrMat, cBChanMat, 2);
        cBChanMat = labelMat(cRChanMat, "Cb");
        Imgproc.threshold(yCbCrMat, thresholdMat, 102, 255, Imgproc.THRESH_BINARY_INV);
        thresholdMat = labelMat(thresholdMat, "Threshold");
        Imgproc.findContours(thresholdMat, contoursList, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        numContoursFound = contoursList.size();
        input.copyTo(contoursOnFrameMat);
        Imgproc.drawContours(contoursOnFrameMat, contoursList, -1, new Scalar(0, 0, 255), 3, 2);
        contoursOnFrameMat = labelMat(contoursOnFrameMat, "Contours");
        yCbCrMat = labelMat(yCbCrMat, "YCrCb");
        this.input = labelMat(input, "Input");

        matSwitcher();
        return matSelector();
    }

    public Mat matSelector() {
        switch (stageToRenderToViewport) {

            case YCrCb: {
                return yCbCrMat;
            }
            case YCrCb_CHAN_Y: {
                return yChanMat;
            }

            case YCrCb_CHAN_CB: {
                return cBChanMat;
            }

            case YCrCb_CHAN_CR: {
                return cRChanMat;
            }

            case THRESHOLD: {
                return thresholdMat;
            }

            case CONTOURS_OVERLAYED_ON_FRAME: {
                return contoursOnFrameMat;
            }

            case RAW_IMAGE: {
                return input;
            }

            default: {
                return input;
            }
        }
    }

    // this method simulates a tap on the viewport screen
    public void matSwitcher() {
        if (timer.seconds() > 5) {
            onViewportTapped();
            timer.reset();
        }
    }

    public int getNumContoursFound() {
        return numContoursFound;
    }

    // label the mat
    private Mat labelMat(Mat input, String label) {
        int font = Imgproc.FONT_HERSHEY_PLAIN;
        int fontScale = 1;
        // location of text to be written on the mat
        Point textAnchor = new Point(20, input.height() / 2);
        Scalar green = new Scalar(0, 255, 0, 255);
        Imgproc.putText(input, label, textAnchor, font, fontScale, green, 2);
        return input;
    }
}

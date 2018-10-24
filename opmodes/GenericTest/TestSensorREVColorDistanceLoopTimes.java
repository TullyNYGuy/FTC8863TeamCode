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

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

import java.lang.annotation.ElementType;
import java.util.Locale;

/*
 * This is an example LinearOpMode that shows how to use
 * the REV Robotics Color-Distance Sensor.
 *
 * It assumes the sensor is configured with the name "sensor_color_distance".
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 */
@TeleOp(name = "Sensor: TestREVColorDistance Loop Times", group = "Sensor")
//@Disabled                            // Comment this out to add to the opmode list
public class TestSensorREVColorDistanceLoopTimes extends LinearOpMode {

    /**
     * Note that the REV Robotics Color-Distance incorporates two sensors into one device.
     * It has a light/distance (range) sensor.  It also has an RGB color sensor.
     * The light/distance sensor saturates at around 2" (5cm).  This means that targets that are 2"
     * or closer will display the same value for distance/light detected.
     * <p>
     * Although you configure a single REV Robotics Color-Distance sensor in your configuration file,
     * you can treat the sensor as two separate sensors that share the same name in your op mode.
     * <p>
     * In this example, we represent the detected color by a hue, saturation, and value color
     * model (see https://en.wikipedia.org/wiki/HSL_and_HSV).  We change the background
     * color of the screen to match the detected color.
     * <p>
     * In this example, we  also use the distance sensor to display the distance
     * to the target object.  Note that the distance sensor saturates at around 2" (5 cm).
     */
    ColorSensor sensorColor;
    double red;
    double green;
    double blue;
    double alpha;

    DistanceSensor sensorDistance;
    DataLogging dataLogging;
    int loopcount = 0;

    ElapsedTime timer;
    // a variable to save the elapsed time
    double elapsedTime = 0;
    // the average loop time

    double loopTimeDoingNothing = 0;
    double averageLoopTimeDoingNothing = 0;

    double loopTimeReadingColor =0;
    double averageLoopTimeReadingColor = 0;

    double loopTimeConvertingColor = 0;
    double averageLoopTimeConvertingColor = 0;

    double loopTimeWritingLogFilemSec = 0;
    double loopTimeWritingLogFileSec = 0;
    double averageLoopTimeWritingLogFile = 0;

    @Override
    public void runOpMode() {

        // get a reference to the color sensor.
        sensorColor = hardwareMap.get(ColorSensor.class, "revColorSensor");

        // get a reference to the distance sensor that shares the same name.
        sensorDistance = hardwareMap.get(DistanceSensor.class, "revColorSensor");

        dataLogging = new DataLogging("colorSensor", telemetry);

        // create a timer with resolution in mSec
        timer = new ElapsedTime();

        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        // values is a reference to the hsvValues array.
        final float values[] = hsvValues;

        // sometimes it helps to multiply the raw RGB values with a scale factor
        // to amplify/attentuate the measured values.
        final double SCALE_FACTOR = 255;

        // get a reference to the RelativeLayout so we can change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        // wait for the start button to be pressed.
        waitForStart();

        //******************************************************************************************
        // loop and do not do anything real
        // reset the timer to 0 (like re-setting the stopwatch
        timer.reset();
        // run the loop 1000 times for a good average
        while (opModeIsActive() && loopcount < 1000) {
            loopcount++; // same as loopcount = loopcount + 1
            // you need an idle() method call to allow the other parts of the robot code to run
            idle();
        }
        // calculate the average loop time in milliseconds timer.milliseconds() get the elapsed time
        // (like a stopwatch)
        loopTimeDoingNothing = timer.milliseconds();
        averageLoopTimeDoingNothing = loopTimeDoingNothing / 1000;
        telemetry.addData("wait for it, this will take about 25 sec","...");
        telemetry.update();
        //******************************************************************************************


        //******************************************************************************************
        // loop and only read the color sensor
        timer.reset();
        // I have not reset the loopcount since it is already at 1001 from the previous loop.
        // If I don't reset it then the next loop gets skipped right over! DUH!
        loopcount = 0;
        while (opModeIsActive() && loopcount < 1000) {
            loopcount++; // same as loopcount = loopcount + 1

            // read the color sensor - I'm not doing anything with the values - just reading to see
            // how long it takes
            red = sensorColor.red();
            green = sensorColor.green();
            blue = sensorColor.blue();

            idle();
        }
        loopTimeReadingColor = timer.milliseconds();
        averageLoopTimeReadingColor = loopTimeReadingColor / 1000;
        //******************************************************************************************


        //******************************************************************************************
        // loop and only read convert to hue - I'm not reading the color sensor. Instead I'm using
        // the last color readings and just running the calculation 100 time. I want to see how much
        // time running the calculation itself takes.
        timer.reset();
        loopcount = 0;
        while (opModeIsActive() && loopcount < 1000) {
            loopcount++; // same as loopcount = loopcount + 1

            // run the conversion calculation - not doing anything with the result - just doing it
            // to see how long it takes
            Color.RGBToHSV((int) (red * SCALE_FACTOR),
                    (int) (green * SCALE_FACTOR),
                    (int) (blue * SCALE_FACTOR),
                    hsvValues);

            idle();
        }
        loopTimeConvertingColor = timer.milliseconds();
        averageLoopTimeConvertingColor = loopTimeConvertingColor / 1000;
        //******************************************************************************************


        //******************************************************************************************
        // loop and only write to the log file
        timer.reset();
        loopcount = 0;
        // start the log timer so you can compare the answers between the log file timer and the
        // elapsed timer
        dataLogging.startTimer();
        while (opModeIsActive() && loopcount < 1000) {
            loopcount++; // same as loopcount = loopcount + 1

            // write a data line into the log file - just doing it to see how long it takes
            dataLogging.logData("This is a test line written to the log file");

            idle();
        }
        loopTimeWritingLogFilemSec = timer.milliseconds();
        averageLoopTimeWritingLogFile = loopTimeWritingLogFilemSec / 1000;
        //******************************************************************************************


        // now create the data lines to send the averages back to the driver station phone
        // the format string says to format the floating point number (f) as 5 digits . no digits (5.0f)
//        telemetry.addData("Total loop time doing nothing (mSec):       ", String.format("%5.3f", loopTimeDoingNothing));
        telemetry.addData("Ave loop time doing nothing (mSec):     ", String.format("%5.3f", averageLoopTimeDoingNothing));
//        telemetry.addData("Total loop time reading color (mSec):       ", String.format("%5.3f", loopTimeReadingColor));
        telemetry.addData("Ave loop time reading color (mSec):     ", String.format("%5.3f", averageLoopTimeReadingColor));
//        telemetry.addData("Total loop time converting to hue (mSec):   ", String.format("%5.3f", loopTimeConvertingColor));
        telemetry.addData("Ave loop time converting to hue (mSec): ", String.format("%5.3f", averageLoopTimeConvertingColor));
//        telemetry.addData("Total loop time writing to log (mSec):      ", String.format("%5.3f", loopTimeWritingLogFilemSec));
        telemetry.addData("Ave loop time writing to log (mSec):    ", String.format("%5.3f", averageLoopTimeWritingLogFile));

        // actually display the lines on the driver station phone
        telemetry.update();

        // close the data log file
        dataLogging.closeDataLog();

        // give the person time to read the driver phone before shutting the program down
        sleep(20000);
    }
}

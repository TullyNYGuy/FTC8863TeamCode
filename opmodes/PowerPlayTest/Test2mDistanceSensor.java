/*
Copyright (c) 2018 FIRST

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of FIRST nor the names of its contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.StatTrackerGB;

/**
 * {@link Test2mDistanceSensor} illustrates how to use the REV Robotics
 * Time-of-Flight Range Sensor.
 *
 * The op mode assumes that the range sensor is configured with a name of "sensor_range".
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 *
 * @see <a href="http://revrobotics.com">REV Robotics Web Page</a>
 */
@TeleOp(name = "Sensor: PowerPlay2mDistance", group = "Test")
//@Disabled
public class Test2mDistanceSensor extends LinearOpMode {

    //private DistanceSensor distanceSensorNormal;
    private DistanceSensor distanceSensorNormal;
    private DistanceSensor distanceSensorInverse;
    private int NUMBER_OF_READINGS_TO_TAKE = 50;
    private int readingsTaken = 0;
    private double distanceReadNormal = 0;
    private double distanceReadInverse = 0;

    private ElapsedTime timer = new ElapsedTime();

    private StatTrackerGB statTrackerNormal = new StatTrackerGB();
    private StatTrackerGB statTrackerInverse = new StatTrackerGB();

    @Override
    public void runOpMode() {
        // you can use this as a regular DistanceSensor.
        distanceSensorNormal = hardwareMap.get(DistanceSensor.class, "distanceSensorNormal");
        distanceSensorInverse = hardwareMap.get(DistanceSensor.class, "distanceSensorInverse");

        // you can also cast this to a Rev2mDistanceSensor if you want to use added
        // methods associated with the Rev2mDistanceSensor class.
        Rev2mDistanceSensor sensorTimeOfFlight = (Rev2mDistanceSensor)distanceSensorNormal;

        telemetry.addData(">>", "Press start to continue");
        telemetry.update();

        waitForStart();
        timer.reset();

        while(opModeIsActive() && readingsTaken < NUMBER_OF_READINGS_TO_TAKE) {
            // take the readings with enough time between them to make sure that the readings are
            // not just cached copies of I2C reads.
            if(timer.milliseconds() > 50) {
                readingsTaken ++;
                distanceReadNormal = distanceSensorNormal.getDistance(DistanceUnit.MM);
                statTrackerNormal.addDataPoint(distanceReadNormal);
                telemetry.addData("Taking readings from distance sensor. Please wait", "...");
                telemetry.addData("Reading number = ", readingsTaken);
                telemetry.addData("Distance Normal(mm) = ", distanceReadNormal);
                telemetry.update();
                timer.reset();
            }
            idle();
        }

        // take the inverse readings after the normal sensor so that there is no chance of one sensor
        // reading reflected light emitted by the other.
        readingsTaken = 0;
        while(opModeIsActive() && readingsTaken <= NUMBER_OF_READINGS_TO_TAKE) {
            // take the readings with enough time between them to make sure that the readings are
            // not just cached copies of I2C reads.
            if(timer.milliseconds() > 50) {
                readingsTaken ++;
                distanceReadInverse = distanceSensorInverse.getDistance(DistanceUnit.MM);
                statTrackerInverse.addDataPoint(distanceReadInverse);
                telemetry.addData("Taking readings from distance sensor. Please wait", "...");
                telemetry.addData("Reading number = ", readingsTaken);
                telemetry.addData("Distance Inverse(mm) = ", distanceReadInverse);
                telemetry.update();
                timer.reset();
            }
            idle();
        }

        while(opModeIsActive()) {
            telemetry.addData("Finished", "!");
            telemetry.addData("Number of readings = ", statTrackerNormal.getCount());
            telemetry.addData("Average distance Normal(mm) = ", statTrackerNormal.getAverage());
            telemetry.addData("Minimum distance read = ", statTrackerNormal.getMinimum());
            telemetry.addData("Maximum distance read = ", statTrackerNormal.getMaximum());
            telemetry.addData("Standard deviation = ", statTrackerNormal.getStandardDeviation());
            telemetry.addData("Average distance Inverse(mm) = ", statTrackerInverse.getAverage());
            telemetry.addData("Minimum distance read = ", statTrackerInverse.getMinimum());
            telemetry.addData("Maximum distance read = ", statTrackerInverse.getMaximum());
            telemetry.addData("Standard deviation = ", statTrackerInverse.getStandardDeviation());
            telemetry.update();
            idle();
        }
        statTrackerNormal.dumpDataCSV("normalTest");
        statTrackerInverse.dumpDataCSV("inverseTest");
    }
}
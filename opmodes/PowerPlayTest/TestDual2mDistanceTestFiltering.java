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

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExponentialMovingAverage;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LimitedQueue;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlay2mDistanceSensor;

import java.util.Iterator;
import java.util.List;

/**
 * {@link TestDual2mDistanceTestFiltering} illustrates how to use the REV Robotics
 * Time-of-Flight Range Sensor.
 * <p>
 * The op mode assumes that the range sensor is configured with a name of "sensor_range".
 * <p>
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 *
 * @see <a href="http://revrobotics.com">REV Robotics Web Page</a>
 */
@Config
@Autonomous(group = "Test")
//@Disabled
public class TestDual2mDistanceTestFiltering extends LinearOpMode {

    private enum Mode {
        START_CONTINUOUS,
        CONTINUOUS,
        COMPLETE
    }

    private Mode mode = Mode.START_CONTINUOUS;

    private enum SensorBeingRead {
        INVERSE,
        NORMAL
    }

    private SensorBeingRead sensorBeingRead = SensorBeingRead.INVERSE;

    private FtcDashboard dashboard = FtcDashboard.getInstance();

    //private DistanceSensor distanceSensorNormal;
    private PowerPlay2mDistanceSensor distanceSensorNormal;
    private PowerPlay2mDistanceSensor distanceSensorInverse;
    private double continuousDistanceNormalUnfiltered = 0;
    private double continuousDistanceInverseUnfiltered = 0;
    private double continuousDistanceNormalFiltered = 0;
    private double continuousDistanceInverseFiltered = 0;
    private double differenceUnfiltered = 0;
    private double differenceFiltered = 0;

    private LimitedQueue<Double> times;
    private LimitedQueue<Double> continuousDistancesNormalUnfiltered;
    private LimitedQueue<Double> continuousDistancesInverseUnfiltered;
    private LimitedQueue<Double> continuousDistancesNormalFiltered;
    private LimitedQueue<Double> continuousDistancesInverseFiltered;

    private CSVDataFile rawSensorReadingsCSV;
    private boolean wroteData = false;

    private ExponentialMovingAverage movingAverageNormal;
    private ExponentialMovingAverage movingAverageInverse;

    private ElapsedTime timer = new ElapsedTime();

    private GamepadButtonMultiPush gamepad1a;

    @Override
    public void runOpMode() {
        distanceSensorNormal = new PowerPlay2mDistanceSensor(hardwareMap, telemetry, "distanceSensorNormal", DistanceUnit.MM);
        distanceSensorInverse = new PowerPlay2mDistanceSensor(hardwareMap, telemetry, "distanceSensorInverse", DistanceUnit.MM);
        gamepad1a = new GamepadButtonMultiPush(1);

        rawSensorReadingsCSV = new CSVDataFile("rawSensorReadings");
        rawSensorReadingsCSV.headerStrings("inverse", "normal", "difference");

        movingAverageNormal = new ExponentialMovingAverage(.5);
        movingAverageInverse = new ExponentialMovingAverage(.5);

        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        times = new LimitedQueue<>(200);
        continuousDistancesNormalUnfiltered = new LimitedQueue<>(200);
        continuousDistancesInverseUnfiltered = new LimitedQueue<>(200);
        continuousDistancesNormalFiltered = new LimitedQueue<>(200);
        continuousDistancesInverseFiltered = new LimitedQueue<>(200);

        telemetry.addData(">>", "Press start to continue");
        telemetry.update();

        waitForStart();
        timer.reset();
        mode = Mode.START_CONTINUOUS;

        while (opModeIsActive()) {
            // take the readings with enough time between them to make sure that the readings are
            // not just cached copies of I2C reads.

            switch (mode) {
                case START_CONTINUOUS: {
                    distanceSensorInverse.startSingleReading(50);
                    mode = Mode.CONTINUOUS;
                    sensorBeingRead = SensorBeingRead.INVERSE;
                    timer.reset();
                }
                break;
                case CONTINUOUS: {
                    if(gamepad1a.buttonPress(gamepad1.a)) {
                        mode = Mode.COMPLETE;
                    }
                    switch (sensorBeingRead) {
                        case INVERSE: {
                            if (distanceSensorInverse.isSingleReadingReady()) {
                                continuousDistanceInverseUnfiltered = distanceSensorInverse.getSingleReading(DistanceUnit.MM);
                                continuousDistancesInverseUnfiltered.add(continuousDistanceInverseUnfiltered);
                                continuousDistanceInverseFiltered = movingAverageInverse.average(continuousDistanceInverseUnfiltered);
                                continuousDistancesInverseFiltered.add(continuousDistanceInverseFiltered);
                                times.add(timer.milliseconds());
                                distanceSensorNormal.startSingleReading(50);
                                sensorBeingRead = sensorBeingRead.NORMAL;
                            }
                        }
                        break;
                        case NORMAL: {
                            if (distanceSensorNormal.isSingleReadingReady()) {
                                continuousDistanceNormalUnfiltered = distanceSensorNormal.getSingleReading(DistanceUnit.MM);
                                continuousDistancesNormalUnfiltered.add(continuousDistanceNormalUnfiltered);
                                continuousDistanceNormalFiltered = movingAverageNormal.average(continuousDistanceNormalUnfiltered);
                                continuousDistancesNormalFiltered.add(continuousDistanceNormalFiltered);
                                distanceSensorInverse.startSingleReading(50);
                                sensorBeingRead = sensorBeingRead.INVERSE;
                            }
                        }
                        break;
                    }
                    differenceUnfiltered = Math.round(continuousDistanceNormalUnfiltered - continuousDistanceInverseUnfiltered);
                    differenceFiltered = Math.round(continuousDistanceNormalFiltered - continuousDistanceInverseFiltered);
                    telemetry.addData("Normal distance Unfiltered = ", continuousDistanceNormalUnfiltered);
                    telemetry.addData("Normal distance Filtered = ", Math.round(continuousDistanceNormalFiltered));
                    telemetry.addData("Inverse distance Unfiltered = ", continuousDistanceInverseUnfiltered);
                    telemetry.addData("Inverse distance Filtered = ", Math.round(continuousDistanceInverseFiltered));
                    telemetry.addData("Unfiltered Difference (normal-inverse) = ", differenceUnfiltered);
                    telemetry.addData("Filtered Difference (normal-inverse) = ", differenceFiltered);
                    telemetry.update();
                }
                break;
                case COMPLETE: {
                    if(!wroteData) {
                        rawSensorReadingsCSV.headerStrings(
                                "time (mS)",
                                "inverse unfiltered",
                                "normal unfiltered",
                                "inverse filtered",
                                "normal filtered",
                                "difference unfiltered",
                                "difference filtered");
                        for (int i = 0; i < continuousDistancesInverseUnfiltered.size(); i++) {
                            rawSensorReadingsCSV.writeData(
                                    times.get(i),
                                    continuousDistancesInverseUnfiltered.get(i),
                                    continuousDistancesNormalUnfiltered.get(i),
                                    continuousDistancesInverseFiltered.get(i),
                                    continuousDistancesNormalFiltered.get(i),
                                    continuousDistancesInverseUnfiltered.get(i) - continuousDistancesNormalUnfiltered.get(i),
                                    continuousDistancesInverseFiltered.get(i) - continuousDistancesNormalFiltered.get(i)
                            );
                        }
                        rawSensorReadingsCSV.closeDataLog();
                        wroteData = true;
                    }
                    telemetry.addData("Readings saved in CSV file", "." );
                    telemetry.addData("mode = ", mode.toString());
                    telemetry.update();
                }
                break;
            }
        }
        idle();
    }
}
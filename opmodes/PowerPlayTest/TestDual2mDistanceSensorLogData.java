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

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlay2mDistanceSensor;

/**
 * {@link TestDual2mDistanceSensorLogData} illustrates how to use the REV Robotics
 * Time-of-Flight Range Sensor.
 * <p>
 * The op mode assumes that the range sensor is configured with a name of "sensor_range".
 * <p>
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 *
 * @see <a href="http://revrobotics.com">REV Robotics Web Page</a>
 */
@TeleOp(name = "Sensor: PowerPlay2mDistance Log Data", group = "Test")
@Disabled
public class TestDual2mDistanceSensorLogData extends LinearOpMode {

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

    //private DistanceSensor distanceSensorNormal;
    private PowerPlay2mDistanceSensor distanceSensorNormal;
    private PowerPlay2mDistanceSensor distanceSensorInverse;
    private double continuousDistanceNormal = 0;
    private double continuousDistanceInverse = 0;
    private double averageDistanceNormal = 0;
    private double averageDistanceInverse = 0;
    private int NUMBER_OF_READINGS_TO_TAKE = 100;
    private int numberOfReadindsTaken = 0;

    private CSVDataFile rawSensorReadingsCSV;
    private boolean wroteData = false;

    private ElapsedTime timer = new ElapsedTime();


    @Override
    public void runOpMode() {
        distanceSensorNormal = new PowerPlay2mDistanceSensor(hardwareMap, telemetry, "distanceSensorNormal");
        distanceSensorInverse = new PowerPlay2mDistanceSensor(hardwareMap, telemetry, "distanceSensorInverse");

        rawSensorReadingsCSV = new CSVDataFile("rawSensorReadings");
        rawSensorReadingsCSV.headerStrings("inverse", "normal", "difference");

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
                    if (numberOfReadindsTaken > NUMBER_OF_READINGS_TO_TAKE) {
                        mode = Mode.COMPLETE;
                    }
                    switch (sensorBeingRead) {
                        case INVERSE: {
                            if (distanceSensorInverse.isSingleReadingReady()) {
                                continuousDistanceInverse = distanceSensorInverse.getSingleReading(DistanceUnit.MM);
                                distanceSensorNormal.startSingleReading(50);
                                sensorBeingRead = sensorBeingRead.NORMAL;
                            }
                        }
                        break;
                        case NORMAL: {
                            if (distanceSensorNormal.isSingleReadingReady()) {
                                numberOfReadindsTaken++;
                                continuousDistanceNormal = distanceSensorNormal.getSingleReading(DistanceUnit.MM);
                                rawSensorReadingsCSV.writeData(timer.milliseconds(), continuousDistanceInverse, continuousDistanceNormal, continuousDistanceInverse - continuousDistanceNormal);
                                distanceSensorInverse.startSingleReading(50);
                                sensorBeingRead = sensorBeingRead.INVERSE;
                            }
                        }
                        break;
                    }
                    telemetry.addData("Normal distance = ", continuousDistanceNormal);
                    telemetry.addData("Inverse distance = ", continuousDistanceInverse);
                    telemetry.addData("Difference (normal-inverse)= ", continuousDistanceNormal - continuousDistanceInverse);
                    telemetry.addData("mode = ", mode.toString());
                    telemetry.update();
                }
                break;
                case COMPLETE: {
                    if(!wroteData) {
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
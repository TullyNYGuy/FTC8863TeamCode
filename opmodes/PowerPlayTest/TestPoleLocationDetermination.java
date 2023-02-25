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
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExponentialMovingAverage;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LimitedQueue;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlay2mDistanceSensor;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayDual2mDistanceSensors;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoleLocationDetermination;

/**
 * {@link TestPoleLocationDetermination} illustrates how to use the REV Robotics
 * Time-of-Flight Range Sensor.
 * <p>
 * The op mode assumes that the range sensor is configured with a name of "sensor_range".
 * <p>
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 *
 * @see <a href="http://revrobotics.com">REV Robotics Web Page</a>
 */
@TeleOp(name = "Test Pole Location Determination", group = "Test")
//@Disabled
public class TestPoleLocationDetermination extends LinearOpMode {

    private FtcDashboard dashboard = FtcDashboard.getInstance();

    private PowerPlayDual2mDistanceSensors distanceSensors;
    private PowerPlayPoleLocationDetermination poleLocationDetermination;
    private PowerPlayPoleLocationDetermination.PoleLocation poleLocation;

    private ElapsedTime timer = new ElapsedTime();

    private DataLogging poleLocationLogFile;

    private double distanceToPole = 0;
    private double capturedDistanceToPole = 0;
    private boolean centered = false;

    @Override
    public void runOpMode() {
        distanceSensors = new PowerPlayDual2mDistanceSensors(hardwareMap, telemetry, "Dual Distance Sensors", DistanceUnit.MM);
        poleLocationDetermination = new PowerPlayPoleLocationDetermination(distanceSensors);
        poleLocationLogFile = new DataLogging("poleLocations");
        poleLocationDetermination.setDataLog(poleLocationLogFile);
        poleLocationDetermination.enableDataLogging();

        telemetry.addData(">>", "Press start to continue");
        telemetry.update();

        waitForStart();
        timer.reset();
        poleLocationDetermination.enablePoleLocationDetermination();

        while (opModeIsActive()) {
            poleLocationDetermination.update();
            if (poleLocationDetermination.isDataValid()) {
                poleLocation = poleLocationDetermination.getPoleLocation();
                distanceToPole = poleLocationDetermination.getDistanceFromPole(DistanceUnit.MM);
                telemetry.addData("Pole location = ", poleLocation.toString());
                telemetry.addData("normal distance = ", poleLocationDetermination.getNormalDistance(DistanceUnit.MM));
                telemetry.addData("inverse distance = ", poleLocationDetermination.getInverseDistance(DistanceUnit.MM));
                telemetry.addData("difference = ", poleLocationDetermination.getSensorDifference(DistanceUnit.MM));
                telemetry.addData("Distance to pole = ", distanceToPole);

                // capture data on the first time the pole is centered
                if (!centered && poleLocation == PowerPlayPoleLocationDetermination.PoleLocation.CENTER) {
                    centered = true;
                    capturedDistanceToPole = distanceToPole;
                }
                if (centered) {
                    telemetry.addData("Pole was centered", "!");
                    telemetry.addData("Distance to pole = ", capturedDistanceToPole);
                }
                telemetry.update();
            }
            idle();
        }
    }
}
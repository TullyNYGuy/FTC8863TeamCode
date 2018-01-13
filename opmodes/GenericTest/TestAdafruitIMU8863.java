package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import android.view.Gravity;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.StatTracker;

import java.util.Locale;

/**
 * This Opmode tests the IMU.
 *
 * Phone configuration:
 * I2C port type: Adafruit IMU
 * I2C device name: IMU
 */
@TeleOp(name = "Test Adafruit IMU 8863", group = "Test")
//@Disabled
public class TestAdafruitIMU8863 extends LinearOpMode {

    // Put your variable declarations here
    AdafruitIMU8863 imu;
    double heading = 0;
    double pitch = 0;
    double roll = 0;

    Orientation angles;
    Acceleration gravity;
    BNO055IMU.SystemStatus systemStatus;
    boolean isConnected;

    boolean yButtonIsReleased = true;
    boolean xButtonIsReleased = true;
    boolean aButtonIsReleased = true;
    boolean bButtonIsReleased = true;

    StatTracker loopTimeTracker;
    ElapsedTime loopTimer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        imu = new AdafruitIMU8863(hardwareMap);
isConnected = imu.isIMUConnected();

        // 12/10/2017 for some reason this line is causing the robot controller app to crash
        //systemStatus = imu.getSystemStatus();

        loopTimeTracker = new StatTracker();
        loopTimer = new ElapsedTime();

        // Wait for the start button

 //       telemetry.addData("IMU status = ", String.valueOf(systemStatus));
        if (isConnected) {
            telemetry.addData("IMU is connected", "!");
        } else {
            telemetry.addData("IMU is NOT connected.", " Check the wiring");
        }
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Start the logging of measured acceleration
        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        loopTimer.reset();

        while (opModeIsActive()) {

            //if(systemStatus != BNO055IMU.SystemStatus.UNKNOWN) {
            if (isConnected){
                loopTimeTracker.compareValue(loopTimer.milliseconds());
                loopTimer.reset();

                // Y BUTTON IS RELATIVE ANGLES, RELATIVE TO THE LAST TIME THE REFERENCE WAS RESET AND
                // THEN NORMALIZED TO -180 (RIGHT TURN) TO +180 (LEFT TURN)
                if (gamepad1.y) {
                    if (yButtonIsReleased) {
                        imu.setAngleMode(AdafruitIMU8863.AngleMode.RELATIVE);
                        yButtonIsReleased = false;
                    }
                } else {
                    yButtonIsReleased = true;
                }

                // B BUTTON IS ABSOLUTE ANGLES READ FROM IMU, RELATIVE TO THE POSITION OF THE IMU
                // WHEN IT WAS INITIALIZED, AND THEN NORMALIZED TO -180 (RIGHT TURN) TO +180 (LEFT TURN)
                if (gamepad1.b) {
                    if (bButtonIsReleased) {
                        imu.setAngleMode(AdafruitIMU8863.AngleMode.ABSOLUTE);
                        bButtonIsReleased = false;
                    }
                } else {
                    bButtonIsReleased = true;
                }

                // A BUTTON IS RAW ANGLES AS READ FROM THE IMU
                if (gamepad1.a) {
                    if (aButtonIsReleased) {
                        imu.setAngleMode(AdafruitIMU8863.AngleMode.RAW);
                        aButtonIsReleased = false;
                    }
                } else {
                    aButtonIsReleased = true;
                }

                // X button resets the reference angles
                if (gamepad1.x) {
                    if (xButtonIsReleased) {
                        // toggle the mode
                        imu.resetAngleReferences();
                        xButtonIsReleased = false;
                    }
                } else {
                    xButtonIsReleased = true;
                }

                // Put your calls that need to run in a loop here
                heading = imu.getHeading();
                pitch = imu.getPitch();
                roll = imu.getRoll();

                // Display the current value
                telemetry.addData("IMU mode = ", imu.getAngleMode().toString());
                telemetry.addData("Heading = ", "%5.2f", heading);
                telemetry.addData("Pitch = ", "%5.2f", pitch);
                telemetry.addData("Roll = ", "%5.2f", roll);
                telemetry.addData("Min loop time (mS) = ", "%3.3f", loopTimeTracker.getMinimum());
                telemetry.addData("Max loop time (mS) = ", "%3.3f", loopTimeTracker.getMaximum());
                telemetry.addData("Ave loop time (mS) = ", "%3.3f", loopTimeTracker.getAverage());
                telemetry.addData(">", "Press Stop to end test.");
            } else {
                telemetry.addData("IMU is not connected! ", "Check wiring!");
            }

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}


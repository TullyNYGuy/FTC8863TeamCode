package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import android.provider.ContactsContract;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.ar.pl.DrawOverlayView;

import net.frogbots.ftcopmodetunercommon.opmode.TunableLinearOpMode;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Tune PID for Turn", group = "Test")
//@Disabled
public class TunableLinearOpModeTest extends TunableLinearOpMode {

    // Put your variable declarations here
    DriveTrain driveTrain;
    DataLogging dataLog;
    double kp;
    double ki;
    double kd;
    double power =.3;
    double turnAngle = 90;
    double turnThreshold = 1.0;

    ElapsedTime timer;
    double timeToCompletedTurn = 0;
    boolean turnCompleted = false;

    boolean completionStatus = false;

    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        dataLog = new DataLogging("PID", telemetry);
        driveTrain.setDataLog(dataLog);
        driveTrain.pidControl.setDatalog(dataLog);
        driveTrain.pidControl.disableDataLogging();

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        kp = getDouble("kp");
        ki = getDouble("ki");
        ki = ki/1000000;
        kd = getDouble("kd");
        power = getDouble("power");
        turnAngle = getDouble("turnAngle");
        turnThreshold = getDouble("turnThreshold");

        driveTrain.setTurnThreshold(turnThreshold);
        driveTrain.setupTurn(turnAngle, power, AdafruitIMU8863.AngleMode.RELATIVE, kp, ki);

        waitForStart();

        dataLog.startTimer();
        dataLog.logData("kp = " + kp);
        dataLog.logData("ki = " + String.format("%.12f", ki));
        dataLog.logData("power = " + power);
        dataLog.logData("turn angle = " + turnAngle);
        dataLog.logData("turn threshold = " + turnThreshold);

        telemetry.addData("kp = ", kp);
        telemetry.addData("ki = ", String.format("%.12f", ki));
        telemetry.addData("kd = ", kd);
        telemetry.addData("power = ", power);
        telemetry.addData("turn threshold = ", turnThreshold);
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();

        timer.reset();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive() && !turnCompleted) {

            // update the turn and get its completion status
            completionStatus = driveTrain.updateTurn();

            // if the turn completed on this run through the loop
            if (completionStatus && !turnCompleted) {
                // record how long it took to complete the turn
                timeToCompletedTurn = timer.milliseconds();
                // flag that the turn has completed
                turnCompleted = true;
                // stop the turn
                driveTrain.stopTurn();

                // display a bunch of data about the turn
                telemetry.addData("kp = ", kp);
                telemetry.addData("ki = ", String.format("%.12f", ki));
                telemetry.addData("kd = ", kd);
                telemetry.addData("power = ", power);
                telemetry.addData("turn threshold = ", turnThreshold);
                telemetry.addData("Turn completed in (mSec) ", timeToCompletedTurn);
                telemetry.addLine("heading = " + driveTrain.imu.getHeading());
                telemetry.addData(">", "Press Stop to end test.");
                telemetry.update();

                // log some data about the turn
                dataLog.logData("Turn completed in (mSec) " + timeToCompletedTurn);
            }
            idle();
        }

        // wait for a bit and then log the heading so that we can see if it matches the heading
        // that the turn finished at
        while (opModeIsActive() && timer.milliseconds() < timeToCompletedTurn + 4000) {
            dataLog.logData(Double.toString(driveTrain.imu.getHeading()));
        }
        // Put your cleanup code here - it runs as the application shuts down
        // display the final heading
        telemetry.addLine("heading = " + driveTrain.imu.getHeading());
        dataLog.closeDataLog();
        telemetry.update();

        // give the user time to look at the driver station phone data
        sleep(4000);

    }
}

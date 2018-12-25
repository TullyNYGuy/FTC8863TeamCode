package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import android.provider.ContactsContract;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.vuforia.ar.pl.DrawOverlayView;

import net.frogbots.ftcopmodetunercommon.opmode.TunableLinearOpMode;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Tunable Linear Op Mode", group = "Test")
//@Disabled
public class TunableLinearOpModeTest extends TunableLinearOpMode {

    // Put your variable declarations here
    DriveTrain driveTrain;
    DataLogging dataLogging;
    double kp;
    double ki;

    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        dataLogging = new DataLogging("PID", telemetry);
        driveTrain.setDataLogging(dataLogging);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        kp = getDouble("kp");
        ki = getDouble("ki");
        driveTrain.setupTurn(90, 04, AdafruitIMU8863.AngleMode.RELATIVE, kp, ki);
        waitForStart();
        dataLogging.startTimer();
        dataLogging.logData("kp = " + kp);
        dataLogging.logData("ki = " + ki);
        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            driveTrain.updateTurn();

            telemetry.addData("kp = ", getDouble("kp"));
            telemetry.addData("ki = ", getDouble("ki"));
            telemetry.addData("kd = ", getDouble("kd"));
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        dataLogging.closeDataLog();
        telemetry.update();

    }
}

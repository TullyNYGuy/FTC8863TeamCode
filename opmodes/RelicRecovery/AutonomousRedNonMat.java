package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import android.test.RenamingDelegatingContext;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.ReadPictograph;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@Autonomous(name = "Red No Mat", group = "Run")
//@Disabled
public class AutonomousRedNonMat extends AutonomousMethods {

    // Put your variable declarations here
    StartPosition startPosition = StartPosition.RED_NO_MAT;
    AllianceColor.TeamColor teamColor = AllianceColor.TeamColor.RED;
    ReadPictograph readPictograph;
    RelicRecoveryVuMark vuMark;

    @Override
    public void runOpMode() {
        // Put your initializations here
        createRobot();

        readPictograph = new ReadPictograph(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        readPictograph.runAtStart();

        vuMark = readPictograph.getvuMark();
        doAutonomousMovements(startPosition, vuMark);

        while(opModeIsActive()) {
            //robot.jewelArm.knockOffBall(teamColor);

//            telemetry.addData("About to start autonomous movements", "!");
//            telemetry.update();
//            sleep(2000);

//            telemetry.addData("Finished autonomous movements", "!");
//            telemetry.update();
//            sleep(2000);

            // Put your calls that need to run in a loop here

            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

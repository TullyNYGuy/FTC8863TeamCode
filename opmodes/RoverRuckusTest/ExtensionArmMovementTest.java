package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "extension arm movement test", group = "Test")
//@Disabled
public class ExtensionArmMovementTest extends LinearOpMode {

    // Put your variable declarations here
public CollectorArm collectorArm;
public int collectorArmEncoderValue;

    @Override
    public void runOpMode() {


        // Put your initializations here
        collectorArm= new CollectorArm(hardwareMap,telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        collectorArm.extensionMoveToPosition(2);
        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here

            collectorArmEncoderValue= collectorArm.getExtensionEncoderValue();
            telemetry.addData("Extension Arm Encoder = ", collectorArmEncoderValue);
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

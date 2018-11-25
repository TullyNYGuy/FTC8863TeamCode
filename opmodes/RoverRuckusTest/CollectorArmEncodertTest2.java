package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Collector Arm Encoder Test REAL", group = "Test")
//@Disabled
public class CollectorArmEncodertTest2 extends LinearOpMode {

    // Put your variable declarations here
    public CollectorArm collectorArm;

    @Override
    public void runOpMode() {


        // Put your initializations here
        collectorArm = new CollectorArm(hardwareMap,telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        collectorArm.goToHome();
        while(opModeIsActive() && collectorArm.update()!= DcMotor8863.MotorState.COMPLETE_HOLD) {

            // Put your calls that need to run in a loop here
            collectorArm.displayEncoder();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();

            idle();
        }
        sleep(2000);
        collectorArm.goToCollect();
        while(opModeIsActive() && collectorArm.update()!= DcMotor8863.MotorState.COMPLETE_HOLD) {

            // Put your calls that need to run in a loop here
            collectorArm.displayEncoder();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();

            idle();
        }
        collectorArm.floatArm();
        sleep(2000);
        collectorArm.goToTransfer();
        while(opModeIsActive() && collectorArm.update()!= DcMotor8863.MotorState.COMPLETE_HOLD) {

            // Put your calls that need to run in a loop here
            collectorArm.displayEncoder();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();

            idle();
        }
        sleep(2000);
        collectorArm.goToHome();
        while(opModeIsActive() && collectorArm.update()!= DcMotor8863.MotorState.COMPLETE_HOLD) {

            // Put your calls that need to run in a loop here
            collectorArm.displayEncoder();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();

            idle();
        }
        collectorArm.floatArm();

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

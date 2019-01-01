package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorArm;


/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Extension Arm State Machine Tests", group = "Test")
//@Disabled
public class ExtensionArmStateMachineTests extends LinearOpMode {

    // Put your variable declarations here
    public CollectorArm collectorArm;
    public ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        timer = new ElapsedTime();
        collectorArm = new CollectorArm(hardwareMap, telemetry);
        collectorArm.enableDebugMode();
        collectorArm.init();


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // ****************************************************************************************
        // RESET the extension arm
        //*****************************************************************************************
        collectorArm.extensionArmReset();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("resetting extension arm");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at bottom, try to reset, should do nothing
        //*****************************************************************************************
        collectorArm.extensionArmReset();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("resetting extension arm, should do nothing");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at bottom, try to move to bottom, should do nothing
        //*****************************************************************************************
        collectorArm.goToRetract();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to retract, should do nothing");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at bottom, try to move to top, should move to top
        //*****************************************************************************************
        collectorArm.goToExtend();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to extend");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at top, try to move to top, should do nothing
        //*****************************************************************************************
        collectorArm.goToExtend();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to extend, should do nothing");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at top, try to move to bottom, should move to bottom
        //*****************************************************************************************
        collectorArm.goToRetract();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move from extend to retract");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at bottom, try to move to position
        //*****************************************************************************************
        collectorArm.gotoExtensionArm5Inches();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to 5 inches");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at 5", try to reset but interrupt that with a move to top
        //*****************************************************************************************
        collectorArm.extensionArmReset();
        timer.reset();

        // start a reset and interrupt it after 2 seconds
        while (opModeIsActive() && timer.milliseconds() < 2000) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("reset but try an interrupt during reset. Should reset anyway");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // after 2 seconds has passed command a move to top. This occurs before the reset has finished
        // This should not interrupt the reset
        collectorArm.goToExtend();
        timer.reset();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("interrupted reset with a move to extend. Should reset anyway");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at bottom, try to move to top, should move to top
        //*****************************************************************************************
        collectorArm.goToExtend();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to extend");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at top, try to move to position
        //*****************************************************************************************
        collectorArm.gotoExtensionArm5Inches();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to 5 inches");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at position, try to move to top, should move to top
        //*****************************************************************************************
        collectorArm.goToExtend();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to extend from a position");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at top, try to reset, should reset
        //*****************************************************************************************
        collectorArm.extensionArmReset();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("resetting from extend");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at reset, try to move to position
        //*****************************************************************************************
        collectorArm.gotoExtensionArm5Inches();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to 5 inches");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at position, try to move to position
        //*****************************************************************************************
        collectorArm.gotoExtensionArm8Inches();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to 8 inches");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at position, try to move to top
        //*****************************************************************************************
        collectorArm.goToExtend();

        // when the extension arm reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to extend from position");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // done!
        //*****************************************************************************************

        // after the extension arm reaches its position, the loop stops and this code runs
        collectorArm.displayExtensionMotorEncoder();
        collectorArm.displayExtensionArmPosition();
        collectorArm.displayExtensionArmState();
        telemetry.update();
        // give the user time to read the driver station
        sleep(4000);
    }
}

package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.DeliveryLiftSystem;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Lift State Machine Tests", group = "Test")
//@Disabled
public class LiftStateMachineTests extends LinearOpMode {

    // Put your variable declarations here
    public DeliveryLiftSystem deliveryLiftSystem;
    public ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        timer = new ElapsedTime();
        deliveryLiftSystem = new DeliveryLiftSystem(hardwareMap, telemetry);
        deliveryLiftSystem.init();
        deliveryLiftSystem.enableDebugMode();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // ****************************************************************************************
        // RESET the lift
        //*****************************************************************************************
        deliveryLiftSystem.liftReset();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("resetting lift");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at bottom, try to reset, should do nothing
        //*****************************************************************************************
        deliveryLiftSystem.liftReset();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("resetting lift, should do nothing");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at bottom, try to move to bottom, should do nothing
        //*****************************************************************************************
        deliveryLiftSystem.goToBottom();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to bottom, should do nothing");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at bottom, try to move to top, should move to top
        //*****************************************************************************************
        deliveryLiftSystem.goToTop();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to top");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at top, try to move to top, should do nothing
        //*****************************************************************************************
        deliveryLiftSystem.goToTop();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to top, should do nothing");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at top, try to move to bottom, should move to bottom
        //*****************************************************************************************
        deliveryLiftSystem.goToBottom();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move from top to bottom");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at bottom, try to move to position
        //*****************************************************************************************
        deliveryLiftSystem.goto5Inches();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to 5 inches");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at 5", try to reset but interrupt that with a move to top
        //*****************************************************************************************
        deliveryLiftSystem.liftReset();
        timer.reset();

        // start a reset and interrupt it after 2 seconds
        while (opModeIsActive() && timer.milliseconds() < 2000) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("reset but try an interrupt during reset. Should reset anyway");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // after 2 seconds has passed command a move to top. This occurs before the reset has finished
        // This should not interrupt the reset
        deliveryLiftSystem.goToTop();
        timer.reset();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("interrupted reset with a move to top. Should reset anyway");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at bottom, try to move to top, should move to top
        //*****************************************************************************************
        deliveryLiftSystem.goToTop();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to top");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at top, try to move to position
        //*****************************************************************************************
        deliveryLiftSystem.goto5Inches();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to 5 inches");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at position, try to move to top, should move to top
        //*****************************************************************************************
        deliveryLiftSystem.goToTop();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to top from a position");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at top, try to reset, should reset
        //*****************************************************************************************
        deliveryLiftSystem.liftReset();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("resetting from top");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at reset, try to move to position
        //*****************************************************************************************
        deliveryLiftSystem.goto5Inches();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to 5 inches");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at position, try to move to position
        //*****************************************************************************************
        deliveryLiftSystem.goto8Inches();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to 8 inches");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // at position, try to move to top
        //*****************************************************************************************
        deliveryLiftSystem.goToTop();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to top from position");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);

        // ****************************************************************************************
        // done!
        //*****************************************************************************************

        // after the lift reaches its position, the loop stops and this code runs
        deliveryLiftSystem.displayLiftMotorEncoder();
        deliveryLiftSystem.displayLiftPosition();
        deliveryLiftSystem.displayLiftState();
        telemetry.update();
        // give the user time to read the driver station
        sleep(4000);
    }
}

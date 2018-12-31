package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.DeliveryLiftSystem;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Lift State Machine Joystick Tests", group = "Test")
//@Disabled
public class LiftJoystickTests extends LinearOpMode {

    // Put your variable declarations here
    public DeliveryLiftSystem deliveryLiftSystem;
    public ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        timer = new ElapsedTime();
        deliveryLiftSystem = new DeliveryLiftSystem(hardwareMap, telemetry);
        deliveryLiftSystem.enableDebugMode();
        deliveryLiftSystem.init();


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // ****************************************************************************************
        // RESET the lift, joystick input should not do anything
        //*****************************************************************************************
        deliveryLiftSystem.liftReset();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            deliveryLiftSystem.setLiftPowerUsingJoystick(.1);

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("resetting lift, joystick should do nothing");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        deliveryLiftSystem.update();
        sleep(5000);

//        // ****************************************************************************************
//        // after joystick, tell the lift to go to 5 inches
//        //*****************************************************************************************
//        deliveryLiftSystem.goto5Inches();
//
//        // when the lift reaches its position, the loop will stop running and the next set of code
//        // will run
//        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {
//
//            // Put your calls that need to run in a loop here
//            deliveryLiftSystem.update();
//
//            // Display the current value
//            telemetry.addLine("move to 5 inches");
//            deliveryLiftSystem.displayLiftMotorEncoder();
//            deliveryLiftSystem.displayLiftPosition();
//            deliveryLiftSystem.displayLiftState();
//            telemetry.addData(">", "Press Stop to end test.");
//
//            telemetry.update();
//
//            idle();
//        }
//        sleep(5000);

        // ****************************************************************************************
        // at bottom, give some joystick input up
        //*****************************************************************************************

        timer.reset();

        // give joystick input for 2 seconds
        while (opModeIsActive() && timer.milliseconds() < 2000) {

            deliveryLiftSystem.setLiftPowerUsingJoystick(.4);

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("moving lift up for 2 seconds using joystick");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        // lift should stop
        deliveryLiftSystem.setLiftPowerUsingJoystick(0);
        // IMPORTANT - THIS UPDATE HAS TO BE HERE IN ORDER FOR THE STATE MACHINE TO RUN AND SHUT
        // OFF THE MOTOR BY GIVING IT 0 POWER
        deliveryLiftSystem.update();
        sleep(5000);

        // ****************************************************************************************
        // after joystick, tell the lift to go to 5 inches
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
        // give some joystick input down
        //*****************************************************************************************

        timer.reset();

        // give joystick input for 2 seconds
        while (opModeIsActive() && timer.milliseconds() < 2000) {

            deliveryLiftSystem.setLiftPowerUsingJoystick(-.4);

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("moving lift down for 2 seconds using joystick");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        // lift should stop
        deliveryLiftSystem.setLiftPowerUsingJoystick(0);
        // IMPORTANT - THIS UPDATE HAS TO BE HERE IN ORDER FOR THE STATE MACHINE TO RUN AND SHUT
        // OFF THE MOTOR BY GIVING IT 0 POWER
        deliveryLiftSystem.update();
        sleep(5000);


        // ****************************************************************************************
        // try to move to top, should move to top
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
        // at top, give some joystick input up, should not work
        //*****************************************************************************************

        timer.reset();

        // give joystick input for 2 seconds
        while (opModeIsActive() && timer.milliseconds() < 2000) {

            deliveryLiftSystem.setLiftPowerUsingJoystick(.4);

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("trying to joystick up, should not work");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        // lift should stop
        deliveryLiftSystem.setLiftPowerUsingJoystick(0);
        // IMPORTANT - THIS UPDATE HAS TO BE HERE IN ORDER FOR THE STATE MACHINE TO RUN AND SHUT
        // OFF THE MOTOR BY GIVING IT 0 POWER
        deliveryLiftSystem.update();
        sleep(5000);

        // ****************************************************************************************
        // at top, try to move to bottom after joystick up fails
        //*****************************************************************************************
        deliveryLiftSystem.goToBottom();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to bottom");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);


        // ****************************************************************************************
        // at bottom, give some joystick input down, should not work
        //*****************************************************************************************

        timer.reset();

        // give joystick input for 2 seconds
        while (opModeIsActive() && timer.milliseconds() < 2000) {

            deliveryLiftSystem.setLiftPowerUsingJoystick(-.4);

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("trying to joystick down, should not work");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        // lift should stop
        deliveryLiftSystem.setLiftPowerUsingJoystick(0);
        // IMPORTANT - THIS UPDATE HAS TO BE HERE IN ORDER FOR THE STATE MACHINE TO RUN AND SHUT
        // OFF THE MOTOR BY GIVING IT 0 POWER
        deliveryLiftSystem.update();
        sleep(5000);


        // ****************************************************************************************
        // at bottom, give some joystick input up until hit top, should stop at top limit switch
        //*****************************************************************************************

        timer.reset();

        // give joystick input for 2 seconds
        while (opModeIsActive() && timer.milliseconds() < 10000) {

            deliveryLiftSystem.setLiftPowerUsingJoystick(.4);

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("joystick to top, should auto stop");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        telemetry.addLine("auto stopped lift?");
        telemetry.update();
        sleep(5000);
        // lift should stop
        deliveryLiftSystem.setLiftPowerUsingJoystick(0);
        // IMPORTANT - THIS UPDATE HAS TO BE HERE IN ORDER FOR THE STATE MACHINE TO RUN AND SHUT
        // OFF THE MOTOR BY GIVING IT 0 POWER
        deliveryLiftSystem.update();
        sleep(1000);


        // ****************************************************************************************
        // after joystick, tell the lift to go to 5 inches
        //*****************************************************************************************
        deliveryLiftSystem.goto5Inches();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to 5 inches after autostop at top");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(5000);


        // ****************************************************************************************
        // at 5 inches, give some joystick input down until hit bottom, should stop at top limit switch
        //*****************************************************************************************

        timer.reset();

        // give joystick input for 2 seconds
        while (opModeIsActive() && timer.milliseconds() < 5000) {

            deliveryLiftSystem.setLiftPowerUsingJoystick(-.4);

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("joystick to bottom, should auto stop");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        // lift should stop
        telemetry.addLine("auto stopped lift?");
        telemetry.update();
        sleep(5000);
        deliveryLiftSystem.setLiftPowerUsingJoystick(0);
        // IMPORTANT - THIS UPDATE HAS TO BE HERE IN ORDER FOR THE STATE MACHINE TO RUN AND SHUT
        // OFF THE MOTOR BY GIVING IT 0 POWER
        deliveryLiftSystem.update();
        sleep(1000);

/*
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
        sleep(5000);*/

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

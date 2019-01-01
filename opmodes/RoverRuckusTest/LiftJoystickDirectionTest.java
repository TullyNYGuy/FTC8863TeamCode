package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.DeliveryLiftSystem;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Lift Joystick Direction Test", group = "Test")
@Disabled
public class LiftJoystickDirectionTest extends LinearOpMode {

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
        // set the lift to a position between the top and bottom and give some joystick input up
        //*****************************************************************************************

        timer.reset();

        // give joystick input for 2 seconds
        while (opModeIsActive() && timer.milliseconds() < 2000) {

            deliveryLiftSystem.setLiftPowerUsingJoystick(.3);

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
        deliveryLiftSystem.update();
        telemetry.addLine("lift should stop");
        telemetry.update();
        sleep(5000);

        // ****************************************************************************************
        // set the lift to a position between the top and bottom and give some joystick input down
        //*****************************************************************************************

        timer.reset();

        // give joystick input for 2 seconds
        while (opModeIsActive() && timer.milliseconds() < 2000) {

            deliveryLiftSystem.setLiftPowerUsingJoystick(-.3);

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
        deliveryLiftSystem.update();
        telemetry.addLine("lift should stop");
        telemetry.update();
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

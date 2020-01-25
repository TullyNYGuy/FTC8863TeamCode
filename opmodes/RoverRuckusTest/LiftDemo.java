package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.DeliveryLiftSystem;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "AAA Lift Demo Rover Ruckus", group = "Test")
@Disabled
public class LiftDemo extends LinearOpMode {

    // Put your variable declarations here
    public DeliveryLiftSystem deliveryLiftSystem;
    public ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        timer = new ElapsedTime();

        deliveryLiftSystem = new DeliveryLiftSystem(hardwareMap, telemetry);
        deliveryLiftSystem.init();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();
        deliveryLiftSystem.deliveryBoxToOutOfWay();
        deliveryLiftSystem.goTo9Inches();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to 9 inches");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Display the current value
        telemetry.addLine("Done");
        deliveryLiftSystem.displayLiftMotorEncoder();
        deliveryLiftSystem.displayLiftPosition();
        deliveryLiftSystem.displayLiftState();
        telemetry.addData("Time to move 9 inches (mS) = ", timer.milliseconds());
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();
        sleep(500);

        timer.reset();
        deliveryLiftSystem.goToHome();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !deliveryLiftSystem.isLiftMovementComplete()) {

            // Put your calls that need to run in a loop here
            deliveryLiftSystem.update();

            // Display the current value
            telemetry.addLine("move to home");
            deliveryLiftSystem.displayLiftMotorEncoder();
            deliveryLiftSystem.displayLiftPosition();
            deliveryLiftSystem.displayLiftState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        deliveryLiftSystem.deliveryBoxToHome();

        // Display the current value
        telemetry.addLine("Done");
        deliveryLiftSystem.displayLiftMotorEncoder();
        deliveryLiftSystem.displayLiftPosition();
        deliveryLiftSystem.displayLiftState();
        telemetry.addData("Time to move 9 inches (mS) = ", timer.milliseconds());
        telemetry.update();
        sleep(4000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

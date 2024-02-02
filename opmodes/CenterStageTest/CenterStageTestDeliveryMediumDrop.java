package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageDeliveryController;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Test Delivery Controller Medium Drop", group = "Test")
//@Disabled
public class CenterStageTestDeliveryMediumDrop extends LinearOpMode {

    // Put your variable declarations here
    CenterStageDeliveryController deliveryController;
    DataLogging log;
    ElapsedTime timer;
    Debouncer debouncedDpadUp = new Debouncer();
    Debouncer debouncedDpadDown = new Debouncer();
    Debouncer debouncedDpadLeft = new Debouncer();
    Debouncer debouncedDpadRight = new Debouncer();
    Debouncer debouncedDpadUpGamepad2 = new Debouncer();
    Debouncer debouncedDpadDownGamepad2 = new Debouncer();
    Debouncer debouncedDpadLeftGamepad2 = new Debouncer();
    Debouncer debouncedDpadRightGamepad2 = new Debouncer();

    @Override
    public void runOpMode() {


        // Put your initializations here
        log = new DataLogging("LiftLog");
        timer = new ElapsedTime();
        deliveryController = new CenterStageDeliveryController(hardwareMap, telemetry,null);

        deliveryController.setDataLog(log);
        deliveryController.enableDataLogging();

        deliveryController.init(null);
        while (!deliveryController.isInitComplete()) {
            deliveryController.update();
            telemetry.addData("state = ", deliveryController.getState().toString());
            if (deliveryController.isInitComplete()) {
                telemetry.addData("deliveryController init = ", "complete");
            } else {
                telemetry.addData("deliveryController init = ", "NOT complete");
            }
            telemetry.update();
        }

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        deliveryController.setupForDelivery();

        // Put your calls here - they will not run in a loop


        // after the reset is complete just loop so the user can see the state
        while (opModeIsActive() && !deliveryController.isPositionReached()) {
            deliveryController.update();

            telemetry.update();
            idle();
        }

        timer.reset();
        while (opModeIsActive() && timer.milliseconds()<2000){
            deliveryController.update();

            telemetry.update();
            idle();
        }

        deliveryController.setUpForMediumPosition();
        while (opModeIsActive() && !deliveryController.isPositionReached()) {
            deliveryController.update();

            telemetry.update();
            idle();
        }

        timer.reset();
        while (opModeIsActive()){
            deliveryController.update();

            if (debouncedDpadUp.isPressed(gamepad1.dpad_up)){
           deliveryController.wristServo.bumpUpBig();
            }

            if (debouncedDpadDown.isPressed(gamepad1.dpad_down)){
                deliveryController.wristServo.bumpDownBig();
            }

            if (debouncedDpadLeft.isPressed(gamepad1.dpad_left)){
                deliveryController.wristServo.bumpDownSmall();
            }

            if (debouncedDpadRight.isPressed(gamepad1.dpad_right)){
                deliveryController.wristServo.bumpUpSmall();
            }

            if (debouncedDpadUpGamepad2.isPressed(gamepad2.dpad_up)){
                deliveryController.armServo.bumpUpBig();
            }

            if (debouncedDpadDownGamepad2.isPressed(gamepad2.dpad_down)){
                deliveryController.armServo.bumpDownBig();
            }

            if (debouncedDpadLeftGamepad2.isPressed(gamepad2.dpad_left)){
                deliveryController.armServo.bumpDownSmall();
            }

            if (debouncedDpadRightGamepad2.isPressed(gamepad2.dpad_right)){
                deliveryController.armServo.bumpUpSmall();
            }

            telemetry.addData("Arm Servo = ", deliveryController.armServo.getCurrentPosition());
            telemetry.addData("Wrist Servo = ", deliveryController.wristServo.getCurrentPosition());

            telemetry.update();
            idle();
        }
        
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

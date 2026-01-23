package org.firstinspires.ftc.teamcode.opmodes.DecodeTest.MotorTesting;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeSorterMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Sorter Motor Go To Position", group = "Test")
//@Disabled
public class TestSorterMotorGoToPosition extends LinearOpMode {

    // Put your variable declarations her
    DecodeSorterMotor sorterMotor;

    @Override
    public void runOpMode() {

        // These debounce the buttons so that you only see a single press even if a button is held
        // down for a long time.
        Debouncer debouncedY = new Debouncer();
        Debouncer debouncedB = new Debouncer();
        Debouncer debouncedX = new Debouncer();
        Debouncer debouncedA = new Debouncer();
        Debouncer debouncedDpadUp = new Debouncer();
        Debouncer debouncedDpadDown = new Debouncer();
        Debouncer debouncedDpadLeft = new Debouncer();

        // Put your initializations here
        sorterMotor = new DecodeSorterMotor("sorterMotor", hardwareMap, telemetry);
        sorterMotor.init(null);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            if (debouncedY.isPressed(gamepad2.y)) {
               sorterMotor.moveToPosition(120);
            }
            if (debouncedA.isPressed(gamepad2.a)) {
                sorterMotor.moveToPosition(240);
            }
            if (debouncedX.isPressed(gamepad2.x)) {
                sorterMotor.moveToPosition(0);
            }
            if (debouncedB.isPressed(gamepad2.b)) {
                sorterMotor.moveToPosition(360);
            }

            // limit the rpm between 0 and 1


            telemetry.addData("Y = ", "120");
            telemetry.addData("X = ", "0");
            telemetry.addData("B = ", "360");
            telemetry.addData("A = ", "240");
            telemetry.addData("Actual RPM = ", sorterMotor.getActualRPM());
            telemetry.addData(">", "stop to finish");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

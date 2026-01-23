package org.firstinspires.ftc.teamcode.opmodes.DecodeTest.MotorTesting;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIntakeMotor;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeSorterMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Sorter And Intake Motors", group = "Test")
//@Disabled
public class TestSorterAndIntakeMotors extends LinearOpMode {

    // Put your variable declarations her
    DecodeSorterMotor sorterMotor;
    DecodeIntakeMotor intakeMotor;

    @Override
    public void runOpMode() {
        int rpmSorter = 0;
        int nextRPMSorter = 0;
        int courseRPMAdjustmentSorter = 60;
        int fineRPMAdjustmentSorter = 20;

        int rpmIntake = 0;
        int nextRPMIntake = 0;
        int courseRPMAdjustmentIntake = 500;
        int fineRPMAdjustmentIntake = 100;
        // These debounce the buttons so that you only see a single press even if a button is held
        // down for a long time.
        Debouncer debouncedYG2 = new Debouncer();
        Debouncer debouncedBG2 = new Debouncer();
        Debouncer debouncedXG2 = new Debouncer();
        Debouncer debouncedAG2 = new Debouncer();
        Debouncer debouncedDpadUpG2 = new Debouncer();
        Debouncer debouncedDpadDownG2 = new Debouncer();
        Debouncer debouncedDpadLeftG2 = new Debouncer();

        Debouncer debouncedYG1 = new Debouncer();
        Debouncer debouncedBG1 = new Debouncer();
        Debouncer debouncedXG1 = new Debouncer();
        Debouncer debouncedAG1 = new Debouncer();
        Debouncer debouncedDpadUpG1 = new Debouncer();
        Debouncer debouncedDpadDownG1 = new Debouncer();
        Debouncer debouncedDpadLeftG1 = new Debouncer();
        // Put your initializations here
        sorterMotor = new DecodeSorterMotor("sorterMotor", hardwareMap, telemetry);
        sorterMotor.init(null);

        intakeMotor = new DecodeIntakeMotor("intakeMotor", hardwareMap, telemetry);
        intakeMotor.init(null);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            if (debouncedYG2.isPressed(gamepad2.y)) {
                nextRPMSorter = nextRPMSorter + courseRPMAdjustmentSorter;
            }
            if (debouncedAG2.isPressed(gamepad2.a)) {
                nextRPMSorter = nextRPMSorter - courseRPMAdjustmentSorter;
            }
            if (debouncedXG2.isPressed(gamepad2.x)) {
                nextRPMSorter = 435;
            }
            if (debouncedBG2.isPressed(gamepad2.b)) {
                nextRPMSorter = 0;
            }
            if (debouncedDpadUpG2.isPressed(gamepad2.dpad_up)) {
                nextRPMSorter = nextRPMSorter + fineRPMAdjustmentSorter;
            }
            if (debouncedDpadDownG2.isPressed(gamepad2.dpad_down)) {
                nextRPMSorter = nextRPMSorter - fineRPMAdjustmentSorter;
            }

            // limit the rpm between 0 and 1
            nextRPMSorter = Range.clip(nextRPMSorter, 0, 435);

            if (debouncedDpadLeftG2.isPressed(gamepad2.dpad_left)) {
                rpmSorter = nextRPMSorter;
                sorterMotor.setRPM(rpmSorter);
            }

            if (debouncedYG1.isPressed(gamepad1.y)) {
                nextRPMIntake = nextRPMIntake + courseRPMAdjustmentIntake;
            }
            if (debouncedAG1.isPressed(gamepad1.a)) {
                nextRPMIntake = nextRPMIntake - courseRPMAdjustmentIntake;
            }
            if (debouncedXG1.isPressed(gamepad1.x)) {
                nextRPMIntake = 1150;
            }
            if (debouncedBG1.isPressed(gamepad1.b)) {
                nextRPMIntake = 0;
            }
            if (debouncedDpadUpG1.isPressed(gamepad1.dpad_up)) {
                nextRPMIntake = nextRPMIntake + fineRPMAdjustmentIntake;
            }
            if (debouncedDpadDownG1.isPressed(gamepad1.dpad_down)) {
                nextRPMIntake = nextRPMIntake - fineRPMAdjustmentIntake;
            }

            // limit the rpm between 0 and 1
            nextRPMIntake = Range.clip(nextRPMIntake, 0, 1150);

            if (debouncedDpadLeftG1.isPressed(gamepad1.dpad_left)) {
                rpmIntake = nextRPMIntake;
                intakeMotor.setRPM(rpmIntake);
            }

            telemetry.addData("Y = ", "+" + Integer.toString(courseRPMAdjustmentSorter));
            telemetry.addData("X = ", "435");
            telemetry.addData("B = ", "-" + Integer.toString(courseRPMAdjustmentSorter));
            telemetry.addData("A = ", "0");
            telemetry.addData("Dpad up = ", "+" + Integer.toString(fineRPMAdjustmentSorter));
            telemetry.addData("Dpad down = ", "-" + Integer.toString(fineRPMAdjustmentSorter));
            telemetry.addData("Dpad left = ", "Set motor to next rpm");
            telemetry.addLine();
            telemetry.addData("Current Sorter Speed = ", rpmSorter);
            telemetry.addData("Next Sorter Speed = ", nextRPMSorter);
            telemetry.addData("Actual Sorter RPM = ", sorterMotor.getActualRPM());
            telemetry.addData(">", "stop to finish");

            telemetry.addData("Y = ", "+" + Integer.toString(courseRPMAdjustmentIntake));
            telemetry.addData("X = ", "1150");
            telemetry.addData("B = ", "-" + Integer.toString(courseRPMAdjustmentIntake));
            telemetry.addData("A = ", "0");
            telemetry.addData("Dpad up = ", "+" + Integer.toString(fineRPMAdjustmentIntake));
            telemetry.addData("Dpad down = ", "-" + Integer.toString(fineRPMAdjustmentIntake));
            telemetry.addData("Dpad left = ", "Set motor to next rpm");
            telemetry.addLine();
            telemetry.addData("Current Intake Speed = ", rpmIntake);
            telemetry.addData("Next Intake Speed = ", nextRPMIntake);
            telemetry.addData("Actual Intake RPM = ", intakeMotor.getRPM());
            telemetry.addData(">", "stop to finish");
            telemetry.update();
            idle();

        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

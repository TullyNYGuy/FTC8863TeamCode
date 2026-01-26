package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeBallShooter;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIntakeMotor;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeSorterMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Sorter Intake Shooter Motors", group = "Test")
//@Disabled
public class TestSorterIntakeShooter extends LinearOpMode {

    // Put your variable declarations her
    DecodeSorterMotor sorterMotor;
    DecodeIntakeMotor intakeMotor;

    DecodeBallShooter ballShooter;

    @Override
    public void runOpMode() {
        int rpmSorter = 0;
        int nextRPMSorter = 0;
        int courseRPMAdjustmentSorter = 120;
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
        sorterMotor = new DecodeSorterMotor(hardwareMap, telemetry);
        sorterMotor.init(null);

        intakeMotor = new DecodeIntakeMotor(hardwareMap, telemetry);
        intakeMotor.init(null);

        ballShooter = new DecodeBallShooter(hardwareMap, telemetry);
        ballShooter.init(null);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            intakeMotor.setRPM(500);
            sorterMotor.setRPM(180);
            ballShooter.setRPM(3100);

            telemetry.addData(">", "stop to finish");
            telemetry.update();
            idle();

        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.IntakeWheels;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Intake", group = "Test")
//@Disabled
public class TestIntake extends LinearOpMode {

    // Put your variable declarations her

    @Override
    public void runOpMode() {

        Configuration config;

        String currentStatus = "Stopped";

        // Put your initializations here
//        config = new Configuration();
//        if (!config.load()) {
//            telemetry.addData("ERROR", "Couldn't load config file");
//            telemetry.update();
//        }


        // THIS OPMODE DOES NOT WORK! ONLY ONE MOTOR RUNS OUT OF THE 2 MOTORS IN THE INTAKEWHEELS OBJECT. I'M NOT SURE WHY!


        IntakeWheels intakeWheels = new IntakeWheels(hardwareMap,
                SkystoneRobot.HardwareName.INTAKE_RIGHT_MOTOR.hwName,
                SkystoneRobot.HardwareName.INTAKE_LEFT_MOTOR.hwName,
                SkystoneRobot.HardwareName.INTAKE_SWITCH_BACK_LEFT.hwName,
                SkystoneRobot.HardwareName.INTAKE_SWITCH_BACK_RIGHT.hwName,
                SkystoneRobot.HardwareName.INTAKE_SWITCH_FRONT_RIGHT.hwName,
                SkystoneRobot.HardwareName.INTAKE_SWITCH_FRONT_LEFT.hwName);
        //intakeWheels.init(config);
        intakeWheels.stop();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            if (gamepad1.dpad_up) {
                intakeWheels.intake();
                currentStatus = "Intake";
            }

            if (gamepad1.dpad_down) {
                intakeWheels.outtake();
                currentStatus = "Outtake";
            }

            if (gamepad1.dpad_right || gamepad1.dpad_left) {
                intakeWheels.stop();
                currentStatus = "Stopped";
            }

            telemetry.addData("Status = ", currentStatus);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Mecanum to Driver Station", group = "Test")
//@Disabled
public class TestMecanumToDriverStation extends LinearOpMode {

    // Put your variable declarations here
    Mecanum mecanum;
    Mecanum.WheelVelocities wheelVelocities;
    HaloControls haloControls;
    MecanumCommands mecanumCommands;

    @Override
    public void runOpMode() {


        // Put your initializations here
        mecanumCommands = new MecanumCommands();

        mecanum = new Mecanum(null, null, null, null, telemetry);
        haloControls = new HaloControls(gamepad1, null);


        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here

            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            haloControls.calculateMecanumCommands(mecanumCommands);

            mecanum.calculateWheelVelocity(mecanumCommands);

            telemetry.addData("front left = ", mecanum.getFrontLeft());
            telemetry.addData("front right = ", mecanum.getFrontRight());
            telemetry.addData("back left = ", mecanum.getBackLeft());
            telemetry.addData("back right = ", mecanum.getBackRight());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

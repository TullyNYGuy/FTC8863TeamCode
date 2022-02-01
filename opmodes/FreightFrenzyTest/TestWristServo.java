package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.WristServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Wrist Servo", group = "Test")
//@Disabled
public class TestWristServo extends LinearOpMode {

    // Put your variable declarations her
    WristServo wristServo;
    ElapsedTime timer;

    @Override
    public void runOpMode() {
        
        // Put your initializations here
        wristServo = new WristServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        wristServo.pickup();
        while (opModeIsActive() && !wristServo.isPositionReached()) {
            telemetry.addData("wrist pickup", "!");
            telemetry.update();
            idle();
        }

        wristServo.carry();
        while (opModeIsActive() && !wristServo.isPositionReached()) {
            telemetry.addData("wrist carry", "!");
            telemetry.update();
            idle();
        }

        wristServo.dropOff();
        while (opModeIsActive() && !wristServo.isPositionReached()) {
            telemetry.addData("wrist drop", "!");
            telemetry.update();
            idle();
        }

        wristServo.hold();
        while (opModeIsActive() && !wristServo.isPositionReached()) {
            telemetry.addData("wrist hold", "!");
            telemetry.update();
            idle();
        }

        while (opModeIsActive()) {
            idle();
        }
        //wristServo.testPositionUsingJoystick(this);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}

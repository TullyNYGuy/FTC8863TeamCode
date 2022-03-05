package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863New;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.WristServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Calibrate Wrist Servo", group = "Calibrate")
@Disabled
public class CalibrateWristServo extends LinearOpMode {

    // Put your variable declarations her
    Servo8863New wristServo;

    @Override
    public void runOpMode() {

        // Put your initializations here
        wristServo = new Servo8863New("wristServo", hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        // Put your calls here - they will not run in a loop

        wristServo.testPositionsUsingJoystick(this);
    }
}

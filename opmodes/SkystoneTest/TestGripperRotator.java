package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.GripperRotator;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Gripper Rotator", group = "Test")
@Disabled
public class TestGripperRotator extends LinearOpMode {

    // Put your variable declarations here
    public org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.GripperRotator gripperRotator;

    public enum Steps {
        ZERO, ONE, TWO, THREE, FOUR
    }

    public Steps steps = Steps.ZERO;

    @Override
    public void runOpMode() {


        // Put your initializations here
        Configuration config = new Configuration();
        config.load();
        gripperRotator = new GripperRotator(hardwareMap, SkystoneRobot.HardwareName.GRIPPER_ROTATOR_SERVO.hwName, telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        gripperRotator.init(config);

        while (opModeIsActive()) {
            gripperRotator.update();

            switch (steps) {
                case ZERO:
                    if (gripperRotator.isInitComplete()) {
                        gripperRotator.rotateOutward();
                        steps = steps.ONE;
                    }
                    break;
                case ONE:
                    if (gripperRotator.isRotateOutwardComplete()) {
                        gripperRotator.rotateInward();
                        steps = Steps.TWO;
                    }
                    break;
                case TWO:
                    if (gripperRotator.isRotateInwardComplete()) {
                        gripperRotator.rotateOutward();
                        steps = Steps.THREE;
                    }
                    break;
                case THREE:
                    if (gripperRotator.isRotateOutwardComplete()) {
                        //robot.chill
                    }
                    break;
                case FOUR:
                    break;
            }

        }

    }
}

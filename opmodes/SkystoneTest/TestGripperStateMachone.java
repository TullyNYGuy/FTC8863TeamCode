package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Gripper;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.GripperRotator;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Gripper Test state machien", group = "Test")
@Disabled
public class TestGripperStateMachone extends LinearOpMode {

    // Put your variable declarations here
    public Gripper gripper;
    public GripperRotator rotator;

    @Override
    public void runOpMode() {
        GamepadButtonMultiPush gamepad1x = new GamepadButtonMultiPush(1);
        GamepadButtonMultiPush gamepad1y = new GamepadButtonMultiPush(1);
        GamepadButtonMultiPush gamepad1a = new GamepadButtonMultiPush(1);
        GamepadButtonMultiPush gamepad1b = new GamepadButtonMultiPush(1);
        // Put your initializations here
        Configuration config = new Configuration();
        config.load();
        rotator = new GripperRotator(hardwareMap, "gripperRotator", telemetry);
        gripper = new Gripper(hardwareMap, "gripper", telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        gripper.init(config);
        waitForStart();

        while (!gripper.isInitComplete() && !rotator.isInitComplete()) {
            idle();
        }

        while (opModeIsActive()) {
            // Put your calls here - they will not run in a loop


            if (gamepad1a.buttonPress(gamepad1.a)) {

                gripper.gripBlock();

            }
            if (gamepad1b.buttonPress(gamepad1.b)) {
                gripper.releaseBlock();


            }
            if (gamepad1x.buttonPress(gamepad1.x)) {
                rotator.rotateOutward();


            }
            if (gamepad1y.buttonPress(gamepad1.y)) {
                rotator.rotateInward();


            }
            rotator.update();
            gripper.update();
            telemetry.update();
            idle();
            // Put your cleanup code here - it runs as the application shuts down
        }
        telemetry.addData(">", "Done");
        telemetry.update();


    }
}
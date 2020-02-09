package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Gripper;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Gripper Test", group = "Test")
//@Disabled
public class TestGripper extends LinearOpMode {

    // Put your variable declarations here
    public Gripper gripper;

    public enum Steps {
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        ELEVEN,
        TWELVE,
        THIRTEEN,
        FOURTEEN,
        FIFTEEN,
        SIXTEEN,
        SEVENTEEN
    }

    public Steps steps = Steps.ZERO;

    @Override
    public void runOpMode() {


        // Put your initializations here
        Configuration config = new Configuration();
        config.load();


        gripper = new Gripper(hardwareMap, "gripper", telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        gripper.init(config);

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {


            switch (steps) {
                case ZERO:
                    if (gripper.isInitComplete()) {
                        gripper.gripBlock();
                        steps = steps.ONE;
                    }
                    break;
                case ONE:
                    if (gripper.IsGripComplete()) {
                        gripper.releaseBlock();
                        steps = Steps.TWO;
                    }
                    break;
                case TWO:
                    if (gripper.isReleaseComplete()) {
                        gripper.gripBlock();
                        steps = Steps.THREE;
                    }
                    break;
                case THREE:
                    if (gripper.IsGripComplete()) {
                        //robot.chill
                    }
                    break;
                case FOUR:
                    break;
            }
            telemetry.addData(">", "Done");
            telemetry.update();
            gripper.update();

        }
    }
}

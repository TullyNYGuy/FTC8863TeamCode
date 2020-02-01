package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.GripperRotator;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Gripper Rotator Test", group = "Test")
//@Disabled
public class TestGripperRotator extends LinearOpMode {

    // Put your variable declarations here
    public org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.GripperRotator gripperRotator;

    @Override
    public void runOpMode() {


        // Put your initializations here
        Configuration config = new Configuration();
        config.load();
        gripperRotator = new GripperRotator(hardwareMap, "gripperRotator", telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        gripperRotator.init(config);
        sleep(5000);
        gripperRotator.rotateOutward();
        sleep(5000);
        gripperRotator.rotateInward();
        sleep(5000);
        gripperRotator.rotateOutward();
        sleep(5000);
        gripperRotator.shutdown();
        sleep(5000);
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

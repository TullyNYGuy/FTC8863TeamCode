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

        // Put your calls here - they will not run in a loop
        gripper.init(config);
        sleep(5000);
        gripper.grip();
        sleep(5000);
        gripper.release();
        sleep(5000);
        gripper.grip();
        sleep(5000);
        gripper.shutdown();
        sleep(5000);
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

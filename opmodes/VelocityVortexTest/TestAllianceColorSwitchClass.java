package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.AllianceColorSwitch;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Alliance Color Switch Class", group = "Test")
//@Disabled
public class TestAllianceColorSwitchClass extends LinearOpMode {

    // Put your variable declarations here
    private AllianceColorSwitch allianceColorSwitch;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        allianceColorSwitch = new AllianceColorSwitch(hardwareMap);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        telemetry.addData("Alliance Color = ", allianceColorSwitch.getAllianceColor().toString());
        telemetry.update();
        sleep(5000);
    }
}

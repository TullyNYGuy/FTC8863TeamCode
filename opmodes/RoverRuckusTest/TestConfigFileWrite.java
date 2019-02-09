package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousConfigurationFile;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Config File Write", group = "Test")
//@Disabled
public class TestConfigFileWrite extends LinearOpMode {

    // Put your variable declarations here
    public AutonomousConfigurationFile autonomousConfigurationFile;

    @Override
    public void runOpMode() {


        // Put your initializations here
        autonomousConfigurationFile = new AutonomousConfigurationFile();
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loopC
        autonomousConfigurationFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        autonomousConfigurationFile.setDelay(5);
        autonomousConfigurationFile.setSample(AutonomousConfigurationFile.Sample.CRATER_SIDE);
        autonomousConfigurationFile.setClaimDepot(false);
        autonomousConfigurationFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        autonomousConfigurationFile.writeConfigurationFile();


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousConfigurationFile;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Config File Read", group = "Test")
//@Disabled
public class TestConfigFileRead extends LinearOpMode {

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

        // Put your calls here - they will not run in a loop
        autonomousConfigurationFile.readConfigurationFile();
        telemetry.addData("hang location=", autonomousConfigurationFile.getHangLocation().toString());
        telemetry.addData("delay=", autonomousConfigurationFile.getDelay());
        telemetry.addData("sample=", autonomousConfigurationFile.getSample().toString());
        telemetry.addData("claim depot=", autonomousConfigurationFile.isClaimDepot());
        telemetry.addData("park location=", autonomousConfigurationFile.getParkLocation().toString());


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(10000);

    }
}

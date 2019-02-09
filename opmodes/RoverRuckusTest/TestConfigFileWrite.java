package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousConfigirationFile;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Config File Write", group = "Test")
//@Disabled
public class TestConfigFileWrite extends LinearOpMode {

    // Put your variable declarations here
    public AutonomousConfigirationFile autonomousConfigirationFile;

    @Override
    public void runOpMode() {


        // Put your initializations here
        autonomousConfigirationFile = new AutonomousConfigirationFile();
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        autonomousConfigirationFile.setHangLocation(AutonomousConfigirationFile.HangLocation.CRATER_SIDE);
        autonomousConfigirationFile.setDelay(0);
        autonomousConfigirationFile.setSample(AutonomousConfigirationFile.Sample.BOTH);
        autonomousConfigirationFile.setClaimDepot(true);
        autonomousConfigirationFile.setParkLocation(AutonomousConfigirationFile.ParkLocation.OUR_CRATER);
        autonomousConfigirationFile.writeConfigirationFile();


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

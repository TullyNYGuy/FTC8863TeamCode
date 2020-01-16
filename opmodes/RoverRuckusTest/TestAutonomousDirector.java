package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousConfigurationFile;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousDirector;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Autonomous Director", group = "Test")
//@Disabled
public class TestAutonomousDirector extends LinearOpMode {

    // Put your variable declarations here
    AutonomousConfigurationFile configFile;
    AutonomousDirector director;

    @Override
    public void runOpMode() {


        // Put your initializations here
        configFile = new AutonomousConfigurationFile();
        director = new AutonomousDirector(configFile);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        //
        // Crater side sample variations with claim depot and park our crater
        //
        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.CRATER_SIDE);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.BOTH);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.NO_SAMPLE);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);
        //
        // Depot side sample variations with claim depot and park our crater
        //
        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.CRATER_SIDE);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.BOTH);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.NO_SAMPLE);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);
        //
        // Crater side sample variations with NO claim depot and park our crater
        //
        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.CRATER_SIDE);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.BOTH);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.NO_SAMPLE);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);
        //
        // Depot side sample variations with NO claim depot and park our crater
        //
        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.CRATER_SIDE);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.BOTH);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.NO_SAMPLE);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        // same as above but this time park other crater

        //
        // Crater side sample variations with claim depot and park other crater
        //
        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.CRATER_SIDE);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.BOTH);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.NO_SAMPLE);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);
        //
        // Depot side sample variations with claim depot and park other crater
        //
        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.CRATER_SIDE);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.BOTH);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.NO_SAMPLE);
        configFile.setClaimDepot(true);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);
        //
        // Crater side sample variations with NO claim depot and park other crater
        //
        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.CRATER_SIDE);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.BOTH);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.NO_SAMPLE);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);
        //
        // Depot side sample variations with NO claim depot and park other crater
        //
        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.CRATER_SIDE);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.BOTH);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        //////////////////////////////////
        configFile.setHangLocation(AutonomousConfigurationFile.HangLocation.DEPOT_SIDE);
        configFile.setSample(AutonomousConfigurationFile.Sample.NO_SAMPLE);
        configFile.setClaimDepot(false);
        configFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OTHER_CRATER);
        configFile.setDelay(0);
        director.testDirector(telemetry);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

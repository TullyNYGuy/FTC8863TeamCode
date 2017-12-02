package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.SharpDistanceSensor;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Sharp Distance Sensor", group = "Test")
//@Disabled
public class TestSharpDistanceSensor extends LinearOpMode {

    // Put your variable declarations here

    SharpDistanceSensor sharpDistanceSensor;
    double distance = 0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        sharpDistanceSensor = new SharpDistanceSensor(RobotConfigMappingForGenericTest.getSharpDistanceSensorName(), hardwareMap);
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Voltage = ", "%5.2f", sharpDistanceSensor.getVoltageReading());
            telemetry.addData("Distance = ", "%5.2f", sharpDistanceSensor.getDistance());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIntakeColorSensor;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Color Sensor", group = "Test")
//@Disabled
public class TestColorSensor extends LinearOpMode {

    // Put your variable declarations her
    DecodeIntakeColorSensor colorSensor;

    @Override
    public void runOpMode() {


        // Put your initializations here
        colorSensor = new DecodeIntakeColorSensor(hardwareMap, telemetry, "colorSensor-");

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            colorSensor.displayColorSensorDistance(telemetry);
            colorSensor.displayColors(telemetry);
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

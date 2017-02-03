package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.hardware.ams.AMSColorSensorImpl;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

/**
 * Configure an I2C port on the phone as I2C Device
 *
 *
 */
@TeleOp(name = "Test AMSColor Sensor", group = "Test")
@Disabled
public class TestAMSColorSensor extends LinearOpMode {

    // Put your variable declarations here

    AMSColorSensor.Parameters parameters;
    I2cDevice i2cDevice;
    AMSColorSensor colorSensor;
    String colorSensorName = "colorSensor";

    ElapsedTime timer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        parameters = AMSColorSensor.Parameters.createForAdaFruit();
        i2cDevice = hardwareMap.get(I2cDevice.class, colorSensorName);
        colorSensor = AMSColorSensorImpl.create(parameters, i2cDevice);

        // Use a timer just to see some changes on the driver station. With all 0s for the color
        // sensor I want to see something change!
        timer = new ElapsedTime();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();
        timer.reset();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {
            // Put any calls here that you want to run in a loop

            // Display the current values from the sensor
            telemetry.addData("Red = ", colorSensor.red());
            telemetry.addData("Blue = ", colorSensor.blue());
            telemetry.addData("Green = ", colorSensor.green());
            telemetry.addData("Opaqueness = ", colorSensor.alpha());
            telemetry.addData("Timer = ", "%3.1f", timer.seconds());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

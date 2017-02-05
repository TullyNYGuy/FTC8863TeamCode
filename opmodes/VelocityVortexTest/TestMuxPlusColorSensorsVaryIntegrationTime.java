package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.MuxPlusColorSensors;

import java.util.Properties;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Mux Plus Color Sensors vary I time", group = "Test")
//@Disabled
public class TestMuxPlusColorSensorsVaryIntegrationTime extends LinearOpMode {

    // Put your variable declarations here
    MuxPlusColorSensors muxPlusColorSensors;
    ElapsedTime timer;
    AdafruitColorSensor8863.IntegrationTime integrationTimes[];
    int index = 0;

    public void runOpMode() {

        // Put your initializations here
        muxPlusColorSensors = new MuxPlusColorSensors(hardwareMap, telemetry);
        integrationTimes = new AdafruitColorSensor8863.IntegrationTime[4];
        integrationTimes[0] = AdafruitColorSensor8863.IntegrationTime.AMS_COLOR_ITIME_24MS;
        integrationTimes[1] = AdafruitColorSensor8863.IntegrationTime.AMS_COLOR_ITIME_50MS;
        integrationTimes[2] = AdafruitColorSensor8863.IntegrationTime.AMS_COLOR_ITIME_307MS;
        integrationTimes[3] = AdafruitColorSensor8863.IntegrationTime.AMS_COLOR_ITIME_700MS;
        timer = new ElapsedTime();

        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();

        while(opModeIsActive()) {

            if (timer.milliseconds() > 5000) {
                muxPlusColorSensors.frontRightBeaconPusherColorSensorSetIntegrationTime(integrationTimes[index]);
                index ++;
                if (index == integrationTimes.length) {
                    index = 0;
                }
                timer.reset();
            }
            // Put your calls that need to run in a loop here
            muxPlusColorSensors.displayColorValues(MuxPlusColorSensors.WhichColorSensor.FRONT_RIGHT, AdafruitColorSensor8863.AmountOfDataToDisplay.NORMAL);

            // REPORTED INTEGRATION TIME IS NOT CORRECT. iT IS ALWAYS 24MS
            // RED IS 35% MORE THAN GREEN AND BLUE

            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

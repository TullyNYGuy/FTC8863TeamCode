package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.MuxPlusColorSensors;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Mux Plus Color Sensors", group = "Test")
@Disabled
public class TestMuxPlusColorSensors extends LinearOpMode {

    // Put your variable declarations here
    MuxPlusColorSensors muxPlusColorSensors;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        muxPlusColorSensors = new MuxPlusColorSensors(hardwareMap, telemetry);
        timer = new ElapsedTime();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            muxPlusColorSensors.displayColorValues(MuxPlusColorSensors.WhichColorSensor.FRONT_RIGHT, AdafruitColorSensor8863.AmountOfDataToDisplay.MIN);
            muxPlusColorSensors.displayColorValues(MuxPlusColorSensors.WhichColorSensor.FRONT_LEFT, AdafruitColorSensor8863.AmountOfDataToDisplay.MIN);

            telemetry.addData("Front Pusher Right Color = ", muxPlusColorSensors.getSimpleColorFrontBeaconPusherRightColorSensor().toString());
            telemetry.addData("Front Pusher Left Color = ", muxPlusColorSensors.getSimpleColorFrontBeaconPusherLeftColorSensor().toString());
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.FrontBeaconPusher;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.MuxPlusColorSensors;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Front Beacon Pusher Color", group = "Test")
//@Disabled
public class TestFrontBeaconPusherColorSensors extends LinearOpMode {

    // Put your variable declarations here
    FrontBeaconPusher frontBeaconPusher;
    MuxPlusColorSensors muxPlusColorSensors;

    FrontBeaconPusher.BeaconPusherState frontBeaconPusherState;

    ElapsedTime timer;

    public void runOpMode() {


        // Put your initializations here
        muxPlusColorSensors = new MuxPlusColorSensors(hardwareMap, telemetry);
        frontBeaconPusher = new FrontBeaconPusher(hardwareMap, telemetry, muxPlusColorSensors);
        timer = new ElapsedTime();

        frontBeaconPusherState = frontBeaconPusher.findBeaconPusherState();
        telemetry.addData("Beacon pusher state found = ", frontBeaconPusherState.toString());

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            frontBeaconPusher.displayBeaconColor(telemetry);
            telemetry.addData("Right color sensor value = ", frontBeaconPusher.rgbValuesScaledAsString(MuxPlusColorSensors.BeaconSide.RIGHT));
            telemetry.addData("Left color sensor value = ", frontBeaconPusher.rgbValuesScaledAsString(MuxPlusColorSensors.BeaconSide.LEFT));
            telemetry.update();
            frontBeaconPusher.setCoreDimLEDToMatchColorSensor(MuxPlusColorSensors.BeaconSide.RIGHT);

            idle();
        }


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}

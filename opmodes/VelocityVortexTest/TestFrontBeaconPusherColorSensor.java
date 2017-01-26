package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.VelocityVortex.FrontBeaconPusher;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortex.MuxPlusColorSensors;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Front Beacon Color Sensor", group = "Test")
//@Disabled
public class TestFrontBeaconPusherColorSensor extends LinearOpMode {

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

        // Put your calls here - they will not run in a loop

        // Put your calls that need to run in a loop here
        timer.reset();

        while (opModeIsActive()) {
            telemetry.addData("Beacon color is (left/right) ", frontBeaconPusher.getBeaconColor().toString());
            telemetry.addData("Right color sensor value = ", frontBeaconPusher.rgbValuesScaledAsString(MuxPlusColorSensors.BeaconSide.RIGHT));
            telemetry.update();
            frontBeaconPusher.setCoreDimLEDToMatchColorSensor(MuxPlusColorSensors.BeaconSide.RIGHT);
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}

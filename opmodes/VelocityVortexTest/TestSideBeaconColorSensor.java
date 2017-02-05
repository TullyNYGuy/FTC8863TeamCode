package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.MuxPlusColorSensors;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.SideBeaconPusher;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Side Beacon Color Sensor", group = "Test")
//@Disabled
public class TestSideBeaconColorSensor extends LinearOpMode {

    // Put your variable declarations here
    Servo8863 beaconServo;
    MuxPlusColorSensors muxPlusColorSensors;
    SideBeaconPusher sideBeaconPusher;
    DriveTrain driveTrain;

    //@Override
    public void runOpMode() {


        // Put your initializations here
        beaconServo = new Servo8863(RobotConfigMappingForGenericTest.getRightSideBeaconPusherServo(), hardwareMap, telemetry);
        muxPlusColorSensors = new MuxPlusColorSensors(hardwareMap, telemetry);
        sideBeaconPusher = new SideBeaconPusher(hardwareMap, telemetry, driveTrain, SideBeaconPusher.SideBeaconPusherPosition.RIGHT, muxPlusColorSensors);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        //double startPosition, double endPosition, double positionIncrement, double timeBetweenPositions
        //beaconServo.setUpServoCalibration(0, 1, .05, 1000);
        //beaconServo.setPosition(.5);

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            //sideBeaconPusher.displayBeaconColor(telemetry);
            telemetry.addData("Right color sensor value = ", sideBeaconPusher.rgbValuesScaledAsString(MuxPlusColorSensors.BeaconSide.RIGHT));
            //sideBeaconPusher.setCoreDimLEDToMatchColorSensor(MuxPlusColorSensors.BeaconSide.RIGHT);
            telemetry.addData(">", "Press Stop to end test." );


            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

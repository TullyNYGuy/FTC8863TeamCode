package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Side Beacon Servo", group = "Test")
@Disabled
public class TestSideBeaconServo extends LinearOpMode {

    // Put your variable declarations here
    Servo8863 beaconServo;
    double homePosition = .25;
    double halfwayPosition = .30;
    double openPosition = .40;
    double extraPosition1 = 0;
    double extraPosition2 = 0;

    //@Override
    public void runOpMode() {


        // Put your initializations here
        beaconServo = new Servo8863(RobotConfigMappingForGenericTest.getRightSideBeaconPusherServo(), hardwareMap, telemetry);
        beaconServo.setHomePosition(homePosition);
        beaconServo.setPositionOne(halfwayPosition);
        beaconServo.setPositionTwo(openPosition);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        beaconServo.goHome();
        waitForStart();

        // Put your calls here - they will not run in a loop

        //double startPosition, double endPosition, double positionIncrement, double timeBetweenPositions
        //beaconServo.setUpServoCalibration(0, 1, .05, 1000);
        //beaconServo.setPosition(.5);

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            //beaconServo.updateServoCalibration();
            beaconServo.goPositionTwo();
            sleep(1000);
            beaconServo.goPositionOne();
            sleep(1000);
            beaconServo.goHome();
            sleep(1000);

            telemetry.addData(">", "Press Stop to end test." );


            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

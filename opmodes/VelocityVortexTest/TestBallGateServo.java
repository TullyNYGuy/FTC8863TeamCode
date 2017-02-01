package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Calibrate Ball Gate Servo", group = "Test")
//@Disabled
public class TestBallGateServo extends LinearOpMode {

    // Put your variable declarations here
    Servo8863 ballGateServo;


    //@Override
    public void runOpMode() {


        // Put your initializations here
        ballGateServo = new Servo8863(RobotConfigMappingForGenericTest.getBallGateServoName(), hardwareMap, telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        //double startPosition, double endPosition, double positionIncrement, double timeBetweenPositions
        ballGateServo.setUpServoCalibration(0, 1, .05, 1000);
        //ballGateServo.setPosition(.5);

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            ballGateServo.updateServoCalibration();

            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

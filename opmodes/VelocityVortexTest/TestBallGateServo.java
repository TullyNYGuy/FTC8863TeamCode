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
@TeleOp(name = "Test Ball Gate Servo", group = "Test")
@Disabled
public class TestBallGateServo extends LinearOpMode {

    // Put your variable declarations here
    Servo8863 ballGateServo;
    boolean gamepad1aButtonIsReleased = true;
    boolean gamepad1bButtonIsReleased = true;

    double homePosition = 1.00;
    double openPosition = 0.75;


    //@Override
    public void runOpMode() {


        // Put your initializations here
        ballGateServo = new Servo8863(RobotConfigMappingForGenericTest.getBallGateServoName(), hardwareMap, telemetry);
        ballGateServo.setHomePosition(homePosition);
        ballGateServo.setPositionOne(openPosition);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        //double startPosition, double endPosition, double positionIncrement, double timeBetweenPositions
        //ballGateServo.setUpServoCalibration(0, 1, .05, 1000);
        //ballGateServo.setPosition(.5);

        while(opModeIsActive()) {

            if (gamepad1.a) {
                if (gamepad1aButtonIsReleased) {
                    //open
                    ballGateServo.goPositionOne();
                    gamepad1aButtonIsReleased = false;
                }
            } else {
                gamepad1aButtonIsReleased = true;
            }

            if (gamepad1.b) {
                if (gamepad1bButtonIsReleased) {
                    // close
                    ballGateServo.goHome();
                    gamepad1bButtonIsReleased = false;
                }
            } else {
                gamepad1bButtonIsReleased = true;
            }
            // Put your calls that need to run in a loop here
            //ballGateServo.updateServoCalibration();

            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

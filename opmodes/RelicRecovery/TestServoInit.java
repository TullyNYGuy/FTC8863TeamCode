package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Servo Init", group = "Test")
//@Disabled
public class TestServoInit extends LinearOpMode {

    // Put your variable declarations here
    private Servo wristServo;
    private double wristPositionInit = 1.0;
    private double wristPositionHome = 1.0;
    private double wristPositionPickup = .8;
    private double wristPositionCarry = .05;
    @Override
    public void runOpMode() {


        // Put your initializations here
//        wristServo = new Servo8863("wristServo", hardwareMap, telemetry);
//        wristServo.setInitPosition(wristPositionInit);
//        wristServo.setHomePosition(wristPositionHome);
//        wristServo.setPositionOne(wristPositionPickup);
//        wristServo.setPositionTwo(wristPositionCarry);
        wristServo  = hardwareMap.get(Servo.class, "wristServo");
        wristServo.setDirection(Servo.Direction.FORWARD);

        wristServo.setPosition(wristPositionInit);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here

            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

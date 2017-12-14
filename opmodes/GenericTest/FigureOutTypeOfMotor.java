package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This Opmode is meant to be used to figure out what type of motor you have. We have Neverest 20,
 * 40 and 60 motors. Some of the are not marked and we don't know what they are.
 * They all have different encoder counts per revolution. This routine asks the user to rotate the
 * motor by hand one complete revolution. The encoder count is used to narrow down the type of motor.
 */
@TeleOp(name = "Figure out what type of motor", group = "Test")
//@Disabled
public class FigureOutTypeOfMotor extends LinearOpMode {

    // Put your variable declarations here
    private DcMotor8863 testMotor;
    private int startingEncoderCount;
    private int currentEncoderCount;

    private int countPerRevForNeverest20 = 560;
            private int countPerRevForNeverest40 = 1120;
            private int countPerRevForNeverest60 = 1680;

    @Override
    public void runOpMode() {

        // Put your initializations here
        testMotor = new DcMotor8863("armMotor", hardwareMap);
        testMotor.setDirection(DcMotor.Direction.FORWARD);
        testMotor.setMaxMotorPower(1);
        testMotor.setMinMotorPower(-1);
        testMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        // the extension of the arm is to a set point, like on a ruler
        // it is not moving by an amount each time (relative)
        testMotor.setMotorMoveType(DcMotor8863.MotorMoveType.ABSOLUTE);
        // how close to the target encoder value do we have to get before we call it good enough
        // to stop the motor
        testMotor.setTargetEncoderTolerance(10);
        testMotor.setMovementPerRev(360);
        // the motor will apply power to hold its position after it arrives at the position
        testMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        // the motor will move to the target position and then narrow in on it - the motor controller
        // will control this for us
        testMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        startingEncoderCount = testMotor.getCurrentPosition();
        
        // Wait for the start button
        telemetry.addData("Rotate the motor 1 full revolution", " after pressing start.");
        telemetry.addData("The motor will be the lowest gear ratio shown", ".");
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here

            currentEncoderCount = testMotor.getCurrentPosition();

            // the 100 is added in the following tests to account for the fact that the user could
            // turn the motor just a little past one full rotation. I still want the motor to show
            // up properly so I give the user a little margin.
            telemetry.addData("Encoder count change so far: ", "%d", currentEncoderCount - startingEncoderCount);

            if (Math.abs(currentEncoderCount - startingEncoderCount+100) < countPerRevForNeverest20) {
                telemetry.addData("So far motor could be a 20", " OR");
            }

            if (Math.abs(currentEncoderCount - startingEncoderCount+100) < countPerRevForNeverest40) {
                telemetry.addData("So far motor could be a 40", " OR");
            }

            if (Math.abs(currentEncoderCount - startingEncoderCount+100) < countPerRevForNeverest60) {
                telemetry.addData("So far motor could be a 60", ".");
            }

            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This OpMode tests a DC motor and is meant to test the functionality of the DcMotor8863 class.
 */
@TeleOp(name = "Test DcMotor8863", group = "Test")
//@Disabled
public class TestDCMotor8863 extends LinearOpMode {

    DcMotor8863 motor;
    int feedback = 0;
    double powerToRunAt = .5; // % of full speed
    int value = 0;

    private ElapsedTime runningTimer;

    private double lastTime = 0;

    @Override
    public void runOpMode() {

        runningTimer = new ElapsedTime(0);

        // Instantiate and initialize motors
        motor = new DcMotor8863(RobotConfigMappingForGenericTest.getleftMotorName(), hardwareMap);
        motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        motor.setMovementPerRev(360);
        motor.setTargetEncoderTolerance(5);
        motor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        motor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        motor.setMinMotorPower(-1);
        motor.setMaxMotorPower(1);
        motor.resetEncoder();

        motor.setDirection(DcMotor.Direction.FORWARD);

        // test internal routines from DcMotor8863
        //value = motor.getEncoderCountForDegrees(-400);
        //telemetry.addData("Encoder count for degrees = ", "%d", value);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motor.");
        telemetry.update();
        waitForStart();

        runningTimer.reset();

        //motor.moveToPosition(powerToRunAt, 720, DcMotor8863.FinishBehavior.HOLD); //works
        //motor.moveByAmount(powerToRunAt, 720, DcMotor8863.FinishBehavior.HOLD); // works
        //motor.rotateNumberOfRevolutions(powerToRunAt, 3, DcMotor8863.FinishBehavior.FLOAT); // works
        //motor.rotateNumberOfDegrees(powerToRunAt, 720, DcMotor8863.FinishBehavior.HOLD); // works
        //motor.rotateNumberOfRevolutions(powerToRunAt, 2, DcMotor8863.FinishBehavior.HOLD); // works
        motor.runAtConstantSpeed(powerToRunAt);

        //sleep(2000);
        //motor.stop();

        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();
            //powerToRunAt = powerToRunAt + .01;
            //motor.setPower(powerToRunAt);
            telemetry.addData(">", "Not complete yet");
            telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
            telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        sleep(2000);

        //motor.moveToPosition(powerToRunAt, 360, DcMotor8863.FinishBehavior.FLOAT); // works
        //motor.moveByAmount(powerToRunAt, -360, DcMotor8863.FinishBehavior.FLOAT); // works
        // motor.rotateNumberOfRevolutions(powerToRunAt, 2, DcMotor8863.FinishBehavior.HOLD); // works
        //motor.rotateNumberOfDegrees(powerToRunAt, -720, DcMotor8863.FinishBehavior.HOLD); // works
        //motor.rotateNumberOfRevolutions(powerToRunAt, -2, DcMotor8863.FinishBehavior.HOLD); // works

        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();
            telemetry.addData(">", "Not complete yet");
            telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
            telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }


        telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
        telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
        telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
        telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
        telemetry.addData(">", "Movement complete");
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();

        while (opModeIsActive()) {
            idle();
        }

//        while (opModeIsActive()) {
//
//            // update the motor power
//            //motor.runAtConstantPower(powerToRunAt);
//
//            if (runningTimer.seconds() > lastTime + 1) {
//                lastTime++;
//            }
//
//            // Display the current value
//            telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
//            telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
//            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
//            telemetry.addData("Elapsed time = ", "%5.0f", lastTime);
//            telemetry.addData(">", "Press Stop to end test.");
//            telemetry.update();
//
//            idle();
//        }

        // Turn off motor and signal done;
        motor.setMotorToFloat();
        telemetry.addData(">", "Done");

        telemetry.update();

    }
}

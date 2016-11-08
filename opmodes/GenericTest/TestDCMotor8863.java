package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDControl;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This OpMode tests a DC motor and is meant to test the functionality of the DcMotor8863 class.
 */
@TeleOp(name = "Test DcMotor8863", group = "Test")
//@Disabled
public class TestDCMotor8863 extends LinearOpMode {

    DcMotor8863 motor;
    int feedback = 0;
    double powerToRunAt = .5; // % of full speed

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

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motor.");
        telemetry.update();
        waitForStart();

        runningTimer.reset();

        motor.rotateNumberOfRevolutions(powerToRunAt, 3, DcMotor8863.FinishBehavior.FLOAT);

        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();
            telemetry.addData(">", "Not complete yet");
            telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            telemetry.addData("feedback = ", "%5.2f", motor.getMotorPosition());
            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
            telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        sleep(2000);

        motor.rotateNumberOfRevolutions(powerToRunAt, 2, DcMotor8863.FinishBehavior.HOLD);

        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();
            telemetry.addData(">", "Not complete yet");
            telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            telemetry.addData("feedback = ", "%5.2f", motor.getMotorPosition());
            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
            telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }


        telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
        telemetry.addData("feedback = ", "%5.2f", motor.getMotorPosition());
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
//            telemetry.addData("feedback = ", "%5.2f", motor.getMotorPosition());
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

package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDControl;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Arm Tilt Motor PID", group = "Test")
//@Disabled
public class TestTiltArmMotorPID extends LinearOpMode {

    // Put your variable declarations here
    private DcMotor8863 tiltMotor;
    private PIDControl pidControl;
    ModernRoboticsI2cRangeSensor rangeSensor;
    private double correction;

    @Override
    public void runOpMode() {


        // Put your initializations here
        tiltMotor = new DcMotor8863("tiltMotor", hardwareMap);
        tiltMotor.setDirection(DcMotor.Direction.REVERSE);
        tiltMotor.setMaxMotorPower(1);
        tiltMotor.setMinMotorPower(-1);
        tiltMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        // the extension of the arm is to a set point, like on a ruler
        // it is not moving by an amount each time (relative)
        tiltMotor.setMotorMoveType(DcMotor8863.MotorMoveType.ABSOLUTE);
        // how close to the target encoder value do we have to get before we call it good enough
        // to stop the motor
        tiltMotor.setTargetEncoderTolerance(10);
        tiltMotor.setMovementPerRev(360);
        // the motor will apply power to hold its position after it arrives at the position
        tiltMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        // the motor will move to the target position and then narrow in on it - the motor controller
        // will control this for us
        tiltMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sleep(200);

        pidControl = new PIDControl(.02, 28, 1);
        pidControl.setThreshold(.5);
        pidControl.setUseRampControl(false);

        rangeSensor = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "armDistanceSensor");

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
       // tiltMotor.moveToPosition(.1, -15, DcMotor8863.FinishBehavior.HOLD);
        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            correction = -pidControl.getCorrection(rangeSensor.cmUltrasonic());
            tiltMotor.setPower(correction);
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            telemetry.addData("Encoder Count=", "%5d", tiltMotor.getCurrentPosition());
            telemetry.addData("Actual Position= ", "%3.1f", rangeSensor.cmUltrasonic());
            telemetry.addData("Set Point= ", "%3.1f", pidControl.getSetpoint());
            telemetry.addData("Correction= ", "%3.1f", correction);
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

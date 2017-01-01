package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDControl;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RampControl;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other.
 * It is meant to be used to test a double ball shooter
 * <p>
 * This code assumes a DC motor configured with the name "motor"  and "rightShooterMotor"
 */
@TeleOp(name = "Test PID Motor", group = "Test")
//@Disabled
public class TestPIDMotor extends LinearOpMode {

    DcMotor8863 motor;
    PIDControl pidControl;
    int feedback = 0;
    double powerToRunAt = 1.0; // 80% of full speed

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

        motor.setDirection(DcMotor.Direction.FORWARD);

        pidControl = new PIDControl(.005, 18000,.5); //Kp, target, speed limit

        pidControl.setupRamp(0, .5, 5000); //starting power, ending power, time in ms from start to finish

        // test internal routines from DcMotor8863
        telemetry.addData("Encoder Count for movement = ", "%d", motor.getEncoderCountForRevs(2.5));
        telemetry.update();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motor.");
        telemetry.update();
        waitForStart();

        runningTimer.reset();

        while (opModeIsActive()) {
            motor.runAtConstantPower(0);
            //run the PID calculation
            powerToRunAt = pidControl.getCorrection(motor.getPositionInTermsOfAttachment());
            // update the motor power
            motor.setPower(powerToRunAt);

            if (runningTimer.seconds() > lastTime + 1) {
                lastTime++;
            }

            // Display the current value
            telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
            telemetry.addData("Elapsed time = ", "%5.0f", lastTime);
            telemetry.update();

            idle();
        }

        // Turn off motor and signal done;
        motor.setMotorToFloat();
        telemetry.addData(">", "Done");

        telemetry.update();

    }
}

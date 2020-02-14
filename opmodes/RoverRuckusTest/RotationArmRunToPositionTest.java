package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Rotation Arm Run To Position Test", group = "Test")
@Disabled
public class RotationArmRunToPositionTest extends LinearOpMode {

    // Put your variable declarations here
    public DcMotor8863 collectorArmRotationMotor;
    public DcMotor8863.MotorState collectorArmRotationMotorState;
    public int collectorArmEncoderCount;
    public DataLogging logFile;

    @Override
    public void runOpMode() {


        // Put your initializations here
        logFile = new DataLogging("CollectorArm", telemetry);

        //collectorArmRotationMotor = hardwareMap.get(DcMotor.class,"collectorArmRotatorMotor");
        collectorArmRotationMotor = new DcMotor8863("collectorArmRotationMotor", hardwareMap, telemetry);
        collectorArmRotationMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_60);
        collectorArmRotationMotor.setMovementPerRev(360 * 48 / 128);
        collectorArmRotationMotor.setMotorToHold();

        collectorArmRotationMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        collectorArmRotationMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        collectorArmRotationMotor.setTargetEncoderTolerance(10);
        collectorArmRotationMotor.moveToPosition(.25, -120, DcMotor8863.FinishBehavior.HOLD);
        logFile.headerStrings("Encoder Count", "Motor State");
        logFile.startTimer();

        while (opModeIsActive() && collectorArmRotationMotor.update() != DcMotor8863.MotorState.COMPLETE_HOLD) {

            // Put your calls that need to run in a loop here
            collectorArmRotationMotorState = collectorArmRotationMotor.update();
            collectorArmEncoderCount = collectorArmRotationMotor.getCurrentPosition();
            telemetry.addData("Encoder count = ", collectorArmEncoderCount);
            telemetry.addData("Position (degrees) = ", collectorArmRotationMotor.getPositionInTermsOfAttachment());
            telemetry.addData("Motor state = ", collectorArmRotationMotorState.toString());
            logFile.logData(String.format("%d", collectorArmRotationMotor.getCurrentPosition()), collectorArmRotationMotorState.toString());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(1000);
        collectorArmRotationMotor.moveToPosition(.25, 10, DcMotor8863.FinishBehavior.HOLD);
        while (opModeIsActive() && collectorArmRotationMotor.update() != DcMotor8863.MotorState.COMPLETE_HOLD) {

            // Put your calls that need to run in a loop here
            collectorArmRotationMotorState = collectorArmRotationMotor.update();
            collectorArmEncoderCount = collectorArmRotationMotor.getCurrentPosition();
            telemetry.addData("Encoder count = ", collectorArmEncoderCount);
            telemetry.addData("Position (degrees) = ", collectorArmRotationMotor.getPositionInTermsOfAttachment());
            telemetry.addData("Motor state = ", collectorArmRotationMotorState.toString());
            logFile.logData(String.format("%d", collectorArmRotationMotor.getCurrentPosition()), collectorArmRotationMotorState.toString());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        // Put your cleanup code here - it runs as the application shuts down
        logFile.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

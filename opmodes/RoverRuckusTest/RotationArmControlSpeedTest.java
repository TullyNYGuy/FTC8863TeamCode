package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Rotation Arm Speed Test", group = "Test")
//@Disabled
public class RotationArmControlSpeedTest extends LinearOpMode {

    // Put your variable declarations here
    public DcMotor8863 collectorArmRotationMotor;

    @Override
    public void runOpMode() {


        // Put your initializations here
        //collectorArmRotationMotor = hardwareMap.get(DcMotor.class,"collectorArmRotatorMotor");
        collectorArmRotationMotor = new DcMotor8863("collectorArmRotatorMotor", hardwareMap, telemetry);
        collectorArmRotationMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        collectorArmRotationMotor.setMovementPerRev(360 * 48 / 128);
        collectorArmRotationMotor.setMotorToHold();

        collectorArmRotationMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        collectorArmRotationMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        collectorArmRotationMotor.setPower(.1);

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            telemetry.addData("Encoder count = ", collectorArmRotationMotor.getCurrentPosition());
            telemetry.addData("Position (degrees) = ", collectorArmRotationMotor.getPositionInTermsOfAttachment());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

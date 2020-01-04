package org.firstinspires.ftc.teamcode.opmodes.Skystone;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Lift Left & Right", group = "Test")
//@Disabled
public class TestLiftBothSides extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanism extensionRetractionMechanismLeft;
    public ExtensionRetractionMechanism extensionRetractionMechanismRight;

    public ExtensionRetractionMechanism.ExtensionRetractionStates extensionRetractionStateLeft;
    public ExtensionRetractionMechanism.ExtensionRetractionStates extensionRetractionStateRight;

    public int encoderValueLeft = 0;
    public int encoderValueMaxLeft = 0;
    public int encoderValueRight = 0;
    public int encoderValueMaxRight = 0;

    public DataLogging logFile;
    public double spoolDiameter = 1.25 * 25.4;
    public ElapsedTime timerLeft;
    public ElapsedTime timerRight;
    public double startTime = 0;
    public double endUpTimeLeft = 0;
    public double endUpTimeRight = 0;
    public double endDownTimeLeft = 0;
    public double endDownTimeRight = 0;
    public int encoderValueMax = 0;
    public int encoderValueMin = 0;

    public String buffer = "";

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionRetractionMechanismLeft = new ExtensionRetractionMechanism(hardwareMap, telemetry, "extensionRetractionLeft",
                "extensionLimitSwitchLeft", "retractionLimitSwitchLeft", "extensionRetractionMotorLeft",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);
        //extensionRetractionMechanismLeft.reverseMotor();

        extensionRetractionMechanismRight = new ExtensionRetractionMechanism(hardwareMap, telemetry, "extensionRetractionRight",
                "extensionLimitSwitchRight", "retractionLimitSwitchRight", "extensionRetractionMotorRight",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);
        extensionRetractionMechanismRight.reverseMotor();

        logFile = new DataLogging("ExtensionRetractionTest", telemetry);
        timerLeft = new ElapsedTime();
        timerRight = new ElapsedTime();
        extensionRetractionMechanismLeft.setDataLog(logFile);
        extensionRetractionMechanismLeft.enableDataLogging();
        extensionRetractionMechanismLeft.setResetPower(-0.1);
        extensionRetractionMechanismLeft.setRetractionPower(-.7);
        extensionRetractionMechanismLeft.setExtensionPower(+.7);

        extensionRetractionMechanismLeft.setExtensionPosition(2800.0);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        extensionRetractionMechanismLeft.testReset(this);
        extensionRetractionMechanismRight.testReset(this);
        sleep(3000);

        extensionRetractionMechanismLeft.goToFullExtend();
        extensionRetractionMechanismRight.goToFullExtend();
        timerLeft.reset();
        timerRight.reset();

        while (opModeIsActive() && !(extensionRetractionMechanismLeft.isExtensionComplete() && extensionRetractionMechanismRight.isExtensionComplete())) {

            extensionRetractionStateLeft = extensionRetractionMechanismLeft.update();
            extensionRetractionStateRight = extensionRetractionMechanismRight.update();

            encoderValueLeft = extensionRetractionMechanismLeft.getCurrentEncoderValue();
            encoderValueRight = extensionRetractionMechanismRight.getCurrentEncoderValue();

            if (encoderValueLeft > encoderValueMaxLeft) {
                encoderValueMaxLeft = encoderValueLeft;
            }

            if (encoderValueRight > encoderValueMaxRight) {
                encoderValueMaxRight = encoderValueRight;
            }

            telemetry.addData("Left state = ", extensionRetractionStateLeft.toString());
            telemetry.addData("Right state = ", extensionRetractionStateRight.toString());
            telemetry.addData("Left encoder = ", encoderValueLeft);
            telemetry.addData("Right encoder = ", encoderValueRight);
            telemetry.update();
            idle();
        }

        endUpTimeLeft = timerLeft.seconds();
        endUpTimeRight = timerRight.seconds();

        sleep (10000);

        extensionRetractionMechanismLeft.goToFullRetract();
        extensionRetractionMechanismRight.goToFullRetract();
        timerLeft.reset();
        timerRight.reset();

        while (opModeIsActive() && !(extensionRetractionMechanismLeft.isExtensionComplete() && extensionRetractionMechanismRight.isExtensionComplete())) {

            extensionRetractionStateLeft = extensionRetractionMechanismLeft.update();
            extensionRetractionStateRight = extensionRetractionMechanismRight.update();

            encoderValueLeft = extensionRetractionMechanismLeft.getCurrentEncoderValue();
            encoderValueRight = extensionRetractionMechanismRight.getCurrentEncoderValue();

            if (encoderValueLeft > encoderValueMaxLeft) {
                encoderValueMaxLeft = encoderValueLeft;
            }

            if (encoderValueRight > encoderValueMaxRight) {
                encoderValueMaxRight = encoderValueRight;
            }

            telemetry.addData("Left state = ", extensionRetractionStateLeft.toString());
            telemetry.addData("Right state = ", extensionRetractionStateRight.toString());
            telemetry.addData("Left encoder = ", encoderValueLeft);
            telemetry.addData("Right encoder = ", encoderValueRight);
            telemetry.update();
            idle();
        }

        endDownTimeLeft = timerLeft.seconds();
        endDownTimeRight = timerRight.seconds();

        buffer = String.format("%.2f", endUpTimeLeft) + " " + String.format("%.2d", endUpTimeRight);
        telemetry.addData("time up = ", buffer);
        buffer = String.format("%.2f", endDownTimeLeft) + " " + String.format("%.2f", endDownTimeRight);
        telemetry.addData("time down = ", buffer);
        buffer = String.format("%d", encoderValueMaxLeft) + " " + String.format("%d", encoderValueMaxRight);
        telemetry.addData("max encoder value = ", buffer);
        telemetry.addData("min encoder value = ", encoderValueMin);
        telemetry.addData(">", "Done");
        telemetry.update();

        // wait for user to kill the app
        while (opModeIsActive()) {
            idle();
        }
    }

    // Put your cleanup code here - it runs as the application shuts down
}

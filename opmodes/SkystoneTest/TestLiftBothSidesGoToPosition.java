package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Lift Left & Right Go To Position", group = "Test")
//@Disabled
public class TestLiftBothSidesGoToPosition extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanism extensionRetractionMechanismLeft;
    public ExtensionRetractionMechanism extensionRetractionMechanismRight;

    public ExtensionRetractionMechanism.ExtensionRetractionStates extensionRetractionStateLeft;
    public ExtensionRetractionMechanism.ExtensionRetractionStates extensionRetractionStateRight;

    public int encoderValueLeft = 0;
    public int encoderValueMaxLeft = 0;
    public int encoderValueRight = 0;
    public int encoderValueMaxRight = 0;
    public int encoderValueMinLeft = 0;
    public int encoderValueMinRight = 0;

    public DataLogging logFileLeft;
    public DataLogging logFileRight;
    public CSVDataFile timeEncoderValueFile;
    public double spoolDiameter = 1.25; //inches
    // spool diameter * pi * 5 stages
    public double movementPerRevolution = spoolDiameter * Math.PI * 5;
    public ElapsedTime timerLeft;
    public ElapsedTime timerRight;
    public double startTime = 0;
    public double endUpTimeLeft = 0;
    public double endUpTimeRight = 0;
    public double endDownTimeLeft = 0;
    public double endDownTimeRight = 0;

    public String buffer = "";

    public double speed = 1.0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionRetractionMechanismLeft = new ExtensionRetractionMechanism(hardwareMap, telemetry, "extensionRetractionLeft",
                "extensionLimitSwitchLeft", "retractionLimitSwitchLeft", "extensionRetractionMotorLeft",
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);
        //extensionRetractionMechanismLeft.reverseMotor();

        extensionRetractionMechanismRight = new ExtensionRetractionMechanism(hardwareMap, telemetry, "extensionRetractionRight",
                "extensionLimitSwitchRight", "retractionLimitSwitchRight", "extensionRetractionMotorRight",
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);
        extensionRetractionMechanismRight.reverseMotor();

        timerLeft = new ElapsedTime();
        timerRight = new ElapsedTime();

        logFileLeft = new DataLogging("ExtensionRetractionTestLeft", telemetry);
        logFileRight = new DataLogging("ExtensionRetractionTestRight", telemetry);
        timeEncoderValueFile = new CSVDataFile("LiftTimeEncoderValues", telemetry);

        extensionRetractionMechanismLeft.setDataLog(logFileLeft);
        extensionRetractionMechanismLeft.enableDataLogging();
        extensionRetractionMechanismLeft.enableCollectData();
        extensionRetractionMechanismLeft.setResetPower(-0.1);
        extensionRetractionMechanismLeft.setRetractionPower(-speed);
        extensionRetractionMechanismLeft.setExtensionPower(+speed);

        extensionRetractionMechanismLeft.setExtensionPositionInMechanismUnits(9.5 * 5); //inches * 5 stages

        //logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;
        extensionRetractionMechanismRight.setDataLog(logFileRight);
        extensionRetractionMechanismRight.enableDataLogging();
        extensionRetractionMechanismRight.enableCollectData();
        extensionRetractionMechanismRight.setResetPower(-0.1);
        extensionRetractionMechanismRight.setRetractionPower(-speed);
        extensionRetractionMechanismRight.setExtensionPower(+speed);

        extensionRetractionMechanismRight.setExtensionPositionInMechanismUnits(9.5 * 5); //inches * 5 stages

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        extensionRetractionMechanismLeft.testReset(this);
        extensionRetractionMechanismRight.testReset(this);
        sleep(3000);

        extensionRetractionMechanismLeft.goToPosition(24.0, speed);
        extensionRetractionMechanismRight.goToPosition(24.0,speed);
        timerLeft.reset();
        timerRight.reset();

        while (opModeIsActive() && !(extensionRetractionMechanismLeft.isPositionReached() && extensionRetractionMechanismRight.isPositionReached())) {

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

        // have to update the state machine in order to generate the last state update
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        endUpTimeLeft = timerLeft.seconds();
        endUpTimeRight = timerRight.seconds();

        buffer = String.format("%.2f", endUpTimeLeft) + " " + String.format("%.2f", endUpTimeRight);
        telemetry.addData("time up = ", buffer);
        telemetry.update();

        sleep (2000);

        extensionRetractionMechanismLeft.goToPosition(5, speed);
        extensionRetractionMechanismRight.goToPosition(5, speed);
        timerLeft.reset();
        timerRight.reset();

        // have to update the state machine in order to get the go to position command to register
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        while (opModeIsActive() && !(extensionRetractionMechanismLeft.isPositionReached() && extensionRetractionMechanismRight.isPositionReached())) {

            extensionRetractionStateLeft = extensionRetractionMechanismLeft.update();
            extensionRetractionStateRight = extensionRetractionMechanismRight.update();

            encoderValueLeft = extensionRetractionMechanismLeft.getCurrentEncoderValue();
            encoderValueRight = extensionRetractionMechanismRight.getCurrentEncoderValue();

            if (encoderValueLeft < encoderValueMinLeft) {
                encoderValueMinLeft = encoderValueLeft;
            }

            if (encoderValueRight < encoderValueMinRight) {
                encoderValueMinRight = encoderValueRight;
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


        // have to update the state machine in order to generate the last state update
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        buffer = String.format("%.2f", endDownTimeLeft) + " " + String.format("%.2f", endDownTimeRight);
        telemetry.addData("time down = ", buffer);
        telemetry.update();

        sleep (2000);

        //*****************************************************************************************

        extensionRetractionMechanismLeft.goToPosition(30.0, speed);
        extensionRetractionMechanismRight.goToPosition(30.0,speed);
        timerLeft.reset();
        timerRight.reset();


        // have to update the state machine in order to get the go to position command to register
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        while (opModeIsActive() && !(extensionRetractionMechanismLeft.isPositionReached() && extensionRetractionMechanismRight.isPositionReached())) {

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

        // have to update the state machine in order to generate the last state update
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        endUpTimeLeft = timerLeft.seconds();
        endUpTimeRight = timerRight.seconds();

        buffer = String.format("%.2f", endUpTimeLeft) + " " + String.format("%.2f", endUpTimeRight);
        telemetry.addData("time up = ", buffer);
        telemetry.update();

        sleep (2000);

        extensionRetractionMechanismLeft.goToPosition(10, speed);
        extensionRetractionMechanismRight.goToPosition(10, speed);
        timerLeft.reset();
        timerRight.reset();

        // have to update the state machine in order to get the go to position command to register
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        while (opModeIsActive() && !(extensionRetractionMechanismLeft.isPositionReached() && extensionRetractionMechanismRight.isPositionReached())) {

            extensionRetractionStateLeft = extensionRetractionMechanismLeft.update();
            extensionRetractionStateRight = extensionRetractionMechanismRight.update();

            encoderValueLeft = extensionRetractionMechanismLeft.getCurrentEncoderValue();
            encoderValueRight = extensionRetractionMechanismRight.getCurrentEncoderValue();

            if (encoderValueLeft < encoderValueMinLeft) {
                encoderValueMinLeft = encoderValueLeft;
            }

            if (encoderValueRight < encoderValueMinRight) {
                encoderValueMinRight = encoderValueRight;
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


        // have to update the state machine in order to generate the last state update
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        buffer = String.format("%.2f", endDownTimeLeft) + " " + String.format("%.2f", endDownTimeRight);
        telemetry.addData("time down = ", buffer);
        telemetry.update();

        sleep (2000);

        //*******************************************************************************************
        //*****************************************************************************************

        extensionRetractionMechanismLeft.goToPosition(20.0, speed);
        extensionRetractionMechanismRight.goToPosition(20.0,speed);
        timerLeft.reset();
        timerRight.reset();


        // have to update the state machine in order to get the go to position command to register
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        while (opModeIsActive() && !(extensionRetractionMechanismLeft.isPositionReached() && extensionRetractionMechanismRight.isPositionReached())) {

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

        // have to update the state machine in order to generate the last state update
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        endUpTimeLeft = timerLeft.seconds();
        endUpTimeRight = timerRight.seconds();

        buffer = String.format("%.2f", endUpTimeLeft) + " " + String.format("%.2f", endUpTimeRight);
        telemetry.addData("time up = ", buffer);
        telemetry.update();

        sleep (2000);

        extensionRetractionMechanismLeft.goToPosition(5, speed);
        extensionRetractionMechanismRight.goToPosition(5, speed);
        timerLeft.reset();
        timerRight.reset();

        // have to update the state machine in order to get the go to position command to register
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        while (opModeIsActive() && !(extensionRetractionMechanismLeft.isPositionReached() && extensionRetractionMechanismRight.isPositionReached())) {

            extensionRetractionStateLeft = extensionRetractionMechanismLeft.update();
            extensionRetractionStateRight = extensionRetractionMechanismRight.update();

            encoderValueLeft = extensionRetractionMechanismLeft.getCurrentEncoderValue();
            encoderValueRight = extensionRetractionMechanismRight.getCurrentEncoderValue();

            if (encoderValueLeft < encoderValueMinLeft) {
                encoderValueMinLeft = encoderValueLeft;
            }

            if (encoderValueRight < encoderValueMinRight) {
                encoderValueMinRight = encoderValueRight;
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


        // have to update the state machine in order to generate the last state update
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        buffer = String.format("%.2f", endDownTimeLeft) + " " + String.format("%.2f", endDownTimeRight);
        telemetry.addData("time down = ", buffer);
        telemetry.update();

        sleep (2000);

        //*******************************************************************************************
        //*****************************************************************************************

        extensionRetractionMechanismLeft.goToPosition(30.0, speed);
        extensionRetractionMechanismRight.goToPosition(30.0,speed);
        timerLeft.reset();
        timerRight.reset();


        // have to update the state machine in order to get the go to position command to register
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        while (opModeIsActive() && !(extensionRetractionMechanismLeft.isPositionReached() && extensionRetractionMechanismRight.isPositionReached())) {

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

        // have to update the state machine in order to generate the last state update
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        endUpTimeLeft = timerLeft.seconds();
        endUpTimeRight = timerRight.seconds();

        buffer = String.format("%.2f", endUpTimeLeft) + " " + String.format("%.2f", endUpTimeRight);
        telemetry.addData("time up = ", buffer);
        telemetry.update();

        sleep (2000);

        extensionRetractionMechanismLeft.goToPosition(5, speed);
        extensionRetractionMechanismRight.goToPosition(5, speed);
        timerLeft.reset();
        timerRight.reset();

        // have to update the state machine in order to get the go to position command to register
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        while (opModeIsActive() && !(extensionRetractionMechanismLeft.isPositionReached() && extensionRetractionMechanismRight.isPositionReached())) {

            extensionRetractionStateLeft = extensionRetractionMechanismLeft.update();
            extensionRetractionStateRight = extensionRetractionMechanismRight.update();

            encoderValueLeft = extensionRetractionMechanismLeft.getCurrentEncoderValue();
            encoderValueRight = extensionRetractionMechanismRight.getCurrentEncoderValue();

            if (encoderValueLeft < encoderValueMinLeft) {
                encoderValueMinLeft = encoderValueLeft;
            }

            if (encoderValueRight < encoderValueMinRight) {
                encoderValueMinRight = encoderValueRight;
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


        // have to update the state machine in order to generate the last state update
        extensionRetractionMechanismLeft.update();
        extensionRetractionMechanismRight.update();

        buffer = String.format("%.2f", endDownTimeLeft) + " " + String.format("%.2f", endDownTimeRight);
        telemetry.addData("time down = ", buffer);
        telemetry.update();

        sleep (2000);

        //*******************************************************************************************

        extensionRetractionMechanismLeft.writeTimerEncoderDataToCSVFile(timeEncoderValueFile);
        extensionRetractionMechanismRight.writeTimerEncoderDataToCSVFile(timeEncoderValueFile);

        extensionRetractionMechanismRight.shutdown();
        extensionRetractionMechanismLeft.shutdown();

        buffer = String.format("%.2f", endUpTimeLeft) + " " + String.format("%.2f", endUpTimeRight);
        telemetry.addData("time up = ", buffer);
        buffer = String.format("%.2f", endDownTimeLeft) + " " + String.format("%.2f", endDownTimeRight);
        telemetry.addData("time down = ", buffer);
        buffer = String.format("%d", encoderValueMaxLeft) + " " + String.format("%d", encoderValueMaxRight);
        telemetry.addData("max encoder value = ", buffer);
        buffer = String.format("%d", encoderValueMinLeft) + " " + String.format("%d", encoderValueMinRight);
        telemetry.addData("min encoder value = ", buffer);
        telemetry.addData(">", "Done");
        telemetry.update();

        // wait for user to kill the app
        while (opModeIsActive()) {
            idle();
        }
    }

    // Put your cleanup code here - it runs as the application shuts down
}

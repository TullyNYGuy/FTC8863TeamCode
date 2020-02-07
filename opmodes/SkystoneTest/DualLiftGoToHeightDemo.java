package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.DualLift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "AAAAAAAAH Dual Lift Demo", group = "RUN")
//@Disabled
public class DualLiftGoToHeightDemo extends LinearOpMode {

    // Put your variable declarations here
    public Lift liftLeft;
    public Lift liftRight;

    public Lift.ExtensionRetractionStates extensionRetractionStateLeft;
    public Lift.ExtensionRetractionStates extensionRetractionStateRight;

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

    public double speed = 0.2;

    private DualLift dualLift;

    @Override
    public void runOpMode() {
        dualLift = new DualLift(hardwareMap,
                "LiftRight",
                "LiftMotortRight",
                "LiftExtensionLimitSwitchRight",
                "LiftRetractionLimitSwitchRight",
                "LiftLeft",
                "LiftMotortLeft",
                "LiftExtensionLimitSwitchLeft",
                "LiftRetractionLimitSwitchLeft",
                telemetry);


        timerLeft = new ElapsedTime();
        timerRight = new ElapsedTime();

        logFileLeft = new DataLogging("ExtensionRetractionTestLeft", telemetry);
        logFileRight = new DataLogging("ExtensionRetractionTestRight", telemetry);
        timeEncoderValueFile = new CSVDataFile("LiftTimeEncoderValues");

        liftLeft.setDataLog(logFileLeft);
        liftLeft.enableDataLogging();
        liftLeft.enableCollectData();
        liftLeft.setResetPower(-0.1);
        liftLeft.setRetractionPower(-speed);
        liftLeft.setExtensionPower(+speed);

        liftLeft.setExtensionPositionInMechanismUnits(9.5 * 5); //inches * 5 stages

        //logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;
        liftRight.setDataLog(logFileRight);
        liftRight.enableDataLogging();
        liftRight.enableCollectData();
        liftRight.setResetPower(-0.1);
        liftRight.setRetractionPower(-speed);
        liftRight.setExtensionPower(+speed);

        liftRight.setExtensionPositionInMechanismUnits(9.5 * 5); //inches * 5 stages

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timerLeft.reset();
        timerRight.reset();

        liftLeft.reset();
        liftRight.reset();

        while (opModeIsActive() && !(liftLeft.isResetComplete() && liftRight.isResetComplete())) {
            liftLeft.update();
            liftRight.update();
        }

        sleep(500);

        dualLift.goToBlockHeights(1);

        while (opModeIsActive() && !(dualLift.isPositionReached())) {

            //logFileLeft.logData("in loop for first go to position");
            //logFileRight.logData("in loop for first go to position");

            liftLeft.update();

            extensionRetractionStateLeft = liftLeft.getExtensionRetractionState();
            liftRight.getExtensionRetractionState();
            extensionRetractionStateRight = liftRight.getExtensionRetractionState();

            encoderValueLeft = dualLift.getCurrentEncoderValueLeft();
            encoderValueRight = dualLift.getCurrentEncoderValueRight();

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

        dualLift.goToBlockHeights(2);

        while (opModeIsActive() && !(dualLift.isPositionReached())) {

            //logFileLeft.logData("in loop for first go to position");
            //logFileRight.logData("in loop for first go to position");

            liftLeft.update();

            extensionRetractionStateLeft = liftLeft.getExtensionRetractionState();
            liftRight.getExtensionRetractionState();
            extensionRetractionStateRight = liftRight.getExtensionRetractionState();

            encoderValueLeft = dualLift.getCurrentEncoderValueLeft();
            encoderValueRight = dualLift.getCurrentEncoderValueRight();

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

        dualLift.goToBlockHeights(3);

        while (opModeIsActive() && !(dualLift.isPositionReached())) {

            //logFileLeft.logData("in loop for first go to position");
            //logFileRight.logData("in loop for first go to position");

            liftLeft.update();

            extensionRetractionStateLeft = liftLeft.getExtensionRetractionState();
            liftRight.getExtensionRetractionState();
            extensionRetractionStateRight = liftRight.getExtensionRetractionState();

            encoderValueLeft = dualLift.getCurrentEncoderValueLeft();
            encoderValueRight = dualLift.getCurrentEncoderValueRight();

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

        dualLift.goToBlockHeights(4);

        while (opModeIsActive() && !(dualLift.isPositionReached())) {

            //logFileLeft.logData("in loop for first go to position");
            //logFileRight.logData("in loop for first go to position");

            liftLeft.update();

            extensionRetractionStateLeft = liftLeft.getExtensionRetractionState();
            liftRight.getExtensionRetractionState();
            extensionRetractionStateRight = liftRight.getExtensionRetractionState();

            encoderValueLeft = dualLift.getCurrentEncoderValueLeft();
            encoderValueRight = dualLift.getCurrentEncoderValueRight();

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

        dualLift.goToBlockHeights(5);

        while (opModeIsActive() && !(dualLift.isPositionReached())) {

            //logFileLeft.logData("in loop for first go to position");
            //logFileRight.logData("in loop for first go to position");

            liftLeft.update();

            extensionRetractionStateLeft = liftLeft.getExtensionRetractionState();
            liftRight.getExtensionRetractionState();
            extensionRetractionStateRight = liftRight.getExtensionRetractionState();

            encoderValueLeft = dualLift.getCurrentEncoderValueLeft();
            encoderValueRight = dualLift.getCurrentEncoderValueRight();

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

        dualLift.goToBlockHeights(6);

        while (opModeIsActive() && !(dualLift.isPositionReached())) {

            //logFileLeft.logData("in loop for first go to position");
            //logFileRight.logData("in loop for first go to position");

            liftRight.update();
            extensionRetractionStateRight = liftRight.getExtensionRetractionState();
            liftLeft.update();
            extensionRetractionStateLeft = liftLeft.getExtensionRetractionState();


            encoderValueLeft = dualLift.getCurrentEncoderValueLeft();
            encoderValueRight = dualLift.getCurrentEncoderValueRight();

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

        buffer = String.format("%.2f", endUpTimeLeft) + " " + String.format("%.2f", endUpTimeRight);
        telemetry.addData("time up = ", buffer);
        telemetry.update();
        dualLift.goToBottom();


        while (opModeIsActive() && !(dualLift.isPositionReached())) {

            //logFileLeft.logData("in loop for first go to position");
            //logFileRight.logData("in loop for first go to position");

            liftRight.update();
            extensionRetractionStateRight = liftRight.getExtensionRetractionState();
            liftLeft.update();
            extensionRetractionStateLeft = liftLeft.getExtensionRetractionState();


            encoderValueLeft = dualLift.getCurrentEncoderValueLeft();
            encoderValueRight = dualLift.getCurrentEncoderValueRight();

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
        //liftLeft.update();
        //liftRight.update();


        sleep(5000);

        /*
        liftLeft.goToPosition(5, 0.5);
        liftRight.goToPosition(5, 0.5);
        timerLeft.reset();
        timerRight.reset();

        // have to update the state machine in order to get the go to position command to register
        //liftLeft.update();
        //liftRight.update();

        while (opModeIsActive() && !(liftLeft.isPositionReached() && liftRight.isPositionReached())) {

            logFileLeft.logData("in loop for second go to position");
            logFileRight.logData("in loop for second go to position");


            extensionRetractionStateLeft = liftLeft.update();
            extensionRetractionStateRight = liftRight.update();

            encoderValueLeft = liftLeft.getCurrentEncoderValue();
            encoderValueRight = liftRight.getCurrentEncoderValue();

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
        liftLeft.update();
        liftRight.update();

        buffer = String.format("%.2f", endDownTimeLeft) + " " + String.format("%.2f", endDownTimeRight);
        telemetry.addData("time down = ", buffer);
        telemetry.update();

        liftLeft.writeTimerEncoderDataToCSVFile(timeEncoderValueFile);
        liftRight.writeTimerEncoderDataToCSVFile(timeEncoderValueFile);

        //liftRight.shutdown();
        //liftLeft.shutdown();

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
        logFileLeft.logData("program done");
        logFileRight.logData("program done");
        */
        while (opModeIsActive()) {
            // hold waiting for the user to terminate the program
        }
    }
    // Put your cleanup code here - it runs as the application shuts down
}

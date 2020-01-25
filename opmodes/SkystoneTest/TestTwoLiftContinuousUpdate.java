package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Two Lift Demo with One Update", group = "DEMO")
//@Disabled
public class TestTwoLiftContinuousUpdate extends LinearOpMode {

    public enum Steps {
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        ELEVEN,
        TWELVE,
        THIRTEEN
    }

    public Steps steps = Steps.ZERO;
    public ElapsedTime stateTimer;
    public double waitTime = 2000; // milliseconds

    // Put your variable declarations here
    public Lift liftLeft;
    public Lift liftRight;

    public Lift.ExtensionRetractionStates extensionRetractionStateLeft;
    public Lift.ExtensionRetractionStates extensionRetractionStateRight;

    public int encoderValueLeft = 0;

    public int encoderValueRight = 0;

    public DataLogging logFileLeft;
    public DataLogging logFileRight;
    public CSVDataFile timeEncoderValueFile;
    public double spoolDiameter = 1.25; //inches
    // spool diameter * pi * 5 stages
    public double movementPerRevolution = spoolDiameter * Math.PI * 5;
    public ElapsedTime timerLeft;
    public ElapsedTime timerRight;
    public double startTime = 0;

    public String buffer = "";

    public double speed = 0.3;

    @Override
    public void runOpMode() {


        // Put your initializations here
        liftLeft = new Lift(hardwareMap, telemetry, "liftLeft",
                "extensionLimitSwitchLiftLeft", "retractionLimitSwitchLiftLeft", "liftMotorLeft",
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);
        //liftLeft.reverseMotor();

        liftRight = new Lift(hardwareMap, telemetry, "liftRight",
                "extensionLimitSwitchLiftRight", "retractionLimitSwitchLiftRight", "liftMotorRight",
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);
        liftRight.reverseMotor();

        timerLeft = new ElapsedTime();
        timerRight = new ElapsedTime();
        stateTimer = new ElapsedTime();

        logFileLeft = new DataLogging("LiftTestLeft", telemetry);
        logFileRight = new DataLogging("LiftTestRight", telemetry);
        timeEncoderValueFile = new CSVDataFile("LiftTimeEncoderValues", telemetry);

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

        while (opModeIsActive()) {
            extensionRetractionStateLeft = liftLeft.update();
            extensionRetractionStateRight = liftRight.update();


            switch (steps) {
                case ZERO:
                    liftLeft.init();
                    liftRight.init();
                    steps = Steps.ONE;
                case ONE:
                    if (liftLeft.isInitComplete() && liftRight.isInitComplete()) {
                        liftLeft.goToPosition(10, speed);
                        liftRight.goToPosition(10, speed);
                        steps = Steps.TWO;
                    }
                    break;
                case TWO:
                    if (liftLeft.isPositionReached() && liftRight.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.THREE;
                    }
                    break;
                case THREE:
                    if (stateTimer.milliseconds() > waitTime) {
                        liftLeft.goToPosition(20, speed);
                        liftRight.goToPosition(20, speed);
                        steps = Steps.FOUR;
                    }
                    break;
                case FOUR:
                    if (liftLeft.isPositionReached() && liftRight.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.FIVE;
                    }
                    break;
                case FIVE:
                    if (stateTimer.milliseconds() > waitTime) {
                        liftLeft.goToPosition(5, speed);
                        liftRight.goToPosition(5, speed);
                        steps = Steps.SIX;
                    }
                    break;
                case SIX:
                    if (liftLeft.isPositionReached() && liftRight.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.SEVEN;
                    }
                    break;
                case SEVEN:
                    if (stateTimer.milliseconds() > waitTime) {
                        liftLeft.goToPosition(40, speed);
                        liftRight.goToPosition(40, speed);
                        steps = Steps.EIGHT;
                    }
                    break;
                case EIGHT:
                    if (liftLeft.isPositionReached() && liftRight.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.NINE;
                    }
                    break;
                case NINE:
                    if (stateTimer.milliseconds() > waitTime) {
                        liftLeft.goToPosition(5, speed);
                        liftRight.goToPosition(5, speed);
                        steps = Steps.TEN;
                    }
                    break;
                case TEN:
                    if (liftLeft.isPositionReached() && liftRight.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.ELEVEN;
                    }
                    break;
                case ELEVEN:
                    if (stateTimer.milliseconds() > waitTime) {
                        liftLeft.reset();
                        liftRight.reset();
                        steps = Steps.TWELVE;
                    }
                    ;
                    break;
                case TWELVE:
                    break;
            }

            telemetry.addData("Left state = ", extensionRetractionStateLeft.toString());
            telemetry.addData("Right state = ", extensionRetractionStateRight.toString());
            telemetry.addData("Left encoder = ", encoderValueLeft);
            telemetry.addData("Right encoder = ", encoderValueRight);
            telemetry.update();
            idle();
        }
    }

    // Put your cleanup code here - it runs as the application shuts down
}

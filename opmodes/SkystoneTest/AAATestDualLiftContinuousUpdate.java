package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.DualLift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "A MEGA-EPIC Dual Lift Test", group = "DEMO")
//@Disabled
public class AAATestDualLiftContinuousUpdate extends LinearOpMode {

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
        THIRTEEN,
        FOURTEEN,
        FIFTEEN,
        SIXTEEN,
        SEVENTEEN
    }

    public Steps steps = Steps.ZERO;
    public ElapsedTime stateTimer;
    public double waitTime = 2000; // milliseconds

    // Put your variable declarations here
    public DualLift dualLift;

    public double positionPower;

    public ExtensionRetractionMechanism.ExtensionRetractionStates dualLiftState;


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

    public DataLogging logFileBoth;

    @Override
    public void runOpMode() {


        // Put your initializations here
        dualLift = new DualLift(hardwareMap, telemetry, positionPower);

        timerLeft = new ElapsedTime();
        timerRight = new ElapsedTime();
        stateTimer = new ElapsedTime();

        logFileBoth = new DataLogging("LiftTestBoth", telemetry);
        timeEncoderValueFile = new CSVDataFile("LiftTimeEncoderValues", telemetry);

        dualLift.setDataLog(logFileBoth);
        dualLift.enableDataLogging();
        dualLift.enableCollectData();
        dualLift.setResetPower(-0.1);
        dualLift.setRetractionPower(-speed);
        dualLift.setExtensionPower(+speed);

        dualLift.setExtensionPositionInMechanismUnits(9.5 * 5); //inches * 5 stages


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timerLeft.reset();
        timerRight.reset();

        while (opModeIsActive()) {
            dualLiftState = dualLift.update();


            switch (steps) {
                case ZERO:
                    dualLift.init();
                    steps = Steps.ONE;
                case ONE:
                    if (dualLift.isInitComplete()) {
                        dualLift.goToPosition(10, speed);
                        steps = Steps.TWO;
                    }
                    break;
                case TWO:
                    if (dualLift.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.THREE;
                    }
                    break;
                case THREE:
                    if (stateTimer.milliseconds() > waitTime) {
                        dualLift.goToPosition(20, speed);
                        steps = Steps.FOUR;
                    }
                    break;
                case FOUR:
                    if (dualLift.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.FIVE;
                    }
                    break;
                case FIVE:
                    if (stateTimer.milliseconds() > waitTime) {
                        dualLift.goToPosition(5, speed);
                        steps = Steps.SIX;
                    }
                    break;
                case SIX:
                    if (dualLift.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.SEVEN;
                    }
                    break;
                case SEVEN:
                    if (stateTimer.milliseconds() > waitTime) {
                        dualLift.goToPosition(40, speed);
                        steps = Steps.EIGHT;
                    }
                    break;
                case EIGHT:
                    if (dualLift.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.NINE;
                    }
                    break;
                case NINE:
                    if (stateTimer.milliseconds() > waitTime) {
                        dualLift.goToPosition(5, speed);
                        steps = Steps.TEN;
                    }
                    break;
                case TEN:
                    if (dualLift.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.ELEVEN;
                    }
                    break;
                case ELEVEN:
                    if (stateTimer.milliseconds() > waitTime) {
                        dualLift.goToBlockHeights(5);
                        steps = Steps.TWELVE;
                    }
                    break;
                case TWELVE:
                    if (dualLift.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.THIRTEEN;
                    }
                    ;
                    break;
                case THIRTEEN:
                    if (stateTimer.milliseconds() > waitTime) {
                        dualLift.goToBlockHeights(10);
                        steps = Steps.FOURTEEN;
                    }

                    break;
                case FOURTEEN:
                    if (dualLift.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.ELEVEN;
                    }
                    break;
                case FIFTEEN:
                    if (stateTimer.milliseconds() > waitTime) {
                        dualLift.goToBlockHeights(1);
                        steps = Steps.SIXTEEN;
                    }
                    break;
                case SIXTEEN:
                    if (dualLift.isPositionReached()) {
                        steps = Steps.SEVENTEEN;
                    }
                    break;
                case SEVENTEEN:
                    break;
            }

            // telemetry.addData("The One True state = ", dualLift.toString());
            telemetry.addData("The One True encoder R = ", dualLift.getCurrentEncoderValueRight());
            telemetry.addData("The One True encoder L = ", dualLift.getCurrentEncoderValueLeft());
            telemetry.update();
            idle();
        }
    }

    // Put your cleanup code here - it runs as the application shuts down
}

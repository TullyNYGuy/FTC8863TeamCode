package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.hardware.motors.NeveRest40Gearmotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArm;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test EXTENSION ARM GO TO POSITION", group = "Test")
//@Disabled
public class TestExtensionArmGoToPosition extends LinearOpMode {

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
    public ExtensionArm extensionArm;

    public ExtensionRetractionMechanism.ExtensionRetractionStates extensionArmState;


    //public Lift.ExtensionRetractionStates extensionRetractionStateLeft;

    public int encoderValue = 0;


    public DataLogging logFile;
    public CSVDataFile timeEncoderValueFile;
    public double spoolDiameter = 2.75; //inches
    // spool diameter * pi * 5 stages
    public double movementPerRevolution = spoolDiameter * Math.PI * 2;
    public ElapsedTime timer;
    public double startTime = 0;

    public String buffer = "";

    public double speed = 1.0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionArm = new ExtensionArm(hardwareMap, telemetry, "Extension Arm", "extensionLimitSwitchArm",
                "retractionLimitSwitchArm", "extensionArmEncoder", DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);


        timer = new ElapsedTime();
        stateTimer = new ElapsedTime();

        logFile = new DataLogging("MegaEpicExtensionArmTest", telemetry);
        timeEncoderValueFile = new CSVDataFile("LiftTimeEncoderValues", telemetry);


        //logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;
        extensionArm.setDataLog(logFile);
        extensionArm.enableDataLogging();
        extensionArm.enableCollectData();
        extensionArm.setResetPower(-0.1);
        extensionArm.setRetractionPower(-speed);
        extensionArm.setExtensionPower(+speed);

        extensionArm.setExtensionPositionInMechanismUnits(14 * 2); //inches * 5 stages

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();

        while (opModeIsActive()) {
            extensionArmState = extensionArm.update();


            switch (steps) {
                case ZERO:
                    extensionArm.init();
                    steps = Steps.ONE;
                case ONE:
                    if (extensionArm.isInitComplete()) {
                        extensionArm.goToPosition(10, speed);
                        steps = Steps.TWO;
                    }
                    break;
                case TWO:
                    if (extensionArm.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.THREE;
                    }
                    break;
                case THREE:
                    if (stateTimer.milliseconds() > waitTime) {
                        extensionArm.goToPosition(20, speed);
                        steps = Steps.FOUR;
                    }
                    break;
                case FOUR:
                    if (extensionArm.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.FIVE;
                    }
                    break;
                case FIVE:
                    if (stateTimer.milliseconds() > waitTime) {
                        extensionArm.goToPosition(5, speed);
                        steps = Steps.SIX;
                    }
                    break;
                case SIX:
                    if (extensionArm.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.SEVEN;
                    }
                    break;
                case SEVEN:
                    if (stateTimer.milliseconds() > waitTime) {
                        extensionArm.goToPosition(12, speed);
                        steps = Steps.EIGHT;
                    }
                    break;
                case EIGHT:
                    if (extensionArm.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.NINE;
                    }
                    break;
                case NINE:
                    if (stateTimer.milliseconds() > waitTime) {
                        extensionArm.goToPosition(3, speed);
                        steps = Steps.TEN;
                    }
                    break;
                case TEN:
                    if (extensionArm.isPositionReached()) {
                        stateTimer.reset();
                        steps = Steps.ELEVEN;
                    }
                    break;
                case ELEVEN:
                    if (stateTimer.milliseconds() > waitTime) {
                        extensionArm.reset();
                        steps = Steps.TWELVE;
                    }
                    ;
                    break;
                case TWELVE:
                    break;
            }

            telemetry.addData("STEP = ", steps.toString());
            telemetry.addData("STATE = ", extensionArmState.toString());
            telemetry.addData("ENCODER = ", encoderValue);
            telemetry.update();
            idle();
        }
    }

    // Put your cleanup code here - it runs as the application shuts down
}

package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.DualLift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Dual Lift Go To Position", group = "Test")
//@Disabled
public class TestDualLiftGoToPosition extends LinearOpMode {

    // Put your variable declarations here

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
        SEVENTEEN,
        EIGHTEEN,
        NINETEEN
    }

    public Steps step = Steps.ZERO;

    public DualLift lift;

    public Lift.ExtensionRetractionStates extensionRetractionStateRight;
    public Lift.ExtensionRetractionStates extensionRetractionStateLeft;

    public int encoderValueRight = 0;
    public int encoderValueMaxRight = 0;
    public int encoderValueMinRight = 0;

    public int encoderValueLeft = 0;
    public int encoderValueMaxLeft = 0;
    public int encoderValueMinLeft = 0;

    public DataLogging logFile;

    public CSVDataFile timeEncoderValueFile;
    //public double spoolDiameter = 1.25; //inches
    // spool diameter * pi * 5 stages
    //public double movementPerRevolution = spoolDiameter * Math.PI * 5;

    public ElapsedTime timer;

    public double startTime = 0;

    public double endUpTimeRight = 0;
    public double endDownTimeRight = 0;

    public double endUpTimeLeft = 0;
    public double endDownTimeLeft = 0;

    public String buffer = "";

    public double speed = 0.3;

    @Override
    public void runOpMode() {

        // Put your initializations here
        lift = new DualLift(hardwareMap,
                SkystoneRobot.HardwareName.LIFT_RIGHT_NAME.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_MOTOR.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_RETRACTION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_ZERO_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_NAME.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_MOTOR.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_RETRACTION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_ZERO_SWITCH.hwName,
                telemetry);

        timer = new ElapsedTime();

        logFile = new DataLogging("TestDualLiftGoToPosition", telemetry);

        //logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;
        lift.setDataLog(logFile);
        lift.enableDataLogging();
        lift.enableCollectData("dualLiftTimeEncoderValues");
        lift.setRetractionPower(-speed);
        lift.setExtensionPower(+speed);
        //lift.setTelemetry(telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        lift.reset();

        timer.reset();

        while (opModeIsActive()) {
            lift.update();

            switch (step) {
                case ZERO:
                    if (lift.isResetComplete()) {
                        timer.reset();
                        step = Steps.ONE;
                    }
                    break;
                case ONE:
                    if (timer.milliseconds() > 2000) {
                        lift.goToPosition(8, speed);
                        step = Steps.TWO;
                    }
                    break;
                case TWO:
                    // ToDo something is broken here
                    if (lift.isPositionReached()) {
                        timer.reset();
                        step = Steps.THREE;
                    }
                    break;
                case THREE:
                    if (timer.milliseconds() > 2000) {
                        lift.goToPosition(2, speed);
                        step = Steps.FOUR;
                    }
                    break;
                case FOUR:
                    if (lift.isPositionReached()) {
                        timer.reset();
                        step = Steps.FIVE;
                    }
                    break;
                case FIVE:
                    if (timer.milliseconds() > 2000) {
                        lift.goToPosition(20, speed);
                        step = Steps.SIX;
                    }
                    break;
                case SIX:
                    if (lift.isPositionReached()) {
                        timer.reset();
                        step = Steps.SEVEN;
                    }
                    break;
                case SEVEN:
                    if (timer.milliseconds() > 2000) {
                        lift.goToPosition(2, speed);
                        step = Steps.EIGHT;
                    }
                    break;
                case EIGHT:
                    if (lift.isPositionReached()) {
                        timer.reset();
                        step = Steps.NINE;
                    }
                    break;
                case NINE:
                    if (timer.milliseconds() > 2000) {
                        lift.goToPosition(30, speed);
                        step = Steps.TEN;
                    }
                    break;
                case TEN:
                    if (lift.isPositionReached()) {
                        timer.reset();
                        step = Steps.ELEVEN;
                    }
                    break;
                case ELEVEN:
                    if (timer.milliseconds() > 2000) {
                        lift.goToPosition(2, speed);
                        step = Steps.TWELVE;
                    }
                    break;
                case TWELVE:
                    if (lift.isPositionReached()) {
                        timer.reset();
                        step = Steps.THIRTEEN;
                    }
                    break;
                case THIRTEEN:
                    if (timer.milliseconds() > 2000) {
                        lift.goToPosition(40, speed);
                        step = Steps.FOURTEEN;
                    }
                    break;
                case FOURTEEN:
                    if (lift.isPositionReached()) {
                        timer.reset();
                        step = Steps.FIFTEEN;
                    }
                    break;
                case FIFTEEN:
                    if (timer.milliseconds() > 2000) {
                        lift.goToPosition(2, speed);
                        step = Steps.SIXTEEN;
                    }
                    break;
                case SIXTEEN:
                    if (lift.isPositionReached()) {
                        timer.reset();
                        step = Steps.SEVENTEEN;
                    }
                    break;
                case SEVENTEEN:
                    if (timer.milliseconds() > 2000) {
                        lift.reset();
                        step = Steps.EIGHTEEN;
                    }
                    break;
                case EIGHTEEN:
                    if (lift.isResetComplete()) {
                        step = Steps.NINETEEN;
                    }
                    break;
                case NINETEEN:
                    break;
            }

            // telemetry.addData("", lift.stateToString());
            // telemetry.addData("", lift.encoderValuesToString());
            //telemetry.addData("", lift.resetStateToString());
            telemetry.addData("STEP", step);
            telemetry.update();
            idle();
        }

        telemetry.addData("", "DONE!");
        telemetry.update();

        lift.shutdown();
    }
}

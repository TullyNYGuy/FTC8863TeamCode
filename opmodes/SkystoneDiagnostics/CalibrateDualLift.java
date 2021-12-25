package org.firstinspires.ftc.teamcode.opmodes.SkystoneDiagnostics;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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
@TeleOp(name = "Calibrate Dual Lift", group = "Calibrate")
@Disabled
public class CalibrateDualLift extends LinearOpMode {

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
        THIRTEEN
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

    public ElapsedTime timer;

    public double startTime = 0;

    public double endUpTimeRight = 0;
    public double endDownTimeRight = 0;

    public double endUpTimeLeft = 0;
    public double endDownTimeLeft = 0;

    public String buffer = "";

    public double speed = 0.1;

    public boolean startCalibrate = false;

    @Override
    public void runOpMode() {

        // Put your initializations here
        lift = new DualLift(hardwareMap,
                SkystoneRobot.HardwareName.LIFT_RIGHT_NAME.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_MOTOR.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_RETRACTION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_NAME.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_MOTOR.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_RETRACTION_SWITCH.hwName,
                telemetry);

        timer = new ElapsedTime();

        logFile = new DataLogging("CalibrateDualLift", telemetry);

        //logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;
        lift.setDataLog(logFile);
        lift.enableDataLogging();
        lift.enableCollectData("dualLiftTimeEncoderValues");
        lift.setResetPower(-0.1);
        lift.setRetractionPower(-speed);
        lift.setExtensionPower(+speed);
        lift.setExtensionPositionInMechanismUnits(9.5 * 5); //inches * 5 stages

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        lift.reset();

        timer.reset();

        while (opModeIsActive() && !startCalibrate) {
            lift.update();

            switch (step) {
                case ZERO:
                    if (lift.isResetComplete()) {
                        timer.reset();
                        step = Steps.ONE;
                    }
                    break;
                case ONE:
                    if (timer.milliseconds() > 1000) {
                        lift.goToPosition(10, speed);
                        step = Steps.TWO;
                    }
                    break;
                case TWO:
                    if (lift.isPositionReached()) {
                        timer.reset();
                        step = Steps.THREE;
                    }
                    break;
                case THREE:
                    telemetry.addData(">", "You have 10 sec to measure the height of the lift");
                    if (timer.milliseconds() > 10000) {
                        startCalibrate = true;
                        step = Steps.FOUR;
                    }
                    break;
                case FOUR:
                    break;
                case FIVE:
                    break;
                case SIX:
                    break;
                case SEVEN:
                    break;
                case EIGHT:
                    break;
                case NINE:
                    break;
                case TEN:
                    break;
                case ELEVEN:
                    break;
                case TWELVE:
                    break;
                case THIRTEEN:
                    break;
            }

            telemetry.addData("", lift.stateToString());
            telemetry.addData("", lift.encoderValuesToString());
            telemetry.addData("", lift.resetStateToString());
            telemetry.update();
            idle();
        }

        lift.calibrate(360, .1, this);

        while (opModeIsActive()) {
            // hang out while the user measures the lift height
            idle();
        }

        telemetry.addData("", "DONE!");
        telemetry.update();

        lift.shutdown();
    }
}

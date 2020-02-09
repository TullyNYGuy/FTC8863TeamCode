package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;
import org.firstinspires.ftc.teamcode.opmodes.SkystoneDiagnostics.CalibrateDualLift;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Calibrate Lift Right", group = "Test")
//@Disabled
public class CalibrateLiftRight extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanism liftRight;
    public DataLogging logFile;
    public double spoolDiameter = 1.25;
    public double movementPerRevolution = spoolDiameter * Math.PI * 5;
    public ElapsedTime timer;
    public double startTime = 0;
    public double endUpTime = 0;
    public double endDownTime = 0;
    public int encoderValueMax = 0;
    public int encoderValueMin = 0;

    public boolean startCalibrate = false;

    public CalibrateDualLift.Steps step = CalibrateDualLift.Steps.ZERO;

    public double speed = 0.1;

    @Override
    public void runOpMode() {


        // Put your initializations here
        liftRight = new ExtensionRetractionMechanism(hardwareMap, telemetry, "extensionRetractionRight",
                SkystoneRobot.HardwareName.LIFT_RIGHT_EXTENSION_SWITCH.hwName, SkystoneRobot.HardwareName.LIFT_RIGHT_RETRACTION_SWITCH.hwName, SkystoneRobot.HardwareName.LIFT_RIGHT_MOTOR.hwName,
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);
        liftRight.reverseMotor();
        logFile = new DataLogging("ExtensionRetractionTest", telemetry);
        timer = new ElapsedTime();
        liftRight.setDataLog(logFile);
        liftRight.enableDataLogging();
        liftRight.setResetPower(-0.1);
        liftRight.setRetractionPower(-.1);
        liftRight.setExtensionPower(+.1);

        liftRight.setExtensionPositionInEncoderCounts(2700.0);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        liftRight.reset();

        timer.reset();

        while (opModeIsActive() && !startCalibrate) {
            liftRight.update();

            switch (step) {
                case ZERO:
                    if (liftRight.isResetComplete()) {
                        timer.reset();
                        step = CalibrateDualLift.Steps.ONE;
                    }
                    break;
                case ONE:
                    if (timer.milliseconds() > 1000) {
                        liftRight.goToPosition(10, speed);
                        step = CalibrateDualLift.Steps.TWO;
                    }
                    break;
                case TWO:
                    if (liftRight.isPositionReached()) {
                        timer.reset();
                        step = CalibrateDualLift.Steps.THREE;
                    }
                    break;
                case THREE:
                    telemetry.addData(">", "You have 10 sec to measure the height of the lift");
                    if (timer.milliseconds() > 10000) {
                        startCalibrate = true;
                        step = CalibrateDualLift.Steps.FOUR;
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

            telemetry.addData("state = ", liftRight.getExtensionRetractionState().toString());
            telemetry.addData("", liftRight.getCurrentEncoderValue());
            telemetry.update();
            idle();
        }

        liftRight.calibrate(360 * 1, 0.1, this);

        while (opModeIsActive()) {
            // hang out while the user measures the lift height
            idle();
        }

        telemetry.addData("", "DONE!");
        telemetry.update();

        liftRight.shutdown();
    }
}

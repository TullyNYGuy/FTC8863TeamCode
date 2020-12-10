package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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
@TeleOp(name = "Calibrate Lift Left", group = "Test")
@Disabled
public class CalibrateLiftLeft extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanism liftLeft;
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
        liftLeft = new ExtensionRetractionMechanism(hardwareMap, telemetry, "extensionRetractionLeft",
                SkystoneRobot.HardwareName.LIFT_LEFT_EXTENSION_SWITCH.hwName, SkystoneRobot.HardwareName.LIFT_LEFT_RETRACTION_SWITCH.hwName, SkystoneRobot.HardwareName.LIFT_LEFT_MOTOR.hwName,
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);
        //liftLeft.reverseMotor();
        logFile = new DataLogging("ExtensionRetractionTest", telemetry);
        timer = new ElapsedTime();
        liftLeft.setDataLog(logFile);
        liftLeft.enableDataLogging();
        liftLeft.setResetPower(-0.1);
        liftLeft.setRetractionPower(-.1);
        liftLeft.setExtensionPower(+.1);

        liftLeft.setExtensionPositionInEncoderCounts(2700.0);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        liftLeft.reset();

        timer.reset();

        while (opModeIsActive() && !startCalibrate) {
            liftLeft.update();

            switch (step) {
                case ZERO:
                    if (liftLeft.isResetComplete()) {
                        timer.reset();
                        step = CalibrateDualLift.Steps.ONE;
                    }
                    break;
                case ONE:
                    if (timer.milliseconds() > 1000) {
                        liftLeft.goToPosition(10, speed);
                        step = CalibrateDualLift.Steps.TWO;
                    }
                    break;
                case TWO:
                    if (liftLeft.isPositionReached()) {
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

            telemetry.addData("state = ", liftLeft.getExtensionRetractionState().toString());
            telemetry.addData("", liftLeft.getCurrentEncoderValue());
            telemetry.update();
            idle();
        }

        liftLeft.calibrate(360 * 1, 0.1, this);

        while (opModeIsActive()) {
            // hang out while the user measures the lift height
            idle();
        }

        telemetry.addData("", "DONE!");
        telemetry.update();

        liftLeft.shutdown();
    }
}

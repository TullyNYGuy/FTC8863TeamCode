package org.firstinspires.ftc.teamcode.opmodes.SkystoneDiagnostics;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArm;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArmConstants;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Calibrate Extension Arm", group = "Calibrate")
//@Disabled
public class CalibrateExtensionArm extends LinearOpMode {

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
    public ExtensionArm extensionArm;

    public boolean startCalibrate = false;
    public double speed = 0.1;
    public ElapsedTime timer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        extensionArm = new ExtensionArm(hardwareMap, telemetry,
                ExtensionArmConstants.mechanismName,
                SkystoneRobot.HardwareName.EXT_ARM_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.EXT_ARM_RETRACTION_SWITCH.hwName,
                SkystoneRobot.HardwareName.EXT_ARM_MOTOR_NAME_FOR_ENCODER_PORT.hwName,
                ExtensionArmConstants.motorType,
                ExtensionArmConstants.movementPerRevolution);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        extensionArm.reset();

        timer.reset();

        while (opModeIsActive() && !startCalibrate) {
            extensionArm.update();

            switch (step) {
                case ZERO:
                    if (extensionArm.isResetComplete()) {
                        timer.reset();
                        step = Steps.ONE;
                    }
                    break;
                case ONE:
                    if (timer.milliseconds() > 1000) {
                        extensionArm.goToPosition(5, speed);
                        step = Steps.TWO;
                    }
                    break;
                case TWO:
                    if (extensionArm.isPositionReached()) {
                        timer.reset();
                        step = Steps.THREE;
                    }
                    break;
                case THREE:
                    telemetry.addData(">", "You have 10 sec to measure the length of the extensionArm");
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

            telemetry.addData("", extensionArm.getExtensionRetractionState().toString());
            telemetry.addData("", extensionArm.getCurrentEncoderValue());
            telemetry.update();
            idle();
        }

        extensionArm.calibrate(360, .1, this);

        while (opModeIsActive()) {
            // hang out while the user measures the extensionArm height
            idle();
        }

        extensionArm.calibrate(360, .1, this);

        while (opModeIsActive()) {
            // sit here until the user has read the values and then they kill the opmode
            idle();
        }
    }
}

package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Lift Left & Right", group = "Test")
//@Disabled
public class TestExtensionArmFullExtensionRetraction extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionArm extensionArm;

    public ExtensionRetractionMechanism.ExtensionRetractionStates extensionRetractionStateLeft;

    public int encoderValue = 0;
    public int encoderValueMax = 0;
    public int encoderValueMin = 0;

    public DataLogging logFile;
    public CSVDataFile timeEncoderValueFile;
    public double spoolDiameter = 2.75;
    public ElapsedTime timer;
    public double startTime = 0;
    public double endOutTime = 0;

    public String buffer = "";

    public double speed = 0.7;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionArm = new ExtensionArm(hardwareMap, telemetry, "extensionRetractionLeft",
                "extensionLimitSwitchLeft", "retractionLimitSwitchLeft", "extensionRetractionMotorLeft",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);
        //extensionArm.reverseMotor();

        timer = new ElapsedTime();

        logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);
        timeEncoderValueFile = new CSVDataFile("LiftTimeEncoderValues", telemetry);

        extensionArm.setDataLog(logFile);
        extensionArm.enableDataLogging();
        extensionArm.enableCollectData();
        extensionArm.setResetPower(-0.1);
        extensionArm.setRetractionPower(-speed);
        extensionArm.setExtensionPower(+speed);

        extensionArm.setExtensionPositionInEncoderCounts(2700.0);

        logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        extensionArm.testReset(this);
        sleep(3000);

        extensionArm.goToFullExtend();
        timer.reset();

        while (opModeIsActive() && !(extensionArm.isExtensionComplete())) {

            extensionRetractionStateLeft = extensionArm.update();

            encoderValue = extensionArm.getCurrentEncoderValue();

            if (encoderValue > encoderValueMax) {
                encoderValueMax = encoderValue;
            }

            telemetry.addData("Left state = ", extensionRetractionStateLeft.toString());
            telemetry.addData("Left encoder = ", encoderValue);
            telemetry.update();
            idle();
        }

        endOutTime = timer.seconds();

        sleep (3000);

        extensionArm.goToFullRetract();
        timer.reset();

        while (opModeIsActive() && !(extensionArm.isRetractionComplete())) {

            extensionRetractionStateLeft = extensionArm.update();

            encoderValue = extensionArm.getCurrentEncoderValue();

            if (encoderValue < encoderValueMin) {
                encoderValueMin = encoderValue;
            }

            telemetry.addData("Left state = ", extensionRetractionStateLeft.toString());
            telemetry.addData("Left encoder = ", encoderValue);
            telemetry.update();
            idle();
        }

        endOutTime = timer.seconds();

        extensionArm.writeTimerEncoderDataToCSVFile(timeEncoderValueFile);

        buffer = String.format("%.2f", endOutTime);
        telemetry.addData("time out = ", buffer);
        buffer = String.format("%.2f", endOutTime);
        telemetry.addData("time in = ", buffer);
        buffer = String.format("%d", encoderValueMax);
        telemetry.addData("max encoder value = ", buffer);
        buffer = String.format("%d", encoderValueMin);
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

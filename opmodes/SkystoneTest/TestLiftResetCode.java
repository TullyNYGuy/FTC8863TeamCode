package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Lift Left & Right Reset", group = "Test")
@Disabled
public class TestLiftResetCode extends LinearOpMode {

    // Put your variable declarations here

    public Lift liftRight;
    public Lift liftLeft;

    public Lift.ExtensionRetractionStates extensionRetractionStateRight;
    public Lift.ExtensionRetractionStates extensionRetractionStateLeft;

    public int encoderValueRight = 0;
    public int encoderValueMaxRight = 0;
    public int encoderValueMinRight = 0;

    public int encoderValueLeft = 0;
    public int encoderValueMaxLeft = 0;
    public int encoderValueMinLeft = 0;

    public DataLogging logFileRight;
    public DataLogging logFileLeft;

    public CSVDataFile timeEncoderValueFile;
    public double spoolDiameter = 1.25; //inches
    // spool diameter * pi * 5 stages
    public double movementPerRevolution = spoolDiameter * Math.PI * 5;

    public ElapsedTime timerRight;
    public ElapsedTime timerLeft;

    public double startTime = 0;

    public double endUpTimeRight = 0;
    public double endDownTimeRight = 0;

    public double endUpTimeLeft = 0;
    public double endDownTimeLeft = 0;

    public String buffer = "";

    public double speed = 1.0;

    @Override
    public void runOpMode() {

        // Put your initializations here
        liftRight = new Lift(hardwareMap, telemetry, "liftRight",
                "LiftExtensionLimitSwitchRight", "LiftRetractionLimitSwitchRight", "LiftZeroLimitSwitchRight", "LiftMotorRight",
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);
        liftRight.reverseMotor();

        liftLeft = new Lift(hardwareMap, telemetry, "liftLeft",
                "LiftExtensionLimitSwitchLeft", "LiftRetractionLimitSwitchLeft", "LiftZeroLimitSwitchLeft", "LiftMotorLeft",
                DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);

        timerRight = new ElapsedTime();
        timerLeft = new ElapsedTime();

        logFileRight = new DataLogging("ResetTestRight", telemetry);
        logFileLeft = new DataLogging("ResetTestLeft", telemetry);

        timeEncoderValueFile = new CSVDataFile("LiftTimeEncoderValues");

        //logFile = new DataLogging("ExtensionRetractionTestBoth", telemetry);;
        liftRight.setDataLog(logFileRight);
        liftRight.enableDataLogging();
        liftRight.enableCollectData();
        liftRight.setResetPower(-0.1);
        liftRight.setRetractionPower(-speed);
        liftRight.setExtensionPower(+speed);
        liftRight.setExtensionPositionInMechanismUnits(9.5 * 5); //inches * 5 stages

        liftLeft.setDataLog(logFileLeft);
        liftLeft.enableDataLogging();
        liftLeft.enableCollectData();
        liftLeft.setResetPower(-0.1);
        liftLeft.setRetractionPower(-speed);
        liftLeft.setExtensionPower(+speed);
        liftLeft.setExtensionPositionInMechanismUnits(9.5 * 5); //inches * 5 stages

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        liftRight.reset();
        liftLeft.reset();

        timerRight.reset();
        timerLeft.reset();

        while (opModeIsActive() && (!liftRight.isResetComplete() || !liftLeft.isResetComplete())) {
            liftRight.update();
            extensionRetractionStateRight = liftRight.getExtensionRetractionState();
            liftLeft.update();
            extensionRetractionStateLeft = liftLeft.getExtensionRetractionState();

            encoderValueRight = liftRight.getCurrentEncoderValue();
            encoderValueLeft = liftLeft.getCurrentEncoderValue();

            telemetry.addData("State   (L, R) = ", extensionRetractionStateLeft.toString() + " " + extensionRetractionStateRight.toString());
            telemetry.addData("Encoder (L, R) = ", encoderValueLeft + " " + encoderValueRight);
            telemetry.update();
            idle();
        }

        // have to update the state machine in order to generate the last state update
        liftRight.update();
        liftLeft.update();

        buffer = String.format(String.format("%.2f", endUpTimeRight));
        telemetry.addData("DONE! time up = ", buffer);
        telemetry.update();

        liftRight.writeTimerEncoderDataToCSVFile(timeEncoderValueFile);
        liftLeft.writeTimerEncoderDataToCSVFile(timeEncoderValueFile);

        // wait for user to kill the app
        while (opModeIsActive()) {
            idle();
        }

        liftRight.shutdown();
        liftLeft.shutdown();
    }
}

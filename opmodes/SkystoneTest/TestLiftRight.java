package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Lift Right", group = "Test")
//@Disabled
public class TestLiftRight extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanism extensionRetractionMechanismRight;
    public DataLogging logFile;
    public double spoolDiameter = 1.25 *25.4;
    public ElapsedTime timer;
    public double startTime = 0;
    public double endUpTime = 0;
    public double endDownTime = 0;
    public int encoderValueMax = 0;
    public int encoderValueMin = 0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionRetractionMechanismRight = new ExtensionRetractionMechanism(hardwareMap,telemetry,"extensionRetractionRight",
                "extensionLimitSwitchRight", "retractionLimitSwitchRight", "extensionRetractionMotorRight",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);
        extensionRetractionMechanismRight.reverseMotor();
        logFile = new DataLogging("ExtensionRetractionTest", telemetry);
        timer = new ElapsedTime();
        extensionRetractionMechanismRight.setDataLog(logFile);
        extensionRetractionMechanismRight.enableDataLogging();
        extensionRetractionMechanismRight.setResetPower(-0.1);
        extensionRetractionMechanismRight.setRetractionPower(-.7);
        extensionRetractionMechanismRight.setExtensionPower(+.7);

        extensionRetractionMechanismRight.setExtensionPositionInEncoderCounts(2700.0);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        extensionRetractionMechanismRight.testReset(this);
        sleep(3000);
        timer.reset();
        encoderValueMax = extensionRetractionMechanismRight.testExtension(this);
        endUpTime = timer.seconds();
        telemetry.update();
        sleep(10000);
        timer.reset();
        encoderValueMin =  extensionRetractionMechanismRight.testRetraction(this);
        endDownTime = timer.seconds();

        telemetry.addData("time up = ", endUpTime);
        telemetry.addData("time down = ", endDownTime);
        telemetry.addData("max encoder value = ", encoderValueMax);
        telemetry.addData("min encoder value = ", encoderValueMin);
        telemetry.addData(">", "Done");
        telemetry.update();

        // wait for user to read values and kill routine
        while(opModeIsActive()) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down

        sleep(5000);
    }
}

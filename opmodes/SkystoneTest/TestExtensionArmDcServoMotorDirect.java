package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionRetractionMechanismDCServoMotor;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test extension arm dc servo motor direct", group = "Test")
//@Disabled
public class TestExtensionArmDcServoMotorDirect extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanismDCServoMotor extensionArm;
    public DataLogging logFile;
    public double spoolDiameter = 2.75; // inches
    public ElapsedTime timer;
    public double startTime = 0;
    public double endUpTime = 0;
    public double endDownTime = 0;
    public int encoderValueMax = 0;
    public int encoderValueMin = 0;

    @Override
    public void runOpMode() {

        // Glenn here - I believe that we will have to extend ExtensionRetractionMechanism and hide the variable extensionRetractionMotor, making the motor of type DcServoMotor
        // rather than of type DcMotor8863.
        // This is because the motor in ExtensionRetractionMechanism is created by new DcMotor8863. That in turn gets the motor in question from the hardware map.
        // In this case we need to create a DcServoMotor. So we will need to override ExtensionRetractionMechanism.createExtensionRetractionMechanismCommonCommands
        // and instead of creating DcMotor8863, create DcServoMotor.

        // Put your initializations here
        extensionArm = new ExtensionRetractionMechanismDCServoMotor(hardwareMap, telemetry, "extensionArm",
                "extensionLimitSwitchArm", "retractionLimitSwitchArm", "extensionArmMotor",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);
        extensionArm.reverseMotor();
        logFile = new DataLogging("ExtensionRetractionTest", telemetry);
        timer = new ElapsedTime();
        extensionArm.setDataLog(logFile);
        extensionArm.enableDataLogging();
        extensionArm.setResetPower(-0.1);
        extensionArm.setRetractionPower(-.7);
        extensionArm.setExtensionPower(+.7);

        extensionArm.setExtensionPositionInEncoderCounts(2700.0);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        extensionArm.testReset(this);
        sleep(3000);
        timer.reset();
        encoderValueMax = extensionArm.testExtension(this);
        endUpTime = timer.seconds();
        telemetry.update();
        sleep(10000);
        timer.reset();
        encoderValueMin = extensionArm.testRetraction(this);
        endDownTime = timer.seconds();

        telemetry.addData("time up = ", endUpTime);
        telemetry.addData("time down = ", endDownTime);
        telemetry.addData("max encoder value = ", encoderValueMax);
        telemetry.addData("min encoder value = ", encoderValueMin);
        telemetry.addData(">", "Done");
        telemetry.update();

        // wait for user to read values and kill routine
        while (opModeIsActive()) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down

        sleep(5000);
    }
}

package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;
import org.openftc.revextensions2.ExpansionHubEx;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Expansion hub voltage drop", group = "Test")
//@Disabled
public class TestRevVoltageMonitors extends LinearOpMode {

    // Put your variable declarations her
    ExpansionHubEx expansionHubPrimary;
    ExpansionHubEx expansionHubSecondary;

    CSVDataFile csvDataFile;

    DcMotor8863 frontLeft;
    DcMotor8863 frontRight;
    DcMotor8863 backLeft;
    DcMotor8863 backRight;

    @Override
    public void runOpMode() {


        // Put your initializations here
        expansionHubPrimary = hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 1");
        expansionHubSecondary = hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 2");

        csvDataFile = new CSVDataFile("RevVoltageCurrentMonitors", telemetry);
        csvDataFile.headerStrings("Primary Voltage", "Secondary Voltage", "Primary Current", "Secondary Current");

        ElapsedTime timer = new ElapsedTime();

        frontLeft = new DcMotor8863(SkystoneRobot.HardwareName.FRONT_LEFT_MOTOR.hwName, hardwareMap);
        frontRight = new DcMotor8863(SkystoneRobot.HardwareName.FRONT_RIGHT_MOTOR.hwName, hardwareMap);
        backLeft = new DcMotor8863(SkystoneRobot.HardwareName.BACK_LEFT_MOTOR.hwName, hardwareMap);
        backRight = new DcMotor8863(SkystoneRobot.HardwareName.BACK_RIGHT_MOTOR.hwName, hardwareMap);

        frontLeft.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        frontLeft.runAtConstantPower(0);
        frontRight.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        frontRight.runAtConstantPower(0);
        backLeft.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        backLeft.runAtConstantPower(0);
        backRight.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        backRight.runAtConstantPower(0);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();

        while (opModeIsActive()) {
            csvDataFile.writeData(
                    expansionHubPrimary.read12vMonitor(ExpansionHubEx.VoltageUnits.VOLTS),
                    expansionHubSecondary.read12vMonitor(ExpansionHubEx.VoltageUnits.VOLTS),
                    expansionHubPrimary.getTotalModuleCurrentDraw((ExpansionHubEx.CurrentDrawUnits.AMPS)),
                    expansionHubSecondary.getTotalModuleCurrentDraw((ExpansionHubEx.CurrentDrawUnits.AMPS))
            );

            if (timer.milliseconds() > 2000) {
                frontLeft.setPower(1);
                frontRight.setPower(1);
                backLeft.setPower(1);
                backRight.setPower(1);
                timer.reset();
            }

            if (timer.milliseconds() > 3000) {
                frontLeft.setPower(0);
                frontRight.setPower(0);
                backLeft.setPower(0);
                backRight.setPower(0);
            }

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        csvDataFile.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}

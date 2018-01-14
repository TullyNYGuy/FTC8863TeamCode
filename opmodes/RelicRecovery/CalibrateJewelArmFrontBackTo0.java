package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.JewelArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Calibrate Jewel Arm Front Back to 0", group = "Test")
//@Disabled
public class CalibrateJewelArmFrontBackTo0 extends LinearOpMode {

    JewelArm jewelArm;
    public AdafruitColorSensor8863.ColorFromSensor ballColor;
    DataLogging dataLog;
    AllianceColor.TeamColor teamColor = AllianceColor.TeamColor.RED;

    double elbowCommand;
    double upDownCommand;
    double frontBackCommand;

    public void runOpMode() {

        // Put your initializations here
        dataLog = new DataLogging("jewelArmTest", telemetry);
        jewelArm = new JewelArm(JewelArm.RobotSide.LEFT, hardwareMap, telemetry, teamColor, dataLog );
        jewelArm.init();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        // Put your calls here - they will not run in a loop
        elbowCommand = jewelArm.getElbowServoCommandFromAngle(142);
        upDownCommand = jewelArm.getUpDownServoCommandFromAngle(80);
        frontBackCommand = jewelArm.getFrontBackServoCommandFromAngle(0);
        jewelArm.elbowServo.setPosition(elbowCommand);
        jewelArm.upDownServo.setPosition(upDownCommand);
        jewelArm.frontBackServo.setPosition(frontBackCommand);

        while (opModeIsActive()) {

            telemetry.addData(">", "Jewel Arm should be between balls");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        //telemetry.addData("Ball color = ", printBallColor.toString());
        //telemetry.addData(">", "Done");
       // telemetry.update();


    }
}

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
@TeleOp(name = "Test Jewel Arm Knock off ball", group = "Test")
//@Disabled
public class TestJewelArmKnockOffBall extends LinearOpMode {

    JewelArm leftJewelArm;
    public AdafruitColorSensor8863.ColorFromSensor ballColor;
    DataLogging dataLog;
    AllianceColor.TeamColor teamColor = AllianceColor.TeamColor.RED;

    public void runOpMode() {

        // Put your initializations here
        dataLog = new DataLogging("jewelArmTest", telemetry);
        leftJewelArm = new JewelArm(JewelArm.RobotSide.LEFT, hardwareMap, telemetry, teamColor, dataLog );
        leftJewelArm.init();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            leftJewelArm.updateKnockJewelOff();


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

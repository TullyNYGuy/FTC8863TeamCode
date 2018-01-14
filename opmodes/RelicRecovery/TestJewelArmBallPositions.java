package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.JewelArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Jewel Arm Ball Positions", group = "Test")
@Disabled
public class TestJewelArmBallPositions extends LinearOpMode {

    JewelArm jewelArm;
    public AdafruitColorSensor8863.ColorFromSensor ballColor;
    DataLogging dataLog;
    AllianceColor.TeamColor teamColor = AllianceColor.TeamColor.RED;

    public void runOpMode() {

        // Put your initializations here
        //dataLog = new DataLogging("jewelArmTest", telemetry);
        jewelArm = new JewelArm(JewelArm.RobotSide.LEFT, hardwareMap, telemetry, teamColor, dataLog);
        jewelArm.init();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        while (opModeIsActive() && !jewelArm.updateGoAboveBall()) {

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        //back ball center position = .54
        //back ball right position = .56
        //back ball left position = .52
        //front ball right position = .44
        //front ball center position = .42
        //front ball left position = .40

        telemetry.addData("about to move to next location", "!");
        telemetry.update();
        sleep(2000);
        jewelArm.frontBackServo.setPosition(.40);

        while(opModeIsActive()) {
            telemetry.addData("ball color = ", jewelArm.getBallColor().toString());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        //telemetry.addData("Ball color = ", printBallColor.toString());
        //telemetry.addData(">", "Done");
        // telemetry.update();


    }
}

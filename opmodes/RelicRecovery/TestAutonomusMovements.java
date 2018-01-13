package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.ReadPictograph;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@Autonomous(name = "1Test Autonomous Movements-real", group = "Run")
//@Disabled
public class TestAutonomusMovements extends AutonomousMethods {

    // Put your variable declarations here
    StartPosition startPosition = StartPosition.AWAY_FROM_MAT;
    AllianceColor.TeamColor teamColor = AllianceColor.TeamColor.RED;
    ExeJewel exeJewel = ExeJewel.JEWEL;

    public void passPositionsAndColorAndJewel(StartPosition startPosition, AllianceColor.TeamColor teamColor, ExeJewel exeJewel) {
        super.setPositionsAndColorAndJewel(startPosition, teamColor, exeJewel);
    }

    @Override
    public void runOpMode() {
        // Put your initializations here
        createRobot();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        blueMatColumn3Movements();


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData("actual turn angle was ", "%3.2f", actualTurnAngle);
        telemetry.addData(">", "Done");
        telemetry.addData("final Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
        robot.shutdown();
        sleep(3000);
    }
}
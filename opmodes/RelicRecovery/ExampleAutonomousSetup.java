package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.ReadPictograph;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@Autonomous(name = "Example Autonomous Setup", group = "Run")
//@Disabled
public class ExampleAutonomousSetup extends AutonomousMethods {

    // Put your variable declarations here
    StartPosition startPosition = StartPosition.AWAY_FROM_MAT;
    AllianceColor.TeamColor teamColor = AllianceColor.TeamColor.RED;

    public void passPositionsAndColor(StartPosition startPosition, AllianceColor.TeamColor teamColor) {
        super.setPositionsAndColor(startPosition, teamColor);
    }

    @Override
    public void runOpMode() {
        passPositionsAndColor(startPosition, teamColor);
        super.runOpMode();
    }
}

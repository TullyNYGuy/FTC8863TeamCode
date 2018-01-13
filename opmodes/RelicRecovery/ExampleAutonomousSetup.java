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
    ExeJewel exeJewel = ExeJewel.JEWEL;

    public void passPositionsAndColorAndJewel(StartPosition startPosition, AllianceColor.TeamColor teamColor, ExeJewel exeJewel) {
        super.setPositionsAndColorAndJewel(startPosition, teamColor, exeJewel);
    }

    @Override
    public void runOpMode() {
        passPositionsAndColorAndJewel(startPosition, teamColor, exeJewel);
        super.runOpMode();
    }
}

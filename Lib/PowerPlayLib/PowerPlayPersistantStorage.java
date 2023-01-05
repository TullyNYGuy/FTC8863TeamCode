package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;

public class PowerPlayPersistantStorage {

    // public static DATA FIELDS that persist between opmodes

    private static Pose2d robotPose;

    public static Pose2d getRobotPose() {
        return robotPose;
    }

    public static void setRobotPose(Pose2d robotPose) {
        PowerPlayPersistantStorage.robotPose = robotPose;
    }

    private static AllianceColor allianceColor;

    public static AllianceColor getAllianceColor() {
        return allianceColor;
    }

    public static void setAllianceColor(AllianceColor allianceColor) {
        PowerPlayPersistantStorage.allianceColor = allianceColor;
    }

    private static TeamLocation teamLocation;

    public static TeamLocation getTeamLocation() {
        return teamLocation;
    }

    public static void setTeamLocation(TeamLocation teamLocation) {
        PowerPlayPersistantStorage.teamLocation = teamLocation;
    }

    private static MatchPhase matchPhase;

    public static MatchPhase getMatchPhase() {
        return matchPhase;
    }

    public static void setMatchPhase(MatchPhase matchPhase) {
        PowerPlayPersistantStorage.matchPhase = matchPhase;
    }
}

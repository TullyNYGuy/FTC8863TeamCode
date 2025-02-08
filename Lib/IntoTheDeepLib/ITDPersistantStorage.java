package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;//package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Vector2D;

public class ITDPersistantStorage {
    // public static DATA FIELDS that persist between opmodes

    private static Pose2d robotPose;

    public static Pose2d getRobotPose() {
        return robotPose;
    }

    public static void setRobotPose(Pose2d robotPose) {
        ITDPersistantStorage.robotPose = robotPose;
    }

    private static Color allianceColor;

    public static Color getAllianceColor() {
        if (allianceColor == null) {
            // if the drivers forgot to set the alliance color, then return Red. It is better than crashing
            return Color.RED;
        } else {
            return allianceColor;
        }
    }

    public static void setAllianceColor(Color allianceColor) {
        ITDPersistantStorage.allianceColor = allianceColor;
    }

    private static TeamLocation teamLocation;

    public static TeamLocation getTeamLocation() {
        if (teamLocation == null) {
            // if the drivers forgot to set the team location, then return left. It is better than crashing
            return TeamLocation.LEFT;
        } else {
            return teamLocation;
        }
    }

    public static void setTeamLocation(TeamLocation teamLocation) {
        ITDPersistantStorage.teamLocation = teamLocation;
    }

    private static AllianceColorTeamLocation.ColorLocation colorLocation;

    public static AllianceColorTeamLocation.ColorLocation getColorLocation() {
        if (colorLocation == null) {
            // if the drivers forgot to set the team location and alliance color, then return red left. It is better than crashing
            return AllianceColorTeamLocation.ColorLocation.RED_LEFT;
        } else {
            return colorLocation;
        }
    }

    public static void setColorLocation(AllianceColorTeamLocation.ColorLocation colorLocation) {
        ITDPersistantStorage.colorLocation = colorLocation;
    }

    private static MatchPhase matchPhase;

    public static MatchPhase getMatchPhase() {
        return matchPhase;
    }
    public static void setMatchPhase(MatchPhase matchPhase) {
        ITDPersistantStorage.matchPhase = matchPhase;
    }

}

package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;

public class PowerPlayPersistantStorage {
    // public static DATA FIELDS that persist between opmodes

    private static PowerPlayField.ParkLocation parkLocation;

    public static PowerPlayField.ParkLocation getParkLocation() {
        return parkLocation;
    }

    public static void setParkLocation(PowerPlayField.ParkLocation parkLocation) {
        PowerPlayPersistantStorage.parkLocation = parkLocation;
    }

    private static Pose2d robotPose;

    public static Pose2d getRobotPose() {
        return robotPose;
    }

    public static void setRobotPose(Pose2d robotPose) {
        PowerPlayPersistantStorage.robotPose = robotPose;
    }

    private static AllianceColor allianceColor;

    public static AllianceColor getAllianceColor() {
        if (allianceColor == null) {
            // if the drivers forgot to set the alliance color, then return Red. It is better than crashing
            return AllianceColor.RED;
        } else {
            return allianceColor;
        }
    }

    public static void setAllianceColor(AllianceColor allianceColor) {
        PowerPlayPersistantStorage.allianceColor = allianceColor;
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
        PowerPlayPersistantStorage.teamLocation = teamLocation;
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
        PowerPlayPersistantStorage.colorLocation = colorLocation;
    }

    private static MatchPhase matchPhase;

    public static MatchPhase getMatchPhase() {
        return matchPhase;
    }

    public static void setMatchPhase(MatchPhase matchPhase) {
        PowerPlayPersistantStorage.matchPhase = matchPhase;
    }
}

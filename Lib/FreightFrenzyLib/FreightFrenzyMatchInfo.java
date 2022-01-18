package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

public class FreightFrenzyMatchInfo {

    public enum StartLocation {
        NEAR_BARRIERS,
        NEAR_CAROUSEL
    }

    private static StartLocation startLocation;

    public static StartLocation getStartLocation() {
        return startLocation;
    }

    public static void setStartLocation(StartLocation startLocation) {
        FreightFrenzyMatchInfo.startLocation = startLocation;
    }

    public enum MatchPhase {
        AUTONOMOUS,
        TELEOP
    }

    private static MatchPhase matchPhase;

    public static MatchPhase getMatchPhase() {
        return matchPhase;
    }

    public static void setMatchPhase(MatchPhase matchPhase) {
        FreightFrenzyMatchInfo.matchPhase = matchPhase;
    }

    public enum AllianceColor {
        RED,
        BLUE
    }

    private static AllianceColor allianceColor;

    public static AllianceColor getAllianceColor() {
        return allianceColor;
    }

    public static void setAllianceColor(AllianceColor allianceColor) {
        FreightFrenzyMatchInfo.allianceColor = allianceColor;
    }
}

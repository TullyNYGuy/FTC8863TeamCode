package org.firstinspires.ftc.teamcode.Lib.FTCLib;

public class AllianceColorTeamLocation {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum ColorLocation {
        RED_LEFT,
        RED_RIGHT,
        BLUE_LEFT,
        BLUE_RIGHT
    }

    public enum AllianceColor {
        RED,
        BLUE;
    }

    public enum TeamLocation {
        LEFT,
        RIGHT;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private static AllianceColor myAllianceColor;

    public static AllianceColor getAllianceColor() {
        if (myAllianceColor == null) {
            // if the drivers forgot to set the alliance color, then return Red. It is better than crashing
            return AllianceColor.RED;
        } else {
            return myAllianceColor;
        }
    }

    public static void setAllianceColor(AllianceColor allianceColor) {
        myAllianceColor = allianceColor;
        setColorLocation();
    }

    private static TeamLocation myTeamLocation;

    public static TeamLocation getTeamLocation() {
        if (myTeamLocation == null) {
            // if the drivers forgot to set the team location, then return left. It is better than crashing
            return TeamLocation.LEFT;
        } else {
            return myTeamLocation;
        }
    }

    public static void setTeamLocation(TeamLocation teamLocation) {
        myTeamLocation = teamLocation;
        setColorLocation();
    }

    private static ColorLocation myColorLocation;

    public static ColorLocation getColorLocation() {
        if (myColorLocation == null) {
            // if the drivers forgot to set the team location and alliance color, then return red left. It is better than crashing
            return AllianceColorTeamLocation.ColorLocation.RED_LEFT;
        } else {
            return myColorLocation;
        }
    }

    private static void setColorLocation() {
        if (myAllianceColor != null && myTeamLocation != null) {
            if (myAllianceColor == AllianceColor.RED && myTeamLocation == TeamLocation.LEFT) {
                myColorLocation = ColorLocation.RED_LEFT;
            }
            if (myAllianceColor == AllianceColor.RED && myTeamLocation == TeamLocation.RIGHT) {
                myColorLocation = ColorLocation.RED_RIGHT;
            }
            if (myAllianceColor == AllianceColor.BLUE && myTeamLocation == TeamLocation.LEFT) {
                myColorLocation = ColorLocation.BLUE_LEFT;
            }
            if (myAllianceColor == AllianceColor.BLUE && myTeamLocation == TeamLocation.RIGHT) {
                myColorLocation = ColorLocation.BLUE_RIGHT;
            }
        }
    }

//    private ColorLocation colorLocation;
//
//    public ColorLocation getColorLocation() {
//        return colorLocation;
//    }
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

//    public AllianceColorTeamLocation(AllianceColor allianceColor, TeamLocation teamLocation) {
//        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.LEFT) {
//            colorLocation = ColorLocation.RED_LEFT;
//        }
//        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.RIGHT) {
//            colorLocation = ColorLocation.RED_RIGHT;
//        }
//        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.LEFT) {
//            colorLocation = ColorLocation.BLUE_LEFT;
//        }
//        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.RIGHT) {
//            colorLocation = ColorLocation.BLUE_RIGHT;
//        }
//    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

//    public static ColorLocation getColorLocation(AllianceColor allianceColor, TeamLocation teamLocation) {
//        ColorLocation colorLocation = ColorLocation.RED_LEFT;
//        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.LEFT) {
//            colorLocation = ColorLocation.RED_LEFT;
//        }
//        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.RIGHT) {
//            colorLocation = ColorLocation.RED_RIGHT;
//        }
//        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.LEFT) {
//            colorLocation = ColorLocation.BLUE_LEFT;
//        }
//        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.RIGHT) {
//            colorLocation = ColorLocation.BLUE_RIGHT;
//        }
//        return colorLocation;
//    }

}

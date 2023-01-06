package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

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

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

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

    public static ColorLocation getColorLocation(AllianceColor allianceColor, TeamLocation teamLocation) {
        ColorLocation colorLocation = ColorLocation.RED_LEFT;
        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.LEFT) {
            colorLocation = ColorLocation.RED_LEFT;
        }
        if (allianceColor == AllianceColor.RED && teamLocation == TeamLocation.RIGHT) {
            colorLocation = ColorLocation.RED_RIGHT;
        }
        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.LEFT) {
            colorLocation = ColorLocation.BLUE_LEFT;
        }
        if (allianceColor == AllianceColor.BLUE && teamLocation == TeamLocation.RIGHT) {
            colorLocation = ColorLocation.BLUE_RIGHT;
        }
        return colorLocation;
    }

}

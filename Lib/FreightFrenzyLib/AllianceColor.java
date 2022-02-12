package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum AllianceColor {
        RED,
        BLUE;


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

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

    public static AllianceColor getAllianceColor(FreightFrenzyStartSpot startSpot) {
        AllianceColor allianceColor = AllianceColor.RED;
        switch (startSpot) {
            case RED_WALL:
            case RED_WAREHOUSE:
                allianceColor = AllianceColor.RED;
                break;
            case BLUE_WALL:
            case BLUE_WAREHOUSE:
                allianceColor = AllianceColor.BLUE;
                break;
        }
        return allianceColor;
    }
}


package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import android.view.textclassifier.TextClassifierEvent;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class AngleUtilities {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

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

    /**
     * Given an angle in either degrees or radians, return the angle in a range from 0 to 360 degrees
     *
     * @param angle - angle to convert to 0 to 360 range
     * @param units - units of the angle you provided
     * @return - angle in range of 0 to 360 degrees
     */
    public static double to0to360(double angle, AngleUnit units) {
        if (units == AngleUnit.RADIANS) {
            angle = units.toDegrees(angle);
        }
        angle = angle % 360;
        if (angle < 0) {
            angle = angle + 360;
        }
        return angle;
    }

    /**
     * Given an angle in either degrees or radians, return the angle in a range from 0 to 2 PI radians
     *
     * @param angle - angle to convert to 0 to 2 * PI range
     * @param units - units of the angle you provided
     * @return - angle in range of 0 to 2 * PI radians
     */
    public static double to0to2PI(double angle, AngleUnit units) {
        if (units == AngleUnit.DEGREES) {
            angle = units.toRadians(angle);
        }
        angle = angle % (2 * Math.PI);
        if (angle < 0) {
            angle = angle + 2 * Math.PI;
        }
        return angle;
    }


    public static double convertAngle(double angle, AngleUnit units) {
        if (units == AngleUnit.DEGREES) {
            return angle;
        } else {
            return Math.toRadians(angle);
        }
    }

}

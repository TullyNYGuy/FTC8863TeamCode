package org.firstinspires.ftc.teamcode.Lib.FTCLib;

/**
 * This enum represents the 0 reference for an angle.
 * <p>
 * An angle of 0 can be defined to be at one of 4 of the axes. The four axes are what we think of as
 * +x +y -x and -y. Most commonly, in our robots, 0 degrees is the + y axis. By comparison most of
 * us think of +x as the 0 axis.
 * <p>
 * +y
 * ^
 * |
 * |
 * -x <----------|------------> +x
 * |
 * |
 * -y
 * <p>
 * This enum translates between different reference axes.
 */

//*********************************************************************************************
//          ENUMERATED TYPES
//
// user defined types
//
//*********************************************************************************************

public enum AngleReference {
    PLUSX(0),
    PLUSY(1),
    MINUSX(2),
    MINUSY(3);

    public final byte bVal;

    AngleReference(int i) {
        bVal = (byte) i;
    }

    /**
     * Translate the given angle from plus x reference to the reference set in this enum.
     *
     * @param angle
     * @return
     */
    public double fromPlusX(double angle) {
        double translatedAngle = 0;
        switch (this) {
            case PLUSX:
                // already at plus x so don't do anything to the angle
                translatedAngle = angle;
                break;
            case PLUSY:
                // translate to plus y. Plus y is 90 relative to plus x, so to make plus y = 0,
                // subtract 90 from the angle. Any input angle from 0 to 90 will end up negative
                // after the subtraction so add 360 to those angles.
                if (angle < 90 && angle >= 0) {
                    angle = angle + 270;
                } else {
                    angle = angle - 90;
                }
                break;
            case MINUSX:
                break;
            case MINUSY:
                break;
        }
        return translatedAngle;
    }

}

//*********************************************************************************************
//          PRIVATE DATA FIELDS
//
// can be accessed only by this class, or by using the public
// getter and setter methods
//*********************************************************************************************

//*********************************************************************************************
//          GETTER and SETTER Methods
//
// allow access to private data fields for example setMotorPower,
// getPositionInTermsOfAttachment
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


package org.firstinspires.ftc.teamcode.Lib.FTCLib;

/**
 * This enum represents the range for an angle.
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

/**
 * The IMU returns angles in a range from -180 to + 180 normally. But sometimes you may want
 * angles in the range of 0 to 360  or 0 to -360 instead. This enum allows to associate an angle
 * with its range. And it translates between ranges.
 */

public enum AngleRange {
    PLUS_TO_MINUS_180(0),
    ZERO_TO_PLUS_360(1),
    ZERO_TO_MINUS_360(2);

    public final byte bVal;

    AngleRange(int i) {
        bVal = (byte) i;
    }

    /**
     * Translate that given angle, which is in the range of 0 to 360, to the range in this enum.
     * @param angle
     * @return
     */
    public double fromZeroToPlus360(double angle) {
        double translatedAngle = 0;
        // process the given angle to make sure it is in the range the user says it is
        // if the angle is > 360, put it back in the range it is supposed to be in (0 to +360)
        if (angle > 360) {
            angle = angle % 360;
        }
        // if the angle is < 0 (negative) put it in the negative (0 - 360) range and
        // then translate that to a positive angle so it ends up in the (0 - +360)
        if (angle < 0) {
            angle = angle % 360 + 360;
        }

        // now that the angle is actually in the 0 - +360 range, translate it to one of these
        // cases
        switch (this) {
            case ZERO_TO_PLUS_360:
                // the angle is already 0 to +360, just return it.
                translatedAngle = angle;
                break;
            case ZERO_TO_MINUS_360:
                // the angle is 0 to +360, subtract 360 to get it into -360 to 0 range
                translatedAngle = angle - 360;
                break;
            case PLUS_TO_MINUS_180:
                // the angle is 0 to +360,
                if (angle >= 0 && angle <= 180) {
                    translatedAngle = angle;
                } else {
                    translatedAngle = angle - 360;
                }
                break;
        }
        return translatedAngle;
    }

    public double adjustAngle(double angle) {
        double result = 0;
        if (Math.abs(angle) < Math.abs(threshold)) {
            result = angle;

        }
        if (Math.abs(angle) >= Math.abs(threshold)) {
            if (angleRange == AngleRange.ZERO_TO_PLUS_360) {
                if (angle < 0) {
                    result = 360 + angle;

                } else {
                    result = angle;
                }
            }
            if (angleRange == AngleRange.ZERO_TO_MINUS_360) {
                if (angle < 0) {
                    result = angle;

                } else {
                    result = angle - 360;
                }
            }
            if (angleRange == AngleRange.PLUS_TO_MINUS_180) {
                result = angle;
            }
        }
        return result;
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


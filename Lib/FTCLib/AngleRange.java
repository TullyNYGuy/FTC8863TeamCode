package org.firstinspires.ftc.teamcode.Lib.FTCLib;


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

// 0 to 360 range
//                0
//                ^
//                |
//                |
//  270<----------|------------> 90
//                |
//                |
//               180
//
// negative angles rotate counter clockwise. i.e.
// -90 = 270
// -180 = 180
// -270 = 90

// 0 to -360 range
//                 0
//                 ^
//                 |
//                 |
//  -90 <----------|------------> -270
//                 |
//                 |
//               -180
//
// positive angles rotate clockwise i.e.
// +90 = -270
// +180 = -180
// +270 = -90

// -180 to 180 range
//                0
//                ^
//                |
//                |
// -90 <----------|------------> 90
//                |
//                |
//            -179 +179



public enum AngleRange {
    PLUS_TO_MINUS_180(0),
    ZERO_TO_PLUS_360(1),
    ZERO_TO_MINUS_360(2);

    public final byte bVal;

    AngleRange(int i) {
        bVal = (byte) i;
    }

    /**
     * Take any angle, positive or negative, and rotate it into the range of 0 to 360 degrees. The
     * assumption is that the angle is supposed to be in the range of 0 to 360 to start with. But
     * maybe it wrapped past 360 (like 720) or 0 (like -360) to get it out of the range.
     *
     * @param angle
     * @return
     */
    public double normalizeToZeroTo360(double angle) {
        // I'm not going to trust that the angle passed to this method is actually in the range
        // of 0 to 360. So put it in the range of -360 to +360. Use the remainder operator (modulo)
        // to do that.
        // example:
        // -450 % 360 = -90
        // +720 % 360 = 0
        angle = angle % 360;
        // now get the negative angles back to where they should be
        // if the angle is < 0 (negative) translate it to a positive angle so it ends up in the
        // (0 to +360)
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * Take any angle, positive or negative, and rotate it into the range of 0 to -360 degrees. The
     * assumption is that the angle is supposed to be in the range of 0 to -360 to start with. But
     * maybe it wrapped past -360 (like -720) or 0 (like +360) to get it out of the range.
     *
     * @param angle
     * @return
     */
    public double normalizeToZeroToMinus360(double angle) {
        // I'm not going to trust that the angle passed to this method is actually in the range
        // of 0 to -360. So put it in the range of -360 to +360. Use the remainder operator (modulo)
        // to do that.
        // example:
        // -450 % 360 = -90
        // +720 % 360 = 0
        angle = angle % 360;
        // now get the negative angles back to where they should be
        // if the angle is < 0 (negative) translate it to a positive angle so it ends up in the
        // (0 to +360)
        if (angle > 0) {
            angle -= 360;
        }
        return angle;
    }

    public double normalizeToPlusToMinus180(double angle) {
        // I'm not going to trust that the angle passed to this method is actually in the range
        // of -180 to +180. So put it in the range of -360 to +360. Use the remainder operator (modulo)
        // to do that.
        // example:
        // -450 % 360 = -90
        // +720 % 360 = 0
        // ToDo essentially the task is to translate any negative or positive angle to -180 to +180 range
        // starting point? angle = angle % 180;
    }

    /**
     * Translate the given angle, which is in the range of 0 to 360, to the range in this enum.
     * @param angle
     * @return
     */
    public double fromZeroToPlus360(double angle) {
        // I'm not going to trust that the angle passed to this method is actually in the range
        // of 0 to 360. So
        angle = normalizeToZeroTo360(angle);

        // now that the angle is actually in the 0 - +360 range, translate it to one of these
        // cases
        switch (this) {
            case ZERO_TO_PLUS_360:
                // the angle is already 0 to +360, don't do anything to translated angle.
                break;
            case ZERO_TO_MINUS_360:
                // the angle is 0 to +360, subtract 360 to get it into -360 to 0 range
                angle -= 360;
                break;
            case PLUS_TO_MINUS_180:
                //ToDo need an algorithm here
                // maybe? angle = (angle + 360) % 360;
                break;
        }
        return angle;
    }

    /**
     * Translate the given angle, which is in the range of -180 to +180, to the range in this enum.
     *
     * @param angle
     * @return
     */
    public double fromPlusToMinus180(double angle) {
        // I'm not going to trust that the angle passed to this method is actually in the range
        // of -180 to + 180. So I'm going to process it to make sure.
        double translatedAngle = 0;
        // process the given angle to make sure it is in the range the user says it is
        // if the angle is > 180, put it back in the range it is supposed to be in (-180 to +180)
        // by dividing by 180 and using the remainder (modulo)
        translatedAngle = angle % 180;
        // if the angle is < 0 (negative) translate it to a positive angle so it ends up in the
        // (0 to +360)
        if (translatedAngle < 0) {
            translatedAngle += 360;
        }

        // now that the angle is actually in the 0 - +360 range, translate it to one of these
        // cases
        switch (this) {
            case ZERO_TO_PLUS_360:
                // the angle is already 0 to +360, don't do anything to translated angle.
                break;
            case ZERO_TO_MINUS_360:
                // the angle is 0 to +360, subtract 360 to get it into -360 to 0 range
                translatedAngle -= 360;
                break;
            case PLUS_TO_MINUS_180:
                // the angle is 0 to +360, for 0 to 180 the angle is the same
                if (translatedAngle > 180 && translatedAngle <= 360) {
                    // for 180 to 360, the equivalent angle is -180 to 0 so subtract 360
                    translatedAngle -= 360;
                }
                break;
        }
        return translatedAngle;
    }

    /**
     * Translate the given angle, which is in the range of 0 to -360, to the range in this enum.
     *
     * @param angle
     * @return
     */
    public double fromZeroToMinus360(double angle) {
        // I'm not going to trust that the angle passed to this method is actually in the range
        // of 0 to 360. So I'm going to process it to make sure.
        double translatedAngle = 0;
        // process the given angle to make sure it is in the range the user says it is
        // if the angle is > 360, put it back in the range it is supposed to be in (0 to +360)
        // by dividing by 360 and using the remainder (modulo)
        translatedAngle = angle % 360;
        // if the angle is > 0 (positive) translate it to a negative angle so it ends up in the
        // (0 to -360)
        if (translatedAngle > 0) {
            translatedAngle -= 360;
        }

        // now that the angle is actually in the 0 to -360 range, translate it to one of these
        // cases
        switch (this) {
            case ZERO_TO_PLUS_360:
                // to get to 0 to +360 from -360 to 0, add 360
                translatedAngle += 360;
                break;
            case ZERO_TO_MINUS_360:
                // the angle already is 0 to -360, don't do anything
                break;
            case PLUS_TO_MINUS_180:
                // the angle is 0 to -360, to get to +180 to -180 use two steps
                // for 0 to -180 the angle is the same
                // for -180 to -360, the equivalent angle is +180 to 0 so add 360
                if (translatedAngle < -180 && translatedAngle >= -360) {
                    translatedAngle += 360;
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


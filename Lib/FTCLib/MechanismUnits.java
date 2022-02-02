package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class MechanismUnits {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * The conversion factor between mechanism units and encoder counts
     */
    private double mechanismUnitPerEncoderCount = 0;

    private void setMechanismUnitPerEncoderCount(double movementForOneRevolution, double countsForOneRevolution) {
        mechanismUnitPerEncoderCount = movementForOneRevolution / countsForOneRevolution;
    }

    public double getMechanismUnitPerEncoderCount() {
        return mechanismUnitPerEncoderCount;
    }

    /**
     * If the constructor that contains DistanceUnit is called, then this variable holds the native
     * distance unit for the mechanism. The angleUnit will not be used since it is assumed that the
     * mechanism is linear in nature.
     */
    private DistanceUnit distanceUnit;

    /**
     * If the constructor that contains AngleUnit is called, then this variable holds the native
     * angle unit for the mechanism. The distanceUnit will not be used since it is assumed that the
     * mechanism is rotational in nature.
     */
    private AngleUnit angleUnit;

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

    /**
     * Use this constructor for a mechanism that moves in a linear fashion.
     * @param distanceForOneRevolution - linear movement of the mechanism per motor revolution
     * @param units - units associated with the distance
     * @param motorType - the type of motor will be used to look up the encoder counts per motor
     *                  revolution
     */
    public MechanismUnits(double distanceForOneRevolution, DistanceUnit units, MotorType motorType) {
        setMechanismUnitPerEncoderCount(distanceForOneRevolution, motorType.getCountsPerRev());
        this.distanceUnit = units;
        // for emphasis
        this.angleUnit = null;
    }

    /**
     * Use this constructor for a mechanism that rotates.
     * @param rotationForOneRevolution - note that due to gear ratios, there can be more or less than
     *                                 360 degrees (2PI) of rotation of the mechanism per motor
     *                                 revolution
     * @param units - units associated with the rotation
     * @param motorType - the type of motor will be used to look up the encoder counts per motor
     *                  revolution
     */
    public MechanismUnits(double rotationForOneRevolution, AngleUnit units, MotorType motorType) {
        setMechanismUnitPerEncoderCount(rotationForOneRevolution, motorType.getCountsPerRev());
        this.angleUnit = units;
        // for emphasis
        this.distanceUnit = null;
    }

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
     * Convert a mechanism position given in some distance unit to encoder counts. This is applicable
     * to a mechanism the moves in a linear fashion.
     * @param mechanismPosition - position of mechanism in a DistanceUnit
     * @param units - the DistanceUnit of the provided mechanism position
     * @return - encoder counts
     */
    public int fromMechanism(double mechanismPosition, DistanceUnit units) {
        if (distanceUnit != null) {
            // convert from the units the user gave us to our internal units
            mechanismPosition = distanceUnit.fromUnit(units, mechanismPosition);
        } else {
            throw new NullPointerException("Asked to convert mechanism distance to encoder value but mechanism was specified as rotational, not linear");
        }
        return (int) (mechanismPosition / mechanismUnitPerEncoderCount);
    }

    /**
     * Convert a mechanism position given in some angle unit to encoder counts. This is applicable
     * to a mechanism that rotates.
     * @param mechanismPosition
     * @param units
     * @return
     */
    public int fromMechanism(double mechanismPosition, AngleUnit units) {
        if (angleUnit != null) {
            // convert from the units the user gave us to our internal units
            mechanismPosition = angleUnit.fromUnit(units, mechanismPosition);
        } else {
            throw new NullPointerException("Asked to convert mechanism angle to encoder value but mechanism was specified as linear, not rotational");
        }
        return (int) (mechanismPosition/ mechanismUnitPerEncoderCount);
    }

    /**
     * Convert from encoder counts to a mechanism position in a distance unit. This is applicable to
     * mechanisms that move in a linear fashion.
     * @param counts - encoder counts
     * @param desiredUnits - the desired units for the position
     * @return - mechanism position in the requested distance unit.
     */
    public double fromCounts(int counts, DistanceUnit desiredUnits) {
        double distanceInOurUnits = counts * mechanismUnitPerEncoderCount;
        if (distanceUnit != null) {
            return desiredUnits.fromUnit(distanceUnit, distanceInOurUnits);
        } else {
            throw new NullPointerException("Asked to convert encoder counts to mechanism position but mechanism was specified as rotational, not linear");
        }
    }

    /**
     * Convert from encoder counts to a mechanisim rotational position in an angle unit. This is applicable
     * to mechanisms that rotate.
     * @param counts
     * @param desiredUnits
     * @return
     */
    public double fromCounts(int counts, AngleUnit desiredUnits) {
        double angleInOurUnits = counts * mechanismUnitPerEncoderCount;
        if (angleUnit != null) {
            return desiredUnits.fromUnit(angleUnit, angleInOurUnits);
        } else {
            throw new NullPointerException("Asked to convert encoder counts to mechanism rotational angle but mechanism was specified as linear, not rotational");
        }
    }

    /**
     * Convert from encoder counts to a mechanism position in a distance unit. This is applicable to
     * mechanisms that move in a linear fashion.
     * @param counts - encoder counts
     * @param desiredUnits - the desired units for the position
     * @return - mechanism position in the requested distance unit.
     */
    public double toMechanism(int counts, DistanceUnit desiredUnits) {
        return fromCounts(counts, desiredUnits);
    }

    /**
     * Convert from encoder counts to a mechanisim rotational position in an angle unit. This is applicable
     * to mechanisms that rotate.
     * @param counts
     * @param desiredUnits
     * @return
     */
    public double toMechanism(int counts, AngleUnit desiredUnits){
        return fromCounts(counts, desiredUnits);
    }

    /**
     * Convert a mechanism position given in some distance unit to encoder counts. This is applicable
     * to a mechanism the moves in a linear fashion.
     * @param mechanismPosition - position of mechanism in a DistanceUnit
     * @param units - the DistanceUnit of the provided mechanism position
     * @return - encoder counts
     */
    public int toCounts(double mechanismPosition, DistanceUnit units) {
        return fromMechanism(mechanismPosition, units);
    }

    /**
     * Convert a mechanism position given in some angle unit to encoder counts. This is applicable
     * to a mechanism that rotates.
     * @param mechanismPosition
     * @param units
     * @return
     */
    public int toCounts(double mechanismPosition, AngleUnit units) {
        return fromMechanism(mechanismPosition, units);
    }

    /**
     * Return a string with the mechanism position for a linear mechanism
     * @param mechanismPosition - position of a linear mechanism
     * @param units - units associated with the position
     * @return
     */
    public String toString(double mechanismPosition, DistanceUnit units) {
        return "mechanism position = " + mechanismPosition + " " + units.toString();
    }

    /**
     * Return a string with the mechanism angle for a mechanism that rotates.
     * @param mechanismPosition - angle of the mechanism
     * @param units - units associated with the angle
     * @return
     */
    public String toString(double mechanismPosition, AngleUnit units) {
        return "mechanism position = " + mechanismPosition + " " + units.toString();
    }

    /**
     * Return a string with the encoder counts
     * @param counts
     * @return
     */
    public String toString(int counts) {
        return "encoder counts = " + counts;
    }
}

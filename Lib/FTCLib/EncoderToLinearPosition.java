package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class EncoderToLinearPosition implements EncoderToPhysicalPosition<DistanceUnit> {

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

    private DistanceUnit unit = DistanceUnit.MM; //default is mm

    public DistanceUnit getUnit() {
        return unit;
    }

    public void setUnit(DistanceUnit unit) {
        this.unit = unit;
    }

    private double movementPerRev = 0;

    public double getMovementPerRev(DistanceUnit unit) {
        return unit.fromUnit(this.unit, movementPerRev);
    }

    public void setMovementPerRev(double movementPerRev, DistanceUnit unit) {
        this.movementPerRev = this.unit.fromUnit(unit, movementPerRev);
    }

    private int countsPerRev = 0;

    public int getCountsPerRev() {
        return countsPerRev;
    }

    public void setCountsPerRev(int countsPerRev) {
        this.countsPerRev = countsPerRev;
    }
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * This class allows conversion from a linear position or distance to encoder counts or the
     * other way around, from encoder counts to a linear position. It uses the mechanism's linear
     * movement per revolution of the motor and the motor's encoder count per revolution for the
     * calculation.
     *
     * @param movementPerRev The linear movement of the mechanism for each rev of the motor
     * @param unit           The distance units of the movement.
     * @param countsPerRev   The encoder count per motor revolution, a property of the motor.
     */
    public EncoderToLinearPosition(double movementPerRev, DistanceUnit unit, int countsPerRev) {
        setMovementPerRev(movementPerRev, unit);
        this.countsPerRev = countsPerRev;
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

    //*********************************************************************************************
    // First, given the position or movement, get the encoder count
    //*********************************************************************************************

    /**
     * Calculate the number or motor revolutions needed to move whatever is attached to the motor
     * a certain amount. It uses the MovementPerRev value for the calculation.
     *
     * @param movement The amount to move whatever is attached. It could be degrees, cm or any
     *                 other units.
     * @param unit     The units for the distance (mm, in, etc)
     * @return Number of motor revolutions to turn.
     */
    private double getRevsForMovement(double movement, DistanceUnit unit) {
        // result is in the requested units as long as movementPerRev is in the same units so
        // convert movementPerRev to the requested units
        return movement / getMovementPerRev(unit);
    }

    /**
     * Calculate the number of encoder counts needed to move whatever is attached to the motor
     * a certain amount. It uses the MovementPerRev value and number of encoder counts per revolution
     * for the calculation. The number of encoder counts per rev is dependent on the motor type.
     *
     * @param movement The amount to move whatever is attached. It could be cm or any
     *                 other distance units.
     * @param unit     The units for the distance (mm, in, etc)
     * @return Number of encoder counts to turn to create the movement.
     */
    @Override
    public int getEncoderCountForMovement(double movement, DistanceUnit unit) {
        return (int) Math.round(getCountsPerRev() * getRevsForMovement(movement, unit));
    }

    /**
     * As long as the position of the mechanism is 0 when the encoder count is 0, the above method
     * can be used to get the position. For clarity, wrap it in a more appropriate name.
     *
     * @param position
     * @param unit
     * @return
     */
    @Override
    public int getEncoderCountForPosition(double position, DistanceUnit unit) {
        return getEncoderCountForMovement(position, unit);
    }

    //*********************************************************************************************
    // Next, given the encoder count, get the position or the movement
    //*********************************************************************************************

    /**
     * Calculate the "movement" of whatever is attached to the motor based on the
     * encoder counts given. The movement can be the length a lift has extended, the
     * number of cm a wheel attached to the motor has turned etc.
     *
     * @param encoderCount The position of the motor as given by the encoder count
     * @param unit         The units for the distance (mm, in, etc)
     * @return How far the motor has moved whatever is attached to it.
     */
    @Override
    public double getMovementForEncoderCount(int encoderCount, DistanceUnit unit) {
        // note that I have to cast encoderCount to a double in order to get a double answer
        // If I did not then 1000/300 = 3 rather than 3.3333 because 1000 and 300 are integers in the
        // equation below. The compiler makes the answer int also and drops the .3333. So you get
        // the wrong answer. Casting the numerator forces the compiler to do double math and you
        // get the correct answer (3.333).
        return (double) encoderCount / countsPerRev * getMovementPerRev(unit);
    }

    /**
     * As long as the position of the mechanism is 0 when the encoder count is 0, the above method
     * can be used to get the position. For clarity, wrap it in a more appropriate name.
     *
     * @return position in units of whatever is attached to it
     */
    // note that this method is essentially the same as getPositionInTermsOfAttachment
    @Override
    public double getPositionForEncoderCount(int encoderCount, DistanceUnit unit) {
        return getMovementForEncoderCount(encoderCount, unit);
    }
}

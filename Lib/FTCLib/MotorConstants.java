package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class MotorConstants {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum MotorType {
        NXT,
        ANDYMARK_20,
        ANDYMARK_40,
        ANDYMARK_60,
        TETRIX,
        ANDYMARK_20_ORBITAL,
        ANDYMARK_3_7_ORBITAL,
        ANDYMARK_3_7_ORBITAL_OLD,
        USDIGITAL_360PPR_ENCODER,
        GOBILDA_30,
        GOBILDA_43,
        GOBILDA_60,
        GOBILDA_84,
        GOBILDA_117,
        GOBILDA_223,
        GOBILDA_312,
        GOBILDA_435,
        GOBILDA_1150,
        GOBILDA_1620,
        GOBILDA_6000
    }

    /**
     * Type of motor. Controls the encoder counts per revolution
     */
    private MotorType motorType = MotorType.ANDYMARK_40;

    public MotorType getMotorType() {
        return motorType;
    }

    /**
     * Encoder counts per shaft revolution for this type of motor
     */
    private int countsPerRev = 0;

    public int getCountsPerRev() {
        return countsPerRev;
    }

    /**
     * The no load RPM for the motor as given by the motor datasheet
     */
    private int noLoadRPM = 0;

    public int getNoLoadRPM() {
        return noLoadRPM;
    }

    /**
     * The no load max speed in encoder ticks per second
     */
    private int maxEncoderTicksPerSecond = 0;

    public int getMaxEncoderTicksPerSecond() {
        return maxEncoderTicksPerSecond;
    }

    /**
     * Minimum power for this motor
     */
    private double minMotorPower = -1;

    public double getMinMotorPower() {
        return minMotorPower;
    }

    public void setMinMotorPower(double minMotorPower) {
        if (minMotorPower > 0) {
            minMotorPower = 0;
        }
        if (minMotorPower < -1) {
            minMotorPower = -1.0;
        }
        this.minMotorPower = minMotorPower;
    }

    /**
     * Maximum power for this motor
     */
    private double maxMotorPower = 1;

    public double getMaxMotorPower() {
        return maxMotorPower;
    }

    public void setMaxMotorPower(double maxMotorPower) {
        if (maxMotorPower > 1) {
            maxMotorPower = 1.0;
        }
        if (maxMotorPower < 0) {
            maxMotorPower = 0;
        }
        this.maxMotorPower = maxMotorPower;
    }

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
    public MotorConstants(MotorType motorType) {
        this.motorType = motorType;
        setMotorConstants(motorType);
        maxEncoderTicksPerSecond = getMotorSpeedInEncoderTicksPerSec(countsPerRev, noLoadRPM);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Set the number of encoder counts per revolution of the shaft based on the type of motor.
     *
     * @param motorType Type of motor.
     * @return Number of encoder counts per revolution of the output shaft of the motor
     */
    private void setMotorConstants(MotorType motorType) {
        switch (motorType) {
            case GOBILDA_6000:
                this.countsPerRev = 28;
                noLoadRPM = 6000;
                break;
            case ANDYMARK_3_7_ORBITAL_OLD:
                this.countsPerRev = 44;
                noLoadRPM = 1784; //questionable!!!! check it before using it
                break;
            case ANDYMARK_3_7_ORBITAL:
                this.countsPerRev = 103;
                noLoadRPM = 1784;
                break;
            case GOBILDA_1620:
                this.countsPerRev = 104;
                noLoadRPM = 1620;
                break;
            case GOBILDA_1150:
                this.countsPerRev = 145;
                noLoadRPM = 1150;
                break;
            case NXT:
                this.countsPerRev = 360;
                noLoadRPM = 165;
                break;
            case GOBILDA_435:
                this.countsPerRev = 385;
                noLoadRPM = 435;
                break;
            case ANDYMARK_20_ORBITAL:
                this.countsPerRev = 537;
                noLoadRPM = 340;
                break;
            case GOBILDA_312:
                this.countsPerRev = 538;
                noLoadRPM = 213;
                break;
            case ANDYMARK_20:
                // http://www.andymark.com/NeveRest-20-12V-Gearmotor-p/am-3102.htm
                this.countsPerRev = 560;
                noLoadRPM = 315;
                break;
            case GOBILDA_223:
                this.countsPerRev = 752;
                noLoadRPM = 223;
                break;
            case ANDYMARK_40:
                // http://www.andymark.com/NeveRest-40-Gearmotor-p/am-2964a.htm
                this.countsPerRev = 1120;
                noLoadRPM = 160;
                break;
            case GOBILDA_117:
                this.countsPerRev = 1425;
                noLoadRPM = 117;
                break;
            case TETRIX:
                // http://www.cougarrobot.com/attachments/328_Tetrix_DC_Motor_V2.pdf
                this.countsPerRev = 1440;
                noLoadRPM = 150;
                break;
            case USDIGITAL_360PPR_ENCODER:
                this.countsPerRev = 1440;
                noLoadRPM = 60;
                break;
            case ANDYMARK_60:
                // http://www.andymark.com/NeveRest-60-Gearmotor-p/am-3103.htm
                this.countsPerRev = 1680;
                noLoadRPM = 105;
                break;
            case GOBILDA_84:
                this.countsPerRev = 1993;
                noLoadRPM = 84;
                break;
            case GOBILDA_60:
                this.countsPerRev = 2786;
                noLoadRPM = 60;
                break;
            case GOBILDA_43:
                this.countsPerRev = 3896;
                noLoadRPM = 43;
                break;
            case GOBILDA_30:
                this.countsPerRev = 5281;
                noLoadRPM = 30;
                break;
            default:
                this.countsPerRev = 0;
                noLoadRPM = 0;
                break;
        }
    }

    /**
     * Calculate the motor speed in encoder ticks per second given the number of ticks per revolution
     * and the speed of the motor in RPM.
     *
     * @param countsPerRev
     * @param motorRPM
     * @return motor speed in encoder ticks / sec
     */
    private int getMotorSpeedInEncoderTicksPerSec(int countsPerRev, int motorRPM) {
        return (int) Math.round((double) motorRPM * 1 / 60 * countsPerRev);
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

}

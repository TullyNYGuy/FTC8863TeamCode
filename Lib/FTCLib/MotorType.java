package org.firstinspires.ftc.teamcode.Lib.FTCLib;

//*********************************************************************************************
//          ENUMERATED TYPES
//
// user defined types
//
//*********************************************************************************************

/**
 * Defines the type of motor.
 */
public enum MotorType {
    NXT(0),
    ANDYMARK_20(1),
    ANDYMARK_40(2),
    ANDYMARK_60(2),
    TETRIX(3),
    ANDYMARK_20_ORBITAL(4),
    ANDYMARK_3_7_ORBITAL(5),
    ANDYMARK_3_7_ORBITAL_OLD(6),
    USDIGITAL_360PPR_ENCODER(7),
    GOBILDA_312(8),
    GOBILDA_6000(9);

    private byte enumValue;
    
    private int countsPerRev;

    /**
     * Set the number of encoder counts per revolution of the shaft based on the type of motor.
     */
    private void setCountsPerRev() {
        this.countsPerRev = getCountsPerRev(this);
    }

    /**
     * Get the encoder counts for one revolution of this motor or encoder
     *
     * @return
     */
    public int getCountsPerRev() {
        return this.countsPerRev;
    }
    
    public static int getCountsPerRev(MotorType motorType) {
        int countsPerRev;
        switch (motorType) {
            case NXT:
                countsPerRev = 360;
                break;
            case ANDYMARK_20:
                // http://www.andymark.com/NeveRest-20-12V-Gearmotor-p/am-3102.htm
                countsPerRev = 560;
                break;
            case ANDYMARK_40:
                // http://www.andymark.com/NeveRest-40-Gearmotor-p/am-2964a.htm
                countsPerRev = 1120;
                break;
            case ANDYMARK_60:
                // http://www.andymark.com/NeveRest-60-Gearmotor-p/am-3103.htm
                countsPerRev = 1680;
                break;
            case TETRIX:
                // http://www.cougarrobot.com/attachments/328_Tetrix_DC_Motor_V2.pdf
                countsPerRev = 1440;
                break;
            case ANDYMARK_20_ORBITAL:
                countsPerRev = 537;
                break;
            case ANDYMARK_3_7_ORBITAL:
                countsPerRev = 103;
                break;
            case ANDYMARK_3_7_ORBITAL_OLD:
                countsPerRev = 44;
                break;
            case USDIGITAL_360PPR_ENCODER:
                countsPerRev = 1440;
                break;
            case GOBILDA_312:
                countsPerRev = 538;
                break;
            case GOBILDA_6000:
                countsPerRev = 28;
                break;
            default:
                countsPerRev = 0;
                break;
        }
        return countsPerRev;
    }
    
    private int noLoadRPM;

    /**
     * Put the data in for the no load RPM of each type of motor.
     *
     */
    private void setNoLoadRPM() {
        this.noLoadRPM = getNoLoadRPM(this);
    }
    
    public int getNoLoadRPM() {
        return noLoadRPM;
    }

    public static int getNoLoadRPM(MotorType motorType) {
        int noLoadRPM;
        switch (motorType) {
            case NXT:
                // http://www.philohome.com/nxtmotor/nxtmotor.htm
                noLoadRPM = 165;
                break;
            case ANDYMARK_20:
                // http://www.andymark.com/NeveRest-20-12V-Gearmotor-p/am-3102.htm
                noLoadRPM = 315;
                break;
            case ANDYMARK_40:
                // http://www.andymark.com/NeveRest-40-Gearmotor-p/am-2964a.htm
                noLoadRPM = 160;
                break;
            case ANDYMARK_60:
                // http://www.andymark.com/NeveRest-60-Gearmotor-p/am-3103.htm
                noLoadRPM = 105;
                break;
            case TETRIX:
                // http://www.cougarrobot.com/attachments/328_Tetrix_DC_Motor_V2.pdf
                noLoadRPM = 150;
                break;
            case ANDYMARK_20_ORBITAL:
                noLoadRPM = 340;
                break;
            case ANDYMARK_3_7_ORBITAL:
                noLoadRPM = 1784;
                break;
            case USDIGITAL_360PPR_ENCODER:
                noLoadRPM = 60;
                break;
            case GOBILDA_312:
                noLoadRPM = 312;
                break;
            case GOBILDA_6000:
                // https://www.gobilda.com/5202-series-yellow-jacket-motor-1-1-ratio-24mm-length-6mm-d-shaft-6000-rpm-3-3-5v-encoder/
                noLoadRPM = 6000;
                break;
            default:
                noLoadRPM = 0;
                break;
        }
        return noLoadRPM;
    }
    
    private double maxCountsPerSecond;
    
    private void setMaxCountsPerSecond() {
        maxCountsPerSecond = getMaxCountsPerSecond(this);
    }

    public double getMaxCountsPerSecond() {
        return maxCountsPerSecond;
    }

    public static double getMaxCountsPerSecond(MotorType motorType) {
        return getCountsPerSecond(getNoLoadRPM(motorType), motorType);
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
    MotorType(int motorType) {
        this.enumValue = (byte) motorType;
        setCountsPerRev();
        setNoLoadRPM();
        setMaxCountsPerSecond();
    }

//*********************************************************************************************
//          Helper Methods
//
// methods that aid or support the major functions in the class
//*********************************************************************************************
    
    public double getCountsPerSecond (double RPM) {
        return  RPM * 1 / 60 * countsPerRev;
    }

    public static double getCountsPerSecond (double RPM, MotorType motorType) {
        return RPM * 1/60 * getCountsPerRev(motorType);
    }

//*********************************************************************************************
//          MAJOR METHODS
//
// public methods that give the class its functionality
//*********************************************************************************************
}


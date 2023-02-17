package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class RelativeEncoder {

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

    /**
     * The base encoder count is the actual motor encoder count noted at a given time. Any encoder
     * counts given by this class are adjusted by that base. In effect, the present encoder count
     * is relative to the time the base was set. For example, assume the hardware encoder count at time
     * t=1 sec was 2000 and the base was set at that time. The hardware encoder count at time t=5 sec
     * is 5000, 3000 more than the base. If you get the relative encoder count you will get 5000-2000
     * = 3000 counts.
     */
    private int baseEncoderCount = 0;

    public void setBaseEncoderCount(int baseEncoderCount) {
        this.baseEncoderCount = baseEncoderCount;
    }

    public int getBaseEncoderCount() {
        return baseEncoderCount;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public RelativeEncoder(int baseEncoderCount) {
        this.baseEncoderCount = baseEncoderCount;
    }

    public RelativeEncoder() {
        this.baseEncoderCount = 0;
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
     * Zero the base encoder count
     */
    public void resetEncoder() {
        this.baseEncoderCount = 0;
    }

    /**
     * Get the relative encoder count given the hardware encoder count.
     * Suppose the base encoder was set to 2000 counts. The corresponding relative encoder is 0.
     * Later the hardware encoder reads 5000 counts. The relative encoder is 5000-2000 or 3000.
     * @param hardwareEncoderCount
     * @return
     */
    public int getRelativeEncoderCount(int hardwareEncoderCount) {
        return hardwareEncoderCount - baseEncoderCount;
    }

    /**
     * Get the hardware encoder count given the relative encoder count.
     * Suppose the base encoder was set to 2000 counts. The corresponding relative encoder is 0.
     * Later your relative encoder count for a target in a move is 3000 counts.
     * The corresponding hardware encoder is 3000+2000 or 5000.
     * @param relativeEncoderCount
     * @return
     */
    public int getHardwareEncoderCount(int relativeEncoderCount) {
        return relativeEncoderCount + baseEncoderCount;
    }

}

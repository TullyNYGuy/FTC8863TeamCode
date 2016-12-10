package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.Range;

public class PIDControl {

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
     * Proportionality constant for PIDControl
     */
    private double Kp = 0;

    /**
     * Integral Constant for PIDControl
     */
    private double Ki = 0;

    /**
     * Derivitive constant for PIDControl
      */
    private double Kd = 0;

    /**
     * Desired Value for PIDControl. For example 45 degrees for a 45 degree turn.
     */
    private double setpoint = 0;

    private double maxCorrection = 0;

    private double feedback = 0;

    private double threshold = 0;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     *
     * @return Proportionality constant for PIDControl
     */
    public double getKp() {
        return Kp;
    }

    /**
     *
     * @param kp set Proportionality constant for PIDControl
     */
    public void setKp(double kp) {
        Kp = kp;
    }

    /**
     *
     * @return Integral Constant for PIDControl
     */
    public double getKi() {
        return Ki;
    }

    /**
     *
     * @param ki Set Integral Constant for PIDControl
     */
    public void setKi(double ki) {
        Ki = ki;
    }

    /**
     *
      * @return Derivitive constant for PIDControl
     */
    public double getKd() {
        return Kd;
    }

    /**
     *
      * @param kd Set Derivitive constant for PIDControl
     */
    public void setKd(double kd) {
        Kd = kd;
    }

    /**
     *
      * @return Desired Value for PIDControl
     */
    public double getSetpoint() {
        return setpoint;
    }

    public double getFeedback() {
        return feedback;
    }

    public void setFeedback(double feedback) {
        this.feedback = feedback;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    /**
     *
     * @param setpoint Set Desired Value for PIDControl
     */
    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    public double getMaxCorrection() {
        return maxCorrection;
    }

    public void setMaxCorrection(double maxCorrection) {
        this.maxCorrection = maxCorrection;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * Constructor. Integral and Derivivtive not implemented at this time.
      * @param kp Proportionality constant for PIDControl
     * @param ki Integral Constant for PIDControl
     * @param kd Derivitive constant for PIDControl
     * @param setpoint Set Desired Value for PIDControl
     */
    public PIDControl(double kp, double ki, double kd, double setpoint) {
        Kp = kp;
        Ki = ki;
        Kd = kd;
        this.setpoint = setpoint;
    }

    public PIDControl() {
    }

    /**
     * Constructor Ki=0 Kd=0
      * @param kp Proportionality constant for PIDControl
     * @param setpoint Set Desired Value for PIDControl
     */
    public PIDControl(double kp, double setpoint, double maxCorrection) {
        Kp = kp;
        Ki = 0;
        Kd = 0;
        this.maxCorrection = maxCorrection;
        this.setpoint = setpoint;
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
     * Returns correction from PIDControl
      * @param feedback Actual Value from sensor.
     * @return Correction to use in control code.
     */
    public double getCorrection(double feedback){
        setFeedback(feedback);
        double correction = (getSetpoint() - feedback) * getKp();
        correction = Range.clip(correction, -maxCorrection, maxCorrection);
        return correction;
    }
    public boolean isFinished(){
        if (Math.abs(getFeedback() - getSetpoint()) < getThreshold()){
            return true;
        } else {
            return false;
        }
    }
}

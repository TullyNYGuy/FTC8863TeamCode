package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class PIDControlExternalTimer {

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

    private RampControl rampControl;

    private boolean useRampControl = false;

    private double feedback = 0;

    private double threshold = 0;

//    private ElapsedTime elapsedTime;

    private boolean externalTimer = false;

    private double lastTime = 0;

    private double integral = 0;

    private ElapsedTime finishedTimer;

    private double lastFinishedTime = 0;

    private double lastIntegral = 0;

    private double lastError = 0;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * @return Proportionality constant for PIDControl
     */
    public double getKp() {
        return Kp;
    }

    /**
     * @param kp set Proportionality constant for PIDControl
     */
    public void setKp(double kp) {
        Kp = kp;
    }

    /**
     * @return Integral Constant for PIDControl
     */
    public double getKi() {
        return Ki;
    }

    /**
     * @param ki Set Integral Constant for PIDControl
     */
    public void setKi(double ki) {
        Ki = ki;
    }

    /**
     * @return Derivitive constant for PIDControl
     */
    public double getKd() {
        return Kd;
    }

    /**
     * @param kd Set Derivitive constant for PIDControl
     */
    public void setKd(double kd) {
        Kd = kd;
    }

    /**
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

    public boolean isUseRampControl() {
        return useRampControl;
    }

    public void setUseRampControl(boolean useRampControl) {
        this.useRampControl = useRampControl;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * Constructor. Derivivtive not implemented at this time but here for the future.
     *
     * @param kp       Proportionality constant for PIDControl
     * @param ki       Integral Constant for PIDControl
     * @param kd       Derivitive constant for PIDControl
     * @param setpoint Set Desired Value for PIDControl
     */
    public PIDControlExternalTimer(double kp, double ki, double kd, double maxCorrection) {
        this.Kp = kp;
        this.Ki = ki;
        this.Kd = kd;
        this.maxCorrection = maxCorrection;
        this.setpoint = 0;
        rampControl = new RampControl(0, 0, 0);
        useRampControl = false;
        finishedTimer = new ElapsedTime();
        finishedTimer.reset();
        lastFinishedTime = finishedTimer.milliseconds();
    }

    /**
     * Constructor. Derivivtive not implemented at this time but here for the future.
     *
     * @param kp       Proportionality constant for PIDControl
     * @param ki       Integral Constant for PIDControl
     * @param kd       Derivitive constant for PIDControl
     * @param setpoint Set Desired Value for PIDControl
     */
    /**
     * Constructor without setting any gains at all. You should set them later or you will get 0
     * back for the correction.
     */
    public PIDControlExternalTimer() {
        this.Kp = 0;
        this.Ki = 0;
        this.Kd = 0;
        this.maxCorrection = 0;
        this.setpoint = 0;
        rampControl = new RampControl(0, 0, 0);
        useRampControl = false;
        finishedTimer = new ElapsedTime();
        finishedTimer.reset();
        lastFinishedTime = finishedTimer.milliseconds();
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
     * The PID can have a ramp up that slowly applies a correction to the thing being controlled.
     * If you don't use this you can get a big step in the correction applied to the thing.
     *
     * @param valueAtStartTime
     * @param valueAtFinishTime
     * @param timeToReachFinishValueInmSec
     */
    public void setupRamp(double valueAtStartTime, double valueAtFinishTime, double timeToReachFinishValueInmSec) {
        rampControl.setup(valueAtStartTime, valueAtFinishTime, timeToReachFinishValueInmSec);
        setUseRampControl(true);
    }

    /**
     * Reset the integral value and the PID timer used to calculate the integral. You must call this
     * as part of the setup of the PID control.
     */
    public void reset() {
        integral = 0;
        lastIntegral = 0;
        lastError = 0;
        lastTime = 0;
        finishedTimer.reset();
    }

    /**
     * Returns correction from PIDControl
     *
     * @param feedback Actual Value from sensor.
     * @return Correction to use in control code.
     */
    public double getCorrection(double feedback, double currentTimeMS) {
        // set the feedback property so it can be retrieved later
        setFeedback(feedback);
        double error = (getSetpoint() - feedback);
        // figure out the integral part of the correction
        double timeDifference = currentTimeMS - lastTime;
        integral = error * timeDifference * getKi();
        integral = integral + lastIntegral;
        double correction = error * getKp() + integral + getKd() * (error - lastError) / timeDifference;
        // if the correction that is calculated is above the limit of what is of what can be physically
        // controlled (like motor power is 110%), then we have to limit the integral portion or it will
        // windup.
        // if the correction is larger than limit
        // clamp the integral term to the last one OR
        // Kb * (correction - maxCorrection) added back to the integral term
        if (correction > maxCorrection || correction < -maxCorrection) {
            integral = lastIntegral;
        }
        lastIntegral = integral;
        if (useRampControl && !rampControl.isRunning() && !rampControl.isFinished()) {
            rampControl.start();
        }
        correction = rampControl.getRampValueLinear(correction);
        correction = Range.clip(correction, -maxCorrection, maxCorrection);
        lastTime = currentTimeMS;
        lastError = error;
        return correction;
    }

    /**
     * If the thing being controlled has reached the setpoint, within the tolerance/threshold,
     * return true. It has to remain at the setpoint for a certain time in order to be called
     * finished.
     *
     * @return
     */
    public boolean isFinished() {
        if (Math.abs(getFeedback() - getSetpoint()) < getThreshold()) {
            if (finishedTimer.milliseconds() > 100) {
                return true;
            } else {
                return false;
            }
        } else {
            finishedTimer.reset();
            return false;
        }
    }
}

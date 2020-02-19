package org.firstinspires.ftc.teamcode.Lib.FTCLib;


/**
 * This class is intended to synchronize the position of two motors as they move so that they move
 * together. It assumes that the motors are in RUN_TO_POSITION mode. It manipulates the max power
 * of each motor to attempt to keep them in the same position at each instance in time.
 */
public class MotorSynchronizer {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum WhichMotor{
        MOTOR1(0),
        MOTOR2(1);


        public final int index;

        WhichMotor(int index) {
            this.index = index;
        }
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private DcMotor8863 motor1;
    private DcMotor8863 motor2;

    private PIDControl pidControl;

    private int motor1Position;
    private int motor2Position;

    private double correction = 0;

    /**
     * Once the PID has been setup is complete, this gets set to true.
     */
    private boolean setupPIDComplete = false;

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

    public MotorSynchronizer(DcMotor8863 motor1, DcMotor8863 motor2) {
        this.motor1 = motor1;
        this.motor2 = motor2;
        pidControl = new PIDControl();
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

    public void setupPID(double setPoint, double kp, double ki, double kd) {
        // the max that a motor power can be is 1.0
        pidControl.setMaxCorrection(1.0);
        pidControl.setKp(kp);
        pidControl.setKi(ki);
        pidControl.setKd(kd);
        pidControl.setSetpoint(setPoint);
        setupPIDComplete = true;
    }

    public double getCorrection() {
        motor1Position = motor1.getCurrentPosition();
        motor2Position = motor2.getCurrentPosition();
        correction = pidControl.getCorrection(motor1Position - motor2Position);
        return correction;
    }
}

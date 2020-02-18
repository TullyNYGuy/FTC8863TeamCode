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

    private PIDControl pidControl1;
    private PIDControl pidControl2;
    private double correction1;
    private double correction2;
    private double[] corrections;

    private int motor1Position;
    private int motor2Position;

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
        pidControl1 = new PIDControl();
        pidControl2 = new PIDControl();
        corrections = new double[]{0, 0};
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

    public void setupPID(double kp, double ki, double kd) {
        pidControl1.setKp(kp);
        pidControl1.setKi(ki);
        pidControl1.setKd(kd);
        pidControl2.setKp(kp);
        pidControl2.setKi(ki);
        pidControl2.setKd(kd);
        setupPIDComplete = true;
    }

    public double[] synchronizePosition() {
        motor1Position = motor1.getCurrentPosition();
        motor2Position = motor2.getCurrentPosition();
        int averagePosition = (int)(motor2Position - motor1Position) / 2;
        pidControl1.setSetpoint(averagePosition);
        pidControl2.setSetpoint(averagePosition);
        corrections[WhichMotor.MOTOR1.index] = pidControl1.getCorrection(motor1Position);
        corrections[WhichMotor.MOTOR2.index] = pidControl2.getCorrection(motor2Position);
        return corrections;
    }
}

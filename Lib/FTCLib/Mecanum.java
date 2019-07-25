package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class Mecanum {

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
    private Motion motion;
    private Wheels wheels;
    private double leftStickX;
    private double rightStickX;
    private double leftStickY;
    private double rightStickY;

    public class Wheels {
        private double frontLeft;
        private double frontRight;
        private double backLeft;
        private double backRight;

        public double getFrontLeft() {
            return frontLeft;
        }

        public double getFrontRight() {
            return frontRight;
        }

        public double getBackLeft() {
            return backLeft;
        }

        public double getBackRight() {
            return backRight;
        }

        public Wheels(){
            frontLeft = 0;
            frontRight = 0;
            backLeft = 0;
            backRight = 0;
        }
    }

    public class Motion {
        private double theta_d;
        private double v_theta;
        private double vd;

        public double getTheta_d() {
            return theta_d;
        }

        public double getV_theta() {
            return v_theta;
        }

        public double getVd() {
            return vd;
        }

        public Motion(){
            theta_d = 0;
            v_theta = 0;
            vd = 0;
        }
    }
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
    public Mecanum(){
        motion = new Motion();
        wheels = new Wheels();
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
    public Motion joystickToMotion(double leftStickX, double rightStickX,double leftStickY,double rightStickY){

    }
}

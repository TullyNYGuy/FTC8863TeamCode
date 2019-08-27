package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.Telemetry;

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
        protected double frontLeft;
        protected double frontRight;
        protected double backLeft;
        protected double backRight;

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

        private Wheels scale4Numbers(Wheels wheels){
            double biggerNumber = Math.abs(wheels.frontLeft);
            if (biggerNumber < Math.abs(wheels.frontRight)){
                biggerNumber = Math.abs(wheels.frontRight);
            }
            if (biggerNumber < Math.abs(wheels.backRight)){
                biggerNumber = Math.abs(wheels.backRight);
            }
            if (biggerNumber < Math.abs(wheels.backLeft)){
                biggerNumber = Math.abs(wheels.backLeft);
            }
            if (biggerNumber != 0){
                wheels.frontRight = wheels.frontRight / biggerNumber;
            }
            if (biggerNumber != 0) {
                wheels.frontLeft = wheels.frontLeft / biggerNumber;
            }
            if (biggerNumber != 0) {
                wheels.backRight = wheels.backRight / biggerNumber;
            }
            if (biggerNumber != 0) {
                wheels.backLeft = wheels.backLeft / biggerNumber;
            }
            return wheels;
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
    public Wheels calculateWheelVelocity (Motion motion){
        wheels.frontLeft = motion.vd * Math.sin(-motion.theta_d + (Math.PI/4)) - motion.v_theta;
        wheels.frontRight = motion.vd * Math.cos(-motion.theta_d + (Math.PI/4)) + motion.v_theta;
        wheels.backLeft = motion.vd * Math.cos(-motion.theta_d + (Math.PI/4)) - motion.v_theta;
        wheels.backRight = motion.vd * Math.sin(-motion.theta_d + (Math.PI/4)) + motion.v_theta;
    return wheels.scale4Numbers(wheels);
    }
    public void test(Telemetry telemetry){
        motion = new Motion();
        motion.v_theta = 0.3;
        motion.theta_d = Math.PI/2;
        motion.vd = 0.99;
        Wheels wheels = calculateWheelVelocity(motion);
        telemetry.addData("front left = ",wheels.frontLeft);
        telemetry.addData("front right = ",wheels.frontRight);
        telemetry.addData("back left = ",wheels.backLeft);
        telemetry.addData("back right = ",wheels.backRight);
    }
}

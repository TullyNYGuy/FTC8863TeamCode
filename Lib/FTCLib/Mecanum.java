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
    private Wheels wheels;
    private double leftStickX;
    private double rightStickX;
    private double leftStickY;
    private double rightStickY;

    public class Wheels {
        protected double frontLeft = 0;
        protected double frontRight = 0;
        protected double backLeft = 0;
        protected double backRight = 0;

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

        public Wheels() {
            frontLeft = 0;
            frontRight = 0;
            backLeft = 0;
            backRight = 0;
        }

        private Wheels scale4Numbers(Wheels wheels) {
            double biggerNumber = Math.abs(wheels.frontLeft);
            if (biggerNumber < Math.abs(wheels.frontRight)) {
                biggerNumber = Math.abs(wheels.frontRight);
            }
            if (biggerNumber < Math.abs(wheels.backRight)) {
                biggerNumber = Math.abs(wheels.backRight);
            }
            if (biggerNumber < Math.abs(wheels.backLeft)) {
                biggerNumber = Math.abs(wheels.backLeft);
            }
            if (biggerNumber != 0 && biggerNumber > 1) {
                wheels.frontRight = wheels.frontRight / biggerNumber;
                wheels.frontLeft = wheels.frontLeft / biggerNumber;
                wheels.backRight = wheels.backRight / biggerNumber;
                wheels.backLeft = wheels.backLeft / biggerNumber;
            }
            return wheels;
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
    public Mecanum() {
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
    //if speed of rotation is = 0 then our max speed is 0.707. We may want to scale up to 1.
    public Wheels calculateWheelVelocity(MecanumData mecanumData) {
        wheels.frontLeft = mecanumData.getSpeed() * Math.sin(-mecanumData.getAngleOfTranslation() + (Math.PI / 4)) - mecanumData.getSpeedOfRotation();
        wheels.frontRight = mecanumData.getSpeed() * Math.cos(-mecanumData.getAngleOfTranslation() + (Math.PI / 4)) + mecanumData.getSpeedOfRotation();
        wheels.backLeft = mecanumData.getSpeed() * Math.cos(-mecanumData.getAngleOfTranslation() + (Math.PI / 4)) - mecanumData.getSpeedOfRotation();
        wheels.backRight = mecanumData.getSpeed() * Math.sin(-mecanumData.getAngleOfTranslation() + (Math.PI / 4)) + mecanumData.getSpeedOfRotation();
        return wheels.scale4Numbers(wheels);
    }

    public void test(Telemetry telemetry) {
        MecanumData mecanumData = new MecanumData();
        mecanumData.setSpeedOfRotation(0);
        mecanumData.setAngleOfTranslation(-Math.PI / 2);
        mecanumData.setSpeed(0.5);
        Wheels wheels = calculateWheelVelocity(mecanumData);
        telemetry.addData("front left = ", wheels.frontLeft);
        telemetry.addData("front right = ", wheels.frontRight);
        telemetry.addData("back left = ", wheels.backLeft);
        telemetry.addData("back right = ", wheels.backRight);
    }
}
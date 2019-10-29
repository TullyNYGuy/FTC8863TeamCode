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
    private WheelVelocities wheelVelocities;
    private double leftStickX;
    private double rightStickX;
    private double leftStickY;
    private double rightStickY;

    public double getFrontLeft() {
        return wheelVelocities.getFrontLeft();
    }

    public double getBackLeft() {
        return wheelVelocities.getBackLeft();
    }

    public double getFrontRight() {
        return wheelVelocities.getFrontRight();
    }

    public double getBackRight() {
        return wheelVelocities.getBackRight();
    }

    static public class WheelVelocities {
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

        public WheelVelocities() {
            frontLeft = 0;
            frontRight = 0;
            backLeft = 0;
            backRight = 0;
        }

        private WheelVelocities scale4Numbers(WheelVelocities wheelVelocities) {
            double biggerNumber = Math.abs(wheelVelocities.frontLeft);
            if (biggerNumber < Math.abs(wheelVelocities.frontRight)) {
                biggerNumber = Math.abs(wheelVelocities.frontRight);
            }
            if (biggerNumber < Math.abs(wheelVelocities.backRight)) {
                biggerNumber = Math.abs(wheelVelocities.backRight);
            }
            if (biggerNumber < Math.abs(wheelVelocities.backLeft)) {
                biggerNumber = Math.abs(wheelVelocities.backLeft);
            }
            if (biggerNumber != 0 && biggerNumber > 1) {
                wheelVelocities.frontRight = wheelVelocities.frontRight / biggerNumber;
                wheelVelocities.frontLeft = wheelVelocities.frontLeft / biggerNumber;
                wheelVelocities.backRight = wheelVelocities.backRight / biggerNumber;
                wheelVelocities.backLeft = wheelVelocities.backLeft / biggerNumber;
            }
            return wheelVelocities;
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
        wheelVelocities = new WheelVelocities();
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
    public WheelVelocities calculateWheelVelocity(MecanumCommands mecanumCommands) {
        wheelVelocities.frontLeft = mecanumCommands.getSpeed() * Math.sin(-mecanumCommands.getAngleOfTranslation() + (Math.PI / 4)) + mecanumCommands.getSpeedOfRotation();
        wheelVelocities.frontRight = mecanumCommands.getSpeed() * Math.cos(-mecanumCommands.getAngleOfTranslation() + (Math.PI / 4)) - mecanumCommands.getSpeedOfRotation();
        wheelVelocities.backLeft = mecanumCommands.getSpeed() * Math.cos(-mecanumCommands.getAngleOfTranslation() + (Math.PI / 4)) + mecanumCommands.getSpeedOfRotation();
        wheelVelocities.backRight = mecanumCommands.getSpeed() * Math.sin(-mecanumCommands.getAngleOfTranslation() + (Math.PI / 4)) - mecanumCommands.getSpeedOfRotation();
        return wheelVelocities.scale4Numbers(wheelVelocities);
    }

    public void test(Telemetry telemetry) {
        MecanumCommands mecanumCommands = new MecanumCommands();
        mecanumCommands.setSpeedOfRotation(0);
        mecanumCommands.setAngleOfTranslation(-Math.PI / 2);
        mecanumCommands.setSpeed(0.5);
        WheelVelocities wheelVelocities = calculateWheelVelocity(mecanumCommands);
        telemetry.addData("front left = ", wheelVelocities.frontLeft);
        telemetry.addData("front right = ", wheelVelocities.frontRight);
        telemetry.addData("back left = ", wheelVelocities.backLeft);
        telemetry.addData("back right = ", wheelVelocities.backRight);
    }
}
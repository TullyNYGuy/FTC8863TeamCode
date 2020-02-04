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
    private DcMotor8863 frontLeft;
    private DcMotor8863 frontRight;
    private DcMotor8863 backLeft;
    private DcMotor8863 backRight;

    private static double SPEED_ADJUSTER = 1 / Math.cos(Math.PI / 4);

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

        public WheelVelocities() {
            frontLeft = 0;
            frontRight = 0;
            backLeft = 0;
            backRight = 0;
        }

        private void scale4Numbers() {
            double biggerNumber = Math.abs(frontLeft);
            if (biggerNumber < Math.abs(frontRight)) {
                biggerNumber = Math.abs(frontRight);
            }
            if (biggerNumber < Math.abs(backRight)) {
                biggerNumber = Math.abs(backRight);
            }
            if (biggerNumber < Math.abs(backLeft)) {
                biggerNumber = Math.abs(backLeft);
            }
            if (biggerNumber > 1) {
                frontRight = frontRight / biggerNumber;
                frontLeft = frontLeft / biggerNumber;
                backRight = backRight / biggerNumber;
                backLeft = backLeft / biggerNumber;
            }
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
    public Mecanum(DcMotor8863 frontLeft, DcMotor8863 frontRight, DcMotor8863 backLeft, DcMotor8863 backRight) {
        wheelVelocities = new WheelVelocities();
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
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
        wheelVelocities.frontLeft = mecanumCommands.getSpeed() * Math.sin(-mecanumCommands.getAngleOfTranslation() + (Math.PI / 4)) * SPEED_ADJUSTER + mecanumCommands.getSpeedOfRotation();
        wheelVelocities.frontRight = mecanumCommands.getSpeed() * Math.cos(-mecanumCommands.getAngleOfTranslation() + (Math.PI / 4)) * SPEED_ADJUSTER - mecanumCommands.getSpeedOfRotation();
        wheelVelocities.backLeft = mecanumCommands.getSpeed() * Math.cos(-mecanumCommands.getAngleOfTranslation() + (Math.PI / 4)) * SPEED_ADJUSTER + mecanumCommands.getSpeedOfRotation();
        wheelVelocities.backRight = mecanumCommands.getSpeed() * Math.sin(-mecanumCommands.getAngleOfTranslation() + (Math.PI / 4)) * SPEED_ADJUSTER - mecanumCommands.getSpeedOfRotation();
        wheelVelocities.scale4Numbers();
        return wheelVelocities;
    }

    public void setMotorPower(MecanumCommands mecanumCommands) {
        calculateWheelVelocity(mecanumCommands);
        // update() is a call that runs the state machine for the motor. It is really only needed
        // when we are running the motor using feedback.
        //frontLeft.update();

        // set the power to the motor. This is the call to use when changing power after the
        // motor is set up for a mode.

        frontLeft.setPower(wheelVelocities.frontLeft);
        frontRight.setPower(wheelVelocities.frontRight);
        backLeft.setPower(wheelVelocities.backLeft);
        backRight.setPower((wheelVelocities.backRight));
    }

    public void stopMotor() {
        frontLeft.stop();
        backLeft.stop();
        frontRight.stop();
        backRight.stop();
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
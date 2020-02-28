package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Mecanum {

    final private String PROP_MIN_POWER = "Mecanum.minMotorPower";
    final private String PROP_MIN_POWER_MULTIPLIER = "Mecanum.minMotorPowerMultiplier";
    final private String PROP_MAX_POWER = "Mecanum.maxMotorPower";

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
    private DcMotor8863 frontLeft;
    private DcMotor8863 frontRight;
    private DcMotor8863 backLeft;
    private DcMotor8863 backRight;
    private Telemetry telemetry;

    private final static double SPEED_ADJUSTER = 1 / Math.cos(Math.PI / 4);

    private double minMotorPower;

    private double minMotorPowerMultiplier;

    private double maxMotorPower;

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

        private double getFrontLeft() {
            return frontLeft;
        }

        private double getFrontRight() {
            return frontRight;
        }

        private double getBackLeft() {
            return backLeft;
        }

        private double getBackRight() {
            return backRight;
        }

        WheelVelocities() {
            frontLeft = 0;
            frontRight = 0;
            backLeft = 0;
            backRight = 0;
        }

        private void scale4Numbers(double maxMotorPower) {
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
            if (biggerNumber > maxMotorPower) {
                frontRight = frontRight / biggerNumber * maxMotorPower;
                frontLeft = frontLeft / biggerNumber * maxMotorPower;
                backRight = backRight / biggerNumber * maxMotorPower;
                backLeft = backLeft / biggerNumber * maxMotorPower;
            }
        }

        @Override
        public String toString() {
            return String.format("FL: %.2f, FR: %.2f, BL: %.2f, BR: %.2f", frontLeft, frontRight, backLeft, backRight);
        }

    }
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    public double getMinMotorPower() {
        return minMotorPower;
    }

    public void setMinMotorPower(double minMotorPower) {
        if (minMotorPower > 1.0)
            minMotorPower = 1.0;
        if (minMotorPower < 0)
            minMotorPower = 0;
        this.minMotorPower = minMotorPower;
    }

    public double getMinMotorPowerMultiplier() {
        return minMotorPowerMultiplier;
    }

    public void setMinMotorPowerMultiplier(double minMotorPowerMultiplier) {
        if (minMotorPowerMultiplier > 1.0)
            minMotorPowerMultiplier = 1.0;
        if (minMotorPowerMultiplier < 0)
            minMotorPowerMultiplier = 0;
        this.minMotorPowerMultiplier = minMotorPowerMultiplier;
    }

    public double getMaxMotorPower() {
        return maxMotorPower;
    }

    public void setMaxMotorPower(double maxMotorPower) {
        if (maxMotorPower > 1.0)
            maxMotorPower = 1.0;
        if (maxMotorPower < 0)
            maxMotorPower = 0;
        this.maxMotorPower = maxMotorPower;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public Mecanum(DcMotor8863 frontLeft, DcMotor8863 frontRight, DcMotor8863 backLeft, DcMotor8863 backRight, Telemetry telemetry) {
        wheelVelocities = new WheelVelocities();
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
        this.telemetry = telemetry;
        setMinMotorPower(0.0);
        setMinMotorPowerMultiplier(1.0);
        setMaxMotorPower(1.0);
    }

    public Mecanum(DcMotor8863 frontLeft, DcMotor8863 frontRight, DcMotor8863 backLeft, DcMotor8863 backRight, Telemetry telemetry, double minMotorPower, double minMotorPowerMultiplier) {
        wheelVelocities = new WheelVelocities();
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
        this.telemetry = telemetry;
        setMinMotorPower(minMotorPower);
        setMinMotorPowerMultiplier(minMotorPowerMultiplier);
        setMaxMotorPower(1.0);
    }

    public boolean init(Configuration config) {
        if (config != null) {
            setMinMotorPower(config.getPropertyDouble(PROP_MIN_POWER, 0.14));
            setMinMotorPowerMultiplier(config.getPropertyDouble(PROP_MIN_POWER_MULTIPLIER, 0.5));
            setMaxMotorPower(config.getPropertyDouble(PROP_MAX_POWER, 1.0));
            return true;
        } else {
            setMinMotorPower(0.14);
            setMinMotorPowerMultiplier(0.5);
            setMaxMotorPower(1.0);
        }
        return false;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private double adjustForMinimumPower(double power) {
        if (power > 0 && power < minMotorPower) {
            if (power < minMotorPower * minMotorPowerMultiplier)
                return 0.0;
            else
                return minMotorPower;
        } else if (power < 0 && power > -minMotorPower) {
            if (power > -minMotorPower * minMotorPowerMultiplier)
                return 0.0;
            else
                return -minMotorPower;
        } else {
            return power;
        }
    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    //if speed of rotation is = 0 then our max speed is 0.707. We may want to scale up to 1.
    public WheelVelocities calculateWheelVelocity(MecanumCommands mecanumCommands) {
        wheelVelocities.frontLeft = mecanumCommands.getSpeed() * Math.sin(-mecanumCommands.getAngleOfTranslation(AngleUnit.RADIANS) + (Math.PI / 4)) * SPEED_ADJUSTER - mecanumCommands.getSpeedOfRotation();
        wheelVelocities.frontRight = mecanumCommands.getSpeed() * Math.cos(-mecanumCommands.getAngleOfTranslation(AngleUnit.RADIANS) + (Math.PI / 4)) * SPEED_ADJUSTER + mecanumCommands.getSpeedOfRotation();
        wheelVelocities.backLeft = mecanumCommands.getSpeed() * Math.cos(-mecanumCommands.getAngleOfTranslation(AngleUnit.RADIANS) + (Math.PI / 4)) * SPEED_ADJUSTER - mecanumCommands.getSpeedOfRotation();
        wheelVelocities.backRight = mecanumCommands.getSpeed() * Math.sin(-mecanumCommands.getAngleOfTranslation(AngleUnit.RADIANS) + (Math.PI / 4)) * SPEED_ADJUSTER + mecanumCommands.getSpeedOfRotation();
        wheelVelocities.scale4Numbers(maxMotorPower);
        wheelVelocities.frontLeft = adjustForMinimumPower(wheelVelocities.frontLeft);
        wheelVelocities.frontRight = adjustForMinimumPower(wheelVelocities.frontRight);
        wheelVelocities.backLeft = adjustForMinimumPower(wheelVelocities.backLeft);
        wheelVelocities.backRight = adjustForMinimumPower(wheelVelocities.backRight);
        return wheelVelocities;
    }

    public void setMotorPower(MecanumCommands mecanumCommands) {
        calculateWheelVelocity(mecanumCommands);
        // update() is a call that runs the state machine for the motor. It is really only needed
        // when we are running the motor using feedback.
        //frontLeft.update();

        // set the power to the motor. This is the call to use when changing power after the
        // motor is set up for a mode.

       /*
        telemetry.addData("wheel (FL, FR, BL, BR): ",
                String.format("(%.2f, %.2f, %.2f, %.2f)", wheelVelocities.frontLeft, wheelVelocities.frontRight,
                        wheelVelocities.backLeft, wheelVelocities.backRight));

        */
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
        mecanumCommands.setAngleOfTranslation(AngleUnit.RADIANS, -Math.PI / 2);
        mecanumCommands.setSpeed(0.5);
        WheelVelocities wheelVelocities = calculateWheelVelocity(mecanumCommands);
        telemetry.addData("front left = ", wheelVelocities.frontLeft);
        telemetry.addData("front right = ", wheelVelocities.frontRight);
        telemetry.addData("back left = ", wheelVelocities.backLeft);
        telemetry.addData("back right = ", wheelVelocities.backRight);
    }
}
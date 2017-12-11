package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.BNO055IMUImpl;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This class simplifies the interface to the Adafruit IMU. It allows the user to get heading, roll,
 * and pitch angles that are either
 * RAW - as read from the IMU. 0 position is the position of the robot when the IMU was
 * initialized. Kindof. It seems like the IMU does not initialize with pitch and roll not equal
 * to 0. Angles reported can be from -360 to +360.
 * ABSOLUTE - uses 0 position as the position of the robot when the IMU was initialized but adjusts
 * the angles so that they are between -180 and +180. This modes uses code to force the angles
 * at initialization to 0.
 * RELATIVE - uses 0 position as the position of the robot when a resetAngleReferences() was called.
 * The angles are adjusted so that they are between -180 and +180.
 * <p>
 * This class wraps AdafruitBNO055IMU.
 */
public class AdafruitIMU8863 {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * Enum to set the reference point (0 point) for the angles.
     * RAW - the angle as read directly from the IMU.
     * ABSOLUTE - 0 angle is when the IMU was first initialized. This can be used to track the robot
     * position using its 0 position as when it was first started up, typically at the
     * start of the match.
     * RELATIVE - 0 angle is when the last resetAngleReferences() was issued. This can be used to track the
     * robot position since the start of some time or motion. For example, tracking a turn.
     * The start of the turn is 0. A resetAngleReferences() would be issued then. The instant after
     * the turn started would read heading = 0. As the turn proceeded the heading would
     * incrase.
     */
    public enum AngleMode {
        RAW,
        ABSOLUTE,
        RELATIVE;
    }

    /**
     * Enum to define which angle is being referenced.
     */
    private enum WhichAngle {
        HEADING(0),
        ROLL(1),
        PITCH(2);

        private byte index;

        WhichAngle(int i) {
            this.index = (byte) i;
        }
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * The IMU
     */
    private BNO055IMU imu;

    /**
     * The angles read from the IMU.
     */
    private Orientation angles;

    /**
     * The angles to use as 0 reference points for RELATIVE mode
     */
    private Orientation angleReferencesRelative;

    /**
     * The angles to use as 0 reference points for ABSOLUTE mode
     */
    private Orientation angleReferencesAbsolute;

    /**
     * Gravity as read from the IMU
     */
    private Acceleration gravity;

    /**
     * The IMU can be manually calibrated through a series of motions. The calibration data
     * can be written to a file. Then the file can be read later on when an IMU object is created.
     * This saves time by not having to do a manual calibration again. But you don't have to do a manual
     * calibration. The IMU is internally self calibrating. If a manual calibration exists then it
     * saves time for the internal self calibration. This property holds the name of the calibration
     * file. You may or may not have run the manual calibration earlier. If it is null the manual
     * calibration has not been run.
     *
     * @see com.qualcomm.hardware.adafruit.BNO055IMU.Parameters#calibrationDataFile
     * and com.qualcomm.ftcrobotcontroller.external.samples.SensorAdafruitIMUCalibration
     */
    private String calibrationFile = null;

    /**
     * The IMU needs to be initialized using a set of initial parameters. There is an object to
     * hold the parameters.
     */
    BNO055IMU.Parameters parameters;

    /**
     * A string to use when writing data from the IMU into a log file.
     */
    private String loggingTag = "IMU";

    private AngleMode angleMode = AngleMode.RELATIVE;

    /**
     * This is the order of the angles read from the IMU. If this is changed, the WhichAngle enum
     * must be changed to correspond.
     */
    private final static AxesOrder HEADING_ROLL_PITCH = AxesOrder.ZYX;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * Is the IMU currently setup to give angles using a 0 reference defined by resetAngleReferences()
     * or a 0 reference defined when the IMU was initialized.
     *
     * @return AngleMode.ABSOLUTE or AngleMode.RELATIVE
     */
    public AngleMode getAngleMode() {
        return angleMode;
    }

    /**
     * Set the angle mode. Angles are computed relative to a 0 reference defined by resetAngleReferences()
     * or a 0 reference defined when the IMU was initialized.
     *
     * @param angleMode AngleMode.ABSOLUTE or AngleMode.RELATIVE
     */
    public void setAngleMode(AngleMode angleMode) {
        this.angleMode = angleMode;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    // Initially there will only be one IMU on a robot. But in the future there might be more.
    // The reason for configurable loggingTag and calibrationFile is to allow multiple IMUs. Each
    // one can have its own loggingTag and calibrationFile.
    // NOTE: There needs to be a way to set the IMUName differently for each IMU. That is not
    // implemented right now. The IMUName is gotten from the RobotConfigMapping and therefore it
    // can't be different for different IMUs. So right now there can only be one IMU on the robot.

    public AdafruitIMU8863(HardwareMap hardwareMap, String calibrationFile, String loggingTag) {
        this.loggingTag = loggingTag;
        this.calibrationFile = calibrationFile;
        parameters = setupParameters();
        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, RobotConfigMappingForGenericTest.getIMUName());
        imu.initialize(parameters);
        // The resetAngleReferences() does not seem to be getting correct data. I'm guessing that
        // the IMU has not finished initializing yet. Delay the execution of resetAngleReferences()
        // so that initialization of the BNO055IMU can complete.
        delay(100);
        // At initialization both absolute and relative references need to be setup
        resetAngleReferences(AngleMode.ABSOLUTE);
        resetAngleReferences(AngleMode.RELATIVE);
    }

    public AdafruitIMU8863(HardwareMap hardwareMap, String calibrationFile) {
        this.loggingTag = "IMU";
        this.calibrationFile = calibrationFile;
        parameters = setupParameters();
        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, RobotConfigMappingForGenericTest.getIMUName());
        imu.initialize(parameters);
        // The resetAngleReferences() does not seem to be getting correct data. I'm guessing that
        // the IMU has not finished initializing yet. Delay the execution of resetAngleReferences()
        // so that initialization of the BNO055IMU can complete.
        delay(100);
        // At initialization both absolute and relative references need to be setup
        resetAngleReferences(AngleMode.ABSOLUTE);
        resetAngleReferences(AngleMode.RELATIVE);
    }

    public AdafruitIMU8863(HardwareMap hardwareMap) {
        this.loggingTag = "IMU";
        // No calibration file is used
        this.calibrationFile = null;
        parameters = setupParameters();
        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "IMU"
        imu = hardwareMap.get(BNO055IMU.class, RobotConfigMappingForGenericTest.getIMUName());
        imu.initialize(parameters);
        // The resetAngleReferences() does not seem to be getting correct data. I'm guessing that
        // the IMU has not finished initializing yet. Delay the execution of resetAngleReferences()
        // so that initialization of the BNO055IMU can complete.
        delay(100);
        // At initialization both absolute and relative references need to be setup
        resetAngleReferences(AngleMode.ABSOLUTE);
        resetAngleReferences(AngleMode.RELATIVE);
    }

    /**
     * Defines the initialization paremeters for the IMU.
     *
     * @return intialization parameters.
     */
    private BNO055IMU.Parameters setupParameters() {
        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = null;
        //parameters.calibrationDataFile = this.calibrationFile;
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        //parameters.loggingTag = this.loggingTag;
        // At some point I'll have to look into this to see if an integration algorithm can
        // be used to get velocity and position.
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        return parameters;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Get an angle value. Can be heading, pitch, or roll. Adjust it to the reference desired
     * (RELATIVE or ABSOLUTE) and to the range desired.
     *
     * @param angles     a data structure with the heading, roll and pitch angles
     * @param whichAngle do you want the heading, roll or pitch angle?
     * @return the adjusted angle
     */
    private float getAngleUsingReference(Orientation angles, WhichAngle whichAngle) {
        float angleReference = 0;
        float angle = 0;

        // Java insists that angleReferences has to be initialized in order to compile. Even
        // though it gets set later in all cases.
        Orientation angleReferences = angles;

        // Get the reference angles to use in adjusting the angle
        switch (angleMode) {
            case ABSOLUTE:
                angleReferences = angleReferencesAbsolute;
                break;
            case RELATIVE:
                angleReferences = angleReferencesRelative;
                break;
            // no reference adjustment for RAW
            case RAW:
                break;
        }

        // Extract the poper angle from thh data
        switch (whichAngle) {
            case HEADING:
                angleReference = angleReferences.firstAngle;
                angle = angles.firstAngle;
                break;
            case ROLL:
                angleReference = angleReferences.secondAngle;
                angle = angles.secondAngle;
                break;
            case PITCH:
                angleReference = angleReferences.thirdAngle;
                angle = angles.thirdAngle;
                break;
        }

        // make the angle adjustements
        switch (angleMode) {
            case RELATIVE:
            case ABSOLUTE:
                // The angle should be between -180 and +180. The IMU can report -259 which is really
                // +1
                angle = AngleUnit.normalizeDegrees(angle - angleReference);
                break;
            // Return the actual reading from the IMU - no adjustments
            case RAW:
                break;
        }
        return angle;
    }

    /**
     * Implements a delay
     *
     * @param mSec delay in milli Seconds
     */
    private void delay(int mSec) {
        try {
            Thread.sleep((int) (mSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Read the angles from the IMU. This method forces a read of the IMU.
     *
     * @return angles
     */
    public Orientation getAngularOrientation() {
        angles = imu.getAngularOrientation().toAxesReference(AxesReference.INTRINSIC).toAxesOrder(HEADING_ROLL_PITCH);
        return angles;
    }

    /**
     * Get the roll of the robot. This is the tilt front or back. This can be RELATIVE, ABSOLUTE or
     * RAW. See class documentation for definition of those modes.
     *
     * @return pitch in degrees
     */
    public double getPitch() {
        double pitch;
        // reading the angles to get one pitch is ok. But if you want pitch, heading and roll
        // this is not efficient. A better approach in that case would be to read the angles once
        // and then return each angle.
        angles = getAngularOrientation();
        return (getAngleUsingReference(angles, WhichAngle.PITCH));
    }

    /**
     * Get the roll of the robot. This is the tilt left or right. This can be RELATIVE, ABSOLUTE or
     * RAW. See class documentation for definition of those modes.
     *
     * @return roll in degrees
     */
    public double getRoll() {
        angles = getAngularOrientation();
        return (getAngleUsingReference(angles, WhichAngle.ROLL));
    }

    /**
     * Get the heading of the robot. This is the left or right direction. This can be RELATIVE,
     * ABSOLUTE or RAW. See class documentation for definition of those modes.
     *
     * @return heading in degrees
     */
    public double getHeading() {
        angles = getAngularOrientation();
        return (getAngleUsingReference(angles, WhichAngle.HEADING));
    }

    /**
     * Read the angles from the IMU and save those for use in later calculating absolute or relative
     * angles. At initialization of the IMU, the angles are saved for use in later calculations to
     * return the absolute angles. This has to be done since the IMU does not initialize so that all
     * angles are at 0.
     *
     * @param angleMode save the angles for absolute or for relative calculations
     */
    private void resetAngleReferences(AngleMode angleMode) {
        Orientation angles;
        // read the angles
        angles = this.getAngularOrientation();

        // figure out where to save the angles
        switch (angleMode) {
            case RELATIVE:
                angleReferencesRelative = angles;
                break;
            case ABSOLUTE:
                angleReferencesAbsolute = angles;
                break;
        }
    }

    /**
     * Reset the 0 angle reference to the position of the robot at this time. This is public so that
     * the user can call this. For example just before a 45 degree turn.
     */
    public void resetAngleReferences() {
        resetAngleReferences(AngleMode.RELATIVE);
    }

    /**
     * Wrapper for AdafruitBNO055IMU getGravity().
     * Returns the direction of the force of gravity relative to the sensor.
     *
     * @return the acceleration vector of gravity relative to the sensor
     */
    public Acceleration getGravity() {
        return imu.getGravity();
    }

    /**
     * Wrapper for AdafruitBNO055IMU getSystemStatus().
     * Returns the current status of the system.
     *
     * @return
     */
    public BNO055IMU.SystemStatus getSystemStatus() {
        return getSystemStatus();
    }

    /**
     * Wrapper for AdafruitBNO055IMU getCalibrationStatus().
     * Returns the calibration status of the IMU
     *
     * @return the calibration status of the IMU
     */
    public BNO055IMU.CalibrationStatus getCalibrationStatus() {
        return getCalibrationStatus();
    }

    /**
     * Wrapper for AdafruitBNO055IMU startAccelerationIntegration().
     * Start (or re-start) a thread that continuously at intervals polls the current linear acceleration
     * of the sensor and integrates it to provide velocity and position information.
     *
     * @param initialPosition If non-null, the current sensor position is set to this value. If
     *                        null, the current sensor position is unchanged.
     * @param initialVelocity If non-null, the current sensor velocity is set to this value. If
     *                        null, the current sensor velocity is unchanged.
     * @param msPollInterval  the interval to use, in milliseconds, between successive calls to getLinearAcceleration()
     *                        see stopAccelerationIntegration()
     * @see BNO055IMU.AccelerationIntegrator
     */
    public void startAccelerationIntegration(Position initialPosition, Velocity initialVelocity, int msPollInterval) {
        imu.startAccelerationIntegration(initialPosition, initialVelocity, msPollInterval);
    }

    /**
     * Checks to see if the chip id can be read from the IMU. If it can then the IMU is connected to
     * the core device interface module correctly. If it cannot be read something is wrong, most
     * likely the wiring.
     * @return true if connected
     */
    public boolean isIMUConnected() {
        byte bCHIP_ID_VALUE = (byte) 0xa0;
        boolean result;

        byte chipId = imu.read8(BNO055IMU.Register.CHIP_ID);
        if (chipId != bCHIP_ID_VALUE) {
            delayExtra(650);     // delay value is from from Table 0-2 in the BNO055 specification
            chipId = imu.read8(BNO055IMU.Register.CHIP_ID);
        }
        if (chipId != bCHIP_ID_VALUE) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }

    /**
     * A method to implement a delay.
     * @param ms
     */
    protected void delayExtra(int ms) {
        int msExtra = 50;
        delay(ms + msExtra);
    }
}
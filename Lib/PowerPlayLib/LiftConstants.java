package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;

@Config
public class LiftConstants {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    public static double MAX_VELOCITY_EXTENSION = 60; //in/sec
    public static double MAX_ACCELERATION_EXTENSION = 200; // in/sec^2

    public static double MAX_VELOCITY_RETRACTION = 60; //in/sec
    public static double MAX_ACCELERATION_RETRACTION = 200; // in/sec^2

    //public static double MOVEMENT_PER_REVOLUTION = 5.93; // in / motor revolution
    public static double MOVEMENT_PER_REVOLUTION = 5.867; // in / motor revolution

    public static double MAX_RPM = 1150; // max rpm of motor

    // These are the feedforward parameters
    public static double kVExtension = 0.014;
    public static double kVRetraction0ToMinus60 = 0.0057;
    public static double kVRetractionMinus60ToMinus78 = 0.024;
    public static double kVRetractionMinus78ToMinusInfinity = 0.015;
    public static double kAExtension = .001;
    public static double kARetraction = .001;

    //public static double kStatic = 0.3347;
    public static double kStatic = 0;
    public static double kGAtRetraction = .241;
    public static double kGRetraction0ToMinus60 = .241;
    public static double kGRetractionMinus60ToMinus78 = 1.323;
    public static double kGRetractionMinus78ToMinusInfinity = .597;
    //public static double kGAtRetraction = .3347;
    //public static double kGPerUnitExtension = .0031; // kG/in
    public static double kGPerUnitExtension = 0.0; // kG/in

    public static double getKg(double liftPosition) {
        return kGPerUnitExtension * liftPosition + kGAtRetraction;
    }

    // This lift is non-linear when it is retracting. I have modeled it using 3 different zones
    // depending on lift velocity.
    public static Double[] getkVkGForRetraction(double desiredLiftPosition, double desiredLiftVelocity) {
        // kV and KG when liftVelocity = 0;
        double kV = kVExtension;
        double kG = kGAtRetraction;
        if (desiredLiftVelocity < -78.0) {
            kV = kVRetractionMinus78ToMinusInfinity;
            kG = kGRetractionMinus78ToMinusInfinity;
        } else {
            if (desiredLiftVelocity < -60.0 && desiredLiftVelocity >= -78.0) {
                kV = kVRetractionMinus60ToMinus78;
                kG = kGRetractionMinus60ToMinus78;
            } else {
                if (desiredLiftVelocity < 0 && desiredLiftVelocity >= -60.0) {
                    kV = kVRetraction0ToMinus60;
                    kG = kGRetraction0ToMinus60;
                }
            }
        }
        Double[] kVkG = {kV,kG};
        return kVkG;
    }

//    public static double getKvRetraction(double liftPosition, double liftVelocity) {
//        double kV = kGAtRetraction;
//        if (liftVelocity < -78.0) {
//            kV = kVRetractionMinus78ToMinusInfinity;
//        } else {
//            if (liftVelocity < -60.0 && liftVelocity >= -78.0) {
//                kV = kVRetractionMinus60ToMinus78;
//            } else {
//                if (liftVelocity < 0 && liftVelocity >= -60.0) {
//                    kV = kVRetraction0ToMinus60;
//                }
//            }
//        }
//        return kV;
//    }

    public static PIDCoefficients MOTION_PID_COEFFICENTS = new PIDCoefficients(.6, 0, 0);

    public static double MAXIMUM_LIFT_POSITION = 37; // INCHES
    public static double MINIMUM_LIFT_POSITION = 2; // INCHES

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

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

}

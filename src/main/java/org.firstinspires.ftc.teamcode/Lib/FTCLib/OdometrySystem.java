package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import android.icu.math.MathContext;

public class OdometrySystem {
    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum Position {
        LEFT,
        RIGHT,
        FRONT
    }

    public enum Units {
        IN,
        CM
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
        private double leftEncoderValue;

        private double rightEncoderValue;

        private double backEncoderValue;

        private double robotRadius;

        private double angleOfRotation;

        private double leftEncoderValueRevised;

        private double rightEncoderValueRevised;

        private double backEncoderValueRevised;

        private double angleOfTranslation;

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


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
     public void findAngleOfRotation(){
         //this will be used to alter the encoder values to provide info about the straight translation
        angleOfRotation = 2* robotRadius / ( Math.abs(leftEncoderValue)- Math.abs(rightEncoderValue)) ;
      }
    public double findLengthOfTranslation(){
        return Math.sqrt(leftEncoderValue*leftEncoderValue  + backEncoderValue*backEncoderValue);
    }

    public void findAngleOfTranslation(){
         angleOfTranslation = Math.cos(backEncoderValue/leftEncoderValue);
    }

    public void findArcLength(){
         arcLength = robotRadius * angleOfRotation;
    }

    public void cancelOutAngleFromMovement(){
        leftEncoderValueRevised = leftEncoderValue -

    }
    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

}

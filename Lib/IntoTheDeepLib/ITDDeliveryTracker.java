package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;


public class ITDDeliveryTracker {

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
    private int numberOfSamplesDelivered = 0;

    private ITDLift lift;

    public void setLift(ITDLift lift) {
        this.lift = lift;
        lift.setDeliveryHeight(ITDLift.Basket.HIGH_AUTO);
    }

    public void deliveryOccured() {
        numberOfSamplesDelivered++;
        setHighDeliveryPosition();
    }

    public void setHighDeliveryPosition() {
        if (numberOfSamplesDelivered > 9) {
            lift.setDeliveryHeight(ITDLift.Basket.HIGH_TELEOP);
        } else {
            lift.setDeliveryHeight((ITDLift.Basket.HIGH_AUTO));
        }
    }
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDDeliveryTracker() {

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

}

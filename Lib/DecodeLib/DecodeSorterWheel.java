package org.firstinspires.ftc.teamcode.Lib.DecodeLib;


import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;

public class DecodeSorterWheel {

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

    private DecodeSorterSlot slot1;
    private DecodeSorterSlot slot2;
    private DecodeSorterSlot slot3;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public DecodeSorterWheel() {
        slot1 = new DecodeSorterSlot(0);
        slot2 = new DecodeSorterSlot(120);
        slot3 = new DecodeSorterSlot(240);
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

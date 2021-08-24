package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class BoundingBox {

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

    private double x = 0;
    private double negX = 0;
    private double y = 0;
    private double negY = 0;



    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getX() {
        return x;
    }

    public double getNegX() {
        return negX;
    }

    public double getY() {
        return y;
    }

    public double getNegY() {
        return negY;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public BoundingBox(double x, double negX, double y, double negY) {
        this.x = x;
        this.negX = negX;
        this.y = y;
        this.negY = negY;
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
    public BoundingBox offsetInward(double xOffset, double negXOffset, double yOffset, double negYOffset){
        xOffset = Math.abs(xOffset);
        negXOffset = Math.abs(negXOffset);
        yOffset = Math.abs(yOffset);
        negYOffset = Math.abs(negYOffset);
        double x = this.x - xOffset;
        double negX = this.negX + negXOffset;
        double y = this.y - yOffset;
        double negY = this.negY + negYOffset;

        return new BoundingBox(x, negX, y, negY);
    }
}

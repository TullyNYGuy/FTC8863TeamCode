package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest.vision;


import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorGB;

public class MineralVoting {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum MineralPosition {
        LEFT,
        CENTER,
        RIGHT,
    }

    public enum LikelyPosition {
        LEFT,
        CENTER,
        RIGHT,
        LEFT_CENTER,
        LEFT_RIGHT,
        CENTER_RIGHT,
        TIE
    }
    public enum MineralType {
        GOLD,
        SILVER
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private int leftPositionCount = 0;
    private int centerPositionCount = 0;
    private int rightPositionCount = 0;
    private int numberVotes = 0;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public int getNumberVotes() {
        return numberVotes;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public MineralVoting() {

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

    public void addMineralVote(MineralType mineralType, MineralPosition mineralPosition) {
        numberVotes ++;
        switch(mineralPosition) {
            case LEFT:
                if (mineralType == MineralType.GOLD) {
                    leftPositionCount ++;
                } else {
                    leftPositionCount --;
                }
                break;
            case CENTER:
                if (mineralType == MineralType.GOLD) {
                    leftPositionCount ++;
                } else {
                    leftPositionCount --;
                }
                break;
            case RIGHT:
                if (mineralType == MineralType.GOLD) {
                    leftPositionCount ++;
                } else {
                    leftPositionCount --;
                }
                break;
        }
    }

    public LikelyPosition getMostLikelyGoldPosition() {
        LikelyPosition mostLikelyMineralPosition = LikelyPosition.TIE;

        if (leftPositionCount > centerPositionCount && leftPositionCount > rightPositionCount) {
            mostLikelyMineralPosition = LikelyPosition.LEFT;
        }
        if (centerPositionCount > leftPositionCount && centerPositionCount > rightPositionCount) {
            mostLikelyMineralPosition = LikelyPosition.CENTER;
        }
        if (rightPositionCount > leftPositionCount && rightPositionCount > centerPositionCount) {
            mostLikelyMineralPosition = LikelyPosition.RIGHT;
        }
        if (rightPositionCount == centerPositionCount && rightPositionCount == leftPositionCount) {
            mostLikelyMineralPosition = LikelyPosition.TIE;
        }
        if (leftPositionCount == centerPositionCount && leftPositionCount > leftPositionCount) {
            mostLikelyMineralPosition = LikelyPosition.LEFT_CENTER;
        }
        if (leftPositionCount == rightPositionCount && leftPositionCount > centerPositionCount) {
            mostLikelyMineralPosition = LikelyPosition.LEFT_RIGHT;
        }
        if (centerPositionCount == rightPositionCount && centerPositionCount > leftPositionCount) {
            mostLikelyMineralPosition = LikelyPosition.CENTER_RIGHT;
        }
        return mostLikelyMineralPosition;
    }
}

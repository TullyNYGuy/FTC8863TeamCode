package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest.vision;


import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorGB;

public class MineralVoting {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * Specifies the position of a detected mineral
     */
    public enum MineralPosition {
        LEFT,
        CENTER,
        RIGHT,
    }

    /**
     * Specifies the most likely position of the gold mineral after the voting has taken place.
     */
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

    /**
     * Once a set of objects has been detected and the location determined for that snapshot, this
     * method should be called for each object detected. The method will process those detections into
     * a vote. The votes from different detection snapshots are added up to determine the most likely
     * location for the gold mineral. This a way of dealing with data that is is not always reliable
     * for each snapshot, but over many passes is reliable.
     * @param mineralType
     * @param mineralPosition
     */
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

    /**
     * Using the votes cast by many detections, determine the most likely location of the gold
     * mineral. In the case of a tie, there are special returns that indicate a tie occurred. It is
     * up to the calling code to break the tie.
     * @return
     */
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

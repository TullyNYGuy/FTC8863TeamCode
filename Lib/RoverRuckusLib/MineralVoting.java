package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import android.provider.ContactsContract;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorGB;

import java.util.List;

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
    private DataLogging dataLogging;
    private boolean dataLoggingOn = true;
    private int recognitionCount = 0;

    private List<Recognition> updatedRecognitionsMaster = null;
    private List<Recognition> updatedRecognitionsFiltered = null;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public int getNumberVotes() {
        return numberVotes;
    }

    public int getLeftPositionCount() {
        return leftPositionCount;
    }

    public int getCenterPositionCount() {
        return centerPositionCount;
    }

    public int getRightPositionCount() {
        return rightPositionCount;
    }

    public boolean isDataLoggingOn() {
        return dataLoggingOn;
    }

    public void enableDataLogging() {
        if (dataLogging != null) {
            this.dataLoggingOn = true;
        } else {
            this.dataLoggingOn = false;
        }
    }

    public void disableDataLogging() {
        this.dataLoggingOn = false;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public MineralVoting(DataLogging dataLogging) {
        this.dataLogging = dataLogging;
        enableDataLogging();
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
     * adds one to recognition count every time that we call this method.
     */
    public void updateRecognitionCount() {
        recognitionCount++;
    }

    /**
     * Once a set of objects has been detected and the location determined for that snapshot, this
     * method should be called for each object detected. The method will process those detections into
     * a vote. The votes from different detection snapshots are added up to determine the most likely
     * location for the gold mineral. This a way of dealing with data that is is not always reliable
     * for each snapshot, but over many passes is reliable.
     * A gold mineral in a position adds one vote for that position. A silver mineral in a position
     * subtracts one vote for that position. In a perfect scenario, a gold mineral will always be
     * detected in the same position and will add a vote for that position every time. But things
     * go wrong in the object detection so this method is a way to average out the detection information
     * across multiple detections passes and finding the most likely location of the gold mineral.
     * Example:
     * pass 1 - gold left, silver center, silver right = +1, -1, -1
     * pass 2 - gold left, nothing center, silver right = +2, -1, -2
     * pass 3 - silver left, gold center, nothing right = +1, 0, -2
     * After 3 passes, most likely location = gold on left
     *
     * @param mineralType
     * @param mineralPosition
     */
    public void addMineralVote(MineralType mineralType, MineralPosition mineralPosition) {
        numberVotes++;
        if (dataLoggingOn = true) {
            dataLogging.logData("mineral type = " + mineralType.toString() + " mineral position " + mineralPosition.toString());
        }
        switch (mineralPosition) {
            case LEFT:
                if (mineralType == MineralType.GOLD) {
                    leftPositionCount++;
                } else {
                    leftPositionCount--;
                }
                break;
            case CENTER:
                if (mineralType == MineralType.GOLD) {
                    centerPositionCount++;
                } else {
                    centerPositionCount--;
                }
                break;
            case RIGHT:
                if (mineralType == MineralType.GOLD) {
                    rightPositionCount++;
                } else {
                    rightPositionCount--;
                }
                break;
        }
    }

    /**
     * Using the votes cast by many detections, determine the most likely location of the gold
     * mineral. In the case of a tie, there are special returns that indicate a tie occurred. It is
     * up to the calling code to break the tie.
     *
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
        if (leftPositionCount == centerPositionCount && leftPositionCount > rightPositionCount) {
            mostLikelyMineralPosition = LikelyPosition.LEFT_CENTER;
        }
        if (leftPositionCount == rightPositionCount && leftPositionCount > centerPositionCount) {
            mostLikelyMineralPosition = LikelyPosition.LEFT_RIGHT;
        }
        if (centerPositionCount == rightPositionCount && centerPositionCount > leftPositionCount) {
            mostLikelyMineralPosition = LikelyPosition.CENTER_RIGHT;
        }
        dataLogging.logData("Voting Results = ", mostLikelyMineralPosition.toString());
        dataLogging.logData("Right votes", Integer.toString(getRightPositionCount()));
        dataLogging.logData("Center votes", Integer.toString(getCenterPositionCount()));
        dataLogging.logData("Left votes", Integer.toString(getLeftPositionCount()));
        dataLogging.logData("Number of Recognitions", Integer.toString(recognitionCount));
        return mostLikelyMineralPosition;
    }
}

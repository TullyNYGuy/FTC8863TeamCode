package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.DcMotor;

public interface DcMotor8863Interface {
    void setBaseEncoderCount(int baseEncoderCount);

    int getBaseEncoderCount();

    void setTargetPosition(int virtualTargetEncoderCount);

    int getCurrentPosition();

    void setMotorType(DcMotor8863.MotorType motorType);

    int getCountsPerRev();

    void setMovementPerRev(double MovementPerRev);

    void setTargetEncoderTolerance(int targetEncoderTolerance);

    void setFinishBehavior(DcMotor8863.FinishBehavior finishBehavior);

    DcMotor8863.MotorState getCurrentMotorState();

    // tested
    double getPositionInTermsOfAttachment();

    // tested
    boolean moveToPosition(double power, double targetPosition, DcMotor8863.FinishBehavior afterCompletion);

    // tested
    boolean rotateNumberOfRevolutions(double power, double revs, DcMotor8863.FinishBehavior afterCompletion);

    /**
     * Implement a state machine to track the motor. See enum declarations for a description of each
     * state.
     *
     * @return the state that the motor is in currently
     */
    DcMotor8863.MotorState update();

    boolean isMovementComplete();

    //*********************************************************************************************
    //          Wrapper Methods
    //*********************************************************************************************
    void setMode(DcMotor.RunMode mode);

    void setPower(double power);

    void setDirection(DcMotor.Direction direction);
}

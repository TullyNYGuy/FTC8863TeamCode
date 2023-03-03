package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import java.util.LinkedList;

/**
 * A FIFO queue that contains a limited number of objects. FIFO means
 * input in this order - 1, 2, 3, 4 (ie 1 is first in, or oldest data)
 * get output in this order = 1, 2, 3, 4 (ie 1 is first out)
 * @param <E>
 */
public class LimitedQueue<E> extends LinkedList<E> {

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

    private int maxNumberOfItems;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public LimitedQueue(int maxNumberOfItems) {
        this.maxNumberOfItems = maxNumberOfItems;
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
    @Override
    public boolean add(E item) {
        super.add(item);
        while (size() > maxNumberOfItems) { super.remove(); }
        return true;
    }

    // To iterate the queue:
//    Iterator iterator = queue.iterator();
//
//        while (iterator.hasNext()) {
//        System.out.print(iterator.next() + " ");
//    }

}

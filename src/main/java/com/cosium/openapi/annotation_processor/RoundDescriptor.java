package com.cosium.openapi.annotation_processor;

/**
 * Created on 18/08/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class RoundDescriptor {

    private final int number;
    private final boolean last;

    public RoundDescriptor(int number, boolean last) {
        this.number = number;
        this.last = last;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isFirst() {
        return number == 1;
    }

}

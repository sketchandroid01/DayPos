package com.daypos.fragments.billhistory;

public abstract class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_CHILDS = 1;

    abstract public int getType();
}
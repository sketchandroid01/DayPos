package com.daypos.fragments.billhistory;

public class HeaderItem extends ListItem {

    private String header_name;

    public String getHeaderName() {
        return header_name;
    }

    public void setHeader(String header) {
        this.header_name = header;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

}
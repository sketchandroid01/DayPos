package com.daypos.container;

public class DrawerData {

    private String title;
    private int icon;
    private String color_code;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getColor_code() {
        return color_code;
    }

    public DrawerData setColor_code(String color_code) {
        this.color_code = color_code;
        return this;
    }
}

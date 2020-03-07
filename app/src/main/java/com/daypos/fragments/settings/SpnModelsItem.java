package com.daypos.fragments.settings;

/**
 * Created by ANDROID on 3/30/2017.
 */
public class SpnModelsItem {

    private String brandName = "Epson";
    private String modelName = "";
    private int modelConstant = 0;

    public SpnModelsItem(String modelName, int modelConstant) {
        this.modelName = modelName;
        this.modelConstant = modelConstant;
    }

    public int getModelConstant() {
        return modelConstant;
    }

    @Override
    public String toString() {
        return modelName;
    }

    public String getBrandName() {
        return brandName;
    }
}

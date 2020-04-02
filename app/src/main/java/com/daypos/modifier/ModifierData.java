package com.daypos.modifier;

import java.io.Serializable;

public class ModifierData implements Serializable {

    private String id;
    private String name;
    private ModifierItemsData modifierItemsData;

    public String getId() {
        return id;
    }

    public ModifierData setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ModifierData setName(String name) {
        this.name = name;
        return this;
    }

    public ModifierItemsData getModifierItemsData() {
        return modifierItemsData;
    }

    public ModifierData setModifierItemsData(ModifierItemsData modifierItemsData) {
        this.modifierItemsData = modifierItemsData;
        return this;
    }
}

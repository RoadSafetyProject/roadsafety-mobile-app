package com.RSMSA.policeApp;

/**
 * Created by Isaiah on 9/2/2014.
 */
public class Model {

    private String description;
    private boolean selected;

    public Model(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

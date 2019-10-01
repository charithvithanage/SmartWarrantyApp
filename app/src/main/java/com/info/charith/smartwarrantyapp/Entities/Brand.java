package com.info.charith.smartwarrantyapp.Entities;

import android.graphics.drawable.Drawable;

public class Brand {
    String name;
    Drawable imageResource;

    public Brand(String name, Drawable imageResource) {
        this.name = name;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImageResource() {
        return imageResource;
    }

    public void setImageResource(Drawable imageResource) {
        this.imageResource = imageResource;
    }
}

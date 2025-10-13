package com.vessel.gsim;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Planet {
    public String name;
    public float x, y;
    public float radius;
    public Sprite surface, clouds; 
    public float surfaceRotation, cloudsRotation;

    public Planet(String name, float radius, float x, float y, float surfaceRotation) {
        this.name = name;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.surfaceRotation = surfaceRotation;
    }
    public Planet(String name, float radius, float x, float y, float surfaceRotation, float cloudsRotation) {
        this.name = name;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.surfaceRotation = surfaceRotation;
        this.cloudsRotation = cloudsRotation;
    }

}

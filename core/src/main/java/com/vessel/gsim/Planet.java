package com.vessel.gsim;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Planet {
    public String name;
    public Sprite surface, clouds;
    public float surfaceRotation, cloudsRotation;

    public double mass;
    public double radius;
    public double x, y;
    public double vx, vy;

    public Array<Vector2> trail = new Array<>();
    public int trailMaxLines = 5000;

    public Planet(String name, double mass, double radius, double x, double y, float surfaceRotation, double vx, double vy) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.surfaceRotation = surfaceRotation;
        this.vx = vx;
        this.vy = vy;
    }
    public Planet(String name, double mass, double radius, double x, double y, float surfaceRotation, float cloudsRotation) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.surfaceRotation = surfaceRotation;
        this.cloudsRotation = cloudsRotation;
    }
}

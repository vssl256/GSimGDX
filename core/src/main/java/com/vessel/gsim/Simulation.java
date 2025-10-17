package com.vessel.gsim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.Gdx.*;

public class Simulation {
    private List<Planet> planets = new ArrayList<>();
    public List<Planet> getPlanets() { return planets; }

    Texture surfaceError = new Texture( "planets/error.png" );
    Texture cloudsError = new Texture( "planets/clouds/error.png" );

    public void addPlanet( String name, double mass, double radius, double x, double y, float surfaceRotation, double vx, double vy ) {
        Planet planet = new Planet( name, mass, radius, x, y, surfaceRotation, vx, vy );
        try {
            planet.surface = new Sprite( new Texture( "planets/" + planet.name + ".png" ) );
        } catch ( GdxRuntimeException e ) {
            planet.surface = new Sprite( surfaceError );
            app.log( "Simulation", "Surface texture not found for " + planet.name );
        }
        planet.surface.setOriginCenter();

        planets.add( planet );
    }
    public void addPlanet( String name, double mass, double radius, double x, double y, float surfaceRotation, float cloudsRotation ) {
        Planet planet = new Planet( name, mass, radius, x, y, surfaceRotation, cloudsRotation );
        try {
            planet.surface = new Sprite( new Texture( "planets/" + planet.name + ".png" ) );
        } catch ( GdxRuntimeException e ) {
            planet.surface = new Sprite( surfaceError );
            app.log( "Simulation", "Surface texture not found for " + planet.name );
        }
        try {
            planet.clouds = new Sprite( new Texture( "planets/clouds/" + planet.name + ".png" ) );
        } catch ( GdxRuntimeException e ) {
            planet.clouds = new Sprite( cloudsError );
            app.log( "Simulation", "Clouds texture not found for " + planet.name );
        }
        planet.surface.setOriginCenter();
        planet.clouds.setOriginCenter();

        planets.add(planet);
    }
}

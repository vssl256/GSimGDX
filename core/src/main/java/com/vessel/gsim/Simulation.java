package com.vessel.gsim;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.GdxRuntimeException;
import static com.badlogic.gdx.Gdx.*;

public class Simulation {
    private List<Planet> planets = new ArrayList<>();
    public List<Planet> getPlanets() { return planets; }

    Texture surfaceError = new Texture( "planets/error.png" );
    Texture cloudsError = new Texture( "planets/clouds/error.png" );
    
    public void addPlanet(String name, float radius, float x, float y, float surfaceRotation) {
        Planet planet = new Planet(name, radius, x, y, surfaceRotation);
        try {
            planet.surface = new Sprite( new Texture( "planets/" + planet.name + ".png" ) );
        } catch ( GdxRuntimeException e ) {
            planet.surface = new Sprite( surfaceError );
            app.log( "Simulation", "Surface texture not found for " + planet.name );
        }
        planet.surface.setOriginCenter();

        planets.add(planet);
    }
    public void addPlanet(String name, float radius, float x, float y, float surfaceRotation, float cloudsRotation) {
        Planet planet = new Planet(name, radius, x, y, surfaceRotation, cloudsRotation);
        try {
            planet.surface = new Sprite( new Texture( "planets/" + planet.name + ".png" ) );
        } catch ( GdxRuntimeException e ) {
            planet.surface = new Sprite( surfaceError );
            app.log( "Simulation", "Surface texture not found for " + planet.name );
        }
        try {
            planet.clouds = new Sprite( new Texture( "planets/clouds/" + planet.name + ".png" ) );
        } catch ( GdxRuntimeException e ) {
            planet.clouds = new Sprite( surfaceError );
            app.log( "Simulation", "Clouds texture not found for " + planet.name );
        }
        planet.surface.setOriginCenter();
        planet.clouds.setOriginCenter();

        planets.add(planet);
    }
}

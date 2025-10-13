package com.vessel.gsim;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.GdxRuntimeException;
import static com.badlogic.gdx.Gdx.*;

public class Simulation {
    List<Planet> planets = new ArrayList<>();

    Texture surfaceError = new Texture( "planets/error.png" );
    Texture cloudsError = new Texture( "planets/clouds/error.png" );

    public void addPlanet(String name, float radius, float x, float y, float surfaceRotation, float cloudsRotation) {
        Planet planet = new Planet(name, radius, x, y, surfaceRotation, cloudsRotation);
        try {
            planet.surface = new Sprite( new Texture( "planets/" + planet.name ) );
        } catch ( GdxRuntimeException e ) {
            planet.surface = new Sprite( surfaceError );
            app.log( "Simulation", "Surface texture not found for " + planet.name );
        }
        try {
            planet.clouds = new Sprite( new Texture( "planets/clouds/" + planet.name ) );
        } catch ( GdxRuntimeException e ) {
            planet.surface = new Sprite( surfaceError );
            app.log( "Simulation", "Clouds texture not found for " + planet.name );
        }
    }
}

package com.vessel.gsim;

import com.badlogic.gdx.utils.Array;

import java.util.List;

import static com.badlogic.gdx.Gdx.*;

public class Physics {
    Simulation simulation;
    List<Planet> planets;

    public Physics( Simulation simulation ) {
        this.simulation = simulation;
        planets = simulation.getPlanets();
    }

    double G = 6.6743e-11f;

    public void step( float STEP ) {
        for ( Planet mainPlanet : planets ) {
            double ax = 0, ay = 0;
            for ( Planet secondaryPlanet : planets ) {
                if ( mainPlanet == secondaryPlanet ) continue;
                double dx = secondaryPlanet.x - mainPlanet.x;
                double dy = secondaryPlanet.y - mainPlanet.y;
                double r = Math.sqrt( dx*dx + dy*dy );
                double F = ( G * mainPlanet.mass * secondaryPlanet.mass / ( r*r ) );
                ax += F * dx / ( r * mainPlanet.mass );
                ay += F * dy / ( r * mainPlanet.mass );
            }
            mainPlanet.vx += ax * STEP;
            mainPlanet.vy += ay * STEP;
            mainPlanet.x += mainPlanet.vx * STEP;
            mainPlanet.y += mainPlanet.vy * STEP;
        }
    }
}

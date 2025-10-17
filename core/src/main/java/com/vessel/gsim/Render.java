package com.vessel.gsim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

import static com.badlogic.gdx.Gdx.*;

public class Render {
    List<Planet> planets;
    Simulation simulation;

    Viewport viewport;
    SpriteBatch batch;
    Sprite background;
    ShapeRenderer shapeRenderer;

    float RENDER_SCALE = 1e6f;

    int targetFPS = 60;

    public Render(Simulation simulation, Viewport viewport, SpriteBatch batch, ShapeRenderer shapeRenderer) {
        background = new Sprite( new Texture( "milkyWay4k.png" ) );
        background.getTexture().setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.simulation = simulation;
        planets = simulation.getPlanets();
        this.viewport = viewport;
    }

    public void drawBackground() {
        float bgScale = 2f;
        float bgWidth = graphics.getWidth() * bgScale;
        float bgHeight = graphics.getHeight() * bgScale;
        float bgX = -( bgWidth - graphics.getWidth() ) / 2;
        float bgY = -( bgHeight - graphics.getHeight() ) / 2;
        batch.begin();
        batch.draw( background, bgX, bgY, bgWidth, bgHeight );
        batch.end();
    }

    public void drawPlanets( float STEP ) {
        float deltaTime = graphics.getDeltaTime();

        batch.begin();
        for ( Planet planet : planets ) {
            Sprite surface = planet.surface;
            Sprite clouds = planet.clouds;

            float radius = ( float )( planet.radius / RENDER_SCALE );

            float x = ( float )( planet.x / RENDER_SCALE );
            float y = ( float )( planet.y / RENDER_SCALE );

            float drawX = x - radius / 2;
            float drawY = y - radius / 2;



            surface.setPosition( drawX, drawY );
            surface.rotate( planet.surfaceRotation * STEP );
            surface.setSize( radius, radius );
            surface.setOriginCenter();
            surface.draw( batch );

            if ( clouds != null ) {
                clouds.setPosition( drawX, drawY );
                clouds.rotate( planet.cloudsRotation * STEP );
                clouds.setSize( radius, radius );
                clouds.setOriginCenter();
                clouds.draw( batch );
            }

            batch.end();
            addTrail( planet, x, y );
            drawTrail( planet );
            batch.begin();
        }
        batch.end();
    }

    private void addTrail(Planet planet, float x, float y) {
        planet.trail.add( new Vector2( x, y ) );
        if ( planet.trail.size > planet.trailMaxLines ) {
            planet.trail.removeIndex( 0 );
        }
    }

    private void drawTrail( Planet planet ) {
        shapeRenderer.begin( ShapeRenderer.ShapeType.Line );
        for ( int i = 1; i < planet.trail.size; i++ ) {
            Vector2 point1 = planet.trail.get( i - 1 );
            Vector2 point2 = planet.trail.get( i );
            shapeRenderer.line( point1.x, point1.y, point2.x, point2.y );
        }
        shapeRenderer.end();
    }
}

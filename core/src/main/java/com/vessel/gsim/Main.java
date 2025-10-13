package com.vessel.gsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static java.lang.System.*;
import static com.badlogic.gdx.Gdx.*;

public class Main extends ApplicationAdapter {
    final int WINDOW_WIDTH = 640;
    final int WINDOW_HEIGHT = 360;

    final float WORLD_WIDTH = 1366;
    final float WORLD_HEIGHT = 768;

    final String PLANET_PATH = "earth250.png";

    OrthographicCamera camera;
    Viewport viewport;

    boolean isFullscreen = false;
    boolean fWasPressed = false;

    Sprite earthSprite;
    Sprite earthCloudsSprite;
    Sprite earthAtmosphereSprite;
    SpriteBatch batch;

    long frame = 0;

    @Override
    public void create() {
        graphics.setVSync(false);
        graphics.setForegroundFPS(Integer.MAX_VALUE);

        earthSprite = new Sprite( new Texture( "planets/" + PLANET_PATH ) );
        earthCloudsSprite = new Sprite( new Texture( "planets/clouds/" + PLANET_PATH ) );
        earthAtmosphereSprite = new Sprite( new Texture( "planets/atmosphere/" + PLANET_PATH ) );
        earthSprite.setOriginCenter();
        earthCloudsSprite.setOriginCenter();

        batch = new SpriteBatch();
        graphics.setWindowedMode( WINDOW_WIDTH, WINDOW_HEIGHT );
        
        inputHandler();

        camera = new OrthographicCamera();
        viewport = new FitViewport( WORLD_WIDTH, WORLD_HEIGHT, camera );
    }

    public void inputHandler() {
        input.setInputProcessor( new InputAdapter() {
            @Override
            public boolean keyDown( int keycode ) {
                if ( keycode == Keys.F ) {
                    if ( !isFullscreen ) graphics.setFullscreenMode( graphics.getDisplayMode() );
                    else graphics.setWindowedMode( 640, 360 );
                    isFullscreen = !isFullscreen;
                    return true;
                }
                if ( keycode == Keys.ESCAPE ) {
                    app.exit();
                }
            return false;
            }

            @Override
            public boolean scrolled( float scrollX, float scrollY ) {
                camera.zoom += scrollY * 0.1;
                camera.zoom = MathUtils.clamp( camera.zoom, 0.1f, 50 );
                camera.update();
                return true;
            } 
        });
    }

    @Override
	public void resize ( int width, int height ) {
        viewport.update( width, height, true );
    }

    @Override
    public void render() {
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        float deltaTime = graphics.getDeltaTime();

        float screenCX = viewport.getWorldWidth() / 2;
        float screenCY = viewport.getWorldHeight() / 2;
        float earthX = screenCX - earthSprite.getWidth() / 2;
        float earthY = screenCY - earthSprite.getHeight() / 2;

        //x = input.getX();
        //y = input.getY();
        //Vector2 mouse = new Vector2( x, y );
        //viewport.unproject( mouse );

        earthSprite.setPosition( earthX, earthY );
        earthCloudsSprite.setPosition( earthX, earthY );
        earthAtmosphereSprite.setPosition( earthX, earthY );

        batch.setProjectionMatrix( camera.combined );
        batch.begin();
        earthSprite.draw( batch );
        earthCloudsSprite.draw( batch );
        earthAtmosphereSprite.draw( batch );
        batch.end();

        cameraMov();

        earthSprite.rotate( 0.02f * deltaTime * 60 );
        earthCloudsSprite.rotate( 0.1f * deltaTime * 60 );
        if ( frame++ % 30 == 0 ) out.println( graphics.getFramesPerSecond() + " FPS" );
    }

    float camSpeed;
    public void cameraMov() {
        camSpeed = 15 * graphics.getDeltaTime() * 60;
        if ( input.isKeyPressed( Keys.W ) ) camera.translate( 0, camSpeed );
        if ( input.isKeyPressed( Keys.A ) ) camera.translate( -camSpeed, 0 );
        if ( input.isKeyPressed( Keys.S ) ) camera.translate( 0, -camSpeed );
        if ( input.isKeyPressed( Keys.D ) ) camera.translate( camSpeed, 0 );
        camera.update();
    }
}

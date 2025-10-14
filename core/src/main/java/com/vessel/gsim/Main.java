package com.vessel.gsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static java.lang.System.*;

import java.util.List;

import static com.badlogic.gdx.Gdx.*;

public class Main extends ApplicationAdapter {
    Simulation simulation;

    List<Planet> planets;

    final int WINDOW_WIDTH = 640;
    final int WINDOW_HEIGHT = 360;

    final float WORLD_WIDTH = 1366;
    final float WORLD_HEIGHT = 768;

    OrthographicCamera camera;
    OrthographicCamera staticCamera;
    OrthographicCamera worldCamera;
    Viewport viewport;

    boolean isFullscreen = false;
    boolean fWasPressed = false;

    Sprite background;

    SpriteBatch batch;

    long frame = 0;
    int targetFPS = 60;
    
    @Override
    public void create() {
        simulation = new Simulation();

        graphics.setVSync(false );
        graphics.setForegroundFPS( Integer.MAX_VALUE );

        background = new Sprite( new Texture( "milkyway4k.png" ) );
        background.getTexture().setFilter( TextureFilter.Linear, TextureFilter.Linear );

        batch = new SpriteBatch();
        graphics.setWindowedMode( WINDOW_WIDTH, WINDOW_HEIGHT );
        
        inputHandler();

        camera = new OrthographicCamera();
        staticCamera = new OrthographicCamera();
        staticCamera.setToOrtho( false, graphics.getWidth(), graphics.getHeight() );
        worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho( false, WORLD_WIDTH, WORLD_HEIGHT );
        viewport = new FitViewport( WORLD_WIDTH, WORLD_HEIGHT, camera );
        createPlanets();
        planets = simulation.getPlanets();
    }

    public void createPlanets() {
        simulation.addPlanet( "Sun", 1000, 1000, 0, 0.02f / 25f );
        simulation.addPlanet( "Earth", 10, 0, 0, 0.02f, 0.1f );
        simulation.addPlanet( "Moon", 2.5f, -400, 0, 0.2f ); 
    }
    float lastMouseX = 0;
    float lastMouseY = 0;
    boolean isRMB = false;
    public void inputHandler() {
        input.setInputProcessor( new InputAdapter() {
            @Override
            public boolean touchDown( int screenX, int screenY, int pointer, int button ) {
                if ( button == Buttons.RIGHT ) {
                    lastMouseX = screenX;
                    lastMouseY = screenY;
                    isRMB = true;
                    return true;
                }
                return false;
            }
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if ( button == Buttons.RIGHT ) isRMB = false;
                return true;
            }
            @Override
            public boolean touchDragged( int screenX, int screenY, int pointer ) {
                if ( isRMB ) {
                    float deltaX = ( screenX - lastMouseX ) * camera.zoom * ( WORLD_WIDTH / graphics.getWidth() );
                    float deltaY = ( screenY - lastMouseY ) * camera.zoom * ( WORLD_HEIGHT / graphics.getHeight() );
                    camera.translate( -deltaX , deltaY );
                    camera.update();

                    lastMouseX = screenX;
                    lastMouseY = screenY;
                }
                return true;
            }

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
                camera.zoom += scrollY * 0.1 * camera.zoom;
                camera.zoom = MathUtils.clamp( camera.zoom, 1e-5f, 500 );
                out.println(camera.zoom);
                camera.update();
                return true;
            } 
        });
    }

    @Override
	public void resize ( int width, int height ) {
        viewport.update( width, height, false );
        staticCamera.setToOrtho( false, width, height );
    }
    float deltaTime;
    @Override
    public void render() {
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        deltaTime = graphics.getDeltaTime();

        //x = input.getX();
        //y = input.getY();
        //Vector2 mouse = new Vector2( x, y );
        //viewport.unproject( mouse );

        float bgScale = 2f;
        float bgWidth = graphics.getWidth() * bgScale;
        float bgHeight = graphics.getHeight() * bgScale;
        float bgX = -( bgWidth - graphics.getWidth() ) / 2;
        float bgY = -( bgHeight - graphics.getHeight() ) / 2;
        deltaTime = graphics.getDeltaTime();

        batch.setProjectionMatrix( staticCamera.combined );
        batch.begin();
        batch.draw( background, bgX, bgY, bgWidth, bgHeight );
        batch.end();

        
        
        drawPlanets();
        

        cameraMov();

        //if ( frame++ % 30 == 0 ) out.println( graphics.getFramesPerSecond() + " FPS" );
    }

    public void drawPlanets() {
        batch.setProjectionMatrix( camera.combined );
        batch.begin();
        for ( Planet planet : planets ) {
            Sprite surface = planet.surface;
            Sprite clouds = planet.clouds;

            float radius = planet.radius;

            float drawX = planet.x - viewport.getWorldWidth() / 2;
            float drawY = planet.y - viewport.getWorldHeight() / 2;
            
            surface.setPosition( drawX, drawY );
            surface.rotate( planet.surfaceRotation * deltaTime * targetFPS );
            surface.setSize( radius, radius );
            surface.setOriginCenter();
            surface.draw( batch );

            if ( clouds != null ) {
                clouds.setPosition( drawX, drawY );
                clouds.rotate( planet.cloudsRotation * deltaTime * targetFPS );
                clouds.setSize( radius, radius );
                clouds.setOriginCenter();
                clouds.draw( batch );
            }
        }
        batch.end();
    }

    float camSpeed;
    float parallaxSpeed;
    public void cameraMov() {
        camSpeed = 15 * graphics.getDeltaTime() * 60;
        parallaxSpeed = 0.1f * graphics.getDeltaTime() * 60;
        if ( input.isKeyPressed( Keys.W ) ) { 
            camera.translate( 0, camSpeed );
            staticCamera.translate( 0, parallaxSpeed );
        }
        if ( input.isKeyPressed( Keys.A ) ) { 
            camera.translate( -camSpeed, 0 );
            staticCamera.translate( -parallaxSpeed, 0 );
        }
        if ( input.isKeyPressed( Keys.S ) ) { 
            camera.translate( 0, -camSpeed );
            staticCamera.translate( 0, -parallaxSpeed );
        }
        if ( input.isKeyPressed( Keys.D ) ) { 
            camera.translate( camSpeed, 0 );
            staticCamera.translate( parallaxSpeed, 0 );
        }
        camera.update();
        staticCamera.update();
    }
}

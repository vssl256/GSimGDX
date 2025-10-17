package com.vessel.gsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

import static com.badlogic.gdx.Gdx.*;
import static java.lang.System.*;

public class Controller extends ApplicationAdapter {
    Simulation simulation;
    Render render;
    Physics physics;
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

    SpriteBatch batch;
    ShapeRenderer shapeRenderer;

    boolean following = false;

    @Override
    public void create() {
        camera = new OrthographicCamera();

        staticCamera = new OrthographicCamera();
        staticCamera.setToOrtho( false, graphics.getWidth(), graphics.getHeight() );

        worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho( false, WORLD_WIDTH, WORLD_HEIGHT );

        viewport = new FitViewport( WORLD_WIDTH, WORLD_HEIGHT, camera );

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        simulation = new Simulation();
        render = new Render( simulation, viewport, batch, shapeRenderer );
        physics = new Physics( simulation );

        graphics.setVSync( true );
        graphics.setForegroundFPS( Integer.MAX_VALUE );

        graphics.setWindowedMode( WINDOW_WIDTH, WINDOW_HEIGHT );

        inputHandler();

        createPlanets();
        planets = simulation.getPlanets();
        planets.get( 1 ).surface.rotate90( true );
    }

    public void createPlanets() {
        //simulation.addPlanet( "Sun", 1e16f, 1000e6f, 1000e6f, 250e6f, 0.02f / 25f );
        simulation.addPlanet( "Earth", 5.97e24, 6.371e6, 0, 0, 0.02f, 0.1f );
        simulation.addPlanet( "Moon", 7.34e22, 1.737e6, 3.844e8, 0, 0.0008f, 0, 1022 );
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
                switch ( keycode ) {
                    case ( Keys.F ): {
                        if ( !isFullscreen ) graphics.setFullscreenMode( graphics.getDisplayMode() );
                        else graphics.setWindowedMode( 640, 360 );
                        isFullscreen = !isFullscreen;
                        break;
                    }
                    case ( Keys.ESCAPE ): {
                        app.exit();
                        break;
                    }
                    case ( Keys.C ): {
                        following = !following;
                        break;
                    }
                    case ( Keys.EQUALS ): {
                        timeFactor *= 2;
                        out.println(timeFactor);
                        break;
                    }
                    case ( Keys.MINUS ): {
                        timeFactor *= 0.5f;
                        out.println(timeFactor);
                        break;
                    }
                }
                return false;
            }

            @Override
            public boolean scrolled( float scrollX, float scrollY ) {
                camera.zoom += scrollY * 0.1f * camera.zoom;
                camera.zoom = MathUtils.clamp( camera.zoom, 1e-5f, 500 );
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
    float accumulatedTime = 0f;
    float STEP = 60f;
    float timeFactor = 1f;
    @Override
    public void render() {
        deltaTime = graphics.getDeltaTime() * timeFactor;

        gl.glClearColor(0, 0, 0, 1);
        gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        accumulatedTime += deltaTime;


        batch.setProjectionMatrix( staticCamera.combined );
        render.drawBackground();

        while ( accumulatedTime >= STEP ) {
            physics.step( STEP );
            accumulatedTime -= STEP;

        }
        follow();
        batch.setProjectionMatrix( camera.combined );
        shapeRenderer.setProjectionMatrix( camera.combined );
        render.drawPlanets( deltaTime );



        cameraMov();

        //out.println( graphics.getFramesPerSecond() + " FPS" );
    }

    void follow() {
        planets = simulation.getPlanets();
        float cx = (float) ((planets.get(0).mass * planets.get(0).x + planets.get(1).mass * planets.get(1).x)
            / (planets.get(0).mass + planets.get(1).mass)) /1e6f;
        float cy = (float) ((planets.get(0).mass * planets.get(0).y + planets.get(1).mass * planets.get(1).y)
            / (planets.get(0).mass + planets.get(1).mass)) /1e6f;
        if (following) camera.position.set( (float)planets.get( 1 ).x / 1e6f, (float)planets.get( 1 ).y / 1e6f, 0 );
        camera.update();
    }

    float camSpeed;
    float parallaxSpeed;
    public void cameraMov() {
        camSpeed = 15 * deltaTime / timeFactor * 60;
        parallaxSpeed = 0.1f * deltaTime / timeFactor * 60;
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

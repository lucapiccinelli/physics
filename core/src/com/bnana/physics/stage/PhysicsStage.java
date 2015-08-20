package com.bnana.physics.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.bnana.physics.Const;

/**
 * Created by luca.piccinelli on 20/08/2015.
 */
public class PhysicsStage extends Stage{
    private final float TIME_STEP = 1 / 300f;
    private final Box2DDebugRenderer renderer;
    SpriteBatch batch;
    OrthographicCamera camera;
    private World world;
    private float accumulator;
    private Body body;

    public PhysicsStage(){
        accumulator = 0;

        batch = new SpriteBatch();

        createWorld();
        createElement();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 40, 26);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();

        renderer = new Box2DDebugRenderer();

        Gdx.input.setInputProcessor(this);
    }

    private void createElement() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(5, 5);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 1);
        body.createFixture(shape, 0.1f);
        //body.setLinearVelocity(2, 5);

        shape.dispose();
    }

    private void createWorld() {
        world = new World(new Vector2(0, 0f), true);
    }

    @Override
    public boolean keyDown(int keycode){
        if(keycode == Input.Keys.W){
            body.applyForce(new Vector2(0, 2f), body.getWorldCenter(), true);
        }

        return true;
    }

    @Override
    public void draw(){
        super.draw();
        renderer.render(world, camera.combined);
    }

    @Override
    public void act(float delta){
        super.act(delta);

        accumulator += delta;

        while (accumulator >= delta){
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
    }
}

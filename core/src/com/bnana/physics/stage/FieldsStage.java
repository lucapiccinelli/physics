package com.bnana.physics.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by luca.piccinelli on 20/08/2015.
 */
public class FieldsStage extends Stage{
    private final float TIME_STEP = 1 / 300f;
    private final Box2DDebugRenderer renderer;
    private final Body body3;
    SpriteBatch batch;
    OrthographicCamera camera;
    private World world;
    private float accumulator;
    private Body body1;
    private final Body body2;

    public FieldsStage(){
        accumulator = 0;

        batch = new SpriteBatch();

        createWorld();
        body1 = createElement(5, 5);
        body2 = createElement(10, 10);
        body3 = createElement(8, 12, 2f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 40, 26);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();

        renderer = new Box2DDebugRenderer();

        Gdx.input.setInputProcessor(this);
    }

    private Body createElement(int x, int y){
        return createElement(x, y, 1);
    }

    private Body createElement(int x, int y, float mass){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(1);
        body.createFixture(shape, mass);
        //body.setLinearVelocity(2, 5);

        shape.dispose();

        return body;
    }

    private void attract(Body b1, Body b2){
        Vector2 p1 = b1.getWorldCenter();
        Vector2 p2 = b2.getWorldCenter();
        float dist = p2.dst(p1);

        Vector2 force = p1.sub(p2).scl(1 / dist).scl(b1.getMass() * 10);
        b2.applyForceToCenter(force, true);
    }

    private void createWorld() {
        world = new World(new Vector2(0, 0f), true);
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

        attract(body1, body2);
        attract(body3, body2);
        while (accumulator >= delta){
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
    }
}

package com.bnana.physics.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.io.Console;

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

        body.applyLinearImpulse(new Vector2(1f, 1f), body.getWorldCenter(), true);
    }

    private void createElement() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(5, 5);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 1.25f);
        body.createFixture(shape, 1);
        //body.setLinearVelocity(2, 5);

        shape.dispose();
    }

    private void createWorld() {
        world = new World(new Vector2(0, 0f), true);
    }

    @Override
    public boolean keyDown(int keycode){
        if(keycode == Input.Keys.W){
            float direction = Math.signum(((int) (body.getAngle() * MathUtils.radiansToDegrees) % 360) + 180);
            System.out.println(direction);
            body.applyLinearImpulse(getForwardVelocity(body, direction).nor().scl(10), body.getWorldCenter(), true);
        }else if (keycode == Input.Keys.A){
            body.applyTorque(1000f, true);
        }else if (keycode == Input.Keys.D){
            body.applyTorque(-1000f , true);
        }

        return true;
    }

    private Vector2 getLateralVelocity(Body _body){
        Vector2 rightNormal = _body.getWorldVector(new Vector2(1, 0));
        Vector2 vel = _body.getLinearVelocity();
        return rightNormal.scl(rightNormal.dot(vel));
    }

    private Vector2 getForwardVelocity(Body _body, float direction){
        Vector2 forwardNormal = _body.getWorldVector(new Vector2(0, direction));
        Vector2 vel = _body.getLinearVelocity();
        return forwardNormal.scl(forwardNormal.dot(vel));
    }

    private void updateFriction(Body _body){
        Vector2 impulse = getLateralVelocity(_body).scl(-_body.getMass());
        float max = 0.5f;
        if(impulse.len() > max){
            impulse = impulse.scl(max / impulse.len());
        }
        _body.applyLinearImpulse(impulse, _body.getWorldCenter(), true);

        _body.applyAngularImpulse(0.05f * _body.getInertia() * -_body.getAngularVelocity(), true);

        float direction = Math.signum(((int) (body.getAngle() * MathUtils.radiansToDegrees) % 360) + 180);
        _body.applyForce(getForwardVelocity(body, direction).scl(-2f), _body.getWorldCenter(), true);
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

        updateFriction(body);
        while (accumulator >= delta){
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
    }
}

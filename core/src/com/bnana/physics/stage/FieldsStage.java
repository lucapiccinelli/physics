package com.bnana.physics.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by luca.piccinelli on 20/08/2015.
 */
public class FieldsStage extends Stage{
    private final float TIME_STEP = 1 / 300f;
    private final Box2DDebugRenderer renderer;
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

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 40, 26);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();

        renderer = new Box2DDebugRenderer();

        Gdx.input.setInputProcessor(this);

        createChain();

        /*attract(body1, body2);
        attract(body2, body1);*/
    }



    private void createChain(){
        Body chainStart = createChainElement(BodyDef.BodyType.StaticBody, 15, 15);
        Body chain1 = createChainElement(BodyDef.BodyType.DynamicBody, 15, 20);
        Body chainEnd = createElement(15, 25);

        createJoint(chainStart, chain1);
        createJoint(chain1, chainEnd);

        chainEnd.applyLinearImpulse(new Vector2(5, 0), chainEnd.getWorldCenter(), true);
    }

    private void createJoint(Body a, Body b){
        DistanceJointDef jointDef = new DistanceJointDef();
        Vector2 anchorA = a.getWorldCenter();
        Vector2 anchorB = b.getWorldCenter();

        jointDef.initialize(a, b, anchorA, anchorB);
        jointDef.collideConnected = false;

        Joint joint = world.createJoint(jointDef);
    }

    private Body createChainElement(BodyDef.BodyType bodyType, int x, int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
        body.resetMassData();

        shape.dispose();

        return body;
    }


    private Body createElement(int x, int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(1);
        body.createFixture(shape, 1);

        shape.dispose();

        return body;
    }

    private void attract(Body b1, Body b2){
        Vector2 p1 = b1.getWorldCenter();
        Vector2 p2 = b2.getWorldCenter();
        float dist = p2.dst(p1);
        dist = dist > 2 ? dist : 0;
//        if(dist > 0){
            Vector2 force = p1.sub(p2).scl(1000 / dist);
            b2.applyForceToCenter(force, true);
//        }
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

        while (accumulator >= delta){
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
    }
}

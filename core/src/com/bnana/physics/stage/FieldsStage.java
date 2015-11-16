package com.bnana.physics.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

/**
 * Created by luca.piccinelli on 20/08/2015.
 */
public class FieldsStage extends Stage{
    private final float TIME_STEP = 1 / 300f;
    private final Box2DDebugRenderer renderer;
    private Body body3;
    SpriteBatch batch;
    OrthographicCamera camera;
    private World world;
    private float accumulator;
    private Body body1;
    private final Body body2;
    private final float radius;

    public FieldsStage(){
        accumulator = 0;
        radius = 0.1f;

        batch = new SpriteBatch();

        createWorld();
        body1 = createElement(15, 15);
        body2 = createElement(17, 15);
        //body3 = createElement(8, 12, 2f);

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

    private Vector2 nextTo(Vector2 position, Vector2 positionToSet){
        return positionToSet.set(position.x, position.y + radius * 2);
    }

    private void createChain(){
        Array<Body> chains = new Array<Body>();

        float circleRadius = 1.5f;
        Vector2 offSet = new Vector2(16, 15);
        for (int i = 0; i < 360; i+=20){
            float x = offSet.x +  MathUtils.cosDeg(i) * circleRadius;
            float y = offSet.y + MathUtils.sinDeg(i) * circleRadius;

            chains.add(createChainElement(BodyDef.BodyType.DynamicBody, x, y));
        }

        for (int i = 0; i < chains.size; i++){
            createJoint(chains.get(i), chains.get((i + 1) % chains.size));
        }

        body1.applyLinearImpulse(new Vector2(-1f, -1f), body1.getWorldCenter(), true);
        body2.applyLinearImpulse(new Vector2(1f, 1f), body1.getWorldCenter(), true);

        /*Body chainStart =
        Body chain1 = createChainElement(BodyDef.BodyType.DynamicBody, chainStart.getPosition());
        Body chain2 = createChainElement(BodyDef.BodyType.DynamicBody, chain1.getPosition());
        Body chain3 = createChainElement(BodyDef.BodyType.DynamicBody, chain2.getPosition());
        Body chain4 = createChainElement(BodyDef.BodyType.DynamicBody, chain3.getPosition());
        Body chain5 = createChainElement(BodyDef.BodyType.DynamicBody, chain3.getPosition());
        Body chainEnd = createChainElement(BodyDef.BodyType.DynamicBody, chain4.getPosition());

        createJoint(chainStart, chain1);
        createJoint(chain1, chain2);
        createJoint(chain2, chain3);
        createJoint(chain3, chain4);
        createJoint(chain4, chainEnd);

        chainEnd.applyLinearImpulse(new Vector2(0f, 5f), chainEnd.getWorldCenter(), true);*/
    }

    private void createJoint(Body a, Body b){
        DistanceJointDef jointDef = new DistanceJointDef();
        Vector2 anchorA = a.getWorldCenter();
        Vector2 anchorB = b.getWorldCenter();

        jointDef.initialize(a, b, anchorA, anchorB);
        jointDef.collideConnected = true;
        jointDef.dampingRatio = 0;
        jointDef.frequencyHz = 0.5f;
        world.createJoint(jointDef);

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.maxLength = radius * 15f;
        ropeJointDef.localAnchorA.set(0, 0);
        ropeJointDef.localAnchorB.set(0, 0);
        ropeJointDef.bodyA = a;
        ropeJointDef.bodyB = b;
        world.createJoint(ropeJointDef);
    }

    private Body createChainElement(BodyDef.BodyType bodyType, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = false;

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
        body.resetMassData();

        shape.dispose();

        return body;
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

        //attract(body1, body2);
        //attract(body3, body2);

        while (accumulator >= delta){
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
    }
}

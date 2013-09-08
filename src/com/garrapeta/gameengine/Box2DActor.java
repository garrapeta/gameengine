package com.garrapeta.gameengine;

import java.util.ArrayList;

import android.graphics.Canvas;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * Actor de un mundo físico Box2D
 * 
 * @author GaRRaPeTa
 */
public abstract class Box2DActor<T extends Box2DWorld> extends Actor<T> {

    // --------------------------------------------------- Variables
    /**
     * Cuerpos del actor
     */
    protected ArrayList<Body> mBodies;

    /**
     * Joints del actor
     */
    protected ArrayList<Joint> mJoints;

    // --------------------------------------------------- Constructor

    /**
     * @param world
     * @param worldPos
     *            , posición en el mundo, en unidades del mundo
     */
    public Box2DActor(T world) {
        this(world, 0);
    }

    /**
     * @param world
     * @param worldPos
     *            , posición en el mundo, en unidades del mundo
     */
    public Box2DActor(T world, int zIndex) {
        super(world, zIndex);
    }

    // ------------------------------------------------- métodos propios

    public float getMass() {
        float mass = 0;

        for (int i = 0; i < mBodies.size(); i++) {
            mass += mBodies.get(i).getMass();
        }

        return mass;
    }

    /**
     * Callback invocado cuando el actor empieza a ser contactado por otro actor
     * 
     * @param thisBody
     *            body de este actor que ha contactado
     * @param otherBody
     *            body del otro actor que ha contactado
     * @param contact
     */
    public void onBeginContact(Body thisBody, Box2DActor<T> other, Body otherBody, Contact contact) {
    }

    /**
     * @param bodyA
     * @param actorB
     * @param bodyB
     * @param contact
     * @param oldManifold
     */
    public void onPreSolveContact(Body bodyA, Box2DActor<T> actorB, Body bodyB, Contact contact, Manifold oldManifold) {
    }

    /**
     * @param bodyA
     * @param actorB
     * @param bodyB
     * @param contact
     * @param impulse
     */
    public void onPostSolveContact(Body bodyA, Box2DActor<T> actorB, Body bodyB, Contact contact, ContactImpulse impulse) {
    }

    /**
     * Callback invocado cuando el actor termina de ser contactado por otro
     * actor
     * 
     * @param contact
     * @param thisBody
     *            body de este actor que ha contactado
     * @param otherBody
     *            body del otro actor que ha contactado
     */
    public void onEndContact(Body bodyA, Box2DActor<T> actorB, Body bodyB, Contact contact) {
    }

    // --------------------------------------------------- métodos de Actor

    @Override
    public void draw(Canvas canvas) {
        drawBodiesShapes(canvas);
    }

    public void processFrame(float gameTimeStep) {
    }

    /**
     * Pinta las shapes de este objeto usando los shapedrawers declarados
     * 
     * @param canvas
     */
    public void drawBodiesShapes(Canvas canvas) {
        if (mBodies != null) {
            int size = mBodies.size();
            for (int i = 0; i < size; i++) {
                Body body = mBodies.get(i);
                for (Fixture fixture : body.getFixtureList()) {
                    Vector2 worldPos = body.getWorldCenter();
                    canvas.save();
                    float screenPositionX = getWorld().mViewport.worldToScreenX(worldPos.x);
                    float screenPositionY = getWorld().mViewport.worldToScreenY(worldPos.y);
                    canvas.translate(screenPositionX, screenPositionY);
                    canvas.rotate(-(float) Math.toDegrees(body.getAngle()));
                    ShapeDrawer.draw(canvas, getWorld().mViewport, fixture.getShape());
                    canvas.restore();
                }
            }
        }
    }

    public void addBody(Body body) {
        if (mBodies == null) {
            mBodies = new ArrayList<Body>();
        }

        // si este body pertenecán a otro actor, se le quita como body
        @SuppressWarnings("unchecked")
        Box2DActor<T> oldActor = (Box2DActor<T>) body.getUserData();
        if (oldActor != null) {
            oldActor.removeBody(body);
        }

        // se pone el body en este actor
        mBodies.add(body);
    }

    public void removeBody(Body body) {
        if (mBodies != null) {
            mBodies.remove(body);
        }
        body.setUserData(null);
    }

    public void addJoint(Joint joint) {
        if (mJoints == null) {
            mJoints = new ArrayList<Joint>();
        }
        mJoints.add(joint);
    }

    public void removeJoint(Joint joint) {
        if (mJoints != null) {
            mJoints.remove(joint);
        }
    }

    public ArrayList<Body> getBodies() {
        return mBodies;
    }

    public ArrayList<Shape> getShapes() {
        ArrayList<Shape> shapes = new ArrayList<Shape>();

        int l0 = mBodies.size();
        for (int i = 0; i < l0; i++) {
            Body b = mBodies.get(i);

            ArrayList<Fixture> fixtures = b.getFixtureList();

            int l1 = fixtures.size();
            for (int j = 0; j < l1; j++) {
                shapes.add(fixtures.get(j).getShape());
            }
        }

        return shapes;
    }

    @Override
    void doOnRemovedFromWorld() {
        super.doOnRemovedFromWorld();
        if (mJoints != null) {
            int l = mJoints.size();
            for (int i = l - 1; i >= 0; i--) {
                getWorld().destroyJoint(this, mJoints.get(i));
            }
            mJoints.clear();
            mJoints = null;
        }

        if (mBodies != null) {
            int l = mBodies.size();
            for (int i = l - 1; i >= 0; i--) {
                getWorld().destroyBody(this, mBodies.get(i));
            }
            mBodies.clear();
            mBodies = null;
        }
    }

}

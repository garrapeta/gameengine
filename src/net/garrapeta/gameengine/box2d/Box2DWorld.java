package net.garrapeta.gameengine.box2d;

import java.util.Vector;

import net.garrapeta.gameengine.Actor;
import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.GameWorld;
import android.app.Activity;
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Box2DWorld extends GameWorld implements ContactListener {

    // ----------------------------------------------- Constantes

    /** Nombre del thread del game loop */
    public static final String LOG_SRC = GameWorld.LOG_SRC + ".physics";

    /** Default phisical steps per second */
    private static final float DEFAULT_PHYSICAL_PERIOD = 60f; // (recomendado: 60Hertz)
    // ------------------------------------------------ Variables

    public World box2dWorld;
    private Vector2 gravity;

    /**
     * Cuerpos marcados para eliminaci�n. En el pr�ximo frame estos cuerpos
     * ser�n destru�dos y eliminados del juego.
     */
    private Vector<Body> markedForDestructionBodies;

    /** Target frequency of the of psyhical simulation, in seconds */
    private float mFrequency; //

    /** Target timestep of psyhical simulation, in seconds */
    private float mTimeStep;
    // ---------------------------------------------------- Inicializaci�n
    // est�tica

    static {
        Log.i(LOG_SRC, "Attempting to load gdx native library");
        try {
            System.loadLibrary("gdx");
            Log.i(LOG_SRC, "gdx native library loaded");
        } catch (Throwable t) {
            Log.e(LOG_SRC, "Could not load gdx native library: " + t.toString());
            System.exit(1);
        }

    }

    // -------------------------------------------------------------
    // Constructores

    public Box2DWorld(Activity activity, GameView gameView) {
        super(activity, gameView);

        markedForDestructionBodies = new Vector<Body>();

        // Step 1: Create Physics World Boundaries
        // worldAABB = new AABB();
        // worldAABB.lowerBound.set(new Vector2((float) 0, (float) 0));
        // worldAABB.upperBound.set(new Vector2((float) 100.0, (float) 100.0));

        // Step 2: Create Physics World with Gravity
        gravity = new Vector2((float) 0.0, (float) 0.0);
        boolean doSleep = true;
        box2dWorld = new World(gravity, doSleep);
        box2dWorld.setContactListener(this);

        setPhysicalFrequency(DEFAULT_PHYSICAL_PERIOD);
    }

    public void setPhysicalFrequency(float frequency) {
        mFrequency = frequency;
        mTimeStep = 1f / mFrequency;
    }


    // ------------------------------------------------------ M�todos de
    // GameWorld

    protected synchronized String getDebugString() {

        String actorCount;
        actorCount = String.valueOf(actors.size());

        String bodyCount;
        bodyCount = String.valueOf(box2dWorld.getBodyCount());

        return actorCount + " actors " + bodyCount + " bodies " + super.getDebugString();
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        actor.onAddedToWorld();
    }

    @Override
    protected void onRemoveFromWorld(Actor actor) {
        super.onRemoveFromWorld(actor);
        actor.onRemovedFromWorld();
    }

    @Override
    public void processFrame(float lastFrameLength) {
        doPhysicalStep(lastFrameLength);
    }

    @Override
    public synchronized void preProcessFrame() {
        super.preProcessFrame();
        // Se destruyen actores marcados como muertos
        for (int i = 0; i < markedForDestructionBodies.size(); i++) {
            box2dWorld.destroyBody(markedForDestructionBodies.elementAt(i));
        }
        markedForDestructionBodies.removeAllElements();
    }

    /**
     * Triggers physical simulation
     * 
     * @param time to emulate, in ms
     */
    private void doPhysicalStep(float time) {
        time = time / 1000;

        float step = mTimeStep;
        if (time < step) {
            Log.w(LOG_SRC, "Physical timestep higher than game timestep. Physical simulation can be unestable.");
            step = time;
        }
        while (time > 0) {
            box2dWorld.step(step, 2, 1);
            time -= step;
        }
    }
    
    // M�todos relativos a unidades l�gicas / pantalla

    @Override
    public void dispose() {
        box2dWorld.dispose();
    }

    // ---------------------------------------------------------- M�todos
    // propios

    public Body createBody(Box2DActor actor, PointF worldPos, boolean dynamic) {

        // Create Dynamic Body
        BodyDef bodyDef = new BodyDef();

        bodyDef.position.set(worldPos.x, worldPos.y);

        Body body;
        synchronized (this) {
            body = box2dWorld.createBody(bodyDef);
        }

        body.setUserData(new BodyUserData());
        actor.addBody(body);

        if (dynamic) {
            body.setType(BodyType.DynamicBody);
        }

        return body;

    }

    public void destroyBody(Box2DActor actor, Body body) {
        if (!markedForDestructionBodies.contains(body)) {
            markedForDestructionBodies.add(body);
            actor.removeBody(body);
        }
    }

    public void createJoint(Box2DActor actor, JointDef jointDef) {
        Joint joint;
        synchronized (this) {
            joint = box2dWorld.createJoint(jointDef);
        }
        actor.addJoint(joint);
    }

    public void destroyJoint(Box2DActor actor, Joint joint) {
        synchronized (this) {
            box2dWorld.destroyJoint(joint);
        }
        actor.removeJoint(joint);
    }

    public float getGravityX() {
        synchronized (this) {
            return box2dWorld.getGravity().x;
        }
    }

    public float getGravityY() {
        synchronized (this) {
            return box2dWorld.getGravity().y;
        }
    }

    public void setGravityX(float gx) {
        gravity.x = gx;
        synchronized (this) {
            box2dWorld.setGravity(gravity);
        }
    }

    public void setGravityY(float gy) {
        gravity.y = gy;
        synchronized (this) {
            box2dWorld.setGravity(gravity);
        }
    }

    /**
     * Aplica una fuerza en un punto del mundo que afecta al actor
     * 
     * @param origin
     * @param target
     * @param radius
     * @param force
     */
    public void applyForce(Vector2 origin, Body target, float radius, float force) {
        Vector2 aux = target.getWorldCenter().cpy();
        aux.sub(origin);

        float dist = aux.len();
        float diff = radius - dist;
        if (diff > 0) {
            float factor = diff / radius;
            aux.nor();
            aux.mul(force * factor);
            target.setAwake(true);
            target.applyLinearImpulse(aux, target.getWorldCenter());
        }
    }

    // ----------------------------------------------- M�todos de
    // ContactListener

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        Box2DActor actorA = null;
        Object objectA = bodyA.getUserData();
        if (objectA != null) {
            if (objectA instanceof BodyUserData) {
                actorA = ((BodyUserData) objectA).getActor();
            }
        }

        if (actorA != null) {
            Box2DActor actorB = null;
            Object objectB = bodyB.getUserData();
            if (objectB != null) {
                if (objectB instanceof BodyUserData) {
                    actorB = ((BodyUserData) objectB).getActor();

                    if (actorB != null) {
                        actorA.onBeginContact(bodyA, actorB, bodyB, contact);
                        actorB.onBeginContact(bodyB, actorA, bodyA, contact);
                    }
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        Box2DActor actorA = null;
        Object objectA = bodyA.getUserData();
        if (objectA != null) {
            if (objectA instanceof BodyUserData) {
                actorA = ((BodyUserData) objectA).getActor();
            }
        }

        if (actorA != null) {
            Box2DActor actorB = null;
            Object objectB = bodyB.getUserData();
            if (objectB != null) {
                if (objectB instanceof BodyUserData) {
                    actorB = ((BodyUserData) objectB).getActor();

                    if (actorB != null) {
                        actorA.onEndContact(bodyA, actorB, bodyB, contact);
                        actorB.onEndContact(bodyB, actorA, bodyA, contact);
                    }
                }
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        Box2DActor actorA = null;
        Object objectA = bodyA.getUserData();
        if (objectA != null) {
            if (objectA instanceof BodyUserData) {
                actorA = ((BodyUserData) objectA).getActor();
            }
        }

        if (actorA != null) {
            Box2DActor actorB = null;
            Object objectB = bodyB.getUserData();
            if (objectB != null) {
                if (objectB instanceof BodyUserData) {
                    actorB = ((BodyUserData) objectB).getActor();

                    if (actorB != null) {
                        actorA.onPreSolveContact(bodyA, actorB, bodyB, contact, oldManifold);
                        actorB.onPreSolveContact(bodyB, actorA, bodyA, contact, oldManifold);
                    }
                }
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        Box2DActor actorA = null;
        Object objectA = bodyA.getUserData();
        if (objectA != null) {
            if (objectA instanceof BodyUserData) {
                actorA = ((BodyUserData) objectA).getActor();
            }
        }

        if (actorA != null) {
            Box2DActor actorB = null;
            Object objectB = bodyB.getUserData();
            if (objectB != null) {
                if (objectB instanceof BodyUserData) {
                    actorB = ((BodyUserData) objectB).getActor();

                    if (actorB != null) {
                        actorA.onPostSolveContact(bodyA, actorB, bodyB, contact, impulse);
                        actorB.onPostSolveContact(bodyB, actorA, bodyA, contact, impulse);
                    }
                }
            }
        }
    }

    // -------------------------------------- Clases internas

//    /**
//     * Runnable del hilo de f�sicas
//     * 
//     * @author GaRRaPeTa
//     */
//    class PhysicsThreadRunnable implements Runnable {
//
//        @Override
//        public void run() {
//            Log.i(LOG_SRC, "Physics thread started. Thread: " + Thread.currentThread().getName());
//
//            long prevTimeStamp = System.currentTimeMillis();
//
//            while (running) {
//                if (playing) {
//                    physicalStep(mSpfs);
//                }
//
//                long currentTimeStamp = System.currentTimeMillis();
//                long elapsed = currentTimeStamp - prevTimeStamp;
//                Log.v(LOG_SRC, "Physical simulation frame. Desired: " + mSpfs + " Actual: " + elapsed);
//
//                long diff = (long) (mSpfs - elapsed);
//                if (diff > 0) {
//                    try {
//                        Thread.sleep(diff);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                mLastPhysicStepTime = System.currentTimeMillis() - prevTimeStamp;
//                prevTimeStamp = currentTimeStamp;
//
//                Thread.yield();
//
//            }
//
//            synchronized (physicsThread) {
//                physicsThread.notify();
//            }
//
//            Log.i(LOG_SRC, "Physics thread ended");
//        }
//
//    }

}

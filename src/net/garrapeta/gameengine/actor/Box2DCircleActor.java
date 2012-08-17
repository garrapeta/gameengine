package net.garrapeta.gameengine.actor;

import net.garrapeta.gameengine.Box2DWorld;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;

/**
 * Actor basado en un c�rculo
 * 
 * @author GaRRaPeTa
 */
public class Box2DCircleActor extends Box2DAtomicActor {


    // -------------------------------------------------- Constructor

    /**
     * Constructor
     * 
     * @param world
     * @param worldPos
     *            , posici�n en el mundo, en unidades del mundo
     * @param radius
     *            , radio en unidades del mundo
     * @param dynamic
     */
    public Box2DCircleActor(Box2DWorld world, PointF worldPos, float radius, boolean dynamic) {
        super(world);

        // Create Shape with Properties
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        Body body = world.createBody(this, worldPos, dynamic);
        // Assign shape to Body
        body.createFixture(circleShape, 1.0f);
        circleShape.dispose();

    }

    // ------------------------------------------------------ M�todos propios

}

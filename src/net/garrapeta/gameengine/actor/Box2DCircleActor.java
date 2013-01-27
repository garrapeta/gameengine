package net.garrapeta.gameengine.actor;

import net.garrapeta.gameengine.Box2DWorld;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;

/**
 * Actor basado en un círculo
 * 
 * @author GaRRaPeTa
 */
public class Box2DCircleActor<T extends Box2DWorld> extends Box2DAtomicActor<T> {


    // -------------------------------------------------- Constructor

    /**
     * Constructor
     * 
     * @param world
     * @param worldPos
     *            , posición en el mundo, en unidades del mundo
     * @param radius
     *            , radio en unidades del mundo
     * @param dynamic
     */
    public Box2DCircleActor(T world, PointF worldPos, float radius, boolean dynamic) {
        super(world);

        // Create Shape with Properties
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        Body body = world.createBody(this, worldPos, dynamic);
        // Assign shape to Body
        body.createFixture(circleShape, 1.0f);
        circleShape.dispose();

    }

}

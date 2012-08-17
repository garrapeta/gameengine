package net.garrapeta.gameengine.actor;

import net.garrapeta.gameengine.BodyUserData;
import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.ShapeBasedBodyDrawer;
import net.garrapeta.gameengine.actor.Box2DAtomicActor;
import android.graphics.Color;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;

/**
 * Actor basado en un c�rculo
 * 
 * @author GaRRaPeTa
 */
public class Box2DCircleActor extends Box2DAtomicActor {

    // -------------------------------------------------- Constantes

    private final static int DEFAULT_STROKE_COLOR = Color.argb(0xFF, 0xFF, 0, 0);
    private final static int DEFAULT_FILL_COLOR   = Color.argb(0x40, 0xFF, 0, 0);
    private final static int DEFAULT_LINE_COLOR   = Color.GRAY;

    // ------------------------------------------ Variables est�ticas

    private final static ShapeBasedBodyDrawer circleShapeDrawer;

    // -------------------------------------- Inicializaci�n est�tica

    static {
        circleShapeDrawer = new ShapeBasedBodyDrawer(DEFAULT_STROKE_COLOR, DEFAULT_FILL_COLOR, DEFAULT_LINE_COLOR);
    }

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

        this.strokeColor = DEFAULT_STROKE_COLOR;
        this.fillColor = DEFAULT_FILL_COLOR;
        this.lineColor = DEFAULT_LINE_COLOR;

        // Create Shape with Properties
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        Body body = world.createBody(this, worldPos, dynamic);
        // Assign shape to Body
        body.createFixture(circleShape, 1.0f);
        ((BodyUserData) body.getUserData()).setBodyDrawer(circleShapeDrawer);
        circleShape.dispose();

    }

    // ------------------------------------------------------ M�todos propios

}

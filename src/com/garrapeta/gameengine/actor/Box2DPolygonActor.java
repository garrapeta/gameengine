package com.garrapeta.gameengine.actor;

import com.garrapeta.gameengine.Box2DWorld;
import com.garrapeta.gameengine.Viewport;
import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Box2DPolygonActor<T extends Box2DWorld> extends Box2DAtomicActor<T> {

    // -------------------------------------------------- Constructor

    /**
     * @param world
     * @param mWorldPos
     *            , posición en el mundo, en unidades del mundo
     * @param vertexes
     *            vertices, EN EL SENTIDO CONTRARIO A LAS AGUJAS DEL RELOJ, en
     *            unidades del mundo
     * @param dynamic
     */
    public Box2DPolygonActor(T world) {
        super(world);
    }

    /**
     * @param world
     * @param worldPos
     *            , posición en el mundo, en unidades del mundo
     * @param vertexes
     *            vertices, EN EL SENTIDO CONTRARIO A LAS AGUJAS DEL RELOJ, en
     *            unidades del mundo
     * @param dynamic
     */
    public Box2DPolygonActor(T world, PointF worldPos, PointF[] vertexes, boolean dynamic) {
        this(world);

        // Create Shape with Properties
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(Viewport.pointFToVector2(vertexes));
        Body body = world.createBody(this, worldPos, dynamic);
        // Assign shape to Body
        body.createFixture(polygonShape, 1.0f);
        polygonShape.dispose();
    }

    /**
     * @param world
     * @param worldPos
     *            , posición en el mundo, en unidades del mundo
     * @param halfWidth
     *            en unidades del mundo
     * @param halfHeight
     *            en unidades del mundo
     * @param dynamic
     */
    public Box2DPolygonActor(T world, PointF worldPos, float halfWidth, float halfHeight, boolean dynamic) {
        this(world);

        // Create Shape with Properties
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(halfWidth, halfHeight);
        // Body body = createBody(world, worldPos, dynamic);

        Body body = world.createBody(this, worldPos, dynamic);
        // Assign shape to Body
        body.createFixture(polygonShape, 1.0f);
        polygonShape.dispose();
    }

    /**
     * @param world
     * @param worldPos
     *            , posición en el mundo, en unidades del mundo
     * @param halfWidth
     *            en unidades del mundo
     * @param halfHeight
     *            en unidades del mundo
     * @param angle
     * @param dynamic
     */
    public Box2DPolygonActor(T world, PointF worldPos, float halfWidth, float halfHeight, float angle, boolean dynamic) {
        this(world);

        // Create Shape with Properties
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(halfWidth, halfHeight, new Vector2(0, 0), angle);
        Body body = world.createBody(this, worldPos, dynamic);
        // Assign shape to Body
        body.createFixture(polygonShape, 1.0f);
        polygonShape.dispose();
    }

}

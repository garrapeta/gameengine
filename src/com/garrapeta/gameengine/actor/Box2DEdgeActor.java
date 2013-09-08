package com.garrapeta.gameengine.actor;

import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.garrapeta.gameengine.Box2DWorld;
import com.garrapeta.gameengine.Viewport;

public class Box2DEdgeActor<T extends Box2DWorld> extends Box2DAtomicActor<T> {


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
    public Box2DEdgeActor(T world) {
        super(world);
    }

    /**
     * @param world
     * @param worldPos
     *            , posición en el mundo, en unidades del mundo
     * @param p0
     *            vertice inicial, en unidades del mundo
     * @param p1
     *            vertice final, en unidades del mundo
     * @param dynamic
     */
    public Box2DEdgeActor(T world, PointF worldPos, PointF p0, PointF p1, boolean dynamic) {
        this(world);

        Body body = world.createBody(this, worldPos, dynamic);
        setEdges(body, Viewport.pointFToVector2(p0), Viewport.pointFToVector2(p1) );
    }

    private void setEdges(Body body, Vector2 p0, Vector2 p1) {
        // Create Shape with Properties
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(p0, p1);

        body.createFixture(edgeShape, 1.0f);
        edgeShape.dispose();
    }

}

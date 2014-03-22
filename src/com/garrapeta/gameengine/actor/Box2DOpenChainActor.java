package com.garrapeta.gameengine.actor;

import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.garrapeta.gameengine.Box2DWorld;
import com.garrapeta.gameengine.Viewport;

public class Box2DOpenChainActor<T extends Box2DWorld> extends Box2DAtomicActor<T> {

    /**
     * @param world
     * @param worldPos
     *            , posición en el mundo, en unidades del mundo
     * @param vertexes
     *            , vértices, en unidades del mundo
     * @param dynamic
     */
    public Box2DOpenChainActor(T world, PointF worldPos, PointF[] vertexes, boolean dynamic) {
        super(world);

        Body body = world.createBody(this, worldPos, dynamic);
        // Create Shape with Properties
        ChainShape chainShape = new ChainShape();
        chainShape.createChain(Viewport.pointFToVector2(vertexes));
        body.createFixture(chainShape, 1.0f);
        chainShape.dispose();
    }

}

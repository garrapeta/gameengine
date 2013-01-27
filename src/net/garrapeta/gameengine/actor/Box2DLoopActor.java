package net.garrapeta.gameengine.actor;

import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.Viewport;
import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;

public class Box2DLoopActor<T extends Box2DWorld> extends Box2DAtomicActor<T> {


    /**
     * @param world
     * @param worldPos, posicién en el mundo, en unidades del mundo
     * @param vertexes, vértices, en unidades del mundo
     * @param dynamic
     */
    public Box2DLoopActor(T world, PointF worldPos, PointF[] vertexes, boolean dynamic) {
        super(world);
        
        Body body = world.createBody(this, worldPos, dynamic);
        setEdges(body, Viewport.pointFToVector2(vertexes));
    }
    
    private void setEdges(Body body, Vector2[] vertexes) {
        // Create Shape with Properties
        ChainShape chainShape  = new ChainShape();
        chainShape.createLoop(vertexes);
        body.createFixture(chainShape, 1.0f);
        chainShape.dispose();
    }

}

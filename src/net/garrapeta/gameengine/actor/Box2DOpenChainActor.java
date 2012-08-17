package net.garrapeta.gameengine.actor;

import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.Viewport;
import android.graphics.PointF;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;

public class Box2DOpenChainActor extends Box2DAtomicActor {

     /**
     * @param world
     * @param worldPos, posici�n en el mundo, en unidades del mundo
     * @param vertexes, v�rtices, en unidades del mundo
     * @param dynamic
     */
    public Box2DOpenChainActor(Box2DWorld world, PointF worldPos, PointF[] vertexes, boolean dynamic) {
        super(world);
        
        Body body = world.createBody(this, worldPos, dynamic);
        // Create Shape with Properties
        ChainShape chainShape  = new ChainShape();
        chainShape.createChain(Viewport.pointFToVector2(vertexes));
        body.createFixture(chainShape, 1.0f);
        chainShape.dispose();
    }
    
    
}

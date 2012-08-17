package net.garrapeta.gameengine.test;

import net.garrapeta.gameengine.Box2DActor;
import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.GameMessage;
import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.gameengine.actor.Box2DCircleActor;
import net.garrapeta.gameengine.actor.Box2DEdgeActor;
import net.garrapeta.gameengine.actor.Box2DLoopActor;
import net.garrapeta.gameengine.actor.SimpleActor;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class TimingTestActivity extends Activity {

    private static final String LOG_SRC = GameWorld.LOG_SRC_GAME_ENGINE + ".Test";

    private TimingBox2DWorld mWorld;
    private GameView mGameView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TimingTestActivity.LOG_SRC, "onCreate " + this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.timing_test);
        mGameView = (GameView) findViewById(R.id.game_surface);
        mWorld = new TimingBox2DWorld(this, mGameView);
        mWorld.setFPS(33);
        mWorld.setDrawDebugInfo(true);

        {
            Button button = (Button)findViewById(R.id.btn1);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.setFPS(10);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn2);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.setFPS(33);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn3);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.setPhysicalFrequency(15);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn4);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.setPhysicalFrequency(60);
                }
            });
        }
   
    }

    /**
     * World
     */
    class TimingBox2DWorld extends Box2DWorld {

        private float mVelX = 1;
        private float mRadius = 0.5f;
        
        private Box2DActor  mBox2DActor;
        private SimpleActor mSimpleActor;
        
        public TimingBox2DWorld(Activity activity, GameView gameView) {
            super(activity, gameView);            
        }

        @Override
        public void onGameViewSizeChanged(int width, int height) {
            Log.i(TimingTestActivity.LOG_SRC, "onGameViewSizeChanged " + this);
            viewport.setWorldWidth(10);
            startLooping();
            play();
        }
 
        @Override
        public void onGameWorldSizeChanged() {
            Log.i(TimingTestActivity.LOG_SRC, "onGameWorldSizeChanged " + this);
            create();
         }

        private void create() {
            post(new GameMessage() {

                @Override
                public void process(GameWorld world) {
                    removeAllActors();

                    RectF vb = viewport.getWorldBoundaries();

                    float margin = viewport.pixelsToWorldUnits(1);
                    float left   = vb.left + margin;
                    float bottom = vb.bottom + margin ;
                    float right  = vb.right - margin;
                    float top    = vb.top - margin;

                    // box
                    addActor(new Box2DLoopActor(mWorld, 
                                                 new PointF(0, 0),
                                                 new PointF[] {
                                                    new PointF(left,   bottom),
                                                    new PointF(left,   top),
                                                    new PointF(right,  top),
                                                    new PointF(right,  bottom),
                                                 },
                                                 false));
                }});
        }

        @Override
        public boolean processFrame(float lastFrameLength) {
            if (mBox2DActor != null) {
                if (mBox2DActor.getBodies().get(0).getWorldCenter().x >= viewport.getWorldBoundaries().right - (mRadius * 2)) {
                    removeActor(mBox2DActor);
                    mBox2DActor = null;
                }
            } else {
                createBox2DActor();
            }
            
            if (mSimpleActor != null) {
                if (mSimpleActor.getWorldPos().x >= viewport.getWorldBoundaries().right - (mRadius * 2)) {
                    removeActor(mSimpleActor);
                    mSimpleActor = null;
                }
            } else {
                createSimpleActor();
            }
            return false;
        }
        

        private void createBox2DActor() {
            RectF wb = viewport.getWorldBoundaries();
            PointF worldPos = new PointF(wb.left + mRadius, (wb.top - wb.bottom)  / 3);
            mBox2DActor = new Box2DCircleActor(this, worldPos, mRadius, true);
            mBox2DActor.getBodies().get(0).setLinearVelocity(mVelX, 0);
            addActor(mBox2DActor);
        }

        private void createSimpleActor() {
            RectF wb = viewport.getWorldBoundaries();
            PointF worldPos = new PointF(wb.left + mRadius, ((wb.top - wb.bottom)  / 3) * 2);
            mSimpleActor = new SimpleActor(this, worldPos.x, worldPos.y) {

                @Override
                public void draw(Canvas canvas) {
                    PointF worldPos = getWorldPos();
                    PointF screenPos = viewport.worldToScreen(worldPos);
                    Paint paint = new Paint();
                    paint.setColor(Color.MAGENTA);
                    canvas.drawCircle(screenPos.x, screenPos.y, viewport.worldUnitsToPixels(mRadius), paint);
                }

            };
            mSimpleActor.setLinearVelocity(mVelX, 0);
            addActor(mSimpleActor);
        }
        
        

    }

}


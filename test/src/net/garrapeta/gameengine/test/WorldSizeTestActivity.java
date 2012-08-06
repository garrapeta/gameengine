package net.garrapeta.gameengine.test;

import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.gameengine.Viewport;
import net.garrapeta.gameengine.box2d.Box2DWorld;
import net.garrapeta.gameengine.box2d.actor.Box2DCircleActor;
import net.garrapeta.gameengine.box2d.actor.Box2DEdgeActor;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class WorldSizeTestActivity extends Activity {

    private static final String LOG_SRC = GameWorld.LOG_SRC_GAME_ENGINE + ".Test";
    private static final int FPS = 60;

    private WorldSizeBox2DWorld mWorld;
    private GameView mGameView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(WorldSizeTestActivity.LOG_SRC, "onCreate " + this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.world_size_test);
        mGameView = (GameView) findViewById(R.id.game_surface);
        mWorld = new WorldSizeBox2DWorld(this, mGameView);
        mWorld.setFPS(FPS);
        mWorld.setDrawDebugInfo(true);
        
        
        {
            Button button = (Button)findViewById(R.id.btn1);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.viewport.setWorldSize(5, 10, 40);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn2);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.viewport.setWorldSize(5, 10, 20);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn3);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.viewport.setWorldSize(5, 10, Viewport.ProjectionMode.FIT_WIDTH);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn4);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.viewport.setWorldSize(5, 10, Viewport.ProjectionMode.FIT_HEIGHT);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn5);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.viewport.setWorldWidth(5, 40);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn6);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.viewport.setWorldWidth(5, 20);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn7);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.viewport.setWorldHeight(10, 40);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn8);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.viewport.setWorldHeight(10, 20);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn9);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.viewport.setWorldWidth(5);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn10);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.viewport.setWorldHeight(10);
                }
            });
        }
    }

    /**
     * World
     */
    class WorldSizeBox2DWorld extends Box2DWorld {

        public WorldSizeBox2DWorld(Activity activity, GameView gameView) {
            super(activity, gameView);
        }

        @Override
        public void onGameViewSizeChanged(int width, int height) {
            Log.i(WorldSizeTestActivity.LOG_SRC, "onGameViewSizeChanged " + this);
            setGravityY(-9.8f);
            mWorld.startLooping();
            mWorld.play();
        }
 
        @Override
        public void onGameWorldSizeChanged() {
            Log.i(WorldSizeTestActivity.LOG_SRC, "onGameWorldSizeChanged " + this);
            removeAllActors();


            RectF vb = viewport.getWorldBoundaries();

            float margin = viewport.pixelsToWorldUnits(1);
            float left   = vb.left + margin;
            float bottom = vb.bottom + margin ;
            float right  = vb.right - margin;
            float top    = vb.top - margin;

            // upper edge
            addActor(new Box2DEdgeActor(this, 
                                         new PointF(0, 0),
                                         new PointF(left,   top),  
                                         new PointF(right,  top),
                                         false));
            
            // bottom edge
            addActor(new Box2DEdgeActor(this,
                                         new PointF(0, 0),  
                                         new PointF(left,   bottom),  
                                         new PointF(right,  bottom),
                                         false));
            
            // left edge
            addActor(new Box2DEdgeActor(this,
                                         new PointF(0,0 ),
                                         new PointF(left,   bottom),  
                                         new PointF(left,   top),
                                         false));            

            // right edge
            addActor(new Box2DEdgeActor(this,
                                        new PointF(0, 0),
                                        new PointF(right,   bottom),  
                                        new PointF(right,   top ),
                                        false));
        }

        @Override
        public void processFrame(float gameTimeStep) {

        }

        @Override
        protected void drawWorld(Canvas canvas) {
            super.drawWorld(canvas);
            viewport.drawBoundaries(canvas);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                PointF worldPos = viewport.screenToWorld(event.getX(), event.getY());
                createCircleActor(worldPos);
            }
        }

       
        
        
        private void createCircleActor(PointF worldPos) {
            float radius = 0.5f;
            Box2DCircleActor actor = new Box2DCircleActor(this, worldPos, radius, true);
            addActor(actor);
        }

    }
}


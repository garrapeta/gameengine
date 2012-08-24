package net.garrapeta.gameengine.test;

import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.GameMessage;
import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.gameengine.Viewport;
import net.garrapeta.gameengine.actor.Box2DEdgeActor;
import net.garrapeta.gameengine.actor.Box2DLoopActor;
import android.app.Activity;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class WorldSizeTestActivity extends Activity {

    private static final String LOG_SRC = GameWorld.LOG_SRC_GAME_ENGINE + ".Test";
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
            mWorld.startRunning();
            mWorld.play();
        }
 
        @Override
        public void onGameWorldSizeChanged() {
            Log.i(WorldSizeTestActivity.LOG_SRC, "onGameWorldSizeChanged " + this);
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

                }
                
            });
        }
    }
}


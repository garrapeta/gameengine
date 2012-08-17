package net.garrapeta.gameengine;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;

public class ShapeBasedBodyDrawer implements IBodyDrawer {
	
	// ------------------------------------------------------------------- Constantes
	
	private static final int DEFAULT_STROKE_COLOR 	= Color.WHITE;
	private static final int DEFAULT_FILL_COLOR 	= Color.DKGRAY;
	private static final int DEFAULT_LINE_COLOR 	= Color.GRAY;
	

	// ------------------------------------------------------------------- Variables

	protected int strokeColor;
	protected int fillColor;
	protected int lineColor;
	
	
	// ------------------------------------------------------------------- Constructor
	
	public ShapeBasedBodyDrawer () {
		this(DEFAULT_STROKE_COLOR, DEFAULT_FILL_COLOR, DEFAULT_LINE_COLOR);
	}
	
	public ShapeBasedBodyDrawer (int strokeColor, int fillColor, int lineColor) {
		this.strokeColor = strokeColor;
		this.fillColor = fillColor;
		this.lineColor = lineColor;
	}
	
	// --------------------------------------------------------- M�todos est�ticos
	
	// ----------------------------------------------------- M�todos de IBodyDrawer
	
	@Override
	public void draw(Canvas canvas, Box2DActor actor, Body body) {
		Vector2 worldPos = body.getWorldCenter();
		
		PointF screenPos = actor.mGameWorld.viewport.worldToScreen(worldPos.x, worldPos.y);
		canvas.save();
		
		canvas.translate(screenPos.x, screenPos.y);
		canvas.rotate(- (float)Math.toDegrees(body.getAngle()));
		
		
		ArrayList<Fixture> fixtures = body.getFixtureList();
		Iterator<Fixture> ite = fixtures.iterator();
        while (ite.hasNext()) {
        	Fixture f = ite.next();
        	drawShape(canvas, actor, f.getShape());
        }
        
        canvas.restore();
		
	}
	
	private static Paint sPaint = new Paint();

	protected void drawShape(Canvas canvas, Box2DActor actor, Shape shape) {
	    ShapeDrawer.draw(canvas, sPaint, actor.mGameWorld.viewport, shape, strokeColor, fillColor, lineColor);
	}
	
}

package com.garrapeta;

import java.util.Vector;

import android.graphics.PointF;


/**
 * Funciones matem�ticas de utilidad 
 * @author GaRRaPeTa
 */
public class MathUtils {
	
	/**
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @return distancia entre dos puntos
	 */
	public static float getDistance(float x0, float y0, float x1, float y1) {
		return getDistance(x1 - x0, y1 - y0);
	}
	
	/**
	 * @param side0
	 * @param side1
	 * @return distancia entre dos puntos en la misma recta
	 */
	public static float getDistance(float side0, float side1) {
		return (float) Math.sqrt(side0 * side0 + side1 * side1);
	}
	
	
	/**
	 * @param pointX
	 * @param pointY
	 * @param circleX
	 * @param circleY
	 * @param radius
	 * @return Si un punto est� dentro de un c�rculo
	 */
	public static boolean isPointInCicle(float pointX, float pointY, 
			                             float circleX, float circleY, float radius) {
		return circleX + radius >= pointX && circleX - radius <= pointX &&
		       circleY + radius >= pointY && circleY - radius <= pointY;
	}
	
	public static boolean isPointInRectangle(float circX, float circY, 
			                                 float rectX0, float rectY0,
			                                 float rectX1, float rectY1) {
		if (circX < rectX0) {
			return false;
		}
		if (circX > rectX1) {
			return false;
		}
		if (circY < rectY0) {
			return false;
		}
		if (circY  > rectY1) {
			return false;
		}
		return true;
	}

	
	/**
	 * 
	 * http://alienryderflex.com/intersect/
	 * 
	 * @param a0x
	 * @param a0y
	 * @param a1x
	 * @param a1y
	 * @param b0x
	 * @param b0y
	 * @param b1x
	 * @param b1y 
	 * @return Intersecci�n entre dos segmentos
	 */
	public static PointF segmentsIntersection(float a0x, float a0y, float a1x, float a1y, 
			                              float b0x, float b0y, float b1x, float b1y) {

		  float  distA, theCos, theSin, newX, aPos ;

		  //  Fail if either line segment is zero-length.
		  if (a0x == a1x && a0y == a1y || b0x == b1x && b0y ==b1y) {
			  return null;
		  }

		  //  Fail if the segments share an end-point.
		  if (a0x == b0x && a0y == b0y || a1x == b0x && a1y == b0y
				  ||  a0x == b1x && a0y == b1y || a1x == b1x && a1y == b1y) {
		    return null; 
		  }

		  //  (1) Translate the system so that point a0 is on the origin.
		  a1x -= a0x; a1y -= a0y;
		  b0x -= a0x; b0y -= a0y;
		  b1x -= a0x; b1y -= a0y;

		  //  Discover the length of segment a0 - a1
		  distA = MathUtils.getDistance(a1x, a1y);

		  //  (2) Rotate the system so that point a1 is on the positive X axis.
		  theCos = a1x / distA;
		  theSin = a1y / distA;
		  newX = b0x * theCos + b0y * theSin;
		  b0y  = b0y * theCos - b0x * theSin; 
		  b0x = newX;
		  newX = b1x * theCos + b1y * theSin;
		  b1y   = b1y * theCos - b1x * theSin; 
		  b1x=newX;

		  //  Fail if segment b0-b1 doesn't cross line a0-a1
		  if (b0y < 0 && b1y < 0 || b0y >= 0 && b1y >= 0) {
			  return null;
		  }

		  //  (3) Discover the position of the intersection point along line a0-a1
		  aPos = b1x + (b0x - b1x) * b1y / (b1y - b0y);

		  //  Fail if segment b0-b1 crosses line a0-a1 outside of segment a0-a1
		  if (aPos<0 || aPos>distA) {
			  return null;
		  }

		  //  (4) Apply the discovered position to line a0-a1 in the original coordinate system.
		  //  Success.
		  return new PointF(a0x + aPos * theCos, a0y + aPos * theSin); 
	}
	
	public static boolean isCircunferenceInRectangle(float circX, float circY, float radius,
			                                         float rectX0, float rectY0,
			                                         float rectX1, float rectY1) {
		if (circX - radius < rectX0) {
			return false;
		}
		if (circX + radius > rectX1) {
			return false;
		}
		if (circY - radius < rectY0) {
			return false;
		}
		if (circY + radius > rectY1) {
			return false;
		}
		return true;
	}
	
	/**
	 * @param circleX
	 * @param circleY
	 * @param circleRadius
	 * @param rectX
	 * @param rectY
	 * @param rectWidth
	 * @param rectHeight
	 * @return Si un c�rculo interseca un rect�ngulo
	 */
	public static boolean circleIntersectsRectagle(float circleX, float circleY, float circleRadius,
			                                       float rectX, float rectY, float rectWidth, float rectHeight)
	{
	    float circleDistanceX = Math.abs(circleX - rectX - rectWidth  /2);
	    float circleDistanceY = Math.abs(circleY - rectY - rectHeight / 2);

	    if (circleX > (rectWidth /2 + circleRadius)) { return false; }
	    if (circleY > (rectHeight/2 + circleRadius)) { return false; }

	    if (circleX <= (rectWidth/2))  { return true; } 
	    if (circleY <= (rectHeight/2)) { return true; }

	    float cornerDistance_sq = (float)(Math.sqrt(circleDistanceX - rectWidth/2) +
	                                      Math.sqrt(circleDistanceY - rectHeight/2));


	    return cornerDistance_sq <= Math.sqrt(circleRadius);
	}
	
	/**
	 * @param circleX
	 * @param circleY
	 * @param circleRadius
	 * @param rectX0
	 * @param rectY0
	 * @param rectX1
	 * @param rectY1
	 * @param distance
	 * @return Si un c�rculo interseca una recta
	 */
	public static boolean circleIntersectsLine(float circleX, float circleY, float circleRadius,
                                               float rectX0, float rectY0, 
                                               float rectX1, float rectY1, 
                                               float distance)
	{
		if (isPointInCicle(rectX0, rectY0, circleX, circleY, circleRadius)) {
			return true;
		}
		if (getDistance(rectX0, rectY0, rectX1, rectY1) > distance)  {
			float medX = (rectX0 + rectX1) / 2;
			float medY = (rectY0 + rectY1) / 2;
			return circleIntersectsLine(circleX, circleY, circleRadius, medX, medY, rectX1, rectY1, distance);
		}
		
		return false;
	}
	
	
	/**
	 * // http://www.visibone.com/inpoly/
	 * 
	 * @param points
	 * @param offset
	 * @param count
	 * @param xt
	 * @param yt
	 * @return si un punto est� dentro de un pol�gono
	 */ 
	public static  boolean isPointInsidePolygon(Vector<PointF> points, int offset, int count, float xt, float yt) {
		float xnew,ynew;
	     float xold,yold;
	     float x1,y1;
	     float x2,y2;
	     int i;
	     boolean inside = false;

	     if (count < 3) {
	          return(false);
	     }
	     PointF pOld = points.elementAt(count-1);
	     xold = pOld.x;
	     yold = pOld.y;
	     for (i = offset ; i < count ; i++) {
	    	  PointF pNew = points.elementAt(i);
	    	  xnew = pNew.x;
	          ynew = pNew.y;
	          if (xnew > xold) {
	               x1=xold;
	               x2=xnew;
	               y1=yold;
	               y2=ynew;
	          }
	          else {
	               x1=xnew;
	               x2=xold;
	               y1=ynew;
	               y2=yold;
	          }
	          if ((xnew < xt) == (xt <= xold)          /* edge "open" at one end */
	           && (yt-y1)*(x2-x1) < (y2-y1)*(xt-x1)) {
	               inside = !inside;
	          }
	          xold=xnew;
	          yold=ynew;
	     }
	     return(inside);
	}
	
	/**
	 * @param cx
	 * @param cy
	 * @param radius
	 * @param sides
	 * @return obtiene los vértices de un pol�gono regular
	 */
	public static float[][] getPolyconVertexes(float cx, float cy, float radius, int sides) {
		
		float[][] vertexes = new float[sides][2];
		int i   = 0;
		 
		do {
			float x = cx + radius * (float)Math.cos(2.0 * Math.PI *i/sides);
			float y = cy + radius * (float)Math.sin(2.0 * Math.PI *i/sides);
			
			vertexes[i][0] = x; 
			vertexes[i][1] = y;
			i++;

		} while (i < sides);
		
		return vertexes;
	}



}

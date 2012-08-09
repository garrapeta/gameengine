package net.garrapeta.utils;

/**
 * M�todos de utilidad de f�sicas
 * @author GaRRaPeTa
 */
public class PhysicsUtils {

	
	/**
	 * @param maxHeight
	 * @return
	 */
	public final static double getInitialVelocity(double distance, double finalVel, double aceleration) {
		// 2gy = Vy^2 - Voy^2
		// http://ar.answers.yahoo.com/question/index?qid=20110809214755AAghXGn
		return Math.sqrt( Math.pow(finalVel, 2) - (2 * aceleration * distance));
	}
	
	/**
	 * @param maxHeight
	 * @return
	 */
	public final static double getDistance(double initVel, double finalVel, double aceleration) {
		// 2gy = Vy^2 - Voy^2
		// http://ar.answers.yahoo.com/question/index?qid=20110809214755AAghXGn
		return  (Math.pow(finalVel, 2) - Math.pow(initVel, 2) ) / (2 * aceleration); 
	}
	
	/**
	 * @param maxHeight
	 * @return
	 */
	public final static double getTime(double initVel, double finalVel, double aceleration) {
		// Vf = Vi + at
		// http://ar.answers.yahoo.com/question/index?qid=20110809214755AAghXGn
		return  (finalVel - initVel) / aceleration;
	}
	
	/**
	 * @param maxHeight
	 * @return
	 */
	public final static double getTime(double distance, double aceleration) {
		return  Math.sqrt((2 * aceleration * distance) ) / aceleration;
	}
	
	/**
	 * @param pos
	 * @param initVel
	 * @return
	 */
	public final static double getUpAndDownTime(float pos, double initVel, double acceleration) {
		double maxHeight = pos;
		double secondsUp = 0;
		
		if (initVel > 0) {
			maxHeight += PhysicsUtils.getDistance(initVel, 0, acceleration);
			secondsUp += PhysicsUtils.getTime(initVel, 0, acceleration);
		} 

		double secondsDown = PhysicsUtils.getTime(maxHeight, -acceleration);
		
		return secondsUp + secondsDown;
	}
}

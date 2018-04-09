package edu.cg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collection;

public class SeamsCarver extends ImageProcessor {
	
	//MARK: An inner interface for functional programming.
	@FunctionalInterface
	interface ResizeOperation {
		BufferedImage apply();
	}
	
	//MARK: Fields
	private int numOfSeams;
	private ResizeOperation resizeOp;
	private long[][] m;
	private long[][] i;
	private Collection seams;
	
	//TODO: Add some additional fields:
	
	
	//MARK: Constructor
	public SeamsCarver(Logger logger, BufferedImage workingImage,
			int outWidth, RGBWeights rgbWeights) {
		super(logger, workingImage, rgbWeights, outWidth, workingImage.getHeight()); 
		
		numOfSeams = Math.abs(outWidth - inWidth);
		BufferedImage gradientMag = gradientMagnitude();
		
		if(inWidth < 2 | inHeight < 2)
			throw new RuntimeException("Can not apply seam carving: workingImage is too small");
		
		if(numOfSeams > inWidth/2)
			throw new RuntimeException("Can not apply seam carving: too many seams...");
		
		//Sets resizeOp with an appropriate method reference
		if(outWidth > inWidth)
			resizeOp = this::increaseImageWidth;
		else if(outWidth < inWidth)
			resizeOp = this::reduceImageWidth;
		else
			resizeOp = this::duplicateWorkingImage;
		
		// TODO: Format seams collection
		
		// Initialize both previous and current matrices as gradient magnitude.
		m = new long[gradientMag.getWidth()][gradientMag.getHeight()];
		i = new long[gradientMag.getWidth()][gradientMag.getHeight()];
		logger.log("starting to copy matrix");
		forEach((y,x) -> {
			m[x][y] = new Color(gradientMag.getRGB(x, y)).getRed();
			i[x][y] = new Color(gradientMag.getRGB(x, y)).getRed();
		});
		logger.log("done intializing");
		// TODO: add iterator for number of seams k
		// Run matrix
		for(int y=1;y<m.length;y++) {
			for(int x=0;x<m[0].length;x++) {
				m[y][x] = calcEnergy(y,x);
			}
		}
		// get 
		logger.log("completed");
		
	}
	
	public long calcEnergy(int y, int x) {
		long energy = m[y][x];
		long MCL, MCV, MCR;
		if(x == 0) {
			// TODO: Finish the edge case for MCR/MCV
			MCL = Long.MAX_VALUE;
			MCR = m[y-1][x+1] + Math.abs(i[y][x+1]-i[y][x-1]) + Math.abs(i[y-1][x]-i[y][x+1]);
		} else 		if(x < m[0].length) {
			// TODO: Finish the edge case for MCR/MCV
			MCL =  m[y-1][x-1] + Math.abs(i[y][x+1]-i[y][x-1]) + Math.abs(i[y-1][x]-i[y][x-1]);
		} else {
			MCR = m[y-1][x+1] + Math.abs(i[y][x+1]-i[y][x-1]) + Math.abs(i[y-1][x]-i[y][x+1]);
			MCL =  m[y-1][x-1] + Math.abs(i[y][x+1]-i[y][x-1]) + Math.abs(i[y-1][x]-i[y][x-1]);
			MCV = m[y-1][x] + Math.abs(i[y][x+1]-i[y][x-1]);
		}
		energy += Math.min(Math.min(MCL, MCR), MCV);
		return energy;
	}
	//MARK: Methods
	public BufferedImage resize() {
		logger.log("starting resize");
		return resizeOp.apply();
	}
	
	//MARK: Unimplemented methods
	private BufferedImage reduceImageWidth() {
		//TODO: Implement this method, remove the exception.
		throw new UnimplementedMethodException("reduceImageWidth");
	}
	
	private BufferedImage increaseImageWidth() {
		//TODO: Implement this method, remove the exception.
		throw new UnimplementedMethodException("increaseImageWidth");
	}
	
	public BufferedImage showSeams(int seamColorRGB) {
		//TODO: Implement this method (bonus), remove the exception.
		throw new UnimplementedMethodException("showSeams");
	}
}

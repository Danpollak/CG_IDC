package edu.cg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;

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
	private long[][] e;
	private int[][] seams;
	
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
				
		// Initialize both previous and current matrices as gradient magnitude.
		m = new long[gradientMag.getWidth()][gradientMag.getHeight()];
		e = new long[gradientMag.getWidth()][gradientMag.getHeight()];
		forEach((y,x) -> {
			m[x][y] = new Color(gradientMag.getRGB(x, y)).getRed();
			e[x][y] = new Color(gradientMag.getRGB(x, y)).getRed();
		});
		// Run matrix
		for(int y=1;y<m.length;y++) {
			for(int x=0;x<m[0].length;x++) {
				m[y][x]+= calcEnergy(y,x);
			}
		}
		
		// extract k seams
		seams = new int[numOfSeams][m.length];
		int[][] indexTracking = new int[m.length][m[0].length];
		
		for(int k=0;k<numOfSeams;k++) {
			long[] topRow = m[m.length-1];
			int[] minimalSeam = new int[m.length];
			int minimalIndex = 0;
			// find the minimal index in the top row
			for(int i=1;i<topRow.length;i++) {
				if(topRow[minimalIndex] < topRow[i]) {
					minimalIndex = i;
				}
			}	
			// backtrack minimal energy seam
			minimalSeam[m.length-1] = minimalIndex + indexTracking[m.length-1][minimalIndex];
			logger.log("backtrack");
			for(int j=m.length-1;j>0;j--) {
				// choose the minimal value for each previous 3 index
				long leftIndex = minimalIndex > 0 ? m[j-1][minimalIndex-1] : Long.MAX_VALUE;
				long midIndex = m[j-1][minimalIndex];
				long rightIndex = minimalIndex >= topRow.length ? m[j-1][minimalIndex+1] : Long.MAX_VALUE;
				if(leftIndex < midIndex && leftIndex < rightIndex) {
					minimalIndex--;
				} else if (rightIndex < midIndex && rightIndex < leftIndex) {
					minimalIndex++;
				}
				minimalSeam[j-1] = minimalIndex + indexTracking[j-1][minimalIndex];
			}
			// put the seam in the seam collection
			seams[k] = minimalSeam;
			// remove seam, update index and calculate new energy
			logger.log("remove seam");
			for(int j=0;j<m.length;j++) {
				long[] newRow = new long[m[0].length-1];
				for(int i=0;i<m[0].length-1;i++) {
					if(i<minimalSeam[j]) {
						// if you haven't hit the removed index, copy
						newRow[i] = m[j][i];
					} else {
						// if you are above the removed index, skip one and indicate it on the indexTracking
						newRow[i] = m[j][i+1];
						indexTracking[j][i]++;
					}
				}
				// point the matrix to the new row
				m[j] = newRow;
			}	
		}
	}
	
	public long calcEnergy(int y, int x) {
		long MCL, MCV, MCR;
		if(x == 0) {
			MCL = Long.MAX_VALUE;
			MCV = 0;
			MCR = m[y-1][x+1] + Math.abs(e[y-1][x]-e[y][x+1]);
		} else if (x < m[0].length-1) {
			MCL =  m[y-1][x-1] + Math.abs(e[y][x+1]-e[y][x-1]) + Math.abs(e[y-1][x]-e[y][x-1]);
			MCV = m[y-1][x] + Math.abs(e[y][x+1]-e[y][x-1]);
			MCR = m[y-1][x+1] + Math.abs(e[y][x+1]-e[y][x-1]) + Math.abs(e[y-1][x]-e[y][x+1]);
		} else {
			MCL = m[y-1][x-1] + Math.abs(e[y-1][x]-e[y][x-1]);
			MCV = 0;
			MCR = Long.MAX_VALUE;
		}
		return Math.min(Math.min(MCL, MCR), MCV);
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

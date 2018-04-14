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
		int rows = gradientMag.getHeight();
		int columns = gradientMag.getWidth();
		m = new long[columns][rows];
		e = new long[columns][rows];
		logger.log("initializing both matrices from workingImage");
		forEach((y,x) -> {
			m[x][y] = new Color(gradientMag.getRGB(x,y)).getRed();
			e[x][y] = new Color(gradientMag.getRGB(x,y)).getRed();
		});
		
		logger.log("computing matrix");
		// Run matrix
		for(int y=1;y<rows;y++) {
			for(int x=0;x<columns;x++) {
				m[x][y]+= calcEnergy(x,y);
			}
		}
		logger.log("starting to extract seams");
		// extract k seams
		seams = new int[numOfSeams][rows];
		int[][] indexOffset = new int[columns][rows];
		
		for(int k=0;k<numOfSeams;k++) {
			logger.log("finding seam " + k + "#");
			// minimalSeam will hold the array that has the indexes in the workingImage
			int[] minimalSeam = new int[rows];
			// minimalIndex will be the pointer for the current minimal index
			int minimalIndex = 0;
			// find the minimal index in the top row
			for(int x=1;x<columns;x++) {
				if(m[minimalIndex][rows-1] >= m[x][rows-1]) {
					minimalIndex = x;
				}
			}
			logger.log("found minimal index: " + minimalIndex);
			// setting the top row minimal index as the last index in minimalSeam
			minimalSeam[rows-1] = minimalIndex;
			// backtrack minimal seam in m
			for(int y=rows-1;y>0;y--) {
				// choose the minimal value for each previous 3 index
				long leftValue = minimalIndex > 0 ? m[minimalIndex-1][y-1] : Long.MAX_VALUE;
				long midValue = m[minimalIndex][y-1];
				long rightValue = minimalIndex >= columns ? m[minimalIndex+1][y-1] : Long.MAX_VALUE;
				if(leftValue < midValue && leftValue < rightValue) {
					minimalIndex--;
				} else if (rightValue < midValue && rightValue < leftValue) {
					minimalIndex++;
				}
				minimalSeam[y-1] = minimalIndex;
			}
			// add offsets for minimalSeam
			for(int y=0;y<rows;y++) {
				minimalSeam[y]+=indexOffset[minimalSeam[y]][y];
			}
			logger.log(minimalSeam[rows-1]);
			seams[k] = minimalSeam;
			// remove seam, update index and calculate new energy
			logger.log("removing seam and updating indexes");
			long[][] mUpdate = new long[columns-1][rows];
			long[][] eUpdate = new long[columns-1][rows];
			for(int y=0;y<rows;y++) {
				// get the seam index without the offset (that may change during runtime)
				int seamIndexNoOffset = minimalSeam[y] - indexOffset[minimalSeam[y]][y];
				for(int x=0;x<columns-1;x++) {
					// if the minimal seam index in this row is bigger than the current index, copy the matrix as is
					if(x<seamIndexNoOffset) {
						mUpdate[x][y] = m[x][y];
						eUpdate[x][y] = e[x][y];
					} else {
					// if the minimal seam in this row is bigger or equal to the current index, shift its value by 1 and add it to the indexOffset
						mUpdate[x][y] = m[x+1][y];
						eUpdate[x][y] = e[x+1][y];
						indexOffset[x][y]++;
					}
				}
				// need to recalculate the energy - problematic because you both need the current offset AND the updated m/e matrices
//				if(y>0) {
//				mUpdate[seamIndexNoOffset][y] = calcEnergy(seamIndexNoOffset, y);
//				if(seamIndexNoOffset>=0) {
//					mUpdate[seamIndexNoOffset-1][y] = calcEnergy(seamIndexNoOffset-1, y);
//				}
//			}
			}
			m = mUpdate;
			e = eUpdate;
			columns--;
		}
	}
	
	public long calcEnergy(int x, int y) {
		long MCL, MCV, MCR;
		if(x == 0) {
			MCL = Long.MAX_VALUE;
			MCV = m[x][y-1];
			MCR = m[x+1][y-1] + Math.abs(e[x][y-1]-e[x+1][y]);
		} else if (x < m.length-1) {
			MCL =  m[x-1][y-1] + Math.abs(e[x+1][y]-e[x-1][y]) + Math.abs(e[x][y-1]-e[x-1][y]);
			MCV = m[x][y-1] + Math.abs(e[x+1][y]-e[x-1][y]);
			MCR = m[x+1][y-1] + Math.abs(e[x+1][y]-e[x-1][y]) + Math.abs(e[x][y-1]-e[x+1][y]);
		} else {
			MCL = m[x-1][y-1] + Math.abs(e[x][y-1]-e[x-1][y]);
			MCV = m[x][y-1];
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
		BufferedImage ans =  newEmptyOutputSizedImage();
		int pointer = 0;
		int rows = ans.getHeight();
		int columns = ans.getWidth();
		for(int y=0;y<rows;y++) {
			for(int x=0;x<columns;x++) {
				for(int[] seam : seams) {
					if(x == seam[y]) {
						pointer++;
					}
			}
			ans.setRGB(x, y, this.workingImage.getRGB(x+pointer,y));
		}
			pointer = 0;

	}
		return ans;
	}
	
	private BufferedImage increaseImageWidth() {
		BufferedImage ans =  newEmptyOutputSizedImage();
		int pointer = 0;
		int rows = ans.getHeight();
		int columns = ans.getWidth();
		int orgColumns = this.workingImage.getWidth();
		for(int y=0;y<rows;y++) {
			for(int x=0;x<orgColumns;x++) {
				for(int[] seam : seams) {
					if(x == seam[y]) {
						ans.setRGB(x+pointer, y, this.workingImage.getRGB(x,y));
						logger.log("moving pointer: " + pointer + " ,x:" + x);
						pointer++;
					}
			}
			ans.setRGB(x+pointer, y, this.workingImage.getRGB(x,y));
		}
			pointer = 0;

	}
		return ans;
	}
	
	public BufferedImage showSeams(int seamColorRGB) {
		BufferedImage ans =  newEmptyInputSizedImage();
		forEach((y,x) -> {
			ans.setRGB(x,y, this.workingImage.getRGB(x, y));
		});
		for(int j=0;j<ans.getHeight();j++) {
			for(int[] seam: seams) {
				ans.setRGB(seam[j], j, seamColorRGB);
			}
		}
		return ans;
	}
}

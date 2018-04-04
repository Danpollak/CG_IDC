package edu.cg;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageProcessor extends FunctioalForEachLoops {
	
	//MARK: Fields
	public final Logger logger;
	public final BufferedImage workingImage;
	public final RGBWeights rgbWeights;
	public final int inWidth;
	public final int inHeight;
	public final int workingImageType;
	public final int outWidth;
	public final int outHeight;
	
	//MARK: Constructors
	public ImageProcessor(Logger logger, BufferedImage workingImage,
			RGBWeights rgbWeights, int outWidth, int outHeight) {
		super(); //Initializing for each loops...
		
		this.logger = logger;
		this.workingImage = workingImage;
		this.rgbWeights = rgbWeights;
		inWidth = workingImage.getWidth();
		inHeight = workingImage.getHeight();
		workingImageType = workingImage.getType();
		this.outWidth = outWidth;
		this.outHeight = outHeight;
		setForEachInputParameters();
	}
	
	public ImageProcessor(Logger logger,
			BufferedImage workingImage,
			RGBWeights rgbWeights) {
		this(logger, workingImage, rgbWeights,
				workingImage.getWidth(), workingImage.getHeight());
	}
	
	//MARK: Change picture hue - example
	public BufferedImage changeHue() {
		logger.log("Prepareing for hue changing...");
		
		int r = rgbWeights.redWeight;
		int g = rgbWeights.greenWeight;
		int b = rgbWeights.blueWeight;
		int max = rgbWeights.maxWeight;
		
		BufferedImage ans = newEmptyInputSizedImage();
		
		forEach((y, x) -> {
			Color c = new Color(workingImage.getRGB(x, y));
			int red = r*c.getRed() / max;
			int green = g*c.getGreen() / max;
			int blue = b*c.getBlue() / max;
			Color color = new Color(red, green, blue);
			ans.setRGB(x, y, color.getRGB());
		});
		
		logger.log("Changing hue done!");
		
		return ans;
	}
	
	
	//MARK: Unimplemented methods
	public BufferedImage greyscale() {
		logger.log("Prepareing for greyscale changing...");

		int r = rgbWeights.redWeight;
		int g = rgbWeights.greenWeight;
		int b = rgbWeights.blueWeight;
		int greyscaleDenominator = r+g+b;

		BufferedImage ans = newEmptyInputSizedImage();

		forEach((y, x) -> {
			Color c = new Color(workingImage.getRGB(x, y));
			int red = r*c.getRed() / greyscaleDenominator;
			int green = g*c.getGreen() / greyscaleDenominator;
			int blue = b*c.getBlue() / greyscaleDenominator;
			int greyscaleTone = red+green+blue;
			Color color = new Color(greyscaleTone, greyscaleTone, greyscaleTone);
			ans.setRGB(x, y, color.getRGB());
		});

		logger.log("Changing greyscale done!");

		return ans;
	}

	public BufferedImage gradientMagnitude() {
		logger.log("Computing gradient magnitude...");

		BufferedImage greyscale = greyscale();
		BufferedImage ans = newEmptyInputSizedImage();
		forEach((y, x) -> {
			int magnitdue = 0;
			int current = new Color(greyscale.getRGB(x, y)).getRed();
			int dx = x < getForEachWidth() -1 ?
					new Color(greyscale.getRGB(x+1, y)).getRed() - current
					: new Color(greyscale.getRGB(x-1, y)).getRed() - current;
			int dy = y < getForEachHeight() -1 ?
					new Color(greyscale.getRGB(x, y+1)).getRed() - current
					: new Color(greyscale.getRGB(x, y-1)).getRed() - current;
			magnitdue = (int)Math.sqrt((dx*dx + dy*dy)/2);
			Color color = new Color(magnitdue, magnitdue, magnitdue);
			ans.setRGB(x, y, color.getRGB());
		});

		return ans;
	}
	
	public BufferedImage nearestNeighbor() {
		logger.log("Resizing by nearest neighbor");
		
		BufferedImage ans = newEmptyOutputSizedImage();
		float ratioX = (float)workingImage.getWidth() / ans.getWidth();
		float ratioY = (float)workingImage.getHeight() / ans.getHeight();
		setForEachOutputParameters();
		
		forEach((y,x) -> {
			int nearestX = Math.round(x*ratioX);
			int nearestY = Math.round(y*ratioY);
			Color neighbor = new Color(workingImage.getRGB(nearestX, nearestY));
			ans.setRGB(x, y, neighbor.getRGB());
		});
		
		return ans;
	}
	
	public BufferedImage bilinear() {
		logger.log("Resizing by billinear");
		
		BufferedImage ans = newEmptyOutputSizedImage();
		double ratioX = (double)workingImage.getWidth() / ans.getWidth();
		double ratioY = (double)workingImage.getHeight() / ans.getHeight();
		setForEachOutputParameters();
		
		forEach((y,x) -> {
			double transformationX = x*ratioX;
			double transformationY = y*ratioY;
			int roundupX = Math.min((int)Math.ceil(transformationX),workingImage.getWidth() -1);
			int roundupY = Math.min((int)Math.ceil(transformationY),workingImage.getHeight() -1) ;
			int rounddownX = (int)Math.floor(transformationX);
			int rounddownY = (int)Math.floor(transformationY);
			Color p11 = new Color(workingImage.getRGB(roundupX,roundupY));
			Color p01 = new Color(workingImage.getRGB(rounddownX,roundupY));
			Color p10 = new Color(workingImage.getRGB(roundupX,rounddownY));
			Color p00 = new Color(workingImage.getRGB(rounddownX,rounddownY));
			int r = (p11.getRed() + p01.getRed() + p10.getRed() + p00.getRed())/4;
			int g = (p11.getGreen() + p01.getGreen() + p10.getGreen() + p00.getGreen())/4;
			int b = (p11.getBlue() + p01.getBlue() + p10.getBlue() + p00.getBlue())/4;
			Color neighbor = new Color(r,g,b);
			ans.setRGB(x, y, neighbor.getRGB());
		});
		
		return ans;
	}
	
	
	//MARK: Utilities
	public final void setForEachInputParameters() {
		setForEachParameters(inWidth, inHeight);
	}
	
	public final void setForEachOutputParameters() {
		setForEachParameters(outWidth, outHeight);
	}
	
	public final BufferedImage newEmptyInputSizedImage() {
		return newEmptyImage(inWidth, inHeight);
	}
	
	public final BufferedImage newEmptyOutputSizedImage() {
		return newEmptyImage(outWidth, outHeight);
	}
	
	public final BufferedImage newEmptyImage(int width, int height) {
		return new BufferedImage(width, height, workingImageType);
	}
	
	public final BufferedImage duplicateWorkingImage() {
		BufferedImage output = newEmptyInputSizedImage();
		
		forEach((y, x) -> 
			output.setRGB(x, y, workingImage.getRGB(x, y))
		);
		
		return output;
	}
}

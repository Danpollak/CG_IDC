package edu.cg.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.cg.Logger;
import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.objects.Surface;

public class Scene {
	private String name = "scene";
	private int maxRecursionLevel = 1;
	private int antiAliasingFactor = 1; // gets the values of 1, 2 and 3
	private boolean renderRefarctions = false;
	private boolean renderReflections = false;

	private Point camera = new Point(0, 0, 5);
	private Vec ambient = new Vec(1, 1, 1); // white
	private Vec backgroundColor = new Vec(0, 0.5, 1); // blue sky
	private List<Light> lightSources = new LinkedList<>();
	private List<Surface> surfaces = new LinkedList<>();

	// MARK: initializers
	public Scene initCamera(Point camera) {
		this.camera = camera;
		return this;
	}

	public Scene initAmbient(Vec ambient) {
		this.ambient = ambient;
		return this;
	}

	public Scene initBackgroundColor(Vec backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Scene addLightSource(Light lightSource) {
		lightSources.add(lightSource);
		return this;
	}

	public Scene addSurface(Surface surface) {
		surfaces.add(surface);
		return this;
	}

	public Scene initMaxRecursionLevel(int maxRecursionLevel) {
		this.maxRecursionLevel = maxRecursionLevel;
		return this;
	}

	public Scene initAntiAliasingFactor(int antiAliasingFactor) {
		this.antiAliasingFactor = antiAliasingFactor;
		return this;
	}

	public Scene initName(String name) {
		this.name = name;
		return this;
	}

	public Scene initRenderRefarctions(boolean renderRefarctions) {
		this.renderRefarctions = renderRefarctions;
		return this;
	}

	public Scene initRenderReflections(boolean renderReflections) {
		this.renderReflections = renderReflections;
		return this;
	}

	// MARK: getters
	public String getName() {
		return name;
	}

	public int getFactor() {
		return antiAliasingFactor;
	}

	public int getMaxRecursionLevel() {
		return maxRecursionLevel;
	}

	public boolean getRenderRefarctions() {
		return renderRefarctions;
	}

	public boolean getRenderReflections() {
		return renderReflections;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Camera: " + camera + endl + "Ambient: " + ambient + endl + "Background Color: " + backgroundColor + endl
				+ "Max recursion level: " + maxRecursionLevel + endl + "Anti aliasing factor: " + antiAliasingFactor
				+ endl + "Light sources:" + endl + lightSources + endl + "Surfaces:" + endl + surfaces;
	}

	private static class IndexTransformer {
		private final int max;
		private final int deltaX;
		private final int deltaY;

		IndexTransformer(int width, int height) {
			max = Math.max(width, height);
			deltaX = (max - width) / 2;
			deltaY = (max - height) / 2;
		}

		Point transform(int x, int y) {
			double xPos = (2 * (x + deltaX) - max) / ((double) max);
			double yPos = (max - 2 * (y + deltaY)) / ((double) max);
			return new Point(xPos, yPos, 0);
		}
	}

	private transient IndexTransformer transformaer = null;
	private transient ExecutorService executor = null;
	private transient Logger logger = null;

	private void initSomeFields(int imgWidth, int imgHeight, Logger logger) {
		this.logger = logger;
		// TODO: initialize your additional field here.
	}

	public BufferedImage render(int imgWidth, int imgHeight, Logger logger)
			throws InterruptedException, ExecutionException {

		initSomeFields(imgWidth, imgHeight, logger);

		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		transformaer = new IndexTransformer(imgWidth, imgHeight);
		int nThreads = Runtime.getRuntime().availableProcessors();
		nThreads = nThreads < 2 ? 2 : nThreads;
		this.logger.log("Intitialize executor. Using " + nThreads + " threads to render " + name);
		executor = Executors.newFixedThreadPool(nThreads);

		@SuppressWarnings("unchecked")
		Future<Color>[][] futures = (Future<Color>[][]) (new Future[imgHeight][imgWidth]);

		this.logger.log("Starting to shoot " + (imgHeight * imgWidth * antiAliasingFactor * antiAliasingFactor)
				+ " rays over " + name);

		for (int y = 0; y < imgHeight; ++y)
			for (int x = 0; x < imgWidth; ++x)
				futures[y][x] = calcColor(x, y);

		this.logger.log("Done shooting rays.");
		this.logger.log("Wating for results...");

		for (int y = 0; y < imgHeight; ++y)
			for (int x = 0; x < imgWidth; ++x) {
				Color color = futures[y][x].get();
				img.setRGB(x, y, color.getRGB());
			}

		executor.shutdown();

		this.logger.log("Ray tracing of " + name + " has been completed.");

		executor = null;
		transformaer = null;
		this.logger = null;

		return img;
	}

	private Future<Color> calcColor(int x, int y) {
		return executor.submit(() -> {
			// TODO: change this method implementation to implement super sampling
			Point pointOnScreenPlain = transformaer.transform(x, y);
			Ray ray = new Ray(camera, pointOnScreenPlain);
			return calcColorV2(ray, 0).toColor();
		});
	}

	private Vec calcColor(Ray ray, int recusionLevel) {
		Hit hit = FindIntersection(ray);
		if (hit == null) {
			return this.backgroundColor;
		}
		Surface surface = hit.getSurface();
		Point hitPoint = ray.add(hit.t());
		// add ambient
		Vec color = surface.Ka().mult(this.ambient);
		Vec N = hit.getNormalToSurface();
		for (Light light : this.lightSources) {
			// check for occlusions
			Vec L = light.getDirection(hitPoint);
			Vec R = N.mult(2 * (N.dot(L))).add(L.neg());
			Ray rayToLight = new Ray(hitPoint, L.neg());
			rayToLight.setSurface(surface);
			Hit occlusion = this.FindIntersection(rayToLight);
			if(occlusion == null) {
				Vec V = ray.direction();
				Vec kd = surface.Kd(hitPoint);
				Vec ks = surface.Ks();
				Vec I = light.getIntensity(hitPoint);
				double n = surface.shininess();
				Vec diff = kd.mult(N.dot(L));
				Vec spec = ks.mult(Math.pow(V.dot(R), n)).mult(I);
				color = color.add(diff);
				color = color.add(spec);
			} else {
				this.logger.log(surface.shapeType() + " was occuluded by " + occlusion.getSurface().shapeType() + " at t " + occlusion.t());
			}
		}
//		if (recusionLevel == this.maxRecursionLevel) {
//			return new Vec(0,0,0);
//		}
//		Ray out_ray = new Ray(hitPoint, N);
//		out_ray.setSurface(surface);
//		color= color.add(surface.Ks().mult(calcColor(out_ray, recusionLevel+1)));
		return color;
	}

	private Vec calcColorV2(Ray ray, int recusionLevel) {
		Hit hit = FindIntersection(ray);
		if (recusionLevel == this.maxRecursionLevel) {
			return new Vec(0,0,0);
		}
		if (hit == null) {
			return this.backgroundColor;
		}
		// I - Intensity of light. K property of material
		// <v,w> - dot prod of 2 vectors!
		// ambient - natural color of the material
		// diffusal - light scattering
		// specular - perpspective based
		// I = I_emission + K_ambient*I_ambient +
		// Sum(light l):
		// [K_diffusion*<normalToSurface,vectorFromLight>]I_l +
		// [K_specular*(<vectorFromCamera,vectorFromLight>^n)]I_l
		Vec intensity = new Vec(0,0,0);
		Vec ambientVec = clacAmbientIntensity(hit, ray);
//		this.logger.log(hit);
		Vec diffusiveVec = calcDiffusiveIntensity(hit, ray);
//		this.logger.log("done diff");
		Vec specularVec = calcSpecularIntensity(hit, ray);
		Vec reflectionVec = calcReflectionIntensity(hit, ray, recusionLevel);
//		this.logger.log("done spec");
		intensity = intensity.add(ambientVec)
			.add(diffusiveVec).add(specularVec).add(reflectionVec);
		return intensity;
	}
	
	
	private Vec clacAmbientIntensity(Hit hit, Ray ray){
		
		
		Surface surface = hit.getSurface();
		
		Vec Iambient = surface.Ka().mult(this.ambient);
		
		return Iambient;
	}


	// Iterates over all light sources,
	// calculates the sum of diffusive intensities
	private Vec calcDiffusiveIntensity(Hit hit, Ray ray){

		// I - Intensity of light. K property of material
		// <v,w> - dot prod of 2 vectors!
		// diffusal - light scattering
		// I_d = Sum(light l):
		// [K_diffusion*<normalToSurface,vectorFromLight>]I_l +
		Vec Idiffusive = new Vec(0,0,0);
		Point hitPoint = ray.add(hit.t());
		Surface surface = hit.getSurface();
		// note that all coef's are given as triplets,
		// as each light component are orthogonal

		Vec K_diffusion = surface.Kd(hitPoint);
		Vec normalToSurface = hit.getNormalToSurface();
		for(Light light : lightSources){
			Vec vectorFromLight = light.getDirection(hitPoint);
//			this.logger.log("vectorFromLight");
			Vec I_l = light.getIntensity(hitPoint);
//			this.logger.log("getIntensity");
			double dotProduct = vectorFromLight.dot(normalToSurface);
			if (occuluded(hit, hitPoint, light)){
//				this.logger.log("Kdiff: " + K_diffusion.toString() + " I_l:" + I_l.toString() + " dotP: " + dotProduct);
				Idiffusive = Idiffusive.add(K_diffusion.mult(I_l).mult(dotProduct));	
			}
			
//			this.logger.log("K diff:" + K_diffusion.toString() + " intensity:" + I_l.toString() + " dotProduct:" + dotProduct);
			
		}
		return Idiffusive;
	}

	
	// Iterates over all light sources,
	// calculates the sum of specular intensities
	private Vec calcSpecularIntensity(Hit hit, Ray ray){

		// I - Intensity of light. K property of material
		// <v,w> - dot prod of 2 vectors!
		// specular - perpspective based
		// I_spec = Sum(light l):
		// [K_specular*(<vectorFromCamera,ReflectionOfVectorFromLight>^n)]I_l

		Vec Ispecular = new Vec(0,0,0);
		Point hitPoint = ray.add(hit.t());
		Surface surface = hit.getSurface();
		// note that all coef's are given as triplets,
		// as each light component are orthogonal

		Vec K_specular = surface.Ks();
		Vec normalToSurface = hit.getNormalToSurface();
		for(Light light : lightSources){
			Vec vectorFromLight = light.getDirection(hitPoint);
			Vec reflectionVector = Ops.reflect(vectorFromLight, normalToSurface);
			
			Vec I_l = light.getIntensity(hitPoint);
			double dotProduct = reflectionVector.dot(ray.direction());
			dotProduct = Math.pow(dotProduct, surface.shininess());
			
			
			if (occuluded(hit, hitPoint, light)){
				Ispecular = Ispecular.add(K_specular.mult(I_l).mult(dotProduct));	
			}
		}
		return Ispecular;
	}
	
	// returns the reflection color vector by recursive colorCalc
	private Vec calcReflectionIntensity(Hit hit, Ray ray, int recusionLevel) {
			Surface surface = hit.getSurface();
			Vec normalToSurface = hit.getNormalToSurface();
			Vec reflectVec = Ops.reflect(ray.direction(), normalToSurface);
			Ray out_ray = new Ray(ray.add(hit.t()),reflectVec);
			double reflectionIntensity = surface.reflectionIntensity();
			Vec reflectionColor = calcColorV2(out_ray, recusionLevel+1);
			return reflectionColor.mult(surface.Ks()).mult(reflectionIntensity);
	}

	// iterates over all surfaces,
	// returns the first hit(if exists) that occurs
	// above the t axis
	private Hit FindIntersection(Ray ray) {
		Hit minHit = null;
		Surface src = ray.surface();
		for (Surface surface : surfaces) {
			// check if you are not checking intersection with yourself
			Hit currentHit = surface.intersect(ray);
			if (currentHit != null) {
				if(surface == src && currentHit.isWithinTheSurface()) {
					return currentHit;
				}
				if (minHit == null) {
					minHit = currentHit;
				} else {
					if (minHit.t() > currentHit.t()) {
						minHit = currentHit;
					}
				}
			}
		}
		if(minHit == null) {
			return null;
		}
		if(minHit.t() == Ops.infinity) {
			return null;
		}
		return minHit;
	}

	private boolean occuluded(Hit hit,Point hitPoint, Light light){
//		return true;
		Ray pntToLight = new Ray(hitPoint, light.getDirection(hitPoint));
		pntToLight.setSurface(hit.getSurface());
		Hit lightHit = FindIntersection(pntToLight);
		if( lightHit == null){
			return true;
		}
		double d = light.getDistance(hitPoint);
		double hitD = hitPoint.dist(pntToLight.add(lightHit.t()));
		if(d < hitD) {
			return true;
		}
		return false;
	}

}

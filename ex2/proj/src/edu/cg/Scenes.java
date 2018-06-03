package edu.cg;

import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;
import edu.cg.scene.Scene;
import edu.cg.scene.lightSources.DirectionalLight;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.lightSources.Spotlight;
import edu.cg.scene.objects.Dome;
import edu.cg.scene.objects.Material;
import edu.cg.scene.objects.Plain;
import edu.cg.scene.objects.Shape;
import edu.cg.scene.objects.Sphere;
import edu.cg.scene.objects.Surface;

public class Scenes {
	
	public static Scene scene1() {
		Shape plainShape = new Plain(0, -0.5, -1, -3.5);
		Material plainMat = new Material()
				.initKa(new Vec(1))
				.initKd1(new Vec(0, 1, 1))
				.initKd2(new Vec(0, 0.5, 0.5))
				.initKs(new Vec(0.7))
				.initReflectionIntensity(0.3)
				.initIsCheckerBoard(true)
				.initShininess(10);
		Surface plainSurface = new Surface(plainShape, plainMat);

		Shape sphereShape1 = new Sphere()
				.initCenter(new Point(-0.7, -0.7, -2))
				.initRadius(0.5);
		Material sphereMat1 = new Material()
				.initKa(new Vec(1))
				.initKd1(new Vec(1, 0, 0))
				.initKs(new Vec(0.7))
				.initReflectionIntensity(0.95)
				.initShininess(10);
		Surface sphereSurface1 = new Surface(sphereShape1, sphereMat1);
		
		Shape sphereShape2 = new Sphere()
				.initCenter(new Point(0.6, -0.5, -1))
				.initRadius(0.5);
		Material sphereMat2 = new Material()
				.initKa(new Vec(1))
				.initKd1(new Vec(0.6, 0, 0.8))
				.initKs(new Vec(1))
				.initReflectionIntensity(0.005)
				.initRefractionIndex(1.5)
				.initRefractionIntensity(0.945)
				.initKt(new Vec(1))
				.initIsTransparent(true)
				.initShininess(10);
		Surface sphereSurface2 = new Surface(sphereShape2, sphereMat2);
		
		Light spotLight = new Spotlight()
				.initPosition(new Point(2, 1, 3))
				.initDirection(new Vec(0.5, 0, -1))
				.initIntensity(new Vec(0.2, 0.5, 0.7))
				.initAngle(0.6);
		
		Light dirLight = new DirectionalLight()
				.initDirection(new Vec(0, 0.5, -1))
				.initIntensity(new Vec(0.7, 0.5, 0));

		return new Scene()
				.initAmbient(new Vec(0.1, 0.2, 0.3))
				.initCamera(new Point(0, 0, 4))
				.addLightSource(dirLight)
				.addLightSource(spotLight)
				.addSurface(plainSurface)
				.addSurface(sphereSurface1)
				.addSurface(sphereSurface2)
				.initName("scene1")
				.initAntiAliasingFactor(3);
	}
	
	public static Scene scene2() {
		Shape plainShape1 = new Plain(1, 0, -0.1, -3);
		Material plainMat1 = new Material()
				.initKa(new Vec(1))
				.initKd1(new Vec(0.6, 0, 0.8))
				.initKd2(new Vec(0.3, 0, 0.4))
				.initKs(new Vec(0.7))
				.initReflectionIntensity(0.3)
				.initIsCheckerBoard(true)
				.initShininess(20);
		Surface plainSurface1 = new Surface(plainShape1, plainMat1);
		
		Shape plainShape2 = new Plain(0, 0, -1, -3.5);
		Material plainMat2 = new Material()
				.initKa(new Vec(1))
				.initKd1(new Vec(0.7, 0.7, 0))
				.initKd2(new Vec(0.35, 0.35, 0))
				.initKs(new Vec(0.7))
				.initReflectionIntensity(0.3)
				.initIsCheckerBoard(true)
				.initShininess(10);
		Surface plainSurface2 = new Surface(plainShape2, plainMat2);
		
		Shape plainShape3 = new Plain(-1, 0, -0.1, -3);
		Material plainMat3 = new Material()
				.initKa(new Vec(1))
				.initKd1(new Vec(0, 0.9, 0.5))
				.initKd2(new Vec(0, 0.45, 0.25))
				.initKs(new Vec(0.7))
				.initReflectionIntensity(0.3)
				.initIsCheckerBoard(true)
				.initShininess(15);
		Surface plainSurface3 = new Surface(plainShape3, plainMat3);

		Shape plainShape4 = new Plain(0, 1, -0.1, -3);
		Material plainMat4 = new Material()
				.initKa(new Vec(1))
				.initKd1(new Vec(0, 0.8, 0.8))
				.initKd2(new Vec(0, 0.4, 0.4))
				.initKs(new Vec(0.7))
				.initReflectionIntensity(0.3)
				.initIsCheckerBoard(true)
				.initShininess(10);
		Surface plainSurface4 = new Surface(plainShape4, plainMat4);
		
		Shape plainShape5 = new Plain(0, -1, -0.1, -3);
		Material plainMat5 = new Material()
				.initKa(new Vec(1))
				.initKd1(new Vec(0.9, 0, 0.1))
				.initKd2(new Vec(0.45, 0, 0.05))
				.initKs(new Vec(0.7))
				.initReflectionIntensity(0.3)
				.initIsCheckerBoard(true)
				.initShininess(15);
		Surface plainSurface5 = new Surface(plainShape5, plainMat5);
		
		Light spotlight1 = new Spotlight()
				.initAngle(0.8)
				.initIntensity(new Vec(0.3, 0.9, 0.2))
				.initPosition(new Point())
				.initDirection(new Vec(0, 0.5, -1));
		
		Light spotlight2 = new Spotlight()
				.initAngle(0.9)
				.initIntensity(new Vec(0.9, 0.5, 0.5))
				.initPosition(new Point())
				.initDirection(new Vec(0.5, 0, -1));
		
		Light spotlight3 = new Spotlight()
				.initAngle(0.7)
				.initIntensity(new Vec(0.3, 0.5, 0.9))
				.initPosition(new Point(-0.2, 0, 0))
				.initDirection(new Vec(-0.4, -0.3, -1));

		return new Scene()
				.addLightSource(spotlight1)
				.addLightSource(spotlight2)
				.addLightSource(spotlight3)
				.addSurface(plainSurface1)
				.addSurface(plainSurface2)
				.addSurface(plainSurface3)
				.addSurface(plainSurface4)
				.addSurface(plainSurface5)
				.initAmbient(new Vec(0.2, 0.1, 0))
				.initCamera(new Point(0, 0, 1))
				.initName("scene2")
				.initAntiAliasingFactor(3);
	}
	
	public static Scene scene3() {
		return scene1()
				.initName("scene3")
				.initRenderRefarctions(true)
				.initRenderReflections(true)
				.initMaxRecursionLevel(6);
	}
	
	public static Scene scene4() {
		return scene2()
				.initName("scene4")
				.initRenderRefarctions(true)
				.initRenderReflections(true)
				.initMaxRecursionLevel(6);
	}
	
	public static Scene scene5() {
		Shape plainShape = new Plain();
		Material plainMat = new Material()
				.initKa(new Vec(0.05))
				.initKd1(new Vec(1))
				.initKd2(new Vec())
				.initKs(new Vec(1))
				.initReflectionIntensity(0.3)
				.initIsCheckerBoard(true)
				.initShininess(20);
		Surface plainSurface = new Surface(plainShape, plainMat);

		Shape sphereShape = new Sphere();
		Material sphereMat = new Material()
				.initKa(new Vec(0.05))
				.initKd1(new Vec())
				.initKs(new Vec(1))
				.initReflectionIntensity(0.95)
				.initShininess(100);

		Surface sphereSurface = new Surface(sphereShape, sphereMat);

		Light dirLight = new DirectionalLight();

		return new Scene()
				.initName("scene5")
				.initMaxRecursionLevel(6)
				.initAntiAliasingFactor(3)
				.initRenderReflections(true)
				.addLightSource(dirLight)
				.addSurface(plainSurface)
				.addSurface(sphereSurface);
	}
	
	public static Scene scene6() {
		Shape plainShape = new Plain();
		Material plainMat = new Material()
				.initKa(new Vec(0.05))
				.initKd1(new Vec(1))
				.initKd2(new Vec())
				.initKs(new Vec(1))
				.initReflectionIntensity(0.3)
				.initIsCheckerBoard(true)
				.initShininess(20);
		Surface plainSurface = new Surface(plainShape, plainMat);

		Shape sphereShape1 = new Sphere()
				.initCenter(new Point(-0.4, -0.5, -8));
		Shape sphereShape2 = new Sphere()
				.initCenter(new Point(0, -0.5, -6));
		Material sphereMat1 = new Material()
				.initKa(new Vec(0.05))
				.initKd1(new Vec())
				.initKs(new Vec(1))
				.initReflectionIntensity(0.95)
				.initShininess(100);

		Material sphereMat2 = new Material()
				.initKa(new Vec(0.05))
				.initKd1(new Vec())
				.initKs(new Vec(1))
				.initReflectionIntensity(0.005)
				.initRefractionIndex(1.5)
				.initRefractionIntensity(0.945)
				.initKt(new Vec(1))
				.initIsTransparent(true)
				.initShininess(100);
		Surface sphereSurface1 = new Surface(sphereShape1, sphereMat1);
		Surface sphereSurface2 = new Surface(sphereShape2, sphereMat2);
		Light dirLight = new DirectionalLight()
				.initDirection(new Vec(-1));

		return new Scene()
				.initName("scene6")
				.initAntiAliasingFactor(3)
				.addLightSource(dirLight)
				.addSurface(plainSurface)
				.addSurface(sphereSurface1)
				.addSurface(sphereSurface2)
				.initRenderRefarctions(true)
				.initRenderReflections(true)
				.initMaxRecursionLevel(6);
	}

	public static Scene scene7() {
		Shape plainShape = new Plain();
		Material plainMat = new Material()
				.initKa(new Vec(0.05))
				.initKd1(new Vec(1))
				.initKd2(new Vec())
				.initKs(new Vec(1))
				.initReflectionIntensity(0.1)
				.initIsCheckerBoard(true)
				.initShininess(20);
		Surface plainSurface = new Surface(plainShape, plainMat);

		Shape sphereShape = new Sphere()
				.initCenter(new Point(-0.4, -0.5, -8));
		
		Material sphereMat = new Material()
				.initKa(new Vec(0.05))
				.initKd1(new Vec())
				.initKs(new Vec(1))
				.initReflectionIntensity(0.95)
				.initShininess(100);
		
		Shape domeShape = new Dome();
		Material domeMat = new Material()
				.initKa(new Vec(0.05))
				.initKd1(new Vec())
				.initKs(new Vec(1))
				.initReflectionIntensity(0.005)
				.initRefractionIndex(1.5)
				.initRefractionIntensity(0.945)
				.initKt(new Vec(1, 0.3, 0.3))
				.initIsTransparent(true)
				.initShininess(100);
		Surface sphereSurface = new Surface(sphereShape, sphereMat);
		Surface domeSurface = new Surface(domeShape, domeMat);
		Light dirLight = new DirectionalLight()
				.initDirection(new Vec(-1));

		return new Scene()
				.initName("scene7")
				.initAntiAliasingFactor(3)
				.addLightSource(dirLight)
				.addSurface(plainSurface)
				.addSurface(sphereSurface)
				.addSurface(domeSurface)
				.initRenderRefarctions(true)
				.initRenderReflections(true)
				.initMaxRecursionLevel(6);
	}
}

package edu.cg.scene.objects;

import edu.cg.algebra.Vec;

public class Material {
	public Vec Ka = new Vec(0.1, 0.1, 0.1); //ambient coefficient
	public Vec Kd1 = new Vec(1, 1, 1); //diffuse coefficient 1
	public Vec Kd2 = new Vec(0, 0, 0); //diffuse coefficient 2
	public boolean isCheckerBoard = false;
	public Vec Ks = new Vec(0.7, 0.7, 0.7); //specular & reflection coefficient
	public double reflectionIntensity = 0.3;
	public int shininess = 10; //shine factor - for specular calculation
	
	
	//Bonus
	public boolean isTransparent = false;
	public Vec Kt = new Vec(1, 1, 1); //transparency coefficient - for refraction bonus
	public double refractionIntensity = 0;
	public double refractionIndex = 1.5; //refraction index - bonus
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Ka: " + Ka + endl + 
				"Kd1: " + Kd1 + endl +
				"Kd2: " + Kd2 + endl +
				"isCheckerBoard: " + isCheckerBoard + endl +
				"Ks: " + Ks + endl + 
				"Reflection Intensity: " + reflectionIntensity + endl +
				"Shininess: " + shininess + endl + 
				"isTransparent: " + isTransparent + endl +
				"Kt: " + Kt + endl +
				"Refraction Intensity: " + refractionIntensity + endl +
				"Refraction Index: " + refractionIndex + endl;
	}
	
	public Material initKa(Vec Ka) {
		this.Ka = Ka;
		return this;
	}
	
	public Material initKd1(Vec Kd1) {
		this.Kd1 = Kd1;
		return this;
	}
	
	public Material initKd2(Vec Kd2) {
		this.Kd2 = Kd2;
		return this;
	}
	
	public Material initIsCheckerBoard(boolean isCheckerBoard) {
		this.isCheckerBoard = isCheckerBoard;
		return this;
	}
	
	public Material initKs(Vec Ks) {
		this.Ks = Ks;
		return this;
	}
	
	public Material initReflectionIntensity(double reflectionIntensity) {
		this.reflectionIntensity = reflectionIntensity;
		return this;
	}
	
	public Material initShininess(int shininess) {
		this.shininess = shininess;
		return this;
	}
	
	public Material initKt(Vec Kt) {
		this.Kt = Kt;
		return this;
	}
	
	public Material initRefractionIntensity(double refractionIntensity) {
		this.refractionIntensity = refractionIntensity;
		return this;
	}
	
	public Material initRefractionIndex(double refractionIndex) {
		this.refractionIndex = refractionIndex;
		return this;
	}
	
	public Material initIsTransparent(boolean isTransparent) {
		this.isTransparent = isTransparent;
		return this;
	}
}

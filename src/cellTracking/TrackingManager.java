package cellTracking;

import java.util.ArrayList;

import ij.ImagePlus;
import ij.gui.Roi;

public class TrackingManager {

	private ArrayList<ArrayList<Roi>> detectionRois;
    private int stackSize;
    private ImagePlus imageArray[];
    private double deathProb;
    private double deathProbMat[][];
    private double mitoProb;
    private double mitoProbMat[][];
    private double migProb;
    private double migProbMat[][];
    private double appearProb;
    private double appearProbMat[][];
    private double disappearProb;
    private double disappearProbMat[][];
    
    
	public ArrayList<ArrayList<Roi>> getDetectionRois() {
	
		return detectionRois;
		
	}
	
	public void setDetectionRois(ArrayList<ArrayList<Roi>> detectionRois) {
		
		this.detectionRois = detectionRois;
		
	}
	
	public ImagePlus[] getImageArray() {
		
		return imageArray;

	}
	
	public void setImageArray(ImagePlus imageArray[]) {
	
		this.imageArray = imageArray;

	}
	
	public int getStackSize() {
	
		return stackSize;
	
	}
	public void setStackSize(int stackSize) {
	
		this.stackSize = stackSize;
	
	}

	public double getDeathProb() {
		return deathProb;
	}

	public void setDeathProb(double deathProb) {
		this.deathProb = deathProb;
	}

	public double[][] getDeathProbMat() {
		return deathProbMat;
	}

	public void setDeathProbMat(double deathProbMat[][]) {
		this.deathProbMat = deathProbMat;
	}

	public double getMitoProb() {
		return mitoProb;
	}

	public void setMitoProb(double mitoProb) {
		this.mitoProb = mitoProb;
	}

	public double[][] getMitoProbMat() {
		return mitoProbMat;
	}

	public void setMitoProbMat(double mitoProbMat[][]) {
		this.mitoProbMat = mitoProbMat;
	}

	public double getMigProb() {
		return migProb;
	}

	public void setMigProb(double migProb) {
		this.migProb = migProb;
	}

	public double[][] getMigProbMat() {
		return migProbMat;
	}

	public void setMigProbMat(double migProbMat[][]) {
		this.migProbMat = migProbMat;
	}

	public double getAppearProb() {
		return appearProb;
	}

	public void setAppearProb(double appearProb) {
		this.appearProb = appearProb;
	}

	public double[][] getAppearProbMat() {
		return appearProbMat;
	}

	public void setAppearProbMat(double appearProbMat[][]) {
		this.appearProbMat = appearProbMat;
	}

	public double getDisappearProb() {
		return disappearProb;
	}

	public void setDisappearProb(double disappearProb) {
		this.disappearProb = disappearProb;
	}

	public double[][] getDisappearProbMat() {
		return disappearProbMat;
	}

	public void setDisappearProbMat(double disappearProbMat[][]) {
		this.disappearProbMat = disappearProbMat;
	}
    
}

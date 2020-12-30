package activeSegmentation.filter;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.gui.DialogListener;
import ij.measure.Calibration;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ijaux.datatype.IComplexFArray;
import ijaux.scale.*;

import java.awt.*;
import java.util.*;
import java.util.List;

import static activeSegmentation.FilterType.SEGM;
import static java.lang.Math.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import activeSegmentation.AFilter;
import activeSegmentation.AFilterField;
import activeSegmentation.IFilter;
import activeSegmentation.IFilterViz;
import dsp.Conv;
import fftscale.FFTConvolver;
import fftscale.filter.FFTKernelLoG;

/**
 * @version 	30 Dec 2020

 * 				
 *   
 * 
 * @author Dimiter Prodanov, IMEC , Sumit Kumar Vohra , KULeuven
 *
 *
 * @contents
 * The plugin computes the eigenvalues of the Structure matrix
 * 
 * 
 * @license This library is free software; you can redistribute it and/or
 *      modify it under the terms of the GNU Lesser General Public
 *      License as published by the Free Software Foundation; either
 *      version 2.1 of the License, or (at your option) any later version.
 *
 *      This library is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *       Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public
 *      License along with this library; if not, write to the Free Software
 *      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

@AFilter(key="STRUCTURE", value="Structure tensor", type=SEGM)
public class StructureT_Filter_ implements ExtendedPlugInFilter, DialogListener, IFilter, IFilterViz {
    @SuppressWarnings("unused")

	private PlugInFilterRunner pfr=null;

	final int flags=DOES_ALL+KEEP_PREVIEW+ NO_CHANGES;
	private String version="1.0";
    @SuppressWarnings("unused")

	private int nPasses=1;
	private int pass;

	public final static String SIGMA="LOG_sigma",MAX_LEN="G_MAX",FULL_OUTPUT="Full_out",LEN="G_len";

	private boolean isEnabled=true;

	private float[][] kernel=null;

	private ImagePlus image=null;
	public static boolean debug=IJ.debugMode;

	public boolean fulloutput=false;

	private boolean isFloat=false;
    @SuppressWarnings("unused")

	private boolean hasRoi=false;

	@AFilterField(key=LEN, value="initial scale")
	public int sz= Prefs.getInt(LEN, 2);
	
	@AFilterField(key=MAX_LEN, value="max scale")
	public int max_sz= Prefs.getInt(MAX_LEN, 8);

	/* NEW VARIABLES*/

	/** A string key identifying this factory. */
	private final  String FILTER_KEY = "STRUCTURE";

	/** The pretty name of the target detector. */
	private final String FILTER_NAME = "Strcuture components";
	
  	
	/** It stores the settings of the Filter. */
	private Map< String, String > settings= new HashMap<>();
	
	/** It is the result stack*/
	private ImageStack imageStack;

	/**
	 * This method is to setup the PlugInFilter using image stored in ImagePlus 
	 * and arguments of filter
	 */
	@Override
	public int setup(String arg, ImagePlus imp) {
		image=imp;
		isFloat= (image.getType()==ImagePlus.GRAY32);
		hasRoi=imp.getRoi()!=null;
		cal=image.getCalibration();
		return  flags;
	}

	final int Ox=0, Oy=1, Oz=2;

	// It is used to check whether to calibirate or not
	private boolean doCalib = false;
	/*
	 * This variable is to calibrate the Image Window
	 */
	private Calibration cal=null;
	
	public void initialseimageStack(ImageStack img){
		this.imageStack = img;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
	 */
	@Override
	public void run(ImageProcessor ip) {
		int r = (sz-1)/2;
		GScaleSpace sp=new GScaleSpace(r);


		imageStack=new ImageStack(ip.getWidth(),ip.getHeight());

		imageStack = filter(ip,sp,sz,imageStack);


		image=new ImagePlus("ALoG result hw="+((sz-1)/2),imageStack);
		image.show();
	}

	
	
	@Override
	public void applyFilter(ImageProcessor image, String filterPath,List<Roi> roiList) {

			for (int sigma=sz; sigma<= max_sz; sigma *=2){		
				ImageStack imageStack=new ImageStack(image.getWidth(),image.getHeight());
				GScaleSpace sp=new GScaleSpace(sigma);
				imageStack=filter(image, sp,sigma, imageStack);
				for(int j=1;j<=imageStack.getSize();j++){
					String imageName=filterPath+"/"+imageStack.getSliceLabel(j)+".tif" ;
					IJ.save(new ImagePlus(imageStack.getSliceLabel(j), imageStack.getProcessor(j)),imageName );
				}

			}

	}


	
	/**
	 * 
	 * This method is helper function for both applyFilter and run method
	 * @param ip input image
	 * @param sp gaussian scale space
	 * @param sigma filter sigma
	 */
	private ImageStack filter(ImageProcessor ip,GScaleSpace sp, float sigma, ImageStack imageStack){

		ip.snapshot();

		if (!isFloat) 
			ip=ip.toFloat(0, null);

		pass++;
		//System.out.println(settings.get(LEN)+"MG");
		//GScaleSpace sp=new GScaleSpace(sigma);
		float[] kernx= sp.gauss1D();
		//System.out.println("kernx :"+kernx.length);
		GScaleSpace.flip(kernx);	

		float[] kern_diff1=sp.diffGauss1D();
		//System.out.println("kernx1:"+kern_diff1.length);
		GScaleSpace.flip(kern_diff1);
		
		//float[] kern_diff2= sp.diff2Gauss1D();
		//GScaleSpace.flip(kern_diff2);
		//System.out.println("kernx2 :"+kern_diff2.length);

		kernel=new float[4][];
		kernel[0]=kernx;
		//kernel[1]=kern_diff2;
		kernel[1]=kern_diff1;

		//float[] kernel2=sp.computeDiff2Kernel2D();
		//kernel[3]=kernel2;
	//	GScaleSpace.flip(kernel2);  // symmetric but this is the correct way

		int sz= sp.getSize();
		if (debug && pass==1) {
			FloatProcessor fpkern2=new FloatProcessor(sz,sz);

			float[][] disp= new float[2][];

			disp[0]=GScaleSpace.joinXY(kernel, 0, 1);
			disp[1]=GScaleSpace.joinXY(kernel, 1, 0);

			for (int i=0; i<sz*sz; i++)
				fpkern2.setf(i, disp[0][i]+ disp[1][i]);

			new ImagePlus("kernel sep",fpkern2).show();


		}

		FloatProcessor fpaux= (FloatProcessor) ip;

		Conv cnv=new Conv();

		FloatProcessor gradx=(FloatProcessor) fpaux.duplicate();
		FloatProcessor grady=(FloatProcessor) fpaux.duplicate();
		
		/*
		FloatProcessor lap_xx=(FloatProcessor) fpaux.duplicate();
		FloatProcessor lap_yy=(FloatProcessor) fpaux.duplicate();
		FloatProcessor lap_xy=(FloatProcessor) fpaux.duplicate();
		 */
		cnv.convolveFloat1D(gradx, kern_diff1, Ox);
		cnv.convolveFloat1D(gradx, kernx, Oy);

		cnv.convolveFloat1D(grady, kern_diff1, Oy);
		cnv.convolveFloat1D(grady, kernx, Ox);
/*
		cnv.convolveFloat1D(lap_xx, kern_diff2, Ox);
		cnv.convolveFloat1D(lap_xx, kernx, Oy);

		cnv.convolveFloat1D(lap_yy, kern_diff2, Oy);
		cnv.convolveFloat1D(lap_yy, kernx, Ox);

		cnv.convolveFloat1D(lap_xy, kern_diff1, Oy);
		cnv.convolveFloat1D(lap_xy, kern_diff1, Ox);
		*/
		int width=ip.getWidth();
		int height=ip.getHeight();

//		FloatProcessor lap_t=new FloatProcessor(width, height); // tangential component
//		FloatProcessor lap_o=new FloatProcessor(width, height); // orthogonal component
		
		FloatProcessor pamp=new FloatProcessor(width, height); // amplitude of gradient
		FloatProcessor phase=new FloatProcessor(width, height); // phase of gradient
		
		FloatProcessor eigen1=new FloatProcessor(width, height); // eigenvalue 1
		FloatProcessor eigen2=new FloatProcessor(width, height); // eigenvalue 2
		FloatProcessor coher=new FloatProcessor(width, height); // coherence 

		for (int i=0; i<width*height; i++) {
			double gx=gradx.getf(i);
			double gy=grady.getf(i);

			/*
			 *  components of the Structure Tensor
			 */
 
			
			final double trace=gx*gx+gy*gy;
			final double det=gx*gx*gy*gy- gx*gy*gx*gy;
			final double disc= sqrt(abs(trace*trace-4.0*det));
			final double ee1=0.5*(trace+disc);
			final double ee2=0.5*(trace-disc);

			final double l1=max(ee1, ee2);
			final double l2=min(ee1, ee2);
		    double coh=0;
			if (l1+l2>0) 
			   coh=(l1-l2)/(l1+l2);
			
			double amp=(gx+gy)+ 1e-6;
			
 
			pamp.setf(i, (float) sqrt(amp));
			
			double phase1=sqrt(gy/amp);
				//	phase1=asin(phase1);
			phase.setf(i, (float) phase1);

			eigen1.setf(i, (float) l1);
			eigen2.setf(i, (float) l2);
			coher.setf(i, (float) coh);
		}

		if (fulloutput) {
			imageStack.addSlice(FILTER_KEY+"X_diff"+sigma, gradx);
			imageStack.addSlice(FILTER_KEY+"Y_diff"+sigma, grady);
		}

		imageStack.addSlice(FILTER_KEY+"Amp"+sigma, pamp);
		imageStack.addSlice(FILTER_KEY+"Phase"+sigma, phase);
		imageStack.addSlice(FILTER_KEY+"Coh"+sigma, phase);
		imageStack.addSlice(FILTER_KEY+"E2"+sigma, eigen2);
		imageStack.addSlice(FILTER_KEY+"E1"+sigma, eigen1);
 
		eigen2.resetMinAndMax();
 
		return imageStack;
	}



	/**
	 * @param i
	 * @return
	 */
	public float[] getKernel(int i) {
		return kernel[i];
	}

	/* (non-Javadoc)
	 * @see ij.plugin.filter.ExtendedPlugInFilter#showDialog(ij.ImagePlus, java.lang.String, ij.plugin.filter.PlugInFilterRunner)
	 */
	@Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		this.pfr = pfr;
		int r = (sz-1)/2;
		GenericDialog gd=new GenericDialog("Structure Tensor " + version);

		gd.addNumericField("half width", r, 2);
		//gd.addNumericField("sigma", sigma, 1);
		gd.addCheckbox("Show kernel", debug);
		gd.addCheckbox("Full output", fulloutput);	
		if (cal!=null) {
			if (!cal.getUnit().equals("pixel"))
				gd.addCheckbox("units ( "+cal.getUnit() + " )", doCalib); 
		}		

		gd.addPreviewCheckbox(pfr);
		gd.addDialogListener(this);
		gd.setResizable(false);
		gd.showDialog();

		//pixundo=imp.getProcessor().getPixelsCopy();
		if (gd.wasCanceled()) {			
			return DONE;
		}

		return IJ.setupDialog(imp, flags);
	}




	// Called after modifications to the dialog. Returns true if valid input.
	/* (non-Javadoc)
	 * @see ij.gui.DialogListener#dialogItemChanged(ij.gui.GenericDialog, java.awt.AWTEvent)
	 */
	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		double r = (int)(gd.getNextNumber());
		//sigma = (float) (gd.getNextNumber());
		debug = gd.getNextBoolean();
		fulloutput = gd.getNextBoolean();
		if (cal!=null)
			doCalib=gd.getNextBoolean();

		if (doCalib) {
			r= (r/cal.pixelWidth);
		}
		sz =  (2*(int)r+1);
		if (gd.wasCanceled()) {

			return false;
		}
		return r>0;
		//return sigma>0;
	}


	/* (non-Javadoc)
	 * @see ij.plugin.filter.ExtendedPlugInFilter#setNPasses(int)
	 */
	@Override
	public void setNPasses (int nPasses) {
		this.nPasses = nPasses;
	}

	/* Saves the current settings of the plugin for further use
	 * 
	 *
	 * @param prefs
	 */
	public void savePreferences(Properties prefs) {
		prefs.put(LEN, Integer.toString(sz));
		// prefs.put(SIGMA, Float.toString(sigma));

	}

	@Override
	public Map<String, String> getDefaultSettings() {

		settings.put(LEN, Integer.toString(sz));
		settings.put(MAX_LEN, Integer.toString(max_sz));
		settings.put(FULL_OUTPUT, Boolean.toString(fulloutput));

		return this.settings;
	}

	@Override
	public boolean reset() {
		sz= Prefs.getInt(LEN, 2);
		max_sz= Prefs.getInt(MAX_LEN, 8);
		fulloutput= Prefs.getBoolean(FULL_OUTPUT, true);
		return true;
	}

	@Override
	public boolean updateSettings(Map<String, String> settingsMap) {
		sz=Integer.parseInt(settingsMap.get(LEN));
		max_sz=Integer.parseInt(settingsMap.get(MAX_LEN));
		fulloutput= Boolean.parseBoolean(settingsMap.get(FULL_OUTPUT));
		
		return true;
	}

	@Override
	public String getKey() {
		return this.FILTER_KEY;
	}
 

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		this.isEnabled= isEnabled;
	}
	
	@Override
	public String getName() {
		return this.FILTER_NAME;
	}
	

	private double logKernel(double x){
		final double x2=x*x;
		return -(x)* exp(-0.5*x2)/(2.0*sqrt(PI));
	}
	
	@Override
	public double[][] kernelData() {
		final int n=40;
		double [][] data=new double[2][n];
		data[0]=SUtils.linspace(-10.0, 10.0, n);
		for(int i=0; i<n; i++){
			data[1][i]=logKernel(data[0][i]);
		}
		return data;
	}

	public static void main (String[] args) {
		StructureT_Filter_ filter=new StructureT_Filter_();
		System.out.println("annotated fields");
		System.out.println(filter.getAnotatedFileds());
		
	 


		//new ImagePlus("convovled",output).show();
	 
		
	}


}

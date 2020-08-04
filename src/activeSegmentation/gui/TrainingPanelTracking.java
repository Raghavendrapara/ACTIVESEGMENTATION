package activeSegmentation.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.TextRoi;
import ij.gui.Wand;
import ij.io.RoiEncoder;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.process.LUT;
import ijaux.datatype.Pair;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import activeSegmentation.ASCommon;
import activeSegmentation.EventType;
import activeSegmentation.LearningType;
import activeSegmentation.ProjectType;
import activeSegmentation.feature.FeatureManager;
import activeSegmentation.feature.GroundTruthExtractor;
import activeSegmentation.prj.ProjectManager;
import activeSegmentation.util.GuiUtil;

import static  activeSegmentation.ProjectType.*;

public class TrainingPanelTracking extends ImageWindow implements ASCommon  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FeatureManager featureManager;
	/** opacity (in %) of the result overlay image */
	int overlayOpacity = 33;
	/** alpha composite for the result overlay image */
	Composite overlayAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayOpacity / 100f);
	private ImageOverlay resultOverlay;
	LUT overlayLUT;
	/** flag to display the overlay image */
	private boolean showColorOverlay=false;
	ImagePlus classifiedImage;
	// Create overlay LUT
	byte[] red = new byte[ 256 ];
	byte[] green = new byte[ 256 ];
	byte[] blue = new byte[ 256 ];

	private Vector<String> templist=new Vector<>();
	private Vector<String> templistFrame=new Vector<>();
	public static final int  largeframeWidth=1200;
	public static final int  largeframeHight=1000;
	
	public static final int IMAGE_CANVAS_DIMENSION = 700; //same width and height	

	/** array of ROI list overlays to paint the transparent ROIs of each class */
	private Map<String,RoiListOverlay> roiOverlayList;

	HashMap<String,Roi> roiNameMap=new HashMap<>();
	HashMap<String,Roi> roiNameMapAll=new HashMap<>();
	HashMap<String,JList<String>> listRoi=new HashMap<>();
	JList<String> list;
	JList<String> listCurrent;
	HashSet<String> roiAlreadyLabelled=new HashSet<>(); 
	DefaultListModel<String> dl=new DefaultListModel<>();
	/** Used only during classification setting*/
	private Map<String,Integer> predictionResultClassification;

	final Composite transparency050 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f );
	
	/*
	 *  the files must be in the resources/feature folder
	 */
	private static final Icon uploadIcon = new ImageIcon(TrainingPanelTracking.class.getResource("upload.png"));
	private static final Icon downloadIcon = new ImageIcon(TrainingPanelTracking.class.getResource("download.png"));
 

	/** This {@link ActionEvent} is fired when the 'next' button is pressed. */
	private ActionEvent NEXT_BUTTON_PRESSED = new ActionEvent( this, 0, "Next" );
	/** This {@link ActionEvent} is fired when the 'previous' button is pressed. */
	private ActionEvent PREVIOUS_BUTTON_PRESSED = new ActionEvent( this, 1, "Previous" );
	private ActionEvent ADDCLASS_BUTTON_PRESSED = new ActionEvent( this, 2, "AddClass" );
	private ActionEvent SAVECLASS_BUTTON_PRESSED= new ActionEvent( this, 3, "SaveLabel" );
	private ActionEvent DELETE_BUTTON_PRESSED = new ActionEvent( this, 4, "DeleteClass" );
	private ActionEvent COMPUTE_BUTTON_PRESSED  = new ActionEvent( this, 5, "TRAIN" );
	private ActionEvent SAVE_BUTTON_PRESSED  = new ActionEvent( this, 6, "SAVEDATA" );
	private ActionEvent TOGGLE_BUTTON_PRESSED = new ActionEvent( this, 7, "TOGGLE" );
	private ActionEvent DOWNLOAD_BUTTON_PRESSED = new ActionEvent( this, 8, "DOWNLOAD" );
	private ActionEvent MASKS_BUTTON_PRESSED = new ActionEvent( this, 9, "MASKS" );
	private ActionEvent MITOSIS_BUTTON_PRESSED = new ActionEvent( this, 10, "MITOSIS" );
	private ActionEvent APOPTOSIS_BUTTON_PRESSED = new ActionEvent( this, 11, "APOPTOSIS" );
	private ActionEvent CLUSTERCOUNT_BUTTON_PRESSED = new ActionEvent( this, 12, "CLUSTERCOUNT" );
	private ActionEvent PROB_BUTTON_PRESSED = new ActionEvent( this, 13, "PROBABILITIES" );
	private ImagePlus displayImage;
	private ImagePlus prvImage;
	private ImagePlus nxtImage;
	/** Used only in classification setting, in segmentation we get from feature manager*/
	//private ImagePlus tempClassifiedImage;
	private JPanel imagePanel,classPanel,roiPanel,currentRoiPanel;
	private JTextField imageNum;
	private JLabel total;
	private List<JCheckBox> jCheckBoxList;
	private Map<String,JTextArea> jTextList;
	private JComboBox<LearningType> learningType;
	private JComboBox<EventType> eventType;
	private JFrame frame;
    private JPanel nxt;
    private JPanel prv;
    private ArrayList<Roi> CurrentTrackFrameRoiList;
    HashMap<Roi,Pair<Integer,String>> roiMap;
	/*
	 * constructor 
	 */
	public TrainingPanelTracking(FeatureManager featureManager)  {		
		super(featureManager.getCurrentImage());
		this.featureManager = featureManager;
		this.displayImage= featureManager.getCurrentImage();
		this.jCheckBoxList= new ArrayList<JCheckBox>();
		this.jTextList= new HashMap<String,JTextArea>();
		
		roiOverlayList = new HashMap<String, RoiListOverlay>();
		//tempClassifiedImage = new ImagePlus();		
		this.setVisible(false);
		this.CurrentTrackFrameRoiList=GroundTruthExtractor.runextracter(displayImage,featureManager.getRoiMan(),1,255,0);
		featureManager.addMigration(CurrentTrackFrameRoiList.size());
		resetRois();
		showPanel();
	}



	public void showPanel() {
		frame = new JFrame("Train");	     
		
		frame.setResizable(false);
 		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		JList<String> frameList= GuiUtil.model();
		frameList.setForeground(Color.BLACK);
		
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setFont(panelFONT);
		panel.setBackground(Color.GRAY);
		
		imagePanel      = new JPanel();	
		roiPanel        = new JPanel();
		classPanel      = new JPanel();
		currentRoiPanel = new JPanel();

		/*
		 * image panel
		 */
		imagePanel.setLayout(new BorderLayout());
		
		ic=new SimpleCanvas(featureManager.getCurrentImage());
		ic.setMinimumSize(new Dimension(IMAGE_CANVAS_DIMENSION, IMAGE_CANVAS_DIMENSION));
		ic.addMouseListener(mouseListener);
		loadImage(displayImage);
		setOverlay();
		imagePanel.setBackground(Color.GRAY);		
		imagePanel.add(ic,BorderLayout.EAST);
		imagePanel.setBounds( 10, 10, ic.getWidth(),ic.getHeight() );		
		panel.add(imagePanel);
		
		
		
		nxt=new JPanel();
		nxt.setLayout(new BorderLayout());
		nxtImage=featureManager.getNextImageTrack();
		Image imag=nxtImage.getImage();
		imag=imag.getScaledInstance(340, 260, Image.SCALE_SMOOTH);
		nxtImage=new ImagePlus("Next",imag);
		ImageCanvas temp=new ImageCanvas(nxtImage);
		nxt=new JPanel();
	    nxt.add(temp);
		nxt.setBounds(360, ic.getHeight()+30, 340, 260);
		panel.add(nxt); 
		
		prv=new JPanel();
		prvImage=featureManager.getCurrentImage();
		imag=prvImage.getImage();
		imag=imag.getScaledInstance(340, 260, Image.SCALE_SMOOTH);
		prvImage=new ImagePlus("Prev",imag);
		temp=new ImageCanvas(prvImage);
	    prv.add(temp);
		prv.setBounds(10, ic.getHeight()+30, 340, 260);
		panel.add(nxt);
	 	panel.add(prv);
		classPanel.setBounds(785,20,350,100);
		classPanel.setPreferredSize(new Dimension(350, 100));
		classPanel.setBorder(BorderFactory.createTitledBorder("Events"));
		
		addClassPanel();
		panel.add(classPanel);
		/*
		 * features
		 */
		JPanel features= new JPanel();
		features.setBounds(785,120,350,100);
		features.setBorder(BorderFactory.createTitledBorder("Move Frame"));
		
		addButton(new JButton(), "PREVIOUS",null , 795, 130, 120, 20,features,PREVIOUS_BUTTON_PRESSED,null );
		
		imageNum= new JTextField();
		imageNum.setColumns(5);
		imageNum.setBounds( 800, 130, 10, 20 );
		JLabel dasedLine= new JLabel("/");
		dasedLine.setFont(new Font( "Arial", Font.PLAIN, 15 ));
		dasedLine.setForeground(Color.BLACK);
		dasedLine.setBounds(  820, 130, 10, 20 );
		total= new JLabel("Total");
		total.setFont(new Font( "Arial", Font.PLAIN, 15 ));
		total.setForeground(Color.BLACK);
		total.setBounds( 800, 600, 80, 30);		
		imageNum.setText(Integer.toString(featureManager.getCurrentSlice()));
		total.setText(Integer.toString(featureManager.getTotalSlice()));
		features.add(imageNum);
		features.add(dasedLine);
		features.add(total);
		
		/*
		 * compute panel
		 */
		
		JPanel computePanel = new JPanel();
		addButton(new JButton(), "Next",null , 800, 130, 80, 20,features,NEXT_BUTTON_PRESSED,null );
	/*	addButton(new JButton(), "Train",null, 550,550,350,100,computePanel, COMPUTE_BUTTON_PRESSED,null);
		
		addButton(new JButton(), "Overlay",null, 550,550,350,100,computePanel, TOGGLE_BUTTON_PRESSED,null);
		addButton(new JButton(), "Masks",null, 550,550,350,100,computePanel, MASKS_BUTTON_PRESSED,null);
	*/	
		addButton(new JButton(), "GetProbabilities",null, 750,170,100,30,features, PROB_BUTTON_PRESSED,null);
		features.add(computePanel);
		frame.add(features);
		
		/*
		 *  Data panel
		 */
		
		
		/*
		 * ROI panel
		 */
		roiPanel.setBorder(BorderFactory.createTitledBorder("Regions Of Interest"));
		//roiPanel.setPreferredSize(new Dimension(350, 400));
		JScrollPane scrollPane = new JScrollPane(roiPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);	
		scrollPane.setBounds(805,300,350,250);
		panel.add(scrollPane);
	    

	    currentRoiPanel.setBorder(BorderFactory.createTitledBorder("Current Frame ROIs"));
		//roiPanel.setPreferredSize(new Dimension(350, 400));
		JScrollPane scrollPaneCurrent = new JScrollPane(currentRoiPanel);
		scrollPaneCurrent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);	
		scrollPaneCurrent.setBounds(805,600,250,200);
		panel.add(scrollPaneCurrent);
	    frame.add(panel);
		/*
		 *  frame code
		 */
		frame.pack();
		frame.setSize(largeframeWidth,largeframeHight);
		//frame.setSize(getMaximumSize());		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		updateGui();

	}

	private void addClassPanel(){
		classPanel.removeAll();
		roiPanel.removeAll();
		jCheckBoxList.clear();
		jTextList.clear();
		int classes=featureManager.getNumOfClasses();
		IJ.log(Integer.toString(classes));
		if(classes%3==0){
			int tempSize=classes/3;
			classPanel.setPreferredSize(new Dimension(340, 80+30*tempSize));	
		}
		roiPanel.setPreferredSize(new Dimension(350, 175*classes));
		addButton(new JButton(), "MITOSIS",null , 800, 20, 130, 20,classPanel,MITOSIS_BUTTON_PRESSED,null );
		addButton(new JButton(), "APOPTOSIS",null , 800, 20, 130, 20,classPanel,APOPTOSIS_BUTTON_PRESSED,null );
		addButton(new JButton(), "CLUSTERCOUNT",null , 800, 20, 130, 20,classPanel,CLUSTERCOUNT_BUTTON_PRESSED,null );
		dl.addElement("                ");
        for(Roi rr:CurrentTrackFrameRoiList) {
		dl.addElement(rr.getName());
		roiNameMapAll.put(rr.getName(), rr);
        }
		list=new JList<>(dl);
		listCurrent=new JList<>(dl);
		listRoi.put("Example",list);
		listRoi.put("ExampleCurrent",listCurrent);
	   
			addSidePanel("Example");
			addSidePanelCurrent("ExampleCurrent");
	
	}
    
	/**
	 * Draw the painted traces on the display image
	 */
	private void drawExamples(){
		for(String key: featureManager.getClassKeys()){
			ArrayList<Roi> rois=(ArrayList<Roi>) featureManager.
					getExamples(key,learningType.getSelectedItem().toString(), featureManager.getCurrentSlice());
			roiOverlayList.get(key).setColor(featureManager.getClassColor(key));
			roiOverlayList.get(key).setRoi(rois);
			//System.out.println("roi draw"+ key);
		}

		getImagePlus().updateAndDraw();
	}
	private void addSidePanel(String keynm){
		JPanel panel= new JPanel();
		JList<String> current=GuiUtil.model();

		current.setForeground(Color.WHITE);
	
	//	RoiListOverlay roiOverlay = new RoiListOverlay();
		//roiOverlay.setComposite( transparency050 );
	//	((OverlayedImageCanvas)ic).addOverlay(roiOverlay);
		JPanel buttonPanel= new JPanel();
		buttonPanel.setName(keynm);
		ActionEvent addbuttonAction= new ActionEvent(buttonPanel, 1,"AddButton");
		ActionEvent uploadAction= new ActionEvent(buttonPanel, 2,"UploadButton");
		ActionEvent downloadAction= new ActionEvent(buttonPanel, 3,"DownloadButton");
		JButton addButton= new JButton();
		addButton.setName("ADD");
		JButton upload= new JButton();
		upload.setName(keynm);
		JButton download= new JButton();
		download.setName(keynm);
		addButton(addButton, "ADD", null, 855,280,350,250, buttonPanel, addbuttonAction, null);
		addButton(upload, null, uploadIcon, 805,280,350,250, buttonPanel, uploadAction, null);
		addButton(download, null, downloadIcon, 805,280,350,250, buttonPanel, downloadAction, null);
		roiPanel.add(buttonPanel);
		String key=""+featureManager.getCurrentSlice();
		panel.add(GuiUtil.addScrollPanel(listRoi.get(keynm),null));
		panel.add(GuiUtil.addScrollPanel(listRoi.get(keynm),null));
		listRoi.get(keynm).addMouseListener(mouseListener1);
		roiPanel.add(panel );
		
	}

	private void addSidePanelCurrent(String keynm){
		JPanel panel= new JPanel();
		JList<String> current=GuiUtil.model();

		current.setForeground(Color.WHITE);
	
	
		String key=""+featureManager.getCurrentSlice();
		panel.add(GuiUtil.addScrollPanel(listRoi.get(keynm),null));
		panel.add(GuiUtil.addScrollPanel(listRoi.get(keynm),null));
		listRoi.get(keynm).addMouseListener(mouseListener2);
		currentRoiPanel.add(panel );
		
	}
	private void addAction(JButton button ,final  ActionEvent action){
		 button.addActionListener( new ActionListener()	{
			@Override
			public void actionPerformed( final ActionEvent e )	{
					doAction(action);
				
			}
		} );
	 
	}
	
	private void loadImage(ImagePlus image) {
		this.displayImage=image;
		setImage(this.displayImage);
		updateImage(this.displayImage);
	}

	
	
	public void validateFrame(){
		frame.invalidate();
		frame.revalidate();
		frame.repaint();
	}

	private void resetRois()
	{
		String key="Example";
		
        displayImage.killRoi();
			
		featureManager.getRoiMan().reset();
		
	}
	public void doAction( final ActionEvent event ) {
		if(event== ADDCLASS_BUTTON_PRESSED){
			featureManager.addClass();
			addClassPanel();
			validateFrame();
			updateGui();
		} // end if
		
		if(event== MITOSIS_BUTTON_PRESSED){
			
			featureManager.addtoManager(displayImage.getRoi());
			if(roiAlreadyLabelled.contains(displayImage.getRoi().getName())) {
				displayImage.killRoi();
				featureManager.getRoiMan().reset();
				JOptionPane.showMessageDialog(frame, "Already Labelled");
			}
			else {
			featureManager.addMitosis();
			String key1=((Component)event.getSource()).getName();
			Roi roi=displayImage.getRoi();
			String key=featureManager.getCurrentSlice()+" "+roi.getName();
			roiNameMap.put(key,roi );
			templist.addElement(key);
			resetRois();
		    updateroiList(key1);
		    }
		} // end if
		
		
		if(event== APOPTOSIS_BUTTON_PRESSED){

			featureManager.addtoManager(displayImage.getRoi());
			if(roiAlreadyLabelled.contains(displayImage.getRoi().getName())) {
				displayImage.killRoi();
				featureManager.getRoiMan().reset();
				JOptionPane.showMessageDialog(frame, "Already Labelled");
			}
			else {
			featureManager.addApoptosis();
			String key1=((Component)event.getSource()).getName();
			Roi roi=displayImage.getRoi();
			featureManager.addtoManager(roi);
			String key=featureManager.getCurrentSlice()+" "+roi.getName();
			roiNameMap.put(key,roi );
			templist.addElement(key);
			resetRois();
		    updateroiList(key1);
			}
		} // end if
		
		
		if(event== CLUSTERCOUNT_BUTTON_PRESSED){

			featureManager.addtoManager(displayImage.getRoi());
			if(roiAlreadyLabelled.contains(displayImage.getRoi().getName())) {
				displayImage.killRoi();
				featureManager.getRoiMan().reset();
				JOptionPane.showMessageDialog(frame, "Already Labelled");
			}
			else {
			String key1=((Component)event.getSource()).getName();
			featureManager.addClusterCount();
			Roi roi=displayImage.getRoi();
			featureManager.addtoManager(roi);
			String key=featureManager.getCurrentSlice()+" "+roi.getName();
			roiNameMap.put(key,roi );
			templist.addElement(key);
            resetRois();
			updateroiList(key1);
			}
		} // end if
		
		
		if(event== PROB_BUTTON_PRESSED){
			int mitosi=featureManager.getMitosis();
			int apopto=featureManager.getApoptosis();
			int clusto=featureManager.getClusterCount();
			int migrat=featureManager.getMigration();
			double normal=mitosi+apopto+clusto+migrat;
			double temp1=(double)(Math.round(mitosi/normal *10000d))/10000d;
			double temp2=(double)(Math.round(apopto/normal *10000d))/10000d;
			double temp3=(double)(Math.round(clusto/normal *10000d))/10000d;
			String display="Probabilities Computed\nMitosis:  "+ temp1+"\nApoptosis:  "+temp2+"\nClusterCount:  "+temp3;
			
			JOptionPane.showMessageDialog(null, display);
		
		}
		
		
		if(event==DELETE_BUTTON_PRESSED){          

			System.out.println(featureManager.getNumOfClasses());
			System.out.println(jCheckBoxList.size());
			int totalDel=0;
			
			for (JCheckBox checkBox : jCheckBoxList) 
				if (checkBox.isSelected()) 
					totalDel++;
		
			if(featureManager.getNumOfClasses()-totalDel<2) 
             JOptionPane.showMessageDialog(null, "There should be minimum two classes");
			else {
				for (JCheckBox checkBox : jCheckBoxList) 
					if (checkBox.isSelected()) 
						featureManager.deleteClass(checkBox.getName());
				addClassPanel();
				validateFrame();
				updateGui();
			}	

		} // end if

		if(event==SAVE_BUTTON_PRESSED){
			featureManager.saveFeatureMetadata();
			JOptionPane.showMessageDialog(null, "Successfully saved regions of interest");
		} //end if
		
		if(event==SAVECLASS_BUTTON_PRESSED){
			for (JCheckBox checkBox : jCheckBoxList) {				
				//System.out.println(checkBox.getText());
				String key=checkBox.getName();
				featureManager.setClassLabel(key,jTextList.get(key).getText() );
				
			}
			addClassPanel();
			validateFrame();
			updateGui();
		} // end if
		
		if(event == PREVIOUS_BUTTON_PRESSED){		
			ImagePlus image=featureManager.getPreviousImage();
			imageNum.setText(Integer.toString(featureManager.getCurrentSlice()));
			loadImage(image);
			
			if (showColorOverlay){
				if(featureManager.getProjectType()==ProjectType.CLASSIF) 
					classifiedImage = null;
				else 
					classifiedImage=featureManager.getClassifiedImage();		
				updateResultOverlay(classifiedImage);
			}

			// force limit size of image window
			if(ic.getWidth()>IMAGE_CANVAS_DIMENSION) {
				int x_centre = ic.getWidth()/2+ic.getX();
				int y_centre = ic.getHeight()/2+ic.getY();
				ic.zoomIn(x_centre,y_centre);
			}			
			updateGui();
			updateFrame();
			updateGuiFrame();
		} // end if
		
		if(event==NEXT_BUTTON_PRESSED  ){			
			ImagePlus image=featureManager.getNextImage();
			imageNum.setText(Integer.toString(featureManager.getCurrentSlice()));
			loadImage(image);
			if (showColorOverlay){
				if(featureManager.getProjectType()==ProjectType.CLASSIF)
					classifiedImage = null;
				else
					classifiedImage=featureManager.getClassifiedImage();
				updateResultOverlay(classifiedImage);
			}

			// force limit size of image window
			if(ic.getWidth()>IMAGE_CANVAS_DIMENSION) {
				int x_centre = ic.getWidth()/2+ic.getX();
				int y_centre = ic.getHeight()/2+ic.getY();
				ic.zoomIn(x_centre,y_centre);
			}
			//imagePanel.add(ic);
			updateGui();
			updateFrame();
			updateGuiFrame();
		} // end if
		
		if(event==COMPUTE_BUTTON_PRESSED){
			if(featureManager.getProjectType()==ProjectType.CLASSIF) {
				// it means new round of training, so set result setting to false
				showColorOverlay = false;
				// removing previous markings and reset things
				predictionResultClassification = null;
				displayImage.setOverlay(null);

				// compute new predictions
				featureManager.compute();				
				predictionResultClassification = featureManager.getClassificationResultMap();

				// we do not need to get any image in classification setting, only predictions are needed
				classifiedImage = null;
			}

			//segmentation setting
			else {
				classifiedImage=featureManager.compute();
			}
			IJ.log("compute");

			toggleOverlay();
		} //end if
		
		if(event==TOGGLE_BUTTON_PRESSED){
			toggleOverlay();
		} // end if
		
		if(event==DOWNLOAD_BUTTON_PRESSED){

			ImagePlus image=featureManager.stackedClassifiedImage();
			image.show();
			//FileSaver saver= new FileSaver(image);
			//saver.saveAsTiff();
		} //end if
		
		if(event==MASKS_BUTTON_PRESSED){
			System.out.println("masks ");
			if (classifiedImage==null) {
				classifiedImage=featureManager.compute();
			}
			classifiedImage.show();
			 
		} //end if
		
		if(event.getActionCommand()== "ColorButton"){	
			String key=((Component)event.getSource()).getName();
			Color c;
			c = JColorChooser.showDialog( new JFrame(),
					"CLASS COLOR", featureManager.getClassColor(key));

			((Component)event.getSource()).setBackground(c);
			featureManager.updateColor(key, c);
			updateGui();
		}// end if
		
		if(event.getActionCommand()== "AddButton"){	
			if(roiAlreadyLabelled.contains(displayImage.getRoi()))
				JOptionPane.showMessageDialog(null, "Already Labelled");
			else {
			String key=((Component)event.getSource()).getName();
			System.out.println(key);
			Roi roi=displayImage.getRoi();
			featureManager.addtoManager(roi);
			String key1=featureManager.getCurrentSlice()+" "+roi.getName();
			templist.addElement(key1);
			resetRois();
			updateroiList(key);
			}
	
			
		} //end if
		
		if(event.getActionCommand()== "UploadButton"){	
			String key=((Component)event.getSource()).getName();
			ArrayList<Roi> roiLabels=new ArrayList<>();
			for(String keytemp:roiNameMap.keySet())
				roiLabels.add(roiNameMap.get(keytemp));
			
		//	System.out.println(key);	
			featureManager.uploadTrackTraining(key, roiLabels);
			JOptionPane.showMessageDialog(null, "Succesfully Uploaded Labels");
			updateGui();
		}//end if
		
		if(event.getActionCommand()== "DownloadButton"){	
			String key=((Component)event.getSource()).getName();
			key=featureManager.getTrackPath()+"/"+key+ASCommon.FORMAT;
			
			List<Roi> tempRoilist = featureManager.getTrackRoiLabel(key);
			
			for(Roi r:tempRoilist)
				roiAlreadyLabelled.add(r.getName().trim());
			JOptionPane.showMessageDialog(null, "Succesfully Downloaded Labels");
			
		}


	}

	
	private void updateFrame() 
	{
		String key="ExampleCurrent";
		
        displayImage.killRoi();
			
		featureManager.getRoiMan().reset();
		CurrentTrackFrameRoiList=GroundTruthExtractor.runextracter(featureManager.getCurrentImage(), featureManager.getRoiMan(),1,255,0);
		featureManager.addMigration(CurrentTrackFrameRoiList.size());
		resetRois();
		templistFrame.clear();
		for(Roi rr:CurrentTrackFrameRoiList) {
	    templistFrame.addElement(rr.getName());
	    roiNameMapAll.put(rr.getName(), rr);
		}
		
			listRoi.get(key).removeAll();	
			listRoi.get(key).setListData(templistFrame);
			listRoi.get(key).setForeground(Color.BLACK);
	}

	/**
	 * Toggle between overlay and original image with markings
	 */
	private void toggleOverlay()
	{
		if(featureManager.getProjectType()== ProjectType.SEGM) {
			showColorOverlay = !showColorOverlay;			
			if (showColorOverlay && (null != classifiedImage)){
				updateResultOverlay(classifiedImage);
			}
			else{
				resultOverlay.setImage(null);
				displayImage.updateAndDraw();
			}
		}

		// classification setting, no classified image
		else {			
			showColorOverlay = !showColorOverlay;
			// user wants to see results
			if(showColorOverlay) {
				updateResultOverlay(classifiedImage);
			}

			// user wants to see original rois, no results
			else {

				// remove result overlay
				displayImage.setOverlay(null);
				displayImage.updateAndDraw();

				//just show examples drawn by user
				updateGui();
			}
		}		
	}

	public void updateResultOverlay(ImagePlus classifiedImage)
	{
		if(featureManager.getProjectType()==ProjectType.SEGM) {
			ImageProcessor overlay = classifiedImage.getProcessor().duplicate();
			overlay = overlay.convertToByte(false);
			setLut(featureManager.getColors());
			overlay.setColorModel(overlayLUT);
			resultOverlay.setImage(overlay);
			displayImage.updateAndDraw();
		}

		if(featureManager.getProjectType()== ProjectType.CLASSIF) {
			// remove previous overlay
			displayImage.setOverlay(null);
			displayImage.updateAndDraw();

			//get current slice
			int currentSlice = featureManager.getCurrentSlice();			
			Font font = new Font("Arial", Font.PLAIN, 38);           
			Overlay overlay = new Overlay();		 		 			 			
			ArrayList<Roi> rois;
			for(String classKey:featureManager.getClassKeys()) {
				//returns rois of current image slice of given class, current slice is updated internally
				rois = (ArrayList<Roi>) featureManager.getExamples(classKey,learningType.getSelectedItem().toString(), featureManager.getCurrentSlice());
				if(rois!=null) {					
					for (Roi roi:rois) {
						int pred = predictionResultClassification.get(roi.getName());
						TextRoi textroi = new TextRoi(roi.getBounds().x,roi.getBounds().y,
								roi.getFloatWidth(),roi.getFloatHeight(),Integer.toString(pred),font);
						textroi.setFillColor(roi.getFillColor());
						//textroi.setNonScalable(true);
						textroi.setPosition(currentSlice);
						overlay.add(textroi);
					}
				}
			}
			// add result overlay
			displayImage.setOverlay(overlay);			
			displayImage.updateAndDraw();				
		}
	}

	public void setLut(List<Color> colors ){
		int i=0;
		for(Color color: colors){
			red[i] = (byte) color.getRed();
			green[i] = (byte) color.getGreen();
			blue[i] = (byte) color.getBlue();
			i++;
		}
		overlayLUT = new LUT(red, green, blue);
	}
	
	private void updateGui(){
		try{
			
			//updateallExampleLists();
			ic.setMinimumSize(new Dimension(IMAGE_CANVAS_DIMENSION, IMAGE_CANVAS_DIMENSION));
			ic.repaint();
		
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void updateGuiFrame(){
		try{
			
		
			
			nxt=new JPanel();
			nxtImage=featureManager.getNextImageTrack();
			Image imag=nxtImage.getImage();
			imag=imag.getScaledInstance(340, 260, Image.SCALE_SMOOTH);
			nxtImage=new ImagePlus("next",imag);
			ImageCanvas temp=new ImageCanvas(nxtImage);
		    nxt.add(temp);
		    nxt.setBounds(360,ic.getHeight()+30,340,260);
			frame.add(nxt);

			
			prv=new JPanel();
			prvImage=featureManager.getPreviousImageTrack();
			imag=prvImage.getImage();
			imag=imag.getScaledInstance(340, 260, Image.SCALE_SMOOTH);
			prvImage=new ImagePlus("prev",imag);
			temp=new ImageCanvas(prvImage);
		    prv.add(temp);
		    prv.setBounds(10,ic.getHeight()+30,340,260);
			frame.add(prv);	
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void updateroiList(String key)
	{
		key="Example";
	    listRoi.get(key).removeAll();	
	   // System.out.print(templist.firstElement());
		listRoi.get(key).setListData(templist);
		listRoi.get(key).setForeground(Color.BLACK);
		
	}
	
	private void updateroiFrameList(String key)
	{
		
		
		key="ExampleCurrent";
	    listRoi.get(key).removeAll();	
	   // System.out.print(templist.firstElement());
		listRoi.get(key).setListData(templistFrame);
		listRoi.get(key).setForeground(Color.BLACK);
		
		
		
	}
	/*private void updateExampleLists()	{
		LearningType type=(LearningType) learningType.getSelectedItem();
		for(String key:featureManager.getClassKeys()){
			exampleList.get(key).removeAll();
			Vector<String> listModel = new Vector<String>();

			for(int j=0; j<featureManager.getRoiListSize(key, learningType.getSelectedItem().toString(),featureManager.getCurrentSlice()); j++){	
				listModel.addElement(key+ " "+ j + " " +
						featureManager.getCurrentSlice()+" "+type.getLearningType());
			}
			exampleList.get(key).setListData(listModel);
			exampleList.get(key).setForeground(featureManager.getClassColor(key));
		}
	}	
*/
	private  MouseListener mouseListener1 = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			JList<?>  theList = ( JList<?>) mouseEvent.getSource();
			if (mouseEvent.getClickCount() == 1) {
				int index = theList.getSelectedIndex();

				if (index >= 0) {
					String item =theList.getSelectedValue().toString();
					
					//System.out.println("Class Id"+ arr[0].trim());
					//int sliceNum=Integer.parseInt(arr[2].trim());
					showSelected( item);

				}
			}

			if (mouseEvent.getClickCount() == 2) {
				int index = theList.getSelectedIndex();
				String key="Example";
				if (index >= 0) {
					String item =theList.getSelectedValue().toString();
			
				    	templist.remove(item);
				    	listRoi.get(key).removeAll();	
						listRoi.get(key).setListData(templist);
						listRoi.get(key).setForeground(Color.BLACK);}
				        updateroiList(key);
					updateGui();
				}
			
		}
	};
	
	private  MouseListener mouseListener2 = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			JList<?>  theList = ( JList<?>) mouseEvent.getSource();
			if (mouseEvent.getClickCount() == 1) {
				int index = theList.getSelectedIndex();

				if (index >= 0) {
					String item =theList.getSelectedValue().toString();
					
					//System.out.println("Class Id"+ arr[0].trim());
					//int sliceNum=Integer.parseInt(arr[2].trim());
					showSelected( item);

				}
			}

			if (mouseEvent.getClickCount() == 2) {
				int index = theList.getSelectedIndex();
				String key="ExampleCurrent";
				if (index >= 0) {
					String item =theList.getSelectedValue().toString();
			
				    	templistFrame.remove(item);
				    	listRoi.get(key).removeAll();	
						listRoi.get(key).setListData(templistFrame);
						listRoi.get(key).setForeground(Color.BLACK);}
				        updateroiFrameList(key);
					updateGui();
				}
			
		}
	};

	private void showSelected(String classKey ){
		updateGui();


		displayImage.setColor(Color.YELLOW);
	
		//System.out.println(classKey+"--"+index+"---"+type);
		if(roiNameMapAll.get(classKey)!=null)
		displayImage.setRoi(roiNameMapAll.get(classKey));
		else
		displayImage.setRoi(roiNameMap.get(classKey));
		
		displayImage.updateAndDraw();
	} 
	
	private  MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent mouseEvent) {
			
			if (mouseEvent.getClickCount() == 1) {
				

				double X=mouseEvent.getX();
				double Y=mouseEvent.getY();
				IJ.doWand((int)X, (int)Y);
				
						}

		}
	};
	
	
	
	/**
	 * Select a list and deselect the others
	 * @param e item event (originated by a list)
	 * @param i list index
	 */
	private void showSelected(Roi roi ){
		updateGui();


		displayImage.setColor(Color.YELLOW);
		//String type= learningType.getSelectedItem().toString();
		//System.out.println(classKey+"--"+index+"---"+type);
		//final Roi newRoi = featureManager.getRoi(classKey, index,type);	
		//System.out.println(newRoi);
		roi.setImage(displayImage);
		displayImage.setRoi(roi);
		displayImage.updateAndDraw();
	}  
	private JButton addButton(final JButton button ,final String label, final Icon icon, final int x,
			final int y, final int width, final int height,
			JComponent panel, final ActionEvent action,final Color color )
	{
		panel.add(button);
		button.setText( label );
		button.setIcon( icon );
		button.setFont( panelFONT );
		button.setBorderPainted(false); 
		button.setFocusPainted(false); 
		button.setBackground(new Color(192, 192, 192));
		button.setForeground(Color.WHITE);
		if(color!=null){
			button.setBackground(color);
		}
		button.setBounds( x, y, width, height );
		button.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				//System.out.println(e.toString());
				doAction(action);
			}
		});

		return button;
	}

	private void setOverlay(){
		resultOverlay = new ImageOverlay();
		resultOverlay.setComposite( overlayAlpha );
		((OverlayedImageCanvas)ic).addOverlay(resultOverlay);
	}

	private void downloadRois(String key) {
		String type=learningType.getSelectedItem().toString();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setAcceptAllFileFilterUsed(false);
		int rVal = fileChooser.showOpenDialog(null);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			String name=fileChooser.getSelectedFile().toString();
			if(!name.endsWith(".zip")){
				name = name + ".zip";
			}

			featureManager.saveExamples(name, key,type, featureManager.getCurrentSlice());
		}
	}

	private void uploadExamples(String key) {
		String type=learningType.getSelectedItem().toString();
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setAcceptAllFileFilterUsed(false);
		int rVal = fileChooser.showOpenDialog(null);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			featureManager.uploadExamples(fileChooser.getSelectedFile().toString(),key,type, featureManager.getCurrentSlice());
		}
	}



}
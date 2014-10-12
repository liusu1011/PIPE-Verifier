package pipe.gui.handler;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


import pipe.dataLayer.DataLayer;
import pipe.gui.CreateGui;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsTxtPane;
import pipe.gui.widgets.EscapableDialog;
//import sam_model.SamModel;
//import sam_model.analysis.ElementaryNetComposition;
import analysis.myPromela;
import pipe.gui.GuiFrame;
import pipe.gui.CreateGui;

/**
 * Gui for verification in SPIN
 * @author suliu
 *
 */
public class AnalysisModuleHandler extends AbstractAction {
	
	JButton btn;
	private static final String MODULE_NAME = "Translation from Petri Net to Promela";
	private ResultsTxtPane results;
	private JTextField formulatext;
	private JLabel formulaSpecLabel;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof JButton){
			btn = (JButton) e.getSource();
			if(btn.getName().compareTo("btnModelToPromela") == 0){
				modelToPromelaWindow();
			}
		}
		
	}
	public void modelToPromelaWindow() {
		 // Build interface
		EscapableDialog guiDialog = 
	              new EscapableDialog(CreateGui.appGui, MODULE_NAME, true);
		   // 1 Set layout
	      Container contentPane = guiDialog.getContentPane();
	      contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
	      
//	      // 2 Add file browser
//	      sourceFilePanel = new PetriNetChooserPanel("Source net",pnmlData);
//	      contentPane.add(sourceFilePanel);
	      
	      // 3 Add results pane
	      contentPane.add(results = new ResultsTxtPane(null)); //SUTODO: the null parameter in ResultsTxtPane to be reconsider.
	      
	      //add  formula textbox
	      contentPane.add(formulaSpecLabel = new JLabel("Property Formula Specification:"));
	      contentPane.add(formulatext = new JTextField(CreateGui.getModel().getPropertyFormula()));
	      
	      // 4 Add button
	      contentPane.add(new ButtonBar("Translate", translateButtonClick,
	              guiDialog.getRootPane()));
	      
	      contentPane.add(new ButtonBar("Verify", verifyButtonClick, 
	    		  guiDialog.getRootPane()));
	      
	      // 5 Make window fit contents' preferred size
	      guiDialog.pack();
	      
	      // 6 Move window to the middle of the screen
	      guiDialog.setLocationRelativeTo(null);
	      
	      guiDialog.setVisible(true);
	}
	
	  public String getName() {
	      return MODULE_NAME;
	   }
	  
	  /**
	    * Translate button click handler
	    */
	   ActionListener translateButtonClick = new ActionListener() {
	      
	      public void actionPerformed(ActionEvent e) {
	    	 DataLayer sourceDataLayer = CreateGui.getModel();
	    	 String propertyFormula = formulatext.getText();
//	    	 SamModel sourceSamModel = SamFrame.getInstance().getCurrentCanvas().getModel();
//	    	 ElementaryNetComposition eleCompNet = new ElementaryNetComposition(sourceSamModel);
//	    	 sourceDataLayer = eleCompNet.getNetCompositionModel();
//	    	 propertyFormula = sourceSamModel.getPropertyFormula();
	    	 
	    	 myPromela promela = null;
	    	 String s = "";
	    	 
	    	 if(sourceDataLayer == null){
	    		 JOptionPane.showMessageDialog( null, "Please, choose a source net", 
	    				 "Error", JOptionPane.ERROR_MESSAGE);
	    		 return;
	    	 }
	    	 if (!sourceDataLayer.hasPlaceTransitionObjects()) {
	    		 s += "No Petri net objects defined!";
	    	 }else{
	    		 promela = new myPromela(sourceDataLayer, propertyFormula);
	               s += promela.getPromela();
	               results.setEnabled(true);
	    	 }
	    	 
	    	 results.setText(s);
	      }
	   };
	   
	   /**
	    * Verify button click handler
	    */
	   ActionListener verifyButtonClick = new ActionListener(){
		   public void actionPerformed(ActionEvent e){
			   BufferedWriter bufferedWriter = null;
			   BufferedReader bufferedReader = null;
			   
			   	DataLayer sourceDataLayer = CreateGui.getModel();
		    	 String propertyFormula = formulatext.getText();
//		    	 SamModel sourceSamModel = SamFrame.getInstance().getCurrentCanvas().getModel();
//		    	 ElementaryNetComposition eleCompNet = new ElementaryNetComposition(sourceSamModel);
//		    	 sourceDataLayer = eleCompNet.getNetCompositionModel();
//		    	 propertyFormula = sourceSamModel.getPropertyFormula();
		    	 
		    	 myPromela promela = null;
		    	 String s = "";
		    	 String r = "";
		    	 
			   try{
				   promela = new myPromela(sourceDataLayer, propertyFormula);
	               s += promela.getPromela();
	               results.setText(s);
	               //writer to a temp file
				   File fmodel = new File("proModel.pml");
				   bufferedWriter = new BufferedWriter(new FileWriter(fmodel));
				   bufferedWriter.write(s);
				   bufferedWriter.close();
				   
				   //call spin to verify the model
//				   System.out.println(System.getProperty("os.name"));
				   if(System.getProperty("os.name").startsWith("Mac OS X")){
					   Process p = Runtime.getRuntime().exec("sh VerificationInSpin.sh "
							   +"proModel.pml");
					   p.waitFor();
				   }else if(System.getProperty("os.name").startsWith("Windows")){
					   Process p = Runtime.getRuntime().exec(".\\WinVerificationInSpin.bat "
							   +"proModel.pml");
					   p.waitFor();
				   }
				   bufferedReader = new BufferedReader(new FileReader("SpinOutput.txt"));
				   String t;
				   while((t=bufferedReader.readLine())!=null){
					   r += t +"\n";
				   }
				   results.setText(r);
				   
			   }catch(IOException ioe){
				   
			   } 
			   catch (InterruptedException ioe) {
				ioe.printStackTrace();
			}
			 
		   }
	   };
	
}

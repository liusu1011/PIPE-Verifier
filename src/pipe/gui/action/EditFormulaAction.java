package pipe.gui.action;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

//import pipe.gui.action.TransitionGuardEdit;

import pipe.dataLayer.Arc;
import pipe.dataLayer.Transition;
import pipe.gui.CreateGui;

import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import formulaParser.ErrorMsg;
import pipe.gui.widgets.FormulaDialog;
import pipe.gui.widgets.FormulaPanel;

/**
 * This class allows the user to change the weight on an arc.
 * 
 * @author unknown 
 * 
 * @author Dave Patterson May 4, 2007: Handle cancel choice without an 
 * exception. Change error messages to ask for a positive integer.
 */
public class EditFormulaAction 
        extends AbstractAction {

   private static final long serialVersionUID = 2003;
   private Container contentPane;
   private Transition myTransition;
//   private FormulaPanel m_panel;
   private FormulaDialog m_dlg;
   private String m_formulaString = "";
//   private TransitionGuardEdit m_guardEdit = null; 
   
   
   public EditFormulaAction(Container contentPane, Transition a){
      this.contentPane = contentPane;
      myTransition = a;
   }
   
   
   public void actionPerformed(ActionEvent e){
	   
      
      m_dlg = new FormulaDialog(myTransition);
      m_dlg.setVisible(true);
//      this.setSize(scrollPane.getWidth() + scrollPane.getX() + 20,
//                   closeBut.getY() + closeBut.getHeight() + 45);
//      setModal(true);
//      m_dlg.m_Panel.m_textField.setText(currentFormula);
//      String input = m_dlg.m_Panel.m_textField.getText();
      
//      String input = JOptionPane.showInputDialog(
//               "Add/Edit Formula:", currentFormula);
      
//      if ( input == null ) {
//         return;		// do nothing if the user clicks "Cancel"
//      }
//      
//      String newFormula = input;
//      
//      if(newFormula != currentFormula){
//    	  CreateGui.getView().getUndoManager().addNewEdit(myTransition.setFormula(newFormula));
    	  
//      }
//      m_guardEdit  = new TransitionGuardEdit(myTransition);
//      if(newFormula != currentFormula){
//    	  CreateGui.getView().getUndoManager().addNewEdit(myTransition.setFormula(newFormula));
//    	  //   	  myTransition.setFormula(newFormula);
//      }
      
//      try {
//         int newWeighting = Integer.parseInt(input);
//         if (newWeighting < 1) {
//            JOptionPane.showMessageDialog(
//                    contentPane, "Weighting cannot be less than 1. Please re-enter");
//         } else if (newWeighting != currentWeighting){
//            CreateGui.getView().getUndoManager().addNewEdit(
//                    myTransition.setWeight(newWeighting));
//         }      
//      } catch (NumberFormatException nfe) {
//         JOptionPane.showMessageDialog(contentPane, 
//                 "Please enter a positive integer greater than 0.", 
//                 "Invalid entry", JOptionPane.ERROR_MESSAGE);
//      } catch (Exception exc) {
//         exc.printStackTrace();
//         JOptionPane.showMessageDialog(contentPane, 
//                 "Please enter a positive integer greater than 0.", 
//                 "Invalid entry", JOptionPane.ERROR_MESSAGE);
//      }
   }
   
}

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
	   
      if(!myTransition.checkIsReadyToDefine())
      {
    	  return;
      }
      m_dlg = new FormulaDialog(myTransition);
      m_dlg.setVisible(true);
   }
   
}

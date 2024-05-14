package org.martincorp.Interface;

import java.util.Optional;

import org.martincorp.Database.DBActions;
import org.martincorp.Database.MainBridge;
import org.martincorp.Model.Employee;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class EmpDeleteController {
    //@FXML objects section:
    @FXML TextField nameText;
    @FXML TextField aliasText;
    @FXML TextField emailText;
    @FXML TextField sDateText;
    @FXML TextField eDateText;
    @FXML Label dateInfoText;
    @FXML Button cancelBut;
    @FXML Button saveBut;

    //Other variables:
    private Stage window;
    private int activeId; 
    private DBActions db;

    //GUI actions:
      //cancelBut
    @FXML private void cancelClick(){
        TemplateController.loadMain();
    }
    @FXML private void cancelEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            TemplateController.loadMain();
        }
    }
      //deleteBut
    @FXML private void deleteEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            delete();
        }
    }

    //Equivalent to main method when the controller is started:
    @FXML public void initialize(){
        db = new DBActions();
    }

    //Methods:
    @FXML private void delete(){
        if(db.dropEmp(activeId)){
            GUI.launchMessage(3, "Operación completada", "Se ha eliminado con éxito al empleado seleccionado.");
            TemplateController.loadEmpTable();
        }
    }

    public void extSetup(Employee emp){
        if(emp.getID() != 0){
            activeId = emp.getID();
            nameText.setText(emp.getName());
            aliasText.setText(emp.getAlias());
            emailText.setText(emp.getMail());
            sDateText.setText(emp.getStartDate());
            eDateText.setText(emp.getEndDate() != "" ? emp.getEndDate() : "Indefinido");
        }
    }



    public void setStage(Stage w){
        this.window = w;
    }
}
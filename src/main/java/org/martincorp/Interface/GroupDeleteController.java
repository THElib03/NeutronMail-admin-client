package org.martincorp.Interface;

import org.martincorp.Database.DBActions;
import org.martincorp.Model.Group;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class GroupDeleteController{
    //@FXML objects section:
    @FXML TextField nameText;
    @FXML TextField ownerText;
    @FXML TextField dateText;
    @FXML TextField passText;
    @FXML Button cancelBut;
    @FXML Button deleteBut;

    //Other variables:
    private Stage window;
    private int activeId; 
    private DBActions db;
    
    //Equivalent to main method when the controller is started:
    @FXML public void initialize(){
        db = new DBActions();
        activeId = 0;
    }

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

    //Methods:
    @FXML private void delete(){
        if(activeId != 0){
            if(db.dropGrp(activeId)){
                TemplateController.loadGroupTable();
            }
        }
    }

    public void extSetup(Group grp){
        activeId = grp.getId();
        nameText.setText(grp.getName());
        ownerText.setText(grp.getOwner());
        dateText.setText(grp.getCreationDate());
    }

    public void setStage(Stage w){
        this.window = w;
    }
}

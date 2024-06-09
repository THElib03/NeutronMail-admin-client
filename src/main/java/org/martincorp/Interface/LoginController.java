package org.martincorp.Interface;

import org.martincorp.Database.DBActions;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

//DONE: When password is incorrect, it doesn't even mention it or launch an exception.

public class LoginController {
    //@FXML objects section:
    @FXML private TextField nameText;
    @FXML private PasswordField passText;
    @FXML private Button cancelBut;
    @FXML private Button approveBut;
    
    //Other variables section:
    private static Stage window;
    private static DBActions db;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize() {
        db = new DBActions();
    }

    //GUI actions:
      //nameText
    @FXML private void sendEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            sendPassword();
        }
    }
      //closeBut
    @FXML private void closeClick(){
        window.close();
    }
    @FXML private void closeEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            window.close();
        }
    }

    //Misc. methods:
    public static void setStage(Stage w){
        window = w;
    }

    @FXML private void sendPassword(){
        if(db.checkUser(nameText.getText(), passText.getText())){
            window.close();
            TemplateController.launchStart(window);
        }
    }
}

package org.martincorp.Interface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CertRebootController{
    //@FXML variables:
    @FXML TextField empText;
    @FXML Button saveBut;
    @FXML Button cancelBut;

    //Other variables:
    private Stage window;
    
    //Methods:
    public void setStage(Stage w){
        this.window = w;
    }
}

package org.martincorp.Interface;

import org.martincorp.Codec.Encrypt;
import org.martincorp.Database.DBActions;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmpEditController {
    //@FXML objects section:

    //Other variables section:
    private Stage window;
    private Scene scene;
    private DBActions db;
    private Encrypt enc;

    //Methods:
    public void setStage(Stage w){
        this.window = w;
    }
}

package org.martincorp.Interface;

import java.net.URL;
import org.martincorp.Database.DBActions;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class MainController {
    //@FXML objects section:
    @FXML private ImageView empView;
    @FXML private ImageView groupView;
    @FXML private ImageView certView;

    @FXML private Label emplText;
    @FXML private Label actEmpText;
    @FXML private Label actGroupText;
    @FXML private Label empGroupText;
    @FXML private Label genCertText;

      //Other variables:
    private Stage window;
    private DBActions db;
    private URL imgURL;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        db = new DBActions();

        //Setting up the menu icons:
        imgURL = getClass().getResource("/Img/employee_icon.png");
        empView.setImage(new Image(imgURL.toExternalForm()));
        imgURL = getClass().getResource("/Img/group_icon.png");
        groupView.setImage(new Image(imgURL.toExternalForm()));
        imgURL = getClass().getResource("/Img/certificate_icon.png");
        certView.setImage(new Image(imgURL.toExternalForm()));

        //Setting up the scen labels:
        emplText.setText(String.valueOf(db.getEmpCount()));
        actEmpText.setText(String.valueOf(db.getActiveEmp()));
        actGroupText.setText(String.valueOf(db.getGroupCount()));
        empGroupText.setText(String.valueOf(db.getEmpInGroup()));
        genCertText.setText(String.valueOf(db.getGeneratedCert()));
    }

    //GUI actions
      //empView
    @FXML private void launchEmp(){
        TemplateController.loadEmpTable();
    }
      //groupView
    @FXML private void launchGroup(){
        TemplateController.loadGroupTable();
    }
      //certView
    @FXML private void launchCert(){
        TemplateController.loadCertTable();
    }

    //Methods:
    public void setStage(Stage w){
        this.window = w;
    }
}

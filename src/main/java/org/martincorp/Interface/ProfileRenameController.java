/**
 *  NeutronMail client for admintration purposes.
 *  Copyright (C) 2024 by Martín Marín.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.martincorp.Interface;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ProfileRenameController {
    //FXML objects section:
    @FXML private BorderPane root; 
    @FXML private TextField nameText;
    @FXML private Button cancelBut;
    @FXML private Button saveBut;

    //Other variables section:
    private Stage window;
    private Scene scene;
    private String oldName;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        nameText.setText(oldName);
    }

    //GUI actions:
      //nameText
    @FXML private void nameEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            saveName();
        }
    }
      //cancelBut
    @FXML private void cancelClick(){
        window.close();
    }
    @FXML private void cancelEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            window.close();
        }
    }
      //saveBut
    @FXML private void saveEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            saveName();
        }
    }

    //Misc. methods:
    private void saveName(){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/settings.fxml"));
            Parent root = loader.load();

            Stage newWindow = new Stage();
            Scene settingsScene = new Scene(root);

            SettingsController sCont = loader.getController();
            sCont.setStage(newWindow);
            sCont.renameLaunch(oldName, nameText.getText());

            URL iconURL = GUI.class.getResource("/Img/icon.png");
            newWindow.getIcons().add(new Image(iconURL.toExternalForm()));
            newWindow.setTitle("Configuración del cliente de NeutronMail para Administradores");
            newWindow.setWidth(640);
            newWindow.setHeight(480);
            newWindow.setScene(settingsScene);
            newWindow.showAndWait();

            window.close();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public void setStage(Stage w){
        this.window = w;
        scene = window.getScene();
    }

    public void setName(String newName){
        this.oldName = newName;
    }
}

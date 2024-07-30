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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class StartController{
    //@FXML objects section:
    @FXML private BorderPane bPane;
    @FXML private ImageView logoView;

    //Other variables section:
    private static Stage window;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){}

    //Misc. methods:
    public static void setStage(Stage w) {
        window = w;
    }

    public void setLogo(){
        URL logoURL = getClass().getResource("/Img/logo.png");
        Image logo = new Image(logoURL.toExternalForm(), window.getWidth(), window.getHeight(), true, true);
        logoView.setImage(logo);
    }

    public void launchMain(int time){
        try{               
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/template.fxml"));
            Parent root = loader.load();

            TemplateController tCont = loader.getController();
            tCont.setStage(window);
            tCont.loadMain();

            Thread.sleep(time);
            Scene newScene = new Scene(root, window.getWidth(), window.getHeight());
            Platform.runLater( () -> window.setScene(newScene));
        }
        catch(InterruptedException ie){
            ie.printStackTrace();
            GUI.launchMessage(2, "Error del sistema", "Ha ocurrido un error con el manejo de procesos.\n\n" + ie.getMessage());
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
        catch(IllegalStateException ise){
            ise.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ise.getMessage());
        }
    }
}

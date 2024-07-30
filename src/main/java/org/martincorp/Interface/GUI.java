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
import java.util.Optional;

import org.martincorp.Codec.SettingsParser;
import org.martincorp.Database.LoginBridge;
import org.martincorp.Database.MainBridge;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;;

public class GUI extends Application{
    //Variables:
    private static LoginBridge lBridge;
    private Scene loginScene;

    //Main:
    public static void main(String[] args) {
        launch(args);
    }

    //LaunchUI start:
    @Override
    public void start(Stage window){
        //I can't do these in main() 'cause they use launchMessage() and that one needs a JavaFX thread:
        SettingsParser settingsLoad = new SettingsParser("settings.set");
        startLoginDb();

        //Window launch:
        URL iconURL = getClass().getResource("/Img/icon.png");
        window.getIcons().add(new Image(iconURL.toExternalForm()));
        window.setTitle("Administrador de Empleados NeutronMail");
        //Window size:
        window.setMinWidth(640);
        window.setMaxWidth(640);
        window.setWidth(640);
        window.setMinHeight(480);
        window.setMaxHeight(480);
        window.setHeight(480);
        
        //Login interface load:
        try{
            Thread.sleep(750);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/login.fxml"));
            Parent root = loader.load();

            LoginController lCont = loader.getController();
            loginScene = new Scene(root, window.getMaxWidth(), window.getHeight());
            lCont.setStage(window);
            
            window.setScene(loginScene);
            window.show();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            launchMessage(2, "Error de interfaz", "No se ha modido cargar la siguiente vista del programa.\n\n" + ioe.getMessage());
        }
        catch(InterruptedException ie){
            ie.printStackTrace();
            GUI.launchMessage(2, "Error del sistema", "Ha ocurrido un error con el manejo de procesos.\n\n" + ie.getMessage());
        }
    }

    //Methods:
    /*
     * Displays a range of alert popup windows.
     * Requires the type of alert to be shown, this can be:
     * 1 - CONFIRMATION alert, will display a message asking for a user's choice
     * 2 - ERROR alert, will display a message suggestion that something has gone wrong.
     * 3 - INFORMATION alert, will display a message giving some extra information.
     * 4 - NONE alert, will display a message without any default properties. -It's use is not recommended, it works fine but it cannot be closed by the user.
     * 5 - WARNING alert, will display a message about a possible consecuence of a certain action.
     *
     * When user input is needed in an alert, the next structure is recommended or is the simplest:
        Optional<ButtonType> save = LoginUI.launchMessage(5, "Advertencia", "Se han realizado cambios sin guardar\n¿Desea guardar los cambios?'");
        if(save.isPresent() && save.get() == ButtonType.OK){
            parser.saveProfile();
        }
     * The actions/input of an alert can be saved into a Optional<ButtonType> object and then be used for further actions.
     */
    public static Optional<ButtonType> launchMessage(int type, String mainMess, String contextMess) {
        switch (type) {
            case 1:
                Alert conAlert = new Alert(AlertType.CONFIRMATION, mainMess, null);
                conAlert.setHeaderText(mainMess);
                conAlert.setContentText(contextMess);

                return conAlert.showAndWait();
            case 2:
                Alert errAlert = new Alert(AlertType.ERROR, mainMess, null);
                errAlert.setHeaderText(mainMess);
                errAlert.setContentText(contextMess);

                return errAlert.showAndWait();
            case 3:
                Alert infAlert = new Alert(AlertType.INFORMATION, mainMess, null);
                infAlert.setHeaderText(mainMess);
                infAlert.setContentText(contextMess);

                return infAlert.showAndWait();
            case 4:
                Alert nonAlert = new Alert(AlertType.NONE, mainMess, null);
                nonAlert.setHeaderText(mainMess);
                nonAlert.setContentText(contextMess);

                return nonAlert.showAndWait();
            case 5:
                Alert warAlert = new Alert(AlertType.WARNING, mainMess, null);
                warAlert.setHeaderText(mainMess);
                warAlert.setContentText(contextMess);

                return warAlert.showAndWait();
            default:
                return launchMessage(2, "Error de alerta", "Se ha lanzado una alerta no válida");
        }
    }

    // 
    public static void restartSettings(String p){
        Optional<ButtonType> res = GUI.launchMessage(1, "No hay configuración presente", "¿Desea crear un nuevo archivo de\nconfiguración con valores por defecto?");
        
        if(res.isPresent() && res.get() == ButtonType.OK){
            SettingsParser newSettings = new SettingsParser("settings.set");
        }
    }

    public static void startLoginDb(){
        Thread t = new Thread( () -> {
            try{
                lBridge = new LoginBridge("companies");
                lBridge.startConnection();
            }
            catch(NullPointerException npe){
                npe.printStackTrace();
                retryDatabase(npe.getMessage(), true);
            }
        });
        t.run();
    }

    public static void retryDatabase(String message, boolean db){
        Optional<ButtonType> save = launchMessage(1, "Error Interno", "Ha ocurrido un error al conectar con la base de\ndatos o esta se encuentra desconectada:\n" + message + "\n\n¿Desea reintentar la conexión?");
        if(save.isPresent() && save.get() == ButtonType.OK){
            if(db){
                startLoginDb();
            }   
            else{
                MainBridge.startConnection();
            }
        }
    }
}

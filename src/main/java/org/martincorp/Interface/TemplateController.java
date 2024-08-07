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

import org.martincorp.Database.DBActions;
import org.martincorp.Model.Employee;
import org.martincorp.Model.Group;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TemplateController{
    //@FXML objects section:
    @FXML private BorderPane root;
    @FXML private Menu backMenu;
    @FXML private Label backText;
    @FXML private ImageView backView;
    @FXML private MenuItem closeItem;
    @FXML private MenuItem changeItem;
    @FXML private MenuItem empTableItem;
    @FXML private MenuItem addEmpItem;
    @FXML private MenuItem editEmpItem;
    @FXML private MenuItem delEmpItem;
    @FXML private MenuItem groupTableItem;
    @FXML private MenuItem addGroupItem;
    @FXML private MenuItem editGroupItem;
    @FXML private MenuItem delGroupItem;
    @FXML private MenuItem certTableItem;
    @FXML private MenuItem certRebItem;
    // @FXML private MenuItem settingsItem;
    @FXML private MenuItem aboutItem;

    //Other Variables:
    private static Stage window;
    private static DBActions db = new DBActions();
    private static BorderPane templateRoot;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        templateRoot = root;

        backMenu.setGraphic(backText);
        URL backImg = getClass().getResource("/Img/back_icon.png");
        backView.setImage(new Image(backImg.toExternalForm()));
    }

    //GUI actions:
      //backText
    @FXML private void back(){
        loadMain();
    }
      //closeItem
    @FXML private void close(){
        closeUser(window);
    }
      //changeItem
    @FXML private void change(){}
      //empTableItem
    @FXML private void empTable(){
        loadEmpTable();
    }
      //empAddItem
    @FXML private void empAdd(){
        loadEmpAdd();
    }
      //empEditItem
    @FXML private void empEdit(){
        loadEmpEdit(null);
    }
      //empDeleteItem
    @FXML private void empDelete(){
        loadEmpDel(null);
    }
      //groupTableItem
    @FXML private void groupTable(){
        loadGroupTable();
    }
      //groupAddItem
    @FXML private void groupAdd(){
        loadGroupAdd();
    }
      //groupEditItem
    @FXML private void groupEdit(){
        loadGroupEdit(null);
    }
      //groupDeleteItem
    @FXML private void groupDelete(){
        loadGroupDel(null);
    }
      //certTableItem
    @FXML private void certTable(){
        loadCertTable();
    }
      //certRebItem
    @FXML private void certReboot(){
        loadCertReboot();
    }
      //settingsItem
    /* @FXML private void settings(){
        launchSettings();
    } */
      //aboutItem
    @FXML private void about(){
        launchAbout();
    }
    //DONE: finish rewriting the listeners

    //Methods:
    public static void cleanTemplate(){
        templateRoot.setLeft(null);
        templateRoot.setCenter(null);
        templateRoot.setRight(null);
        templateRoot.setBottom(null);
    }

    public static void cleanLeft(){
        templateRoot.setLeft(null);
    }

    public static void cleanRight(){
        templateRoot.setRight(null);
    }

    public static void setLeft(Node node){
        templateRoot.setLeft(node);
    }

    public static void setRight(Node node){
        templateRoot.setRight(node);
    }

    public static void launchTemplate(Stage window){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/template.fxml"));
            Parent root = loader.load();

            MainController mCont = loader.getController();
            Scene newScene = new Scene(root);
            mCont.setStage(window);

            Platform.runLater( () -> window.setScene(newScene));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadMain(){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/main.fxml"));
            Parent root = loader.load();

            MainController mCont = loader.getController();
            mCont.setStage(window);
            
            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setCenter(root));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void closeUser(Stage window){
        Optional<ButtonType> res = GUI.launchMessage(1, "Cerrar sesión", "¿Está seguro de cambiar de compañía?");
        
        if(res.isPresent() && res.get() == ButtonType.OK){
            db.mainClose();
            db.loginOpen();

            window.close();
            launchLogin();
        }
    }

    public static void launchStart(Stage window){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/start.fxml"));
            Parent root = loader.load();

            Stage newWindow = new Stage();
            Scene startScene = new Scene(root, window.getWidth(), window.getHeight());

            StartController sCont = loader.getController();
            sCont.setStage(newWindow);
            sCont.setLogo();

            URL iconURL = GUI.class.getResource("/Img/icon.png");
            newWindow.getIcons().add(new Image(iconURL.toExternalForm()));
            newWindow.setTitle("NeutronMail para administradores");
            newWindow.setWidth(1366);
            newWindow.setHeight(768);
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.setScene(startScene);
            newWindow.show();

            sCont.launchMain(1500);
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void launchLogin(){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/login.fxml"));
            Parent root = loader.load();

            Stage newWindow = new Stage();
            Scene loginScene = new Scene(root);

            LoginController lCont = loader.getController();
            lCont.setStage(newWindow);

            URL iconURL = GUI.class.getResource("/Img/icon.png");
            newWindow.getIcons().add(new Image(iconURL.toExternalForm()));
            newWindow.setTitle("Inicio de sesión");
            newWindow.setWidth(640);
            newWindow.setHeight(480);
            newWindow.setScene(loginScene);
            newWindow.showAndWait();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadEmpTable(){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/empTable.fxml"));
            Parent root = loader.load();

            EmpTableController tEmpCont = loader.getController();
            tEmpCont.setStage(window);
            tEmpCont.loadEmployees();

            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setCenter(root));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadEmpAdd(){
        /* DONE:
         * It would be better to just change into a new pane instead of a 640x480 new window? Like, we got too much info compared to the superadmin program.
         * Or a vertically stretched resized pane? --Would be weird but also more easy to arrange.
        */
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/empAdd.fxml"));
            Parent root = loader.load();

            EmpAddController aEmpCont = loader.getController();
            aEmpCont.setStage(window);

            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setCenter(root));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadEmpDel(Employee delEmp){
        try{
            FXMLLoader delLoader = new FXMLLoader(GUI.class.getResource("/Fxml/empDelete.fxml"));
            Parent delRoot = delLoader.load();
            FXMLLoader schLoader = new FXMLLoader(GUI.class.getResource("/Fxml/empSearch.fxml"));
            Parent schRoot = schLoader.load();

            EmpDeleteController dEmpCont = delLoader.getController();
            EmpSearchController sEmpCont = schLoader.getController();
            dEmpCont.setStage(window);
            sEmpCont.setStage(window);
            sEmpCont.setParent(1);

            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setLeft(delRoot));
            Platform.runLater( () -> templateRoot.setRight(schRoot));

            if(delEmp != null){
                dEmpCont.extSetup(delEmp);
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadEmpEdit(Employee edtEmp){
        try{
            FXMLLoader edtLoader = new FXMLLoader(GUI.class.getResource("/Fxml/empEdit.fxml"));
            Parent edtRoot = edtLoader.load();
            FXMLLoader schLoader = new FXMLLoader(GUI.class.getResource("/Fxml/empSearch.fxml"));
            Parent schRoot = schLoader.load();

            EmpEditController eEmpCont = edtLoader.getController();
            EmpSearchController sEmpCont = schLoader.getController();
            eEmpCont.setStage(window);
            sEmpCont.setStage(window);
            sEmpCont.setParent(2);

            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setLeft(edtRoot));
            Platform.runLater( () -> templateRoot.setRight(schRoot));

            if(edtEmp != null){
                eEmpCont.extSetup(edtEmp);
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadGroupTable(){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/grpTable.fxml"));
            Parent root = loader.load();

            GroupTableController tGrpCont = loader.getController();
            tGrpCont.setStage(window);
            tGrpCont.loadGroups();

            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setCenter(root));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadGroupAdd(){
        try{
            FXMLLoader addLoader = new FXMLLoader(GUI.class.getResource("/Fxml/grpAdd.fxml"));
            Parent addRoot = addLoader.load();
            FXMLLoader schLoader = new FXMLLoader(GUI.class.getResource("/Fxml/empSearch.fxml"));
            Parent schRoot = schLoader.load();

            GroupAddController aGrpCont = addLoader.getController();
            EmpSearchController sEmpCont = schLoader.getController();
            EmpTableController.aGrpCont = aGrpCont;
            aGrpCont.setStage(window);
            sEmpCont.setStage(window);
            sEmpCont.setParent(3);

            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setLeft(addRoot));
            Platform.runLater( () -> templateRoot.setRight(schRoot));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadGroupDel(Group delGrp){
        try{
            FXMLLoader delLoader = new FXMLLoader(GUI.class.getResource("/Fxml/grpDel.fxml"));
            Parent delRoot = delLoader.load();
            FXMLLoader schLoader = new FXMLLoader(GUI.class.getResource("/Fxml/grpSearch.fxml"));
            Parent schRoot = schLoader.load();

            GroupDeleteController dGrpCont = delLoader.getController();
            GroupSearchController sGrpCont = schLoader.getController();
            dGrpCont.setStage(window);
            sGrpCont.setStage(window);
            sGrpCont.setParent(1);

            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setLeft(delRoot));
            Platform.runLater( () -> templateRoot.setRight(schRoot));

            if(delGrp != null){
                dGrpCont.extSetup(delGrp);
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadGroupEdit(Group edtGrp){
        try{
            FXMLLoader edtLoader = new FXMLLoader(GUI.class.getResource("/Fxml/grpEdit.fxml"));
            Parent edtRoot = edtLoader.load();
            FXMLLoader schLoader = new FXMLLoader(GUI.class.getResource("/Fxml/grpSearch.fxml"));
            Parent schRoot = schLoader.load();

            GroupEditController eGrpCont = edtLoader.getController();
            GroupSearchController sGrpCont = schLoader.getController();
            eGrpCont.setStage(window);
            sGrpCont.setStage(window);
            sGrpCont.setParent(2);

            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setLeft(edtRoot));
            Platform.runLater( () -> templateRoot.setRight(schRoot));

            if(edtGrp != null){
                eGrpCont.extSetup(edtGrp);
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha podido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadCertTable(){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/certTable.fxml"));
            Parent root = loader.load();

            CertTableController tCertCont = loader.getController();
            tCertCont.setStage(window);

            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setCenter(root));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void loadCertReboot(){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/grpTable.fxml"));
            Parent root = loader.load();

            GroupTableController tGrpCont = loader.getController();
            tGrpCont.setStage(window);

            Platform.runLater( () -> cleanTemplate());
            Platform.runLater( () -> templateRoot.setCenter(root));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public static void launchAbout(){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/about.fxml"));
            Parent root = loader.load();

            Stage newWindow = new Stage();
            Scene aboutScene = new Scene(root);

            AboutController aCont = loader.getController();
            aCont.setStage(newWindow);

            URL iconURL = GUI.class.getResource("/Img/icon.png");
            newWindow.getIcons().add(new Image(iconURL.toExternalForm()));
            newWindow.setTitle("Sobre el cliente de NeutronMail para Administradores");
            newWindow.setWidth(640);
            newWindow.setHeight(480);
            newWindow.initModality(Modality.APPLICATION_MODAL);
            newWindow.setScene(aboutScene);
            newWindow.showAndWait();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    /* public static void launchSettings(){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/settings.fxml"));
            Parent root = loader.load();

            Stage newWindow = new Stage();
            Scene loginScene = new Scene(root);

            SettingsController lCont = loader.getController();
            lCont.setStage(newWindow);

            URL iconURL = GUI.class.getResource("/Img/icon.png");
            newWindow.getIcons().add(new Image(iconURL.toExternalForm()));
            newWindow.setTitle("Configuración del cliente de NeutronMail para Administradores");
            newWindow.setWidth(640);
            newWindow.setHeight(480);
            newWindow.initModality(Modality.APPLICATION_MODAL);
            newWindow.setScene(loginScene);
            newWindow.showAndWait();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    } */

    /**
     * TODO: Not important but create a way for the backButton in the menu to record which where the previous views
     * Maybe an static list in here that gets added and substracted the name or codes of the visited views.
     */
    public static void launchPrevious(String controller){
        switch(controller){
            case "main":
                break;
            default:
                GUI.launchMessage(2, "", "");
                break;
        }
    }

    public void setStage(Stage w){
        this.window = w;
    }
}
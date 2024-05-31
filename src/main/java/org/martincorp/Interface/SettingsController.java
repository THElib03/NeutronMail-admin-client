package org.martincorp.Interface;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.martincorp.Codec.SettingsParser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SettingsController {
    //FXML objects section:
    @FXML private GridPane root;
    
    @FXML private ChoiceBox<String> activeCombo;
    @FXML private Button renameBut;
    @FXML private Button loadBut;
    @FXML private Button newBut;
    @FXML private ChoiceBox<String> languageCombo;
    @FXML private RadioButton centralRadio;
    @FXML private RadioButton privateRadio;
    @FXML private TextField serverText;
    @FXML private TextField portText;
    @FXML private Button cancelBut;
    @FXML private Button saveBut;    

    //Other variables section:
    private Stage window;
    private Scene scene;
    private SettingsParser parser;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        parser = new SettingsParser();

        ToggleGroup serverGroup = new ToggleGroup();
        centralRadio.setToggleGroup(serverGroup);
        privateRadio.setToggleGroup(serverGroup);

        languageCombo.getItems().addAll("Español", "English", "Catalá");

        update();
    }

    //GUI actions:
      //renameBut
    @FXML private void renameEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            launchRename();
        }
    }
      //loadBut
    @FXML private void loadEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            loadProfile();
        }
    }
      //newBut
    @FXML private void newEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            newProfile();
        }
    }
      //cancelBut
    @FXML private void cancelEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            cancel();
        }
    }
      //saveBut
    @FXML private void saveEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            saveChanges();
        }
    }

    //Misc. methods:
    @FXML private void loadProfile(){
        parser.loadProfile(activeCombo.getValue());
        update();
    }

    @FXML private void newProfile(){
        List<String> names = parser.getNames();
        int count = 0;
        boolean done = false;

        for(int i = 0; i < names.size(); i++){
            String name = names.get(i);
            if(name.length() >= 13){
                if(name.substring(0, 13).equals("Nuevo perfil ")){
                    count++;
                }
            }
        }

        if(count == 0){
            parser.newProfile("Nuevo perfil ");
        }    
        if(count > 0){
            parser.newProfile("Nuevo perfil " + count);
        }
        else if(count < 0){
            GUI.launchMessage(2, "Error de lectura", "Ha ocurrido un error inesperado al\ncrear un nuevo perfil:\n\nUndefined count of new profiles.");
            window.close();
        }

        update();
    }

    @FXML private void cancel(){
        if(true){
            //TODO: implement unsaved changed state
            Optional<ButtonType> save = GUI.launchMessage(5, "Advertencia", "Se han realizado cambios sin guardar\n¿Desea guardar los cambios?'");

            if(save.isPresent() && save.get() == ButtonType.OK){
                parser.saveProfile(activeCombo.getValue(), languageCombo.getValue(), centralRadio.isSelected() ? true : privateRadio.isSelected() ? false : null, serverText.getText(), Integer.parseInt(portText.getText()));
            }
        }

        window.close();
    }

    private void update(){
        //active profile
        List<String> profiles = parser.getNames();
        activeCombo.setItems(FXCollections.observableArrayList(profiles));
        
        for(String prof : profiles){
            if(parser.getName().equals(prof) ){
                activeCombo.setValue(parser.getName());
            }
        }

        //language
        ObservableList languages = languageCombo.getItems();
        for(int i = 0; i < languages.size(); i++){
            String l = parser.getLanguage();
            if(l.equals(languages.get(i))){
                languageCombo.setValue(String.valueOf(languages.get(i)));
            }
        }

        //server
        if(!parser.getServer()){
            centralRadio.setSelected(true);
        }
        else{
            privateRadio.setSelected(true);
        }

        //link
        serverText.setText(parser.getLink());

        //port
        portText.setText(String.valueOf(parser.getPort()));
    }

    private void saveChanges(){
        parser.saveProfile(activeCombo.getValue(), languageCombo.getValue(), centralRadio.isSelected() ? true : privateRadio.isSelected() ? false : null, serverText.getText(), Integer.parseInt(portText.getText()));
        GUI.launchMessage(3, "Guardado exitoso", "El perfil se ha guardado correctamente.");
    }

    private void launchRename(){
        try{
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/Fxml/newNamePromt.fxml"));
            Parent root = loader.load();

            Stage newWindow = new Stage();
            Scene newNameScene = new Scene(root);

            ProfileRenameController pCont = loader.getController();
            pCont.setStage(newWindow);
            pCont.setName(activeCombo.getValue());

            URL iconURL = GUI.class.getResource("/Img/icon.png");
            newWindow.getIcons().add(new Image(iconURL.toExternalForm()));
            newWindow.setTitle("Nuevo nombre de perfil");
            newWindow.setWidth(300);
            newWindow.setHeight(160);
            newWindow.setScene(newNameScene);
            newWindow.show();

            window.close();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }

    public void renameLaunch(String old, String name){
        activeCombo.setItems(FXCollections.observableArrayList(new ArrayList<String>()));
        List<String> profiles = parser.getNames();

        for(String prof : profiles){
            if(old.equals(prof) ){
                activeCombo.setValue(name);
                languageCombo.setValue(name);
            }
        }
    }

    public void setStage(Stage w){
        this.window = w;
        scene = window.getScene();
    }
}

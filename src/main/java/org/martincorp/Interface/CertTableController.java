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

import java.util.List;

import org.martincorp.Database.DBActions;
import org.martincorp.Model.Certificate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class CertTableController {
    //@FXML objects section:
    @FXML private MenuItem contextReboot;
    @FXML private TableView<Certificate> table;
    @FXML private TableColumn<Certificate, String> nameCol;
    @FXML private TableColumn<Certificate, Boolean> statusCol;

    //Other variables:
    private static Stage window;
    private DBActions db;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        db = new DBActions();

        loadTable();
    }

    //GUI actions: 
    @FXML private void contextReboot(){
        ObservableList<TablePosition> selectionList = table.getSelectionModel().getSelectedCells();
            
        if(selectionList.size() > 0){
            TemplateController.loadCertReboot();
            TablePosition selection = selectionList.get(0);
            int selectionRow = selection.getRow();
            Certificate selected = table.getItems().get(selectionRow);

            //eEmpCont.extSetup(selected.getID(), selected.getName(), selected.getPass());
        }
    }

    //Methods:
    private void loadTable(){
        List<Certificate> certificates = db.getCertificates();

        if(certificates.size() == 0){
            certificates.add(new Certificate("No se ha asignado ningún certificado.", false));
        }

        ObservableList<Certificate> empTable = FXCollections.observableArrayList(certificates);
        table.setItems(empTable);

        nameCol.setCellValueFactory(new PropertyValueFactory<>(certificates.get(0).nameProperty().getName()));
        statusCol.setCellValueFactory(new PropertyValueFactory<>(certificates.get(0).statusProperty().getName()));
    }

    public static void setStage(Stage w){
        window = w;
    }
}

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

import java.time.LocalDate;

import org.martincorp.Database.DBActions;
import org.martincorp.Model.Employee;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class EmpDeleteController {
    //@FXML objects section:
    @FXML private TextField nameText;
    @FXML private  TextField aliasText;
    @FXML private TextField emailText;
    @FXML private TextField sDateText;
    @FXML private TextField eDateText;
    @FXML private Label dateInfoText;
    @FXML private Button cancelBut;
    @FXML private Button saveBut;

    //Other variables:
    private Stage window;
    private int activeId; 
    private DBActions db;

    //Equivalent to main method when the controller is started:
    @FXML public void initialize(){
        db = new DBActions();
        activeId = 0;
    }

    //GUI actions:
      //cancelBut
    @FXML private void cancelClick(){
        TemplateController.loadMain();
    }
    @FXML private void cancelEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            TemplateController.loadMain();
        }
    }
      //deleteBut
    @FXML private void deleteEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            delete();
        }
    }

    //Methods:
    @FXML private void delete(){
        if(activeId != 0 && !(aliasText.getText().strip().equals(""))){
            if(db.dropEmp(activeId, aliasText.getText())){
                TemplateController.loadEmpTable();
            }
        }
        else{
            GUI.launchMessage(5, "Advertencia", "No se ha seleccionado ningún empleado para eliminar.");
        }
    }

    public void extSetup(Employee emp){
        if(emp.getID() != 0){
            activeId = emp.getID();
            nameText.setText(emp.getName());
            aliasText.setText(emp.getAlias());
            emailText.setText(emp.getMail());
            sDateText.setText(emp.getStartDate());
            eDateText.setText(emp.getEndDate() != null ? emp.getEndDate() : "Indefinido");

            if(emp.getEndDate() != null){
                if(LocalDate.now().compareTo(LocalDate.parse(emp.getEndDate())) > 0){
                    dateInfoText.setText("El contrato de este empleado ha vencido.");
                }
                else{
                    dateInfoText.setText("Todavía no ha concluido el contrato de este empleado.");
                }
            }
            else{
                dateInfoText.setText("Todavía no ha concluido el contrato de este empleado.");
            }
        }
    }

    public void setStage(Stage w){
        this.window = w;
    }
}
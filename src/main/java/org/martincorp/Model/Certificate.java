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

package org.martincorp.Model;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Certificate {
    //Variables:
    private StringProperty name;
    private BooleanProperty status;

    //Builder:
    public Certificate(String n, boolean s){
        setName(n);
        setStatus(s);
    }

    //Methods:
    public String toString(){
        String string = "Empleado asociado: "
                        + "\nModo de privacidad: ";

        return string;
    }

    public StringProperty nameProperty(){
        if(name == null){
            name = new SimpleStringProperty(this, "name");
        }

        return name;
    }

    public void setName(String n){
        nameProperty().set(n);
    }

    public String getEmpID(){
        return nameProperty().getValue();
    }

    public BooleanProperty statusProperty(){
        if(status == null){
            status = new SimpleBooleanProperty(this, "status");
        }

        return status;
    }

    public void setStatus(boolean s){
        statusProperty().set(s);
    }

    public boolean getStatus(){
        return statusProperty().getValue();
    }
}

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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Employee {
    //Variables:
    private IntegerProperty ID;
    private StringProperty name, startDate, endDate, alias, mail;
    private ObjectProperty<Online> online;

    //Builder:
    public Employee(int i, Online o, String n, String s, String e, String a, String m){
        setID(i);
        setOnline(o);
        setName(n);
        setStartDate(s);
        setEndDate(e);
        setAlias(a);
        setMail(m);
    }

    //Methods:
    public String toString(){
        String string = "Número de empleado: " + getID()
                        + "\nEstado: " + getOnline()
                        + "\nNombre completo: " + getName()
                        + "\nAlias: " + getAlias()
                        + "\nCorreo electrónico: " + getMail()
                        + "\nFecha de contratación: " + getStartDate()
                        + "\nFin de contrato: " + getEndDate();

        return string;
    }

    public IntegerProperty IDProperty(){
        if(ID == null){
            ID = new SimpleIntegerProperty(this, "ID");
        }

        return ID;
    }

    public void setID(int i){
        IDProperty().set(i);
    }

    public Integer getID(){
        return IDProperty().getValue();
    }

    public ObjectProperty<Online> onlineProperty(){
        if(online == null){
            online = new SimpleObjectProperty<Online>(this, "online");
        }

        return online;
    }

    public void setOnline(Online o){
        onlineProperty().set(o);
    }

    public Object getOnline(){
        return onlineProperty().getValue();
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

    public String getName(){
        return nameProperty().getValue();
    }

    public StringProperty startDateProperty(){
        if(startDate == null){
            startDate = new SimpleStringProperty(this, "startDate");
        }

        return startDate;
    }

    public void setStartDate(String e){
        startDateProperty().set(e);
    }

    public String getStartDate(){
        return startDateProperty().getValue();
    }

    public StringProperty endDateProperty(){
        if(endDate == null){
            endDate = new SimpleStringProperty(this, "endDate");
        }

        return endDate;
    }

    public void setEndDate(String n){
        endDateProperty().set(n);
    }

    public String getEndDate(){
        return endDateProperty().getValue();
    }

    public StringProperty aliasProperty(){
        if(alias == null){
            alias = new SimpleStringProperty(this, "alias");
        }

        return alias;
    }

    public void setAlias(String a){
        aliasProperty().set(a);
    }

    public String getAlias(){
        return aliasProperty().getValue();
    }

    public StringProperty mailProperty(){
        if(mail == null){
            mail = new SimpleStringProperty(this, "mail");
        }

        return mail;
    }

    public void setMail(String m){
        mailProperty().set(m);
    }

    public String getMail(){
        return mailProperty().getValue();
    }
}

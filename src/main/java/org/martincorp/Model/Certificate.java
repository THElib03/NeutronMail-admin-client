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

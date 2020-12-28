/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject2;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class MainController implements Initializable {
    private static final Logger LOG = Logger.getLogger(MainController.class.getName());
    @FXML
    private TabPane tabpane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    @FXML
    private void pestanaMarcas(ActionEvent event) {
        this.cargarPestania("Marcas");
    }

    @FXML
    private void pestanaProductos(ActionEvent event) {
        this.cargarPestania("Productos");
    }
    
    private void cargarPestania(String nombre){
        try{
            FXMLLoader loader = new FXMLLoader();
            AnchorPane vistaMarca = loader.load(MainController.class.getResourceAsStream(nombre+".fxml"));
            Tab t=new Tab();
            t.setText(nombre);
            t.setContent(vistaMarca);
            this.tabpane.getTabs().add(t);
            this.tabpane.getSelectionModel().select(t);
        }catch(IOException e){
            LOG.log(Level.SEVERE, "Error al cargar FXML de "+nombre, e);
        }
    }

}

package com.mycompany.mavenproject2;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author leti
 */
public class ProductoController implements Initializable {
private static final Logger LOG = Logger.getLogger(ProductoController.class.getName());
    @FXML
    private TableColumn<Producto, Integer> ColCodigo;
    @FXML
    private TableColumn<Producto, String> ColDescripcion;
    @FXML
    private TableColumn<Producto, Float> ColCantidad;
    @FXML
    private TableColumn<Producto, Float> ColIva;
    @FXML
    private TableColumn<Producto, Float> ColPrecio;
    @FXML
    private TableColumn<Producto, Integer> ColMarca;
    @FXML
    private TextField tfCodigo;
    @FXML
    private TextField tfDescripcion;
    @FXML
    private TextField tfCantidad;
    @FXML
    private ComboBox<String> cmbIva;
    @FXML
    private TextField tfPrecio;
    @FXML
    private ComboBox<Marca> cmbMarca;
    
    private ConexionBD conex;
    @FXML
    private TableView<Producto> tblDatos;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
         this.ColCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
         this.ColDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
         this.ColCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
         this.ColIva.setCellValueFactory(new PropertyValueFactory<>("iva"));
         this.ColPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
         this.ColMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
       
        this.cmbMarca.setCellFactory((ListView<Marca> l) -> {

            return new ListCell<Marca>() {
                @Override
                protected void updateItem(Marca m, boolean empty) {
                    if (!empty) {
                        this.setText("(" + m.getCodigo() + ") " + m.getDescripcion());
                    } else {
                        this.setText("");
                    }
                    super.updateItem(m, empty);
                }
            };
        });

        this.cmbMarca.setButtonCell(new ListCell<Marca>() {
            @Override
            protected void updateItem(Marca m, boolean empty) {
                if (!empty) {
                    this.setText("(" + m.getCodigo() + ") " + m.getDescripcion());
                } else {
                    this.setText("");
                }
                super.updateItem(m, empty);
            }
        }
        );

        this.cmbIva.getItems().add("10%");
        this.cmbIva.getItems().add("5%");
        this.cmbIva.getItems().add("Exento");
        this.cmbIva.getSelectionModel().selectFirst();
        this.conex = new ConexionBD();
        //Invocamos al metodo que trae los registros de la tabla marca para cargar en el combo
        this.cargarMarcas();
        this.cargarDatos();
        
         this.tblDatos.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Producto> obs,Producto valorAnterior, Producto valorNuevo)->{
               if(valorNuevo!=null){
                   this.tfCodigo.setText(valorNuevo.getCodigo().toString());
                   this.tfDescripcion.setText(valorNuevo.getDescripcion());
                   this.tfCantidad.setText(valorNuevo.getCantidad().toString());
                   this.tfPrecio.setText(valorNuevo.getPrecio().toString());
                  
                   for (Marca marc : this.cmbMarca.getItems()){
                       if(marc.getCodigo().equals(this.tblDatos.getSelectionModel().getSelectedItem().getMarca())){
                           this.cmbMarca.getSelectionModel().select(marc);
                           break;
                       }
                   }
               }
    });
    }
        private void cargarMarcas() {
            try {
                String sql = "SELECT * FROM marca";
                Statement stm = this.conex.getConexion().createStatement();
                ResultSet resultado = stm.executeQuery(sql);
                while (resultado.next()) {
                    this.cmbMarca.getItems().add(new Marca(resultado.getInt("idmarca"), resultado.getString("descripcion")));
                }
            }catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error al cargar Marcas", ex);
            }
        }
     private void cargarDatos(){
        this.tblDatos.getItems().clear();
        
        try{
            String sql = "SELECT * FROM producto";
            Statement stm = this.conex.getConexion().createStatement();
            ResultSet resultado = stm.executeQuery(sql);
            while(resultado.next()){
                Integer id = resultado.getInt("idproducto");
                String descrip = resultado.getString("descripcion");
                String cantidad = resultado.getString("cantidad");
                Float canti = Float.parseFloat(cantidad);
                String  iva = resultado.getString("iva");
                Float iv = Float.parseFloat(iva);
                String precio = resultado.getString("precio");
                Float preci = Float.parseFloat(precio);
                Integer marca = resultado.getInt("idmarca");
                Producto p = new Producto(id, descrip, canti, iv, preci, marca);
                this.tblDatos.getItems().add(p);
            }
        }catch(SQLException ex){
            LOG.log(Level.SEVERE, "Error al cargar datos de la BD", ex);
        }
    }
    @FXML
    private void onActionRegistrar(ActionEvent event) throws SQLException {
        
        String id = this.tfCodigo.getText();
        String descripcion = this.tfDescripcion.getText();
        String cantidad = this.tfCantidad.getText();
        String precio = this.tfPrecio.getText();
        String iva = this.cmbIva.getSelectionModel().getSelectedItem();
        Float IVA;
        switch (iva){
            case "10%":
                IVA = Float.parseFloat("10");
                break;
            case "5%":
                IVA = Float.parseFloat("5");
                break;
            default:
                IVA = Float.parseFloat("0");
                break;
        }
        Integer codmarca = this.cmbMarca.getSelectionModel().getSelectedItem().getCodigo();
        if (id.isEmpty() || descripcion.isEmpty() || cantidad.isEmpty() || precio.isEmpty()){
           Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error!");
            a.setHeaderText("Complete todos los campos.");
            a.show();     
        }else{
            this.conex = new ConexionBD();
            String sql = "INSERT INTO producto (descripcion, cantidad, iva, precio, idmarca) VALUES(?,?,?,?,?)";
            PreparedStatement stm = conex.getConexion().prepareStatement(sql);
            stm.setString(1, descripcion);
            stm.setString(2, cantidad);
            stm.setFloat(3, IVA);
            stm.setString(4, precio);
            stm.setInt(5, codmarca);
            stm.execute();
            Alert al = new Alert(Alert.AlertType.INFORMATION);
            al.setTitle("Éxito");
            al.setHeaderText("Marca guardada correctamente");
            al.showAndWait();
            this.tfCodigo.clear();
            this.tfDescripcion.clear();
            this.tfCantidad.clear();
            this.tfPrecio.clear();
            this.cargarDatos();
            this.cargarMarcas();
            
        }
        
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        String id = this.tfCodigo.getText();
        String descripcion = this.tfDescripcion.getText();
        String cantidad = this.tfCantidad.getText();
        String precio = this.tfPrecio.getText();
        
        String iva = this.cmbIva.getSelectionModel().getSelectedItem();
        Float IVA;
        switch (iva){
            case "10%":
                IVA = Float.parseFloat("10");
                break;
            case "5%":
                IVA = Float.parseFloat("5");
                break;
            default:
                IVA = Float.parseFloat("0");
                break;
        }
        Integer codmarca = this.cmbMarca.getSelectionModel().getSelectedItem().getCodigo();
        String sql = "UPDATE producto SET descripcion = ?, cantidad = ?, iva = ?, precio = ?, idmarca = ? WHERE idproducto = ?";
        if (descripcion.isEmpty() || cantidad.isEmpty() ||  precio.isEmpty() ){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error al Editar");
            a.setHeaderText("Complete todos los campos");
            a.show();
        }else{
               try {
            PreparedStatement stm = conex.getConexion().prepareStatement(sql);
            stm.setString(1, descripcion);
            stm.setString(2, cantidad);
            stm.setFloat(3, IVA);
            stm.setString(4, precio);
            stm.setInt(5, codmarca);
            stm.setInt(6, Integer.parseInt(id));
            stm.execute();
            Alert al = new Alert(Alert.AlertType.INFORMATION);
            al.setTitle("Éxito");
            al.setHeaderText("Marca editada correctamente");
            al.show();
            this.tfCodigo.clear();
            this.tfDescripcion.clear();
            this.tfCantidad.clear();
            this.tfPrecio.clear();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error al editar", ex);
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("Error de conexión");
            al.setHeaderText("No se puede editar registro en la base de datos");
            al.setContentText(ex.toString());
            al.showAndWait();
        }
               this.cargarDatos();
               this.cargarMarcas();
        }
    }

    @FXML
    private void onActionEliminar(ActionEvent event) {
        String id = this.tfCodigo.getText();
        String descripcion = this.tfDescripcion.getText();
      
        if (id.isEmpty()){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error al eliminar");
            a.setHeaderText("Ingrese un codigo");
            a.show();
        }else{
            Alert alConfirm = new Alert(Alert.AlertType.CONFIRMATION);
            alConfirm.setTitle("Confirmar");
            alConfirm.setHeaderText("Desea eliminar el producto?");
            alConfirm.setContentText(id+" "+descripcion);
            Optional<ButtonType> accion = alConfirm.showAndWait();
            
            if(accion.get().equals(ButtonType.OK)){
               try{
                String sql = "DELETE FROM producto WHERE  idproducto = ?";
                PreparedStatement stm = conex.getConexion().prepareStatement(sql);
                
                Integer cod = Integer.parseInt(id);
                stm.setInt(1, cod);
                stm.execute();
                int cant = stm.getUpdateCount();
                if (cant == 0){
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error al eliminar");
                    a.setHeaderText("No existe el producto con codigo"+id);
                    a.show();
                }else{
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Eliminado");
                    a.setHeaderText("Producto eliminado correctamente");
                    a.show();
                    this.tfCodigo.clear();
                    this.tfDescripcion.clear();
                    this.tfCantidad.clear();
                    this.tfPrecio.clear();
                    this.cargarDatos();
                    this.cargarMarcas();
                }
            }catch(SQLException ex){
                LOG.log(Level.SEVERE, "Error al eliminar", ex);
            } 
            }
        }
    }

    @FXML
    private void onActionLimpiar(ActionEvent event) {
                    this.tfCodigo.clear();
                    this.tfDescripcion.clear();
                    this.tfCantidad.clear();
                    this.tfPrecio.clear(); 
    }
    
}

package edu.apkinal.bibliotecauniversitaria.controller;

import edu.apkinal.bibliotecauniversitaria.model.Libro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BibliotecaViewController implements Initializable {

    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtEditorial;
    @FXML private TextField txtAnio;
    @FXML private TextField txtStock;

    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colEditorial;
    @FXML private TableColumn<Libro, Integer> colAnio;
    @FXML private TableColumn<Libro, Integer> colStock;

    private LibroController libroController;
    private ObservableList<Libro> listaLibrosData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        libroController = new LibroController();
        configurarColumnas();
        cargarDatosTabla();
    }

    private void configurarColumnas() {
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void cargarDatosTabla() {
        List<Libro> librosDB = libroController.listarLibros();
        listaLibrosData = FXCollections.observableArrayList(librosDB);
        tblLibros.setItems(listaLibrosData);
    }

    @FXML
    private void handleGuardarLibro(ActionEvent event) {
        try {
            String isbn = txtIsbn.getText();
            String titulo = txtTitulo.getText();
            String autor = txtAutor.getText();
            String editorial = txtEditorial.getText();
            int anio = Integer.parseInt(txtAnio.getText());
            int stock = Integer.parseInt(txtStock.getText());

            Libro nuevoLibro = new Libro(isbn, titulo, autor, editorial, anio, stock);
            
            boolean guardado = libroController.agregarLibro(nuevoLibro);
            
            if (guardado) {
                mostrarAlerta("Éxito", "Libro registrado correctamente.", Alert.AlertType.INFORMATION);
                cargarDatosTabla();
                handleLimpiarCampos(null);
            } else {
                mostrarAlerta("Error", "No se pudo guardar el libro. Verifique los datos o si el ISBN ya existe.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Formato Incorrecto", "El año y el stock deben ser valores numéricos enteros.", Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Validación", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleLimpiarCampos(ActionEvent event) {
        txtIsbn.clear();
        txtTitulo.clear();
        txtAutor.clear();
        txtEditorial.clear();
        txtAnio.clear();
        txtStock.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}


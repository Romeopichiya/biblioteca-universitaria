package edu.apkinal.bibliotecauniversitaria.controller;

import edu.apkinal.bibliotecauniversitaria.model.Libro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class BibliotecaViewController implements Initializable {

    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtEditorial;
    @FXML private TextField txtAnio;
    @FXML private TextField txtStock;
    @FXML private TextField txtBuscar;

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
        List<Libro> librosDB = libroController.listarLibros();
        listaLibrosData = FXCollections.observableArrayList(librosDB);

        FilteredList<Libro> datosFiltrados = new FilteredList<>(listaLibrosData, b -> true);

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            datosFiltrados.setPredicate(libro -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (libro.getTitulo().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (libro.getAutor().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (libro.getIsbn().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Libro> datosOrdenados = new SortedList<>(datosFiltrados);
        datosOrdenados.comparatorProperty().bind(tblLibros.comparatorProperty());
        tblLibros.setItems(datosOrdenados);
        tblLibros.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> seleccionarLibro(newValue)
        );
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
        if (listaLibrosData != null) {
            listaLibrosData.setAll(librosDB);
        }
    }

    private void seleccionarLibro(Libro libro) {
        if (libro != null) {
            txtIsbn.setText(libro.getIsbn());
            txtIsbn.setDisable(true);
            txtTitulo.setText(libro.getTitulo());
            txtAutor.setText(libro.getAutor());
            txtEditorial.setText(libro.getEditorial());
            txtAnio.setText(String.valueOf(libro.getAnioPublicacion()));
            txtStock.setText(String.valueOf(libro.getStock()));
        }
    }

    @FXML
    private void handleGuardarLibro(ActionEvent event) {
        try {
            Libro nuevoLibro = capturarDatosFormulario();
            boolean guardado = libroController.agregarLibro(nuevoLibro);
            
            if (guardado) {
                mostrarAlerta("Éxito", "Libro registrado correctamente.", Alert.AlertType.INFORMATION);
                cargarDatosTabla();
                handleLimpiarCampos(null);
            } else {
                mostrarAlerta("Error", "No se pudo guardar el libro (Revise si el ISBN ya existe).", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato Incorrecto", "El año y el stock deben ser numéricos.", Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Validación", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleEditarLibro(ActionEvent event) {
        if (txtIsbn.getText().isEmpty()) {
            mostrarAlerta("Aviso", "Seleccione un libro de la tabla para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Libro libroActualizado = capturarDatosFormulario();
            boolean actualizado = libroController.actualizarLibro(libroActualizado);
            
            if (actualizado) {
                mostrarAlerta("Éxito", "Libro actualizado correctamente.", Alert.AlertType.INFORMATION);
                cargarDatosTabla();
                handleLimpiarCampos(null);
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el libro.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato Incorrecto", "El año y el stock deben ser numéricos.", Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Validación", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleEliminarLibro(ActionEvent event) {
        String isbn = txtIsbn.getText();
        if (isbn.isEmpty()) {
            mostrarAlerta("Aviso", "Seleccione un libro de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este libro?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean eliminado = libroController.eliminarLibro(isbn);
            if (eliminado) {
                mostrarAlerta("Éxito", "Libro eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarDatosTabla();
                handleLimpiarCampos(null);
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el libro.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleLimpiarCampos(ActionEvent event) {
        txtIsbn.clear();
        txtIsbn.setDisable(false);
        txtTitulo.clear();
        txtAutor.clear();
        txtEditorial.clear();
        txtAnio.clear();
        txtStock.clear();
        tblLibros.getSelectionModel().clearSelection();
    }

    private Libro capturarDatosFormulario() {
        return new Libro(
            txtIsbn.getText(),
            txtTitulo.getText(),
            txtAutor.getText(),
            txtEditorial.getText(),
            Integer.parseInt(txtAnio.getText()),
            Integer.parseInt(txtStock.getText())
        );
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}


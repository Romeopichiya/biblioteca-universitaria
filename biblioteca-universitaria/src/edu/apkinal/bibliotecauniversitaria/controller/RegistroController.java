package edu.apkinal.bibliotecauniversitaria.controller;

import edu.apkinal.bibliotecauniversitaria.model.Conexion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegistroController {

    @FXML
    private TextField txtNuevoUsuario;

    @FXML
    private PasswordField txtNuevoPassword;

    @FXML
    private void handleRegistrar() {
        String usuario = txtNuevoUsuario.getText().trim();
        String password = txtNuevoPassword.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor llena todos los campos.");
            return;
        }

        String sql = "INSERT INTO usuarios (usuario, password) VALUES (?, ?)";

        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario registrado correctamente.");
            handleVolver();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar el usuario (es posible que ya exista).");
        }
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtNuevoUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Acceso - Sistema de Biblioteca Universitaria");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}



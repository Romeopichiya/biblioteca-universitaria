package edu.apkinal.bibliotecauniversitaria.controller;

import edu.apkinal.bibliotecauniversitaria.model.Conexion;
import edu.apkinal.bibliotecauniversitaria.model.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroController {

    public boolean agregarLibro(Libro libro) {
        String sql = "INSERT INTO Libros (isbn, titulo, autor, editorial, anioPublicacion, stock) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, libro.getIsbn());
            pstmt.setString(2, libro.getTitulo());
            pstmt.setString(3, libro.getAutor());
            pstmt.setString(4, libro.getEditorial());
            pstmt.setInt(5, libro.getAnioPublicacion());
            pstmt.setInt(6, libro.getStock());
            
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar el libro: " + e.getMessage());
            return false;
        }
    }

    public List<Libro> listarLibros() {
        List<Libro> listaLibros = new ArrayList<>();
        String sql = "SELECT * FROM Libros";
        
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Libro libro = new Libro(
                    rs.getString("isbn"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("editorial"),
                    rs.getInt("anioPublicacion"),
                    rs.getInt("stock")
                );
                listaLibros.add(libro);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar los libros: " + e.getMessage());
        }
        
        return listaLibros;
    }
}


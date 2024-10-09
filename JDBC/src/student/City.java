/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author dd200138d
 */
public class City implements CityOperations{
    
    private Connection connection=DB.getInstance().getConnection();

    public City() {
    }
    
    @Override
    public int insertCity(String string, String string1) {
        if (string.equals("") || string1.equals("")) return -1;

        String checkQuery = "SELECT * FROM Grad WHERE Naziv = ? OR PostanskiBr = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            checkPs.setString(2, string1);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next())
                    return -1;
            }
        } catch (SQLException e) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, e);
        }

        String insertQuery = "INSERT INTO Grad(Naziv, PostanskiBr) VALUES(?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, string);
            ps.setString(2, string1);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating city failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating city failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return -1;
    }

    
    @Override
    public int deleteCity(String... strings) {
        StringBuilder queryBuilder = new StringBuilder("DELETE FROM Grad WHERE Naziv IN (");
        for (int i = 0; i < strings.length; i++) {
            queryBuilder.append("?");
            if (i < strings.length - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(")");

        try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
            for (int i = 0; i < strings.length; i++) {
                ps.setString(i + 1, strings[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, e);
            return -1;
        }
    }

    @Override
    public boolean deleteCity(int i) {
        String query = "DELETE FROM Grad WHERE IdG = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, i);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Override
    public List<Integer> getAllCities() {
        String query = "SELECT IdG FROM Grad";
        List<Integer> cityIds = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                cityIds.add(rs.getInt("IdG"));
            }
        } catch (SQLException e) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, e);
        }
        return cityIds;
    }
}
    


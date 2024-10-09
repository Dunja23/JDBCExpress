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
import rs.etf.sab.operations.DistrictOperations;

/**
 *
 * @author dd200138d
 */
public class District implements DistrictOperations{
        
    private Connection connection=DB.getInstance().getConnection();

    @Override
    public int insertDistrict(String string, int i, int i1, int i2) {
        if(string.equals("")) return -1;
        String checkCityQuery = "SELECT * FROM Grad WHERE IdG = ?";
        try (PreparedStatement checkCityPs = connection.prepareStatement(checkCityQuery)) {
            checkCityPs.setInt(1, i);
            try (ResultSet rs = checkCityPs.executeQuery()) {
                if (!rs.next()) {
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String query = "INSERT INTO Opstina (Naziv, IdG, Xkoord, Ykoord) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, string);
            ps.setInt(2, i);
            ps.setInt(3, i1);
            ps.setInt(4, i2);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating district failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating district failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, e);
        }
        return -1;
    }

    @Override
    public int deleteDistricts(String[] strings) {
        StringBuilder queryBuilder = new StringBuilder("DELETE FROM Opstina WHERE Naziv IN (");
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
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, e);
        }
        return -1;
    }

    @Override
    public boolean deleteDistrict(int i) {
        String query = "DELETE FROM Opstina WHERE IdO = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, i);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Override
    public int deleteAllDistrictsFromCity(String string) {
        String query = "DELETE FROM Opstina WHERE IdG IN (SELECT IdG FROM Grad WHERE Naziv = ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, string);
            return ps.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, e);
        }
        return -1;
    }

    @Override
    public List<Integer> getAllDistrictsFromCity(int i) {
        String query = "SELECT IdO FROM Opstina WHERE IdG = ?";
        List<Integer> districtIds = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    districtIds.add(rs.getInt("IdO"));
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, e);
        }
        return districtIds;
    }

    @Override
    public List<Integer> getAllDistricts() {
        String query = "SELECT IdO FROM Opstina";
        List<Integer> districtIds = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                districtIds.add(rs.getInt("IdO"));
            }
        } catch (SQLException e) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, e);
        }
        return districtIds;
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author dd200138d
 */
public class Vehicle implements VehicleOperations{
    
    private Connection connection = DB.getInstance().getConnection();

    @Override
    public boolean insertVehicle(String string, int i, BigDecimal bd) {
        if (string.equals("") || i > 2 || i < 0) return false;

        String checkQuery = "SELECT * FROM Vozilo WHERE RegBr = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next())
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, e);
        }

        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO Vozilo(RegBr, TipGoriva, Potrosnja) VALUES(?, ?, ?)");) {
            ps.setString(1, string);
            ps.setInt(2, i);
            ps.setBigDecimal(3, bd);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0)
                return false;
            
            return true;
  
        } catch (SQLException e) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return false;

    }

    @Override
    public int deleteVehicles(String... strings) {
        StringBuilder queryBuilder = new StringBuilder("DELETE FROM Vozilo WHERE RegBr IN (");
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
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, e);
        }
        return -1;
    }

    @Override
    public List<String> getAllVehichles() {
        String query = "SELECT RegBr FROM Vozilo";
        List<String> licenceNum = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                licenceNum.add(rs.getString("RegBr"));
            }
        } catch (SQLException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }
        return licenceNum;
    }

    @Override
    public boolean changeFuelType(String string, int i) {
        if (string.equals("")) return false;

        String checkQuery = "SELECT * FROM Vozilo WHERE RegBr = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, e);
        }

        try (PreparedStatement ps = connection.prepareStatement("UPDATE Vozilo SET TipGoriva = ? WHERE RegBr = ?");) {
            ps.setInt(1, i);
            ps.setString(2, string);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0)
                return false;
            
            return true;
            
        } catch (SQLException e) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return false;
    }

    @Override
    public boolean changeConsumption(String string, BigDecimal bd) {
        if (string.equals("")) return false;

        String checkQuery = "SELECT * FROM Vozilo WHERE RegBr = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, e);
        }

        try (PreparedStatement ps = connection.prepareStatement("UPDATE Vozilo SET Potrosnja = ? WHERE RegBr = ?");) {
            ps.setBigDecimal(1, bd);
            ps.setString(2, string);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0)
                return false;
            
            return true;
            
        } catch (SQLException e) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return false;
    }
    
}

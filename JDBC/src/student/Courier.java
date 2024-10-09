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
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author dd200138d
 */
public class Courier implements CourierOperations{
    
    private Connection connection = DB.getInstance().getConnection();

    @Override
    public boolean insertCourier(String string, String string1) {
        if (string.equals("") || string1.equals("")) return false;

        String checkQuery = "SELECT * FROM Kurir WHERE KorIme = ? or RegBr = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            checkPs.setString(2, string1);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) 
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, e);
        }
        
        String checkQuery1 = "SELECT * FROM Korisnik WHERE KorIme = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery1)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next()) 
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, e);
        }
        
        String checkQuery2 = "SELECT * FROM Vozilo WHERE RegBr = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery2)) {
            checkPs.setString(1, string1);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next()) 
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, e);
        }
        
        String checkQuery3 =  "INSERT INTO Kurir(KorIme, RegBr, BrIsporucenihPaketa, Profit, Status) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(checkQuery3, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, string);
            ps.setString(2, string1);
            ps.setInt(3, 0);
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0)
                return false;    
            return true;
        } catch (SQLException e) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Override
    public boolean deleteCourier(String string) {
        String query = "DELETE FROM Kurir WHERE KorIme = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, string);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Override
    public List<String> getCouriersWithStatus(int i) {
        String query = "SELECT KorIme FROM Kurir Where Status = ?";
        List<String> courierNames = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query);) {
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                courierNames.add(rs.getString("KorIme"));
            }
        } catch (SQLException e) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, e);
        }
        return courierNames;
    }

    @Override
    public List<String> getAllCouriers() {
        String query = "SELECT KorIme FROM Kurir";
        List<String> courierNames = new ArrayList<>();
        try (Statement stmt = connection.createStatement();ResultSet rs = stmt.executeQuery(query);) {
            while (rs.next()) {
                courierNames.add(rs.getString("KorIme"));
            }
        } catch (SQLException e) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, e);
        }
        return courierNames;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int i) {
        BigDecimal bd = BigDecimal.ZERO;
        String query = "SELECT COALESCE(AVG(Profit),0) FROM Kurir";
        try (Statement stmt = connection.createStatement();ResultSet rs = stmt.executeQuery(query);) {
            if (rs.next()) {
                bd = BigDecimal.valueOf(rs.getDouble(1));
            }
        } catch (SQLException e) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, e);
        }
        return bd;
    }
    
}

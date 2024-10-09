/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;
/**
 *
 * @author dd200138d
 */
public class CourierRequest implements CourierRequestOperation {
    
    private Connection connection = DB.getInstance().getConnection();

    @Override
    public boolean insertCourierRequest(String string, String string1) {
        if (string.equals("") || string1.equals("")) return false;

        String checkQuery = "SELECT * FROM ZahtevKurir WHERE KorIme = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) 
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, e);
        }
        
        String checkQuery1 = "SELECT * FROM Korisnik WHERE KorIme = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery1)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next()) 
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, e);
        }
        
        String checkQuery2 = "SELECT * FROM Vozilo WHERE RegBr = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery2)) {
            checkPs.setString(1, string1);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next()) 
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, e);
        }
            
        String checkQuery3 =  "INSERT INTO ZahtevKurir(KorIme, RegBr) VALUES(?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(checkQuery3, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, string);
            ps.setString(2, string1);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0)
                return false;    
            return true;
        } catch (SQLException e) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Override
    public boolean deleteCourierRequest(String string) {
        String query = "DELETE FROM ZahtevKurir WHERE KorIme = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, string);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Override
    public boolean changeVehicleInCourierRequest(String string, String string1) {
        if (string.equals("") || string1.equals("")) return false;

        String checkQuery = "SELECT * FROM Vozilo WHERE RegBr = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string1);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, e);
        }
        
        String checkQuery1 = "SELECT * FROM ZahtevKurir WHERE KorIme = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery1)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, e);
        }

        try (PreparedStatement ps = connection.prepareStatement("UPDATE ZahtevKurir SET RegBr = ? WHERE KorIme = ?");) {
            ps.setString(1, string1);
            ps.setString(2, string);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0)
                return false;
            
            return true;
            
        } catch (SQLException e) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Override
    public List<String> getAllCourierRequests() {
        String query = "SELECT KorIme FROM ZahtevKurir";
        List<String> courierNames = new ArrayList<>();
        try (Statement stmt = connection.createStatement();ResultSet rs = stmt.executeQuery(query);) {
            while (rs.next()) {
                courierNames.add(rs.getString("KorIme"));
            }
        } catch (SQLException e) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, e);
        }
        return courierNames;
    }

    @Override
    public boolean grantRequest(String string) {
        String sql = "{CALL GrantCourierRequest(?)}";
        
        try( CallableStatement stmt = connection.prepareCall(sql);){
            stmt.setString(1, string);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0)
                return false;
            
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
        
    }
    
}

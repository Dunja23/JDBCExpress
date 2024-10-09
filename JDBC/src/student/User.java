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
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author dd200138d
 */
public class User implements UserOperations {
    
    private Connection connection = DB.getInstance().getConnection();

    @Override
    public boolean insertUser(String string, String string1, String string2, String string3) {
       
        if (string.equals("") || string1.equals("") || string2.equals("") || string3.equals("")) return false;

        String checkQuery = "SELECT * FROM Korisnik WHERE KorIme = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) 
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }
        String checkQuery1 =  "INSERT INTO Korisnik(KorIme, Ime, Prezime, Sifra, BrPoslatihPaketa) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(checkQuery1, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, string);
            ps.setString(2, string1);
            ps.setString(3, string2);
            ps.setString(4, string3);
            ps.setInt(5, 0);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0)
                return false;    
            return true;
        } catch (SQLException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }
    

    @Override
    public int declareAdmin(String string) {
        if (string.equals("")) return -1;

        String checkQuery = "SELECT * FROM Korisnik WHERE KorIme = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next()) 
                    return 2;
            }
        } catch (SQLException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }
        
        String checkQuery1 = "SELECT * FROM Administrator WHERE KorIme = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery1)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) 
                    return 1;
            }
        } catch (SQLException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }
        String insertQuery = "INSERT INTO Administrator(KorIme) VALUES(?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, string);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return -1;
            }       
            return 0;
        } catch (SQLException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return -1;
    }

    @Override
    public Integer getSentPackages(String... strings) {
        Integer sum = null;
        String query = new String("SELECT COUNT(*) FROM ZahtevPrevoz Z JOIN Paket P ON Z.IdZP = P.IdZP WHERE Z.KorIme = ?");
        String checkQuery = "SELECT * FROM Korisnik WHERE KorIme = ?";
        for (int i = 0; i < strings.length; i++) {
            
            try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
                checkPs.setString(1, strings[i]);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) 
                        return null;
                }
            } catch (SQLException e) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
            }
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                
                ps.setString(1, strings[i]);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    int temp = rs.getInt(1);
                    if(sum == null)
                        sum = temp;
                    else sum = sum + temp;
                }
                
            } catch (SQLException e) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        
        return sum;

    }

    @Override
    public int deleteUsers(String... strings) {
        StringBuilder queryBuilder = new StringBuilder("DELETE FROM Korisnik WHERE KorIme IN (");
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
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }
        return -1;
    }

    @Override
    public List<String> getAllUsers() {
        String query = "SELECT KorIme FROM Korisnik";
        List<String> userNames = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                userNames.add(rs.getString("KorIme"));
            }
        } catch (SQLException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }
        return userNames;
    }
    
    
}

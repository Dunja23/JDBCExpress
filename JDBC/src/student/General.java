/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author dd200138d
 */
public class General implements GeneralOperations {
   private Connection connection=DB.getInstance().getConnection();
   @Override
    public void eraseAll() {
        String[] tables = { 
            "Paket", 
            "Ponuda", 
            "ZahtevPrevoz", 
            "Kurir", 
            "Voznja", 
            "Vozilo", 
            "ZahtevKurir", 
            "Administrator", 
            "Korisnik", 
            "Opstina", 
            "Grad" 
        };

        for (String table : tables) {
            String query = "DELETE FROM " + table;
            try (PreparedStatement ps = connection.prepareStatement(query);) {
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}

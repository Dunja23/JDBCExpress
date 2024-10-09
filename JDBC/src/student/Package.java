/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author dd200138d
 */
public class Package implements PackageOperations {
    
    private Connection connection=DB.getInstance().getConnection();

    @Override
    public int insertPackage(int i, int i1, String string, int i2, BigDecimal bd) {
        if (string.equals("") || i2 > 2 || i2 < 0) return -1;
        String checkQuery = "SELECT * FROM Opstina WHERE IdO = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setInt(1, i);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return -1;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setInt(1, i1);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return -1;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        try (PreparedStatement checkPs = connection.prepareStatement("SELECT * FROM Korisnik WHERE KorIme = ?")) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return -1;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }   
        
       
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Korisnik SET BrPoslatihPaketa= BrPoslatihPaketa + 1 WHERE KorIme = ?")) {
            ps.setString(1, string);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }  
        
        String insertQuery = "INSERT INTO ZahtevPrevoz(IdOPre, IdODos, KorIme, TipPaketa, TezinaPaketa) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, i);
            ps.setInt(2, i1);
            ps.setString(3, string);
            ps.setInt(4, i2);
            ps.setBigDecimal(5, bd);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating package failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } 
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return -1;
    }

    @Override
    public int insertTransportOffer(String string, int i, BigDecimal bd) {
        if (string.equals("")) return -1;
        String checkQuery = "SELECT Status FROM Kurir WHERE KorIme = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return -1;
                else if(rs.getInt(1) == 1){
                    return -1;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        String checkQuery1 = "SELECT * FROM ZahtevPrevoz WHERE IdZP = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery1)) {
            checkPs.setInt(1, i);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return -1;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        try (PreparedStatement checkPs = connection.prepareStatement("SELECT Status FROM Paket WHERE IdZP = ?")) {
            checkPs.setInt(1, i);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next()){
                    try (PreparedStatement ps = connection.prepareStatement("INSERT INTO Paket(IdZp, Status) VALUES(?, 0)")) {
                        ps.setInt(1, i);
                        int affectedRows1 = ps.executeUpdate();

                        if (affectedRows1 == 0) {
                            return -1;
                        }
                    } 
                } else if(rs.getInt(1) > 0){
                    return -1;   
                }      
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        
        String insertQuery = "INSERT INTO Ponuda(KorIme, IdZP, ProcenatCene) VALUES(?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, string);
            ps.setInt(2, i);
            ps.setBigDecimal(3, bd);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating offer failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return -1;
    }

    @Override
    public boolean acceptAnOffer(int i) {
        int type = 0;
        BigDecimal weight = BigDecimal.ZERO;
        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        String username = "";
        int IdZp = 0;
        int procenat = 0;
        
        BigDecimal euclideanDistance = BigDecimal.ZERO;
        BigDecimal price = BigDecimal.ZERO;
        
        String checkQuery = "SELECT IdZp, ProcenatCene, KorIme FROM Ponuda WHERE IdPon = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setInt(1, i);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return false;
                else {
                    IdZp = rs.getInt(1);
                    procenat = rs.getInt(2);
                    username = rs.getString(3);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        String checkQuery1 = "SELECT TipPaketa, TezinaPaketa, O1.Xkoord as x1, O1.Ykoord as y1, O2.Xkoord as x2, O2.Ykoord as y2 \n" +
            "FROM ZahtevPrevoz Z join Opstina O1 on Z.IdOPre = O1.IdO  join Opstina O2 on Z.IdODos = O2.IdO \n" +
            "WHERE IdZP = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery1)) {
            checkPs.setInt(1, IdZp);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return false;
                else{
                    type = rs.getInt(1);
                    weight = rs.getBigDecimal(2);
                    x1 = rs.getInt(3);
                    y1 = rs.getInt(4);
                    x2 = rs.getInt(5);
                    y2 = rs.getInt(6);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        euclideanDistance = new BigDecimal(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
        switch(type){
            case 0:
                price = (BigDecimal.valueOf(10).add(BigDecimal.valueOf(type).multiply(weight)))
                        .multiply(euclideanDistance).multiply(BigDecimal.ONE.add(BigDecimal.valueOf(procenat).divide(BigDecimal.valueOf(100))));
                break;
            case 1:
                price = (BigDecimal.valueOf(25).add(BigDecimal.valueOf(type).multiply(weight).multiply(BigDecimal.valueOf(100))))
                        .multiply(euclideanDistance).multiply(BigDecimal.ONE.add(BigDecimal.valueOf(procenat).divide(BigDecimal.valueOf(100))));
                break;
            case 2:
                price = (BigDecimal.valueOf(75).add(BigDecimal.valueOf(type).multiply(weight).multiply(BigDecimal.valueOf(300))))
                        .multiply(euclideanDistance).multiply(BigDecimal.ONE.add(BigDecimal.valueOf(procenat).divide(BigDecimal.valueOf(100))));
                break;
            default:
                break;
                    
        }

        try (PreparedStatement ps = connection.prepareStatement("UPDATE Paket SET Status = 1, VremePrihv = getdate(), Cena = ?, KorIme = ? , Distanca = ? WHERE IdZP = ?");) {
            ps.setBigDecimal(1, price);
            ps.setString(2, username);
            ps.setBigDecimal(3, euclideanDistance); 
            ps.setInt(4, IdZp);
            int affectedRows = ps.executeUpdate();

            if (affectedRows != 0) {
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return false;
    }

    @Override
    public List<Integer> getAllOffers() {
        String query = "SELECT IdPon FROM Ponuda";
        List<Integer> offerIds = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                offerIds.add(rs.getInt("IdPon"));
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        return offerIds;
    }

   @Override
    public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int i) {
        String query = "SELECT IdPon, ProcenatCene FROM Ponuda";
        List<Pair<Integer, BigDecimal>> pairs = new ArrayList<>();

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Integer key = rs.getInt("IdPon");
                BigDecimal value = rs.getBigDecimal("ProcenatCene");
                Pair<Integer, BigDecimal> pair = new PairClass<>(key, value);
                pairs.add(pair);
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        return pairs;
    }

    @Override
    public boolean deletePackage(int i) {
        String query = "DELETE FROM ZahtevPrevoz WHERE IdZp = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, i);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    @Override
    public boolean changeWeight(int i, BigDecimal bd) {

        String checkQuery = "SELECT * FROM ZahtevPrevoz WHERE IdZP = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setInt(1, i);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }

        try (PreparedStatement ps = connection.prepareStatement("UPDATE ZahtevPrevoz SET TezinaPaketa = ? WHERE IdZP = ?");) {
            ps.setBigDecimal(1, bd);
            ps.setInt(2, i);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0)
                return false;
            
            return true;
            
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return false;
    }

    @Override
    public boolean changeType(int i, int i1) {
        if (i1 > 2 || i1 < 0) return false;
        String checkQuery = "SELECT * FROM ZahtevPrevoz WHERE IdZP = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setInt(1, i);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }

        try (PreparedStatement ps = connection.prepareStatement("UPDATE ZahtevPrevoz SET TipPaketa = ? WHERE IdZP = ?");) {
            ps.setInt(1, i1);
            ps.setInt(2, i);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0)
                return false;
            
            return true;
            
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return false;
    }

    @Override
    public Integer getDeliveryStatus(int i) {
        Integer integer = null;
        String checkQuery = "SELECT Status FROM Paket WHERE IdZP = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setInt(1, i);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()){
                    integer = rs.getInt(1);
                    return integer;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        return integer;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int i) {
        BigDecimal bd = null;
        String checkQuery = "SELECT Cena FROM Paket WHERE IdZP = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setInt(1, i);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next())
                    bd = rs.getBigDecimal(1);
                    return bd;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        return bd;
    }

    @Override
    public Date getAcceptanceTime(int i) {
        String checkQuery = "SELECT VremePrihv FROM Paket WHERE IdZP = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setInt(1, i);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next())
                    return rs.getDate(1);
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int i) {
        String query = "SELECT IdZP FROM ZahtevPrevoz WHERE TipPaketa = ?";
        List<Integer> packIds = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query);) {
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                packIds.add(rs.getInt("IdZP"));
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        return packIds;
    }

    @Override
    public List<Integer> getAllPackages() {
        String query = "SELECT IdZP FROM ZahtevPrevoz";
        List<Integer> packIds = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                packIds.add(rs.getInt("IdZP"));
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        return packIds;
    }

    @Override
    public List<Integer> getDrive(String string) {
        if (string.equals("")) return null;
        int IdVoznje = 0;
        List<Integer> packIds = new ArrayList<>();
        String checkQuery = "SELECT IdVoznje FROM Voznja WHERE KorIme = ? ";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (!rs.next())
                    return null;
                else{
                    IdVoznje = rs.getInt(1);
                    try (PreparedStatement ps = connection.prepareStatement("Select IdZP FROM Paket WHERE IdVoznje = ?");) {
                        ps.setInt(1, IdVoznje);
                        ResultSet rs1 = ps.executeQuery();
                        while (rs1.next()){
                            packIds.add(rs1.getInt(1));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
       
        
        
        return packIds;
        
    }

    private int insertDrive(String string){
        
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Kurir SET Status = 1 WHERE KorIme = ?");) {
            ps.setString(1, string);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0)
                return -1;
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String insertQuery = "INSERT INTO Voznja(VremePoc, KorIme) VALUES(GETDATE(), ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, string);
            int affectedRows1 = ps.executeUpdate();
            if (affectedRows1 == 0){
                return -1;
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next())
                    return generatedKeys.getInt(1);
            }

        }  catch (SQLException ex) {
                Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }
    
    private boolean getPacksForDrive(int i, String string){
        Timestamp d = null;
        try (PreparedStatement ps = connection.prepareStatement("SELECT VremePoc FROM Voznja WHERE IdVoznje = ?");) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()){
                  d = rs.getTimestamp(1);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Paket SET Status = 2, IdVoznje = ? WHERE Status = 1 and KorIme = ? and VremePrihv <= ? ");) {
            ps.setInt(1, i);
            ps.setString(2, string);
            ps.setTimestamp(3, d);
            int affectedRows2 = ps.executeUpdate();
            if (affectedRows2 == 0)
                return false;
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    
    }
    
    private boolean updateDriveDistanceAndSum(int i, String string){
        BigDecimal suma = new BigDecimal(BigInteger.ZERO);
        BigDecimal potrosnja = new BigDecimal(BigInteger.ZERO);
        BigDecimal distanca = new BigDecimal(BigInteger.ZERO);
        int tip = 0;
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Kurir SET BrIsporucenihPaketa = BrIsporucenihPaketa \n" +
            "+ (SELECT COUNT(*) FROM Paket WHERE IdVoznje = ?) WHERE KorIme =?");) {
            ps.setInt(1, i);
            ps.setString(2, string);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0)
                return false;
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Voznja SET Suma = (SELECT SUM(Cena) from Paket WHERE IdVoznje = ?) WHERE IdVoznje = ?");) {
            ps.setInt(1, i);
            ps.setInt(2, i);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0)
                return false;
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        try (PreparedStatement ps = connection.prepareStatement("SELECT MAX(V.Potrosnja), MAX(TipGoriva) from Vozilo V join Kurir K on V.RegBr = K.RegBr join Voznja V1 on K.KorIme = V1.KorIme  WHERE IdVoznje = ?");) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()){
                  potrosnja = rs.getBigDecimal(1);
                  tip = rs.getInt(2);
                }
                else return false;
                  
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        try (PreparedStatement ps = connection.prepareStatement("SELECT O1.Xkoord, O2.Xkoord, O1.Ykoord, O2.Ykoord, Distanca\n" +
            "FROM Paket P join ZahtevPrevoz Z on P.IdZP = Z.IdZP join Opstina O1 on Z.IdOPre = O1.IdO \n" +
            "join Opstina O2 on Z.IdODos = O2.IdO WHERE IdVoznje = ? and Status = 2");) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()){
                    int x2 = rs.getInt(2), y2 = rs.getInt(4);
                    BigDecimal bd = rs.getBigDecimal(5);
                    distanca = distanca.add(bd);
                    while(rs.next()){
                        bd = rs.getBigDecimal(5);
                        distanca = distanca.add(bd).add(new BigDecimal(Math.sqrt(Math.pow(x2 - rs.getInt(1), 2) + Math.pow(y2 - rs.getInt(1), 2))));
                        x2 = rs.getInt(2);
                        y2 = rs.getInt(4);
                    }
                }
                else return false;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        
        switch(tip){
            case 0:
                tip = 15;
                break;
            case 1:
                tip = 32;
                break;
            case 2:
                tip = 36;
                break;
            default:
                break;
        }
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Voznja SET Potrosnja = ? WHERE IdVoznje = ?");) {
            potrosnja = BigDecimal.valueOf(tip).multiply(potrosnja).multiply(distanca);
            ps.setBigDecimal(1, potrosnja);
            ps.setInt(2, i);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0)
                return false;
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
    
        return false;
    }
    
    private boolean existsPacks1(String string){
        String checkPack = "SELECT * FROM Paket WHERE KorIme = ? and Status = 1";
        try (PreparedStatement checkPs = connection.prepareStatement(checkPack)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next())
                   return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    
    }
    
    private boolean existsPacks2(int i, String string){
        String checkPack = "SELECT * FROM Paket WHERE KorIme = ? and Status = 2 and IdVoznje = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkPack)) {
            checkPs.setString(1, string);
            checkPs.setInt(2, i);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next())
                   return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    
    }
    
    private boolean changeStatusDelivered(int i){
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Paket SET Status = 3, IdVoznje = NULL WHERE IdZP = ?");) {
            ps.setInt(1, i);
            int affectedRows2 = ps.executeUpdate();
            if (affectedRows2 == 0)
                return false;
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    
    }
    
    private int getDriveId(String string){   
        String query = "SELECT IdVoznje FROM Voznja WHERE KorIme = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, string);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1); 
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }       
        return -1;
    }
    
    private boolean deleteDrive(int i, String string){ 
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Kurir SET Profit = Profit + \n" +
            "+ (SELECT MAX(Suma) FROM Voznja WHERE IdVoznje = ?) - (SELECT MAX(Potrosnja) \n" +
            "FROM Voznja WHERE IdVoznje = ?), Status = 0 WHERE KorIme =?");) {
            ps.setInt(1, i);
            ps.setInt(2, i);
            ps.setString(3, string);
            int affectedRows = ps.executeUpdate();
            if(affectedRows == 0) return false;
        } catch (SQLException e) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, e);
        } 
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Voznja WHERE IdVoznje = ?");) {
            ps.setInt(1, i);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, e);
        }
        return false; 
    
    }
    
    private int choosePack(int i, String string){
        String query = "SELECT VremePrihv, IdZP FROM Paket WHERE IdVoznje = ? ORDER BY VremePrihv ASC";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, i);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()){
                    if(changeStatusDelivered(rs.getInt(2)));{
                        int next = rs.getInt(2);
                        if(!rs.next())
                            deleteDrive(i, string);
                        return next; 
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, e);
        }       
        return -1;
    
    }
    
    @Override
    public int driveNextPackage(String string) {
        if (string.equals("")) return -1;    
        
        String checkQuery = "SELECT Status FROM Kurir WHERE KorIme = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setString(1, string);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()){
                    if(rs.getInt(1) == 0){
                        if (!existsPacks1(string)) return -1;
                        int IdVoznja = insertDrive(string);
                        if (!getPacksForDrive(IdVoznja, string)) return -1;
                        updateDriveDistanceAndSum(IdVoznja, string);
                        int next = choosePack(IdVoznja, string);
                        return next;  
                    }   
                    
                    else{
                        int IdVoznja = getDriveId(string);
                        if (!existsPacks2(IdVoznja, string)) return -1;
                        return choosePack(IdVoznja, string);
                        
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return -1;
    }
    
}

package com.resourcepoint;

import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseOpe {


    public static void InitializeDatabase(DatabaseManager databaseManager) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS ASD (" +
                "uuid TEXT PRIMARY KEY," +
                "tagUid TEXT NOT NULL," +
                "timeUid TEXT NOT NULL," +
                "dur INTEGER NOT NULL," +
                "dropItem TEXT NOT NULL,"+
                "fullLine INTEGER NOT NULL"+
                ");";
        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void ClearTable(DatabaseManager databaseManager) {
        String clearTableSQL = "DELETE FROM ASD;";

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(clearTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void ReloadAllArmorStands(DatabaseManager databaseManager) {
        List<String[]> results = new ArrayList<>();
        String query = "SELECT uuid, tagUid,timeUid, dur, dropItem,fullLine FROM ASD;";

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String uuid = rs.getString("uuid");
                String tagUid = rs.getString("tagUid");
                String timeUid = rs.getString("timeUid");
                int dur = rs.getInt("dur");
                String dropItem = rs.getString("dropItem");
                int fullLine = rs.getInt("fullLine");
                results.add(new String[]{uuid, tagUid,timeUid, String.valueOf(dur), dropItem,String.valueOf(fullLine)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (String[] row : results) {
            UUID uuid_A = UUID.fromString(row[0]);
            UUID uuid_B = UUID.fromString(row[1]);
            UUID uuid_C = UUID.fromString(row[2]);
            ArmorStand armorStand= ArmorStandProcess.GetEntityByUid(uuid_A);
            if(armorStand== null){
                continue;
            }
            String name = armorStand.getCustomName();
            ArmorStand tagArmorStand=ArmorStandProcess.GetEntityByUid(uuid_B);
            ArmorStand timeArmorStand=ArmorStandProcess.GetEntityByUid(uuid_C);
            int dur = Integer.parseInt(row[3]);
            int fullLine = Integer.parseInt(row[5]);
            ArmorStandProcess.armorStandList.put(name,armorStand);
            ArmorStandProcess.armorStandName.put(uuid_A,tagArmorStand);
            ArmorStandProcess.subArmorStandList.put(uuid_A,timeArmorStand);
            ArmorStandProcess.dropItemList.put(uuid_A,ArmorStandProcess.armorStandItem.get(row[4]));
            ArmorStandProcess.durList.put(uuid_A,dur);
            ArmorStandProcess.leftTimeMap.put(uuid_A,dur);
            ArmorStandProcess.fullLine.put(uuid_A,fullLine);
        }

    }



    public static void InsertRecord(Connection conn,UUID uuid_A,UUID uuid_B,UUID uuid_C,int dur,String dropItem,int fullLine) {
        String insertSQL = "INSERT INTO ASD (uuid, tagUid,timeUid, dur, dropItem,fullLine) VALUES (?, ?, ?, ?, ?,?);";

        try (
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, uuid_A.toString());
            pstmt.setString(2, uuid_B.toString());
            pstmt.setString(3, uuid_C.toString());
            pstmt.setInt(4, dur);
            pstmt.setString(5, dropItem);
            pstmt.setInt(6, fullLine);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void SaveArmorStandData(DatabaseManager databaseManager) {
        Connection conn = databaseManager.getConnection();

        for (ArmorStand armorStand : ArmorStandProcess.armorStandList.values()) {
            if (armorStand != null && !armorStand.isDead()) {
                UUID uuid_A = armorStand.getUniqueId();
                UUID uuid_B = ArmorStandProcess.armorStandName.get(uuid_A).getUniqueId();
                UUID uuid_C = ArmorStandProcess.subArmorStandList.get(uuid_A).getUniqueId();
                int dur = ArmorStandProcess.durList.get(uuid_A);
                String dropItem = ArmorStandProcess.armorStandItem.inverse().get(ArmorStandProcess.dropItemList.get(uuid_A));
                int fullLine = ArmorStandProcess.fullLine.get(uuid_A);
                InsertRecord(conn, uuid_A, uuid_B, uuid_C, dur, dropItem,fullLine );
            }
        }
        databaseManager.closeConnection();
    }



}

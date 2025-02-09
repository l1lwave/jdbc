package Jdbc1;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/Apartaments?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "Qwerty!2";

    static Connection conn;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            try {
                // create connection
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                initDB();

                while (true) {
                    System.out.println("1: add apartment");
                    System.out.println("2: add random apartment");
                    System.out.println("3: view all apartments");
                    System.out.println("4: find apartment by region");
                    System.out.println("5: find apartment by area");
                    System.out.println("6: find apartment by count of rooms");
                    System.out.println("7: find apartment by price");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addApartments(sc);
                            break;
                        case "2":
                            insertRandomApartments(sc);
                            break;
                        case "3":
                            viewApartments();
                            break;
                        case "4":
                            findByRegion(sc);
                            break;
                        case "5":
                            findByArea(sc);
                            break;
                        case "6":
                            findByRooms(sc);
                            break;
                        case "7":
                            findByPrice(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                if (conn != null) conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void initDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS Apartments");
            st.execute("CREATE TABLE Apartments (id INT NOT NULL " +
                    "AUTO_INCREMENT PRIMARY KEY, region VARCHAR(20), " +
                    "address VARCHAR(20), area INT, countRooms INT, price INT)");
        } finally {
            st.close();
        }
    }

    private static void addApartments(Scanner sc) throws SQLException {
        System.out.print("Enter apartment region: ");
        String region = sc.nextLine();
        System.out.print("Enter apartment address: ");
        String address = sc.nextLine();
        System.out.print("Enter apartment area: ");
        String aArea = sc.nextLine();
        int area = Integer.parseInt(aArea);
        System.out.print("Enter apartment count of rooms: ");
        String aRooms = sc.nextLine();
        int rooms = Integer.parseInt(aRooms);
        System.out.print("Enter apartment price: ");
        String aPrice = sc.nextLine();
        int price = Integer.parseInt(aPrice);

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Apartments (region, address, area, countRooms, price) VALUES(?, ?, ?, ?, ?)");
        try {
            ps.setString(1, region);
            ps.setString(2, address);
            ps.setInt(3, area);
            ps.setInt(4, rooms);
            ps.setInt(5, price);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE

        } finally {
            ps.close();
        }
    }

    private static void insertRandomApartments(Scanner sc) throws SQLException {
        System.out.print("Enter apartments count: ");
        String sCount = sc.nextLine();
        int count = Integer.parseInt(sCount);
        Random rnd = new Random();

        conn.setAutoCommit(false); // enable transactions
        try {
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Apartments (region, address, area, countRooms, price) VALUES(?, ?, ?, ?, ?)");
                try {
                    for (int i = 0; i < count; i++) {
                        ps.setString(1, "Region" + i);
                        ps.setString(2, "Address" + i);
                        ps.setInt(3, rnd.nextInt(10, 50));
                        ps.setInt(4, rnd.nextInt(1, 4));
                        ps.setInt(5, rnd.nextInt(100, 10000));
                        ps.executeUpdate();
                    }
                    conn.commit();
                } finally {
                    ps.close();
                }
            } catch (Exception ex) {
                conn.rollback();
            }
        } finally {
            conn.setAutoCommit(true); // return to default mode
        }
    }

    private static void viewApartments() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments");
        try {
            ResultSet rs = ps.executeQuery();

            try {
                ResultSetMetaData md = rs.getMetaData();

                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.print(md.getColumnName(i) + "\t\t");
                System.out.println();

                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
    }

    private static void findByRegion(Scanner sc) throws SQLException {
        System.out.print("Enter region: ");
        String region = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE region = ?");
        try {
            ps.setString(1, region);

            try {
                ResultSet rs = ps.executeQuery();

                try {
                    ResultSetMetaData md = rs.getMetaData();

                    for (int i = 1; i <= md.getColumnCount(); i++)
                        System.out.print(md.getColumnName(i) + "\t\t");
                    System.out.println();

                    while (rs.next()) {
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            System.out.print(rs.getString(i) + "\t\t");
                        }
                        System.out.println();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                ps.close();
            }

        } finally {
            ps.close();
        }
    }

    private static void findByArea(Scanner sc) throws SQLException {
        System.out.print("Enter area from: ");
        String aAreaFrom = sc.nextLine();
        int areaFrom = Integer.parseInt(aAreaFrom);
        System.out.print("Enter area to: ");
        String aAreaTo = sc.nextLine();
        int areaTo = Integer.parseInt(aAreaTo);


        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE area >= ? AND area <= ?");
        try {
            ps.setInt(1, areaFrom);
            ps.setInt(2, areaTo);

            try {
                ResultSet rs = ps.executeQuery();

                try {
                    ResultSetMetaData md = rs.getMetaData();

                    for (int i = 1; i <= md.getColumnCount(); i++)
                        System.out.print(md.getColumnName(i) + "\t\t");
                    System.out.println();

                    while (rs.next()) {
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            System.out.print(rs.getString(i) + "\t\t");
                        }
                        System.out.println();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                ps.close();
            }

        } finally {
            ps.close();
        }
    }

    private static void findByRooms(Scanner sc) throws SQLException {
        System.out.print("Enter count of rooms from: ");
        String aRoomsFrom = sc.nextLine();
        int roomsFrom = Integer.parseInt(aRoomsFrom);
        System.out.print("Enter count of rooms to: ");
        String aRoomsTo = sc.nextLine();
        int roomsTo = Integer.parseInt(aRoomsTo);


        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE countRooms >= ? AND countRooms <= ?");
        try {
            ps.setInt(1, roomsFrom);
            ps.setInt(2, roomsTo);

            try {
                ResultSet rs = ps.executeQuery();

                try {
                    ResultSetMetaData md = rs.getMetaData();

                    for (int i = 1; i <= md.getColumnCount(); i++)
                        System.out.print(md.getColumnName(i) + "\t\t");
                    System.out.println();

                    while (rs.next()) {
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            System.out.print(rs.getString(i) + "\t\t");
                        }
                        System.out.println();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                ps.close();
            }

        } finally {
            ps.close();
        }
    }

    private static void findByPrice(Scanner sc) throws SQLException {
        System.out.print("Enter price from: ");
        String aPriceFrom = sc.nextLine();
        int priceFrom = Integer.parseInt(aPriceFrom);
        System.out.print("Enter price to: ");
        String aPriceTo = sc.nextLine();
        int priceTo = Integer.parseInt(aPriceTo);


        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE price >= ? AND price <= ?");
        try {
            ps.setInt(1, priceFrom);
            ps.setInt(2, priceTo);

            try {
                ResultSet rs = ps.executeQuery();

                try {
                    ResultSetMetaData md = rs.getMetaData();

                    for (int i = 1; i <= md.getColumnCount(); i++)
                        System.out.print(md.getColumnName(i) + "\t\t");
                    System.out.println();

                    while (rs.next()) {
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            System.out.print(rs.getString(i) + "\t\t");
                        }
                        System.out.println();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                ps.close();
            }

        } finally {
            ps.close();
        }
    }
}

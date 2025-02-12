package Shop;

import java.sql.*;
import java.util.Scanner;

public class Shop {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/myShop?serverTimezone=Europe/Kiev";
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
                    System.out.println("1: add new product");
                    System.out.println("2: add new client");
                    System.out.println("3: place an order");
                    System.out.println("4: show all products");
                    System.out.println("5: show all clients");
                    System.out.println("6: show all orders");
                    System.out.println("7: delete product");
                    System.out.println("8: delete client");
                    System.out.println("9: delete order");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addProduct(sc);
                            break;
                        case "2":
                            addClint(sc);
                            break;
                        case "3":
                            placeOrder(sc);
                            break;
                        case "4":
                            showProducts();
                            break;
                        case "5":
                            showClients();
                            break;
                        case "6":
                            showOrders();
                            break;
                        case "7":
                            deleteOrder(sc);
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

    private static void deleteOrder(Scanner sc) throws SQLException {
        System.out.print("Enter order ID: ");
        String id = sc.nextLine();
        int orderID = Integer.parseInt(id);

        PreparedStatement ps = conn.prepareStatement("DELETE FROM Orders WHERE id = ?");
        try {
            ps.setInt(1, orderID);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
    }
private static void showOrders() throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT Orders.id, " +
                        "Clients.name AS clientName, " +
                        "Clients.phone AS clientPhone, " +
                        "Orders.address, " +
                        "Products.name AS productName, " +
                        "Orders.countToBuy, Orders.date " +
                        "FROM Orders " +
                        "INNER JOIN Clients ON Orders.idClient = Clients.id " +
                        "INNER JOIN Products ON Orders.idProduct = Products.id"
        );

        try {
            ResultSet rs = ps.executeQuery();

            try {
                ResultSetMetaData md = rs.getMetaData();
                
                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.print(md.getColumnLabel(i) + "\t\t");
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

    private static void showClients() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Clients");
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

    private static void showProducts() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Products");
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

    private static void placeOrder(Scanner sc) throws SQLException {
        System.out.print("Enter client phone number: ");
        String phone = sc.nextLine();
        System.out.print("Enter product name: ");
        String name = sc.nextLine();
        System.out.print("Enter how much product did you want to place? : ");
        String countP = sc.nextLine();
        int quantity = Integer.parseInt(countP);
        System.out.print("Enter address: ");
        String address = sc.nextLine();

        int clientId = 0;
        int productId = 0;

        Statement st = conn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT id FROM Clients WHERE phone = '" + phone + "'");
            if (rs.next()) {
                clientId = rs.getInt("id");
            }

            ResultSet rs2 = st.executeQuery("SELECT id FROM Products WHERE name = '" + name + "'");
            if (rs2.next()) {
                productId = rs2.getInt("id");
            }
        } finally {
            st.close();
        }

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Orders (idClient, idProduct, address, countToBuy, date) VALUES(?, ?, ?, ?, ?)");
        try {
            ps.setInt(1, clientId);
            ps.setInt(2, productId);
            ps.setString(3, address);
            ps.setInt(4, quantity);
            ps.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE

        } finally {
            ps.close();
        }

        Statement s1 = conn.createStatement();
        try {
            s1.execute("UPDATE Products SET count = count - " + quantity +
                    " WHERE id = " + productId);
        } finally {
            s1.close();
        }
    }


    private static void addClint(Scanner sc) throws SQLException {
        System.out.print("Enter client name: ");
        String name = sc.nextLine();
        System.out.print("Enter client phone number: ");
        String phone = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Clients (name, phone) VALUES(?, ?)");
        try {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE

        } finally {
            ps.close();
        }
    }

    private static void addProduct(Scanner sc) throws SQLException {
        System.out.print("Enter product name: ");
        String name = sc.nextLine();
        System.out.print("Enter product count in warehouse: ");
        String pCount = sc.nextLine();
        int count = Integer.parseInt(pCount);
        System.out.print("Enter product price: ");
        String pPrice = sc.nextLine();
        int price = Integer.parseInt(pPrice);

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Products (name, count, price) VALUES(?, ?, ?)");
        try {
            ps.setString(1,name);
            ps.setInt(2, count);
            ps.setInt(3, price);
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE

        } finally {
            ps.close();
        }
    }

    private static void initDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS Orders");
            st.execute("DROP TABLE IF EXISTS Products");
            st.execute("DROP TABLE IF EXISTS Clients");

            st.execute("CREATE TABLE Products (id INT NOT NULL " +
                    "AUTO_INCREMENT PRIMARY KEY, name VARCHAR(20) NOT NULL, " +
                    "count INT, price INT)");

            st.execute("CREATE TABLE Clients (id INT NOT NULL " +
                    "AUTO_INCREMENT PRIMARY KEY, name VARCHAR(20) NOT NULL, " +
                    "phone VARCHAR(20))");

            st.execute("CREATE TABLE Orders (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "idClient INT NOT NULL, idProduct INT NOT NULL, " +
                    "address VARCHAR(20), countToBuy INT, date DATE, " +
                    "FOREIGN KEY (idClient) REFERENCES Clients(id), " +
                    "FOREIGN KEY (idProduct) REFERENCES Products(id))");
        } finally {
            st.close();
        }
    }
}

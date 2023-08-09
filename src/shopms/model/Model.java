package shopms.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shopms.controllers.accounts.Account;
import shopms.controllers.categories.Category;
import shopms.controllers.customers.Customer;
import shopms.controllers.others.SalesPurchasesReturns;
import shopms.controllers.products.Product;
import shopms.controllers.purchases.Purchase;
import shopms.controllers.returns.Return;
import shopms.controllers.sales.Sale;

import java.sql.*;

public class Model {
    private static final String DB_NAME = "shopmanagementsystem.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\" + DB_NAME;
    private Connection conn;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_USER_ROLE = "user_role";
    private static final String COLUMN_PASSWORD = "password";

    private static final String SELECT_USERS = "SELECT * FROM " + TABLE_USERS;
    private static final String QUERY_USERNAME = "SELECT * FROM " + TABLE_USERS +
            " WHERE " + COLUMN_USERNAME + " = ?";
    private static final String QUERY_LOGIN = "SELECT * FROM " + TABLE_USERS + " WHERE " +
            COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
    private static final String INSERT_USER = "INSERT INTO " + TABLE_USERS + '(' +
            COLUMN_USERNAME + ", " +
            COLUMN_USER_ROLE + ", " +
            COLUMN_PASSWORD + ") VALUES (?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE " + TABLE_USERS + " SET " +
            COLUMN_USERNAME + " = ?, " +
            COLUMN_USER_ROLE + " = ?, " +
            COLUMN_PASSWORD + " = ? WHERE " + COLUMN_USER_ID + " = ?";
    private static final String REMOVE_USER = "DELETE FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";

    private PreparedStatement queryUsername;
    private PreparedStatement queryLogin;
    private PreparedStatement insertUser;
    private PreparedStatement updateUser;
    private PreparedStatement removeUser;

    private final ObservableList<Account> accounts = FXCollections.observableArrayList();

    public ObservableList<Account> getAccounts() {
        return accounts;
    }

    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY = "category";

    private static final String SELECT_CATEGORIES = "SELECT * FROM " + TABLE_CATEGORIES;
    private static final String QUERY_CATEGORY = "SELECT * FROM " + TABLE_CATEGORIES +
            " WHERE " + COLUMN_CATEGORY + " = ?";
    private static final String INSERT_CATEGORY = "INSERT INTO " + TABLE_CATEGORIES + '(' +
            COLUMN_CATEGORY + ") VALUES (?)";
    private static final String UPDATE_CATEGORY = "UPDATE " + TABLE_CATEGORIES + " SET " +
            COLUMN_CATEGORY + " = ? WHERE " + COLUMN_CATEGORY_ID + " = ?";
    private static final String REMOVE_CATEGORY = "DELETE FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_ID + " = ?";

    private PreparedStatement queryCategory;
    private PreparedStatement insertCategory;
    private PreparedStatement updateCategory;
    private PreparedStatement removeCategory;

    private final ObservableList<Category> categories = FXCollections.observableArrayList();

    public ObservableList<Category> getCategories() {
        return categories;
    }

    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_BRAND = "brand";
    private static final String COLUMN_PRODUCT_CATEGORY = "category";
    private static final String COLUMN_PRODUCT_CODE = "code";
    private static final String COLUMN_PRODUCT_UNIT = "unit";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_QUANTITY = "quantity";

    private static final String SELECT_PRODUCTS = "SELECT * FROM " + TABLE_PRODUCTS;
    private static final String QUERY_PRODUCT = "SELECT * FROM " + TABLE_PRODUCTS +
            " WHERE " + COLUMN_PRODUCT_NAME + " = ? AND (" + COLUMN_PRODUCT_BRAND + " = ? OR " + COLUMN_PRODUCT_CODE + " = ?)";
    private static final String INSERT_PRODUCT = "INSERT INTO " + TABLE_PRODUCTS + '(' +
            COLUMN_PRODUCT_NAME + ", " +
            COLUMN_PRODUCT_BRAND + ", " +
            COLUMN_PRODUCT_CATEGORY + ", " +
            COLUMN_PRODUCT_CODE + ", " +
            COLUMN_PRODUCT_UNIT + ", " +
            COLUMN_PRODUCT_PRICE + ", " +
            COLUMN_PRODUCT_QUANTITY + ") VALUES (?, ?, ? ,? ,? ,? ,?)";
    private static final String UPDATE_PRODUCT = "UPDATE " + TABLE_PRODUCTS + " SET " +
            COLUMN_PRODUCT_NAME + " = ? , " +
            COLUMN_PRODUCT_BRAND + " = ? , " +
            COLUMN_PRODUCT_CATEGORY + " = ? , " +
            COLUMN_PRODUCT_CODE + " = ? , " +
            COLUMN_PRODUCT_UNIT + " = ? , " +
            COLUMN_PRODUCT_PRICE + " = ? , " +
            COLUMN_PRODUCT_QUANTITY + " = ? WHERE " + COLUMN_PRODUCT_CODE + " = ?";
    private static final String UPDATE_PRODUCT_QUANTITY = "UPDATE " + TABLE_PRODUCTS + " SET " +
            COLUMN_PRODUCT_QUANTITY + " = ? WHERE " + COLUMN_PRODUCT_CODE + " = ?";
    private static final String REMOVE_PRODUCT = "DELETE FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_CODE + " = ?";

    private PreparedStatement queryProduct;
    private PreparedStatement insertProduct;
    private PreparedStatement updateProduct;
    private PreparedStatement updateProductQuantity;
    private PreparedStatement removeProduct;

    private final ObservableList<Product> products = FXCollections.observableArrayList();

    public ObservableList<Product> getProducts() {
        return products;
    }

    private static final String TABLE_CUSTOMERS = "customers";
    private static final String COLUMN_CUSTOMER_ID = "customer_id";
    private static final String COLUMN_CUSTOMER_NAME = "name";
    private static final String COLUMN_CUSTOMER_COMPANY_NAME = "company_name";
    private static final String COLUMN_CUSTOMER_ADDRESS = "address";
    private static final String COLUMN_CUSTOMER_PHONE_NUMBER = "phone_number";

    private static final String SELECT_CUSTOMERS = "SELECT * FROM " + TABLE_CUSTOMERS;
    private static final String QUERY_CUSTOMER = "SELECT * FROM " + TABLE_CUSTOMERS +
            " WHERE " + COLUMN_CUSTOMER_NAME + " = ?";
    private static final String INSERT_CUSTOMER = "INSERT INTO " + TABLE_CUSTOMERS + '(' +
            COLUMN_CUSTOMER_NAME + ", " +
            COLUMN_CUSTOMER_COMPANY_NAME + ", " +
            COLUMN_CUSTOMER_ADDRESS + ", " +
            COLUMN_CUSTOMER_PHONE_NUMBER + ") VALUES (?, ?, ?, ?)";
    private static final String UPDATE_CUSTOMER = "UPDATE " + TABLE_CUSTOMERS + " SET " +
            COLUMN_CUSTOMER_NAME + " = ?, " +
            COLUMN_CUSTOMER_COMPANY_NAME + " = ?, " +
            COLUMN_CUSTOMER_ADDRESS + " = ?, " +
            COLUMN_CUSTOMER_PHONE_NUMBER + " = ? WHERE " + COLUMN_CUSTOMER_ID + " = ?";
    private static final String REMOVE_CUSTOMER = "DELETE FROM " + TABLE_CUSTOMERS + " WHERE " + COLUMN_CUSTOMER_ID + " = ?";

    private PreparedStatement queryCustomer;
    private PreparedStatement insertCustomer;
    private PreparedStatement updateCustomer;
    private PreparedStatement removeCustomer;

    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    public ObservableList<Customer> getCustomers() {
        return customers;
    }

    private static final String TABLE_PURCHASES = "purchases";
    private static final String COLUMN_PURCHASE_DATE = "date";
    private static final String COLUMN_PURCHASE_REFERENCE_NO = "reference_no";
    private static final String COLUMN_PURCHASE_SUPPLIER = "supplier";
    private static final String COLUMN_PURCHASE_ITEMS = "items";
    private static final String COLUMN_PURCHASE_QUANTITY = "quantity";
    private static final String COLUMN_PURCHASE_PRODUCT_COST = "product_cost";
    private static final String COLUMN_PURCHASE_SHIPPING_COST = "shipping_cost";
    private static final String COLUMN_PURCHASE_GRAND_TOTAL = "grand_total";

    private static final String SELECT_PURCHASES = "SELECT * FROM " + TABLE_PURCHASES;
    private static final String QUERY_PURCHASE = "SELECT * FROM " + TABLE_PURCHASES +
            " WHERE " + COLUMN_PURCHASE_REFERENCE_NO + " = ?";
    private static final String INSERT_PURCHASE = "INSERT INTO " + TABLE_PURCHASES + " VALUES (?, ?, ?, ? ,? ,? ,?, ?)";
    private static final String UPDATE_PURCHASE = "UPDATE " + TABLE_PURCHASES + " SET " +
            COLUMN_PURCHASE_DATE + " = ?, " +
            COLUMN_PURCHASE_SUPPLIER + " = ?, " +
            COLUMN_PURCHASE_ITEMS + " = ?, " +
            COLUMN_PURCHASE_QUANTITY + " = ?, " +
            COLUMN_PURCHASE_PRODUCT_COST + " = ?, " +
            COLUMN_PURCHASE_SHIPPING_COST + " = ?, " +
            COLUMN_PURCHASE_GRAND_TOTAL + " = ? WHERE " + COLUMN_PURCHASE_REFERENCE_NO + " = ?";
    private static final String REMOVE_PURCHASE = "DELETE FROM " + TABLE_PURCHASES + " WHERE " + COLUMN_PURCHASE_REFERENCE_NO + " = ?";

    private PreparedStatement queryPurchase;
    private PreparedStatement insertPurchase;
    private PreparedStatement updatePurchase;
    private PreparedStatement removePurchase;

    private final ObservableList<Purchase> purchases = FXCollections.observableArrayList();

    public ObservableList<Purchase> getPurchases() {
        return purchases;
    }

    private static final String TABLE_SALES_PURCHASES_RETURNS = "sales_purchases_returns";
    private static final String COLUMN_PRODUCT_PURCHASE_REFERENCE = "reference";
    private static final String COLUMN_PRODUCT_PURCHASE_ITEM_CODE = "item_code";
    private static final String COLUMN_PRODUCT_PURCHASE_QUANTITY = "quantity";
    private static final String COLUMN_PRODUCT_PURCHASE_PRODUCT_COST = "product_cost";

    private static final String SELECT_PRODUCT_PURCHASES = "SELECT * FROM " + TABLE_SALES_PURCHASES_RETURNS;
    private static final String INSERT_PRODUCT_PURCHASE = "INSERT INTO " + TABLE_SALES_PURCHASES_RETURNS + " VALUES (?, ?, ?, ?)";
    private static final String REMOVE_PRODUCT_PURCHASE = "DELETE FROM " + TABLE_SALES_PURCHASES_RETURNS + " WHERE " + COLUMN_PRODUCT_PURCHASE_REFERENCE + " = ?";
    private static final String REMOVE_PRODUCT_PURCHASE_WITH_CODE = "DELETE FROM " + TABLE_SALES_PURCHASES_RETURNS + " WHERE " +
            COLUMN_PRODUCT_PURCHASE_REFERENCE + " = ? AND " +
            COLUMN_PRODUCT_PURCHASE_ITEM_CODE + " = ?";

    private PreparedStatement insertSalePurchaseReturn;
    private PreparedStatement removeSalePurchaseReturn;
    private PreparedStatement removeSalePurchaseReturnWithCode;

    private final ObservableList<SalesPurchasesReturns> salesPurchasesReturns = FXCollections.observableArrayList();

    public ObservableList<SalesPurchasesReturns> getSalesPurchasesReturns() {
        return salesPurchasesReturns;
    }

    private static final String TABLE_SALES = "sales";
    private static final String COLUMN_SALE_DATE = "date";
    private static final String COLUMN_SALE_INVOICE_NO = "invoice_no";
    private static final String COLUMN_SALE_CUSTOMER = "customer";
    private static final String COLUMN_SALE_ITEMS = "items";
    private static final String COLUMN_SALE_QUANTITY = "quantity";
    private static final String COLUMN_SALE_GRAND_TOTAL = "grand_total";
    private static final String COLUMN_SALE_PAID_AMOUNT = "paid_amount";
    private static final String COLUMN_SALE_BALANCE = "balance";
    private static final String COLUMN_SALE_DUE_AMOUNT = "due_amount";
    private static final String COLUMN_SALE_PAYMENT_METHOD = "payment_method";
    private static final String COLUMN_SALE_PAYMENT_STATUS = "payment_status";
    private static final String COLUMN_SALE_CHEQUE_NO = "cheque_no";
    private static final String COLUMN_SALE_CHEQUE_DATE = "cheque_date";

    private static final String SELECT_SALES = "SELECT * FROM " + TABLE_SALES;
    private static final String INSERT_SALE = "INSERT INTO " + TABLE_SALES + " VALUES (?, ?, ?, ?, ? ,? ,? ,?, ?, ? ,?, ?, ?)";
    private static final String UPDATE_SALE = "UPDATE " + TABLE_SALES + " SET " +
            COLUMN_SALE_DATE + " = ?, " +
            COLUMN_SALE_CUSTOMER + " = ?, " +
            COLUMN_SALE_ITEMS + " = ?, " +
            COLUMN_SALE_QUANTITY + " = ?, " +
            COLUMN_SALE_GRAND_TOTAL + " = ?, " +
            COLUMN_SALE_PAID_AMOUNT + " = ?, " +
            COLUMN_SALE_BALANCE + " = ?, " +
            COLUMN_SALE_DUE_AMOUNT + " = ?, " +
            COLUMN_SALE_PAYMENT_METHOD + " = ?, " +
            COLUMN_SALE_PAYMENT_STATUS + " = ?, " +
            COLUMN_SALE_CHEQUE_NO + " = ?, " +
            COLUMN_SALE_CHEQUE_DATE + " = ? WHERE " + COLUMN_SALE_INVOICE_NO + " = ?";
    private static final String REMOVE_SALE = "DELETE FROM " + TABLE_SALES + " WHERE " + COLUMN_SALE_INVOICE_NO + " = ?";

    private PreparedStatement insertSale;
    private PreparedStatement updateSale;
    private PreparedStatement removeSale;

    private final ObservableList<Sale> sales = FXCollections.observableArrayList();

    public ObservableList<Sale> getSales() {
        return sales;
    }

    private static final String TABLE_RETURNS = "returns";
    private static final String COLUMN_RETURN_DATE = "date";
    private static final String COLUMN_RETURN_NO = "return_no";
    private static final String COLUMN_RETURN_SALE_PURCHASE_NO = "sale_purchase_no";
    private static final String COLUMN_RETURN_SUPPLIER_CUSTOMER = "supplier_customer";
    private static final String COLUMN_RETURN_ITEMS = "items";
    private static final String COLUMN_RETURN_QUANTITY = "quantity";
    private static final String COLUMN_RETURN_GRAND_TOTAL = "grand_total";

    private static final String SELECT_RETURNS = "SELECT * FROM " + TABLE_RETURNS;
    private static final String INSERT_RETURN = "INSERT INTO " + TABLE_RETURNS + " VALUES (?, ?, ?, ?, ? ,? ,?)";
    private static final String UPDATE_RETURN = "UPDATE " + TABLE_RETURNS + " SET " +
            COLUMN_RETURN_DATE + " = ?, " +
            COLUMN_RETURN_SUPPLIER_CUSTOMER + " = ?, " +
            COLUMN_RETURN_ITEMS + " = ?, " +
            COLUMN_RETURN_QUANTITY + " = ?, " +
            COLUMN_RETURN_GRAND_TOTAL + " = ? WHERE " + COLUMN_RETURN_NO + " = ?";
    private static final String REMOVE_RETURN = "DELETE FROM " + TABLE_RETURNS + " WHERE " + COLUMN_RETURN_NO + " = ?";

    private PreparedStatement insertReturn;
    private PreparedStatement updateReturn;
    private PreparedStatement removeReturn;

    private final ObservableList<Return> returns = FXCollections.observableArrayList();

    public ObservableList<Return> getReturns() {
        return returns;
    }

    private static final Model instance = new Model();

    public static Model getInstance() {
        return instance;
    }

    public Account loggedAccount;

    public Model() {

    }

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);

            queryUsername = conn.prepareStatement(QUERY_USERNAME);
            queryLogin = conn.prepareStatement(QUERY_LOGIN);
            insertUser = conn.prepareStatement(INSERT_USER);
            updateUser = conn.prepareStatement(UPDATE_USER);
            removeUser = conn.prepareStatement(REMOVE_USER);

            queryCategory = conn.prepareStatement(QUERY_CATEGORY);
            insertCategory = conn.prepareStatement(INSERT_CATEGORY);
            updateCategory = conn.prepareStatement(UPDATE_CATEGORY);
            removeCategory = conn.prepareStatement(REMOVE_CATEGORY);

            queryProduct = conn.prepareStatement(QUERY_PRODUCT);
            insertProduct = conn.prepareStatement(INSERT_PRODUCT);
            updateProduct = conn.prepareStatement(UPDATE_PRODUCT);
            updateProductQuantity = conn.prepareStatement(UPDATE_PRODUCT_QUANTITY);
            removeProduct = conn.prepareStatement(REMOVE_PRODUCT);

            queryCustomer = conn.prepareStatement(QUERY_CUSTOMER);
            insertCustomer = conn.prepareStatement(INSERT_CUSTOMER);
            updateCustomer = conn.prepareStatement(UPDATE_CUSTOMER);
            removeCustomer = conn.prepareStatement(REMOVE_CUSTOMER);

            queryPurchase = conn.prepareStatement(QUERY_PURCHASE);
            insertPurchase = conn.prepareStatement(INSERT_PURCHASE);
            updatePurchase = conn.prepareStatement(UPDATE_PURCHASE);
            removePurchase = conn.prepareStatement(REMOVE_PURCHASE);

            insertSalePurchaseReturn = conn.prepareStatement(INSERT_PRODUCT_PURCHASE);
            removeSalePurchaseReturn = conn.prepareStatement(REMOVE_PRODUCT_PURCHASE);
            removeSalePurchaseReturnWithCode = conn.prepareStatement(REMOVE_PRODUCT_PURCHASE_WITH_CODE);

            insertSale = conn.prepareStatement(INSERT_SALE);
            updateSale = conn.prepareStatement(UPDATE_SALE);
            removeSale = conn.prepareStatement(REMOVE_SALE);

            insertReturn = conn.prepareStatement(INSERT_RETURN);
            updateReturn = conn.prepareStatement(UPDATE_RETURN);
            removeReturn = conn.prepareStatement(REMOVE_RETURN);

            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_USERS +
                    " (" + COLUMN_USER_ID + " integer not null primary key, " +
                    COLUMN_USERNAME + " text not null, " +
                    COLUMN_USER_ROLE + " text not null, " +
                    COLUMN_PASSWORD + " text not null" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES +
                    " (" + COLUMN_CATEGORY_ID + " integer not null primary key, " +
                    COLUMN_CATEGORY + " text not null" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS +
                    " (" + COLUMN_PRODUCT_NAME + " text not null, " +
                    COLUMN_PRODUCT_BRAND + " text not null, " +
                    COLUMN_PRODUCT_CATEGORY + " text not null, " +
                    COLUMN_PRODUCT_CODE + " integer not null primary key, " +
                    COLUMN_PRODUCT_UNIT + " text not null, " +
                    COLUMN_PRODUCT_PRICE + " real not null, " +
                    COLUMN_PRODUCT_QUANTITY + " integer not null" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMERS +
                    " (" + COLUMN_CUSTOMER_ID + " integer not null primary key, " +
                    COLUMN_CUSTOMER_NAME + " text not null, " +
                    COLUMN_CUSTOMER_COMPANY_NAME + " text not null, " +
                    COLUMN_CUSTOMER_ADDRESS + " text not null, " +
                    COLUMN_CUSTOMER_PHONE_NUMBER + " text not null" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_PURCHASES +
                    " (" + COLUMN_PURCHASE_DATE + " text not null, " +
                    COLUMN_PURCHASE_REFERENCE_NO + " text not null, " +
                    COLUMN_PURCHASE_SUPPLIER + " text not null, " +
                    COLUMN_PURCHASE_ITEMS + " integer not null, " +
                    COLUMN_PURCHASE_QUANTITY + " integer not null, " +
                    COLUMN_PURCHASE_PRODUCT_COST + " real not null, " +
                    COLUMN_PURCHASE_SHIPPING_COST + " real not null, " +
                    COLUMN_PURCHASE_GRAND_TOTAL + " real not null" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_SALES_PURCHASES_RETURNS +
                    " (" + COLUMN_PRODUCT_PURCHASE_REFERENCE + " text not null, " +
                    COLUMN_PRODUCT_PURCHASE_ITEM_CODE + " integer not null, " +
                    COLUMN_PRODUCT_PURCHASE_QUANTITY + " integer not null, " +
                    COLUMN_PRODUCT_PURCHASE_PRODUCT_COST + " real not null" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_SALES +
                    " (" + COLUMN_SALE_DATE + " text not null, " +
                    COLUMN_SALE_INVOICE_NO + " text not null, " +
                    COLUMN_SALE_CUSTOMER + " text not null, " +
                    COLUMN_SALE_ITEMS + " integer not null, " +
                    COLUMN_SALE_QUANTITY + " integer not null, " +
                    COLUMN_SALE_GRAND_TOTAL + " real not null, " +
                    COLUMN_SALE_PAID_AMOUNT + " real not null, " +
                    COLUMN_SALE_BALANCE + " real not null, " +
                    COLUMN_SALE_DUE_AMOUNT + " real not null, " +
                    COLUMN_SALE_PAYMENT_METHOD + " text not null, " +
                    COLUMN_SALE_PAYMENT_STATUS + " text not null, " +
                    COLUMN_SALE_CHEQUE_NO + " integer, " +
                    COLUMN_SALE_CHEQUE_DATE + " text" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_RETURNS +
                    " (" + COLUMN_RETURN_DATE + " text not null, " +
                    COLUMN_RETURN_SALE_PURCHASE_NO + " text not null, " +
                    COLUMN_RETURN_NO + " text not null, " +
                    COLUMN_RETURN_SUPPLIER_CUSTOMER + " text not null, " +
                    COLUMN_RETURN_ITEMS + " integer not null, " +
                    COLUMN_RETURN_QUANTITY + " real not null, " +
                    COLUMN_RETURN_GRAND_TOTAL + " real not null" +
                    ")");

            return true;
        } catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            if (conn != null) {
                for (Product product : Model.getInstance().getProducts()) {
                    if (product.getQuantity() < 0) {
                        product.setQuantity(0);
                        setQueryUpdateProductQuantity(product.getQuantity(), product.getCode());
                    }
                }
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    public boolean setQueryUsername(String username) throws SQLException {
        queryUsername.setString(1, username);
        ResultSet resultSet = queryUsername.executeQuery();
        return resultSet.next();
    }

    public boolean setQueryLogin(String username, String password) throws SQLException {
        queryLogin.setString(1, username);
        queryLogin.setString(2, password);
        ResultSet resultSet = queryLogin.executeQuery();
        return resultSet.next();
    }

    public boolean setQueryInsertUser(String username, String user_role, String password) throws SQLException {
        if (!setQueryUsername(username)) {
            insertUser.setString(1, username);
            insertUser.setString(2, user_role);
            insertUser.setString(3, password);

            int affectedRows = insertUser.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert user!");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean setQueryUpdateUser(String username, String user_role, String password, int user_id) throws SQLException {
        updateUser.setString(1, username);
        updateUser.setString(2, user_role);
        updateUser.setString(3, password);
        updateUser.setInt(4, user_id);

        int affectedRows = updateUser.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't update user!");
        } else {
            return true;
        }
    }

    public boolean setQueryRemoveUser(int user_id) throws SQLException {
        removeUser.setInt(1, user_id);

        int affectedRows = removeUser.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't remove user!");
        } else {
            return true;
        }
    }

    public boolean setQueryCategory(String category) throws SQLException {
        queryCategory.setString(1, category);
        ResultSet resultSet = queryCategory.executeQuery();
        return resultSet.next();
    }

    public boolean setQueryInsertCategory(String category) throws SQLException {
        insertCategory.setString(1, category);
        int affectedRows = insertCategory.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't insert category!");
        } else {
            return true;
        }
    }

    public boolean setQueryUpdateCategory(String category, int category_id) throws SQLException {
        updateCategory.setString(1, category);
        updateCategory.setInt(2, category_id);
        int affectedRows = updateCategory.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't update category!");
        } else {
            return true;
        }
    }

    public boolean setQueryRemoveCategory(int category_id) throws SQLException {
        removeCategory.setInt(1, category_id);
        int affectedRows = removeCategory.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't remove category!");
        } else {
            return true;
        }
    }

    public boolean setQueryProduct(String name, String brand, int code) throws SQLException {
        queryProduct.setString(1, name);
        queryProduct.setString(2, brand);
        queryProduct.setInt(3, code);
        ResultSet resultSet = queryProduct.executeQuery();
        return resultSet.next();
    }

    public boolean setQueryInsertProduct(String name, String brand, String category, int code, String unit, double price, int quantity) throws SQLException {
        if (!setQueryProduct(name, brand, code)) {
            insertProduct.setString(1, name);
            insertProduct.setString(2, brand);
            insertProduct.setString(3, category);
            insertProduct.setInt(4, code);
            insertProduct.setString(5, unit);
            insertProduct.setDouble(6, price);
            insertProduct.setInt(7, quantity);
            int affectedRows = insertProduct.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert product!");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean setQueryUpdateProduct(String name, String brand, String category, int newCode, String unit, double price, int quantity, int oldCode) throws SQLException {
        updateProduct.setString(1, name);
        updateProduct.setString(2, brand);
        updateProduct.setString(3, category);
        updateProduct.setInt(4, newCode);
        updateProduct.setString(5, unit);
        updateProduct.setDouble(6, price);
        updateProduct.setInt(7, quantity);
        updateProduct.setInt(8, oldCode);
        int affectedRows = updateProduct.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't update product!");
        } else {
            return true;
        }
    }

    public boolean setQueryUpdateProductQuantity(int quantity, int code) throws SQLException {
        updateProductQuantity.setInt(1, quantity);
        updateProductQuantity.setInt(2, code);
        int affectedRows = updateProductQuantity.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't update product quantity!");
        } else {
            return true;
        }
    }

    public boolean setQueryRemoveProduct(int code) throws SQLException {
        removeProduct.setInt(1, code);
        int affectedRows = removeProduct.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't remove product!");
        } else {
            return true;
        }
    }

    public boolean setQueryCustomer(String name) throws SQLException {
        queryCustomer.setString(1, name);
        ResultSet resultSet = queryCustomer.executeQuery();
        return resultSet.next();
    }

    public boolean setQueryInsertCustomer(String name, String company_name, String address, String phone_number) throws SQLException {
        if (!setQueryCustomer(name)) {
            insertCustomer.setString(1, name);
            insertCustomer.setString(2, company_name);
            insertCustomer.setString(3, address);
            insertCustomer.setString(4, phone_number);
            int affectedRows = insertCustomer.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert customer!");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean setQueryUpdateCustomer(String name, String company_name, String address, String phone_number, int customer_id) throws SQLException {
        updateCustomer.setString(1, name);
        updateCustomer.setString(2, company_name);
        updateCustomer.setString(3, address);
        updateCustomer.setString(4, phone_number);
        updateCustomer.setInt(5, customer_id);
        int affectedRows = updateCustomer.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't update customer!");
        } else {
            return true;
        }
    }

    public boolean setQueryRemoveCustomer(int customer_id) throws SQLException {
        removeCustomer.setInt(1, customer_id);
        int affectedRows = removeCustomer.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't remove customer!");
        } else {
            return true;
        }
    }

    public boolean setQueryPurchase(String reference_no) throws SQLException {
        queryPurchase.setString(1, reference_no);
        ResultSet resultSet = queryPurchase.executeQuery();
        return resultSet.next();
    }

    public boolean setQueryInsertPurchase(String date, String reference_no, String supplier, int items, int quantity,
                                          double product_cost, double shipping_cost, double grand_total) throws SQLException {
        if (!setQueryPurchase(reference_no)) {
            insertPurchase.setString(1, date);
            insertPurchase.setString(2, reference_no);
            insertPurchase.setString(3, supplier);
            insertPurchase.setInt(4, items);
            insertPurchase.setInt(5, quantity);
            insertPurchase.setDouble(6, product_cost);
            insertPurchase.setDouble(7, shipping_cost);
            insertPurchase.setDouble(8, grand_total);

            int affectedRows = insertPurchase.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert purchase!");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean setQueryUpdatePurchase(String date, String supplier, int items, int quantity,
                                          double product_cost, double shipping_cost, double grand_total, String reference_no) throws SQLException {
        updatePurchase.setString(1, date);
        updatePurchase.setString(2, supplier);
        updatePurchase.setInt(3, items);
        updatePurchase.setInt(4, quantity);
        updatePurchase.setDouble(5, product_cost);
        updatePurchase.setDouble(6, shipping_cost);
        updatePurchase.setDouble(7, grand_total);
        updatePurchase.setString(8, reference_no);

        int affectedRows = updatePurchase.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't update purchase!");
        } else {
            return true;
        }
    }

    public boolean setQueryRemovePurchase(String reference_no) throws SQLException {
        removePurchase.setString(1, reference_no);
        int affectedRows = removePurchase.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't remove purchase!");
        } else {
            return true;
        }
    }

    public boolean setQueryInsertSalesPurchaseReturn(String reference_no, int code, int quantity, double cost) throws SQLException {
        insertSalePurchaseReturn.setString(1, reference_no);
        insertSalePurchaseReturn.setInt(2, code);
        insertSalePurchaseReturn.setInt(3, quantity);
        insertSalePurchaseReturn.setDouble(4, cost);

        int affectedRows = insertSalePurchaseReturn.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't insert product purchase!");
        } else {
            return true;
        }
    }

    public boolean setQueryRemoveSalesPurchaseReturn(String reference_no) throws SQLException {
        removeSalePurchaseReturn.setString(1, reference_no);
        int affectedRows = removeSalePurchaseReturn.executeUpdate();
        if (affectedRows < 1) {
            throw new SQLException("Couldn't remove product return!");
        } else {
            return true;
        }
    }

    public boolean setQueryRemoveSalesPurchaseReturnWithCode(String reference_no, int code) throws SQLException {
        removeSalePurchaseReturnWithCode.setString(1, reference_no);
        removeSalePurchaseReturnWithCode.setInt(2, code);
        int affectedRows = removeSalePurchaseReturnWithCode.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't remove product purchase with code!");
        } else {
            return true;
        }
    }

    public boolean setQueryInsertSale(String date, String invoice_no, String customer, int items, int quantity,
                                      double grand_total, double paid_amount, double balance, double due_amount,
                                      String payment_method, String payment_status, int chequeNo, String chequeDate) throws SQLException {
        insertSale.setString(1, date);
        insertSale.setString(2, invoice_no);
        insertSale.setString(3, customer);
        insertSale.setInt(4, items);
        insertSale.setInt(5, quantity);
        insertSale.setDouble(6, grand_total);
        insertSale.setDouble(7, paid_amount);
        insertSale.setDouble(8, balance);
        insertSale.setDouble(9, due_amount);
        insertSale.setString(10, payment_method);
        insertSale.setString(11, payment_status);
        insertSale.setInt(12, chequeNo);
        insertSale.setString(13, chequeDate);

        int affectedRows = insertSale.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't insert sale!");
        } else {
            return true;
        }
    }

    public boolean setQueryUpdateSale(String date, String invoice_no, String customer, int items, int quantity,
                                      double grand_total, double paid_amount, double balance, double due_amount,
                                      String payment_method, String payment_status, int chequeNo, String chequeDate) throws SQLException {
        updateSale.setString(1, date);
        updateSale.setString(2, customer);
        updateSale.setInt(3, items);
        updateSale.setInt(4, quantity);
        updateSale.setDouble(5, grand_total);
        updateSale.setDouble(6, paid_amount);
        updateSale.setDouble(7, balance);
        updateSale.setDouble(8, due_amount);
        updateSale.setString(9, payment_method);
        updateSale.setString(10, payment_status);
        updateSale.setInt(11, chequeNo);
        updateSale.setString(12, chequeDate);
        updateSale.setString(13, invoice_no);

        int affectedRows = updateSale.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't update sale!");
        } else {
            return true;
        }
    }

    public void setQueryRemoveSale(String invoice_no) throws SQLException {
        removeSale.setString(1, invoice_no);
        int affectedRows = removeSale.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't remove sale!");
        }
    }

    public boolean setQueryInsertReturn(String date, String sale_purchase_no, String return_no, String supplier_customer,
                                        int items, int quantity, double grand_total) throws SQLException {
        insertReturn.setString(1, date);
        insertReturn.setString(2, sale_purchase_no);
        insertReturn.setString(3, return_no);
        insertReturn.setString(4, supplier_customer);
        insertReturn.setInt(5, items);
        insertReturn.setInt(6, quantity);
        insertReturn.setDouble(7, grand_total);
        int affectedRows = insertReturn.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't insert return!");
        } else {
            return true;
        }
    }

    public boolean setQueryUpdateReturn(String date, String supplier_customer,
                                        int items, int quantity, double grand_total, String return_no) throws SQLException {
        updateReturn.setString(1, date);
        updateReturn.setString(2, supplier_customer);
        updateReturn.setInt(3, items);
        updateReturn.setInt(4, quantity);
        updateReturn.setDouble(5, grand_total);
        updateReturn.setString(6, return_no);
        int affectedRows = updateReturn.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't update return!");
        } else {
            return true;
        }
    }

    public void setQueryRemoveReturn(String return_no) throws SQLException {
        removeReturn.setString(1, return_no);
        int affectedRows = removeReturn.executeUpdate();
        if (affectedRows != 1) {
            throw new SQLException("Couldn't remove return!");
        }
    }

    public void load() throws SQLException {
        Statement selectStatement = conn.createStatement();
        ResultSet resultSet = selectStatement.executeQuery(SELECT_USERS);
        while (resultSet.next()) {
            Account account = new Account();
            account.setUser_id(resultSet.getInt(1));
            account.setUsername(resultSet.getString(2));
            account.setUser_role(resultSet.getString(3));
            account.setPassword(resultSet.getString(4));
            accounts.add(account);
        }

        resultSet = selectStatement.executeQuery(SELECT_CATEGORIES);
        while (resultSet.next()) {
            Category category = new Category();
            category.setCategory_id(resultSet.getInt(1));
            category.setCategory(resultSet.getString(2));
            categories.add(category);
        }

        resultSet = selectStatement.executeQuery(SELECT_PRODUCTS);
        while (resultSet.next()) {
            Product product = new Product();
            product.setName(resultSet.getString(1));
            product.setBrand(resultSet.getString(2));
            product.setCategory(resultSet.getString(3));
            product.setCode(resultSet.getInt(4));
            product.setUnit(resultSet.getString(5));
            product.setPrice(resultSet.getDouble(6));
            product.setQuantity(resultSet.getInt(7));
            products.add(product);
        }

        resultSet = selectStatement.executeQuery(SELECT_CUSTOMERS);
        while (resultSet.next()) {
            Customer customer = new Customer();
            customer.setId(resultSet.getInt(1));
            customer.setName(resultSet.getString(2));
            customer.setCompany_name(resultSet.getString(3));
            customer.setAddress(resultSet.getString(4));
            customer.setPhone_number(resultSet.getString(5));
            customers.add(customer);
        }

        resultSet = selectStatement.executeQuery(SELECT_PURCHASES);
        while (resultSet.next()) {
            Purchase purchase = new Purchase();
            purchase.setDate(resultSet.getString(1));
            purchase.setReference_no(resultSet.getString(2));
            purchase.setSupplier(resultSet.getString(3));
            purchase.setItems(resultSet.getInt(4));
            purchase.setQuantity(resultSet.getInt(5));
            purchase.setProduct_cost(resultSet.getDouble(6));
            purchase.setShipping_cost(resultSet.getDouble(7));
            purchase.setGrand_total(resultSet.getDouble(8));
            purchases.add(purchase);
        }

        resultSet = selectStatement.executeQuery(SELECT_PRODUCT_PURCHASES);
        while (resultSet.next()) {
            SalesPurchasesReturns productPurchase = new SalesPurchasesReturns();
            productPurchase.setReference(resultSet.getString(1));
            productPurchase.setCode(resultSet.getInt(2));
            productPurchase.setQuantity(resultSet.getInt(3));
            productPurchase.setPrice(resultSet.getDouble(4));
            salesPurchasesReturns.add(productPurchase);
        }

        resultSet = selectStatement.executeQuery(SELECT_SALES);
        while (resultSet.next()) {
            Sale sale = new Sale();
            sale.setDate(resultSet.getString(1));
            sale.setInvoice_no(resultSet.getString(2));
            sale.setCustomer(resultSet.getString(3));
            sale.setItems(resultSet.getInt(4));
            sale.setQuantity(resultSet.getInt(5));
            sale.setGrand_total(resultSet.getDouble(6));
            sale.setPaid_amount(resultSet.getDouble(7));
            sale.setBalance(resultSet.getDouble(8));
            sale.setDue_amount(resultSet.getDouble(9));
            sale.setPayment_method(resultSet.getString(10));
            sale.setPayment_status(resultSet.getString(11));
            sale.setChequeNo(resultSet.getInt(12));
            sale.setChequeDate(resultSet.getString(13));
            sales.add(sale);
        }

        resultSet = selectStatement.executeQuery(SELECT_RETURNS);
        while (resultSet.next()) {
            Return returnItem = new Return();
            returnItem.setDate(resultSet.getString(1));
            returnItem.setSale_purchase_no(resultSet.getString(2));
            returnItem.setReturn_no(resultSet.getString(3));
            returnItem.setSupplier_customer(resultSet.getString(4));
            returnItem.setItems(resultSet.getInt(5));
            returnItem.setQuantity(resultSet.getInt(6));
            returnItem.setGrand_total(resultSet.getDouble(7));
            returns.add(returnItem);
        }
    }
}

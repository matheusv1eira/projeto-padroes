import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ConnectionFactory {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "database.properties";
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            // Usar valores padrão se arquivo não existir
            properties.setProperty("db.url", "jdbc:sqlite:meubanco.db");
            properties.setProperty("db.driver", "org.sqlite.JDBC");
            System.out.println("Arquivo de configuração não encontrado. Usando valores padrão.");
        }
    }
    
    // Factory Method principal - Singleton pattern aplicado
    public static SQLConnection createConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        return createConnection(url);
    }
    
    // Factory Method com URL customizada
    public static SQLConnection createConnection(String url) throws SQLException {
        try {
            // Carregar driver dinamicamente
            String driver = properties.getProperty("db.driver");
            if (driver != null) {
                Class.forName(driver);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado: " + e.getMessage());
        }
        
        return new SQLConnection(url);
    }
    
    // Factory Method com parâmetros completos
    public static SQLConnection createConnection(String url, String user, String password) throws SQLException {
        try {
            String driver = properties.getProperty("db.driver");
            if (driver != null) {
                Class.forName(driver);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado", e);
        }
        
        return new SQLConnection(url, user, password);
    }
    
    // Método para testar conexão
    public static boolean testConnection() {
        try (SQLConnection conn = createConnection()) {
            return conn.getConnection() != null && !conn.getConnection().isClosed();
        } catch (SQLException e) {
            System.err.println("Falha no teste de conexão: " + e.getMessage());
            return false;
        }
    }
}

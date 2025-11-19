import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLConnection implements AutoCloseable {
    private Connection connection;
    private boolean isClosed = false;
    
    // Construtor básico
    public SQLConnection(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
        System.out.println("Conexão estabelecida: " + url);
    }
    
    // Construtor com autenticação
    public SQLConnection(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
        System.out.println("Conexão estabelecida com autenticação: " + url);
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    // Método close melhorado
    @Override
    public void close() {
        if (!isClosed && connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Conexão fechada com sucesso.");
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            } finally {
                isClosed = true;
            }
        }
    }
    
    // Execute query com tratamento melhorado
    public ResultSet executeQuery(String sql) throws SQLException {
        checkConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }
    
    // Execute update com tratamento melhorado
    public int executeUpdate(String sql) throws SQLException {
        checkConnection();
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
    }
    
    // Prepared Statement para segurança
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkConnection();
        return connection.prepareStatement(sql);
    }
    
    // Método para executar queries com parâmetros
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        checkConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);
        setParameters(stmt, params);
        return stmt.executeQuery();
    }
    
    // Método para updates com parâmetros
    public int executeUpdate(String sql, Object... params) throws SQLException {
        checkConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }
    
    // Transações
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkConnection();
        connection.setAutoCommit(autoCommit);
    }
    
    public void commit() throws SQLException {
        checkConnection();
        connection.commit();
    }
    
    public void rollback() throws SQLException {
        checkConnection();
        connection.rollback();
    }
    
    // Método para verificar metadados
    public DatabaseMetaData getMetaData() throws SQLException {
        checkConnection();
        return connection.getMetaData();
    }
    
    // Batch operations
    public void addBatch(String sql) throws SQLException {
        checkConnection();
        try (Statement stmt = connection.createStatement()) {
            stmt.addBatch(sql);
        }
    }
    
    public int[] executeBatch() throws SQLException {
        checkConnection();
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeBatch();
        }
    }
    
    // Método auxiliar para definir parâmetros
    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
    
    // Verificar se conexão está válida
    private void checkConnection() throws SQLException {
        if (isClosed || connection == null || connection.isClosed()) {
            throw new SQLException("Conexão está fechada ou inválida");
        }
    }
    
    // Método para verificar se tabela existe
    public boolean tableExists(String tableName) throws SQLException {
        checkConnection();
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet tables = meta.getTables(null, null, tableName, null)) {
            return tables.next();
        }
    }
    
    // Método para obter todas as tabelas
    public List<String> getTables() throws SQLException {
        checkConnection();
        List<String> tables = new ArrayList<>();
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }
        return tables;
    }
}

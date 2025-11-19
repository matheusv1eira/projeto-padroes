import java.sql.*;

public class TesteConexao {
    public static void main(String[] args) {
        SQLConnection sqlConnection = null;
        
        try {
            System.out.println("=== Testando ConnectionFactory ===");
            
            // Usando a fábrica para criar conexão
            sqlConnection = ConnectionFactory.createConnection();
            System.out.println("Conexão criada com sucesso!");
            
            // Criar uma tabela de teste
            String createSQL = "CREATE TABLE IF NOT EXISTS teste (id INTEGER PRIMARY KEY, nome TEXT)";
            sqlConnection.executeUpdate(createSQL);
            System.out.println("Tabela criada/verificada");
            
            // Inserir um registro
            String insertSQL = "INSERT OR IGNORE INTO teste (id, nome) VALUES (1, 'Exemplo')";
            int rows = sqlConnection.executeUpdate(insertSQL);
            System.out.println(rows + " linha(s) inserida(s)");
            
            // Consultar dados
            ResultSet result = sqlConnection.executeQuery("SELECT * FROM teste");
            System.out.println("\n=== Dados na tabela ===");
            while (result.next()) {
                System.out.println("ID: " + result.getInt("id") + ", Nome: " + result.getString("nome"));
            }
            result.close();
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (sqlConnection != null) {
                try {
                    sqlConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

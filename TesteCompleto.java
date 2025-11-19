import java.sql.*;

public class TesteCompleto {
    public static void main(String[] args) {
        System.out.println("=== TESTE COMPLETO CONNECTION FACTORY ===");
        
        // Testar conexão primeiro
        if (!ConnectionFactory.testConnection()) {
            System.err.println("Falha no teste de conexão!");
            return;
        }
        
        // Usar try-with-resources para fechamento automático
        try (SQLConnection sqlConnection = ConnectionFactory.createConnection()) {
            
            System.out.println("\n1. Testando transações...");
            sqlConnection.setAutoCommit(false);
            
            // Criar tabela
            String createSQL = "CREATE TABLE IF NOT EXISTS produtos (" +
                              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                              "nome TEXT NOT NULL, " +
                              "preco REAL, " +
                              "quantidade INTEGER)";
            sqlConnection.executeUpdate(createSQL);
            System.out.println("Tabela criada/verificada");
            
            // Inserir dados com parâmetros
            String insertSQL = "INSERT INTO produtos (nome, preco, quantidade) VALUES (?, ?, ?)";
            int rows1 = sqlConnection.executeUpdate(insertSQL, "Notebook", 2500.00, 10);
            int rows2 = sqlConnection.executeUpdate(insertSQL, "Mouse", 45.50, 25);
            System.out.println(rows1 + rows2 + " produtos inseridos");
            
            // Commit da transação
            sqlConnection.commit();
            sqlConnection.setAutoCommit(true);
            
            System.out.println("\n2. Consultando dados...");
            String selectSQL = "SELECT * FROM produtos";
            try (ResultSet result = sqlConnection.executeQuery(selectSQL)) {
                System.out.println("ID | Nome      | Preco   | Quantidade");
                System.out.println("-----------------------------------");
                while (result.next()) {
                    int id = result.getInt("id");
                    String nome = result.getString("nome");
                    double preco = result.getDouble("preco");
                    int quantidade = result.getInt("quantidade");
                    System.out.printf("%-2d | %-9s | %-7.2f | %-10d%n", id, nome, preco, quantidade);
                }
            }
            
            System.out.println("\n3. Testando metadados...");
            if (sqlConnection.tableExists("produtos")) {
                System.out.println("Tabela 'produtos' existe no banco");
            }
            
            System.out.println("\n4. Listando tabelas...");
            var tabelas = sqlConnection.getTables();
            System.out.println("Tabelas no banco: " + tabelas);
            
            System.out.println("\n5. Testando prepared statements...");
            String updateSQL = "UPDATE produtos SET quantidade = ? WHERE nome = ?";
            int updated = sqlConnection.executeUpdate(updateSQL, 30, "Mouse");
            System.out.println(updated + " linha(s) atualizada(s)");
            
        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro geral: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== TESTE CONCLUÍDO ===");
    }
}

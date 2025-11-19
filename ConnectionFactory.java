import java.sql.SQLException;

public class ConnectionFactory {
    // Configuração do banco (exemplo com SQLite)
    private static final String URL = "jdbc:sqlite:meubanco.db";

    // Método estático para criar/conectar ao banco
    public static SQLConnection createConnection() throws SQLException {
        return new SQLConnection(URL);
    }

    // Versão com parâmetros flexíveis (exemplo alternativo)
    public static SQLConnection createConnection(String url) throws SQLException {
        return new SQLConnection(url);
    }
}

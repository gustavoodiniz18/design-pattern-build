import java.sql.*;

class Casa {
    private int id;
    private double valorOrcamento;
    private int qtdItens;
    private double descontoOrcamento;

    private Casa(Builder builder) {
        this.valorOrcamento = builder.valorOrcamento;
        this.qtdItens = builder.qtdItens;
        this.descontoOrcamento = builder.descontoOrcamento;
    }

    public static class Builder {
        private double valorOrcamento;
        private int qtdItens;
        private double descontoOrcamento;

        public Builder valorOrcamento(double valorOrcamento) {
            this.valorOrcamento = valorOrcamento;
            return this;
        }

        public Builder qtdItens(int qtdItens) {
            this.qtdItens = qtdItens;
            return this;
        }

        public Builder descontoOrcamento(double descontoOrcamento) {
            this.descontoOrcamento = descontoOrcamento;
            return this;
        }

        public Casa build() {
            return new Casa(this);
        }
    }

    // Getters and setters
}

public class Main {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/money";

    static final String USER = "root";
    static final String PASS = "money123";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            System.out.println("Conectando ao banco de dados...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Execute a query
            System.out.println("Criando tabela...");
            stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS casa " +
                         "(id INTEGER not NULL AUTO_INCREMENT, " +
                         " valorOrcamento DECIMAL(10,2), " +
                         " qtdItens INTEGER, " +
                         " descontoOrcamento DECIMAL(5,2), " +
                         " PRIMARY KEY ( id ))";
            stmt.executeUpdate(sql);
            System.out.println("Tabela criada ou já existente...");

            // Insert data
            System.out.println("Inserindo dados na tabela...");
            Casa casa1 = new Casa.Builder()
                            .valorOrcamento(100.00)
                            .qtdItens(5)
                            .descontoOrcamento(10.00)
                            .build();
            inserirCasa(conn, casa1);

            // Retrieve data
            System.out.println("Buscando dados da tabela...");
            buscarCasas(conn);

            // Update data
            System.out.println("Atualizando dados da tabela...");
            Casa casaAtualizada = new Casa.Builder()
                                        .valorOrcamento(150.00)
                                        .qtdItens(7)
                                        .descontoOrcamento(20.00)
                                        .build();
            atualizarCasa(conn, 1, casaAtualizada);

            // Retrieve updated data
            System.out.println("Buscando dados atualizados da tabela...");
            buscarCasas(conn);

            // Delete data
            System.out.println("Deletando dados da tabela...");
            deletarCasa(conn, 1);

            // Retrieve data after deletion
            System.out.println("Buscando dados após deleção da tabela...");
            buscarCasas(conn);

            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // Finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            } // nothing we can do
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
    } // end main

    static void inserirCasa(Connection conn, Casa casa) throws SQLException {
        String sql = "INSERT INTO casa (valorOrcamento, qtdItens, descontoOrcamento) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, casa.getValorOrcamento());
            pstmt.setInt(2, casa.getQtdItens());
            pstmt.setDouble(3, casa.getDescontoOrcamento());
            pstmt.executeUpdate();
        }
    }

    static void buscarCasas(Connection conn) throws SQLException {
        String sql = "SELECT id, valorOrcamento, qtdItens, descontoOrcamento FROM casa";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                double valorOrcamento = rs.getDouble("valorOrcamento");
                int qtdItens = rs.getInt("qtdItens");
                double descontoOrcamento = rs.getDouble("descontoOrcamento");

                System.out.print("ID: " + id);
                System.out.print(", Valor do Orçamento: " + valorOrcamento);
                System.out.print(", Quantidade de Itens: " + qtdItens);
                System.out.println(", Desconto do Orçamento: " + descontoOrcamento);
            }
        }
    }

    static void atualizarCasa(Connection conn, int id, Casa casa) throws SQLException {
        String sql = "UPDATE casa SET valorOrcamento=?, qtdItens=?, descontoOrcamento=? WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, casa.getValorOrcamento());
            pstmt.setInt(2, casa.getQtdItens());
            pstmt.setDouble(3, casa.getDescontoOrcamento());
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        }
    }

    static void deletarCasa(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM casa WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import model.Interceptacao;

/**
 *
 * @author Vitor
 */
public class InterceptadorDAO {
    
    // MÉTODO DE CRIAÇÃO DA TABELA
    public void criarNovaTabela(Connection conn, String alvo, String url, String base, String usuario, String senha, String tabela) throws SQLException {
        
        String sql = "CREATE TABLE IF NOT EXISTS `"+tabela+"` (\n" +
                    "  `id` int(16) NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                    "  `ipOrigem` varchar(20) NOT NULL,\n" +
                    "  `ipDestino` varchar(20) NOT NULL,\n" +
                    "  `portaOrigem` int(6) NOT NULL,\n" +
                    "  `portaDestino` int(6) NOT NULL,\n" +
                    "  `sequencia` varchar(20) NOT NULL,\n" +
                    "  `maxSaltos` int(5) NOT NULL,\n" +
                    "  `dateTime` datetime NOT NULL,\n" +
                    "  `ack` varchar(12) NOT NULL,\n" +
                    "  `tamanho` int(6) NOT NULL,\n" +
                    "  `janela` int(6) NOT NULL\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        
        try (Statement stmt = conn.createStatement() ) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }    

    // MÉTODO INSERIR
    public void inserir(Connection conn, Interceptacao pac, String tabela) throws SQLException{
        String sql = "INSERT INTO "+tabela+"(ipOrigem, ipDestino, portaOrigem, portaDestino, sequencia, maxSaltos, dateTime, ack, tamanho, janela) values (?,?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, pac.getIpOrigem());
            stm.setString(2, pac.getIpDestino());
            stm.setInt(3, pac.getPortaOrigem());
            stm.setInt(4, pac.getPortaDestino());
            stm.setLong(5, pac.getSequencia());
            stm.setInt(6, pac.getSaltos());
            stm.setTimestamp(7, pac.getTimestamp());
            stm.setLong(8, pac.getAck());
            stm.setInt(9, pac.getTamanho());
            stm.setInt(10, pac.getJanela());
            stm.executeUpdate();
        }
    }

}

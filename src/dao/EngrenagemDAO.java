package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import model.ResultadoDeCalculo;

/**
 *
 * @author Vitor
 */
public class EngrenagemDAO {
    
    // MÉTODO DE CRIAÇÃO DA TABELA
    public void criarNovaTabela(Connection conn, String alvo, String url, String base, String usuario, String senha, String tabela) {
        
    String sql = "CREATE TABLE IF NOT EXISTS `"+tabela+"` (\n" +
                "  `id` int(16) NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                "  `ipOrigem` varchar(20) NOT NULL,\n" +
                "  `ipDestino` varchar(20) NOT NULL,\n" +
                "  `ftp` double(16,4) NOT NULL,\n" +
                "  `ssh` double(16,4) NOT NULL,\n" +
                "  `smtp` double(16,4) NOT NULL,\n" +
                "  `http` double(16,4) NOT NULL,\n" +
                "  `https` double(16,4) NOT NULL,\n" +
                "  `outros` double(16,4) NOT NULL,\n" +
                "  `data` datetime NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        
        try (Statement stmt = conn.createStatement() ) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }    

    // MÉTODO INSERIR
    public void inserir(Connection conn, ResultadoDeCalculo pacs, String tabelaSaida) throws SQLException{
            String sql = "INSERT INTO "+tabelaSaida+"(ipOrigem, ipDestino, ftp, ssh, smtp, http, https, outros, data) values (?,?,?,?,?,?,?,?,?)";
            try(PreparedStatement stm = conn.prepareStatement(sql)){
                stm.setString(1, pacs.getIpOrigem());
                stm.setString(2, pacs.getIpDestino());
                stm.setDouble(3, pacs.getFtp());
                stm.setDouble(4, pacs.getSsh());
                stm.setDouble(5, pacs.getSmtp());
                stm.setDouble(6, pacs.getHttp());
                stm.setDouble(7, pacs.getHttps());
                stm.setDouble(8,  pacs.getOutros());
                stm.setString(9, pacs.getData());
                stm.executeUpdate();
            }
    }

}

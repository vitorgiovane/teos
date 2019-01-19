package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import model.DescricaoDaInterceptacao;
import util.Conexao;

/**
 *
 * @author Vitor
 */
public class DescricaoDaInterceptacaoDAO {
    private final Conexao conexaoDescricaoDaInterceptacao = new Conexao();
    
    // MÉTODO DE CRIAÇÃO DA TABELA
    public void criarNovaTabela(String alvo, String url, String base, String usuario, String senha) throws SQLException {
        
        String sql = "CREATE TABLE IF NOT EXISTS `descricaoDaInterceptacao` (\n" +
                    "  `id` int(16) NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                    "  `inicio` datetime NOT NULL,\n" +
                    "  `fim` datetime NOT NULL,\n" +
                    "  `url` varchar(64) NOT NULL,\n" +
                    "  `base` varchar(32) NOT NULL,\n" +
                    "  `tabela` varchar(32) NOT NULL,\n" +
                    "  `usuario` varchar(16) NOT NULL,\n" +
                    "  `interfaceDeRede` varchar(128) NOT NULL\n" +
                    ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        
        conexaoDescricaoDaInterceptacao.setUrl(url);
        conexaoDescricaoDaInterceptacao.setBase(base);
        conexaoDescricaoDaInterceptacao.setUsuario(usuario);
        conexaoDescricaoDaInterceptacao.setSenha(senha);
        
        try (Statement stmt = conexaoDescricaoDaInterceptacao.getConexao().createStatement() ) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }    

    // MÉTODO INSERIR
    public void inserir(DescricaoDaInterceptacao pac) throws SQLException{
            String sql = "INSERT INTO descricaoDaInterceptacao(inicio, fim, url, base, tabela, usuario, interfaceDeRede) values (?,?,?,?,?,?,?)";
            try(PreparedStatement stm = conexaoDescricaoDaInterceptacao.getConexao().prepareStatement(sql)){
                stm.setTimestamp(1, pac.getInicio());
                stm.setTimestamp(2, pac.getFim());
                stm.setString(3, pac.getUrl());
                stm.setString(4, pac.getBase());
                stm.setString(5, pac.getTabela());
                stm.setString(6, pac.getUsuario());
                stm.setString(7, pac.getInterfaceDeRede());
                stm.executeUpdate();
            }
    }
}// FIM DA CLASSE
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Vitor
 */
public class Conexao {
    private static String ALVO;
    private String url;
    private String base;
    private String usuario;
    private String senha;
    private static Connection conn;
    
    public Connection getConexao() throws SQLException{
        conn = DriverManager.getConnection(ALVO+url+"/"+base,usuario,senha);
        return conn;
    }
    
    public Conexao(){
        ALVO = "jdbc:mysql://";
    }

    public static String getALVO() {
        return ALVO;
    }

    public static void setALVO(String ALVO) {
        Conexao.ALVO = ALVO;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public static Connection getConn() {
        return conn;
    }

    public static void setConn(Connection conn) {
        Conexao.conn = conn;
    }
    
    
}

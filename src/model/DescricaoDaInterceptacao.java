package model;

import java.sql.Timestamp;

/**
 *
 * @author Vitor
 */
public class DescricaoDaInterceptacao {
    private Timestamp inicio;
    private Timestamp fim;
    private String url;
    private String base;
    private String tabela;
    private String usuario;
    private String interfaceDeRede;

    public Timestamp getInicio() {
        return inicio;
    }

    public void setInicio(Timestamp inicio) {
        this.inicio = inicio;
    }

    public Timestamp getFim() {
        return fim;
    }

    public void setFim(Timestamp fim) {
        this.fim = fim;
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

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getInterfaceDeRede() {
        return interfaceDeRede;
    }

    public void setInterfaceDeRede(String interfaceDeRede) {
        this.interfaceDeRede = interfaceDeRede;
    }
}

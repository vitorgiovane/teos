package controller;

import dao.EngrenagemDAO;
import java.sql.ResultSet;
import model.ResultadoDeCalculo;
import util.Conexao;

/**
 *
 * @author Vitor
 */
public class Engrenagem {
    protected String ALVO = "jdbc:mysql://";
    protected String url;
    protected String baseEntrada;
    protected String tabelaEntrada;
    protected String usuarioEntrada;
    protected String senhaEntrada;
    
    protected String baseSaida;
    protected String tabelaSaida;
    protected String usuarioSaida;
    protected String senhaSaida;
    
    protected int ano;
    protected int mes;
    protected int diaInicial;
    protected int diaFinal;
    
    protected ResultSet matrizIpoIpd;
    protected EngrenagemDAO engrenagemDAO = new EngrenagemDAO();
    protected ResultadoDeCalculo resultadoDeCalculo = new ResultadoDeCalculo();
    protected Conexao conexao = new Conexao();
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaseEntrada() {
        return baseEntrada;
    }

    public void setBaseEntrada(String baseEntrada) {
        this.baseEntrada = baseEntrada;
    }

    public String getTabelaEntrada() {
        return tabelaEntrada;
    }

    public void setTabelaEntrada(String tabelaEntrada) {
        this.tabelaEntrada = tabelaEntrada;
    }

    public String getUsuarioEntrada() {
        return usuarioEntrada;
    }

    public void setUsuarioEntrada(String usuarioEntrada) {
        this.usuarioEntrada = usuarioEntrada;
    }

    public String getSenhaEntrada() {
        return senhaEntrada;
    }

    public void setSenhaEntrada(String senhaEntrada) {
        this.senhaEntrada = senhaEntrada;
    }

    public String getBaseSaida() {
        return baseSaida;
    }

    public void setBaseSaida(String baseSaida) {
        this.baseSaida = baseSaida;
    }

    public String getTabelaSaida() {
        return tabelaSaida;
    }

    public void setTabelaSaida(String tabelaSaida) {
        this.tabelaSaida = tabelaSaida;
    }

    public String getUsuarioSaida() {
        return usuarioSaida;
    }

    public void setUsuarioSaida(String usuarioSaida) {
        this.usuarioSaida = usuarioSaida;
    }

    public String getSenhaSaida() {
        return senhaSaida;
    }

    public void setSenhaSaida(String senhaSaida) {
        this.senhaSaida = senhaSaida;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDiaInicial() {
        return diaInicial;
    }

    public void setDiaInicial(int diaInicial) {
        this.diaInicial = diaInicial;
    }

    public int getDiaFinal() {
        return diaFinal;
    }

    public void setDiaFinal(int diaFinal) {
        this.diaFinal = diaFinal;
    }
}

package controller;

import dao.InterceptadorDAO;
import model.Interceptacao;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import util.Conexao;

public class Interceptador {
    
/* ATRIBUTOS */
private JpcapCaptor captor;

private final String alvo = "jdbc:mysql://";
private String url;
private String base;
private String usuario;
private String senha;
private String tabela;
private int interfaceEscolhidaIndex;
private String interfaceEscolhidaItem;
private int totalInterfaces;
private boolean status;
private final InterceptadorDAO interceptadorDAO = new InterceptadorDAO();
private final Conexao conexaoInterceptador = new Conexao();

    public void Interceptar() throws SQLException {
    NetworkInterface[] list = new NetworkInterface[totalInterfaces];
    try {

        list = JpcapCaptor.getDeviceList();
        captor = JpcapCaptor.openDevice(list[interfaceEscolhidaIndex], 65535, false, 20);

        /* FILTROS - ENXERGANDO SOMENTE PACOTES TCP/IP */
       captor.setFilter("ip and tcp", true);
    }
    catch(IOException ioe) {
        JOptionPane.showMessageDialog(null, "<html><body>Não é possível interceptar os pacotes trafegados pela interface escolhida.<br>"
                + "O TEOS foi desenvolvido para interceptar somente pacotes TCP/IP de interfaces conectadas à internet.</body></html>","Interceptação não iniciada, escolha outra interface", JOptionPane.INFORMATION_MESSAGE);

        //System.out.println(ioe.getMessage());
    }

    /* ------------- COMEÇANDO A INTERCEPTAR OS PACOTES --------------- */
    conexaoInterceptador.setUrl(url);
    conexaoInterceptador.setBase(base);
    conexaoInterceptador.setUsuario(usuario);
    conexaoInterceptador.setSenha(senha);
    try(Connection conn = conexaoInterceptador.getConexao()){
        interceptadorDAO.criarNovaTabela(conn, alvo, url, base, usuario, senha, tabela);
        Interceptacao teos = new Interceptacao();
        
        while (status == true) {
            Packet informac = captor.getPacket();
            TCPPacket tcp = (TCPPacket) informac;

            if(informac != null){
                teos.setIpOrigem(tcp.src_ip.toString());
                teos.setIpDestino(tcp.dst_ip.toString());
                teos.setPortaOrigem(tcp.src_port);
                teos.setPortaDestino(tcp.dst_port);
                teos.setSequencia(tcp.sequence);
                teos.setSaltos(tcp.hop_limit);

                java.sql.Timestamp dt = new java.sql.Timestamp (converteT2D(tcp.sec).getTime());
                teos.setTimestamp(dt);

                teos.setAck(tcp.ack_num);
                teos.setTamanho(tcp.caplen);
                teos.setJanela(tcp.window);
            }                  

           //System.out.println(crud.getIpOrigem());
            if(teos.getIpOrigem() != null){
                interceptadorDAO.inserir(conn, teos, tabela);
            }
        }
    } catch (ParseException ex) {
        Logger.getLogger(Interceptacao.class.getName()).log(Level.SEVERE, null, ex);
    }
}
    
    public Timestamp converteT2D(long time) throws ParseException{
        long unixSeconds = time;
        Timestamp date = new Timestamp(unixSeconds*1000L); // *1000 CONVERTE SEGUNDOS EM MILISEGUNDOS         
        return date;
    }

    public String getALVO() {
        return alvo;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getBASE() {
        return base;
    }

    public void setBASE(String base) {
        this.base = base;
    }

    public String getUSUARIO() {
        return usuario;
    }

    public void setUSUARIO(String usuario) {
        this.usuario = usuario;
    }

    public String getSENHA() {
        return senha;
    }

    public void setSENHA(String senha) {
        this.senha = senha;
    }

    public String getTABELA() {
        return tabela;
    }

    public void setTABELA(String tabela) {
        this.tabela = tabela;
    }

    public int getInterfaceEscolhidaIndex() {
        return interfaceEscolhidaIndex;
    }

    public void setInterfaceEscolhidaIndex(int interfaceEscolhida) {
        this.interfaceEscolhidaIndex = interfaceEscolhida;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getTotalInterfaces() {
        return totalInterfaces;
    }

    public void setTotalInterfaces(int totalInterfaces) {
        this.totalInterfaces = totalInterfaces;
    }

    public String getInterfaceEscolhidaItem() {
        return interfaceEscolhidaItem;
    }

    public void setInterfaceEscolhidaItem(String interfaceEscolhidaItem) {
        this.interfaceEscolhidaItem = interfaceEscolhidaItem;
    }

}


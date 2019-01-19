package model;

import java.sql.Timestamp;

public class Interceptacao {
    private String ipOrigem;
    private String ipDestino;
    private int portaOrigem;
    private int portaDestino;
    private long sequencia;
    private int saltos;
    private Timestamp timestamp;
    private long ack;
    private int tamanho;
    private int janela;

    public String getIpOrigem() {
        return ipOrigem;
    }

    public void setIpOrigem(String ipOrigem) {
        this.ipOrigem = ipOrigem;
    }

    public String getIpDestino() {
        return ipDestino;
    }

    public void setIpDestino(String ipDestino) {
        this.ipDestino = ipDestino;
    }

    public int getPortaOrigem() {
        return portaOrigem;
    }

    public void setPortaOrigem(int portaOrigem) {
        this.portaOrigem = portaOrigem;
    }

    public int getPortaDestino() {
        return portaDestino;
    }

    public void setPortaDestino(int portaDestino) {
        this.portaDestino = portaDestino;
    }

    public long getSequencia() {
        return sequencia;
    }

    public void setSequencia(long sequencia) {
        this.sequencia = sequencia;
    }

    public int getSaltos() {
        return saltos;
    }

    public void setSaltos(int saltos) {
        this.saltos = saltos;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public long getAck() {
        return ack;
    }

    public void setAck(long ack) {
        this.ack = ack;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public int getJanela() {
        return janela;
    }

    public void setJanela(int janela) {
        this.janela = janela;
    }
}
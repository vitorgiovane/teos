package model;

/**
 *
 * @author Vitor
 */
public class ResultadoDeCalculo {
    private String ipOrigem;
    private String ipDestino;
    private double ftp;
    private double ssh;
    private double smtp;
    private double http;
    private double https;
    private double outros;
    private String data;
    
    public String getIpOrigem() {
        return ipOrigem;
    }

    public String getIpDestino() {
        return ipDestino;
    }

    public void setIpDestino(String ipDestino) {
        this.ipDestino = ipDestino;
    }

    public double getFtp() {
        return ftp;
    }

    public void setFtp(double ftp) {
        this.ftp = ftp;
    }

    public double getSsh() {
        return ssh;
    }

    public void setSsh(double ssh) {
        this.ssh = ssh;
    }

    public double getSmtp() {
        return smtp;
    }

    public void setSmtp(double smtp) {
        this.smtp = smtp;
    }

    public double getHttp() {
        return http;
    }

    public void setHttp(double http) {
        this.http = http;
    }

    public double getHttps() {
        return https;
    }

    public void setHttps(double https) {
        this.https = https;
    }

    public double getOutros() {
        return outros;
    }

    public void setOutros(double outros) {
        this.outros = outros;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setIpOrigem(String ipOrigem) {
        this.ipOrigem = ipOrigem;
    }

   

}
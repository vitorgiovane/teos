package controller;

import java.sql.*;
import model.ResultadoDeCalculo;

public class EngrenagemPerda extends Engrenagem{
    
    private int totalPacotesFtp;
    private int totalPacotesSsh;
    private int totalPacotesSmtp;
    private int totalPacotesHttp;
    private int totalPacotesHttps;
    private int totalPacotesOutros;
    
    private int repetidosFtp;
    private int repetidosSsh;
    private int repetidosSmtp;
    private int repetidosHttp;
    private int repetidosHttps;
    private int repetidosOutros;
    
    private ResultSet totalDePacotesPorPortaPorHora;
    private ResultSet pacotesSemelhantesPorPorta;    
    
    
    public void calcularPerda() throws SQLException{

        // INICIA A LEITURA DOS CAMPOS DO objeto instanciado no JFrameTeos
        conexao.setUrl(url);
        conexao.setBase(baseSaida);
        conexao.setUsuario(usuarioSaida);
        conexao.setSenha(senhaSaida);
        
        try (Connection conn = conexao.getConexao()) {
            // TENTA CRIAR TABELA
            engrenagemDAO.criarNovaTabela(conn, ALVO, url, baseSaida, usuarioSaida, senhaSaida, tabelaSaida);
            
            Statement stmt = conn.createStatement();
            Statement statement = conn.createStatement();
            pacotesSemelhantesPorPorta = null;
            matrizIpoIpd = null;
            
            int minutosIniciais = 00;
            
            for(int cD = diaInicial; cD <= diaFinal; cD++){ //LAÇO DOS DIAS
                for(int cH = 0; cH < 24; cH++){ // LAÇO DAS HORAS

                    totalDePacotesPorPortaPorHora = statement.executeQuery("SELECT COUNT(id) AS totalPacotes FROM "+tabelaEntrada+" WHERE portaDestino = 21 AND dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":0:00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59'");
                    while(totalDePacotesPorPortaPorHora.next()){
                        totalPacotesFtp = totalDePacotesPorPortaPorHora.getInt("totalPacotes");
                    }
                    
                    totalDePacotesPorPortaPorHora = statement.executeQuery("SELECT COUNT(id) AS totalPacotes FROM "+tabelaEntrada+" WHERE portaDestino = 22 AND dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":0:00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59'");
                    while(totalDePacotesPorPortaPorHora.next()){
                        totalPacotesSsh = totalDePacotesPorPortaPorHora.getInt("totalPacotes");
                    }
                    
                    totalDePacotesPorPortaPorHora = statement.executeQuery("SELECT COUNT(id) AS totalPacotes FROM "+tabelaEntrada+" WHERE portaDestino = 25 AND dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":0:00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59'");
                    while(totalDePacotesPorPortaPorHora.next()){
                        totalPacotesSmtp = totalDePacotesPorPortaPorHora.getInt("totalPacotes");
                    }
                    
                    totalDePacotesPorPortaPorHora = statement.executeQuery("SELECT COUNT(id) AS totalPacotes FROM "+tabelaEntrada+" WHERE portaDestino = 80 AND dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":0:00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59'");
                    while(totalDePacotesPorPortaPorHora.next()){
                        totalPacotesHttp = totalDePacotesPorPortaPorHora.getInt("totalPacotes");
                    }
                    
                    totalDePacotesPorPortaPorHora = statement.executeQuery("SELECT COUNT(id) AS totalPacotes FROM "+tabelaEntrada+" WHERE portaDestino = 443 AND dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":0:00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59'");
                    while(totalDePacotesPorPortaPorHora.next()){
                        totalPacotesHttps = totalDePacotesPorPortaPorHora.getInt("totalPacotes");
                    }
                    
                    totalDePacotesPorPortaPorHora = statement.executeQuery("SELECT COUNT(id) AS totalPacotes FROM "+tabelaEntrada+" WHERE portaDestino != 21 AND portaDestino != 22 AND portaDestino != 25 AND portaDestino != 80 AND portaDestino = 443 AND dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":0:00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59'");
                    while(totalDePacotesPorPortaPorHora.next()){
                        totalPacotesOutros = totalDePacotesPorPortaPorHora.getInt("totalPacotes");
                    }
                    
                    matrizIpoIpd = statement.executeQuery("SELECT ipOrigem, ipDestino FROM "+tabelaEntrada+" WHERE dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":0:00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59' GROUP by ipOrigem, ipDestino ORDER BY id ASC");
                    
                    while ( matrizIpoIpd.next() ) {        
                        resultadoDeCalculo = new ResultadoDeCalculo();

                        resultadoDeCalculo.setIpOrigem(matrizIpoIpd.getString("ipOrigem"));
                        resultadoDeCalculo.setIpDestino(matrizIpoIpd.getString("ipDestino"));

                        String origem = matrizIpoIpd.getString("ipOrigem");
                        String destino = matrizIpoIpd.getString("ipDestino");

                        resultadoDeCalculo.setData(ano+"-"+mes+"-"+cD+" "+cH+":00:00");
                        
                        repetidosFtp = 0;
                        repetidosSsh = 0;
                        repetidosSmtp = 0;
                        repetidosHttp = 0;
                        repetidosHttps = 0;
                        repetidosOutros = 0;
                        
                        pacotesSemelhantesPorPorta = stmt.executeQuery("SELECT portaDestino, COUNT(*) AS pacotesRepetidos FROM "+tabelaEntrada+" WHERE ipOrigem = '"+origem+"' AND ipDestino = '"+destino+"' AND dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":"+minutosIniciais+":00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59' GROUP by ipOrigem, ipDestino, portaDestino, sequencia, ack, janela, tamanho ORDER BY `id` ASC;");
                        while ( pacotesSemelhantesPorPorta.next() ) {
                            switch(pacotesSemelhantesPorPorta.getInt("portaDestino")){
                                case 21:
                                    repetidosFtp += pacotesSemelhantesPorPorta.getInt("pacotesRepetidos");
                                    repetidosFtp--;
                                    break;
                                case 22:
                                    repetidosSsh += pacotesSemelhantesPorPorta.getInt("pacotesRepetidos");
                                    repetidosSsh--;
                                    break;
                                case 25:
                                    repetidosSmtp += pacotesSemelhantesPorPorta.getInt("pacotesRepetidos");
                                    repetidosSmtp--;
                                    break;
                                case 80:
                                    repetidosHttp += pacotesSemelhantesPorPorta.getInt("pacotesRepetidos");
                                    repetidosHttp--;
                                    break;
                                case 443:
                                    repetidosHttps += pacotesSemelhantesPorPorta.getInt("pacotesRepetidos");
                                    repetidosHttps--;
                                    break;
                                default:
                                    repetidosOutros += pacotesSemelhantesPorPorta.getInt("pacotesRepetidos");
                                    repetidosOutros--;
                                    break;
                            }
                        }

                        if(totalPacotesFtp != 0){
                            resultadoDeCalculo.setFtp((double)(repetidosFtp*100)/totalPacotesFtp);
                        }else{
                            resultadoDeCalculo.setFtp(0.0);
                        }
                        if(totalPacotesSsh != 0){
                            resultadoDeCalculo.setSsh((double)(repetidosSsh*100)/totalPacotesSsh);
                        }else{
                            resultadoDeCalculo.setSsh(0.0);
                        }
                        if(totalPacotesSmtp != 0){
                            resultadoDeCalculo.setSmtp((double)(repetidosSmtp*100)/totalPacotesSmtp);
                        }else{
                            resultadoDeCalculo.setSmtp(0.0);
                        }
                        if(totalPacotesHttp != 0){
                            resultadoDeCalculo.setHttp((double)(repetidosHttp*100)/totalPacotesHttp);
                        }else{
                            resultadoDeCalculo.setHttp(0.0);
                        }
                        if(totalPacotesHttps != 0){
                            resultadoDeCalculo.setHttps((double)(repetidosHttps*100)/totalPacotesHttps);
                        }else{
                            resultadoDeCalculo.setHttps(0.0);
                        }
                        if(totalPacotesOutros != 0){
                            resultadoDeCalculo.setOutros((double)(repetidosOutros*100)/totalPacotesOutros);
                        }else{
                            resultadoDeCalculo.setOutros(0.0);
                        }
                        
                        try{
                            engrenagemDAO.inserir(conn, resultadoDeCalculo, tabelaSaida);
                        }catch(SQLException e){
                            System.out.println(e);
                        }
                        
                    } //FIM DE 60 MINUTOS PERCORRIDOS
                } // FIM DO LAÇO DAS HORAS
            } // FIM DO LAÇO DOS DIAS
           
            /////////////////////////////////////////////
            
        } catch (SQLException e) {
            System.err.println("Temos uma exceção!");
            System.err.println(e.getMessage()); 
            System.err.println("Temos uma exceção!");
            System.err.println(e.getMessage());
        }
    } 
}
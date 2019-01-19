package controller;

import java.sql.*;
import model.ResultadoDeCalculo;

public class EngrenagemVazao extends Engrenagem {
    
    private ResultSet matrizTamanhoPorProtocolo;    
    
    public void calcularVazao() throws SQLException{
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
            matrizTamanhoPorProtocolo = null;
            matrizIpoIpd = null;
            
            int minutosIniciais = 00;
            
            for(int cD = diaInicial; cD <= diaFinal; cD++){ //LAÇO DOS DIAS
                for(int cH = 0; cH < 24; cH++){ // LAÇO DAS HORAS
                    matrizIpoIpd = statement.executeQuery("SELECT ipOrigem, ipDestino FROM "+tabelaEntrada+" WHERE dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":0:00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59' GROUP by ipOrigem, ipDestino ORDER BY id ASC");

                    while ( matrizIpoIpd.next() ) {
                            resultadoDeCalculo = new ResultadoDeCalculo();

                            resultadoDeCalculo.setIpOrigem(matrizIpoIpd.getString("ipOrigem"));
                            resultadoDeCalculo.setIpDestino(matrizIpoIpd.getString("ipDestino"));

                            String origem = matrizIpoIpd.getString("ipOrigem");
                            String destino = matrizIpoIpd.getString("ipDestino");

                            resultadoDeCalculo.setData(ano+"-"+mes+"-"+cD+" "+cH+":00:00");
                            
                            matrizTamanhoPorProtocolo = stmt.executeQuery("SELECT portaDestino, SUM(tamanho) AS tamanho FROM "+tabelaEntrada+" WHERE ipOrigem = '"+origem+"' AND ipDestino = '"+destino+"' AND dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":"+minutosIniciais+":00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59' GROUP BY portaDestino ORDER BY id;");
                            
                            while ( matrizTamanhoPorProtocolo.next() ) {
                                switch(matrizTamanhoPorProtocolo.getInt("portaDestino")){
                                    case 21:
                                        double tamanhoFtp = matrizTamanhoPorProtocolo.getDouble("tamanho");
                                        resultadoDeCalculo.setFtp((tamanhoFtp*8)/3600);
                                        break;
                                    case 22:
                                        double tamanhoSsh = matrizTamanhoPorProtocolo.getDouble("tamanho");
                                        resultadoDeCalculo.setSsh((tamanhoSsh*8)/3600);
                                        break;
                                    case 25:
                                        double tamanhoSmtp = matrizTamanhoPorProtocolo.getDouble("tamanho");
                                        resultadoDeCalculo.setSmtp((tamanhoSmtp*8)/3600);
                                        break;
                                    case 80:
                                        double tamanhoHttp = matrizTamanhoPorProtocolo.getDouble("tamanho");
                                        resultadoDeCalculo.setHttp((tamanhoHttp*8)/3600);
                                        break;
                                    case 443:
                                        double tamanhoHttps = matrizTamanhoPorProtocolo.getDouble("tamanho");
                                        resultadoDeCalculo.setHttps((tamanhoHttps*8)/3600);
                                        break;
                                }
                            }
                            matrizTamanhoPorProtocolo = stmt.executeQuery("SELECT SUM(tamanho) AS tamanho FROM "+tabelaEntrada+" WHERE ipOrigem = '"+origem+"' AND ipDestino = '"+destino+"' AND portaDestino!=21 AND portaDestino!=22 AND portaDestino!=25 AND portaDestino!=80 AND portaDestino!=443 AND dateTime BETWEEN '"+ano+"-"+mes+"-"+cD+" "+cH+":"+minutosIniciais+":00' AND '"+ano+"-"+mes+"-"+cD+" "+cH+":59:59';");
                            while ( matrizTamanhoPorProtocolo.next() ) {
                                double tamanhoOutros = matrizTamanhoPorProtocolo.getDouble("tamanho");
                                resultadoDeCalculo.setOutros((tamanhoOutros*8)/3600);
                            }
                                                       
                            try{
                            engrenagemDAO.inserir(conn, resultadoDeCalculo, tabelaSaida);
                            }catch(SQLException e){
                                System.out.println(e);
                            }
                    } //FIM DE 60 MINUTOS PERCORRIDOS
                } // FIM DO LAÇO DAS HORAS
            } // FIM DO LAÇO DOS DIAS
                
        } /*catch (SQLException e) {
            System.err.println("Temos uma exceção!");
            System.err.println(e.getMessage());
        }*/
    }
}
package GUI;

import java.awt.Color;
import javax.swing.JOptionPane;
import controller.Interceptador;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import model.DescricaoDaInterceptacao;
import controller.EngrenagemPerda;
import controller.EngrenagemVazao;
import dao.DescricaoDaInterceptacaoDAO;

public class JFrameTeos extends javax.swing.JFrame {
    // VARIÁVEIS IMPORTANTES
        // Interceptador
    private int numInterfaces;
    
    private Interceptador interceptador = new Interceptador();
    
    private DescricaoDaInterceptacao descricaoDaInterceptacao = new DescricaoDaInterceptacao();
    private DescricaoDaInterceptacaoDAO descricaoDaInterceptacaoDAO = new DescricaoDaInterceptacaoDAO();
    
    private Thread updateThread;
    private Thread cronometro;
    
    private Thread updateThreadVazao;
    private Thread cronometroVazao;
    
    private Thread updateThreadPerda;
    private Thread cronometroPerda;
    
    private java.sql.Timestamp inicioDaInterceptacao = null;
    private java.sql.Timestamp fimDaInterceptacao = null;
    private String duracaoDaInterceptacao;
    
    private EngrenagemVazao engrenagemVazao = new EngrenagemVazao();
    private String duracaoCalculoVazao;
    private EngrenagemPerda engrenagemPerda = new EngrenagemPerda();
    
    private String duracaoCalculoPerda;
    
    private final URL urlImagem;
    private final Image imagemTitulo;
    
    private final long FUSOHORARIO = 10800000;
    
    // FIM DAS VARIÁVEIS IMPORTANTES

    public JFrameTeos() throws SQLException {
        // INICIALIZAÇÃO DO JFRAME
        initComponents();
        numInterfaces = buscarInterfaces();
        urlImagem = this.getClass().getResource("/imgs/eye.png");
        imagemTitulo = Toolkit.getDefaultToolkit().getImage(urlImagem);
        this.setIconImage(imagemTitulo);
        interceptador.setStatus(false);
        // JFRAME INICIALIZADO
        
        
        
        // INICIO DAS THREADS
            // Interceptador
        jButtonInterceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(cpUrlInterceptador.getText().equals("") || cpBaseInterceptador.getText().equals("") || cpTabelaInterceptador.getText().equals("") || cpUsuarioInterceptador.getText().equals("") || cpSenhaInterceptador.getPassword().equals("")){
                        JOptionPane.showMessageDialog(null, "Preencha todos os campos!","Faltam informações", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                            if(jComboBoxListaInterfacesInterceptador.getSelectedIndex() != 0){

                                updateThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //-----------------

                                        interceptador.setURL(cpUrlInterceptador.getText());
                                        interceptador.setBASE(cpBaseInterceptador.getText());
                                        interceptador.setTABELA(cpTabelaInterceptador.getText());
                                        interceptador.setUSUARIO(cpUsuarioInterceptador.getText());
                                        interceptador.setSENHA(new String(cpSenhaInterceptador.getPassword()));
                                        interceptador.setInterfaceEscolhidaIndex(jComboBoxListaInterfacesInterceptador.getSelectedIndex()-1);
                                        interceptador.setInterfaceEscolhidaItem(jComboBoxListaInterfacesInterceptador.getSelectedItem().toString());
                                        interceptador.setTotalInterfaces(numInterfaces);

                                        interceptador.setStatus(true);
                                        jButtonInterromper.setEnabled(true);
                                        jButtonInterceptar.setEnabled(false);
                                        jButtonInterceptar.setText("INTERCEPTANDO...");
                                        
                                        jLabelCronometroInterceptadorBg.setVisible(true);
                                        jLabelCronometroInterceptadorTxt.setVisible(true);

                                        try {
                                            interceptador.Interceptar();

                                        } catch (SQLException ex) {
                                            //Logger.getLogger(JFrameHome.class.getName()).log(Level.SEVERE, null, ex);
                                            jButtonInterromper.setEnabled(true);
                                            jButtonInterceptar.setEnabled(false);
                                            jButtonInterceptar.setText("INTERCEPTAR PACOTES");
                                            
                                            jLabelCronometroInterceptadorBg.setVisible(false);
                                            jLabelCronometroInterceptadorTxt.setVisible(false);
                                            
                                            //System.out.println(ex);
                                            JOptionPane.showMessageDialog(null,"<html><body><b style='font-size:12px;'>Não foi possível estabelecer uma conexão com a base de dados.</b></body></html>\n\n"
                                            + "Por favor, verifique se as informações fornecidas estão corretas.","Base de dados inacessível",JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                });
                                updateThread.start();
                                
                                cronometro = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //-----------------

                                        final long HORAINICIAL = System.currentTimeMillis() - FUSOHORARIO;

                                        //String inicioDaInterceptacao = converter(HORAINICIAL);
                                        
                                        inicioDaInterceptacao = new java.sql.Timestamp (System.currentTimeMillis());                                        

                                        while(true){

                                            long totalMs = (System.currentTimeMillis() - FUSOHORARIO) - (HORAINICIAL);
                                            duracaoDaInterceptacao = converter(totalMs);
                                             jLabelCronometroInterceptadorTxt.setText("INTERCEPTAÇÃO ATIVA HÁ "+converter(totalMs));

                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(JFrameTeos.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                                    }
                                    } // FIM DO RUN

                                    public String converter(long unixTime){

                                        long totalMs = unixTime;

                                        long totalS = totalMs / 1000;
                                        long s = (int)(totalS % 60);

                                        long totalM = totalS / 60;
                                        long m = (int) (totalM % 60);

                                        long totalH = totalM / 60;
                                        long h = (int) (totalH % 24);

                                        long totalD = totalH / 24;

                                           return totalD + " dias - " + h + "h:" + m + "m:" + s + "s";
                                    }
                                });
                                cronometro.start();
                                
                                
                            }else{
                                JOptionPane.showMessageDialog(null,"Escolha uma interface antes de iniciar a interceptação.","Você não escolheu uma interface",JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
            }
        });
        jButtonInterromper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                interceptador.setStatus(false);
                jButtonInterromper.setEnabled(false);
                jButtonInterceptar.setEnabled(true);
                jButtonInterceptar.setText("INTERCEPTAR PACOTES");
                
                updateThread.stop();
                fimDaInterceptacao = new java.sql.Timestamp (System.currentTimeMillis());
                cronometro.stop();
                
                jLabelCronometroInterceptadorBg.setVisible(false);
                jLabelCronometroInterceptadorTxt.setVisible(false);
                                            
                try {
                    descricaoDaInterceptacaoDAO.criarNovaTabela(interceptador.getALVO(),interceptador.getURL(),interceptador.getBASE(),interceptador.getUSUARIO(),interceptador.getSENHA());
                } catch (SQLException ex) {
                    Logger.getLogger(JFrameTeos.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                descricaoDaInterceptacao.setInicio(inicioDaInterceptacao);
                descricaoDaInterceptacao.setFim(fimDaInterceptacao);
                descricaoDaInterceptacao.setUrl(interceptador.getURL());
                descricaoDaInterceptacao.setBase(interceptador.getBASE());
                descricaoDaInterceptacao.setTabela(interceptador.getTABELA());
                descricaoDaInterceptacao.setUsuario(interceptador.getUSUARIO());
                descricaoDaInterceptacao.setInterfaceDeRede(interceptador.getInterfaceEscolhidaItem());
                
                try {
                    descricaoDaInterceptacaoDAO.inserir(descricaoDaInterceptacao);
                } catch (SQLException ex) {
                    Logger.getLogger(JFrameTeos.class.getName()).log(Level.SEVERE, null, ex);
                }              
                
                JOptionPane.showMessageDialog(null,"<html><body style='font-size:12px;'>Você interrompeu a interceptação. Agora, nenhum pacote da interface <br><b>"+descricaoDaInterceptacao.getInterfaceDeRede()+"</b><br> está sendo interceptado."
                        + "<br>Os registros de pacotes interceptados já estão disponíveis na tabela <b>"+descricaoDaInterceptacao.getTabela()+"</b> da base <b>"+descricaoDaInterceptacao.getBase()+"</b>."
                                            + "<br><br>Duração total da interceptação: <b>"+duracaoDaInterceptacao+"</b>.</body></html>","Interceptação interrompida!",JOptionPane.INFORMATION_MESSAGE);
                
            }
        });
            // Vazao
            jButtonCalcularVazao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateThreadVazao = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //-----------------
                        if(cpUrlVazao.getText().equals("") || cpBaseEntradaVazao.getText().equals("") 
                                || cpTabelaEntradaVazao.getText().equals("") || cpUsuarioEntradaVazao.getText().equals("") 
                                || cpTabelaSaidaVazao.getText().equals("") || cpAnoVazao.getText().equals("")
                                || cpDiaInicialVazao.getText().equals("") || cpDiaFinalVazao.getText().equals("")){
                            JOptionPane.showMessageDialog(null, "<html><body style='font-size:12px;'>Preencha todos os campos!</body></html>","Faltam informações", JOptionPane.INFORMATION_MESSAGE);
                            cronometroVazao.stop();                           
                        
                        
                        }else{
                                engrenagemVazao.setUrl(cpUrlVazao.getText());
                                
                                engrenagemVazao.setBaseEntrada(cpBaseEntradaVazao.getText());
                                engrenagemVazao.setTabelaEntrada(cpTabelaEntradaVazao.getText());
                                engrenagemVazao.setUsuarioEntrada(cpUsuarioEntradaVazao.getText());
                                engrenagemVazao.setSenhaEntrada(new String(cpSenhaEntradaVazao.getPassword()));
                                
                                engrenagemVazao.setAno(Integer.parseInt(cpAnoVazao.getText()));
                                engrenagemVazao.setMes(Integer.parseInt(cpMesVazao.getText()));
                                engrenagemVazao.setDiaInicial(Integer.parseInt(cpDiaInicialVazao.getText()));
                                engrenagemVazao.setDiaFinal(Integer.parseInt(cpDiaFinalVazao.getText()));
                              
                                if(cpBaseSaidaVazao.getText().equals("") && cpUsuarioSaidaVazao.getText().equals("")){
                                    engrenagemVazao.setBaseSaida(cpBaseEntradaVazao.getText());
                                    engrenagemVazao.setUsuarioSaida(cpUsuarioEntradaVazao.getText());
                                    engrenagemVazao.setSenhaSaida(new String(cpSenhaEntradaVazao.getPassword()));
                                }else{
                                    engrenagemVazao.setBaseSaida(cpBaseSaidaVazao.getText());
                                    engrenagemVazao.setUsuarioSaida(cpUsuarioSaidaVazao.getText());
                                    engrenagemVazao.setSenhaSaida(new String(cpSenhaSaidaVazao.getPassword()));
                                }
                                
                                engrenagemVazao.setTabelaSaida(cpTabelaSaidaVazao.getText());
                                                                
                                jButtonInterromperVazao.setEnabled(true);
                                jButtonCalcularVazao.setEnabled(false);
                                
                                jButtonCalcularVazao.setText("CALCULANDO...");
                                
                                jLabelCronometroVazaoBg.setVisible(true);
                                jLabelCronometroVazaoTxt.setVisible(true);
                                
                                try {
                                    engrenagemVazao.calcularVazao();
                                    cronometroVazao.stop();
                                    jLabelCronometroVazaoBg.setVisible(false);
                                    jLabelCronometroVazaoTxt.setVisible(false);
                                    
                                    JOptionPane.showMessageDialog(null,"<html><body style='font-size:12px;'>A vazão foi calculada e já está disponível na tabela <b>"+engrenagemVazao.getTabelaSaida()+"</b> da base <b>"+engrenagemVazao.getBaseSaida()+"</b>."
                                            + "<br>Duração total do cálculo: <b>"+duracaoCalculoVazao+"</b>.</body></html>","Cálculo de vazão finalizado!",JOptionPane.INFORMATION_MESSAGE);
                                    
                                    jButtonInterromperVazao.setEnabled(false);
                                    jButtonCalcularVazao.setEnabled(true);
                                    
                                    jButtonCalcularVazao.setText("CALCULAR VAZÃO");
                                    
                                } catch (SQLException ex) {
                                    Logger.getLogger(JFrameTeos.class.getName()).log(Level.SEVERE, null, ex);
                                    cronometroVazao.stop();
                                    jLabelCronometroVazaoBg.setVisible(false);
                                    jLabelCronometroVazaoTxt.setVisible(false);
                                    
                                    jButtonInterromperVazao.setEnabled(false);
                                    jButtonCalcularVazao.setEnabled(true);
                                    
                                    jButtonCalcularVazao.setText("CALCULAR VAZÃO");
                                    JOptionPane.showMessageDialog(null,"<html><body style='font-size:12px;'><b>Não foi possível estabelecer uma conexão com a base de dados.</b></body></html>\n\n"
                    + "Por favor, verifique se as informações fornecidas estão corretas.","Base de dados inacessível",JOptionPane.ERROR_MESSAGE);
                                    
                                    
                                }
                        }
                    }
                });
                updateThreadVazao.start();
                
                cronometroVazao = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //-----------------

                                        final long HORAINICIAL = System.currentTimeMillis() - FUSOHORARIO;

                                        //String inicioDaInterceptacao = converter(HORAINICIAL);            

                                        while(true){

                                            long totalMs = (System.currentTimeMillis() - FUSOHORARIO) - (HORAINICIAL);
                                            duracaoCalculoVazao = converter(totalMs);
                                            jLabelCronometroVazaoTxt.setText("CÁLCULO ATIVO HÁ: "+converter(totalMs));

                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(JFrameTeos.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                                    }
                                    } // FIM DO RUN

                                    public String converter(long unixTime){

                                        long totalMs = unixTime;

                                        long totalS = totalMs / 1000;
                                        long s = (int)(totalS % 60);

                                        long totalM = totalS / 60;
                                        long m = (int) (totalM % 60);

                                        long totalH = totalM / 60;
                                        long h = (int) (totalH % 24);

                                        long totalD = totalH / 24;

                                           return totalD + " dias - " + h + "h:" + m + "m:" + s + "s";
                                    }
                                });
                                cronometroVazao.start();
            }
        });
        jButtonInterromperVazao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateThreadVazao.stop();
                cronometroVazao.stop();
                jButtonInterromperVazao.setEnabled(false);
                jButtonCalcularVazao.setEnabled(true);
                jButtonCalcularVazao.setText("CALCULAR VAZÃO");
                jLabelCronometroVazaoBg.setVisible(false);
                jLabelCronometroVazaoTxt.setVisible(false);
                
                JOptionPane.showMessageDialog(null,"<html><body style='font-size:12px;'>O cálculo da vazão foi interrompido, mas a vazão parcial calculada<br>está disponível na tabela <b>"+engrenagemVazao.getTabelaSaida()+"</b> da base <b>"+engrenagemVazao.getBaseSaida()+"</b>."
                                            + "<br><br>Duração total do cálculo: <b>"+duracaoCalculoVazao+"</b>.</body></html>","Cálculo de vazão interrompido!",JOptionPane.INFORMATION_MESSAGE);

            }
        });
            
            // Perda
        jButtonCalcularPerda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateThreadPerda = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //-----------------
                        if(cpUrlPerda.getText().equals("") || cpBaseEntradaPerda.getText().equals("") 
                                || cpTabelaEntradaPerda.getText().equals("") || cpUsuarioEntradaPerda.getText().equals("") 
                                || cpTabelaSaidaPerda.getText().equals("") || cpAnoPerda.getText().equals("")
                                || cpDiaInicialPerda.getText().equals("") || cpDiaFinalPerda.getText().equals("")){
                            JOptionPane.showMessageDialog(null, "<html><body style='font-size:12px;'>Preencha todos os campos!</body></html>","Faltam informações", JOptionPane.INFORMATION_MESSAGE);
                            cronometroPerda.stop();
                            
                        
                        
                        }else{
                                engrenagemPerda.setUrl(cpUrlPerda.getText());
                                
                                engrenagemPerda.setBaseEntrada(cpBaseEntradaPerda.getText());
                                engrenagemPerda.setTabelaEntrada(cpTabelaEntradaPerda.getText());
                                engrenagemPerda.setUsuarioEntrada(cpUsuarioEntradaPerda.getText());
                                engrenagemPerda.setSenhaEntrada(new String(cpSenhaEntradaPerda.getPassword()));
                                
                                engrenagemPerda.setAno(Integer.parseInt(cpAnoPerda.getText()));
                                engrenagemPerda.setMes(Integer.parseInt(cpMesPerda.getText()));
                                engrenagemPerda.setDiaInicial(Integer.parseInt(cpDiaInicialPerda.getText()));
                                engrenagemPerda.setDiaFinal(Integer.parseInt(cpDiaFinalPerda.getText()));
                              
                                if(cpBaseSaidaPerda.getText().equals("") && cpUsuarioSaidaPerda.getText().equals("")){
                                    engrenagemPerda.setBaseSaida(cpBaseEntradaPerda.getText());
                                    engrenagemPerda.setUsuarioSaida(cpUsuarioEntradaPerda.getText());
                                    engrenagemPerda.setSenhaSaida(new String(cpSenhaEntradaPerda.getPassword()));
                                }else{
                                    engrenagemPerda.setBaseSaida(cpBaseSaidaPerda.getText());
                                    engrenagemPerda.setUsuarioSaida(cpUsuarioSaidaPerda.getText());
                                    engrenagemPerda.setSenhaSaida(new String(cpSenhaSaidaPerda.getPassword()));
                                }
                                
                                engrenagemPerda.setTabelaSaida(cpTabelaSaidaPerda.getText());
                                                                
                                                               
                                jButtonCalcularPerda.setText("CALCULANDO...");
                                
                                jButtonInterromperPerda.setEnabled(true);
                                jButtonCalcularPerda.setEnabled(false);
                                
                                jLabelCronometroPerdaBg.setVisible(true);
                                jLabelCronometroPerdaTxt.setVisible(true);
                                jButtonCalcularPerda.setEnabled(false);
                                
                                try {
                                    engrenagemPerda.calcularPerda();
                                    cronometroPerda.stop();
                                    jLabelCronometroPerdaBg.setVisible(false);
                                    jLabelCronometroPerdaTxt.setVisible(false);
                                    
                                    JOptionPane.showMessageDialog(null,"<html><body style='font-size:12px;'>A perda foi calculada e já está disponível na tabela <b>"+engrenagemPerda.getTabelaSaida()+"</b> da base <b>"+engrenagemPerda.getBaseSaida()+"</b>."
                                            + "<br>Duração total do cálculo: <b>"+duracaoCalculoPerda+"</b>.</body></html>","Cálculo de perda finalizado!",JOptionPane.INFORMATION_MESSAGE);
                                    
                                    jButtonInterromperPerda.setEnabled(false);
                                    jButtonCalcularPerda.setEnabled(true);
                                    
                                    jButtonCalcularPerda.setText("CALCULAR PERDA");
                                    
                                } catch (SQLException ex) {
                                    //Logger.getLogger(JFrameHome.class.getName()).log(Level.SEVERE, null, ex);
                                    cronometroPerda.stop();
                                    jLabelCronometroPerdaBg.setVisible(false);
                                    jLabelCronometroPerdaTxt.setVisible(false);                                    
                                    jButtonInterromperPerda.setEnabled(false);
                                    jButtonCalcularPerda.setEnabled(true);
                                    
                                    jButtonCalcularPerda.setText("CALCULAR PERDA");
                                    JOptionPane.showMessageDialog(null,"<html><body style='font-size:12px;'><b>Não foi possível estabelecer uma conexão com a base de dados.</b></body></html>\n\n"
                    + "Por favor, verifique se as informações fornecidas estão corretas.","Base de dados inacessível",JOptionPane.ERROR_MESSAGE);
                                    
                                    
                                }
                        }
                    }
                });
                updateThreadPerda.start();
                
                cronometroPerda = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //-----------------

                                        final long HORAINICIAL = System.currentTimeMillis() - FUSOHORARIO;                          

                                        while(true){

                                            long totalMs = (System.currentTimeMillis() - FUSOHORARIO) - (HORAINICIAL);
                                            duracaoCalculoPerda = converter(totalMs);
                                            jLabelCronometroPerdaTxt.setText("CÁLCULO ATIVO HÁ: "+converter(totalMs));

                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(JFrameTeos.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                                    }
                                    } // FIM DO RUN

                                    public String converter(long unixTime){

                                        long totalMs = unixTime;

                                        long totalS = totalMs / 1000;
                                        long s = (int)(totalS % 60);

                                        long totalM = totalS / 60;
                                        long m = (int) (totalM % 60);

                                        long totalH = totalM / 60;
                                        long h = (int) (totalH % 24);

                                        long totalD = totalH / 24;

                                           return totalD + " dias - " + h + "h:" + m + "m:" + s + "s";
                                    }
                                });
                                cronometroPerda.start();
            }
        });
        jButtonInterromperPerda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateThreadPerda.stop();
                cronometroPerda.stop();
                jLabelCronometroPerdaBg.setVisible(false);
                jLabelCronometroPerdaTxt.setVisible(false);
                jButtonInterromperPerda.setEnabled(false);
                jButtonCalcularPerda.setEnabled(true);
                jButtonCalcularPerda.setText("CALCULAR PERDA");
                
                
                JOptionPane.showMessageDialog(null,"<html><body style='font-size:12px;'>O cálculo de perda foi interrompido, mas a perda parcial calculada<br>está disponível na tabela <b>"+engrenagemPerda.getTabelaSaida()+"</b> da base <b>"+engrenagemPerda.getBaseSaida()+"</b>."
                                            + "<br><br>Duração total do cálculo: <b>"+duracaoCalculoPerda+"</b>.</body></html>","Cálculo de perda interrompido!",JOptionPane.INFORMATION_MESSAGE);

            }
        });
        // FIM DAS THREADS
        
    } // FIM DO MÉTODO CONSTRUTOR
    
    
    
     @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMenuPrincipal = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabelInterceptadorBtn = new javax.swing.JLabel();
        jLabelPerdaBtn = new javax.swing.JLabel();
        jLabelVazaoBtn = new javax.swing.JLabel();
        jPanelInterceptador = new javax.swing.JPanel();
        jButtonInterceptar = new javax.swing.JButton();
        jButtonInterromper = new javax.swing.JButton();
        jLabelCronometroInterceptadorTxt = new javax.swing.JLabel();
        jLabelCronometroInterceptadorTxt.setVisible(false);
        jLabelCronometroInterceptadorBg = new javax.swing.JLabel();
        jLabelCronometroInterceptadorBg.setVisible(false);
        cpBaseInterceptador = new javax.swing.JTextField();
        jLabelBaseInterceptador = new javax.swing.JLabel();
        cpTabelaInterceptador = new javax.swing.JTextField();
        jLabelTabelaInterceptador = new javax.swing.JLabel();
        cpUsuarioInterceptador = new javax.swing.JTextField();
        jLabelUsuarioInterceptador = new javax.swing.JLabel();
        jLabelSenhaInterceptador = new javax.swing.JLabel();
        cpSenhaInterceptador = new javax.swing.JPasswordField();
        jComboBoxListaInterfacesInterceptador = new javax.swing.JComboBox<>();
        jLabelInterfaceInterceptador = new javax.swing.JLabel();
        cpUrlInterceptador = new javax.swing.JTextField();
        jLabelUrlInterceptador = new javax.swing.JLabel();
        jPanelVazao = new javax.swing.JPanel();
        jButtonCalcularVazao = new javax.swing.JButton();
        jButtonInterromperVazao = new javax.swing.JButton();
        jLabelCronometroVazaoTxt = new javax.swing.JLabel();
        jLabelCronometroVazaoTxt.setVisible(false);
        jLabelCronometroVazaoBg = new javax.swing.JLabel();
        jLabelCronometroVazaoBg.setVisible(false);
        cpBaseEntradaVazao = new javax.swing.JTextField();
        jLabelBaseEntradaVazao = new javax.swing.JLabel();
        cpTabelaEntradaVazao = new javax.swing.JTextField();
        jLabelTabelaEntradaVazao = new javax.swing.JLabel();
        cpUsuarioEntradaVazao = new javax.swing.JTextField();
        jLabelUsuarioEntradaVazao = new javax.swing.JLabel();
        jLabelSenhaEntradaVazao = new javax.swing.JLabel();
        cpSenhaEntradaVazao = new javax.swing.JPasswordField();
        cpUrlVazao = new javax.swing.JTextField();
        jLabelUrlVazao = new javax.swing.JLabel();
        jLabelEntradaVazao = new javax.swing.JLabel();
        cpUsuarioSaidaVazao = new javax.swing.JTextField();
        jLabelUsuarioSaidaVazao = new javax.swing.JLabel();
        jLabelSenhaSaidaVazao = new javax.swing.JLabel();
        cpSenhaSaidaVazao = new javax.swing.JPasswordField();
        cpBaseSaidaVazao = new javax.swing.JTextField();
        jLabelBaseSaidaVazao = new javax.swing.JLabel();
        cpTabelaSaidaVazao = new javax.swing.JTextField();
        jLabelTabelaSaidaVazao = new javax.swing.JLabel();
        jLabelSaidaVazao = new javax.swing.JLabel();
        jSeparatorVazao = new javax.swing.JSeparator();
        cpAnoVazao = new javax.swing.JTextField();
        jLabelAnoVazao = new javax.swing.JLabel();
        cpMesVazao = new javax.swing.JTextField();
        jLabelMesVazao = new javax.swing.JLabel();
        cpDiaFinalVazao = new javax.swing.JTextField();
        cpDiaInicialVazao = new javax.swing.JTextField();
        jLabelDiaInicialVazao = new javax.swing.JLabel();
        jLabelDiaFinalVazao = new javax.swing.JLabel();
        jPanelPerda = new javax.swing.JPanel();
        jButtonCalcularPerda = new javax.swing.JButton();
        jButtonInterromperPerda = new javax.swing.JButton();
        jLabelCronometroPerdaTxt = new javax.swing.JLabel();
        jLabelCronometroPerdaTxt.setVisible(false);
        jLabelCronometroPerdaBg = new javax.swing.JLabel();
        jLabelCronometroPerdaBg.setVisible(false);
        cpBaseEntradaPerda = new javax.swing.JTextField();
        jLabelBaseEntradaPerda = new javax.swing.JLabel();
        cpTabelaEntradaPerda = new javax.swing.JTextField();
        jLabelBaseEntradaTabela = new javax.swing.JLabel();
        cpUsuarioEntradaPerda = new javax.swing.JTextField();
        jLabelUsuarioEntradaPerda = new javax.swing.JLabel();
        jLabelSenhaEntradaPerda = new javax.swing.JLabel();
        cpSenhaEntradaPerda = new javax.swing.JPasswordField();
        cpUrlPerda = new javax.swing.JTextField();
        jLabelUrlPerda = new javax.swing.JLabel();
        jLabelEntradaPerda = new javax.swing.JLabel();
        cpUsuarioSaidaPerda = new javax.swing.JTextField();
        jLabelUsuarioSaidaPerda = new javax.swing.JLabel();
        jLabelSenhaSaidaPerda = new javax.swing.JLabel();
        cpSenhaSaidaPerda = new javax.swing.JPasswordField();
        cpBaseSaidaPerda = new javax.swing.JTextField();
        jLabelBaseSaidaPerda = new javax.swing.JLabel();
        cpTabelaSaidaPerda = new javax.swing.JTextField();
        jLabelTabelaSaidaPerda = new javax.swing.JLabel();
        jLabelSaidaPerda = new javax.swing.JLabel();
        jSeparatorPerda = new javax.swing.JSeparator();
        cpAnoPerda = new javax.swing.JTextField();
        jLabelAnoPerda = new javax.swing.JLabel();
        cpMesPerda = new javax.swing.JTextField();
        jLabelMesPerda = new javax.swing.JLabel();
        cpDiaFinalPerda = new javax.swing.JTextField();
        cpDiaInicialPerda = new javax.swing.JTextField();
        jLabelDiaInicialPerda = new javax.swing.JLabel();
        jLabelDiaFinalPerda = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TEOS - The Eye Of Sauron");
        setMinimumSize(new java.awt.Dimension(1024, 600));
        setResizable(false);
        getContentPane().setLayout(null);

        jPanelMenuPrincipal.setBackground(new java.awt.Color(117, 117, 117));
        jPanelMenuPrincipal.setMinimumSize(new java.awt.Dimension(370, 600));
        jPanelMenuPrincipal.setPreferredSize(new java.awt.Dimension(370, 600));
        jPanelMenuPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/logo.PNG"))); // NOI18N
        jPanelMenuPrincipal.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 50, -1, -1));

        jLabelInterceptadorBtn.setBackground(new java.awt.Color(66, 66, 66));
        jLabelInterceptadorBtn.setFont(new java.awt.Font("TakaoPGothic", 1, 14)); // NOI18N
        jLabelInterceptadorBtn.setForeground(new java.awt.Color(255, 255, 255));
        jLabelInterceptadorBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelInterceptadorBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/IconeVermelho.PNG"))); // NOI18N
        jLabelInterceptadorBtn.setText("INTERCEPTADOR");
        jLabelInterceptadorBtn.setToolTipText("Nada a declarar");
        jLabelInterceptadorBtn.setIconTextGap(10);
        jLabelInterceptadorBtn.setOpaque(true);
        jLabelInterceptadorBtn.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabelInterceptadorBtnMouseMoved(evt);
            }
        });
        jLabelInterceptadorBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabelInterceptadorBtnMousePressed(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelInterceptadorBtnMouseExited(evt);
            }
        });
        jPanelMenuPrincipal.add(jLabelInterceptadorBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 350, 60));

        jLabelPerdaBtn.setBackground(new java.awt.Color(255, 174, 0));
        jLabelPerdaBtn.setFont(new java.awt.Font("TakaoPGothic", 1, 14)); // NOI18N
        jLabelPerdaBtn.setForeground(new java.awt.Color(51, 51, 51));
        jLabelPerdaBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPerdaBtn.setText("CALCULADOR DE PERDA");
        jLabelPerdaBtn.setToolTipText("");
        jLabelPerdaBtn.setIconTextGap(10);
        jLabelPerdaBtn.setOpaque(true);
        jLabelPerdaBtn.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabelPerdaBtnMouseMoved(evt);
            }
        });
        jLabelPerdaBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabelPerdaBtnMousePressed(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelPerdaBtnMouseExited(evt);
            }
        });
        jPanelMenuPrincipal.add(jLabelPerdaBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 480, 350, 60));

        jLabelVazaoBtn.setBackground(new java.awt.Color(255, 90, 0));
        jLabelVazaoBtn.setFont(new java.awt.Font("TakaoPGothic", 1, 14)); // NOI18N
        jLabelVazaoBtn.setForeground(new java.awt.Color(255, 255, 255));
        jLabelVazaoBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelVazaoBtn.setText("CALCULADOR DE VAZÃO");
        jLabelVazaoBtn.setToolTipText("");
        jLabelVazaoBtn.setOpaque(true);
        jLabelVazaoBtn.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabelVazaoBtnMouseMoved(evt);
            }
        });
        jLabelVazaoBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabelVazaoBtnMousePressed(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelVazaoBtnMouseExited(evt);
            }
        });
        jPanelMenuPrincipal.add(jLabelVazaoBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, 350, 60));

        getContentPane().add(jPanelMenuPrincipal);
        jPanelMenuPrincipal.setBounds(0, 0, 370, 600);

        jPanelInterceptador.setBackground(new java.awt.Color(97, 97, 97));
        jPanelInterceptador.setMinimumSize(new java.awt.Dimension(550, 600));
        jPanelInterceptador.setPreferredSize(new java.awt.Dimension(550, 600));
        jPanelInterceptador.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonInterceptar.setBackground(new java.awt.Color(181, 41, 9));
        jButtonInterceptar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonInterceptar.setForeground(new java.awt.Color(255, 255, 255));
        jButtonInterceptar.setText("INTERCEPTAR PACOTES");
        jButtonInterceptar.setBorder(null);
        jButtonInterceptar.setBorderPainted(false);
        jButtonInterceptar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonInterceptar.setRolloverEnabled(true);
        jButtonInterceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInterceptarActionPerformed(evt);
            }
        });
        jPanelInterceptador.add(jButtonInterceptar, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 463, 332, 54));

        jButtonInterromper.setBackground(new java.awt.Color(19, 19, 19));
        jButtonInterromper.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonInterromper.setForeground(new java.awt.Color(255, 255, 255));
        jButtonInterromper.setText("INTERROMPER");
        jButtonInterromper.setBorder(null);
        jButtonInterromper.setBorderPainted(false);
        jButtonInterromper.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonInterromper.setEnabled(false);
        jButtonInterromper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInterromperActionPerformed(evt);
            }
        });
        jPanelInterceptador.add(jButtonInterromper, new org.netbeans.lib.awtextra.AbsoluteConstraints(411, 463, 198, 54));

        jLabelCronometroInterceptadorTxt.setBackground(new java.awt.Color(181, 41, 9));
        jLabelCronometroInterceptadorTxt.setFont(new java.awt.Font("TakaoPGothic", 1, 16)); // NOI18N
        jLabelCronometroInterceptadorTxt.setForeground(new java.awt.Color(255, 255, 255));
        jLabelCronometroInterceptadorTxt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCronometroInterceptadorTxt.setText("Cálculo parado");
        jLabelCronometroInterceptadorTxt.setOpaque(true);
        jPanelInterceptador.add(jLabelCronometroInterceptadorTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 230, 530, 96));

        jLabelCronometroInterceptadorBg.setBackground(new Color(33, 33, 33, 210));
        jLabelCronometroInterceptadorBg.setOpaque(true);
        jPanelInterceptador.add(jLabelCronometroInterceptadorBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 600));

        cpBaseInterceptador.setBackground(new java.awt.Color(255, 255, 255));
        cpBaseInterceptador.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpBaseInterceptador.setForeground(new java.awt.Color(80, 80, 80));
        cpBaseInterceptador.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpBaseInterceptador.setToolTipText("");
        cpBaseInterceptador.setBorder(null);
        cpBaseInterceptador.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        cpBaseInterceptador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpBaseInterceptadorActionPerformed(evt);
            }
        });
        jPanelInterceptador.add(cpBaseInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 187, 255, 45));

        jLabelBaseInterceptador.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelBaseInterceptador.setForeground(new java.awt.Color(204, 204, 204));
        jLabelBaseInterceptador.setText("Nome da base de dados");
        jPanelInterceptador.add(jLabelBaseInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 166, -1, -1));

        cpTabelaInterceptador.setBackground(new java.awt.Color(255, 255, 255));
        cpTabelaInterceptador.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpTabelaInterceptador.setForeground(new java.awt.Color(80, 80, 80));
        cpTabelaInterceptador.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpTabelaInterceptador.setToolTipText("Nadica");
        cpTabelaInterceptador.setBorder(null);
        jPanelInterceptador.add(cpTabelaInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(354, 187, 255, 45));

        jLabelTabelaInterceptador.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelTabelaInterceptador.setForeground(new java.awt.Color(204, 204, 204));
        jLabelTabelaInterceptador.setText("Nome da tabela");
        jPanelInterceptador.add(jLabelTabelaInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(354, 166, -1, -1));

        cpUsuarioInterceptador.setBackground(new java.awt.Color(255, 255, 255));
        cpUsuarioInterceptador.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpUsuarioInterceptador.setForeground(new java.awt.Color(80, 80, 80));
        cpUsuarioInterceptador.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpUsuarioInterceptador.setBorder(null);
        jPanelInterceptador.add(cpUsuarioInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 280, 255, 45));

        jLabelUsuarioInterceptador.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelUsuarioInterceptador.setForeground(new java.awt.Color(204, 204, 204));
        jLabelUsuarioInterceptador.setText("Usuário da base de dados");
        jPanelInterceptador.add(jLabelUsuarioInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 259, -1, -1));

        jLabelSenhaInterceptador.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelSenhaInterceptador.setForeground(new java.awt.Color(204, 204, 204));
        jLabelSenhaInterceptador.setText("Senha");
        jPanelInterceptador.add(jLabelSenhaInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(354, 259, -1, -1));

        cpSenhaInterceptador.setBackground(new java.awt.Color(255, 255, 255));
        cpSenhaInterceptador.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpSenhaInterceptador.setForeground(new java.awt.Color(80, 80, 80));
        cpSenhaInterceptador.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpSenhaInterceptador.setBorder(null);
        cpSenhaInterceptador.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jPanelInterceptador.add(cpSenhaInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(354, 280, 255, 45));

        jComboBoxListaInterfacesInterceptador.setBackground(new java.awt.Color(255, 255, 255));
        jComboBoxListaInterfacesInterceptador.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jComboBoxListaInterfacesInterceptador.setMaximumRowCount(12);
        jComboBoxListaInterfacesInterceptador.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Escolha a interface que deseja interceptar" }));
        jComboBoxListaInterfacesInterceptador.setBorder(null);
        jComboBoxListaInterfacesInterceptador.setMaximumSize(new java.awt.Dimension(600, 32767));
        jComboBoxListaInterfacesInterceptador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxListaInterfacesInterceptadorActionPerformed(evt);
            }
        });
        jPanelInterceptador.add(jComboBoxListaInterfacesInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 372, 563, 53));

        jLabelInterfaceInterceptador.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelInterfaceInterceptador.setForeground(new java.awt.Color(204, 204, 204));
        jLabelInterfaceInterceptador.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelInterfaceInterceptador.setText("Escolha a interface");
        jPanelInterceptador.add(jLabelInterfaceInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 351, -1, -1));

        cpUrlInterceptador.setBackground(new java.awt.Color(255, 255, 255));
        cpUrlInterceptador.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpUrlInterceptador.setForeground(new java.awt.Color(80, 80, 80));
        cpUrlInterceptador.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpUrlInterceptador.setText("localhost");
        cpUrlInterceptador.setToolTipText("");
        cpUrlInterceptador.setBorder(null);
        cpUrlInterceptador.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        cpUrlInterceptador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpUrlInterceptadorActionPerformed(evt);
            }
        });
        jPanelInterceptador.add(cpUrlInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 103, 563, 45));

        jLabelUrlInterceptador.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelUrlInterceptador.setForeground(new java.awt.Color(204, 204, 204));
        jLabelUrlInterceptador.setText("URL");
        jPanelInterceptador.add(jLabelUrlInterceptador, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 82, -1, -1));

        getContentPane().add(jPanelInterceptador);
        jPanelInterceptador.setBounds(370, 0, 660, 600);

        jPanelVazao.setVisible(false);
        jPanelVazao.setBackground(new java.awt.Color(97, 97, 97));
        jPanelVazao.setMinimumSize(new java.awt.Dimension(550, 600));
        jPanelVazao.setPreferredSize(new java.awt.Dimension(550, 600));
        jPanelVazao.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonCalcularVazao.setBackground(new java.awt.Color(255, 90, 0));
        jButtonCalcularVazao.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonCalcularVazao.setForeground(new java.awt.Color(255, 255, 255));
        jButtonCalcularVazao.setText("CALCULAR VAZÃO");
        jButtonCalcularVazao.setBorder(null);
        jButtonCalcularVazao.setBorderPainted(false);
        jButtonCalcularVazao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonCalcularVazao.setRolloverEnabled(true);
        jButtonCalcularVazao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCalcularVazaoActionPerformed(evt);
            }
        });
        jPanelVazao.add(jButtonCalcularVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 524, 332, 54));

        jButtonInterromperVazao.setBackground(new java.awt.Color(19, 19, 19));
        jButtonInterromperVazao.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonInterromperVazao.setForeground(new java.awt.Color(255, 255, 255));
        jButtonInterromperVazao.setText("INTERROMPER");
        jButtonInterromperVazao.setBorder(null);
        jButtonInterromperVazao.setBorderPainted(false);
        jButtonInterromperVazao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonInterromperVazao.setEnabled(false);
        jButtonInterromperVazao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInterromperVazaoActionPerformed(evt);
            }
        });
        jPanelVazao.add(jButtonInterromperVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(413, 524, 198, 54));

        jLabelCronometroVazaoTxt.setBackground(new java.awt.Color(239, 108, 0));
        jLabelCronometroVazaoTxt.setFont(new java.awt.Font("TakaoPGothic", 1, 16)); // NOI18N
        jLabelCronometroVazaoTxt.setForeground(new java.awt.Color(255, 255, 255));
        jLabelCronometroVazaoTxt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCronometroVazaoTxt.setText("Cálculo parado");
        jLabelCronometroVazaoTxt.setOpaque(true);
        jPanelVazao.add(jLabelCronometroVazaoTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 230, 530, 96));

        jLabelCronometroVazaoBg.setBackground(new Color(33, 33, 33, 210));
        jLabelCronometroVazaoBg.setOpaque(true);
        jPanelVazao.add(jLabelCronometroVazaoBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 600));

        cpBaseEntradaVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpBaseEntradaVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpBaseEntradaVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpBaseEntradaVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpBaseEntradaVazao.setToolTipText("");
        cpBaseEntradaVazao.setBorder(null);
        cpBaseEntradaVazao.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        cpBaseEntradaVazao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpBaseEntradaVazaoActionPerformed(evt);
            }
        });
        jPanelVazao.add(cpBaseEntradaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 138, 255, 36));

        jLabelBaseEntradaVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelBaseEntradaVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelBaseEntradaVazao.setText("Nome da base de dados");
        jPanelVazao.add(jLabelBaseEntradaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 117, -1, -1));

        cpTabelaEntradaVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpTabelaEntradaVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpTabelaEntradaVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpTabelaEntradaVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpTabelaEntradaVazao.setBorder(null);
        jPanelVazao.add(cpTabelaEntradaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 138, 255, 36));

        jLabelTabelaEntradaVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelTabelaEntradaVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelTabelaEntradaVazao.setText("Nome da tabela");
        jPanelVazao.add(jLabelTabelaEntradaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 117, -1, -1));

        cpUsuarioEntradaVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpUsuarioEntradaVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpUsuarioEntradaVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpUsuarioEntradaVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpUsuarioEntradaVazao.setBorder(null);
        jPanelVazao.add(cpUsuarioEntradaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 213, 255, 36));

        jLabelUsuarioEntradaVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelUsuarioEntradaVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelUsuarioEntradaVazao.setText("Usuário da base de dados");
        jPanelVazao.add(jLabelUsuarioEntradaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 192, -1, -1));

        jLabelSenhaEntradaVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelSenhaEntradaVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelSenhaEntradaVazao.setText("Senha");
        jPanelVazao.add(jLabelSenhaEntradaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 192, -1, -1));

        cpSenhaEntradaVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpSenhaEntradaVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpSenhaEntradaVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpSenhaEntradaVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpSenhaEntradaVazao.setBorder(null);
        cpSenhaEntradaVazao.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jPanelVazao.add(cpSenhaEntradaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 213, 255, 36));

        cpUrlVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpUrlVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpUrlVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpUrlVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpUrlVazao.setText("localhost");
        cpUrlVazao.setToolTipText("");
        cpUrlVazao.setBorder(null);
        cpUrlVazao.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        cpUrlVazao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpUrlVazaoActionPerformed(evt);
            }
        });
        jPanelVazao.add(cpUrlVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 33, 563, 45));

        jLabelUrlVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelUrlVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelUrlVazao.setText("URL");
        jPanelVazao.add(jLabelUrlVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 12, -1, -1));

        jLabelEntradaVazao.setFont(new java.awt.Font("TakaoPGothic", 1, 14)); // NOI18N
        jLabelEntradaVazao.setForeground(new java.awt.Color(255, 255, 255));
        jLabelEntradaVazao.setText("ENTRADA");
        jPanelVazao.add(jLabelEntradaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(291, 96, -1, -1));

        cpUsuarioSaidaVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpUsuarioSaidaVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpUsuarioSaidaVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpUsuarioSaidaVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpUsuarioSaidaVazao.setBorder(null);
        jPanelVazao.add(cpUsuarioSaidaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 470, 255, 36));

        jLabelUsuarioSaidaVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelUsuarioSaidaVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelUsuarioSaidaVazao.setText("Usuário da base de dados");
        jPanelVazao.add(jLabelUsuarioSaidaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 449, -1, -1));

        jLabelSenhaSaidaVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelSenhaSaidaVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelSenhaSaidaVazao.setText("Senha");
        jPanelVazao.add(jLabelSenhaSaidaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 449, -1, -1));

        cpSenhaSaidaVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpSenhaSaidaVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpSenhaSaidaVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpSenhaSaidaVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpSenhaSaidaVazao.setBorder(null);
        cpSenhaSaidaVazao.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jPanelVazao.add(cpSenhaSaidaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 470, 255, 36));

        cpBaseSaidaVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpBaseSaidaVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpBaseSaidaVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpBaseSaidaVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpBaseSaidaVazao.setToolTipText("");
        cpBaseSaidaVazao.setBorder(null);
        cpBaseSaidaVazao.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        cpBaseSaidaVazao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpBaseSaidaVazaoActionPerformed(evt);
            }
        });
        jPanelVazao.add(cpBaseSaidaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 395, 255, 36));

        jLabelBaseSaidaVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelBaseSaidaVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelBaseSaidaVazao.setText("Nome da base de dados");
        jPanelVazao.add(jLabelBaseSaidaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 374, -1, -1));

        cpTabelaSaidaVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpTabelaSaidaVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpTabelaSaidaVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpTabelaSaidaVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpTabelaSaidaVazao.setBorder(null);
        jPanelVazao.add(cpTabelaSaidaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 395, 255, 36));

        jLabelTabelaSaidaVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelTabelaSaidaVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelTabelaSaidaVazao.setText("Nome da tabela");
        jPanelVazao.add(jLabelTabelaSaidaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 374, -1, -1));

        jLabelSaidaVazao.setFont(new java.awt.Font("TakaoPGothic", 1, 14)); // NOI18N
        jLabelSaidaVazao.setForeground(new java.awt.Color(255, 255, 255));
        jLabelSaidaVazao.setText("SAÍDA");
        jPanelVazao.add(jLabelSaidaVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(308, 353, -1, -1));
        jPanelVazao.add(jSeparatorVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 337, 563, 4));

        cpAnoVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpAnoVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpAnoVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpAnoVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpAnoVazao.setBorder(null);
        jPanelVazao.add(cpAnoVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 283, 130, 36));

        jLabelAnoVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelAnoVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelAnoVazao.setText("Ano");
        jPanelVazao.add(jLabelAnoVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 262, -1, -1));

        cpMesVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpMesVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpMesVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpMesVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpMesVazao.setBorder(null);
        jPanelVazao.add(cpMesVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 283, 130, 36));

        jLabelMesVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelMesVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelMesVazao.setText("Mês");
        jPanelVazao.add(jLabelMesVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 262, -1, -1));

        cpDiaFinalVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpDiaFinalVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpDiaFinalVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpDiaFinalVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpDiaFinalVazao.setBorder(null);
        jPanelVazao.add(cpDiaFinalVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 283, 97, 36));

        cpDiaInicialVazao.setBackground(new java.awt.Color(255, 255, 255));
        cpDiaInicialVazao.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpDiaInicialVazao.setForeground(new java.awt.Color(80, 80, 80));
        cpDiaInicialVazao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpDiaInicialVazao.setBorder(null);
        jPanelVazao.add(cpDiaInicialVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(381, 283, 97, 36));

        jLabelDiaInicialVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelDiaInicialVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelDiaInicialVazao.setText("Dia inicial");
        jPanelVazao.add(jLabelDiaInicialVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(381, 262, -1, -1));

        jLabelDiaFinalVazao.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelDiaFinalVazao.setForeground(new java.awt.Color(204, 204, 204));
        jLabelDiaFinalVazao.setText("Dia final");
        jPanelVazao.add(jLabelDiaFinalVazao, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 262, -1, -1));

        getContentPane().add(jPanelVazao);
        jPanelVazao.setBounds(370, 0, 660, 600);

        jPanelPerda.setVisible(false);
        jPanelPerda.setBackground(new java.awt.Color(97, 97, 97));
        jPanelPerda.setMinimumSize(new java.awt.Dimension(550, 600));
        jPanelPerda.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonCalcularPerda.setBackground(new java.awt.Color(255, 174, 0));
        jButtonCalcularPerda.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonCalcularPerda.setForeground(new java.awt.Color(51, 51, 51));
        jButtonCalcularPerda.setText("CALCULAR PERDA");
        jButtonCalcularPerda.setBorder(null);
        jButtonCalcularPerda.setBorderPainted(false);
        jButtonCalcularPerda.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonCalcularPerda.setRolloverEnabled(true);
        jButtonCalcularPerda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCalcularPerdaActionPerformed(evt);
            }
        });
        jPanelPerda.add(jButtonCalcularPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 524, 332, 54));

        jButtonInterromperPerda.setBackground(new java.awt.Color(19, 19, 19));
        jButtonInterromperPerda.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonInterromperPerda.setForeground(new java.awt.Color(255, 255, 255));
        jButtonInterromperPerda.setText("INTERROMPER");
        jButtonInterromperPerda.setBorder(null);
        jButtonInterromperPerda.setBorderPainted(false);
        jButtonInterromperPerda.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonInterromperPerda.setEnabled(false);
        jButtonInterromperPerda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInterromperPerdaActionPerformed(evt);
            }
        });
        jPanelPerda.add(jButtonInterromperPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(413, 524, 198, 54));

        jLabelCronometroPerdaTxt.setBackground(new java.awt.Color(255, 174, 0));
        jLabelCronometroPerdaTxt.setFont(new java.awt.Font("TakaoPGothic", 1, 16)); // NOI18N
        jLabelCronometroPerdaTxt.setForeground(new java.awt.Color(0, 0, 0));
        jLabelCronometroPerdaTxt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCronometroPerdaTxt.setText("Cálculo parado");
        jLabelCronometroPerdaTxt.setOpaque(true);
        jPanelPerda.add(jLabelCronometroPerdaTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 230, 530, 96));

        jLabelCronometroPerdaBg.setBackground(new Color(33, 33, 33, 210));
        jLabelCronometroPerdaBg.setOpaque(true);
        jPanelPerda.add(jLabelCronometroPerdaBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 600));

        cpBaseEntradaPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpBaseEntradaPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpBaseEntradaPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpBaseEntradaPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpBaseEntradaPerda.setToolTipText("");
        cpBaseEntradaPerda.setBorder(null);
        cpBaseEntradaPerda.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        cpBaseEntradaPerda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpBaseEntradaPerdaActionPerformed(evt);
            }
        });
        jPanelPerda.add(cpBaseEntradaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 138, 255, 36));

        jLabelBaseEntradaPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelBaseEntradaPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelBaseEntradaPerda.setText("Nome da base de dados");
        jPanelPerda.add(jLabelBaseEntradaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 117, -1, -1));

        cpTabelaEntradaPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpTabelaEntradaPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpTabelaEntradaPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpTabelaEntradaPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpTabelaEntradaPerda.setBorder(null);
        jPanelPerda.add(cpTabelaEntradaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 138, 255, 36));

        jLabelBaseEntradaTabela.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelBaseEntradaTabela.setForeground(new java.awt.Color(204, 204, 204));
        jLabelBaseEntradaTabela.setText("Nome da tabela");
        jPanelPerda.add(jLabelBaseEntradaTabela, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 117, -1, -1));

        cpUsuarioEntradaPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpUsuarioEntradaPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpUsuarioEntradaPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpUsuarioEntradaPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpUsuarioEntradaPerda.setBorder(null);
        jPanelPerda.add(cpUsuarioEntradaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 213, 255, 36));

        jLabelUsuarioEntradaPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelUsuarioEntradaPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelUsuarioEntradaPerda.setText("Usuário da base de dados");
        jPanelPerda.add(jLabelUsuarioEntradaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 192, -1, -1));

        jLabelSenhaEntradaPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelSenhaEntradaPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelSenhaEntradaPerda.setText("Senha");
        jPanelPerda.add(jLabelSenhaEntradaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 192, -1, -1));

        cpSenhaEntradaPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpSenhaEntradaPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpSenhaEntradaPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpSenhaEntradaPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpSenhaEntradaPerda.setBorder(null);
        cpSenhaEntradaPerda.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jPanelPerda.add(cpSenhaEntradaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 213, 255, 36));

        cpUrlPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpUrlPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpUrlPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpUrlPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpUrlPerda.setText("localhost");
        cpUrlPerda.setToolTipText("");
        cpUrlPerda.setBorder(null);
        cpUrlPerda.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        cpUrlPerda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpUrlPerdaActionPerformed(evt);
            }
        });
        jPanelPerda.add(cpUrlPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 33, 563, 45));

        jLabelUrlPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelUrlPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelUrlPerda.setText("URL");
        jPanelPerda.add(jLabelUrlPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 12, -1, -1));

        jLabelEntradaPerda.setFont(new java.awt.Font("TakaoPGothic", 1, 14)); // NOI18N
        jLabelEntradaPerda.setForeground(new java.awt.Color(255, 255, 255));
        jLabelEntradaPerda.setText("ENTRADA");
        jPanelPerda.add(jLabelEntradaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(291, 96, -1, -1));

        cpUsuarioSaidaPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpUsuarioSaidaPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpUsuarioSaidaPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpUsuarioSaidaPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpUsuarioSaidaPerda.setBorder(null);
        jPanelPerda.add(cpUsuarioSaidaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 470, 255, 36));

        jLabelUsuarioSaidaPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelUsuarioSaidaPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelUsuarioSaidaPerda.setText("Usuário da base de dados");
        jPanelPerda.add(jLabelUsuarioSaidaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 449, -1, -1));

        jLabelSenhaSaidaPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelSenhaSaidaPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelSenhaSaidaPerda.setText("Senha");
        jPanelPerda.add(jLabelSenhaSaidaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 449, -1, -1));

        cpSenhaSaidaPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpSenhaSaidaPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpSenhaSaidaPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpSenhaSaidaPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpSenhaSaidaPerda.setBorder(null);
        cpSenhaSaidaPerda.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jPanelPerda.add(cpSenhaSaidaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 470, 255, 36));

        cpBaseSaidaPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpBaseSaidaPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpBaseSaidaPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpBaseSaidaPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpBaseSaidaPerda.setToolTipText("");
        cpBaseSaidaPerda.setBorder(null);
        cpBaseSaidaPerda.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        cpBaseSaidaPerda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpBaseSaidaPerdaActionPerformed(evt);
            }
        });
        jPanelPerda.add(cpBaseSaidaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 395, 255, 36));

        jLabelBaseSaidaPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelBaseSaidaPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelBaseSaidaPerda.setText("Nome da base de dados");
        jPanelPerda.add(jLabelBaseSaidaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 374, -1, -1));

        cpTabelaSaidaPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpTabelaSaidaPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpTabelaSaidaPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpTabelaSaidaPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpTabelaSaidaPerda.setBorder(null);
        jPanelPerda.add(cpTabelaSaidaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 395, 255, 36));

        jLabelTabelaSaidaPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelTabelaSaidaPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelTabelaSaidaPerda.setText("Nome da tabela");
        jPanelPerda.add(jLabelTabelaSaidaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 374, -1, -1));

        jLabelSaidaPerda.setFont(new java.awt.Font("TakaoPGothic", 1, 14)); // NOI18N
        jLabelSaidaPerda.setForeground(new java.awt.Color(255, 255, 255));
        jLabelSaidaPerda.setText("SAÍDA");
        jPanelPerda.add(jLabelSaidaPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(308, 353, -1, -1));
        jPanelPerda.add(jSeparatorPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 337, 563, 4));

        cpAnoPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpAnoPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpAnoPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpAnoPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpAnoPerda.setBorder(null);
        jPanelPerda.add(cpAnoPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 283, 130, 36));

        jLabelAnoPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelAnoPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelAnoPerda.setText("Ano");
        jPanelPerda.add(jLabelAnoPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 262, -1, -1));

        cpMesPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpMesPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpMesPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpMesPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpMesPerda.setBorder(null);
        jPanelPerda.add(cpMesPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 283, 130, 36));

        jLabelMesPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelMesPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelMesPerda.setText("Mês");
        jPanelPerda.add(jLabelMesPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 262, -1, -1));

        cpDiaFinalPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpDiaFinalPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpDiaFinalPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpDiaFinalPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpDiaFinalPerda.setBorder(null);
        jPanelPerda.add(cpDiaFinalPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 283, 97, 36));

        cpDiaInicialPerda.setBackground(new java.awt.Color(255, 255, 255));
        cpDiaInicialPerda.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        cpDiaInicialPerda.setForeground(new java.awt.Color(80, 80, 80));
        cpDiaInicialPerda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cpDiaInicialPerda.setBorder(null);
        jPanelPerda.add(cpDiaInicialPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(381, 283, 97, 36));

        jLabelDiaInicialPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelDiaInicialPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelDiaInicialPerda.setText("Dia inicial");
        jPanelPerda.add(jLabelDiaInicialPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(381, 262, -1, -1));

        jLabelDiaFinalPerda.setFont(new java.awt.Font("TakaoPGothic", 0, 14)); // NOI18N
        jLabelDiaFinalPerda.setForeground(new java.awt.Color(204, 204, 204));
        jLabelDiaFinalPerda.setText("Dia final");
        jPanelPerda.add(jLabelDiaFinalPerda, new org.netbeans.lib.awtextra.AbsoluteConstraints(514, 262, -1, -1));

        getContentPane().add(jPanelPerda);
        jPanelPerda.setBounds(370, 0, 660, 600);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusGained

    private void cpBaseInterceptadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpBaseInterceptadorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cpBaseInterceptadorActionPerformed

    private void jButtonInterceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInterceptarActionPerformed
        
    }//GEN-LAST:event_jButtonInterceptarActionPerformed
    
    private void jComboBoxListaInterfacesInterceptadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxListaInterfacesInterceptadorActionPerformed
        
        
    }//GEN-LAST:event_jComboBoxListaInterfacesInterceptadorActionPerformed

    private void jButtonInterromperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInterromperActionPerformed

    }//GEN-LAST:event_jButtonInterromperActionPerformed

    private void cpUrlInterceptadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpUrlInterceptadorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cpUrlInterceptadorActionPerformed

       // Hover ------------------------------
    private void jLabelInterceptadorBtnMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelInterceptadorBtnMouseMoved
        jLabelInterceptadorBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
    }//GEN-LAST:event_jLabelInterceptadorBtnMouseMoved

    private void jButtonCalcularVazaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCalcularVazaoActionPerformed

    }//GEN-LAST:event_jButtonCalcularVazaoActionPerformed

    private void jButtonInterromperVazaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInterromperVazaoActionPerformed

    }//GEN-LAST:event_jButtonInterromperVazaoActionPerformed

    private void cpBaseEntradaVazaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpBaseEntradaVazaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cpBaseEntradaVazaoActionPerformed

    private void cpUrlVazaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpUrlVazaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cpUrlVazaoActionPerformed

    private void cpBaseSaidaVazaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpBaseSaidaVazaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cpBaseSaidaVazaoActionPerformed

    private void cpBaseEntradaPerdaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpBaseEntradaPerdaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cpBaseEntradaPerdaActionPerformed

    private void jButtonCalcularPerdaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCalcularPerdaActionPerformed

    }//GEN-LAST:event_jButtonCalcularPerdaActionPerformed

    private void jButtonInterromperPerdaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInterromperPerdaActionPerformed

    }//GEN-LAST:event_jButtonInterromperPerdaActionPerformed

    private void cpUrlPerdaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpUrlPerdaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cpUrlPerdaActionPerformed

    private void cpBaseSaidaPerdaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpBaseSaidaPerdaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cpBaseSaidaPerdaActionPerformed

    private void jLabelPerdaBtnMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelPerdaBtnMouseMoved
        jLabelPerdaBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLabelPerdaBtnMouseMoved

    private void jLabelVazaoBtnMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelVazaoBtnMouseMoved
        jLabelVazaoBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jLabelVazaoBtnMouseMoved

    private void jLabelPerdaBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelPerdaBtnMouseExited
        
    }//GEN-LAST:event_jLabelPerdaBtnMouseExited

    private void jLabelInterceptadorBtnMousePressed(java.awt.event.MouseEvent evt) {                                            
        jPanelInterceptador.setVisible(true);
        jPanelVazao.setVisible(false);
        jPanelPerda.setVisible(false);
        
        jLabelInterceptadorBtn.setBackground(new java.awt.Color(66, 66, 66));
        jLabelVazaoBtn.setBackground(new java.awt.Color(255, 90, 0));
        jLabelPerdaBtn.setBackground(new java.awt.Color(255, 174, 0));
        
        jLabelPerdaBtn.setForeground(new java.awt.Color(51,51,51));
        
        jLabelInterceptadorBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/IconeVermelho.PNG"))); // NOI18N
        jLabelInterceptadorBtn.setIconTextGap(10);
        jLabelVazaoBtn.setIcon(null); // NOI18N
        jLabelVazaoBtn.setIconTextGap(0);
        jLabelPerdaBtn.setIcon(null); // NOI18N
        jLabelPerdaBtn.setIconTextGap(0);
    } 
    
    private void jLabelPerdaBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelPerdaBtnMousePressed
        jPanelInterceptador.setVisible(false);
        jPanelVazao.setVisible(false);
        jPanelPerda.setVisible(true);
        
        jLabelInterceptadorBtn.setBackground(new java.awt.Color(181, 41, 9));
        jLabelVazaoBtn.setBackground(new java.awt.Color(255, 90, 0));
        jLabelPerdaBtn.setBackground(new java.awt.Color(66, 66, 66));
        
        jLabelPerdaBtn.setForeground(new java.awt.Color(255, 255, 255));
        
        jLabelInterceptadorBtn.setIcon(null); // NOI18N
        jLabelInterceptadorBtn.setIconTextGap(0);
        jLabelVazaoBtn.setIcon(null); // NOI18N
        jLabelVazaoBtn.setIconTextGap(0);
        jLabelPerdaBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/IconeAmarelo.PNG"))); // NOI18N
        jLabelPerdaBtn.setIconTextGap(10);
    }//GEN-LAST:event_jLabelPerdaBtnMousePressed

    private void jLabelVazaoBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelVazaoBtnMousePressed
        jPanelInterceptador.setVisible(false);
        jPanelPerda.setVisible(false);
        jPanelVazao.setVisible(true);
        
        jLabelInterceptadorBtn.setBackground(new java.awt.Color(181, 41, 9));
        jLabelVazaoBtn.setBackground(new java.awt.Color(66, 66, 66));
        jLabelPerdaBtn.setBackground(new java.awt.Color(255, 174, 0));
        
        jLabelPerdaBtn.setForeground(new java.awt.Color(51,51,51));
        
        jLabelInterceptadorBtn.setIcon(null); // NOI18N
        jLabelInterceptadorBtn.setIconTextGap(0);
        jLabelPerdaBtn.setIcon(null); // NOI18N
        jLabelPerdaBtn.setIconTextGap(0);
        jLabelVazaoBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/IconeLaranja.PNG"))); // NOI18N
        jLabelVazaoBtn.setIconTextGap(10);
    }//GEN-LAST:event_jLabelVazaoBtnMousePressed

    private void jLabelVazaoBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelVazaoBtnMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabelVazaoBtnMouseExited

        // Ao tirar o mouse de cima ----------
    private void jLabelInterceptadorBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelInterceptadorBtnMouseExited
        /*if(jPanelInterceptador.isVisible() == true){
            jLabelInterceptadorBtn.setBackground(new java.awt.Color(66, 66, 66));
            jLabelInterceptadorBtn.setForeground(new java.awt.Color(255, 255, 255));
        }else{
            jLabelInterceptadorBtn.setBackground(new java.awt.Color(181, 41, 9));
        }*/

    }//GEN-LAST:event_jLabelInterceptadorBtnMouseExited

//GEN-FIRST:event_jLabelInterceptadorBtnMousePressed
 
//GEN-LAST:event_jLabelInterceptadorBtnMousePressed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
 
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrameTeos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new JFrameTeos().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JFrameTeos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    public final int buscarInterfaces(){
        
        NetworkInterface[] list;

         list = JpcapCaptor.getDeviceList();
         int t = list.length;
        String[] listaInterfaces = new String[list.length];

        for (int x=0; x<list.length; x++) {
            listaInterfaces[x] = "["+(x+1)+"] "+ list[x].description+" - "+list[x].datalink_description+" - "+list[x].datalink_name+" - "+list[x].name;
            
            DefaultComboBoxModel model = (DefaultComboBoxModel) jComboBoxListaInterfacesInterceptador.getModel();
            model.addElement(listaInterfaces[x]);
        }
        return t;
     }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField cpAnoPerda;
    private javax.swing.JTextField cpAnoVazao;
    private javax.swing.JTextField cpBaseEntradaPerda;
    private javax.swing.JTextField cpBaseEntradaVazao;
    private javax.swing.JTextField cpBaseInterceptador;
    private javax.swing.JTextField cpBaseSaidaPerda;
    private javax.swing.JTextField cpBaseSaidaVazao;
    private javax.swing.JTextField cpDiaFinalPerda;
    private javax.swing.JTextField cpDiaFinalVazao;
    private javax.swing.JTextField cpDiaInicialPerda;
    private javax.swing.JTextField cpDiaInicialVazao;
    private javax.swing.JTextField cpMesPerda;
    private javax.swing.JTextField cpMesVazao;
    private javax.swing.JPasswordField cpSenhaEntradaPerda;
    private javax.swing.JPasswordField cpSenhaEntradaVazao;
    private javax.swing.JPasswordField cpSenhaInterceptador;
    private javax.swing.JPasswordField cpSenhaSaidaPerda;
    private javax.swing.JPasswordField cpSenhaSaidaVazao;
    private javax.swing.JTextField cpTabelaEntradaPerda;
    private javax.swing.JTextField cpTabelaEntradaVazao;
    private javax.swing.JTextField cpTabelaInterceptador;
    private javax.swing.JTextField cpTabelaSaidaPerda;
    private javax.swing.JTextField cpTabelaSaidaVazao;
    private javax.swing.JTextField cpUrlInterceptador;
    private javax.swing.JTextField cpUrlPerda;
    private javax.swing.JTextField cpUrlVazao;
    private javax.swing.JTextField cpUsuarioEntradaPerda;
    private javax.swing.JTextField cpUsuarioEntradaVazao;
    private javax.swing.JTextField cpUsuarioInterceptador;
    private javax.swing.JTextField cpUsuarioSaidaPerda;
    private javax.swing.JTextField cpUsuarioSaidaVazao;
    private javax.swing.JButton jButtonCalcularPerda;
    private javax.swing.JButton jButtonCalcularVazao;
    private javax.swing.JButton jButtonInterceptar;
    private javax.swing.JButton jButtonInterromper;
    private javax.swing.JButton jButtonInterromperPerda;
    private javax.swing.JButton jButtonInterromperVazao;
    private javax.swing.JComboBox<String> jComboBoxListaInterfacesInterceptador;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelAnoPerda;
    private javax.swing.JLabel jLabelAnoVazao;
    private javax.swing.JLabel jLabelBaseEntradaPerda;
    private javax.swing.JLabel jLabelBaseEntradaTabela;
    private javax.swing.JLabel jLabelBaseEntradaVazao;
    private javax.swing.JLabel jLabelBaseInterceptador;
    private javax.swing.JLabel jLabelBaseSaidaPerda;
    private javax.swing.JLabel jLabelBaseSaidaVazao;
    private javax.swing.JLabel jLabelCronometroInterceptadorBg;
    private javax.swing.JLabel jLabelCronometroInterceptadorTxt;
    private javax.swing.JLabel jLabelCronometroPerdaBg;
    private javax.swing.JLabel jLabelCronometroPerdaTxt;
    private javax.swing.JLabel jLabelCronometroVazaoBg;
    private javax.swing.JLabel jLabelCronometroVazaoTxt;
    private javax.swing.JLabel jLabelDiaFinalPerda;
    private javax.swing.JLabel jLabelDiaFinalVazao;
    private javax.swing.JLabel jLabelDiaInicialPerda;
    private javax.swing.JLabel jLabelDiaInicialVazao;
    private javax.swing.JLabel jLabelEntradaPerda;
    private javax.swing.JLabel jLabelEntradaVazao;
    private javax.swing.JLabel jLabelInterceptadorBtn;
    private javax.swing.JLabel jLabelInterfaceInterceptador;
    private javax.swing.JLabel jLabelMesPerda;
    private javax.swing.JLabel jLabelMesVazao;
    private javax.swing.JLabel jLabelPerdaBtn;
    private javax.swing.JLabel jLabelSaidaPerda;
    private javax.swing.JLabel jLabelSaidaVazao;
    private javax.swing.JLabel jLabelSenhaEntradaPerda;
    private javax.swing.JLabel jLabelSenhaEntradaVazao;
    private javax.swing.JLabel jLabelSenhaInterceptador;
    private javax.swing.JLabel jLabelSenhaSaidaPerda;
    private javax.swing.JLabel jLabelSenhaSaidaVazao;
    private javax.swing.JLabel jLabelTabelaEntradaVazao;
    private javax.swing.JLabel jLabelTabelaInterceptador;
    private javax.swing.JLabel jLabelTabelaSaidaPerda;
    private javax.swing.JLabel jLabelTabelaSaidaVazao;
    private javax.swing.JLabel jLabelUrlInterceptador;
    private javax.swing.JLabel jLabelUrlPerda;
    private javax.swing.JLabel jLabelUrlVazao;
    private javax.swing.JLabel jLabelUsuarioEntradaPerda;
    private javax.swing.JLabel jLabelUsuarioEntradaVazao;
    private javax.swing.JLabel jLabelUsuarioInterceptador;
    private javax.swing.JLabel jLabelUsuarioSaidaPerda;
    private javax.swing.JLabel jLabelUsuarioSaidaVazao;
    private javax.swing.JLabel jLabelVazaoBtn;
    private javax.swing.JPanel jPanelInterceptador;
    private javax.swing.JPanel jPanelMenuPrincipal;
    private javax.swing.JPanel jPanelPerda;
    private javax.swing.JPanel jPanelVazao;
    private javax.swing.JSeparator jSeparatorPerda;
    private javax.swing.JSeparator jSeparatorVazao;
    // End of variables declaration//GEN-END:variables
}
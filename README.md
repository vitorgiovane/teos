# **TEOS** - *The Eye Of Sauron*
  
![Logo](https://user-images.githubusercontent.com/5404361/51430960-679aea80-1c09-11e9-9474-ce0e9b80e35f.png)
  
TEOS é um sistema desktop desenvolvido em Java que possui as funcionalidades de interceptação de pacotes TCP/IP, cálculo de vazão e cálculo de perda de pacotes.

Esse sistema foi instalado em um servidor gateway, responsável por canalizar o fluxo de internet dos laboratórios do Centro de Ciências Exatas e Tecnológicas da Universidade Estadual de Montes Claros - UNIMONTES. O sistema TEOS, por 10 dias, interceptou os pacotes TCP/IP enviados e recebidos pelos computadores dos laboratórios. Os dados desses pacotes foram processados pelo sistema TEOS que persistiu em uma base de dados as medições de vazão e perda de pacotes. Extraiu-se dessas medições as características que em conjunto convergem para o resultado do **Trabalho de conclusão de curso** intitulado **[ANÁLISE DE TRÁFEGO DE INTERNET A PARTIR DA INTERCEPTAÇÃO E MEDIÇÃO DE PACOTES COM UM SISTEMA DESENVOLVIDO EM JAVA](https://drive.google.com/file/d/1Pw_-9A_aqDBUzz6iqCDKcznrV1tw80of/view?usp=sharing)**.


O sistema TEOS possui três componentes: o Interceptador, o Calculador de vazão e o Calculador de perda de pacotes. A seguir, esses componentes são descritos.
 
## Interceptador
![Interceptador](https://user-images.githubusercontent.com/5404361/51430587-c0b44f80-1c04-11e9-8ff0-50c815697b2d.png)
  
    
O Interceptador é o componente responsável por capturar os pacotes TCP/IP de uma interface de rede selecionada pelo usuário, criar uma conexão com uma base de dados informada pelo usuário, criar nessa base uma tabela de nome escolhido pelo usuário e
armazenar nessa tabela os seguintes dados para cada pacote interceptado:
- IP de origem;
- IP de destino;
- porta de origem;
- porta de destino;
- número de sequência;
- máximo de saltos;
- data, hora, minutos e segundos em que o pacote foi interceptado;
- número ack;
- tamanho do pacote em bytes; e
- janela.

![Interceptador ativo](https://user-images.githubusercontent.com/5404361/51430802-4df8a380-1c07-11e9-8322-f82576d116a5.png)
  
## Calculador de vazão

![Calculador de vazão](https://user-images.githubusercontent.com/5404361/51430823-88624080-1c07-11e9-8b98-775257bf03b8.png)

O Calculador de vazão é o componente responsável por realizar um cálculo de vazão por hora, para um determinado IP de origem e IP de destino, ou seja, em um determinado intervalo de hora X, todos os pacotes portadores de um IP de origem Y e um IP de destino Z são utilizados para produzir, a partir da divisão entre a soma de seus tamanhos e o intervalo de hora X, um resultado R, dado em bits por segundo (bps). Como essa divisão pode ter como resultado um valor decimal, adotou-se para o Calculador de vazão a precisão de quatro casas decimais.

![Calculador de vazão ativo](https://user-images.githubusercontent.com/5404361/51430842-d119f980-1c07-11e9-886c-40a1b717412f.png)
    
## Calculador de perda de pacotes

![Calculador de perda de pacotes](https://user-images.githubusercontent.com/5404361/51430860-feff3e00-1c07-11e9-8c11-888d7552ad42.png)

O Calculador de perda de pacotes é o componente responsável por realizar um cálculo percentual de pacotes perdidos por hora, entre um IP de origem e um IP de destino.

![Calculador de perda de pacotes ativo](https://user-images.githubusercontent.com/5404361/51430872-21915700-1c08-11e9-962c-56f16e36b28c.png)

## Monografia

**Título**: ANÁLISE DE TRÁFEGO DE INTERNET A PARTIR DA INTERCEPTAÇÃO E MEDIÇÃO DE PACOTES COM UM SISTEMA DESENVOLVIDO EM JAVA

**Autor**: Vitor Giovane Pereira Alves

**Orientador**: Prof. Nilton Alves Maia, DOUTOR.

**Instituição**: Universidade Estadual de Montes Claros - UNIMONTES

**Link para download**: [ANÁLISE DE TRÁFEGO DE INTERNET A PARTIR DA INTERCEPTAÇÃO E MEDIÇÃO DE PACOTES COM UM SISTEMA DESENVOLVIDO EM JAVA](https://drive.google.com/file/d/1Pw_-9A_aqDBUzz6iqCDKcznrV1tw80of/view?usp=sharing)


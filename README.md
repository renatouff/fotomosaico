# 🖼️ Gerador de Foto-Mosaico em Java

Uma aplicação desktop desenvolvido em Java que transforma uma imagem comum em um foto-mosaico. O programa analisa a intensidade de cor da imagem original e a recria utilizando um banco de imagens menores (chamadas de ladrilhos), encaixando cada ladrilho na região onde a cor média mais se aproxima.

## ✨ Funcionalidades

* **Interface Gráfica Intuitiva:** Desenvolvida em Java Swing, permitindo a seleção fácil de arquivos e diretórios sem necessidade de usar linha de comando.
* **Processamento Assíncrono:** A geração do mosaico roda em uma Thread separada, garantindo que a interface não congele durante cálculos pesados.
* **Ajuste de Precisão (Epslon):** O usuário pode definir o "Limiar de Cor", controlando a tolerância na variação de tons para o encaixe dos ladrilhos.
* **Controle de Repetição:** O algoritmo limita o uso de um mesmo ladrilho a no máximo 10 vezes, garantindo riqueza visual e diversidade na imagem final.
* **Abertura Automática:** Integração com o sistema operacional para abrir a imagem gerada automaticamente assim que o processamento é concluído.

## 📸 Demonstração

**Interface do Aplicativo:**
<div align="center">
  <img src="\git_img\01.png" alt="Interface do Gerador de Mosaico" width="450"/>
</div>

<br>

**Resultado do Processamento:**

| Imagem Original | Mosaico Gerado |
| :---: | :---: |
| <img src="\git_img\02.png" alt="Gato Original" width="400"/> | <img src="\git_img\03.png" alt="Gato em Mosaico" width="400"/> |

## 🚀 Como Funciona a Lógica (Algoritmo)

O motor do gerador baseia-se na comparação de intensidades de cor (tons de cinza):

1. **Pré-processamento:** A imagem de entrada é convertida para tons de cinza.
2. **Mapeamento dos Ladrilhos:** Cada imagem da pasta de ladrilhos é carregada. O sistema varre todos os pixels de cada ladrilho e calcula a sua **intensidade média de cor**.
3. **Varredura e "Match":** O programa percorre cada pixel da imagem original. Para cada pixel, ele busca na lista de ladrilhos qual possui a intensidade média mais próxima daquele pixel.
4. **Limiar (Epslon):** A diferença entre a cor do pixel e a cor do ladrilho deve ser menor ou igual ao valor de Epslon informado. Se a condição for satisfeita e o ladrilho não tiver extrapolado seu limite de usos, ele é inserido na nova matriz.
5. **Geração:** O resultado é uma nova imagem de alta resolução formada pela união geométrica de todos os ladrilhos validados.

## 🏗️ Estrutura e Arquitetura do Projeto

O projeto foi construído seguindo fortes princípios de **Orientação a Objetos**, como o Princípio da Responsabilidade Única e um bom encapsulamento:

* `app.FotoMosaicoApp`: Responsável exclusivamente pela Interface Gráfica (UI), captura de eventos de botões e gerenciamento de Threads.
* `fotomosaico.GeradorDeMosaico`: O núcleo do processamento. Orquestra a leitura da imagem principal, a iteração dos pixels e a montagem da nova matriz.
* `fotomosaico.Ladrilho`: Representa a entidade da imagem menor. Encapsula o seu próprio `BufferedImage`, calcula a sua própria intensidade média na instanciação e controla o seu estado interno de quantas vezes já foi utilizado.
* `fotomosaico.SaidaDeImagem`: Utilitário responsável apenas pela persistência do dado, salvando a matriz gerada no disco.

## 🛠️ Tecnologias Utilizadas

* **Java SE:** Linguagem base (JDK 8 ou superior recomendado).
* **Java Swing & AWT:** Para construção da interface gráfica e manipulação das imagens (`BufferedImage`, `ImageIO`, `Color`).
* **Orientação a Objetos:** Classes fortemente tipadas, encapsulamento e responsabilidades bem definidas.

## 💻 Como Executar

1. Clone este repositório:
   ```bash
   git clone [https://github.com/](https://github.com/)
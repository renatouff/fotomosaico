package fotomosaico;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Esta classe usa a imagem original como um "mapa" 
 * para saber onde posicionar cada Ladrilho. O resultado final é uma 
 * nova imagem construída exclusivamente pelo agrupamento dos ladrilhos.
 */
public class GeradorDeMosaico {
    private File arquivoEntrada;
    private File pastaLadrilhos;
    private int epslon;

    // Construtor: Apenas guarda as configurações que o usuário escolheu na tela
    public GeradorDeMosaico(File arquivoEntrada, File pastaLadrilhos, int epslon) {
        if (arquivoEntrada == null || pastaLadrilhos == null) {
            throw new IllegalArgumentException("Caminhos não podem ser nulos.");
        }
        this.arquivoEntrada = arquivoEntrada;
        this.pastaLadrilhos = pastaLadrilhos;
        this.epslon = epslon;
    }

    // Faz todo o trabalho de gerar a nova imagem
    public BufferedImage gerar() throws IOException {
        // 1. Prepara as imagens: Lê a foto escolhida e converte para tons de cinza
        BufferedImage imagemOriginal = ImageIO.read(arquivoEntrada);
        if (imagemOriginal == null) throw new IOException("Erro ao ler imagem original.");
        
        BufferedImage imagemCinza = converteEmCinza(imagemOriginal);
        
        // 2. Carrega todos os quadradinhos da pasta
        List<Ladrilho> ladrilhos = carregarLadrilhos(pastaLadrilhos);

        // 3. Calcula o tamanho da "tela gigante" onde o mosaico será desenhado
        int larguraOriginal = imagemCinza.getWidth();
        int alturaOriginal = imagemCinza.getHeight();
        int larguraLadrilho = ladrilhos.get(0).getImagem().getWidth();
        int alturaLadrilho = ladrilhos.get(0).getImagem().getHeight();

        // Cria a nova imagem em branco com as dimensões calculadas
        BufferedImage novaImagem = new BufferedImage(
            larguraOriginal * larguraLadrilho, 
            alturaOriginal * alturaLadrilho, 
            BufferedImage.TYPE_BYTE_GRAY
        );

        boolean incompleto = false;

        // 4. Varre a imagem original pixel por pixel (linha por linha, coluna por coluna)
        for (int x = 0; x < larguraOriginal; x++) {
            for (int y = 0; y < alturaOriginal; y++) {
                
                // Pega a cor (intensidade) do pixel atual
                int pixel = imagemCinza.getRGB(x, y);
                int intensidadePixel = pixel & 0x000000FF;

                // 5. Procura o ladrilho perfeito para esta cor
                Ladrilho escolhido = encontrarLadrilho(ladrilhos, intensidadePixel);
                
                if (escolhido != null) {
                    // Se achou, insere a imagem do ladrilho na tela gigante na posição exata
                    novaImagem.createGraphics().drawImage(escolhido.getImagem(), x * larguraLadrilho, y * alturaLadrilho, null);
                    escolhido.registrarUso(); // Avisa o ladrilho que ele foi usado
                } else {
                    incompleto = true; // Se nenhum ladrilho serviu para essa cor, marca como incompleto
                }
            }
        }

        // Se faltou ladrilho para preencher algum pixel, avisa no console
        if (incompleto) {
            System.err.println("Aviso: Não foi possível preencher toda a imagem com o Epslon informado!");
        }

        // Entrega o mosaico pronto
        return novaImagem;
    }

    // Procura na lista qual ladrilho tem a cor mais parecida com a do pixel atual
    private Ladrilho encontrarLadrilho(List<Ladrilho> ladrilhos, int intensidadePixel) {
        for (Ladrilho ladrilho : ladrilhos) {
            // Pergunta ao ladrilho se ele combina (usando o Epslon) e se ainda não passou do limite de usos
            if (ladrilho.combinaCom(intensidadePixel, this.epslon) && ladrilho.podeSerUsado()) {
                return ladrilho;
            }
        }
        return null; // Nenhum ladrilho serviu
    }

    // Lê todos os arquivos da pasta e transforma cada um em um objeto Ladrilho
    private List<Ladrilho> carregarLadrilhos(File pasta) throws IOException {
        List<Ladrilho> lista = new ArrayList<>();
        File[] arquivos = pasta.listFiles();
        if (arquivos != null) {
            for (File f : arquivos) {
                if (f.isFile()) {
                    lista.add(new Ladrilho(ImageIO.read(f)));
                }
            }
        }
        return lista;
    }

    // Utilitário para transformar uma imagem colorida em preto e branco (tons de cinza)
    private BufferedImage converteEmCinza(BufferedImage cvt) {
        BufferedImage nova = new BufferedImage(cvt.getWidth(), cvt.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        nova.createGraphics().drawImage(cvt, 0, 0, null);
        return nova;
    }
}
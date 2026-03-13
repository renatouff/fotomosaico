package fotomosaico;

import java.awt.image.BufferedImage;

/**
 * Representa uma imagem pequena (o "quadradinho") que vai compor o mosaico.
 */
public class Ladrilho {
    private BufferedImage imagem;
    private int intensidadeMedia;
    private int vezesUsado;
    private static final int MAX_USO = 10; // Limite máximo de vezes que este ladrilho pode aparecer

    // Construtor: Inicializa o ladrilho e já calcula a sua cor média logo de cara
    public Ladrilho(BufferedImage imagem) {
        this.imagem = imagem;
        this.vezesUsado = 0;
        this.intensidadeMedia = calcularIntensidadeMedia();
    }

    // Soma a cor (tom de cinza) de todos os pixels desta imagem e divide pelo total para achar a média
    private int calcularIntensidadeMedia() {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();
        long soma = 0;

        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                int pixel = imagem.getRGB(x, y);
                soma += (pixel & 0x000000FF); // Pega apenas a intensidade da cor
            }
        }
        return (int) (soma / (largura * altura));
    }

    // Verifica se o ladrilho ainda pode ser usado (ou seja, se foi usado menos de 10 vezes)
    public boolean podeSerUsado() {
        return vezesUsado < MAX_USO;
    }

    public void registrarUso() {
        this.vezesUsado++;
    }

    // Vê se a cor deste ladrilho é parecida com a cor do pixel original, 
    // respeitando a tolerância definida pelo usuário (Epslon)
    public boolean combinaCom(int intensidadePixel, int epslon) {
        return Math.abs(intensidadePixel - this.intensidadeMedia) <= epslon;
    }

    // Devolve a imagem em si para o Gerador conseguir desenhá-la na tela final
    public BufferedImage getImagem() {
        return imagem;
    }
}
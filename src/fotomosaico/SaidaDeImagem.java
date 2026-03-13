package fotomosaico;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Utilitário responsável apenas por pegar a matriz final (o mosaico pronto) 
 * que está na memória do computador e transformá-la em um arquivo de imagem real no disco.
 */
public class SaidaDeImagem {

    private File caminho;
    private BufferedImage imagem;

    // Construtor: Recebe e guarda o endereço (pasta e nome do arquivo) onde o mosaico será salvo
    public SaidaDeImagem(File file) {
        if (file == null) {
            // Segurança básica: impede que o programa tente salvar no "nada"
            throw new NullPointerException("Caminho informado nulo");
        }
        this.caminho = file;
    }

    // Recebe a imagem gigante recém-criada pelo Gerador e grava o arquivo de fato
    public void salvarMosaico(BufferedImage img) throws IOException {
        if (img == null) {
            // Se o gerador falhou e mandou uma imagem vazia, interrompe tudo
            throw new NullPointerException("Erro no processamento do mosaico");
        }
        
        this.imagem = img;
        
        try {
            // Pega a matriz de pixels e converte para um formato de imagem (JPG)
            ImageIO.write(imagem, "jpg", caminho);
        } catch (FileNotFoundException e) {
            // Se a pasta de destino não existir ou o Windows bloquear a gravação, avisa com clareza
            throw new FileNotFoundException(e + " Caminho informado para salvar a imagem inválido -> " + caminho);
        }
    }

}
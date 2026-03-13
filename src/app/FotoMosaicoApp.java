package app;

import fotomosaico.GeradorDeMosaico;
import fotomosaico.SaidaDeImagem;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class FotoMosaicoApp extends JFrame {

    private File arquivoEntrada;
    private File pastaLadrilhos = new File("imagens/ladrilhos"); // Caminho padrão relativo
    private final File arquivoSaida = new File("imagens/saidas/mosaico_final.jpg");

    private JLabel lblEntrada;
    private JLabel lblLadrilhos;
    private JSpinner spinEpslon;
    private JButton btnGerar;

    public FotoMosaicoApp() {
        super("Gerador de Foto Mosaico");
        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null); // Centraliza na tela
        setLayout(new BorderLayout(10, 10)); // Layout principal
    }

    private void inicializarComponentes() {
        JPanel painelPrincipal = new JPanel(new GridLayout(4, 1, 10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 1. Seleção da Imagem de Entrada ---
        JPanel painelEntrada = new JPanel(new BorderLayout(5, 5));
        lblEntrada = new JLabel("Nenhuma imagem selecionada");
        JButton btnEntrada = new JButton("Escolher Imagem...");
        btnEntrada.addActionListener(this::escolherImagemEntrada);
        painelEntrada.add(new JLabel("Imagem Original:"), BorderLayout.NORTH);
        painelEntrada.add(lblEntrada, BorderLayout.CENTER);
        painelEntrada.add(btnEntrada, BorderLayout.EAST);

        // --- 2. Seleção da Pasta de Ladrilhos ---
        JPanel painelLadrilhos = new JPanel(new BorderLayout(5, 5));
        lblLadrilhos = new JLabel(pastaLadrilhos.getPath());
        JButton btnLadrilhos = new JButton("Mudar Pasta...");
        btnLadrilhos.addActionListener(this::escolherPastaLadrilhos);
        painelLadrilhos.add(new JLabel("Pasta de Ladrilhos:"), BorderLayout.NORTH);
        painelLadrilhos.add(lblLadrilhos, BorderLayout.CENTER);
        painelLadrilhos.add(btnLadrilhos, BorderLayout.EAST);

        // --- 3. Configuração do Epslon ---
        JPanel painelEpslon = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelEpslon.add(new JLabel("Limiar de Cor (Epslon 0-255): "));
        spinEpslon = new JSpinner(new SpinnerNumberModel(30, 0, 255, 1)); // Padrão 30
        painelEpslon.add(spinEpslon);

        // --- Adicionando tudo ao painel ---
        painelPrincipal.add(painelEntrada);
        painelPrincipal.add(painelLadrilhos);
        painelPrincipal.add(painelEpslon);

        // --- 4. Botão de Gerar ---
        btnGerar = new JButton("Gerar Mosaico!");
        btnGerar.setFont(new Font("Arial", Font.BOLD, 16));
        btnGerar.setBackground(new Color(34, 139, 34)); // Verde
        btnGerar.setForeground(Color.WHITE); // Texto branco
        btnGerar.setFocusPainted(false);
        btnGerar.addActionListener(this::processarMosaico);

        // Montando a tela
        add(painelPrincipal, BorderLayout.CENTER);
        add(btnGerar, BorderLayout.SOUTH);
    }

    private void escolherImagemEntrada(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setDialogTitle("Selecione a Imagem Original");
        chooser.setFileFilter(new FileNameExtensionFilter("Imagens (JPG, PNG)", "jpg", "jpeg", "png"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            arquivoEntrada = chooser.getSelectedFile();
            lblEntrada.setText(arquivoEntrada.getName());
        }
    }

    private void escolherPastaLadrilhos(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setDialogTitle("Selecione a Pasta de Ladrilhos");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Só aceita pastas
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pastaLadrilhos = chooser.getSelectedFile();
            lblLadrilhos.setText(pastaLadrilhos.getName());
        }
    }

    private void processarMosaico(ActionEvent e) {
        if (arquivoEntrada == null || !arquivoEntrada.exists()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma imagem de entrada válida!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!pastaLadrilhos.exists() || !pastaLadrilhos.isDirectory()) {
            JOptionPane.showMessageDialog(this, "A pasta de ladrilhos informada não existe!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int epslon = (Integer) spinEpslon.getValue();

        // Desativa o botão e avisa o usuário
        btnGerar.setEnabled(false);
        btnGerar.setText("Processando... Aguarde.");

        // Thread separada para a interface não "travar" enquanto gera a imagem
        new Thread(() -> {
            try {
                // Certifica que a pasta de saídas existe
                arquivoSaida.getParentFile().mkdirs();

                // Regra de Negócio
                GeradorDeMosaico gerador = new GeradorDeMosaico(arquivoEntrada, pastaLadrilhos, epslon);
                BufferedImage imgFinal = gerador.gerar();

                SaidaDeImagem saida = new SaidaDeImagem(arquivoSaida);
                saida.salvarMosaico(imgFinal);

                SwingUtilities.invokeLater(() -> {
                    btnGerar.setText("Gerar Mosaico!");
                    btnGerar.setEnabled(true);
                    abrirImagemNoWindows(arquivoSaida);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    btnGerar.setText("Gerar Mosaico!");
                    btnGerar.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Erro ao gerar mosaico: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void abrirImagemNoWindows(File arquivo) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(arquivo);
            } else {
                JOptionPane.showMessageDialog(this, "Mosaico salvo em:\n" + arquivo.getAbsolutePath(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Mosaico salvo, mas não foi possível abrir automaticamente.\n" + arquivo.getAbsolutePath(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // --- Método Main que "dá o play" no aplicativo ---
    public static void main(String[] args) {
        // Inicia a interface gráfica na Thread correta do Swing
        SwingUtilities.invokeLater(() -> {
            new FotoMosaicoApp().setVisible(true);
        });
    }
}
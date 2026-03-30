import javax.swing.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class Main {
    public static String lerVersaoSalva() {
        try (BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
            String linha = br.readLine();
            if (linha != null && !linha.isEmpty()) {
                return linha;
            }
        } catch (Exception e) {
            // arquivo não existe ainda
        }
        return null;
    }

    public static void salvarVersao(String versao) {
        try (FileWriter fw = new FileWriter("config.txt")) {
            fw.write(versao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar versão");
        }
    }

    //verifica e exclui a pasta expecífica
    public static void verificarExcluir(){
        String versaoProtegida = lerVersaoSalva();
        String userHome = System.getProperty("user.home");
        String caminhoApps = userHome + "\\AppData\\Local\\CapCut\\Apps";

        File diretorio = new File(caminhoApps);
        File[] arquivos = diretorio.listFiles();

        if (arquivos != null) {
            boolean foundUpdate = false;
            for(File arquivo : arquivos){
                //so apagar se nao for a versao antiga
                if(arquivo.isDirectory() && !arquivo.getName().equals(versaoProtegida) && arquivo.getName().matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                    deletarPastaRecursivamente(arquivo);
                    foundUpdate = true;
                }
            }

            if(foundUpdate){
                JOptionPane.showMessageDialog(null, "Update Removido com Sucesso");
            }else {
                JOptionPane.showMessageDialog(null,"Nenhuma pasta de atualização foi encontrada.");
            }
        }else{
            JOptionPane.showMessageDialog(null,"Diretório não encontrado");
        }
    }

    //metodo auxiliar que apaga pastas com conteúdo
    private static void deletarPastaRecursivamente(File arquivo) {
        if (arquivo.isDirectory()) {
            File[] arquivosInternos = arquivo.listFiles();
            if (arquivosInternos != null) {
                for (File f : arquivosInternos) {
                    deletarPastaRecursivamente(f);
                }
            }
        }
        if (!arquivo.delete()) {
            JOptionPane.showMessageDialog(null,"Não foi possível deletar: " + arquivo.getAbsolutePath());
        }
    }

    //metodo para executar o CapCut
    public static void executarCapCut(){
        try {
            String userHome = System.getProperty("user.home");
            String versao = lerVersaoSalva();
            String caminho = userHome + "\\AppData\\Local\\CapCut\\Apps\\" + versao + "\\CapCut.exe";
            Runtime.getRuntime().exec(caminho);
            JOptionPane.showMessageDialog(null,"Executando CapCut");
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null,"Algo deu Errado, Arquivo não existe ou Corrompido" + e.getMessage());
        }
    }


    public static void main(String[] args) {
        try{
            String versao = lerVersaoSalva();

            if (versao == null) {

                versao = JOptionPane.showInputDialog(
                        "Digite a versão que deseja manter (ex: 2.4.0.634):");

                if (versao != null && !versao.isEmpty()) {
                    salvarVersao(versao);
                } else {
                    JOptionPane.showMessageDialog(null, "Versão inválida");
                    return;
                }
            }

            int opc = JOptionPane.showConfirmDialog(null,"Iniciar verificação e remoção das pastas Update do CapCut?","Confirmação",JOptionPane.YES_NO_OPTION);

            if (opc == JOptionPane.YES_OPTION){
                Main.verificarExcluir();

                int confirm = JOptionPane.showConfirmDialog(null,"Deseja iniciar o CapCut?","Confirmação",JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION){
                    Main.executarCapCut();
                }
                else {
                    JOptionPane.showMessageDialog(null,"Operação Finalizada");
                }
            }
            else {
                JOptionPane.showMessageDialog(null,"Operação Cancelada tenha um ótimo dia :)");
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null,"Ops algo deu errado"+ e.getMessage());
        }
    }
}
package school.sptech;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        System.out.println("Iniciando o tratamento de dados...");
        //Leitura dos arquivos de processos
        Path diretorioAlvo = Paths.get("./3");
        String termoFiltro = "processos";
        List<Processo> processos = new ArrayList<>();

        try(Stream<Path> paths = Files.list(diretorioAlvo)){
            List<Path> arquivosCSV = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().contains(termoFiltro.toLowerCase()))
                    .toList();

            if (arquivosCSV.isEmpty()) {
                System.out.println("Nenhum arquivo CSV encontrado na pasta '3' com o nome 'processos'.");
                return;
            }
            for (Path arquivo : arquivosCSV) {
                System.out.println("Processando arquivo: " + arquivo.getFileName());


                lerEProcessarArquivo(arquivo, processos);

            }
            sumarizar(processos, "3_listaProcessos_27-11-2025.csv");
            System.out.println("Arquivo principal escrito!");
        } catch (IOException e) {
            System.out.println("Erro ao acessar o diretório ou ler arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void lerEProcessarArquivo(Path caminhoArquivo, List<Processo> processos) {

        //Mapeando os nomes de processos e armazenando em uma variável
        Map<String, String> mapeamentoProcessos =  Mapeamento.criarMapeamento();

        try(Scanner sc = new Scanner(caminhoArquivo.toFile())){
        System.out.println("Escrevendo arquivo principal...");
        while( sc.hasNextLine() ){
            String linha = sc.nextLine().trim();

            if(linha.isEmpty() || linha.toLowerCase().startsWith("timestamp")) continue;

            //Separando a linha por campos separados por vírgula
            String[] campos = linha.split(",");
            //Checando se o CSV tem 9 colunas se não tiver, passa
            if(campos.length != 9) continue;
            //Renomeando processos
            String nomeOriginal = campos[2];
            String novoNome = mapeamentoProcessos.getOrDefault(nomeOriginal,"Generic_System_Process");

            campos[2] = novoNome;
            String nome = campos[2];
            Escrever e = new Escrever( campos[0], //Timestamp
                    Integer.valueOf(campos[1]), //Pid
                    campos[2], //Nome
                    Double.valueOf(campos[3]), //Cpu
                    Double.valueOf(campos[5]), //Ram
                    Double.valueOf(campos[6]), //Disco
                    Double.valueOf(campos[7]), //BytesLidos
                    Double.valueOf(campos[8]) ); //BytesEscritos
            e.escrever("3_mediaProcessos_27-11-2025.csv");
            //Criando um objeto da classe Processo e adicionando a lista processos
            processos.add( new Processo(
                    campos[0],
                    Integer.valueOf(campos[1]),
                    campos[2],
                    Double.valueOf(campos[3]),
                    Double.valueOf(campos[5]),
                    Double.valueOf(campos[6]),
                    Double.valueOf(campos[7]),
                    Double.valueOf(campos[8]) ) );
        }
    }catch(IOException e) {
        System.out.println("Erro ao ler arquivo: "+ e.getMessage());
    }
        System.out.println("Tratamento finalizado!");
    }

    public static void sumarizar(List<Processo> processos,String caminhoArquivo){
        System.out.println("Inicio da sumarização dos processos...");
        Map<String, List<Processo>> agrupar = new HashMap<>();

            for(Processo p : processos){
                agrupar.computeIfAbsent(p.getNome(), k -> new ArrayList<>()).add(p);
            }
            for (Map.Entry<String, List<Processo>> entry : agrupar.entrySet()) {
                String nome = entry.getKey();
                List<Processo> lista = entry.getValue();
                List<Double> valoresRam = lista.stream()
                        .map(Processo::getRam)
                        .toList();

                Double mediaCpu = lista.stream().mapToDouble(Processo::getCpu).average().orElse(0);
                Double mediaRam = lista.stream().mapToDouble(Processo::getRam).average().orElse(0);
                Double mediaBytesLidos = lista.stream().mapToDouble(Processo::getBytesLidos).average().orElse(0);
                Double mediaBytesEscritos = lista.stream().mapToDouble(Processo::getBytesEscritos).average().orElse(0);
                Double desvioPadraoRam = calcularDesvioPadrao(valoresRam);
                Double mediaTempoVida = lista.stream().mapToDouble(Processo::getTempoVida).average().orElse(0);

                mediaCpu = Math.round(mediaCpu * 100.0) / 100.0;
                mediaRam = Math.round(mediaRam * 100.0) / 100.0;
                mediaBytesLidos = Math.round(mediaBytesLidos * 100.0) / 100.0;
                mediaBytesEscritos = Math.round(mediaBytesEscritos * 100.0) / 100.0;

                Escrever e2 = new Escrever(nome,mediaCpu,mediaRam,desvioPadraoRam, mediaBytesLidos,mediaBytesEscritos,mediaTempoVida);
                e2.escrever(caminhoArquivo);
            }
        System.out.println("Fim da sumarização!");
    }
    public static Double calcularDesvioPadrao(List<Double> valores) {
        if (valores == null || valores.isEmpty()) {
            return 0.0;
        }

        double media = valores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double somaDiferencasQuadrado = 0.0;

        for (Double valor : valores) {
            somaDiferencasQuadrado += Math.pow(valor - media, 2);
        }

        if (valores.size() <= 1) {
            return 0.0;
        }

        double variancia = somaDiferencasQuadrado / valores.size();

        return Math.sqrt(variancia);
    }

}
package school.sptech;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        //Leitura do arquivo de processos
        try{
            Scanner sc = new Scanner(new File("3_processos27-11-2025.csv"));
            List<Processo> processos = new ArrayList<>();
            BufferedWriter bw = new BufferedWriter(new FileWriter("3_mediaProcessos_27-11-2025.csv"));
            bw.write("Timestamp,Pid,Nome,Cpu,TotalRAM,Ram,TempoVida,BytesLidos,BytesEscritos\n");
            //Mapeando os nomes de processos e armazenando em uma variável
            Map<String, String> mapeamentoProcessos =  Mapeamento.criarMapeamento();

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
                //Criando um objeto da classe Processo e adicionando a lista processos
                processos.add( new Processo(campos[0],Integer.valueOf(campos[1]),campos[2],Double.valueOf(campos[3]),Double.valueOf(campos[5]),Double.valueOf(campos[6]),Long.valueOf(campos[7]),Long.valueOf(campos[8]) ) );



            }
            sumarizar(processos);
        }catch(IOException e) {
            System.out.println("Erro ao ler arquivo: "+ e.getMessage());
        }

    }

    public static void sumarizar(List<Processo> processos){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("3_listaProcessos_27-11-2025.csv"));){
            bw.write("Nome,MediaCPU,MediaRAM,MediaBytesLidos,MediaBytesEscritos,MediaTempoVida,QuantidadeProcessos\n");
            Map<String, List<Processo>> agrupar = new HashMap<>();

            for(Processo p : processos){
                agrupar.computeIfAbsent(p.getNome(), k -> new ArrayList<>()).add(p);
            }

            for (Map.Entry<String, List<Processo>> entry : agrupar.entrySet()) {

                String nome = entry.getKey();
                List<Processo> lista = entry.getValue();

                double mediaCpu = lista.stream().mapToDouble(Processo::getCpu).average().orElse(0);
                double mediaRam = lista.stream().mapToDouble(Processo::getRam).average().orElse(0);
                double mediaBytesLidos = lista.stream().mapToLong(Processo::getBytesLidos).average().orElse(0);
                double mediaBytesEscritos = lista.stream().mapToLong(Processo::getBytesEscritos).average().orElse(0);
                double mediaTempoVida = lista.stream().mapToDouble(Processo::getTempoVida).average().orElse(0);

                mediaCpu = Math.round(mediaCpu*100.0)/100.0;
                mediaRam = Math.round(mediaRam*100.0)/100.0;
                mediaBytesLidos = Math.round(mediaBytesLidos*100.0)/100.0;
                mediaBytesEscritos = Math.round(mediaBytesEscritos*100.0)/100.0;


                bw.write(String.join(",",
                        nome,
                        String.valueOf(mediaCpu),
                        String.valueOf(mediaRam),
                        String.valueOf(mediaBytesLidos),
                        String.valueOf(mediaBytesEscritos),
                        String.valueOf(mediaTempoVida),
                        String.valueOf(lista.size())
                ));
                bw.newLine();
            }

        }catch(IOException e){
            System.out.println("Erro ao gerar sumarização: " + e.getMessage());
        }

    }

}
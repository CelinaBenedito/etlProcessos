package school.sptech;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        //Leitura do arquivo de processos
        try{
            Scanner sc = new Scanner(new File("3_processos27-11-2025.csv"));
            List<Processo> processos = new ArrayList<>();
            BufferedWriter bw = new BufferedWriter(new FileWriter("3_listaProcessos_27-11-2025.csv"));
            bw.write("Timestamp,Pid,Nome,,Cpu,TotalRAM,Ram,TempoVida,BytesLidos,BytesEscritos\n");
            Map<String, String> mapeamentoProcessos =  Mapeamento.criarMapeamento();

            while( sc.hasNextLine() ){
                String linha = sc.nextLine().trim();

                if(linha.isEmpty() || linha.toLowerCase().startsWith("timestamp")) continue;

                //Separando a linha por campos separados por vírgula
                String[] campos = linha.split(",");
                processos.add( new Processo(campos[2],Double.valueOf(campos[3]),Double.valueOf(campos[5]),Long.valueOf(campos[7]),Long.valueOf(campos[8]) ) );

                //Checando se o CSV tem 9 colunas se não tiver, passa
                if(campos.length != 9) continue;
                //Mapeando os nomes de processos e armazenando em uma variável

                //Renomeando processos
                String nomeOriginal = campos[2];
                String novoNome = mapeamentoProcessos.getOrDefault(nomeOriginal,"Generic_System_Process");

                campos[2] = novoNome;

                bw.write(String.join(",",
                        campos[0],
                        campos[1],
                        campos[2]
                        ));

                bw.newLine();
            }

        }catch(IOException e) {
            System.out.println("Erro ao ler arquivo: "+ e.getMessage());
        }

    }

}
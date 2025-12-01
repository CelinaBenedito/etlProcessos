package school.sptech;

import java.io.File;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.regions.Region;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class LambdaETL {
    private final S3Client s3 = S3Client.builder()
            .region(Region.US_EAST_1)
            .build();

    public void processarArquivo(String bucket, String key, String bucketSaida) {
        ZoneId horarioSP = ZoneId.of("America/Sao_Paulo");

        String destinatarioFinal = "OUTROS";
        String ano = String.format("%02d",LocalDate.now().getMonthValue());
        String mes = String.format("%02d", LocalDate.now().getMonthValue());
        int dia = LocalDate.now(horarioSP).getDayOfMonth();
        int numeroSemanaMes = 0;


        if (dia <= 7) {
            numeroSemanaMes = 1;
        }
        else if (dia <= 15) {
            numeroSemanaMes = 2;
        }
        else if (dia <= 22) {
            numeroSemanaMes = 3;
        }
        else {
            numeroSemanaMes = 4;
        }

        try{
            File arquivoLocal = new File("/tmp/" + new File(key).getName());

            if(arquivoLocal.exists() ){
                arquivoLocal.delete();
            }

            s3.getObject(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build(),
                    Paths.get(arquivoLocal.getAbsolutePath()));

            if(!key.toLowerCase().contains("processos")){
                //Ignorando arquivos que não tenham processos no nome
                System.out.println("Ignorando completamente: arquivo não contém 'processos':" + key);
                return;
            }

            destinatarioFinal = key.split("/")[0];

            Conexao conexao = new Conexao();
            ModeloDAO modeloDAO = new ModeloDAO();
            ModeloInfo info = modeloDAO.buscarPorLote(conexao.getConexao(), Integer.parseInt(destinatarioFinal));
            conexao.fecharConexao();

            if (info == null) {
                throw new RuntimeException("Nenhum modelo encontrado para o veiculo " + destinatarioFinal);
            }

            String nomeModelo = info.getNomeModelo();
            int fkModelo = info.getFkModelo();

            String saida = "/tmp/client" + System.currentTimeMillis() + ".csv";
            Main.LeituraCSV leitura = new Main.LeituraCSV(fkModelo);

            leitura.processar(arquivoLocal.getAbsolutePath(), saida);

            s3.putObject(PutObjectRequest.builder()
                            .bucket(bucketSaida)
                            .key("dashLatencia/ano/"+ano+"/"+nomeModelo+"/IDLote/"+destinatarioFinal+"/Mes/"+mes+"/"+"Semana"+numeroSemanaMes+"/Dia/"+dia+"/"+arquivoLocal.getName())
                            .build(),
                    Paths.get(saida));
            System.out.println("Arquivo processado e salvo com sucesso!");

        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro no processamento: " + e.getMessage());
        }
    }

    public String handleRequest(Map<String, Object> event) {
        try {

        }catch (Exception e) {
        e.printStackTrace();
        return "Erro no handleRequest: " + e.getMessage();
    }
    }

}

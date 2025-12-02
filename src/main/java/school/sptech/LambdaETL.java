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
    //Criando um cliente S3
    private final S3Client s3 = S3Client.builder()
            .region(Region.US_EAST_1)
            .build();

    //Definindo a data atual usando o fuso horário de São Paulo
    private final LocalDate HOJE = LocalDate.now( ZoneId.of("America/Sao_Paulo") );

    public String handleRequest(Map<String, Object> event) {
        try {
            String bucketEntrada = System.getenv("NOME_BUCKET_ENTRADA");
            String bucketSaida = System.getenv("NOME_BUCKET_SAIDA");

            String prefixoBase = "dashLatencia/ano/" + HOJE.getYear() + "/";

            List<CommonPrefix> modelos = listarPastas(bucketEntrada, prefixoBase);
            for (CommonPrefix modelo : modelos) {

                String nomeModelo = extrairNome(prefixoBase, modelo.prefix());
                String prefixoLotes = prefixoBase + nomeModelo + "/IDLote/";

                List<CommonPrefix> lotes = listarPastas(bucketEntrada, prefixoLotes);

                for (CommonPrefix lote : lotes) {

                    String idLote = extrairNome(prefixoLotes, lote.prefix());
                    String prefixoArquivos = construirPrefixoFinal(prefixoBase, nomeModelo, idLote);

                    List<S3Object> arquivos = listarArquivos(bucketEntrada, prefixoArquivos);

                    for (S3Object arquivo : arquivos) {
                        if (éArquivoProcessos(arquivo.key())) {
                            processarArquivo(bucketEntrada, arquivo.key(), bucketSaida);
                        }
                    }
                }
            }

            return "Processamento finalizado com sucesso!";
        }catch (Exception e) {
            e.printStackTrace();
            return "Erro no handleRequest: " + e.getMessage();
        }
    }

    private List<S3Object> listarArquivos(String bucket, String prefix) {
        return s3.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build()
        ).contents();
    }

    private String extrairNome(String prefixoBase, String prefixoCompleto) {
        return prefixoCompleto.replace(prefixoBase, "").replace("/", "");
    }

    private boolean éArquivoProcessos(String key) {
        return key.toLowerCase().contains("processos") && key.endsWith(".csv");
    }

    private String construirPrefixoFinal(String base, String modelo, String idLote) {
        int mes = HOJE.getMonthValue();
        int semana = getSemanaMes();
        int dia = HOJE.getDayOfMonth();

        return String.format(
                "%s%s/IDLote/%s/Mes/%02d/Semana%d/Dia/%02d/",
                base, modelo, idLote, mes, semana, dia
        );
    }

    private List<CommonPrefix> listarPastas(String bucket, String prefix) {
        return s3.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .delimiter("/")
                .build()
        ).commonPrefixes();
    }

    public void processarArquivo(String bucketEntrada, String key, String bucketSaida) {

        try {
            File arquivoLocal = baixarArquivoLocal(bucketEntrada, key);

            String destinatario = key.split("/")[0];

            ModeloInfo info = obterInfoModelo(destinatario);

            String arquivoProcessado = processarCSVLocal(arquivoLocal, info.getFkModelo());

            enviarParaS3(bucketSaida, info.getNomeModelo(), destinatario, arquivoLocal.getName(), arquivoProcessado);

            System.out.println("Arquivo processado e salvo com sucesso!");
        }

        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao processar arquivo: " + e.getMessage());
        }
    }

    private File baixarArquivoLocal(String bucket, String key) {
        File local = new File("/tmp/" + new File(key).getName());

        s3.getObject(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                Paths.get(local.getAbsolutePath())
        );

        return local;
    }

    private ModeloInfo obterInfoModelo(String destinatario) throws Exception {
        try (Conexao conexao = new Conexao()) {
            ModeloDAO dao = new ModeloDAO();
            ModeloInfo info = dao.buscarPorLote(conexao.getConexao(), Integer.parseInt(destinatario));

            if (info == null) {
                throw new RuntimeException("Nenhum modelo encontrado para o veículo " + destinatario);
            }
            return info;
        }
    }

    private String processarCSVLocal(File arquivo, int fkModelo) throws Exception {
        String saida = "/tmp/out_" + System.currentTimeMillis() + ".csv";
        Main.LeituraCSV leitura = new Main.LeituraCSV(fkModelo);
        leitura.processar(arquivo.getAbsolutePath(), saida);
        return saida;
    }

    private void enviarParaS3(String bucketSaida, String modelo, String idLote, String nomeArquivo, String arquivoProcessado) {

        int ano = HOJE.getYear();
        int mes = HOJE.getMonthValue();
        int semana = getSemanaMes();
        int dia = HOJE.getDayOfMonth();

        String destino = String.format(
                "dashLatencia/ano/%d/%s/IDLote/%s/Mes/%02d/Semana%d/Dia/%02d/%s",
                ano, modelo, idLote, mes, semana, dia, nomeArquivo
        );

        s3.putObject(PutObjectRequest.builder()
                        .bucket(bucketSaida)
                        .key(destino)
                        .build(),
                Paths.get(arquivoProcessado)
        );
    }

    public int getSemanaMes(){
        WeekFields wf = WeekFields.of(Locale.getDefault());
        LocalDate hoje = LocalDate.now();

        return hoje.get(wf.weekOfMonth());
    }

}

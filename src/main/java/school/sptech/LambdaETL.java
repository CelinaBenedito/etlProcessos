package school.sptech;

import java.io.File;
import software.amazon.awssdk.services.s3.S3Client;
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
        String destinatarioFinal = "OUTROS";
        String pastaMes = String.format("%02d", LocalDate.now().getMonthValue());
        int numeroSemanaMes = 0;

        ZoneId horarioSP = ZoneId.of("America/Sao_Paulo");
    }
}

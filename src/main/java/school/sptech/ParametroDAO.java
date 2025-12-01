package school.sptech;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParametroDAO {

    public static double cpuProcMin, cpuProcNeutro, cpuProcAtencao, cpuProcCritico;
    public static double ramProcUsoMin, ramProcUsoNeutro, ramProcUsoAtencao, ramProcUsoCritico;
    public static double ramProcMbMin, ramProcMbNeutro, ramProcMbAtencao, ramProcMbCritico;
    public static double discoProcMbMin, discoProcMbNeutro, discoProcMbAtencao, discoProcMbCritico;

    int idModelo = 1;

    public void carregarParametrosDoBanco(Connection conn, int fkModelo){

        String comandoSQL = "SELECT h.tipo, ph.parametroMinimo, ph.parametroNeutro, ph.parametroAtencao, ph.parametroCritico, ph.unidadeMedida " +
                "FROM parametroHardware ph JOIN hardware h ON ph.fkHardware = h.id WHERE ph.fkModelo = ?";

        try(PreparedStatement ps = conn.prepareStatement(comandoSQL)){
            ps.setInt(1, fkModelo);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    String tipo = rs.getString("tipo").toUpperCase();
                    double min = rs.getDouble("parametroMinimo");
                    double neutro = rs.getDouble("parametroNeutro");
                    double atencao = rs.getDouble("parametroAtencao");
                    double critico = rs.getDouble("parametroCritico");
                    String unidade = rs.getString("unidadeMedida").toUpperCase();

                    switch (tipo){
                        case "CPUPROCESSOS" -> {

                        cpuProcMin = min;
                        cpuProcNeutro = neutro;
                        cpuProcAtencao = atencao;
                        cpuProcCritico = critico;
                    }

                        case "RAMPROCESSOS" -> {
                            if (unidade.equals("USO")) {
                                ramProcUsoMin = min;
                                ramProcUsoNeutro = neutro;
                                ramProcUsoAtencao = atencao;
                                ramProcUsoCritico = critico;
                            } else if (unidade.equals("MB")) {
                                ramProcMbMin = min;
                                ramProcMbNeutro = neutro;
                                ramProcMbAtencao = atencao;
                                ramProcMbCritico = critico;
                            }
                        }

                        case "DISCOPROCESSOS" -> {
                            discoProcMbMin = min;
                            discoProcMbNeutro = neutro;
                            discoProcMbAtencao = atencao;
                            discoProcMbCritico = critico;
                        }
                    }

                }
            }
        }catch (SQLException e){
            System.out.println("Erro: "+e.getMessage());
        }

    }

}

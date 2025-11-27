package school.sptech;

public class Processo {
    private String nome,timestamp;
    private Integer pid;
    private Double cpu, ram;
    private Long bytesLidos, bytesEscritos;

    public Processo(String timestamp, Integer pid, String nome, Double cpu, Double ram, Long bytesLidos, Long bytesEscritos) {
        this.timestamp = timestamp;
        this.pid = pid;
        this.nome = nome;
        this.cpu = cpu;
        this.ram = ram;
        this.bytesLidos = bytesLidos;
        this.bytesEscritos = bytesEscritos;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public void setBytesLidos(Long bytesLidos) {
        this.bytesLidos = bytesLidos;
    }

    public void setBytesEscritos(Long bytesEscritos) {
        this.bytesEscritos = bytesEscritos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public Double getRam() {
        return ram;
    }

    public void setRam(Double ram) {
        this.ram = ram;
    }

    public long getBytesLidos() {
        return bytesLidos;
    }


    public long getBytesEscritos() {
        return bytesEscritos;
    }

}

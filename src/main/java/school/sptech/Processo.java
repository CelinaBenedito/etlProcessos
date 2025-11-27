package school.sptech;

public class Processo {
    private String nome;
    private Double cpu, ram;
    private Long bytesLidos, bytesEscritos;

    public Processo(String nome, Double cpu, Double ram, Long bytesLidos, Long bytesEscritos) {
        this.nome = nome;
        this.cpu = cpu;
        this.ram = ram;
        this.bytesLidos = bytesLidos;
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

    public void setBytesLidos(long bytesLidos) {
        this.bytesLidos = bytesLidos;
    }

    public long getBytesEscritos() {
        return bytesEscritos;
    }

    public void setBytesEscritos(long bytesEscritos) {
        this.bytesEscritos = bytesEscritos;
    }
}

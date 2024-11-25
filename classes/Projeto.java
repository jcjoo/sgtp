import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Projeto {
    private List<Tarefa> tarefas;
    private String nomeProjeto;
    private LocalDate dataFinal;
    private LocalDate dataInicial;
    private boolean status; 

    public Projeto(String nomeProjeto, LocalDate dataInicial, LocalDate dataFinal) {
        this.nomeProjeto = nomeProjeto;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.tarefas = new ArrayList<>();
        this.status = true;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(LocalDate dataFinal) {
        this.dataFinal = dataFinal;
    }

    public LocalDate getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(LocalDate dataInicial) {
        this.dataInicial = dataInicial;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void adicionarTarefa(Tarefa tarefa) {
        this.tarefas.add(tarefa);
    }

    public void removerTarefa(Tarefa tarefa) {
        this.tarefas.remove(tarefa);
    }
}

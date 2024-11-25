package com.uni.sgtp.classes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Projeto {

    private List<Tarefa> tarefas;
    private String nomeProjeto;
    private LocalDate dataFinal;
    private LocalDate dataInicial;
    private Status status;
    private int id;

    public enum Status {
        EM_ANDAMENTO,
        CONCLUIDO,
    }

    public Projeto(
        int id,
        String nomeProjeto,
        LocalDate dataInicial,
        LocalDate dataFinal
    ) {
        this.id = id;
        this.nomeProjeto = nomeProjeto;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.tarefas = new ArrayList<>();
        this.status = Status.EM_ANDAMENTO;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Status isStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

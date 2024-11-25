package com.uni.sgtp.classes;

import java.time.LocalDate;

public class Tarefa {

    private String nome;
    private boolean concluida;
    private LocalDate prazo;
    private Prioridade prioridade;
    private String descricao;
    private int id;

    public enum Prioridade {
        ALTA,
        MEDIA,
        BAIXA,
    }

    public Tarefa(
        int id,
        String nome,
        boolean concluida,
        LocalDate prazo,
        Prioridade prioridade
    ) {
        this.id = id;
        this.nome = nome;
        this.concluida = concluida;
        this.prazo = prazo;
        this.prioridade = prioridade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }
}

package com.morangosdoamor.WebCursos.model.entity;

import java.util.Objects;
import java.util.UUID;

import com.morangosdoamor.WebCursos.model.valueobject.Email;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Aluno {
    
    @Id
    private String id;
    private String nome;
    
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email"))
    private Email email;
    
    private String senha;

    public Aluno(String nome, String email, String senha) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.email = new Email(email);
        this.senha = senha;
    }

    public Aluno() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aluno aluno = (Aluno) o;
        return Objects.equals(id, aluno.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

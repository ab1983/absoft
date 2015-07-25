package com.algoboss.integration.small.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the emitente database table.
 * 
 */
@Entity
@NamedQuery(name="Emitente.findAll", query="SELECT e FROM Emitente e")
public class Emitente implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String registro;

	private String cep;

	private String cgc;

	private String cnae;

	private String comple;

	private String contato;

	private double cope;

	private String crt;

	private double cven;

	private String email;

	private String endereco;

	private String estado;

	private String hp;

	private double icme;

	private double icms;

	private String ie;

	private String im;

	private double impo;

	private double lucr;

	private String municipio;

	private String nome;

	private double rese;

	private String telefo;

	public Emitente() {
	}

	public String getRegistro() {
		return this.registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	public String getCep() {
		return this.cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCgc() {
		return this.cgc;
	}

	public void setCgc(String cgc) {
		this.cgc = cgc;
	}

	public String getCnae() {
		return this.cnae;
	}

	public void setCnae(String cnae) {
		this.cnae = cnae;
	}

	public String getComple() {
		return this.comple;
	}

	public void setComple(String comple) {
		this.comple = comple;
	}

	public String getContato() {
		return this.contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public double getCope() {
		return this.cope;
	}

	public void setCope(double cope) {
		this.cope = cope;
	}

	public String getCrt() {
		return this.crt;
	}

	public void setCrt(String crt) {
		this.crt = crt;
	}

	public double getCven() {
		return this.cven;
	}

	public void setCven(double cven) {
		this.cven = cven;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEndereco() {
		return this.endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getHp() {
		return this.hp;
	}

	public void setHp(String hp) {
		this.hp = hp;
	}

	public double getIcme() {
		return this.icme;
	}

	public void setIcme(double icme) {
		this.icme = icme;
	}

	public double getIcms() {
		return this.icms;
	}

	public void setIcms(double icms) {
		this.icms = icms;
	}

	public String getIe() {
		return this.ie;
	}

	public void setIe(String ie) {
		this.ie = ie;
	}

	public String getIm() {
		return this.im;
	}

	public void setIm(String im) {
		this.im = im;
	}

	public double getImpo() {
		return this.impo;
	}

	public void setImpo(double impo) {
		this.impo = impo;
	}

	public double getLucr() {
		return this.lucr;
	}

	public void setLucr(double lucr) {
		this.lucr = lucr;
	}

	public String getMunicipio() {
		return this.municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getRese() {
		return this.rese;
	}

	public void setRese(double rese) {
		this.rese = rese;
	}

	public String getTelefo() {
		return this.telefo;
	}

	public void setTelefo(String telefo) {
		this.telefo = telefo;
	}

}
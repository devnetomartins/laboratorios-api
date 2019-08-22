package com.laboratorios.apirest.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="TB_EXAME_LABORATORIO")
public class Associacao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@NotNull
	private long idExame;
	@NotNull
	private long idLaboratorio;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdExame() {
		return idExame;
	}

	public void setIdExame(long idExame) {
		this.idExame = idExame;
	}

	public long getIdLaboratorio() {
		return idLaboratorio;
	}

	public void setIdLaboratorio(long idLaboratorio) {
		this.idLaboratorio = idLaboratorio;
	}
	
	

}

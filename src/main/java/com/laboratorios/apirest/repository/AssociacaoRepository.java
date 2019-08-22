package com.laboratorios.apirest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laboratorios.apirest.models.Associacao;
import com.laboratorios.apirest.models.Exame;

public interface AssociacaoRepository extends JpaRepository<Associacao, Long> {
	
	List<Associacao> findByIdExame(long idExame);
	

}


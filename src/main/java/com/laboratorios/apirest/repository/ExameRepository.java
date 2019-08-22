package com.laboratorios.apirest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laboratorios.apirest.models.Exame;

public interface ExameRepository extends JpaRepository<Exame, Long> {
	
	List<Exame> findByStatus(boolean status);
	
	Exame findByNome(String nome);

}

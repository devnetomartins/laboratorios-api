package com.laboratorios.apirest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laboratorios.apirest.models.Laboratorio;

public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long>{
	//Persistencia de dados
	
	List<Laboratorio> findByStatus(boolean status);
}

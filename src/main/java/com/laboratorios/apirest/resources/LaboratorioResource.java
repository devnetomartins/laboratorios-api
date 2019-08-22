package com.laboratorios.apirest.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laboratorios.apirest.repository.LaboratorioRepository;
import com.laboratorios.apirest.models.Laboratorio;

@RestController
//Define a rota
@RequestMapping(value="/api")
public class LaboratorioResource {
	
	@Autowired
	LaboratorioRepository laboratorioRepository;
	
	@GetMapping("/laboratorios")
	public List<Laboratorio> listaLaboratorios(){
		//Retorno todos os laboratorios que estao ativos
		return laboratorioRepository.findByStatus(true);
	}
	
	@PostMapping("/laboratorios")
	public HashMap<String, String> createLaboratorio(@RequestBody Laboratorio laboratorio) {
		
		HashMap<String, String> map = new HashMap<>();
		
		//Valido se tem todos os campos
		boolean state = Stream.of(laboratorio.getNome(), laboratorio.getBairro(), 
				laboratorio.getCep(), laboratorio.getCidade(), laboratorio.getNumero(), 
				laboratorio.getUf(), laboratorio.getRua())
        .anyMatch(Objects::isNull);
		
		if(!state) {
			ExampleMatcher modelFind = ExampleMatcher.matching().withIgnorePaths("id");
			
			Example<Laboratorio> example = Example.of(laboratorio, modelFind);
			boolean exists = laboratorioRepository.exists(example);
			
			//.withMatcher("model", ignoreCase());
			
			if(!exists) {
				laboratorio.setStatus(true);
				laboratorioRepository.save(laboratorio);
				map.put("message", "Cadastrado com sucesso");
			}else {
				
				map.put("message", "Laboratorio ja cadastrado");
			}
			
		}else {
			map.put("message", "Informe todos os parametros");
		}
		return map;
		
	}
	
	@DeleteMapping("/laboratorios")
	public HashMap<String, String> deleteLaboratorio(@RequestBody Laboratorio laboratorio) {
		
		HashMap<String, String> map = new HashMap<>();
		
		boolean state = Stream.of(laboratorio.getId())
        .anyMatch(Objects::isNull);
		
		if(!state) {
			//Nao atualiza
		    map.put("message", "É preciso informar o id para desativar o laboratorio");
		}else {
			//Valida se o id existe
			Optional<Laboratorio> value = laboratorioRepository.findById(laboratorio.getId());
			if( value.isPresent()) {
				laboratorio = value.get();
				laboratorio.setStatus(false);
				laboratorioRepository.save(laboratorio);
				map.put("message", "Desativado com sucesso");
			}else {
				map.put("message", "ID não encontrado");
			}
			
		}
		
		return map;
		
		
	}
	
	@PutMapping("/laboratorios")
	public HashMap<String, String> updateLaboratorio(@RequestBody Laboratorio laboratorio) {
		
		HashMap<String, String> map = new HashMap<>();
		
		boolean state = Stream.of(laboratorio.getNome(), laboratorio.getBairro(), 
				laboratorio.getCep(), laboratorio.getCidade(), laboratorio.getNumero(), 
				laboratorio.getUf(), laboratorio.getRua(), laboratorio.getStatus(), laboratorio.getId())
        .anyMatch(Objects::isNull);
		
		if(!state) {
			Optional<Laboratorio> lab = laboratorioRepository.findById(laboratorio.getId());
			//Consulta se o ID existe
			System.out.println(lab.isPresent());
			if(lab.isPresent()){
				ExampleMatcher modelMatcher = ExampleMatcher.matching().withIgnorePaths("id");
				Example<Laboratorio> example = Example.of(laboratorio, modelMatcher);
				
				Optional<Laboratorio> result = laboratorioRepository.findOne(example);
				//Testo se retornou
				
				if(result.isPresent()) {
					System.out.println(result.get().getId());
					System.out.println(laboratorio.getId());
					if(result.get().getId() == laboratorio.getId()) {
						laboratorioRepository.save(laboratorio);
						map.put("message", "Atualizado com sucesso");
					
					}else {
						map.put("message", "Conteudo ja existente");
					}
				}else {
					laboratorioRepository.save(laboratorio);
					map.put("message", "Atualizado com sucesso");
				}
				
			}else {
				map.put("message", "ID não encontrado");
			}
			
		}else {
			map.put("message", "Informar todos os parametros");
		}
		
		return map;
	}
	
	

}

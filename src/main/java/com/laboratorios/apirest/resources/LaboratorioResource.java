package com.laboratorios.apirest.resources;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laboratorios.apirest.repository.LaboratorioRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laboratorios.apirest.models.Laboratorio;

@RestController
//Define a rota
@RequestMapping(value="/api")
public class LaboratorioResource {
	
	@Autowired
	LaboratorioRepository laboratorioRepository;
	
	private HttpStatus status;
	
	@GetMapping("/laboratorios")
	public List<Laboratorio> listaLaboratorios(){
		//Retorno todos os laboratorios que estao ativos
		return laboratorioRepository.findByStatus(true);
	}
	
	@PostMapping("/laboratorios")
	public ResponseEntity createLaboratorio(@RequestBody @Valid Laboratorio laboratorio) {
		
		HashMap<String, String> map = new HashMap<>();
		boolean state = false;
		
		//Valido se tem todos os campos
		
		state = Stream.of(laboratorio.getNome(), laboratorio.getBairro(), 
				laboratorio.getCep(), laboratorio.getCidade(), 
				laboratorio.getUf(), laboratorio.getRua())
        .anyMatch(s -> (s == null || ((String) s).trim().equals("")));
		
		
		
		if(!state) {
			ExampleMatcher modelFind = ExampleMatcher.matching().withIgnorePaths("id", "status");
			
			Example<Laboratorio> example = Example.of(laboratorio, modelFind);
			boolean exists = laboratorioRepository.exists(example);
			
			if(!exists) {
				laboratorio.setStatus(true);
				laboratorioRepository.save(laboratorio);
				status = HttpStatus.CREATED;
				map.put("message", "Cadastrado com sucesso");
			}else {
				status = HttpStatus.BAD_REQUEST;
				map.put("message", "Laboratorio ja cadastrado");
				
			}
			
		}else {
			status = HttpStatus.BAD_REQUEST;
			map.put("message", "Informe todos os parametros");
		}
		return new ResponseEntity<>(map,status);
		
	}
	
	@PutMapping("/laboratorios")
	public ResponseEntity updateLaboratorio(@RequestBody Laboratorio laboratorio) {
		
		HashMap<String, String> map = new HashMap<>();
		boolean state, state2;
		
		//Numero, id e status
		
		state = Stream.of(laboratorio.getNome(), laboratorio.getBairro(), 
				laboratorio.getCep(), laboratorio.getCidade(),
				laboratorio.getUf(), laboratorio.getRua())
        .anyMatch(s -> (s == null || ((String) s).trim().equals("")));
		
		state2 = Stream.of(laboratorio.getNumero(), laboratorio.getStatus(),
				laboratorio.getId()).anyMatch(Objects::isNull);
		
		if(!state && !state2) {
			Optional<Laboratorio> lab = laboratorioRepository.findById(laboratorio.getId());
			//Consulta se o ID existe

			if(lab.isPresent()){
				ExampleMatcher modelMatcher = ExampleMatcher.matching().withIgnorePaths("id", "status");
				Example<Laboratorio> example = Example.of(laboratorio, modelMatcher);
				
				Optional<Laboratorio> result = laboratorioRepository.findOne(example);
				//Testo se retornou
				
				if(result.isPresent()) {
					System.out.println(result.get().getId());
					System.out.println(laboratorio.getId());
					if(result.get().getId() == laboratorio.getId()) {
						laboratorioRepository.save(laboratorio);
						status = HttpStatus.OK;
						map.put("message", "Atualizado com sucesso");
					
					}else {
						status = HttpStatus.BAD_REQUEST;
						map.put("message", "Conteudo ja existente");
					}
				}else {
					laboratorioRepository.save(laboratorio);
					status = HttpStatus.OK;
					map.put("message", "Atualizado com sucesso");
				}
				
			}else {
				status = HttpStatus.NOT_FOUND;
				map.put("message", "ID não encontrado");
			}
			
		}else {
			status = HttpStatus.BAD_REQUEST;
			map.put("message", "Informar todos os parametros válidos");
		}
		
		return new ResponseEntity<>(map,status);
	}
	
	@DeleteMapping("/laboratorios")
	public ResponseEntity deleteLaboratorio(@RequestBody Laboratorio laboratorio) {
	 
		HashMap<String, String> map = new HashMap<>();
	
		boolean state = Stream.of(laboratorio.getId())
        .anyMatch(Objects::isNull);
		System.out.println(laboratorio.getId());
		if(!state) {
			//Valida se o id existe
			Optional<Laboratorio> value = laboratorioRepository.findById(laboratorio.getId());
			if( value.isPresent()) {
				laboratorio = value.get();
				laboratorio.setStatus(false);
				laboratorioRepository.save(laboratorio);
				status = HttpStatus.OK;
				map.put("message", "Desativado com sucesso");
			}else {
				status = HttpStatus.NOT_FOUND;
				map.put("message", "ID não encontrado");
			}
			
		}else {
			//Nao atualiza
			status = HttpStatus.BAD_REQUEST;
		    map.put("message", "É preciso informar o id para desativar o laboratorio");
			
		}
		
		return new ResponseEntity<>(map,status);
		
	}

}

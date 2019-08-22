package com.laboratorios.apirest.resources;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laboratorios.apirest.repository.AssociacaoRepository;
import com.laboratorios.apirest.repository.ExameRepository;
import com.laboratorios.apirest.repository.LaboratorioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laboratorios.apirest.models.Associacao;
import com.laboratorios.apirest.models.Exame;
import com.laboratorios.apirest.models.Laboratorio;
import java.lang.reflect.Field;

@RestController
//Define a rota
@RequestMapping(value="/api")
public class ExameResource {
	@Autowired
	ExameRepository exameRepository;
	@Autowired
	AssociacaoRepository associacaoRepository;
	@Autowired
	LaboratorioRepository laboratorioRepository;
	
	@GetMapping("/exames")
	public List<Exame> listaExames(){
		//Retorno todos os exames que estao ativos
		return exameRepository.findByStatus(true);
	}
	
	//Retorna exame pelo nome
	@GetMapping("/exames/{nome}")
	public HashMap<String, ArrayList> listaExameUnico(@PathVariable(value="nome") String nome){
		
		//Retorno todos os exames que estao ativos
		Exame exame = exameRepository.findByNome(nome);
		
		if(exame != null) {
			
			//consulto o id para conseguir a lista de laboratorios
			
			List<Associacao> lista = associacaoRepository.findByIdExame(exame.getId());
			
			ArrayList<Optional<Laboratorio>> lista_labs = new ArrayList<Optional<Laboratorio>>( );
			
			for(int y = 0; y<lista.size(); y++) {
				Optional<Laboratorio> lab = laboratorioRepository.findById(lista.get(y).getIdLaboratorio());
				lista_labs.add(lab);
			}
			HashMap<String, ArrayList> map = new HashMap<>();
		    map.put("labs", lista_labs);
		    return map;
			
		}else {
			return null;
		}
		
		
		
	}
	
	@PostMapping("/exames")
	public HashMap<String, String> createExame(@RequestBody Exame exame) {
		
		HashMap<String, String> map = new HashMap<>();
		
		boolean state = Stream.of(exame.getNome(), exame.getTipo())
        .anyMatch(Objects::isNull);
		
		if(!state) {
			exame.setStatus(true);
			
			ExampleMatcher modelFind = ExampleMatcher.matching().withIgnorePaths("id");
			
			Example<Exame> example = Example.of(exame, modelFind);
			boolean exists = exameRepository.exists(example);
			
			//.withMatcher("model", ignoreCase());
			
			if(!exists) {
				exame.setStatus(true);
				exameRepository.save(exame);
				map.put("message", "Cadastrado com sucesso!");
			}else {
				
				map.put("message", "Dados ja cadastrados!");
			}
			
		}else {
			map.put("message", "Parametros em falta");
		}
		return map;
		
	}
	
	@PutMapping("/exames")
	public ResponseEntity updateExame(@RequestBody @Valid Exame exame) {
		
		HashMap<String, String> map = new HashMap<>();
		
		boolean state = Stream.of(exame.getId(), exame.getNome(), exame.getTipo(), exame.getStatus())
		        .anyMatch(Objects::isNull);
		HttpStatus status = HttpStatus.OK;
		if(!state) {
			
			Optional<Exame> lab = exameRepository.findById(exame.getId());
			//Consulta se o ID existe
			System.out.println(lab.isPresent());
			if(lab.isPresent()){
				ExampleMatcher modelMatcher = ExampleMatcher.matching().withIgnorePaths("id");
				Example<Exame> example = Example.of(exame, modelMatcher);
				
				Optional<Exame> result = exameRepository.findOne(example);
				//Testo se retornou
				
				if(result.isPresent()) {
					System.out.println(result.get().getId());
					System.out.println(exame.getId());
					if(result.get().getId() == exame.getId()) {
						System.out.println(exame.getStatus());
						exameRepository.save(exame);
						map.put("message", "Atualizado com sucesso");
					
					}else {
						map.put("message", "Conteudo ja existente");
						status = HttpStatus.NOT_MODIFIED;
					}
				}else {
					exameRepository.save(exame);
					map.put("message", "Atualizado com sucesso");
				}
				
			}else {
				map.put("message", "ID não encontrado");
				status = HttpStatus.NOT_FOUND;
			}
			
		}else {
			map.put("message", "Parametros em falta");
			status = HttpStatus.BAD_REQUEST;
			
		}
		
		return new ResponseEntity<>(map,status);
	}

}
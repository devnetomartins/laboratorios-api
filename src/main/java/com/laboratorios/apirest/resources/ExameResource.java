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
import org.springframework.web.bind.annotation.DeleteMapping;
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
		//Validar o tipo do exame
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
	
	@DeleteMapping("/exames")
	public HashMap<String, String> deleteExame(@RequestBody Exame exame) {
		
		HashMap<String, String> map = new HashMap<>();
		
		boolean state = Stream.of(exame.getId())
        .anyMatch(Objects::isNull);
		
		if(!state) {
			//Valida se o id existe
			Optional<Exame> value = exameRepository.findById(exame.getId());
			if( value.isPresent()) {
				exame = value.get();
				exame.setStatus(false);
				exameRepository.save(exame);
				map.put("message", "Desativado com sucesso");
			}else {
				map.put("message", "ID não encontrado");
			}
			
		}else {
			//Nao atualiza
		    map.put("message", "É preciso informar o id para desativar o exame");
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
	
	@PostMapping("/exames/associacao")
	public ResponseEntity associaExame(@RequestBody @Valid Associacao associacao) {
		
		HashMap<String, String> map = new HashMap<>();
		
		HttpStatus status = HttpStatus.CREATED;
		
		boolean state = Stream.of(associacao.getIdExame(), associacao.getIdLaboratorio())
        .anyMatch(Objects::isNull);
		
		if(!state) {
			
			Optional<Exame> exame = exameRepository.findById(associacao.getIdExame());
			
			Optional<Laboratorio> lab = laboratorioRepository.findById(associacao.getIdLaboratorio());
			
			if(exame.isPresent()) {
				
				if(lab.isPresent()) {
					//Valido para saber se ambos estao ativos
					if(exame.get().getStatus() && lab.get().getStatus()) {
						//Valido se ja existe
						ExampleMatcher modelFind = ExampleMatcher.matching().withIgnorePaths("id");
						
						Example<Associacao> example = Example.of(associacao, modelFind);
						boolean exists = associacaoRepository.exists(example);
						
						if(!exists) {
							associacaoRepository.save(associacao);
							map.put("message", "Cadastrado com sucesso");
						}else {
							map.put("message", "Associacao ja cadastrada");
							status = HttpStatus.NOT_FOUND;
						}
					}else {
						map.put("message", "Exame ou Laboratorio desativado");
						status = HttpStatus.NOT_FOUND;
					}
					
				}else {
					map.put("message", "ID do laboratorio não encontrado");
					status = HttpStatus.NOT_FOUND;
				}
				
			}else {
				map.put("message", "ID do exame não encontrado");
				status = HttpStatus.NOT_FOUND;
			}
			
		}else {
			map.put("message", "Parametros em falta");
		}
		System.out.println(map.get("message"));
		return new ResponseEntity<>(map,status);
		
	}
	
	@DeleteMapping("/exames/associacao")
	public ResponseEntity desassociaExame(@RequestBody @Valid Associacao associacao) {
		
		HashMap<String, String> map = new HashMap<>();
		
		HttpStatus status = HttpStatus.CREATED;
		
		boolean state = Stream.of(associacao.getIdExame(), associacao.getIdLaboratorio())
        .anyMatch(Objects::isNull);
		
		if(!state) {
			
			Optional<Exame> exame = exameRepository.findById(associacao.getIdExame());
			
			Optional<Laboratorio> lab = laboratorioRepository.findById(associacao.getIdLaboratorio());
			
			if(exame.isPresent()) {
				
				if(lab.isPresent()) {
					//Valido para saber se ambos estao ativos
					if(exame.get().getStatus() && lab.get().getStatus()) {
						//Valido se ja existe
						ExampleMatcher modelFind = ExampleMatcher.matching().withIgnorePaths("id");
						
						Example<Associacao> example = Example.of(associacao, modelFind);
						Optional<Associacao> obj = associacaoRepository.findOne(example);
						
						if(obj.isPresent()) {
							associacaoRepository.delete(obj.get());
							map.put("message", "Deletado com sucesso");
							status = HttpStatus.OK;
						}else {
							map.put("message", "Associacao nao encontrada");
							status = HttpStatus.NOT_FOUND;
						}
						
					}else {
						map.put("message", "Exame ou Laboratorio desativado");
						status = HttpStatus.NOT_FOUND;
					}
					
				}else {
					map.put("message", "ID do laboratorio não encontrado");
					status = HttpStatus.NOT_FOUND;
				}
				
			}else {
				map.put("message", "ID do exame não encontrado");
				status = HttpStatus.NOT_FOUND;
			}
			
		}else {
			map.put("message", "Parametros em falta");
		}
		System.out.println(map.get("message"));
		return new ResponseEntity<>(map,status);
		
	}

}

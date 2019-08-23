package com.laboratorios.apirest.resources;

import java.util.ArrayList;
import java.util.Arrays;
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
	private HttpStatus status;
	
	@GetMapping("/exames")
	public List<Exame> listaExames(){
		//Retorno todos os exames que estao ativos
		return exameRepository.findByStatus(true);
	}
	
	//Retorna exame pelo nome
	@GetMapping("/exames/{nome}")
	public ResponseEntity listaExameUnico(@PathVariable(value="nome") String nome){
		
		//Retorno todos os exames que estao ativos
		Exame exame = exameRepository.findByNome(nome);
		
		HashMap<String, ArrayList> map = new HashMap<>();
		HashMap<String, String> map2 = new HashMap<>();
		
		if(exame != null) {
			
			//consulto o id para conseguir a lista de laboratorios
			
			List<Associacao> lista = associacaoRepository.findByIdExame(exame.getId());
			
			ArrayList<Optional<Laboratorio>> lista_labs = new ArrayList<Optional<Laboratorio>>( );
			
			for(int y = 0; y<lista.size(); y++) {
				Optional<Laboratorio> lab = laboratorioRepository.findById(lista.get(y).getIdLaboratorio());
				lista_labs.add(lab);
			}
			
		    map.put("labs", lista_labs);
		    status = HttpStatus.OK;
		    return new ResponseEntity<>(map,status);
		}else {
			//return null;
			map2.put("message", "Nome nao encontrado");
			status = HttpStatus.BAD_REQUEST;
			return new ResponseEntity<>(map2,status);
		}
		
	}
	
	@PostMapping("/exames")
	public ResponseEntity createExame(@RequestBody @Valid Exame exame) {
		//Validar o tipo do exame
		HashMap<String, String> map = new HashMap<>();
		
		ArrayList<String> tipos = new ArrayList<>(Arrays.asList("analise clinica", "imagem"));
		
		boolean state = Stream.of(exame.getNome(), exame.getTipo())
        .anyMatch(s -> (s == null || ((String) s).trim().equals("")));
		
		if(!state) {
			
			if(!tipos.contains(exame.getTipo())) {
				status = HttpStatus.BAD_REQUEST;
				map.put("message", "Tipo de exame invalido");
				return new ResponseEntity<>(map,status);
			}
			
			exame.setStatus(true);
			
			ExampleMatcher modelFind = ExampleMatcher.matching().withIgnorePaths("id");
			
			Example<Exame> example = Example.of(exame, modelFind);
			boolean exists = exameRepository.exists(example);
			
			if(!exists) {
				
				exameRepository.save(exame);
				status = HttpStatus.CREATED;
				map.put("message", "Cadastrado com sucesso!");
			}else {
				status = HttpStatus.BAD_REQUEST;
				map.put("message", "Dados ja cadastrados");
			}
			
		}else {
			status = HttpStatus.BAD_REQUEST;
			map.put("message", "Conteudo dos atributos invalidos");
			
		}
		return new ResponseEntity<>(map,status);
		
	}
	
	@DeleteMapping("/exames")
	public ResponseEntity deleteExame(@RequestBody Exame exame) {
		
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
				status = HttpStatus.OK;
				map.put("message", "Desativado com sucesso");
			}else {
				status = HttpStatus.BAD_REQUEST;
				map.put("message", "ID nao encontrado");
			}
			
		}else {
			//Nao atualiza
			status = HttpStatus.BAD_REQUEST;
		    map.put("message", "É preciso informar o id para desativar o exame");
		}
		
		return new ResponseEntity<>(map,status);
		
	}
	
	@PutMapping("/exames")
	public ResponseEntity updateExame(@RequestBody @Valid Exame exame) {
		
		HashMap<String, String> map = new HashMap<>();
		
		ArrayList<String> tipos = new ArrayList<>(Arrays.asList("analise clinica", "imagem"));
		boolean state, state2;
		
		state = Stream.of(exame.getNome(), exame.getTipo())
        .anyMatch(s -> (s == null || ((String) s).trim().equals("")));
		
		state2 = Stream.of(exame.getId(), exame.getStatus()).anyMatch(Objects::isNull);
		
		if(!state && !state2) {
			
			if(!tipos.contains(exame.getTipo())) {
				status = HttpStatus.BAD_REQUEST;
				map.put("message", "Tipo de exame invalido");
				return new ResponseEntity<>(map,status);
			}
			
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
						status = HttpStatus.OK;
						map.put("message", "Atualizado com sucesso");
					
					}else {
						map.put("message", "Conteudo ja existente");
						status = HttpStatus.BAD_REQUEST;
					}
				}else {
					exameRepository.save(exame);
					map.put("message", "Atualizado com sucesso");
					status = HttpStatus.OK;
				}
				
			}else {
				map.put("message", "ID nao encontrado");
				status = HttpStatus.BAD_REQUEST;
			}
			
		}else {
			map.put("message", "Parametros em falta");
			status = HttpStatus.BAD_REQUEST;
			
		}
		
		return new ResponseEntity<>(map,status);
	}

}

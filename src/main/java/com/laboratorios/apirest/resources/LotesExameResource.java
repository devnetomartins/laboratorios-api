package com.laboratorios.apirest.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laboratorios.apirest.repository.ExameRepository;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.laboratorios.apirest.models.Exame;
import com.laboratorios.apirest.models.Laboratorio;

@RestController
//Define a rota
@RequestMapping(value="/api")
public class LotesExameResource {
	
	@Autowired
	ExameRepository exameRepository;
	
	HttpStatus status;
	
	@PostMapping("/exames/lotes")
	public ResponseEntity<String> createExamesList(@RequestBody String json) {
		
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, List> lista = new HashMap<>();
		ArrayList<String> tipos = new ArrayList<>(Arrays.asList("analise clinica", "imagem"));
		
		Gson resp = new Gson();
		
		String jsonString = "";
		
		Gson gson = new Gson();
	    Type type = new TypeToken<List<Exame>>(){}.getType();
	    boolean state = false, exists = false;
	    
	    try {
	    	List<Exame> exameList = gson.fromJson(json, type);
	    	
	    	ArrayList<HashMap> list = new ArrayList<>();
	    	
		    //Valido se os cada atributo de cada objeto da lista estao preenchidos como requesitamos
		    for (Exame exame : exameList) {
		    	state = Stream.of(exame.getNome(), exame.getTipo())
		        .anyMatch(s -> (s == null || ((String) s).trim().equals("")));
		    	
		    	if(state) {
		    		break;
		    	}else {
		    		if(!tipos.contains(exame.getTipo())) {
						status = HttpStatus.BAD_REQUEST;
						map.put("message", "Tipo de exame invalido");
						jsonString = resp.toJson(map);
						return new ResponseEntity<>(jsonString,status);
					}
		    	}
		    }
		    
		    
		    //Valida se o state esta como true
		    if(state) {
		    	map.put("message", "Parametros inválidos");
		    	jsonString = resp.toJson(map);
		    	status = HttpStatus.BAD_REQUEST;
		    	return new ResponseEntity<>(jsonString,status);
		    }
		    
		    // valido um por um para saber se nao existe cadastro com os parametros
		    
		    ArrayList<Integer> listIndex = new ArrayList<>();
		    
		    ExampleMatcher modelFind = ExampleMatcher.matching().withIgnorePaths("id","status");
		    int cont = 0;
		    for (Exame exame : exameList) {
		    	Example<Exame> example = Example.of(exame, modelFind);
		    	exists = exameRepository.exists(example);
		    	if(!exists) {
		    		exame.setStatus(true);
		    		exameRepository.save(exame);
					listIndex.add(cont);
				}
		    	cont+=1;
		    }
		    /*Vejo se falhou algum devido a duplicidade
		      Limpo lista de laboratorios*/
		    if(!listIndex.isEmpty()) {
		    	int value;
		    	if(listIndex.size() == exameList.size()) {
		    		exameList.clear();
		    	}else {
			    	for(Integer y : listIndex) {
			    		value = y;
			    		System.out.println(exameList.remove(value));
			    	}
		    	}

		    	if(exameList.isEmpty()) {
		    		//Deu certo
		    		map.put("message", "Cadastrado com sucesso");
		    		status = HttpStatus.CREATED;
			    	jsonString = resp.toJson(map);
		    	}else {
		    		//retorna o que nao deu certo
		    		lista.put("exames", exameList);
		    		map.put("message", "Segue a lista de exames nao cadastrados");
		    		list.add(map);
			    	list.add(lista);
			    	jsonString = resp.toJson(list);
			    	status = HttpStatus.BAD_REQUEST;
		    		
		    	}
		    }else {
		    	//Nao cadastrou nada retorna lista de laboratorios completa
		    	lista.put("exames", exameList);
	    		map.put("message", "Segue a lista de exames nao cadastrados");
	    		list.add(map);
		    	list.add(lista);
		    	jsonString = resp.toJson(list);
		    	status = HttpStatus.BAD_REQUEST;
		    }
			
	    }catch (JsonParseException e) {
	    	//Json nao esta no formato solicitado
	    	map.put("message", "Request nao esta no formato correto");
	    	jsonString = resp.toJson(map);
	    	status = HttpStatus.BAD_REQUEST;
	    }
	    
	    return new ResponseEntity<>(jsonString,status);
	}
	
	@PutMapping("/exames/lotes")
	public ResponseEntity updateExamesList(@RequestBody String json) {
		
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, List> lista = new HashMap<>();
		ArrayList<String> tipos = new ArrayList<>(Arrays.asList("analise clinica", "imagem"));
		
		Gson resp = new Gson();
		
		String jsonString = "";
		
		Gson gson = new Gson();
	    Type type = new TypeToken<List<Exame>>(){}.getType();
	    boolean state = false, state2 = false, exists = false;
		
	    try {
	    	
	    	List<Exame> exameList = gson.fromJson(json, type);
	    	
	    	ArrayList<HashMap> list = new ArrayList<>();
	    	
		    //Valido se os cada atributo de cada objeto da lista estao preenchidos como requesitamos
		    for (Exame exame : exameList) {
		    	state = Stream.of(exame.getNome(), exame.getTipo())
		        .anyMatch(s -> (s == null || ((String) s).trim().equals("")));
		    	state2 = Stream.of(exame.getId(),exame.getStatus()).anyMatch(Objects::isNull);
		    	
		    	if(state || state2) {
		    		break;
		    	}else {
		    		if(!tipos.contains(exame.getTipo())) {
						status = HttpStatus.BAD_REQUEST;
						map.put("message", "Tipo de exame invalido");
						jsonString = resp.toJson(map);
						return new ResponseEntity<>(jsonString,status);
					}
		    	}
		    }
		    
		    //Valida se o state esta como true
		    if(state || state2) {
		    	map.put("message", "Parametros inválidos");
		    	jsonString = resp.toJson(map);
		    	status = HttpStatus.BAD_REQUEST;
		    	return new ResponseEntity<>(jsonString,status);
		    }
	    	
	    	
			Optional<Exame> ex;
			ExampleMatcher modelMatcher = ExampleMatcher.matching().withIgnorePaths("id","status");
			Example<Exame> example;
			Optional<Exame> result;
			ArrayList<Integer> listIndex = new ArrayList<>();
			//Valido um por um
			int cont = 0;
			for(Exame exame : exameList) {
				
				 ex = exameRepository.findById(exame.getId());
				 
				 if(ex.isPresent()){
					 
					 example = Example.of(exame, modelMatcher);
					
					 result = exameRepository.findOne(example);
					//Testo se retornou
					
					if(result.isPresent()) {
						//Se conseguiu
						if(result.get().getId() == ex.get().getId()) {
							exameRepository.save(exame);
							listIndex.add(cont);
							
						}
						
					}else {
						//Se conseguiu
						exameRepository.save(exame);
						listIndex.add(cont);
					}
				}
				 cont+= 1;
			}
			
			/*Vejo se falhou algum devido a duplicidade
		      Limpo lista de laboratorios*/
		    if(!listIndex.isEmpty()) {
		    	int value;
		    	
		    	if(listIndex.size() == exameList.size()) {
		    		exameList.clear();
		    	}else {
		    		//Limpo para retornar os que falharam
			    	for(Integer y : listIndex) {
			    		value = y;
			    		exameList.remove(value);
			    	}
		    	}

		    	if(exameList.isEmpty()) {
		    		//Deu certo
		    		status = HttpStatus.OK;
					map.put("message", "Atualizado com sucesso");
			    	jsonString = resp.toJson(map);
		    	}else {
		    		//retorna o que nao deu certo
		    		lista.put("exames", exameList);
		    		map.put("message", "Segue a lista de exames nao atualizados");
		    		list.add(map);
			    	list.add(lista);
			    	jsonString = resp.toJson(list);
			    	status = HttpStatus.BAD_REQUEST;
		    		
		    	}
		    }else {
		    	//Nao cadastrou nada retorna lista de laboratorios completa
		    	lista.put("exames", exameList);
	    		map.put("message", "Segue a lista de exames nao atualizados");
	    		list.add(map);
		    	list.add(lista);
		    	jsonString = resp.toJson(list);
		    	status = HttpStatus.BAD_REQUEST;
		    }
	    	
	    }catch (JsonParseException e) {
	    	//Json nao esta no formato solicitado
	    	map.put("message", "Request nao esta no formato correto");
	    	status = HttpStatus.BAD_REQUEST;
	    	jsonString = resp.toJson(map);
	    }
	    
	    return new ResponseEntity<>(jsonString,status);
	}
	
	@DeleteMapping("/exames/lotes")
	public ResponseEntity deleteExamesList(@RequestBody String json) {
		
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, List> lista = new HashMap<>();
		
		Gson resp = new Gson();
		
		String jsonString = "";
		
		Gson gson = new Gson();
		
	    Type type = new TypeToken<List<Exame>>(){}.getType();
	    boolean state = false, exists = false;
	    
	    status = HttpStatus.BAD_REQUEST;
	    
	    try {
	    	
	    	List<Exame> exameList = gson.fromJson(json, type);
	    	
	    	ArrayList<HashMap> list = new ArrayList<>();
	    	
			for (Exame ex : exameList) {
				
		    	if(Stream.of(ex.getId()).anyMatch(Objects::isNull)) {
		    		state = true;
		    		break;
		    		
		    	}
		    	
			}
			
			if(state) {
		    	map.put("message", "Parametros inválidos");
		    	jsonString = resp.toJson(map);
		    	status = HttpStatus.BAD_REQUEST;
		    	return new ResponseEntity<>(jsonString,status);
		    }
			
			Optional<Exame> ex;
		
			ArrayList<Integer> listIndex = new ArrayList<>();
			//Valido um por um
			int cont = 0;
			for(Exame exame : exameList) {
				
				 ex = exameRepository.findById(exame.getId());
				//Testo se retornou
				 if(ex.isPresent()){
					 
					 ex.get().setStatus(false);
					 exameRepository.save(ex.get());
					 listIndex.add(cont);
					
				}
				 cont+= 1;
			}
			
			/*Vejo se falhou algum devido a nao existir o id
		      Limpo lista de laboratorios*/
		    if(!listIndex.isEmpty()) {
		    	int value;
		    	
		    	if(listIndex.size() == exameList.size()) {
		    		exameList.clear();
		    	}else {
		    		//Limpo para retornar os que falharam
			    	for(Integer y : listIndex) {
			    		value = y;
			    		exameList.remove(value);
			    	}
		    	}

		    	if(exameList.isEmpty()) {
		    		//Deu certo
		    		status = HttpStatus.OK;
					map.put("message", "Deletado com sucesso");
			    	jsonString = resp.toJson(map);
		    	}else {
		    		//retorna o que nao deu certo
		    		lista.put("exames", exameList);
		    		map.put("message", "Segue a lista de exames nao deletados");
		    		list.add(map);
			    	list.add(lista);
			    	jsonString = resp.toJson(list);
			    	status = HttpStatus.BAD_REQUEST;
		    		
		    	}
		    }else {
		    	//Nao cadastrou nada retorna lista de laboratorios completa
		    	lista.put("exames", exameList);
	    		map.put("message", "Segue a lista de exames nao deletados");
	    		list.add(map);
		    	list.add(lista);
		    	jsonString = resp.toJson(list);
		    	status = HttpStatus.BAD_REQUEST;
		    }
	    	
	    }catch (JsonParseException e) {
	    	//Json nao esta no formato solicitado
	    	map.put("message", "Request nao esta no formato correto");
	    	status = HttpStatus.BAD_REQUEST;
	    	jsonString = resp.toJson(map);
	    }
	    
	    return new ResponseEntity<>(jsonString,status);
	}

}

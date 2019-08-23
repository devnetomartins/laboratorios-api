package com.laboratorios.apirest.resources;

import java.util.ArrayList;
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

import com.laboratorios.apirest.repository.LaboratorioRepository;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.laboratorios.apirest.models.Laboratorio;

@RestController
//Define a rota
@RequestMapping(value="/api")
public class LotesLabResource {
	
	@Autowired
	LaboratorioRepository laboratorioRepository;
	
	HttpStatus status;
	
	@PostMapping("/laboratorios/lotes")
	public ResponseEntity createLaboratorioList(@RequestBody String json) {
		
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, List> lista = new HashMap<>();
		
		Gson resp = new Gson();
		
		String jsonString;
		
		Gson gson = new Gson();
	    Type type = new TypeToken<List<Laboratorio>>(){}.getType();
	    boolean state = false, exists = false;
	    
	    try {
	    	List<Laboratorio> laboratorioList = gson.fromJson(json, type);
	    	
	    	ArrayList<HashMap> list = new ArrayList<>();
	    	
		    //Valido se os cada atributo de cada objeto da lista estao preenchidos
		    for (Laboratorio lab : laboratorioList) {
		    	
		    	if(!Stream.of(lab.getNumero()).anyMatch(Objects::isNull)) {
		    		state = Stream.of(lab.getNome(),
			    			lab.getRua(), lab.getBairro(),
			    			lab.getCidade(), lab.getUf(), lab.getCep()).anyMatch(s -> (s == null || ((String) s).trim().equals("")));
			    	if(state) {
			    		break;
			    	}
		    	}else {
		    		state = false;
		    		break;
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
		    
		    ExampleMatcher modelFind = ExampleMatcher.matching().withIgnorePaths("id", "status");
		    int cont = 0;
		    for (Laboratorio lab : laboratorioList) {
		    	Example<Laboratorio> example = Example.of(lab, modelFind);
		    	exists = laboratorioRepository.exists(example);
		    	if(!exists) {
		    		lab.setStatus(true);
					laboratorioRepository.save(lab);
					listIndex.add(cont);
				}
		    	cont+=1;
		    }
		    /*Vejo se falhou algum devido a duplicidade
		      Limpo lista de laboratorios*/
		    if(!listIndex.isEmpty()) {
		    	int value;
		    	if(listIndex.size() == laboratorioList.size()) {
		    		laboratorioList.clear();
		    	}else {
			    	for(Integer y : listIndex) {
			    		value = y;
			    		System.out.println(laboratorioList.remove(value));
			    	}
		    	}

		    	if(laboratorioList.isEmpty()) {
		    		//Deu certo
		    		map.put("message", "Cadastrado com sucesso");
		    		status = HttpStatus.CREATED;
			    	jsonString = resp.toJson(map);
		    	}else {
		    		//retorna o que nao deu certo
		    		lista.put("labs", laboratorioList);
		    		map.put("message", "Segue a lista de laboratorios nao cadastrados");
		    		list.add(map);
			    	list.add(lista);
			    	jsonString = resp.toJson(list);
			    	status = HttpStatus.BAD_REQUEST;
		    		
		    	}
		    }else {
		    	//Nao cadastrou nada retorna lista de laboratorios completa
		    	lista.put("labs", laboratorioList);
	    		map.put("message", "Segue a lista de laboratorios nao cadastrados");
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
	
	@PutMapping("/laboratorios/lotes")
	public ResponseEntity updateLaboratorioList(@RequestBody String json) {
		
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, List> lista = new HashMap<>();
		
		Gson resp = new Gson();
		
		String jsonString = "";
		
		Gson gson = new Gson();
	    Type type = new TypeToken<List<Laboratorio>>(){}.getType();
	    boolean state = false, state2 = false, exists = false;
	    
	    status = HttpStatus.BAD_REQUEST;
	    
	    try {
	    	
	    	List<Laboratorio> laboratorioList = gson.fromJson(json, type);
	    	
	    	ArrayList<HashMap> list = new ArrayList<>();
	    	
			for (Laboratorio lab : laboratorioList) {
				
		    	if(!Stream.of(lab.getNumero()).anyMatch(Objects::isNull)) {
		    		state = Stream.of(lab.getNome(),
			    			lab.getRua(), lab.getBairro(),
			    			lab.getCidade(), lab.getUf(), lab.getCep()).
		    				anyMatch(s -> (s == null || ((String) s).trim().equals("")));
		    		state2 = Stream.of(lab.getNumero(), lab.getStatus(),
		    				lab.getId()).anyMatch(Objects::isNull);
		    		
			    	if(state || state2) {
			    		
			    		break;
			    	}
		    	}else {
		    		state = false;
		    		break;
		    	}
			}
			
			if(state || state2) {
		    	map.put("message", "Parametros inválidos");
		    	jsonString = resp.toJson(map);
		    	status = HttpStatus.BAD_REQUEST;
		    	return new ResponseEntity<>(jsonString,status);
		    }
			Optional<Laboratorio> lab;
			ExampleMatcher modelMatcher = ExampleMatcher.matching().withIgnorePaths("id", "status");
			Example<Laboratorio> example;
			Optional<Laboratorio> result;
			ArrayList<Integer> listIndex = new ArrayList<>();
			//Valido um por um
			int cont = 0;
			for(Laboratorio laboratorio : laboratorioList) {
				
				 lab = laboratorioRepository.findById(laboratorio.getId());
				 
				 if(lab.isPresent()){
					 
					 example = Example.of(laboratorio, modelMatcher);
					
					 result = laboratorioRepository.findOne(example);
					//Testo se retornou
					
					if(result.isPresent()) {
						//Se conseguiu
						if(result.get().getId() == laboratorio.getId()) {
							laboratorioRepository.save(laboratorio);
							listIndex.add(cont);
							
						}
						
					}else {
						//Se conseguiu
						laboratorioRepository.save(laboratorio);
						listIndex.add(cont);
					}
				}
				 cont+= 1;
			}
			
			/*Vejo se falhou algum devido a duplicidade
		      Limpo lista de laboratorios*/
		    if(!listIndex.isEmpty()) {
		    	int value;
		    	
		    	if(listIndex.size() == laboratorioList.size()) {
		    		laboratorioList.clear();
		    	}else {
		    		//Limpo para retornar os que falharam
			    	for(Integer y : listIndex) {
			    		value = y;
			    		laboratorioList.remove(value);
			    	}
		    	}

		    	if(laboratorioList.isEmpty()) {
		    		//Deu certo
		    		status = HttpStatus.OK;
					map.put("message", "Atualizado com sucesso");
			    	jsonString = resp.toJson(map);
		    	}else {
		    		//retorna o que nao deu certo
		    		lista.put("labs", laboratorioList);
		    		map.put("message", "Segue a lista de laboratorios nao atualizados");
		    		list.add(map);
			    	list.add(lista);
			    	jsonString = resp.toJson(list);
			    	status = HttpStatus.BAD_REQUEST;
		    		
		    	}
		    }else {
		    	//Nao cadastrou nada retorna lista de laboratorios completa
		    	lista.put("labs", laboratorioList);
	    		map.put("message", "Segue a lista de laboratorios nao atualizados");
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
	
	@DeleteMapping("/laboratorios/lotes")
	public ResponseEntity deleteLaboratorioList(@RequestBody String json) {
		
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, List> lista = new HashMap<>();
		
		Gson resp = new Gson();
		
		String jsonString = "";
		
		Gson gson = new Gson();
		
	    Type type = new TypeToken<List<Laboratorio>>(){}.getType();
	    boolean state = false, exists = false;
	    
	    status = HttpStatus.BAD_REQUEST;
	    
	    try {
	    	
	    	List<Laboratorio> laboratorioList = gson.fromJson(json, type);
	    	
	    	ArrayList<HashMap> list = new ArrayList<>();
	    	
			for (Laboratorio lab : laboratorioList) {
				
		    	if(Stream.of(lab.getId()).anyMatch(Objects::isNull)) {
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
			
			Optional<Laboratorio> lab;
			
			ArrayList<Integer> listIndex = new ArrayList<>();
			//Valido um por um
			int cont = 0;
			for(Laboratorio laboratorio : laboratorioList) {
				
				 lab = laboratorioRepository.findById(laboratorio.getId());
				//Testo se retornou
				 if(lab.isPresent()){
					 
					 lab.get().setStatus(false);
					 laboratorioRepository.save(lab.get());
					 listIndex.add(cont);
					
				}
				 cont+= 1;
			}
			
			/*Vejo se falhou algum devido a nao existir o id
		      Limpo lista de laboratorios*/
		    if(!listIndex.isEmpty()) {
		    	int value;
		    	
		    	if(listIndex.size() == laboratorioList.size()) {
		    		laboratorioList.clear();
		    	}else {
		    		//Limpo para retornar os que falharam
			    	for(Integer y : listIndex) {
			    		value = y;
			    		laboratorioList.remove(value);
			    	}
		    	}

		    	if(laboratorioList.isEmpty()) {
		    		//Deu certo
		    		status = HttpStatus.OK;
					map.put("message", "Deletado com sucesso");
			    	jsonString = resp.toJson(map);
		    	}else {
		    		//retorna o que nao deu certo
		    		lista.put("labs", laboratorioList);
		    		map.put("message", "Segue a lista de laboratorios nao deletados");
		    		list.add(map);
			    	list.add(lista);
			    	jsonString = resp.toJson(list);
			    	status = HttpStatus.BAD_REQUEST;
		    		
		    	}
		    }else {
		    	//Nao cadastrou nada retorna lista de laboratorios completa
		    	lista.put("labs", laboratorioList);
	    		map.put("message", "Segue a lista de laboratorios nao deletados");
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

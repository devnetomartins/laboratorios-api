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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@PostMapping("/laboratorios/lotes")
	public String createLaboratorioList(@RequestBody String json) {
		
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
		    	//rua":"Rua do campo","numero": 10, "bairro": "Paraiso","cidade":"Jacobina", "uf": "BA","cep" : "040404040"
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
		    	return jsonString;
		    }
		    
		    // valido um por um para saber se nao existe cadastro com os parametros
		    
		    ArrayList<Integer> listIndex = new ArrayList<>();
		    
		    ExampleMatcher modelFind = ExampleMatcher.matching().withIgnorePaths("id");
		    
		    for (Laboratorio lab : laboratorioList) {
		    	Example<Laboratorio> example = Example.of(lab, modelFind);
		    	exists = laboratorioRepository.exists(example);
		    	if(!exists) {
		    		lab.setStatus(true);
					laboratorioRepository.save(lab);
					listIndex.add(laboratorioList.indexOf(lab));
				}
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
			    	jsonString = resp.toJson(map);
		    	}else {
		    		//retorna o que nao deu certo
		    		lista.put("labs", laboratorioList);
		    		map.put("message", "Segue a lista de laboratorios nao cadastrados");
		    		list.add(map);
			    	list.add(lista);
			    	jsonString = resp.toJson(list);
		    		
		    	}
		    }else {
		    	//Nao cadastrou nada retorna lista de laboratorios completa
		    	lista.put("labs", laboratorioList);
	    		map.put("message", "Segue a lista de laboratorios nao cadastrados");
	    		list.add(map);
		    	list.add(lista);
		    	jsonString = resp.toJson(list);
		    }
		    
		    
			
			
	    }catch (JsonParseException e) {
	    	//Json nao esta no formato solicitado
	    	map.put("message", "Request não esta no formato correto");
	    	jsonString = resp.toJson(map);
	    }
	    
		return jsonString;
	}
	
	@DeleteMapping("/laboratorios")
	public HashMap<String, String> deleteLaboratorio(@RequestBody Laboratorio laboratorio) {
		
		HashMap<String, String> map = new HashMap<>();
		
		boolean state = Stream.of(laboratorio.getId())
        .anyMatch(Objects::isNull);
		
		if(!state) {
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
			
		}else {
			//Nao atualiza
		    map.put("message", "É preciso informar o id para desativar o laboratorio");
			
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

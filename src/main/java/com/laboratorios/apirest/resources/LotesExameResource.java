package com.laboratorios.apirest.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laboratorios.apirest.repository.ExameRepository;
import com.laboratorios.apirest.repository.LaboratorioRepository;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.laboratorios.apirest.models.Laboratorio;

@RestController
//Define a rota
@RequestMapping(value="/api")
public class LotesExameResource {
	
	@Autowired
	ExameRepository exameRepository;
	
	@PostMapping("/exames/lotes")
	public String createExamesList(@RequestBody String json) {
		
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, List> lista = new HashMap<>();
		
		Gson resp = new Gson();
		
		String jsonString = "";
		
		Gson gson = new Gson();
	    Type type = new TypeToken<List<Laboratorio>>(){}.getType();
	    boolean state = false, exists = false;
	    
	    try {
			
	    }catch (JsonParseException e) {
	    	//Json nao esta no formato solicitado
	    	map.put("message", "Request não esta no formato correto");
	    	jsonString = resp.toJson(map);
	    }
	    
		return jsonString;
	}
	

}

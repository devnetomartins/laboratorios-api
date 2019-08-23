package com.laboratorios.apirest.resources;

import java.util.HashMap;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laboratorios.apirest.repository.AssociacaoRepository;
import com.laboratorios.apirest.repository.ExameRepository;
import com.laboratorios.apirest.repository.LaboratorioRepository;
import com.laboratorios.apirest.models.Associacao;
import com.laboratorios.apirest.models.Exame;
import com.laboratorios.apirest.models.Laboratorio;

@RestController
//Define a rota
@RequestMapping(value="/api")
public class AssociacaoResource {
	
	HttpStatus status;
	
	@Autowired
	ExameRepository exameRepository;
	@Autowired
	AssociacaoRepository associacaoRepository;
	@Autowired
	LaboratorioRepository laboratorioRepository;
	
	@PostMapping("/exames/associacao")
	public ResponseEntity associaExame(@RequestBody @Valid Associacao associacao) {
		
		HashMap<String, String> map = new HashMap<>();
			
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
						status = HttpStatus.CREATED;
						map.put("message", "Cadastrado com sucesso");
					}else {
						map.put("message", "Associacao ja cadastrada");
						status = HttpStatus.BAD_REQUEST;
					}
				}else {
					map.put("message", "Exame ou Laboratorio desativado");
					status = HttpStatus.BAD_REQUEST;
				}
				
			}else {
				map.put("message", "ID do laboratorio não encontrado");
				status = HttpStatus.NOT_FOUND;
			}
			
		}else {
			map.put("message", "ID do exame não encontrado");
			status = HttpStatus.NOT_FOUND;
		}
			
		
		
		return new ResponseEntity<>(map,status);
		
	}
	
	@DeleteMapping("/exames/associacao")
	public ResponseEntity desassociaExame(@RequestBody @Valid Associacao associacao) {
		
		HashMap<String, String> map = new HashMap<>();
		
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
					status = HttpStatus.BAD_REQUEST;
				}
				
			}else {
				map.put("message", "ID do laboratorio não encontrado");
				status = HttpStatus.NOT_FOUND;
			}
			
		}else {
			map.put("message", "ID do exame não encontrado");
			status = HttpStatus.NOT_FOUND;
		}
		
		return new ResponseEntity<>(map,status);
		
	}

}


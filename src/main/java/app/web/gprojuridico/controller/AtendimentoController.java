package app.web.gprojuridico.controller;

import app.web.gprojuridico.model.Atendimento;
import app.web.gprojuridico.service.AtendimentoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/atendimentos")
public class AtendimentoController {

    @Autowired
    private AtendimentoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void insert(@RequestBody Atendimento data)  {
        System.out.println("POST Atendimento chamado!");
        service.insert(data);
    }

    @GetMapping
    public List<Object> findAll()  {
        System.out.println("GET Atendimentos chamado!");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Object findById(@PathVariable String id)  {
        System.out.println("GET Atendimento by id chamado!");
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody Map<String, Object> data)  {
        System.out.println("PUT Atendimento chamado!");
        service.update(id, data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id)  {
        System.out.println("DELETE Atendimento chamado!");
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
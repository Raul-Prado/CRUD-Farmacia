package com.generation.farmacia.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.farmacia.model.Produto;
import com.generation.farmacia.repository.CategoriaRepository;
import com.generation.farmacia.repository.ProdutoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/produto")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProdutoController {

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	/*buscar todas as categorias*/
    @GetMapping
    public ResponseEntity<List<Produto>> getAll() {
        return ResponseEntity.ok(produtoRepository.findAll());
    }
    
    /*buscar uma categoria pelo ID */
    @GetMapping("/{id}")
    public ResponseEntity<Produto> getById(@PathVariable Long id) {
    	return produtoRepository.findById(id).map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    /*buscar uma categoria pelo preco */
    @GetMapping("/preco/{preco}")
	public ResponseEntity<List<Produto>> getByPrice(@PathVariable BigDecimal preco){
		return ResponseEntity.status(HttpStatus.OK).body(produtoRepository.findAllByPrecoLessThanEqual(preco));
	}
    
    /*criar uma nova categoria*/
    @PostMapping
    public ResponseEntity<Produto> post(@Valid @RequestBody Produto produto) {
    	// Verifica se a categoria do produto é nula
        if (produto.getCategoria() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if(categoriaRepository.existsById(produto.getCategoria().getId())) {
    		return ResponseEntity.status(HttpStatus.CREATED).body(produtoRepository.save(produto));
        }
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não existe!", null);
    }
    
    /*atualizar uma categoria existente*/
    @PutMapping
    public ResponseEntity<Produto> put(@Valid @RequestBody Produto produto) {
    	return produtoRepository.findById(produto.getId())
				.map(resposta -> ResponseEntity.status(HttpStatus.OK).body(produtoRepository.save(produto)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    /* Deletar uma categoria pelo ID */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        if(produto.isEmpty())
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        produtoRepository.deleteById(id);
    }
}

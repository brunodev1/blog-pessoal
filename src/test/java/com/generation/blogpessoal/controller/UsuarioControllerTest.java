package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", " "));
	}
	
	@Test
	@DisplayName("Cadastrar Um Usuário")
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario> (new Usuario(0L, "Paulo Antunes","paulo_antunes@email.com.br", "13465278", "https://img.redbull.com/images/c_crop,x_454,y_0,h_720,w_576/c_fill,w_400,h_540/q_auto:low,f_auto/redbullcom/2018/09/20/6cb9b120-96c3-4ea0-879e-2a6b476abb2d/pica-pau-collab-70-animadores-01"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
		assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
		assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
	
	}
	
	@Test
	@DisplayName("Não deve permitir a duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://img.redbull.com/images/c_crop,x_454,y_0,h_720,w_576/c_fill,w_400,h_540/q_auto:low,f_auto/redbullcom/2018/09/20/6cb9b120-96c3-4ea0-879e-2a6b476abb2d/pica-pau-collab-70-animadores-01"));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://img.redbull.com/images/c_crop,x_454,y_0,h_720,w_576/c_fill,w_400,h_540/q_auto:low,f_auto/redbullcom/2018/09/20/6cb9b120-96c3-4ea0-879e-2a6b476abb2d/pica-pau-collab-70-animadores-01"));
	
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario (0L, "Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "https://img.redbull.com/images/c_crop,x_454,y_0,h_720,w_576/c_fill,w_400,h_540/q_auto:low,f_auto/redbullcom/2018/09/20/6cb9b120-96c3-4ea0-879e-2a6b476abb2d/pica-pau-collab-70-animadores-01"));
	
		usuarioService.cadastrarUsuario(new Usuario (0L, "Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "https://img.redbull.com/images/c_crop,x_454,y_0,h_720,w_576/c_fill,w_400,h_540/q_auto:low,f_auto/redbullcom/2018/09/20/6cb9b120-96c3-4ea0-879e-2a6b476abb2d/pica-pau-collab-70-animadores-01"));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
					.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

				assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualizar Usuário")
	private void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Juliana Andrews", "juliana_andews@email.com.br", "juliana123", "https://img.redbull.com/images/c_crop,x_454,y_0,h_720,w_576/c_fill,w_400,h_540/q_auto:low,f_auto/redbullcom/2018/09/20/6cb9b120-96c3-4ea0-879e-2a6b476abb2d/pica-pau-collab-70-animadores-01"));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), "Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123", "https://img.redbull.com/images/c_crop,x_454,y_0,h_720,w_576/c_fill,w_400,h_540/q_auto:low,f_auto/redbullcom/2018/09/20/6cb9b120-96c3-4ea0-879e-2a6b476abb2d/pica-pau-collab-70-animadores-01");
	
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
		assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
		assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
	}
	
}

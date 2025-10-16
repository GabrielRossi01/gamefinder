package br.com.fiap.gamefinder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.gamefinder.model.Game;
import br.com.fiap.gamefinder.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/games")
@Slf4j
public class GameController {

    @Autowired
    private GameRepository gameRepository;
    
    @GetMapping
    public List<Game> index() {
        return gameRepository.findAll();
    }

    @GetMapping("{id}")
    public Game get(@PathVariable Long id) {
        log.info("buscando jogos com id " + id);
        return getGameById(id);
    }

    @GetMapping("/genres/{genreId}")
    public List<Game> getGamesByGenre(@PathVariable Long genreId) {
        log.info("buscando jogos pelo gênero com id: {}", genreId);
        return gameRepository.findGamesByGenreId(genreId);
    }

    @GetMapping("/platforms/platformId}")
    public List<Game> getGamesByPlatform(@PathVariable Long platformId) {
        log.info("buscando jogos pela plataforma com id: {}", platformId);
        return gameRepository.findGamesByPlatformId(platformId);
    }

    private Game getGameById(Long id) {
       return gameRepository
                .findById(id)
                .orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado com id " + id)
                );
    }
}

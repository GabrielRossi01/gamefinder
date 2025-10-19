package br.com.fiap.gamefinder.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = {"http://localhost:3000"})
public class GameController {

    @Autowired
    private GameRepository gameRepository;
    
    @GetMapping
    public PagedModel<EntityModel<Game>> index(@PageableDefault(size = 10, direction = Direction.DESC) Pageable pageable, PagedResourcesAssembler<Game> assembler) {
        log.info("buscando todos os jogos");
        Page<Game> page = gameRepository.findAll(pageable);
        return assembler.toModel(page, game -> EntityModel.of(game, linkTo(methodOn(GameController.class).get(game.getId())).withSelfRel().withTitle("Get this game")));
    }

    @GetMapping("{id}")
    public EntityModel<Game> get(@PathVariable Long id) {
        log.info("buscando jogo com id: {}", id);
        Game game = getGameById(id);

        return EntityModel.of(game,
                linkTo(methodOn(GameController.class).get(id)).withSelfRel().withTitle("Get this game"),
                linkTo(methodOn(GameController.class).index(null, null)).withRel("list").withTitle("List all games"));
    }

    @GetMapping("/genres/{genreId}")
    public CollectionModel<EntityModel<Game>> getGamesByGenre(@PathVariable Long genreId) {
        log.info("buscando jogos pelo gênero com id: {}", genreId);
        List<EntityModel<Game>> games = gameRepository.findGamesByGenreId(genreId).stream()
            .map(game -> EntityModel.of(game,
                linkTo(methodOn(GameController.class).get(game.getId())).withSelfRel().withTitle("Get this game"),
                linkTo(methodOn(GameController.class).getGamesByGenre(genreId)).withRel("genre-games").withTitle("List games by this genre")
            ))
            .collect(Collectors.toList());
        
        return CollectionModel.of(games,
            linkTo(methodOn(GameController.class).getGamesByGenre(genreId)).withSelfRel().withTitle("List games by genre"),
            linkTo(methodOn(GameController.class).index(null, null)).withRel("list").withTitle("List all games")
        );
    }

    @GetMapping("/platforms/{platformId}")
    public CollectionModel<EntityModel<Game>> getGamesByPlatform(@PathVariable Long platformId) {
        log.info("buscando jogos pela plataforma com id: {}", platformId);
        List<EntityModel<Game>> games = gameRepository.findGamesByPlatformId(platformId).stream()
            .map(game -> EntityModel.of(game,
                linkTo(methodOn(GameController.class).get(game.getId())).withSelfRel().withTitle("Get this game"),
                linkTo(methodOn(GameController.class).getGamesByPlatform(platformId)).withRel("platform-games").withTitle("List games by this platform")
            ))
            .collect(Collectors.toList());
        
        return CollectionModel.of(games,
            linkTo(methodOn(GameController.class).getGamesByPlatform(platformId)).withSelfRel().withTitle("List games by platform"),
            linkTo(methodOn(GameController.class).index(null, null)).withRel("list").withTitle("List all games")
        );
    }

    private Game getGameById(Long id) {
       return gameRepository
                .findById(id)
                .orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado com id " + id)
                );
    }
}

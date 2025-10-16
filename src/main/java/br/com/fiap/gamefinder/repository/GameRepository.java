package br.com.fiap.gamefinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.gamefinder.model.Game;

public interface GameRepository extends JpaRepository<Game, Long>{

    List<Game> findGamesByGenreId(Long genreId);

    List<Game> findGamesByPlatformId(Long platformId);
    
}

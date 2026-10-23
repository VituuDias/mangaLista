package br.com.vitor.mangaList.repository;

import br.com.vitor.mangaList.model.Manga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MangaRepository extends JpaRepository<Manga, Long> {
    boolean existsByTitleAndAuthor(String title, String author);

    boolean existsByTitleAndAuthorAndIdNot(String title, String author, Long id);
}

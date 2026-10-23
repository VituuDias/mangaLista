package br.com.vitor.mangaList.service;

import br.com.vitor.mangaList.exception.MangaAlreadyExistsException;
import br.com.vitor.mangaList.exception.MangaNotFoundException;
import br.com.vitor.mangaList.model.Format;
import br.com.vitor.mangaList.model.Manga;
import br.com.vitor.mangaList.model.Status;
import br.com.vitor.mangaList.repository.MangaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MangaServiceTest {

    @Mock
    private MangaRepository mangaRepository;

    @InjectMocks
    private MangaService mangaService;

    private Manga createManga(String title, String author, int lastChapterRead, int totalOfChapters) {
        Manga manga = new Manga();
        manga.setTitle(title);
        manga.setAuthor(author);
        manga.setLastChapterRead(lastChapterRead);
        manga.setTotalOfChapters(totalOfChapters);
        return manga;
    }

    // Create
    @Test
    @DisplayName("Deve salvar um mangá quando ele ainda não existe")
    void shouldSaveManga() {
        Manga manga = createManga("Haikyu", "Haruichi Furudate", 0, 402);

        when(mangaRepository.existsByTitleAndAuthor(manga.getTitle(), manga.getAuthor())).thenReturn(false);
        when(mangaRepository.save(manga)).thenReturn(manga);

        Manga result = mangaService.saveManga(manga);

        assertSame(manga, result);
        verify(mangaRepository).save(manga);
    }

    @Test
    @DisplayName("Deve lançar exceção e não salvar quando título e autor já existem")
    void shouldThrowExceptionWhenMangaAlreadyExists() {
        Manga manga = createManga("TestTitle", "TestAuthor", 0, 10);

        when(mangaRepository.existsByTitleAndAuthor(manga.getTitle(), manga.getAuthor())).thenReturn(true);

        MangaAlreadyExistsException exception = assertThrows(MangaAlreadyExistsException.class,
                () -> mangaService.saveManga(manga));

        verify(mangaRepository, never()).save(any(Manga.class));
        assertEquals("Essa obra já existe", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"11, 10", "-1, 10", "0, -1"})
    @DisplayName("Deve lançar exceção e não salvar quando os capítulos são inválidos")
    void shouldThrowExceptionAndNotSaveWhenChaptersAreInvalid(int lastChapterRead, int totalOfChapters) {
        Manga manga = createManga("Haikyu", "Haruichi Furudate", lastChapterRead, totalOfChapters);

        when(mangaRepository.existsByTitleAndAuthor(manga.getTitle(), manga.getAuthor())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> mangaService.saveManga(manga));

        verify(mangaRepository, never()).save(any(Manga.class));
    }

    // Read by id
    @Test
    @DisplayName("Deve buscar um mangá por id")
    void shouldFindMangaById() {
        Long id = 1L;
        Manga manga = createManga("One Piece", "Eiichiro Oda", 1, 1100);

        when(mangaRepository.findById(id)).thenReturn(Optional.of(manga));

        Manga result = mangaService.findById(id);

        assertSame(manga, result);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o mangá não é encontrado por id")
    void shouldThrowExceptionWhenMangaNotFoundById() {
        Long id = 999L;

        when(mangaRepository.findById(id)).thenReturn(Optional.empty());

        MangaNotFoundException exception = assertThrows(MangaNotFoundException.class,
                () -> mangaService.findById(id));

        assertEquals("Obra não encontrada", exception.getMessage());
    }

    // Read all
    @Test
    @DisplayName("Deve listar todos os mangás")
    void shouldListAllMangas() {
        Manga onePiece = createManga("One Piece", "Eiichiro Oda", 1, 1100);
        Manga naruto = createManga("Naruto", "Masashi Kishimoto", 1, 700);
        List<Manga> mangas = List.of(onePiece, naruto);

        when(mangaRepository.findAll()).thenReturn(mangas);

        List<Manga> result = mangaService.findAll();

        assertEquals(2, result.size());
        assertSame(mangas, result);
    }

    // Delete
    @Test
    @DisplayName("Deve excluir o mangá quando ele existe")
    void shouldDeleteMangaById() {
        Long id = 1L;

        when(mangaRepository.existsById(id)).thenReturn(true);

        mangaService.deleteById(id);

        verify(mangaRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção e não excluir quando o mangá não existe")
    void shouldThrowExceptionWhenDeletingNonExistentManga() {
        Long id = 999L;

        when(mangaRepository.existsById(id)).thenReturn(false);

        MangaNotFoundException exception = assertThrows(MangaNotFoundException.class,
                () -> mangaService.deleteById(id));

        verify(mangaRepository, never()).deleteById(any());
        assertEquals("Obra não encontrada", exception.getMessage());
    }

    // Update
    @Test
    @DisplayName("Deve atualizar todos os campos do mangá e salvar")
    void shouldUpdateMangaAndSave() {
        Long id = 1L;

        Manga oldManga = createManga("One Piece", "Eiichiro Oda", 1050, 1100);
        oldManga.setFormat(Format.MANGA);
        oldManga.setStatus(Status.LENDO);
        oldManga.setUrlImage("https://example.com/onepiece.jpg");
        oldManga.setUrlSite("https://example.com/one-piece");

        Manga updatedManga = createManga("Naruto", "Masashi Kishimoto", 700, 700);
        updatedManga.setFormat(Format.MANGA);
        updatedManga.setStatus(Status.COMPLETO);
        updatedManga.setUrlImage("https://example.com/naruto.jpg");
        updatedManga.setUrlSite("https://example.com/naruto");

        when(mangaRepository.findById(id)).thenReturn(Optional.of(oldManga));
        when(mangaRepository.save(oldManga)).thenReturn(oldManga);

        Manga result = mangaService.updateManga(id, updatedManga);

        assertEquals("Naruto", result.getTitle());
        assertEquals("Masashi Kishimoto", result.getAuthor());
        assertEquals(700, result.getLastChapterRead());
        assertEquals(700, result.getTotalOfChapters());
        assertEquals(Format.MANGA, result.getFormat());
        assertEquals(Status.COMPLETO, result.getStatus());
        assertEquals("https://example.com/naruto.jpg", result.getUrlImage());
        assertEquals("https://example.com/naruto", result.getUrlSite());
        verify(mangaRepository).save(oldManga);
    }

    @Test
    @DisplayName("Deve ignorar a própria obra ao checar duplicidade na atualização")
    void shouldIgnoreItselfWhenCheckingDuplicateOnUpdate() {
        Long id = 1L;
        Manga existing = createManga("One Piece", "Eiichiro Oda", 10, 1100);
        Manga updated = createManga("One Piece", "Eiichiro Oda", 20, 1100);

        when(mangaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(mangaRepository.existsByTitleAndAuthorAndIdNot("One Piece", "Eiichiro Oda", id)).thenReturn(false);
        when(mangaRepository.save(existing)).thenReturn(existing);

        Manga result = mangaService.updateManga(id, updated);

        assertEquals(20, result.getLastChapterRead());
        verify(mangaRepository).existsByTitleAndAuthorAndIdNot("One Piece", "Eiichiro Oda", id);
        verify(mangaRepository).save(existing);
    }

    @Test
    @DisplayName("Deve lançar exceção e não salvar ao atualizar para título e autor de outra obra")
    void shouldThrowExceptionWhenUpdatingToTitleAndAuthorOfAnotherManga() {
        Long id = 1L;
        Manga existing = createManga("One Piece", "Eiichiro Oda", 10, 1100);
        Manga updated = createManga("Naruto", "Masashi Kishimoto", 10, 700);

        when(mangaRepository.findById(id)).thenReturn(Optional.of(existing));
        when(mangaRepository.existsByTitleAndAuthorAndIdNot("Naruto", "Masashi Kishimoto", id)).thenReturn(true);

        MangaAlreadyExistsException exception = assertThrows(MangaAlreadyExistsException.class,
                () -> mangaService.updateManga(id, updated));

        verify(mangaRepository, never()).save(any(Manga.class));
        assertEquals("Essa obra já existe", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"11, 10", "-1, 10", "0, -1"})
    @DisplayName("Deve lançar exceção e não salvar ao atualizar com capítulos inválidos")
    void shouldThrowExceptionAndNotSaveWhenUpdatingWithInvalidChapters(int lastChapterRead, int totalOfChapters) {
        Long id = 1L;
        Manga existing = createManga("One Piece", "Eiichiro Oda", 10, 1100);
        Manga updated = createManga("One Piece", "Eiichiro Oda", lastChapterRead, totalOfChapters);

        when(mangaRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> mangaService.updateManga(id, updated));

        verify(mangaRepository, never()).save(any(Manga.class));
    }

    @Test
    @DisplayName("Deve lançar exceção e não salvar ao atualizar um mangá inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentManga() {
        Long id = 999L;
        Manga updatedManga = createManga("Naruto", "Masashi Kishimoto", 700, 700);

        when(mangaRepository.findById(id)).thenReturn(Optional.empty());

        MangaNotFoundException exception = assertThrows(MangaNotFoundException.class,
                () -> mangaService.updateManga(id, updatedManga));

        verify(mangaRepository, never()).save(any(Manga.class));
        assertEquals("Obra não encontrada", exception.getMessage());
    }

    // markChapterAsRead
    @Test
    @DisplayName("Deve marcar o capítulo como lido e salvar o mangá")
    void shouldMarkChapterAsReadAndSave() {
        Long id = 1L;
        Manga manga = createManga("One Piece", "Eiichiro Oda", 1, 999);

        when(mangaRepository.findById(id)).thenReturn(Optional.of(manga));
        when(mangaRepository.save(manga)).thenReturn(manga);

        Manga result = mangaService.markChapterAsRead(id, 150);

        assertEquals(150, result.getLastChapterRead());
        verify(mangaRepository).save(manga);
    }

    @Test
    @DisplayName("Deve lançar exceção e não salvar ao marcar um capítulo inválido")
    void shouldThrowExceptionAndNotSaveWhenMarkingInvalidChapter() {
        Long id = 1L;
        Manga manga = createManga("One Piece", "Eiichiro Oda", 1, 10);

        when(mangaRepository.findById(id)).thenReturn(Optional.of(manga));

        assertThrows(IllegalArgumentException.class, () -> mangaService.markChapterAsRead(id, 11));

        verify(mangaRepository, never()).save(any(Manga.class));
    }

    @Test
    @DisplayName("Deve lançar exceção e não salvar ao marcar capítulo de um mangá inexistente")
    void shouldThrowExceptionWhenMarkingChapterOnNonExistentManga() {
        Long id = 999L;

        when(mangaRepository.findById(id)).thenReturn(Optional.empty());

        MangaNotFoundException exception = assertThrows(MangaNotFoundException.class,
                () -> mangaService.markChapterAsRead(id, 2));

        verify(mangaRepository, never()).save(any(Manga.class));
        assertEquals("Obra não encontrada", exception.getMessage());
    }

    // calculateProgress
    @Test
    @DisplayName("Deve retornar o progresso de leitura do mangá")
    void shouldCalculateProgress() {
        Long id = 1L;
        Manga manga = createManga("One Piece", "Eiichiro Oda", 25, 100);

        when(mangaRepository.findById(id)).thenReturn(Optional.of(manga));

        double result = mangaService.calculateProgress(id);

        assertEquals(25.0, result, 0.01);
    }

    @Test
    @DisplayName("Deve lançar exceção ao calcular o progresso de um mangá inexistente")
    void shouldThrowExceptionWhenCalculatingProgressOfNonExistentManga() {
        Long id = 999L;

        when(mangaRepository.findById(id)).thenReturn(Optional.empty());

        MangaNotFoundException exception = assertThrows(MangaNotFoundException.class,
                () -> mangaService.calculateProgress(id));

        assertEquals("Obra não encontrada", exception.getMessage());
    }

    // isUpToDate
    @Test
    @DisplayName("Deve retornar se o mangá está em dia")
    void shouldCheckIfMangaIsUpToDate() {
        Long id = 1L;
        Manga manga = createManga("One Piece", "Eiichiro Oda", 10, 10);

        when(mangaRepository.findById(id)).thenReturn(Optional.of(manga));

        boolean result = mangaService.isUpToDate(id);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar se um mangá inexistente está em dia")
    void shouldThrowExceptionWhenCheckingUpToDateOfNonExistentManga() {
        Long id = 999L;

        when(mangaRepository.findById(id)).thenReturn(Optional.empty());

        MangaNotFoundException exception = assertThrows(MangaNotFoundException.class,
                () -> mangaService.isUpToDate(id));

        assertEquals("Obra não encontrada", exception.getMessage());
    }
}
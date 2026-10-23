package br.com.vitor.mangaList.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MangaTest {

    private Manga createManga(Integer lastChapterRead, Integer totalOfChapters) {
        Manga manga = new Manga();
        manga.setTitle("One Piece");
        manga.setLastChapterRead(lastChapterRead);
        manga.setTotalOfChapters(totalOfChapters);
        return manga;
    }

    // Builds a manga with lastChapterRead == null, which the setter does not allow
    private Manga createMangaWithNullLastChapterRead(Integer totalOfChapters) {
        return new Manga(null, null, "One Piece", "Eiichiro Oda", null, totalOfChapters, null, null, null);
    }

    // Defaults and setters
    @Test
    @DisplayName("Deve iniciar com o último capítulo lido igual a 0")
    void shouldStartWithLastChapterReadAsZero() {
        assertEquals(0, new Manga().getLastChapterRead());
    }

    @Test
    @DisplayName("Deve converter o último capítulo lido nulo para 0")
    void shouldConvertNullLastChapterReadToZero() {
        Manga manga = createManga(5, 10);

        manga.setLastChapterRead(null);

        assertEquals(0, manga.getLastChapterRead());
    }

    // markChapterAsRead
    @ParameterizedTest
    @ValueSource(ints = {-1, 11, 999})
    @DisplayName("Deve lançar exceção quando o capítulo é negativo ou maior que o total")
    void shouldThrowExceptionForInvalidChapter(int chapter) {
        Manga manga = createManga(1, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> manga.markChapterAsRead(chapter));

        assertEquals("Capítulo inválido!", exception.getMessage());
        assertEquals(1, manga.getLastChapterRead());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    @DisplayName("Deve aceitar capítulos dentro dos limites, incluindo as fronteiras")
    void shouldAcceptChapterWithinLimits(int chapter) {
        Manga manga = createManga(5, 10);

        manga.markChapterAsRead(chapter);

        assertEquals(chapter, manga.getLastChapterRead());
    }

    @Test
    @DisplayName("Deve aceitar qualquer capítulo positivo quando o total é desconhecido")
    void shouldAcceptAnyChapterWhenTotalIsUnknown() {
        Manga manga = createManga(1, null);

        manga.markChapterAsRead(500);

        assertEquals(500, manga.getLastChapterRead());
    }

    @Test
    @DisplayName("Deve rejeitar capítulo negativo mesmo quando o total é desconhecido")
    void shouldRejectNegativeChapterWhenTotalIsUnknown() {
        Manga manga = createManga(1, null);

        assertThrows(IllegalArgumentException.class, () -> manga.markChapterAsRead(-1));

        assertEquals(1, manga.getLastChapterRead());
    }

    // calculateProgress
    @Test
    @DisplayName("Deve calcular o percentual de leitura")
    void shouldCalculateProgress() {
        assertEquals(25.0, createManga(25, 100).calculateProgress(), 0.01);
    }

    @Test
    @DisplayName("Deve calcular percentual com divisão não exata sem truncar")
    void shouldCalculateProgressWithNonExactDivision() {
        assertEquals(33.33, createManga(1, 3).calculateProgress(), 0.01);
    }

    @Test
    @DisplayName("Deve retornar 100 quando tudo foi lido")
    void shouldReturn100WhenEverythingWasRead() {
        assertEquals(100.0, createManga(10, 10).calculateProgress(), 0.01);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -5})
    @DisplayName("Deve retornar 0 quando o total de capítulos é zero ou negativo")
    void shouldReturnZeroWhenTotalIsNotPositive(int total) {
        assertEquals(0.0, createManga(0, total).calculateProgress(), 0.01);
    }

    @Test
    @DisplayName("Deve retornar 0 quando o total de capítulos é desconhecido")
    void shouldReturnZeroWhenTotalIsUnknown() {
        assertEquals(0.0, createManga(5, null).calculateProgress(), 0.01);
    }

    @Test
    @DisplayName("Deve retornar 0 quando o último capítulo lido é nulo")
    void shouldReturnZeroWhenLastChapterReadIsNull() {
        assertEquals(0.0, createMangaWithNullLastChapterRead(10).calculateProgress(), 0.01);
    }

    // isUpToDate
    @Test
    @DisplayName("Deve estar em dia quando leu todos os capítulos")
    void shouldBeUpToDateWhenAllChaptersWereRead() {
        assertTrue(createManga(10, 10).isUpToDate());
    }

    @Test
    @DisplayName("Deve estar em dia quando o último lido passa do total")
    void shouldBeUpToDateWhenLastReadIsGreaterThanTotal() {
        assertTrue(createManga(11, 10).isUpToDate());
    }

    @Test
    @DisplayName("Não deve estar em dia quando falta um capítulo")
    void shouldNotBeUpToDateWhenOneChapterIsMissing() {
        assertFalse(createManga(9, 10).isUpToDate());
    }

    @Test
    @DisplayName("Não deve estar em dia quando o total de capítulos é desconhecido")
    void shouldNotBeUpToDateWhenTotalIsUnknown() {
        assertFalse(createManga(10, null).isUpToDate());
    }

    @Test
    @DisplayName("Não deve estar em dia quando o último capítulo lido é nulo")
    void shouldNotBeUpToDateWhenLastChapterReadIsNull() {
        assertFalse(createMangaWithNullLastChapterRead(10).isUpToDate());
    }

    // validateChapters
    @ParameterizedTest
    @CsvSource({"0, 10", "5, 10", "10, 10"})
    @DisplayName("Não deve lançar exceção quando o lido está entre 0 e o total, incluindo as fronteiras")
    void shouldNotThrowWhenChaptersAreValid(int lastChapterRead, int totalOfChapters) {
        Manga manga = createManga(lastChapterRead, totalOfChapters);

        assertDoesNotThrow(manga::validateChapters);
    }

    @Test
    @DisplayName("Não deve lançar exceção quando o total é desconhecido")
    void shouldNotThrowWhenTotalIsUnknown() {
        assertDoesNotThrow(createManga(500, null)::validateChapters);
    }

    @Test
    @DisplayName("Não deve lançar exceção quando o último capítulo lido é nulo")
    void shouldNotThrowWhenLastChapterReadIsNull() {
        assertDoesNotThrow(createMangaWithNullLastChapterRead(10)::validateChapters);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o último capítulo lido é negativo")
    void shouldThrowWhenLastChapterReadIsNegative() {
        Manga manga = createManga(-1, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                manga::validateChapters);

        assertEquals("O último capítulo lido não pode ser negativo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o total de capítulos é negativo")
    void shouldThrowWhenTotalOfChaptersIsNegative() {
        Manga manga = createManga(0, -1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                manga::validateChapters);

        assertEquals("O total de capítulos não pode ser negativo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o último lido é maior que o total")
    void shouldThrowWhenLastChapterReadIsGreaterThanTotal() {
        Manga manga = createManga(11, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                manga::validateChapters);

        assertEquals("O último capítulo lido não pode ser maior que o total de capítulos", exception.getMessage());
    }
}
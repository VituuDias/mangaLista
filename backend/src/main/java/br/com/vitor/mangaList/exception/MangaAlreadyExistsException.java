package br.com.vitor.mangaList.exception;

public class MangaAlreadyExistsException extends RuntimeException {
    public MangaAlreadyExistsException(String message) {
        super(message);
    }
}

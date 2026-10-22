package br.com.vitor.mangaList.exception;

public class MangaNotFoundException extends RuntimeException{
    public MangaNotFoundException(String message) {
        super(message);
    }
}

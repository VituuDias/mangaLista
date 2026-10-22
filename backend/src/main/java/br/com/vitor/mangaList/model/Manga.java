package br.com.vitor.mangaList.model;

import jakarta.persistence.*;

@Entity
@Table(name="Manga")
public class Manga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String urlImage;
    private String title;
    private String author;
    private String urlSite;
    private Integer totalOfChapters;
    private Integer lastChapterRead = 0;

    @Enumerated(EnumType.STRING)
    private Format format;
    @Enumerated(EnumType.STRING)
    private Status status;

    public Manga() {
    }

    public Manga(Long id, String urlImage, String title, String author, String urlSite, Integer totalOfChapters, Integer lastChapterRead, Format format, Status status) {
        this.id = id;
        this.urlImage = urlImage;
        this.title = title;
        this.author = author;
        this.urlSite = urlSite;
        this.totalOfChapters = totalOfChapters;
        this.lastChapterRead = lastChapterRead;
        this.format = format;
        this.status = status;
    }

    public void markChapterAsRead(int chapter) {
        if (chapter < 0 || (totalOfChapters != null && chapter > totalOfChapters)) {
            throw new IllegalArgumentException("Capítulo inválido!");
        }
        this.lastChapterRead = chapter;
    }

    public double calculateProgress(){
        if(totalOfChapters == null || totalOfChapters <=0){
            return 0;
        }

        int chapterRead = lastChapterRead == null ? 0: lastChapterRead;
        return ((double) chapterRead / totalOfChapters) * 100;
    }

    public boolean isUpToDate(){
        return totalOfChapters != null
                && (lastChapterRead == null ? 0 : lastChapterRead) >= totalOfChapters;
    }

    public Long getId() {
        return id;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrlSite() {
        return urlSite;
    }

    public void setUrlSite(String urlSite) {
        this.urlSite = urlSite;
    }

    public Integer getTotalOfChapters() {
        return totalOfChapters;
    }

    public void setTotalOfChapters(Integer totalOfChapters) {
        this.totalOfChapters = totalOfChapters;
    }

    public Integer getLastChapterRead() {
        return lastChapterRead;
    }

    public void setLastChapterRead(Integer lastChapterRead) {
        this.lastChapterRead = lastChapterRead;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

package br.com.vitor.mangaList.service;

import br.com.vitor.mangaList.exception.MangaAlreadyExistsException;
import br.com.vitor.mangaList.exception.MangaNotFoundException;
import br.com.vitor.mangaList.model.Manga;
import br.com.vitor.mangaList.repository.MangaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MangaService {
    private final MangaRepository mangaRepository;

    public MangaService(MangaRepository mangaRepository) {
        this.mangaRepository = mangaRepository;
    }

    public Manga saveManga(Manga manga){
        if(mangaRepository.existsByTitleAndAuthor(manga.getTitle(), manga.getAuthor())){
            throw new MangaAlreadyExistsException("Essa obra já existe");
        }
        manga.validateChapters();
        return mangaRepository.save(manga);
    }

    public List<Manga> findAll(){
        return mangaRepository.findAll();
    }

    public Manga findById(Long id){
        return mangaRepository.findById(id).orElseThrow(() -> new MangaNotFoundException("Obra não encontrada"));
    }


    public Manga updateManga(Long id, Manga manga){
        Manga mangaNew = findById(id);

        mangaNew.setTitle(manga.getTitle());
        mangaNew.setAuthor(manga.getAuthor());
        mangaNew.setLastChapterRead(manga.getLastChapterRead());
        mangaNew.setTotalOfChapters(manga.getTotalOfChapters());
        mangaNew.setFormat(manga.getFormat());
        mangaNew.setStatus(manga.getStatus());
        mangaNew.setUrlImage(manga.getUrlImage());
        mangaNew.setUrlSite(manga.getUrlSite());

        mangaNew.validateChapters();
        return mangaRepository.save(mangaNew);
    }

    public void deleteById(Long id){
        if (!mangaRepository.existsById(id)) {
            throw new MangaNotFoundException("Obra não encontrada");
        }
        mangaRepository.deleteById(id);

    }

    public Manga markChapterAsRead(Long id, int chapter){
        Manga manga = findById(id);
        manga.markChapterAsRead(chapter);
        return mangaRepository.save(manga);
    }

    public double calculateProgress(Long id){
        Manga manga = findById(id);
        return manga.calculateProgress();
    }

    public boolean isUpToDate(Long id){
        Manga manga = findById(id);
        return manga.isUpToDate();
    }

}

package br.com.vitor.mangaList.controller;

import br.com.vitor.mangaList.model.Manga;
import br.com.vitor.mangaList.service.MangaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manga")
public class MangaController {
    private final MangaService mangaService;

    public MangaController(MangaService mangaService) {
        this.mangaService = mangaService;
    }

    @GetMapping
    public List<Manga> findAll(){
        return mangaService.findAll();
    }

    @GetMapping("/{id}")
    public Manga findById(@PathVariable Long id){
        return mangaService.findById(id);
    }

    @PostMapping
    public Manga saveManga(@RequestBody Manga manga){
        return mangaService.saveManga(manga);
    }

    @PutMapping("/{id}")
    public Manga updateManga(@PathVariable Long id, @RequestBody Manga manga){
        return mangaService.updateManga(id, manga);
    }

    @DeleteMapping("/{id}")
    public void deleteManga(@PathVariable Long id){
        mangaService.deleteById(id);
    }

    @PutMapping("/{id}/chapter/{chapter}")
    public Manga markChapterAsRead(@PathVariable Long id, @PathVariable int chapter){
        return mangaService.markChapterAsRead(id, chapter);
    }

    @GetMapping("/{id}/progress")
    public double calculateProgress(@PathVariable Long id){
        return mangaService.calculateProgress(id);
    }

    @GetMapping("/{id}/up-to-date")
    public boolean isUpToDate(@PathVariable Long id){
        return mangaService.isUpToDate(id);
    }

}

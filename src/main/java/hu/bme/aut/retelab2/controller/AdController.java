package hu.bme.aut.retelab2.controller;

import hu.bme.aut.retelab2.secret.SecretGenerator;
import hu.bme.aut.retelab2.domain.Ad;
import hu.bme.aut.retelab2.domain.Note;
import hu.bme.aut.retelab2.repository.AdRepository;
import hu.bme.aut.retelab2.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/ads")
public class AdController {

    @Autowired
    private AdRepository adRepository;

    @GetMapping
    public List<Ad> getAll(@RequestParam(required = false, defaultValue = "0") int min,
                           @RequestParam(required = false, defaultValue = "10000000") int max) {
        List<Ad> ads = adRepository.findAll(min, max);
        for (int i = 0; i < ads.size(); i++){
            ads.get(i).setCode(null);
        }
        return ads;
    }


    @PostMapping
    public Ad create(@RequestBody Ad ad) {
        ad.setId(null);
        ad.setCode(SecretGenerator.generate());
        return adRepository.save(ad);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Ad ad) {
        try {
            return ResponseEntity.ok(adRepository.modify(ad));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("rossz titkos kod");
        }
    }

    @GetMapping("{tag}")
    public List<Ad> getByTag(@PathVariable String tag){
        List<Ad> ads =  adRepository.findByTag(tag);

        return ads;
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable long id){
        Ad ad = adRepository.findById(id);
        if (ad == null)
            return ResponseEntity.notFound().build();
        else {
            adRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
    }
}

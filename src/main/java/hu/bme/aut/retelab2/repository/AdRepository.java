package hu.bme.aut.retelab2.repository;

import hu.bme.aut.retelab2.domain.Ad;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AdRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Ad save(Ad feedback) {
        return em.merge(feedback);
    }

    public List<Ad> findAll(int min, int max) {
        return em.createQuery("SELECT n FROM Ad n WHERE ?1 <= n.price AND ?2 >= n.price", Ad.class).setParameter(1, min).setParameter(2, max).getResultList();
    }

    public Ad findById(long id) {
        return em.find(Ad.class, id);
    }

    @Transactional
    public Ad modify(Ad ad) throws Exception {
        Ad adToMod = findById(ad.getId());
        if (adToMod == null) throw new Exception("Nincs ilyen");
        if (!(adToMod.getCode().equals(ad.getCode()))) throw new Exception("rossz kod");
        return save(ad);
    }

    @Transactional
    public void deleteById(long id) {
        Ad todo = findById(id);
        em.remove(todo);
    }

    public List<Ad> findByTag(String tag){
        return em.createQuery("SELECT n FROM Ad n WHERE ?1 member of n.tags", Ad.class).setParameter(1, tag).getResultList();
    }

    @Transactional
    @Scheduled(fixedDelay = 6000)
    public void deleteAllExpiredAds(){
        List<Ad> ads = em.createQuery("SELECT n FROM Ad n").getResultList();
        ads.forEach(ad ->{
            if(ad.getExpirationDate() != null){
                if(ad.getExpirationDate().isBefore(LocalDateTime.now())){
                    deleteById(ad.getId());
                }
            }
        });
    }

}

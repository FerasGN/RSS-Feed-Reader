package de.htwsaar.pib2021.rss_feed_reader.database;

import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Component
public class MaterializedViewManager {

    @Autowired
    private EntityManager entityManager;

    @Modifying
    @Transactional
    public void refreshFeedItem(){
        this.entityManager.createNativeQuery("call feedify.public.refresh_mat_view_feed_item();");
    }

    @Modifying
    @Transactional
    public void refreshChannleView(){
        this.entityManager.createNativeQuery("call feedify.public.refresh_mat_view_channel()");
    }

    @Transactional(rollbackOn = Exception.class)
    public void creatematerializedViewFeedItemWithIndex(){

    }

    @Transactional(rollbackOn = Exception.class)
    public void createMaterializedViewChannelWithIndex(){
    }

    @Transactional
    public List<Long> fullTextSeachrFeedItem(String query){
        Query q = entityManager.createNativeQuery("SELECT id\n" +
                                                    "From feedify.public.search_index_feed_item\n" +
                                                    "WHERE document @@ plainto_tsquery('simple', ?1)\n" +
                                                    "ORDER BY ts_rank(search_index_feed_item.document, to_tsquery('simple', ?1)) DESC;");
        q.setParameter(1, query);
        return q.getResultList();
    }

    @Transactional
    public List<Long> fullTextSearchChannel(String query){
        Query q = entityManager.createNativeQuery("SELECT id\n" +
                                                    "From feedify.public.search_index_channel\n" +
                                                    "WHERE document @@ plainto_tsquery('simple', ?1)\n" +
                                                    "ORDER BY ts_rank( search_index_channel.document, to_tsquery('simple', ?1)) DESC;");
        q.setParameter(1, query);
        return q.getResultList();
    }




}

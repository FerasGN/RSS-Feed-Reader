package de.htwsaar.pib2021.rss_feed_reader.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaterializedViewManager {

    @Autowired
    private EntityManager entityManager;

    @Modifying
    @Transactional
    public void refreshFeedItem() {
        this.entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW search_index_feed_item;").executeUpdate();
    }

    @Modifying
    @Transactional
    public void refreshChannleView() {
        this.entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW search_index_channel;").executeUpdate();
    }

    @Transactional(rollbackOn = Exception.class)
    public void creatematerializedViewFeedItemWithIndex() {

    }

    @Transactional(rollbackOn = Exception.class)
    public void createMaterializedViewChannelWithIndex() {
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<Long> fullTextSeachrFeedItem(String query) {
        Query q = entityManager.createNativeQuery("SELECT id\n" 
                                                + "From feedify.public.search_index_feed_item\n"
                                                + "WHERE document @@ plainto_tsquery(?1)\n"
                                                + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery(?1)) DESC;");
        q.setParameter(1, query);
        List<Long> resultList = (List<Long>)  q.getResultList()
                                               .stream()
                                               .map(id -> ((BigInteger) id).longValue())
                                               .collect(Collectors.toList());
        return resultList;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<Long> fullTextSearchChannel(String query) {

        Query q = entityManager.createNativeQuery( "SELECT id\n" 
                                                + "From feedify.public.search_index_channel\n" 
                                                + "WHERE document @@ plainto_tsquery(?1)\n"
                                                + "ORDER BY ts_rank( search_index_channel.document, plainto_tsquery(?1)) DESC;");
        q.setParameter(1, query);
        List<Long> resultList = (List<Long>)  q.getResultList()
                                               .stream()
                                               .map(id -> ((BigInteger) id).longValue())
                                               .collect(Collectors.toList());
        return resultList;
    }

}

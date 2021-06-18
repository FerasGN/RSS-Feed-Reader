package de.htwsaar.pib2021.rss_feed_reader.database;

import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Component
public class MaterializedViewManager {
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void refreshFeedItem(){
        this.entityManager.createNativeQuery("call feedify.public.refresh_mat_view_feed_item();");
    }

    @Transactional
    public void creatematerializedViewFeedItemWithIndex(){
        this.entityManager.createNativeQuery("CREATE MATERIALIZED VIEW search_index_feed_item AS SELECT id, setweight(to_tsvector(language::regconfig, title), 'A')|| setweight(to_tsvector(language::regconfig, description), 'B')|| setweight(to_tsvector(language::regconfig, content), 'C')|| setweight(to_tsvector(language::regconfig, author), 'D') AS document From feedify.public.feed_item GROUP BY id;");
        this.entityManager.createNativeQuery("Create INDEX idx_fts_search ON feedify.public.search_index_feed_item USING gist(document);");
    }

    public List<Long> fulltextSeachrFeedItem(String query){
        Query q = entityManager.createNativeQuery("SELECT id From feedify.public.search_index_feed_item WHERE document @@ to_tsquery('simple', ?1 ) ORDER BY ts_rank(search_index_feed_item.document, to_tsquery('simple', ?1 )) DESC;");
        q.setParameter(1, query);
        return q.getResultList();
    }
    //TODO Das Gleiche für Channel wird morgen hochgeladen




}

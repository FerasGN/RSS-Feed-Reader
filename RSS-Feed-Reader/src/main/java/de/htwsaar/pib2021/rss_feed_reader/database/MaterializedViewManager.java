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
    public void refreshChannleView(){
        this.entityManager.createNativeQuery("call feedify.public.refresh_mat_view_channel()");
    }

    @Transactional
    public void creatematerializedViewFeedItemWithIndex(){
        this.entityManager.createNativeQuery("CREATE MATERIALIZED VIEW search_index_feed_item AS\n" +
                                                "    SELECT id,\n" +
                                                "            setweight(to_tsvector(language::regconfig, title), 'A')||\n" +
                                                "            setweight(to_tsvector(language::regconfig, description), 'B')||\n" +
                                                "            setweight(to_tsvector(language::regconfig, content), 'C')||\n" +
                                                "            setweight(to_tsvector(language::regconfig, author), 'D') AS document\n" +
                                                "From feedify.public.feed_item\n" +
                                                "GROUP BY id;");
        this.entityManager.createNativeQuery("Create INDEX idx_fts_search ON feedify.public.search_index_feed_item USING gist(document);");
        this.entityManager.createNativeQuery("Create FUNCTION refresh_mat_view_feed_item()\n" +
                                                "    RETURNS TRIGGER LANGUAGE plpgsql\n" +
                                                "    AS $$\n" +
                                                "    BEGIN\n" +
                                                "    REFRESH MATERIALIZED VIEW CONCURRENTLY feedify.public.search_index_feed_item;\n" +
                                                "    END $$;");
    }

    @Transactional
    public void createMaterializedViewChannelWithIndex(){
        this.entityManager.createNativeQuery("Create MATERIALIZED VIEW search_index_channel AS\n" +
                                                "    SELECT id,\n" +
                                                "            setweight(to_tsvector(language::regconfig, title), 'A')||\n" +
                                                "            setweight(to_tsvector(language::regconfig, description), 'B') AS document\n" +
                                                "From feedify.public.channel\n" +
                                                "Group BY id;");
        this.entityManager.createNativeQuery("CREATE INDEX idx_fts_search ON feedify.public.search_index_channel USING gist(document);");
        this.entityManager.createNativeQuery("Create FUNCTION refresh_mat_view_channel()\n" +
                                                "    RETURNS TRIGGER LANGUAGE plpgsql\n" +
                                                "    AS $$\n" +
                                                "    BEGIN\n" +
                                                "    REFRESH MATERIALIZED VIEW CONCURRENTLY feedify.public.search_index_channel;\n" +
                                                "    END $$;");
    }

    @Transactional
    public List<Long> fullTextSeachrFeedItem(String query){
        Query q = entityManager.createNativeQuery("SELECT id\n" +
                                                    "From feedify.public.search_index_feed_item\n" +
                                                    "WHERE document @@ to_tsquery('simple', ?1)\n" +
                                                    "ORDER BY ts_rank(search_index_feed_item.document, to_tsquery('simple', ?1)) DESC;");
        q.setParameter(1, query);
        return q.getResultList();
    }

    @Transactional
    public List<Long> fullTextSearchChannel(String query){
        Query q = entityManager.createNativeQuery("SELECT id\n" +
                                                    "From feedify.public.search_index_channel\n" +
                                                    "WHERE document @@ to_tsquery('simple', ?1)\n" +
                                                    "ORDER BY ts_rank( search_index_channel.document, to_tsquery('simple', ?1)) DESC;");
        q.setParameter(1, query);
        return q.getResultList();
    }




}

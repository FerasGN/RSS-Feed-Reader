package de.htwsaar.pib2021.rss_feed_reader.database;

import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.LanguageIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class MaterializedViewManager {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private LanguageIdentifierService languageIdentifierService;

    @Modifying
    @Transactional
    public void refreshFeedItem() {
        this.entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW feedify.public.search_index_feed_item;").executeUpdate();
    }

    @Modifying
    @Transactional
    public void refreshChannleView() {
        this.entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW feedify.public.search_index_channel;").executeUpdate();
    }

    @Transactional(rollbackOn = Exception.class)
    public void creatematerializedViewFeedItemWithIndex() {

    }

    @Transactional(rollbackOn = Exception.class)
    public void createMaterializedViewChannelWithIndex() {
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<Long> fullTextSearchFeedItem(String query) {
        String language = languageIdentifierService.searchLanguage(query);
        Query q;
        switch (language){
            case "DANISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('danish',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('danish',?1)) DESC;");
                break;
            case "DUTCH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('dutch',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('dutch',?1)) DESC;");
                break;
            case "ENGLISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('english',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('english',?1)) DESC;");
                break;
            case "FINNISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('finnish',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('finnish',?1)) DESC;");
                break;
            case "FRENCH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('french',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('french',?1)) DESC;");
                break;
            case "GERMAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('german',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('german',?1)) DESC;");
                break;
            case "HUNGARIAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('hungarian',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('hungarian',?1)) DESC;");
                break;
            case "ITALIAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('italian',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('italian',?1)) DESC;");
                break;
            case "NORWEGIAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('norwegian',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('norwegian',?1)) DESC;");
                break;
            case "PORTUGUESE":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('portuguese',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('portuguese',?1)) DESC;");
                break;
            case "ROMANIAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('romanian',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('romanian',?1)) DESC;");
                break;
            case "RUSSIAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('russian',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('russian',?1)) DESC;");
                break;
            case "SPANISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('spanish',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('spanish',?1)) DESC;");
                break;
            case "SWEDISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('swedish',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('swedish',?1)) DESC;");
                break;
            case "TURKISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('turkish',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('turkish',?1)) DESC;");
                break;
            default:
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_feed_item\n"
                        + "WHERE document @@ plainto_tsquery('simple',?1)\n"
                        + "ORDER BY ts_rank(search_index_feed_item.document, plainto_tsquery('simple',?1)) DESC;");
                break;
        }
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
        String language = languageIdentifierService.searchLanguage(query);
        Query q;
        switch (language){
            case "DANISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('danish',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('danish',?1)) DESC;");
                break;
            case "DUTCH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('dutch',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('dutch',?1)) DESC;");
                break;
            case "ENGLISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('english',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('english',?1)) DESC;");
                break;
            case "FINNISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('finnish',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('finnish',?1)) DESC;");
                break;
            case "FRENCH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('french',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('french',?1)) DESC;");
                break;
            case "GERMAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('german',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('german',?1)) DESC;");
                break;
            case "HUNGARIAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('hungarian',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('hungarian',?1)) DESC;");
                break;
            case "ITALIAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('italian',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('italian',?1)) DESC;");
                break;
            case "NORWEGIAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('norwegian',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('norwegian',?1)) DESC;");
                break;
            case "PORTUGUESE":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('portuguese',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('portuguese',?1)) DESC;");
                break;
            case "ROMANIAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('romanian',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('romanian',?1)) DESC;");
                break;
            case "RUSSIAN":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('russian',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('russian',?1)) DESC;");
                break;
            case "SPANISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('spanish',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('spanish',?1)) DESC;");
                break;
            case "SWEDISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('swedish',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('swedish',?1)) DESC;");
                break;
            case "TURKISH":
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('turkish',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('turkish',?1)) DESC;");
                break;
            default:
                q = entityManager.createNativeQuery("SELECT id\n"
                        + "From feedify.public.search_index_channel\n"
                        + "WHERE document @@ plainto_tsquery('simple',?1)\n"
                        + "ORDER BY ts_rank(search_index_channel.document, plainto_tsquery('simple',?1)) DESC;");
                break;
        }
        q.setParameter(1, query);
        List<Long> resultList = (List<Long>)  q.getResultList()
                                               .stream()
                                               .map(id -> ((BigInteger) id).longValue())
                                               .collect(Collectors.toList());
        return resultList;
    }




}

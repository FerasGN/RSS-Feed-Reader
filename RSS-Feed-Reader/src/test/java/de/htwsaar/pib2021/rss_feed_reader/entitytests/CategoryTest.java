package de.htwsaar.pib2021.rss_feed_reader.entitytests;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Category;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CategoryTest {

    @Autowired
    private CategoryRepository categoryRepo;

    @Test
    @Rollback(false)
    public void saveCategoryTest(){
        Category category = new Category();
        category.setName("Politik");
        category.setId(1L);

        category = categoryRepo.save(category);
        assertEquals(category.getId(), 1);
    }

    @Test
    public void findCategoryTest(){
        Category category = categoryRepo.findById(1L).get();
        assertEquals(category.getId(), 1L);
    }

    @Test
    public void updateCategoryTest(){
        Category category = categoryRepo.findById(1L).get();
        category.setId(5L);
        category = categoryRepo.save(category);
        assertNotEquals(category.getId(), 1L);
    }

    @Test
    public void findAllCategoryTest(){
        List<Category> categories = categoryRepo.findAll();
        assertSame(categories.size(), 1);
    }

}

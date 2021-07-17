package com.feeedify.entitytests;

import com.feeedify.database.entity.Category;
import com.feeedify.database.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.BeforeTransaction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CategoryTest {

    @Autowired
    private CategoryRepository categoryRepo;

    @BeforeTransaction
    public void init(){
        Category category = new Category();
        category.setName("Politik");
        category.setId(1l);
        categoryRepo.save(category);
    }

    @Test
    public void findCategoryTest(){
        Category category = categoryRepo.findById(1l).get();
        assertEquals(category.getId(), 1l);
    }

    @Test
    public void updateCategoryTest(){
        Category category = categoryRepo.findById(1l).get();
        category.setId(5l);
        category = categoryRepo.save(category);
        assertNotEquals(category.getId(), 1l);
    }

    @Test
    public void findAllCategoryTest(){
        List<Category> categories = categoryRepo.findAll();
        assertSame(categories.size(), 1);
    }

}

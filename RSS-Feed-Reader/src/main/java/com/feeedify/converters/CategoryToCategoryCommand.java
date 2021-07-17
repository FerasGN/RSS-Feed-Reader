package com.feeedify.converters;

import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.feeedify.commands.CategoryCommand;
import com.feeedify.database.entity.Category;
import com.feeedify.database.entity.User;
import com.feeedify.rest.service.FeedsService;
import lombok.Data;

@Component
@Data
public class CategoryToCategoryCommand implements Converter<Category, CategoryCommand> {

    private User user;
    private FeedsService feedService;

    public CategoryToCategoryCommand(FeedsService feedService) {
        this.feedService = feedService;
    }

    @Nullable
    @Override
    public CategoryCommand convert(Category source) {
        if (source == null) {
            return null;
        }

        final CategoryCommand categoryCOommand = new CategoryCommand();
        String categoryName = source.getName();
        categoryCOommand.setName(categoryName);
        categoryCOommand.setNumberOfUnreadFeeds(feedService.findNumberOfUnreadFeedsOfCategory(user, categoryName));

        return categoryCOommand;
    }

}

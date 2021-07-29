package com.feeedify.commands;

import lombok.Data;

@Data
public class PostCategoryCommand {

    private CategoryCommand categoryCommand;
    private String newCategoryName;
    private CurrentPageCommand currentPageCommand;

}

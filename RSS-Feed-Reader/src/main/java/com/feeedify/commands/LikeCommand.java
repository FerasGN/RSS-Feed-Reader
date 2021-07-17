package com.feeedify.commands;

import lombok.Data;

@Data
public class LikeCommand {
    
    private Long feedId;
    private boolean liked;
}

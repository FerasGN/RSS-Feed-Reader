package com.feeedify.commands;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LastReadingDateCommand {

    private Long feedId;
    private LocalDateTime lastReadingDate;
}

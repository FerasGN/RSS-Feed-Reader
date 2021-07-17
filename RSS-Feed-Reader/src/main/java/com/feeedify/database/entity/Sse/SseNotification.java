package com.feeedify.database.entity.Sse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SseNotification {

    private String username;
    private String message;
}

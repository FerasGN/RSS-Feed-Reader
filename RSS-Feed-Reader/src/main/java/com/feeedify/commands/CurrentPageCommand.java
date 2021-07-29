package com.feeedify.commands;

import lombok.Data;

@Data
public class CurrentPageCommand {

    private String currentFeedsUrl;
    private String categoryName;
    private String channelTitle;
    private String selectedView;
    private String selectedPeriod;
    private String selectedOrder;

}

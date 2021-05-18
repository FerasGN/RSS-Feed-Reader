package de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Embeddable
@EqualsAndHashCode()
public class ChannelUserId implements Serializable {

	private Long channelId;
	private Long userId;

}


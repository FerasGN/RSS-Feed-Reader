package com.feeedify.database.entity.compositeIds;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelUserId implements Serializable {

	private Long channelId;
	private Long userId;

}

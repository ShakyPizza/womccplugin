package com.womclan;

import lombok.Value;

import java.time.Instant;

@Value
public class WomGroupActivity
{
	String displayName;
	String type;
	String role;
	Instant createdAt;

	boolean isMembershipChange()
	{
		return "joined".equals(type) || "left".equals(type);
	}
}

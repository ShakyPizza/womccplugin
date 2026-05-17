package com.womclan;

import lombok.Value;

import java.time.Instant;

@Value
public class WomAchievement
{
	String displayName;
	String name;
	String metric;
	String measure;
	long threshold;
	Instant createdAt;
}

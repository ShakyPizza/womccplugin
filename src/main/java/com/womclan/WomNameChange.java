package com.womclan;

import lombok.Value;

import java.time.Instant;

@Value
public class WomNameChange
{
	String displayName;
	String oldName;
	String newName;
	String status;
	Instant resolvedAt;
	Instant createdAt;
}

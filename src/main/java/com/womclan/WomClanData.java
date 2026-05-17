package com.womclan;

import lombok.Value;

import java.util.List;

@Value
public class WomClanData
{
	List<WomMember> members;
	List<WomAchievement> achievements;
	List<WomGroupActivity> activity;
}

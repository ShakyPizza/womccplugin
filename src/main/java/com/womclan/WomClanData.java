package com.womclan;

import lombok.Value;

import java.util.List;

@Value
public class WomClanData
{
	WomClanInfo info;
	List<WomMember> members;
	List<WomAchievement> achievements;
	List<WomGroupActivity> activity;
}

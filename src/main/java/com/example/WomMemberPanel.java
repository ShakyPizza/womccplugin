package com.example;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A single row in the member list, displaying one clan member's stats.
 */
class WomMemberPanel extends JPanel
{
	private static final Color COLOR_XP = new Color(255, 200, 60);
	private static final Color COLOR_EHP = new Color(100, 220, 100);
	private static final Color COLOR_EHB = new Color(100, 180, 255);

	WomMemberPanel(WomMember member, boolean odd)
	{
		setLayout(new BorderLayout(6, 0));
		setBorder(new EmptyBorder(5, 8, 5, 8));
		setBackground(odd ? ColorScheme.DARKER_GRAY_COLOR : ColorScheme.DARK_GRAY_COLOR);

		// Player name on the left
		JLabel nameLabel = new JLabel(member.getDisplayName());
		nameLabel.setFont(FontManager.getRunescapeBoldFont());
		nameLabel.setForeground(Color.WHITE);
		add(nameLabel, BorderLayout.WEST);

		// Stats on the right
		JPanel statsPanel = new JPanel(new GridLayout(1, 3, 6, 0));
		statsPanel.setOpaque(false);
		statsPanel.add(statLabel("XP: " + formatXp(member.getTotalXp()), COLOR_XP));
		statsPanel.add(statLabel("EHP: " + formatDecimal(member.getEhp()), COLOR_EHP));
		statsPanel.add(statLabel("EHB: " + formatDecimal(member.getEhb()), COLOR_EHB));
		add(statsPanel, BorderLayout.EAST);
	}

	private JLabel statLabel(String text, Color color)
	{
		JLabel label = new JLabel(text, SwingConstants.RIGHT);
		label.setForeground(color);
		label.setFont(FontManager.getRunescapeSmallFont());
		return label;
	}

	private String formatXp(long xp)
	{
		if (xp >= 1_000_000_000L)
		{
			return String.format("%.2fB", xp / 1_000_000_000.0);
		}
		else if (xp >= 1_000_000L)
		{
			return String.format("%.1fM", xp / 1_000_000.0);
		}
		else if (xp >= 1_000L)
		{
			return String.format("%.1fK", xp / 1_000.0);
		}
		return String.valueOf(xp);
	}

	private String formatDecimal(double value)
	{
		return String.format("%.1f", value);
	}
}

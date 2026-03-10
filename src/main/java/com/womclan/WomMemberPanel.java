package com.womclan;

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
	WomMemberPanel(WomMember member, boolean odd)
	{
		setLayout(new BorderLayout(6, 0));
		setBorder(new EmptyBorder(5, 8, 5, 8));
		setBackground(odd ? ColorScheme.DARKER_GRAY_COLOR : ColorScheme.DARK_GRAY_COLOR);

		JLabel nameLabel = new JLabel(member.getDisplayName());
		nameLabel.setFont(FontManager.getRunescapeBoldFont());
		nameLabel.setForeground(Color.WHITE);
		add(nameLabel, BorderLayout.WEST);

		JLabel roleLabel = new JLabel(formatRole(member.getRole()), SwingConstants.RIGHT);
		roleLabel.setFont(FontManager.getRunescapeSmallFont());
		roleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		add(roleLabel, BorderLayout.EAST);
	}

	private String formatRole(String role)
	{
		if (role == null || role.isEmpty())
		{
			return "Member";
		}
		String[] words = role.replace('_', ' ').split(" ");
		StringBuilder sb = new StringBuilder();
		for (String word : words)
		{
			if (sb.length() > 0) sb.append(' ');
			sb.append(Character.toUpperCase(word.charAt(0)));
			sb.append(word.substring(1).toLowerCase());
		}
		return sb.toString();
	}
}

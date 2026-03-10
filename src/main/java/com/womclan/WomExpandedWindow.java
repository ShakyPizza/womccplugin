package com.womclan;

import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * A resizable standalone window showing the full clan member table
 * with search/filter and sortable columns.
 */
class WomExpandedWindow extends JFrame
{
	private final DefaultTableModel tableModel;
	private final TableRowSorter<DefaultTableModel> sorter;

	WomExpandedWindow()
	{
		super("WOM Clan Stats");
		setSize(700, 500);
		setMinimumSize(new Dimension(500, 300));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(0, 0));

		// ── Search bar ─────────────────────────────────────────────────────────
		JTextField searchField = new JTextField();
		searchField.setFont(FontManager.getRunescapeSmallFont());
		searchField.setToolTipText("Filter members by name…");

		JPanel searchWrapper = new JPanel(new BorderLayout(6, 0));
		searchWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		searchWrapper.add(new JLabel("Search:"), BorderLayout.WEST);
		searchWrapper.add(searchField, BorderLayout.CENTER);
		add(searchWrapper, BorderLayout.NORTH);

		// ── Table ──────────────────────────────────────────────────────────────
		String[] columns = {"#", "Name", "Role", "Total XP", "EHP", "EHB"};
		tableModel = new DefaultTableModel(columns, 0)
		{
			@Override
			public boolean isCellEditable(int row, int col)
			{
				return false;
			}

			@Override
			public Class<?> getColumnClass(int col)
			{
				switch (col)
				{
					case 0: return Integer.class;
					case 3: return Long.class;
					case 4:
					case 5: return Double.class;
					default: return String.class;
				}
			}
		};

		JTable table = new JTable(tableModel);
		sorter = new TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(1).setPreferredWidth(160);
		table.getColumnModel().getColumn(2).setPreferredWidth(110);
		table.getColumnModel().getColumn(3).setPreferredWidth(130);
		table.getColumnModel().getColumn(4).setPreferredWidth(70);
		table.getColumnModel().getColumn(5).setPreferredWidth(70);

		add(new JScrollPane(table), BorderLayout.CENTER);

		// ── Search → filter ────────────────────────────────────────────────────
		searchField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e) { applyFilter(); }
			public void removeUpdate(DocumentEvent e) { applyFilter(); }
			public void changedUpdate(DocumentEvent e) { applyFilter(); }

			private void applyFilter()
			{
				String text = searchField.getText().trim();
				sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text, 1));
			}
		});
	}

	/** Replaces the table contents with the given member list. */
	void setMembers(List<WomMember> members)
	{
		tableModel.setRowCount(0);
		for (int i = 0; i < members.size(); i++)
		{
			WomMember m = members.get(i);
			tableModel.addRow(new Object[]{i + 1, m.getDisplayName(), formatRole(m.getRole()), m.getTotalXp(), m.getEhp(), m.getEhb()});
		}
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

package com.example;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Main sidebar panel for the WOM Clan Stats plugin.
 * Shows a searchable, sorted list of clan members with their XP, EHP, and EHB.
 */
class WomClanPanel extends PluginPanel
{
	private static final long COOLDOWN_MS = 5 * 60 * 1_000L;

	private final WomClanPlugin plugin;

	private final JLabel statusLabel;
	private final JButton syncButton;
	private final JTextField searchField;
	private final JPanel memberListPanel;

	private List<WomMember> allMembers = new ArrayList<>();
	private long lastManualSyncTime = 0;
	private Instant lastSyncInstant = null;

	private final ScheduledExecutorService cooldownExecutor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> cooldownTask;

	WomClanPanel(WomClanPlugin plugin)
	{
		this.plugin = plugin;

		setLayout(new BorderLayout(0, 0));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		// ── Header bar (Sync button + status) ─────────────────────────────────
		syncButton = new JButton("Sync Now");
		syncButton.setFont(FontManager.getRunescapeSmallFont());
		syncButton.setFocusPainted(false);
		syncButton.addActionListener(this::onSyncClicked);

		statusLabel = new JLabel("Not synced yet");
		statusLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		statusLabel.setFont(FontManager.getRunescapeSmallFont());
		statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		JPanel topBar = new JPanel(new BorderLayout(6, 0));
		topBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		topBar.setBorder(new EmptyBorder(8, 8, 6, 8));
		topBar.add(syncButton, BorderLayout.WEST);
		topBar.add(statusLabel, BorderLayout.CENTER);

		// ── Search field ───────────────────────────────────────────────────────
		searchField = new JTextField();
		searchField.setFont(FontManager.getRunescapeSmallFont());
		searchField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchField.setForeground(Color.WHITE);
		searchField.setCaretColor(Color.WHITE);
		searchField.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR),
			new EmptyBorder(4, 6, 4, 6)));
		searchField.setToolTipText("Filter members by name…");
		searchField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				filterMembers();
			}

			public void removeUpdate(DocumentEvent e)
			{
				filterMembers();
			}

			public void changedUpdate(DocumentEvent e)
			{
				filterMembers();
			}
		});

		JPanel searchWrapper = new JPanel(new BorderLayout());
		searchWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchWrapper.setBorder(new EmptyBorder(0, 8, 8, 8));
		searchWrapper.add(searchField, BorderLayout.CENTER);

		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		headerPanel.add(topBar, BorderLayout.NORTH);
		headerPanel.add(searchWrapper, BorderLayout.SOUTH);

		add(headerPanel, BorderLayout.NORTH);

		// ── Member list ────────────────────────────────────────────────────────
		memberListPanel = new JPanel();
		memberListPanel.setLayout(new BoxLayout(memberListPanel, BoxLayout.Y_AXIS));
		memberListPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JScrollPane scrollPane = new JScrollPane(memberListPanel);
		scrollPane.setBorder(null);
		scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));

		add(scrollPane, BorderLayout.CENTER);

		showPlaceholder("Set your WOM Group ID in the\nplugin config, then hit Sync Now.");
	}

	// ── Sync button handler ────────────────────────────────────────────────────

	private void onSyncClicked(ActionEvent e)
	{
		long now = System.currentTimeMillis();
		long elapsed = now - lastManualSyncTime;

		if (elapsed < COOLDOWN_MS)
		{
			long remaining = (COOLDOWN_MS - elapsed) / 1_000;
			syncButton.setText("Wait " + remaining + "s");
			return;
		}

		lastManualSyncTime = now;
		syncButton.setEnabled(false);
		syncButton.setText("Syncing…");
		plugin.manualSync();
		startCooldownTimer();
	}

	private void startCooldownTimer()
	{
		if (cooldownTask != null)
		{
			cooldownTask.cancel(false);
		}

		final long startTime = System.currentTimeMillis();
		cooldownTask = cooldownExecutor.scheduleAtFixedRate(() ->
		{
			long elapsed = System.currentTimeMillis() - startTime;
			long remaining = (COOLDOWN_MS - elapsed) / 1_000;

			if (remaining <= 0)
			{
				SwingUtilities.invokeLater(() ->
				{
					syncButton.setEnabled(true);
					syncButton.setText("Sync Now");
				});
				if (cooldownTask != null)
				{
					cooldownTask.cancel(false);
				}
			}
			else
			{
				final long r = remaining;
				SwingUtilities.invokeLater(() -> syncButton.setText("Wait " + r + "s"));
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	// ── Public API called by WomClanPlugin ─────────────────────────────────────

	/** Called on the EDT after a successful fetch. */
	void updateMembers(List<WomMember> members)
	{
		allMembers = new ArrayList<>(members);
		allMembers.sort(Comparator.comparingLong(WomMember::getTotalXp).reversed());
		lastSyncInstant = Instant.now();
		statusLabel.setText("Synced: just now");
		syncButton.setEnabled(true);
		syncButton.setText("Sync Now");
		filterMembers();
	}

	/** Updates the status label text (call via SwingUtilities.invokeLater from background). */
	void setSyncStatus(String msg)
	{
		statusLabel.setText(msg);
	}

	/** Shows an error state in the panel (call via SwingUtilities.invokeLater). */
	void showError(String msg)
	{
		statusLabel.setText("Sync failed");
		syncButton.setEnabled(true);
		syncButton.setText("Sync Now");
		showPlaceholder("Error: " + msg + "\n\nCheck your Group ID and connection.");
	}

	void shutdown()
	{
		cooldownExecutor.shutdownNow();
	}

	// ── Private helpers ────────────────────────────────────────────────────────

	private void filterMembers()
	{
		String query = searchField.getText().trim().toLowerCase();
		List<WomMember> filtered = new ArrayList<>();

		for (WomMember m : allMembers)
		{
			if (query.isEmpty() || m.getDisplayName().toLowerCase().contains(query))
			{
				filtered.add(m);
			}
		}

		rebuildList(filtered);
	}

	private void rebuildList(List<WomMember> members)
	{
		memberListPanel.removeAll();

		if (members.isEmpty())
		{
			String msg = allMembers.isEmpty()
				? "No data yet.\nHit Sync Now to load members."
				: "No members match \"" + searchField.getText().trim() + "\".";
			showPlaceholder(msg);
		}
		else
		{
			for (int i = 0; i < members.size(); i++)
			{
				WomMemberPanel row = new WomMemberPanel(members.get(i), i % 2 == 0);
				row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
				memberListPanel.add(row);
			}
		}

		memberListPanel.revalidate();
		memberListPanel.repaint();
	}

	private void showPlaceholder(String text)
	{
		memberListPanel.removeAll();

		JLabel label = new JLabel("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
		label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		label.setFont(FontManager.getRunescapeSmallFont());
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(new EmptyBorder(24, 12, 12, 12));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		memberListPanel.add(label);
		memberListPanel.revalidate();
		memberListPanel.repaint();
	}
}

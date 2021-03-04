package com.ur.thph.modbus_urcap.impl;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;

public class DaemonInstallationNodeView implements SwingInstallationNodeView<DaemonInstallationNodeContribution> {
	
	private JButton startButton;
	private JButton stopButton;
	private JLabel statusLabel;

	public DaemonInstallationNodeView(ViewAPIProvider apiProvider) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void buildUI(JPanel panel, DaemonInstallationNodeContribution contribution) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(createStatusLabel(contribution));

	}
	
	private Box createStatusLabel(final DaemonInstallationNodeContribution contribution) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		Dimension dimension = new Dimension();
		dimension.setSize(100, 50);
		
		this.startButton = new JButton("Start Daemon");
		this.stopButton = new JButton("Stop Daemon");
		this.statusLabel = new JLabel("My Daemon status");
		
		this.startButton.setSize(dimension);
		this.stopButton.setSize(dimension);
		
		
		
		startButton.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				contribution.onStartClick();
			}
		});
		
		
		stopButton.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				contribution.onStopClick();
			}
		});
		
		
		box.add(startButton);
		box.add(createHorizontalSpacing(20));
		box.add(stopButton);
		box.add(createHorizontalSpacing(20));
		box.add(statusLabel);
		
		return box;
		
	}
	
	
	
	public void setStartButtonEnabled(boolean enabled) {
		startButton.setEnabled(enabled);
	}

	public void setStopButtonEnabled(boolean enabled) {
		stopButton.setEnabled(enabled);
	}

	public void setStatusLabel(String text) {
		statusLabel.setText(text);
	}
	
	/**
	 * Create a horizontal spacing.
	 * 
	 * @param spacesize
	 * @return
	 */
	private Component createHorizontalSpacing(int spacesize) {
		return Box.createRigidArea(new Dimension(spacesize, 0));
	}

}
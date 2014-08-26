package org.knime.knip.javacv.nodes.hsvthresholder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.knime.core.node.NodeView;
import org.knime.knip.javacv.ViewPanel;

public class InteractiveThresholdingStreamingView extends
		NodeView<HSVThresholderNodeModel> {

	private HSVThresholderNodeModel m_model;
	// private JPanel m_validPeer;
	private ViewPanel m_canvas;
	private boolean first;
	private JPanel m_panel;
	private JPanel m_thresholdingPanel;
	private JTextField m_thresholds;
	private JButton m_confirmButton;

	public InteractiveThresholdingStreamingView(
			final HSVThresholderNodeModel nodeModel, String title) {
		super(nodeModel);
		m_model = nodeModel;
		m_panel = new JPanel();
		m_thresholdingPanel = new JPanel();
		m_thresholdingPanel.add(m_thresholds = new JTextField(
				"20,30,170,255,255,255", 30));
		m_thresholdingPanel.add(m_confirmButton = new JButton("Do It"));

		m_confirmButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StringTokenizer tokenizerH = new StringTokenizer(m_thresholds
						.getText(), ",");
				nodeModel.setHSV(Double.valueOf(tokenizerH.nextToken()),
						Double.valueOf(tokenizerH.nextToken()),
						Double.valueOf(tokenizerH.nextToken()),
						Double.valueOf(tokenizerH.nextToken()),
						Double.valueOf(tokenizerH.nextToken()),
						Double.valueOf(tokenizerH.nextToken()));
			}
		});

		m_canvas = new ViewPanel();

		m_panel.setLayout(new BorderLayout());
		m_panel.add(m_canvas, BorderLayout.NORTH);
		m_panel.add(m_thresholdingPanel, BorderLayout.SOUTH);

		setShowNODATALabel(false);
		setComponent(m_panel);
	}

	@Override
	protected void onClose() {
		first = false;
	}

	@Override
	protected void onOpen() {
		m_canvas.setSize(400, 400);
		first = true;
	}

	@Override
	protected void modelChanged() {

		IplImage img = null;

		if (!first && m_canvas.getBufferStrategy() == null) {
			m_canvas.createBufferStrategy(2);
		}

		first = false;

		// First needs to be !=null
		if ((img = m_model.getFrame()) != null) {
			m_canvas.showImage(img);
			m_canvas.paint(null);
		}
	}
}

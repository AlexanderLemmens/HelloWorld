package life;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonActions implements ActionListener {

	public ButtonActions(ConwayGameOfLife experiment, Buttons button) {
		this.experiment = experiment;
		this.button = button;
	}
	
	private Buttons button;
	
	private ConwayGameOfLife experiment;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.experiment.buttonClicked(this.button);
	}

}
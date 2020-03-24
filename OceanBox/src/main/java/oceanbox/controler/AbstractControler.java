package oceanbox.controler;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import oceanbox.model.AbstractModel;
import oceanbox.view.Alerte;
import oceanbox.view.info.Bandeau_deroulant;
import oceanbox.view.info.Barre_info;
import oceanbox.view.info.BasicInfo;

public abstract class AbstractControler {

	protected AbstractModel model;
	protected Stage stage;
	protected PauseTransition pauseBeforeClose = new PauseTransition(Duration.seconds(20));
	protected EventHandler<ActionEvent> closeApp = event -> {
		stage.close();
	};

	protected Label closeInfoControler;
	protected PauseTransition pauseBeforeCloseAlert = new PauseTransition(Duration.seconds(14));
	protected EventHandler<ActionEvent> closeAlert = event -> {
		closeInfoControler = new Alerte();
		model.notifyObserver(closeInfoControler, true);
	};

	protected Barre_info infoControler;
	protected PauseTransition pauseBeforeShowUpInfo = new PauseTransition(Duration.seconds(8));
	protected EventHandler<ActionEvent> basicInfoShowUp = event -> {
		infoControler = new Barre_info(new Bandeau_deroulant(new BasicInfo(
				"Ceci est une treeeeeeeeeeeeeeeeeeeeeeeeeeeees loooooooooooooooooooooooooooooongue information qui apparaît à l'écran")));
		model.notifyObserver(infoControler, true);
		controlInfo();
	};

	public AbstractControler(Stage stage, AbstractModel model) {
		this.model = model;
		this.stage = stage;
		control();
		stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			model.notifyObserver(closeInfoControler, false);
			control();
		});
	}

	public AbstractModel getModel() {
		return model;
	}

	public abstract void control();
	
	public abstract void controlInfo();
}

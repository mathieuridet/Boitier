package oceanbox.controler;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import java.util.Date;
import java.util.Timer;

import oceanbox.model.AbstractModel;
import oceanbox.model.Contenu;
import oceanbox.model.ftp.DownloadTask;

import oceanbox.propreties.ClientPropreties;

import oceanbox.view.Veille;

/**
 * Cette classe contient des attributs et des méthodes qui jouent sur ce qui
 * s'affiche à l'écran
 */
public abstract class AbstractControler {

	protected AbstractModel model;
	protected int secondsBeforeClose;
	protected boolean sleep;
	protected Veille veille;
	protected Contenu contenu;
	protected boolean download;

	/**
	 * Cet événement alerte l'utilisateur que l'application va se fermer si le
	 * capteur du boîtier ne détecte pas de mouvement
	 */
	// TODO mais peut-être pas possible...

	public AbstractControler() {
		// Ceci est un constructeur qui n'est utile que pour les tests unitaires
	}

	public AbstractControler(AbstractModel model) {

		this.model = model;
		this.sleep = false;
		this.download = false;
		this.secondsBeforeClose = initSecondsBeforeClose();

		initStageProperties();

		control();
		
		this.contenu = new Contenu(this);
	}

	/**
	 * Cette méthode récupère les préférences du client concernant le temps que le
	 * boîtier doit rester allumé si son capteur ne détecte aucun mouvement
	 * 
	 * @return le nombre de secondes restantes avant la fermeture de l'application
	 */
	public int initSecondsBeforeClose() {

		String[] times = ClientPropreties.getPropertie("timeBeforeStandby").split(":");

		return (Integer.parseInt(times[0]) * 3600) + (Integer.parseInt(times[1]) * 60) + Integer.parseInt(times[2]);
	}

	/**
	 * Cette méthode met à jour le paramètre onStandy dans les propriétés du client
	 * 
	 * @param standby : qui est true si l'application est fermée et false sinon
	 */
	public void sleepMode(boolean standby) {

		try {
			if (standby) {
				ClientPropreties.setPropertie("onStandby", "true");
				setSleep(true);
			} else {
				ClientPropreties.setPropertie("onStandby", "false");
				setSleep(false);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cette méthode initialise au lancement de l'application les différents
	 * événements qui ont un effet remarquable à l'écran
	 */
	public void initStageProperties() {

		// TODO sleepMode(false) à l'ouverture de l'application

		// TODO en Java
//		stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
//
//			if (event.getCode() == KeyCode.ESCAPE) {
//				stage.hide();
//
//			} else if (!isSleep()) {
//				if (event.getCode() == KeyCode.SPACE) {
//					goInVeille();
//				} else {
//					if (veilleInfoControler != null)
//						model.notifyObserver(veilleInfoControler, false);
//					control();
//				}
//			} else {
//				goOutOfVeille();
//			}
//		});

	}
	
	public void stopDiffusion() {

		contenu.setStart(-1);
		contenu.getVideoPlayer().stop();
	}

	@SuppressWarnings("unused")
	private void goInVeille() {
		sleepMode(true);
		model.notifyObserver(veille, true);
		controlVeille();
	}

	@SuppressWarnings("unused")
	private void goOutOfVeille() {
		contenu.initVideos();
		control();
	}

	/**
	 * Cette méthode initialise au lancement de l'application les téléchargements
	 * réguliers de vidéos
	 */
	public void initDowloadVideos() {

		LocalTime currentTime = LocalTime.now();

		LocalTime downloadTime = LocalTime.parse(ClientPropreties.getPropertie("downloadHour"));

		Timer dowloadTimer = new Timer();
		LocalDateTime firstTimeDownload = null;

		if (currentTime.compareTo(downloadTime) < 0) {
			firstTimeDownload = LocalDateTime.now().withHour(downloadTime.getHour())
					.withMinute(downloadTime.getMinute()).withSecond(downloadTime.getSecond());
		} else {
			firstTimeDownload = LocalDateTime.now().plus(1, ChronoUnit.DAYS).withHour(downloadTime.getHour())
					.withMinute(downloadTime.getMinute()).withSecond(downloadTime.getSecond());
		}

		// TODO enlever l'heure de download et le faire 12 heures après l'heure de
		// réveil
		dowloadTimer.schedule(new DownloadTask(this),
				Date.from(firstTimeDownload.atZone(ZoneId.systemDefault()).toInstant()),
				contenu.getTotalDurationOfVideo() * 1000);
	}

	public AbstractModel getModel() {
		return model;
	}
	
	public int getSecondsBeforeClose() {
		return secondsBeforeClose;
	}

	public boolean isSleep() {
		return sleep;
	}

	public void setSleep(boolean sleep) {
		this.sleep = sleep;
	}

	public Veille getVeille() {
		return veille;
	}

	public Contenu getContenu() {
		return contenu;
	}

	public void setContenu(Contenu contenu) {
		this.contenu = contenu;
	}

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

	/**
	 * Cette méthode remet les différents comptes à rebours de l'application à zéro
	 * et initialise les événements qui se produisent à leur échéance
	 */
	public abstract void control();

	/**
	 * Cette méthode met la valeur null à tous les compteurs de l'application
	 */
	public abstract void controlVeille();
}

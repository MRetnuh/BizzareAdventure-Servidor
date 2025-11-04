package red;

import interfaces.GameController;

public class HiloTiempo extends Thread{
	
	 private GameController gameController;
	  public HiloTiempo(GameController gameController) {
	        this.gameController = gameController;
	    }

	    @Override
	    public void run() {
	        try {
	            Thread.sleep(2000);
	            this.gameController.finalizarTiempo();
	        } catch (InterruptedException e) {
	            throw new RuntimeException(e);
	        }
	    }
}

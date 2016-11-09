import monitor.*;

class Estanco extends AbstractMonitor{
	private Condition estanquero = makeCondition();
	private Condition[] fumador = new Condition[3];
	private int ing_est;

	public Estanco(){
		for(int i=0; i<3; i++)
	    	fumador[i] = makeCondition();
		ing_est=-1;
	}
	
	public void obtenerIngrediente(int miIngrediente){
		enter();

		if(miIngrediente!=ing_est)
			fumador[miIngrediente].await();
		System.out.println("El fumador coge el ingrediente "+miIngrediente);
		ing_est=-1;
		estanquero.signal();
		leave();
	}

	public void ponerIngrediente(int ingrediente){
		enter();
		
		ing_est=ingrediente;
		System.out.println("El estanquero ha puesto el ingrediente "+ingrediente);
		fumador[ingrediente].signal();	
		leave();
	}
	
	public void esperarRecogidaIngrediente(){
		enter();
		if(ing_est!=-1)
			estanquero.await();
				
		leave();
	}	

}

class Fumador implements Runnable{
	int miIngrediente;
	public Thread thr;
	private Estanco Estanco;

	public Fumador(int p_miIngrediente, Estanco p_Estanco) {
		miIngrediente=p_miIngrediente;
		Estanco=p_Estanco;
		thr = new Thread(this,"Fumador ");
	}

	public void run(){
		while(true){
			Estanco.obtenerIngrediente(miIngrediente);
			aux.dormir_max(2000);
		}
	}
}

class Estanquero implements Runnable{
	public Thread thr;
	private Estanco Estanco;

	public Estanquero(Estanco e_Estanco){
		Estanco=e_Estanco;
		thr = new Thread(this,"estanquero");
	}

	public void run(){
		int ingrediente;
		while(true){
			ingrediente=(int) (Math.random () *3.0);
			Estanco.ponerIngrediente(ingrediente);
			Estanco.esperarRecogidaIngrediente();
		}
	}
}

class MainFumadorEstanquero 
{ 
  public static void main( String[] args ) 
  { 
    
    Estanco est = new Estanco();
    Estanquero estanquero = new Estanquero(est);
    Fumador[] fumador = new Fumador[3];
    for(int i=0; i<3; i++)
    	fumador[i] = new Fumador(i,est);
	  
	estanquero.thr.start();
	
	for(int i = 0; i < fumador.length; i++) 
      fumador[i].thr.start();
  }
}


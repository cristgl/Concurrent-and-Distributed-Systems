import monitor.*;

class Barberia extends AbstractMonitor{
	private Condition sala_espera = makeCondition();
	private Condition barbero = makeCondition();
	//private boolean 

	public void cortarPelo(){ //solicitar corte
		enter();
		//
		if(barbero.isEmpty()) //si el barbero está ocupado
			sala_espera.await();
		
		barbero.signal();
		
		System.out.println("Se pide cortar pelo");
	
		leave();
	}
	
	public void siguienteCliente(){
		enter();
		if(sala_espera.isEmpty())//si no hay clientes
			barbero.await();
		sala_espera.signal();
		System.out.println("El barbero corta pelo");
		
		leave();
	}
	
	public void finCliente(){
		enter();
		if(!sala_espera.isEmpty())
			barbero.signal();
			
		System.out.println("Termina con este cliente");
		
		leave();
	}
}

class Cliente implements Runnable{
	public Thread thr;
	public Barberia barberia;
	
	public Cliente(Barberia barb){
		barberia=barb;
		thr = new Thread(this,"Cliente ");
	}
	
	public void run(){
		while(true){
			barberia.cortarPelo();
			aux.dormir_max(2000);
		}
	}
}

class Barbero implements Runnable{
	public Thread thr;
	public Barberia barberia;
	
	public Barbero(Barberia barb){
		barberia=barb;
		thr = new Thread(this,"Barbero ");
	}
	
	public void run(){
		while(true){
			barberia.siguienteCliente();
			aux.dormir_max(2500);
			barberia.finCliente();
		}
	}	
}

class MainBarbero{
    public static void main(String[] args)
    {
        final int NUM_CLIENTES = 5;

        Barberia barberia = new Barberia();
        Barbero barbero = new Barbero(barberia);
        Cliente[] clientes = new Cliente[NUM_CLIENTES];
        for (int i=0; i<NUM_CLIENTES; i++)
            clientes[i] = new Cliente(barberia);


        barbero.thr.start();

        for (int i=0; i<NUM_CLIENTES; i++)
            clientes[i].thr.start();

    }
}

import monitor.*;

class Estanco extends AbstractMonitor{
	private Condition estanquero = makeCondition();
	private Condition[] fumador = new Condition[18];
	private Condition[] fumador_emp = new Condition[18];
	private int ing_est;

	public Estanco(){
		for(int i=0; i<18; i++)
	    	fumador[i] = makeCondition();
	    for(int i=0; i<18; i++)
	    	fumador_emp[i]=makeCondition();
		ing_est=-1;
	}
	
	public void obtenerIngrediente(int miIngrediente){
		enter();

		if(miIngrediente!=ing_est){
			fumador[miIngrediente].await();
			fumador_emp[miIngrediente].await();	
		}
		//System.out.println("El fumador coge el ingrediente "+miIngrediente);
		ing_est=-1;
		estanquero.signal();
		leave();
	}

	public void ponerIngrediente(int ingrediente){
		enter();
		
		ing_est=ingrediente;
		//System.out.println("El estanquero ha puesto el ingrediente "+ingrediente);
		fumador[ingrediente].signal();
		fumador_emp[ingrediente].signal();	
		leave();
	}
	
	public void esperarRecogidaIngrediente(){
		enter();
		if(ing_est!=-1)
			estanquero.await();
		//System.out.println("El estanquero espera la recogida del ingrediente ");		
		leave();
	}	

}

class Fumador implements Runnable{
	int miIngrediente;
	public Thread thr;
	private Estanco Estanco;

	public Fumador(int e_miIngrediente, Estanco e_Estanco, int i) {
		miIngrediente=e_miIngrediente;
		Estanco=e_Estanco;
		thr = new Thread(this,"Fumador "+i);
	}

	public void run(){
		while(true){
			Estanco.obtenerIngrediente(miIngrediente);
			aux.dormir_max(2000);
			System.out.println(thr.getName()+" coge el ingrediente "+miIngrediente);
		}
	}
}

class FumadorEmpedernido implements Runnable{
	public Thread thr;
	private Estanco Estanco1,Estanco2;
	int ingrediente1,ingrediente2;
	
	public FumadorEmpedernido(Estanco e_Estanco1,Estanco e_Estanco2, int i){
	Estanco1=e_Estanco1;
	Estanco2=e_Estanco2;
	thr = new Thread(this,"Fumador empedernido "+i);
	}

	public void run(){
		while(true){
			ingrediente1=(int) (Math.random()*3.0);
			Estanco1.obtenerIngrediente(ingrediente1);
			System.out.println(thr.getName()+" coge del estanco 1 el ingrediente "+ingrediente1);
			ingrediente2=(int) (Math.random()*3.0);
			System.out.println(thr.getName()+" coge del estanco 2 el ingrediente "+ingrediente2);
			Estanco2.obtenerIngrediente(ingrediente2);
		}
	}
}

class Estanquero implements Runnable{
	public Thread thr;
	private Estanco Estanco;

	public Estanquero(Estanco e_Estanco,int i){
		Estanco=e_Estanco;
		thr = new Thread(this,"Estanquero "+i);
	}

	public void run(){
		int ingrediente;
		while(true){
			ingrediente=(int) (Math.random () *3.0);
			Estanco.ponerIngrediente(ingrediente);
			System.out.println(thr.getName()+" pone el ingrediente "+ingrediente);
			Estanco.esperarRecogidaIngrediente();
		}
	}
}

class MainFumadorEmped 
{ 
  public static void main( String[] args ) 
  { 
    int nf;
    nf = (int) (Math.random()*3.0+1)*6;
    Estanco est[] = new Estanco[2];
    Estanquero estanquero[] = new Estanquero[2];
    
    Fumador[] fumador = new Fumador[nf];
    FumadorEmpedernido[] fumador_emped = new FumadorEmpedernido[nf];
    est[0] = new Estanco();
    est[1] = new Estanco();
    estanquero[0] = new Estanquero(est[0],0);
    estanquero[1]=new Estanquero(est[1],1);
    for(int i=0; i<nf; i++)
    	fumador_emped[i] = new FumadorEmpedernido(est[0],est[1],i);
    for(int i=0; i<nf; i++)
    	fumador[i] = new Fumador(i%3,est[(i/3)%2],i);
	  
	for(int i=0; i<2; i++)
		estanquero[i].thr.start();
	
	for(int i = 0; i < fumador.length; i++) 
      fumador[i].thr.start();
      
    for(int i=0; i<fumador_emped.length; i++)
    	fumador_emped[i].thr.start();
  }
}


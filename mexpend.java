import monitor.*;
import java.util.Random ;

// ****************************************************************************

class aux
{
  static Random genAlea = new Random() ;

  static void dormir_max( int milisecsMax )
  { 
    try
    { 
      Thread.sleep( genAlea.nextInt( milisecsMax ) ) ;
    } 
    catch( InterruptedException e )
    { 
      System.err.println("sleep interumpido en 'aux.dormir_max()'");
    }
  }
}


// ****************************************************************************

class MaquinaExpendedora extends AbstractMonitor{
	private int nt=-1;
	private int[] latas;
	private int rand;
	private Condition[] solicitarLata;
	
	public MaquinaExpendedora(int num_tipos){
		nt=num_tipos;
		latas= new int[nt];
		solicitarLata=new Condition[nt];
		for(int i=0; i<nt; i++)
			solicitarLata[i]=makeCondition();
		for(int i=0; i<nt; i++){
			rand=(int) (Math.random()*6.0);
			latas[i]=rand+4;
		}
	}

	public int numTipos(){
		return nt;
	}
	
	public void pedirLata(int titulo){
		enter();
		int cont=0,rand1,nlat;
		for(int i=0; i<nt; i++)
			cont=cont+solicitarLata[i].count();
			
		solicitarLata[titulo].signal();
		if(cont>nt*7/3){
			System.out.println("Recarga");
			for(int i=0; i<nt; i++){
				rand1=(int) (Math.random()*6.0);
				latas[i]=solicitarLata[i].count()+rand1+4;
				nlat=solicitarLata[i].count();
				for(int j=0; j<nlat;j++)
					solicitarLata[i].signal();
			}
		}
		System.out.println("usuario pide la lata "+titulo);
		while(latas[titulo]==0)
			solicitarLata[titulo].await();
		
		latas[titulo]--;
		System.out.println("usuario recoge la lata "+titulo);
		
		leave();
	}
}



class Usuario implements Runnable{
	public Thread thr;
	private MaquinaExpendedora me;
	int num_tipos,lata;
	
	public Usuario(MaquinaExpendedora e_me,int i){
		me=e_me;
		thr = new Thread(this,"Usuario "+i);
	}
	public void run(){
		num_tipos=me.numTipos();
		while(true){
			lata=(int) (Math.random()*num_tipos);
			me.pedirLata(lata);
			//System.out.println(thr.getName()+" pide la lata "+lata);
			aux.dormir_max(1000);
		}
	}
}

class MainMaqExp 
{ 
  public static void main( String[] args ) 
  { 
    int nt,nu,rand2;
    rand2=(int) (Math.random()*10.0);
    nt = rand2+10;
    nu=7*nt;
    
    MaquinaExpendedora mexp = new MaquinaExpendedora(nt);
    
    Usuario usuario[] = new Usuario[nu];
    
    for(int i=0; i<nu; i++)
    	usuario[i] = new Usuario(mexp,i);
    
    for(int i=0; i<usuario.length; i++)
    	usuario[i].thr.start();
    	
  }
}


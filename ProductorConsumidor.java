
// ****************************************************************************
import monitor.*;
class Monitor extends AbstractMonitor 
{ 
  private int      numSlots = 0    , 
                   cont     = 0    ;   
  private double[] buffer   = null ;
  private Condition consumir = makeCondition();
  private Condition producir = makeCondition();
  
  public Monitor( int p_numSlots ) 
  { 
    numSlots = p_numSlots ; 
    buffer = new double[numSlots] ;
  }
  public void producir(double valor)
  { 
  	enter();
    while( cont == numSlots ) 
      producir.await();      
    
    buffer[cont] = valor ; 
    cont++ ;      
    consumir.signal() ;  
    leave();
  	
  }
  public double consumir()
  { 
  	enter();
    double valor ;
    while( cont == 0 ) 
      consumir.await();
          
    cont--; 
    valor = buffer[cont] ;
    producir.signal();
    leave();
    return valor;
  }
}

// ****************************************************************************

class Productor implements Runnable 
{ 
  private Monitor bb    ;
  private int    veces , 
                 numP  ;
  public  Thread thr   ;
  
  public Productor( Monitor pbb, int pveces, int pnumP ) 
  { 
    bb    = pbb;
    veces = pveces;
    numP  = pnumP ;
    thr   = new Thread(this,"productor "+numP);
  }

  public void run() 
  { 
    try
    { 
      double item = 100*numP ;
      
      for( int i=0 ; i < veces ; i++ )
      { 
        System.out.println(thr.getName()+", produciendo " + item);
        bb.producir( item++ );
      }
    }
    catch( Exception e ) 
    { 
      System.err.println("Excepcion en main: " + e);
    }
  }
}

// ****************************************************************************

class Consumidor implements Runnable 
{ 
  private Monitor  bb   ;
  private int     veces ,
                  numC  ;
  public  Thread  thr   ;
  
  public Consumidor( Monitor pbb, int pveces, int pnumC ) 
  { 
    bb    = pbb;
    veces = pveces;
    numC  = pnumC ;
    thr   = new Thread(this,"consumidor "+numC);
  }
  public void run() 
  { 
    try 
    { 
      for( int i=0 ; i<veces ; i++ )
      { 
        double item= bb.consumir ();
        System.out.println(thr.getName()+", consumiendo "+item);
      } 
    }
    catch( Exception e ) 
    { 
      System.err.println("Excepcion en main: " + e);
    }
  }
}

// ****************************************************************************

class MainProductorConsumidor 
{ 
  public static void main( String[] args ) 
  { 
    if ( args.length != 5 ) 
    {  
      System.err.println("Uso: ncons nprod tambuf niterp niterc");
      return ;
    }

    // leer parametros, crear vectores y buffer intermedio
	Consumidor[] cons      = new Consumidor[Integer.parseInt(args[0])] ;
	Productor[]  prod      = new Productor[Integer.parseInt(args[1])] ;
	Monitor       mon    = new Monitor(Integer.parseInt(args[2]));
	int          iter_cons = Integer.parseInt(args[3]);
	int          iter_prod = Integer.parseInt(args[4]);
	  
    if ( cons.length*iter_cons != prod.length*iter_prod )
	  { 
      System.err.println("no coinciden número de items a producir con a cosumir");
    }    

	  // crear hebras
	  for(int i = 0; i < cons.length; i++) 
	    cons[i] = new Consumidor(mon,iter_cons,i) ;
	  for(int i = 0; i < prod.length; i++)
	    prod[i] = new Productor(mon,iter_prod,i) ;

	  // poner en marcha las hebras
	  for(int i = 0; i < prod.length; i++) 
      prod[i].thr.start();
	  for(int i = 0; i < cons.length; i++) 
      cons[i].thr.start();
  }
}


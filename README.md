
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW
## Ejercicio Introducción al paralelismo - Hilos - Caso BlackListSearch

###Prueba Camilo

### Dependencias:
####   Lecturas:
*  [Threads in Java](http://beginnersbook.com/2013/03/java-threads/)  (Hasta 'Ending Threads')
*  [Threads vs Processes]( http://cs-fundamentals.com/tech-interview/java/differences-between-thread-and-process-in-java.php)

### Descripción
  Este ejercicio contiene una introducción a la programación con hilos en Java, además de la aplicación a un caso concreto.
  

**Parte I - Introducción a Hilos en Java**

1. De acuerdo con lo revisado en las lecturas, complete las clases CountThread, para que las mismas definan el ciclo de vida de un hilo que imprima por pantalla los números entre A y B.

**Para realizar esta parte, completamos la clase ```CountThread``` de la siguiente forma:**
```java
public class CountThread implements Runnable{
    private int num1, num2;
    public CountThread(int num1, int num2) {
        this.num1 = num1;
        this.num2 = num2;
    }
    @Override
    public void run(){
        for (int i=num1; i<=num2; i++){
            System.out.println(i);
        }
    }
}
```

2. Complete el método __main__ de la clase CountMainThreads para que:
	1. Cree 3 hilos de tipo CountThread, asignándole al primero el intervalo [0..99], al segundo [99..199], y al tercero [200..299].
	2. Inicie los tres hilos con 'start()'.
	3. Ejecute y revise la salida por pantalla. 
	4. Cambie el incio con 'start()' por 'run()'. Cómo cambia la salida?, por qué?.
	
**Primero completamos la clase ```CountThreadsMain```, en la cual iniciamos los tres hilos con ```start()```, quedando de la siguiente forma:**

```java
public class CountThreadsMain { 
    public static void main(String a[]){
        CountThread countThread1 = new CountThread(0,99);
        CountThread countThread2 = new CountThread(99,199);
        CountThread countThread3 = new CountThread(200,299);

        Thread hilo1 = new Thread(countThread1);
        Thread hilo2 = new Thread(countThread2);
        Thread hilo3 = new Thread(countThread3);
        
        hilo1.start();
        hilo2.start();
        hilo3.start();
    }  
}
```

**Luego de ejecutarlo, la salida por pantalla es la siguiente:**

```
0
1
2
200
99
100
101
3
4
5
6
7
102
201
202
203
204
103
8
```

**Luego de cambiar el incio con ```start()``` por ```run()```, la clase ```CountThreadsMain``` queda de la siguiente forma:**

```java
public class CountThreadsMain { 
    public static void main(String a[]){
        CountThread countThread1 = new CountThread(0,99);
        CountThread countThread2 = new CountThread(99,199);
        CountThread countThread3 = new CountThread(200,299);

        Thread hilo1 = new Thread(countThread1);
        Thread hilo2 = new Thread(countThread2);
        Thread hilo3 = new Thread(countThread3);
        
        hilo1.run();
        hilo2.run();
        hilo3.run();
    }  
}
```
	
**Al ejecutarlo, la salida por pantalla queda de la siguiente forma:**

```
0
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
```

Como vemos, la salida cuando estaba en ```start()``` es diferente a la de ```run()```. En ```start()``` los números los retorna en desorden, y en ```run()``` los números lo retorna en orden, esto se debe a que cuando el programa llama al método ```start()```, se crea un nuevo hilo y el código dentro de ```run()``` se ejecuta en un nuevo hilo, mientras que si llama al método ```run()``` directamente se creará un nuevo hilo y el código dentro de ```run()``` se ejecutará en el hilo actual directamente. Por eso en el programa cuando implementamos ```run()``` se muestra en orden los números y cuando implementamos ```start()``` se ejecutan en desorden.

-----------------------------------------------------------------------------------

**Parte II - Ejercicio Black List Search**


Para un software de vigilancia automática de seguridad informática se está desarrollando un componente encargado de validar las direcciones IP en varios miles de listas negras (de host maliciosos) conocidas, y reportar aquellas que existan en al menos cinco de dichas listas. 

Dicho componente está diseñado de acuerdo con el siguiente diagrama, donde:

- HostBlackListsDataSourceFacade es una clase que ofrece una 'fachada' para realizar consultas en cualquiera de las N listas negras registradas (método 'isInBlacklistServer'), y que permite también hacer un reporte a una base de datos local de cuando una dirección IP se considera peligrosa. Esta clase NO ES MODIFICABLE, pero se sabe que es 'Thread-Safe'.

- HostBlackListsValidator es una clase que ofrece el método 'checkHost', el cual, a través de la clase 'HostBlackListDataSourceFacade', valida en cada una de las listas negras un host determinado. En dicho método está considerada la política de que al encontrarse un HOST en al menos cinco listas negras, el mismo será registrado como 'no confiable', o como 'confiable' en caso contrario. Adicionalmente, retornará la lista de los números de las 'listas negras' en donde se encontró registrado el HOST.

![](img/Model.png)

Al usarse el módulo, la evidencia de que se hizo el registro como 'confiable' o 'no confiable' se dá por lo mensajes de LOGs:

INFO: HOST 205.24.34.55 Reported as trustworthy

INFO: HOST 205.24.34.55 Reported as NOT trustworthy


Al programa de prueba provisto (Main), le toma sólo algunos segundos análizar y reportar la dirección provista (200.24.34.55), ya que la misma está registrada más de cinco veces en los primeros servidores, por lo que no requiere recorrerlos todos. Sin embargo, hacer la búsqueda en casos donde NO hay reportes, o donde los mismos están dispersos en las miles de listas negras, toma bastante tiempo.

Éste, como cualquier método de búsqueda, puede verse como un problema [vergonzosamente paralelo](https://en.wikipedia.org/wiki/Embarrassingly_parallel), ya que no existen dependencias entre una partición del problema y otra.

Para 'refactorizar' este código, y hacer que explote la capacidad multi-núcleo de la CPU del equipo, realice lo siguiente:

1. Cree una clase de tipo Thread que represente el ciclo de vida de un hilo que haga la búsqueda de un segmento del conjunto de servidores disponibles. Agregue a dicha clase un método que permita 'preguntarle' a las instancias del mismo (los hilos) cuantas ocurrencias de servidores maliciosos ha encontrado o encontró.

**Para realizar la siguiente implementación, se creó la clase ```BlackListThread```, en la cual realiza la búsqueda de un segmento del conjunto de servidores disponibles, en la cual en la clase ```run()``` con hilos se encarga de llevar a cabo las ocurrencias de servidores maliciosos ha encontrado o encontró. La clase fue implementada de la siguiente forma.**

```java
public class BlackListThread extends Thread{
    private static final int BLACK_LIST_ALARM_COUNT=5;
    private int inicio;
    private int fin;
    private String Host;
    HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
    LinkedList<Integer> blackListOcurrences=new LinkedList<>();
    private int checkedListsCount;
    private int ocurrencesCount;
    public BlackListThread(int i,int f,String Host) { 
	this.inicio=i;
	this.fin=f;
	this.Host=Host;
	this.checkedListsCount = 0;
	this.ocurrencesCount = 0;
    }
    public void run() {
        for (int i=inicio;i<fin && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            checkedListsCount++;
            if (skds.isInBlackListServer(i, Host)) {
                blackListOcurrences.add(i);
                ocurrencesCount++;
            }
        } 
    }
    public LinkedList<Integer> getBlackListOcurrences() {
	return blackListOcurrences;
    }
    public int getCheckedListsCount() {
	return checkedListsCount;
    }
    public int getOcurrencesCount() {
	return ocurrencesCount;
    }
}
```

2. Agregue al método 'checkHost' un parámetro entero N, correspondiente al número de hilos entre los que se va a realizar la búsqueda (recuerde tener en cuenta si N es par o impar!). Modifique el código de este método para que divida el espacio de búsqueda entre las N partes indicadas, y paralelice la búsqueda a través de N hilos. Haga que dicha función espere hasta que los N hilos terminen de resolver su respectivo sub-problema, agregue las ocurrencias encontradas por cada hilo a la lista que retorna el método, y entonces calcule (sumando el total de ocurrencuas encontradas por cada hilo) si el número de ocurrencias es mayor o igual a _BLACK_LIST_ALARM_COUNT_. Si se da este caso, al final se DEBE reportar el host como confiable o no confiable, y mostrar el listado con los números de las listas negras respectivas. Para lograr este comportamiento de 'espera' revise el método [join](https://docs.oracle.com/javase/tutorial/essential/concurrency/join.html) del API de concurrencia de Java. Tenga también en cuenta:

	* Dentro del método checkHost Se debe mantener el LOG que informa, antes de retornar el resultado, el número de listas negras revisadas VS. el número de listas negras total (línea 60). Se debe garantizar que dicha información sea verídica bajo el nuevo esquema de procesamiento en paralelo planteado.

	* Se sabe que el HOST 202.24.34.55 está reportado en listas negras de una forma más dispersa, y que el host 212.24.24.55 NO está en ninguna lista negra.

**A continuación, implementamos el método ```checkHost```, el cual se encarga de dadas unas direcciones IP del Host en todas las Black List disponibles, las reporta como confiables (trustworthy) o no confiables (not trustworthy), teniendo en cuenta el ```BLACK_LIST_ALARM_COUNT```, el cual si el número de ocurrencias es mayor al del ```BLACK_LIST_ALARM_COUNT```, se reporta la dirección IP como no confiable, paralelizando la búsqueda a través de hilos, los cuales son los encargados de calcular si el número de ocurrencias es mayor o igual a ```BLACK_LIST_ALARM_COUNT```, de tal forma que la función espera hasta que los N hilos terminen de resolver su respectivo sub-problema, para así realizar una búsqueda paralelizada para reportar si el host es confiable o no confiable.**

```java
public class HostBlackListsValidator {
    private static final int BLACK_LIST_ALARM_COUNT=5;
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return Blacklists numbers where the given host's IP address was found.
     */   
public List<Integer> checkHost(String ipaddress,int N) throws InterruptedException{
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        ArrayList<BlackListThread> threads = new ArrayList<BlackListThread>();       
        int ocurrencesCount=0;        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        int count = skds.getRegisteredServersCount()/N;
        int mod = skds.getRegisteredServersCount()%N;
        for (int i = 0;i<N;i++) {
        		if(i == N-1) {
        			BlackListThread h = new BlackListThread((i*count),(i*count) + count + mod ,ipaddress);
        			threads.add(h);
        			h.start();
        		}else {
        			BlackListThread h = new BlackListThread((i*count),(i*count)+count,ipaddress);
        			threads.add(h);
        			h.start();
        		}
        }
        for (BlackListThread thread : threads) {
        	thread.join();
        }
        int checkedListsCount=0;
        for (BlackListThread thread : threads) {
        	checkedListsCount += thread.getCheckedListsCount();
        	ocurrencesCount += thread.getOcurrencesCount();
        	blackListOcurrences.addAll(thread.getBlackListOcurrences());
        }
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                  
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    } 
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName()); 
}
```

-----------------------------------------------------------------------------------

**Parte II.I Para discutir la próxima clase (NO para implementar aún)**

La estrategia de paralelismo antes implementada es ineficiente en ciertos casos, pues la búsqueda se sigue realizando aún cuando los N hilos (en su conjunto) ya hayan encontrado el número mínimo de ocurrencias requeridas para reportar al servidor como malicioso. Cómo se podría modificar la implementación para minimizar el número de consultas en estos casos?, qué elemento nuevo traería esto al problema?



   * Podríamos considerar que la estrategia la podemos mejorar para que cuando consideremos dicho numero de ocurrencias como maliciosas pues dejemos de buscar estas ocurrencias y optimizar el tiempo, en este caso utilizamos una variable atómica, que nos hace el trabajo de tener una variable global para los distintos hilos. 

-----------------------------------------------------------------------------------

**Parte III - Evaluación de Desempeño**

A partir de lo anterior, implemente la siguiente secuencia de experimentos para realizar las validación de direcciones IP dispersas (por ejemplo 202.24.34.55), tomando los tiempos de ejecución de los mismos (asegúrese de hacerlos en la misma máquina):

Al iniciar el programa ejecute el monitor jVisualVM, y a medida que corran las pruebas, revise y anote el consumo de CPU y de memoria en cada caso. 

![](img/jvisualvm.png)

1. Un solo hilo.

A continuación, ejecutamos el programa realizando las respectivas validaciones de direcciones IP dispersas, el cual se ejecuta en aproximadamente en 109738 milisegundos con un solo hilo, como se puede ver en la siguiente imagen.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/1Hilo.PNG)

Al ejecutar el monitor Java VisualVM justo al empezar el primer experimento con 1 hilo, vemos que en la gráfica de **Threads** se extiende por el eje X que representa el tiempo, esto se debe a que con un solo hilo es cuando el programa más se demora en ejecutar todas las operaciones pertinentes.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/1HiloVisualVM.PNG)

2. Tantos hilos como núcleos de procesamiento (haga que el programa determine esto haciendo uso del [API Runtime](https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html)).

Ahora, realizamos el mismo procedimiento que en el paso anterior, pero esta ves con 8 hilos. Como se puede observar en la siguiente imagen, el tiempo de ejecución es de aproximadamente 20847 milisegundos.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/8Hilos.PNG)

Luego de revisar Java VisualVM mientras se ejecutaban las pruebas, se evidencia una clara diferencia en cuanto a rendimiento. En la gráfica de **Threads**, vemos que se ha reducido un poco el tamaño de la gráfica con respecto al eje X.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/8HilosVisualVM.PNG)

3. Tantos hilos como el doble de núcleos de procesamiento.

Luego realizamos el mismo experimento pero esta ves con el doble de núcleos de procesamiento, en este caso, 16 hilos. Luego de realizar el experimento, el tiempo de ejecución es de aproximadamente 10922 milisegundos.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/16Hilos.PNG)

Al realizar las pruebas con 16 hilos, vemos que se ha reducido un poco el tamaño de la gráfica de **Threads** con respecto a la de 8 hilos, representando una clara diferencia de tiempo de ejecución con respecto al experimento anterior.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/16HilosVisualVM.PNG)

4. 50 hilos.

Ahora realizamos el mismo experimento pero con 50 hilos. Como vemos el tiempo de ejecución del programa se ha reducido considerablemente comparado con 16 hilos, arrojando aproximadamente 3596 milisegundos de tiempo de ejecución.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/50Hilos.PNG)

Ya al realizar la verificación de rendimiento con 50 hilos, se empieza ya a reducir considerablemente la gráfica de **Threads**, verificando por este medio que el tiempo de ejecución con respecto a 16 hilos ha sido reducido considerablemente.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/50HilosVisualVM.PNG)

5. 100 hilos.

Después ejecutamos el programa con 100 hilos. Vemos que el tiempo de ejecución es muy rápido en comparación con los experimentos anteriores, arrojando un tiempo de ejecución de aproximadamente **2042 milisegundos**.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/100Hilos.PNG)

Finalmente, en el último experimento con 100 hilos ya vemos que la gráfica **Threads** se comporta de manera diferente, la cual nos evidencia en cuanto a tamaño en el eje X que el tiempo de ejecución es mucho menor que cuando se realizó el experimento con 50 hilos.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/100HilosVisualVM.PNG)

Con lo anterior, y con los tiempos de ejecución dados, haga una gráfica de tiempo de solución vs. número de hilos. Analice y plantee hipótesis con su compañero para las siguientes preguntas (puede tener en cuenta lo reportado por jVisualVM):

Teniendo en cuenta los datos recolectados después de haber realizado el experimento anterior, realizamos una gráfica de Tiempo de solución vs. Número de hilos, donde el eje X de la gráfica es el Tiempo en milisegundos, y el eje Y de la gráfica son el número de hilos, como se puede observar en la imagen. Claramente se ve una clara diferencia en cuanto a tiempo de ejecución, mas que todo comparando con 1 hilo y con 100 hilos.

![img](https://github.com/Skullzo/ARSW-Lab1/blob/main/img/TablaYgrafica.png)

1. Según la [ley de Amdahls](https://www.pugetsystems.com/labs/articles/Estimating-CPU-Performance-using-Amdahls-Law-619/#WhatisAmdahlsLaw?):

	![](img/ahmdahls.png)
	
	Donde _S(n)_ es el mejoramiento teórico del desempeño, _P_ la fracción paralelizable del algoritmo, y _n_ el número de hilos, a mayor _n_, mayor debería ser dicha mejora. Por qué el mejor desempeño no se logra con los 500 hilos?, cómo se compara este desempeño cuando se usan 200?.
	
	* Si revisamos resultados observamos que el tiempo de ejecución es muy reducido para un numero de hilos pequeño, observamos que si aumentan la cantidad de hilos será menor el tiempo que este tarda en ejecutarlo. Si aumentamos el número de Hilos obtendremos una mejora o incremento del rendimiento del programa. Si tomamos el caso de los 200 y 500 Hilos observaremos que el rendimiento no será un diferenciador muy grande, esto porque el tiempo de rendimiento será una ejecución constante.
	

2. Cómo se comporta la solución usando tantos hilos de procesamiento como núcleos comparado con el resultado de usar el doble de éste?.

	* Podríamos concluir que si tenemos el doble de procesos podríamos obtener la mitad de tiempo de ejecución, pero no es así precisamente, el aumento de rendimiento de tiempo de ejecución no es significativo. Y lo comprobamos de pasar de 8 a 16 hilos. 
	

3. De acuerdo con lo anterior, si para este problema en lugar de 100 hilos en una sola CPU se pudiera usar 1 hilo en cada una de 100 máquinas hipotéticas, la ley de Amdahls se aplicaría mejor?. Si en lugar de esto se usaran c hilos en 100/c máquinas distribuidas (siendo c es el número de núcleos de dichas máquinas), se mejoraría?. Explique su respuesta.
	* La ley de Amdahls puede decirnos que si aumentamos la cantidad de hilos aspiramos a obtener una mejora en el rendimiento de los tiempos de ejecución, de esta manera si usamos una computadora para ejecutar 100 hilos obtendríamos un beneficio en la eficacia del desarrollo de estos, si usamos 100 computadoras para desarrollar un hilo cada una de ellas podríamos concluir que seria un gasto innecesario de recursos y el tiempo aumentaría al desarrollarse este tipo de procedimientos. 

## Autores
[Alejandro Toro Daza](https://github.com/Skullzo)

[David Fernando Rivera Vargas](https://github.com/DavidRiveraRvD)
## Licencia
Licencia bajo la [GNU General Public License](https://github.com/Skullzo/AREP-Lab1/blob/main/LICENSE).

package dominio;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

public class Bibliotecario {

	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";
	public static final String EL_LIBRO_ES_PALINDROMO = "los libros palíndromos solo se pueden utilizar en la biblioteca";

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;
	}

	public void prestar(String isbn, String nombreUsuario) {		
				
		if (this.esPrestado(isbn))
			throw new PrestamoException(EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);		
		
		if (this.esPalindromo(isbn))
			throw new PrestamoException(EL_LIBRO_ES_PALINDROMO);		
		
		Libro libro = this.repositorioLibro.obtenerPorIsbn(isbn);
		
		Date fechaDeEntrega = this.obtenerFechaDeEntrega(isbn);
		Date fechaDeSolicitud = new Date();
		
		Prestamo prestamo = new Prestamo(fechaDeSolicitud, libro, fechaDeEntrega, nombreUsuario);
		this.repositorioPrestamo.agregar(prestamo);			
	}

	public boolean esPrestado(String isbn) {
		
		Libro libro = this.repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn);		
		return libro != null ? true: false;		
	}
		
	public boolean esPalindromo(String isbn) {
		
		 StringBuilder handler = new StringBuilder(isbn);
		 String reves = handler.reverse().toString();		
		 return reves.equals(isbn);			 
	}
	
	public Date obtenerFechaDeEntrega(String isbn) {
				
		int sum = obtenerSuma(isbn);					
		if (sum <= 30) return null;			
		return obtenerFechaLimite();	
	}
		
	// utils
	
	private int obtenerSuma(String isbn) {
		
		char[] isbnArray = isbn.toCharArray();
		int sum = 0;
		
		for (int i = 0; i < isbnArray.length; i++) {
				
			char caracter = isbnArray[i];			
			if (Character.isDigit(caracter)) 
				sum += Character.getNumericValue(caracter);		
		}
		
		return sum;
	}
	
	private Date obtenerFechaLimite() {
		
		int limite = 15, contador = 1;		
		Calendar calendario = Calendar.getInstance();	
		
		while (contador < limite) {
			
			int diaDeLaSemana = calendario.get(Calendar.DAY_OF_WEEK);						
			if (diaDeLaSemana != Calendar.SUNDAY) contador++;						
			calendario.add(Calendar.DAY_OF_MONTH, 1);										
		}

		return calendario.getTime(); 	
	}
	
}

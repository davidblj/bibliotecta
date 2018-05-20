package dominio;

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

	public void prestar(String isbn) {
		
		boolean prestamoValido = !this.esPrestado(isbn) && !this.esPalindromo(isbn);
		
		if (this.esPrestado(isbn)) {
			throw new PrestamoException(this.EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);
		}
		
		if (this.esPalindromo(isbn)) {
			throw new PrestamoException(this.EL_LIBRO_ES_PALINDROMO);
		}
		
		Libro libro = this.repositorioLibro.obtenerPorIsbn(isbn);
		Prestamo prestamo = new Prestamo(libro);
		this.repositorioPrestamo.agregar(prestamo);			
	}

	public boolean esPrestado(String isbn) {
		
		Libro libro = this.repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn);
		
		if (libro != null) {   // get current date ? 
			return true;
		}
		
		return false;
	}
	
	
	public boolean esPalindromo(String isbn) {
		
		 StringBuilder handler = new StringBuilder(isbn);
		 String reves = handler.reverse().toString();		
		 return reves.equals(isbn);			 
	}

}

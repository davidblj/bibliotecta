package dominio;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

public class Bibliotecario {

	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;

	}

	public void prestar(String isbn) {

		// throw new UnsupportedOperationException("M�todo pendiente por implementar");
		
		if (!this.esPrestado(isbn)) {
			
			Libro libro = this.repositorioLibro.obtenerPorIsbn(isbn);
			Prestamo prestamo = new Prestamo(libro);
			this.repositorioPrestamo.agregar(prestamo);
			
		} else {
			
			throw new PrestamoException(this.EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);
		}
	}

	public boolean esPrestado(String isbn) {
		
		Prestamo prestamo = this.repositorioPrestamo.obtener(isbn);
		
		if (prestamo != null) {
			return true;
		}
		
		return false;
	}

}

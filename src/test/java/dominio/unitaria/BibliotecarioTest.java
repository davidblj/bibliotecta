package dominio.unitaria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import org.h2.util.DateTimeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import testdatabuilder.LibroTestDataBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bibliotecario.class)
public class BibliotecarioTest {

	@Test
	public void esPrestadoTest() {
		
		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();
		
		Libro libro = libroTestDataBuilder.build(); 
		
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		when(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn())).thenReturn(libro);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		// act 
		boolean esPrestado =  bibliotecario.esPrestado(libro.getIsbn());
		
		//assert
		assertTrue(esPrestado);
	}
	
	@Test
	public void libroNoPrestadoTest() {
		
		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();
		
		Libro libro = libroTestDataBuilder.build(); 
		
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		when(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn())).thenReturn(null);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		// act 
		boolean esPrestado =  bibliotecario.esPrestado(libro.getIsbn());
		
		//assert
		assertFalse(esPrestado);
	}
	
	
	@Test
	public void isbnEsPalindromoTest() {
		
		// arrange
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		// act
		boolean esPalindromo = bibliotecario.esPalindromo("4224");
		
		// assert
		assertTrue(esPalindromo);		
	}
	
	@Test
	public void isbnNoEsPalindromoTest() {
		
		// arrange
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
				
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		// act
		boolean noEsPalindromo = bibliotecario.esPalindromo("4144");
		
		// assert
		assertFalse(noEsPalindromo);
	}
	
	
	@Test
	public void fechaMaximaVaciaTest() {
		// arrange
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
				
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		// act
		Date fecha= bibliotecario.obtenerFechaDeEntrega("41A44ZD2");
		
		// assert
		assertNull(fecha);	
	}
	
	@Test
	public void fechaMaximaTest() {
		
		// arrange
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
				
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		Calendar defaultDate = Calendar.getInstance();
		defaultDate.set(2017, Calendar.MAY, 24);
		
		PowerMockito.mockStatic(Calendar.class);
		Mockito.when(Calendar.getInstance()).thenReturn(defaultDate);	
		
		// act
		Date fechaDeEntrega = bibliotecario.obtenerFechaDeEntrega("41A44ZD678");		
							
		// assert
		Calendar fechaEsperadaHelper = Calendar.getInstance();
		fechaEsperadaHelper.set(2017, Calendar.JUNE, 9);
		
		Date fechaEsperada = fechaEsperadaHelper.getTime();		
		
		boolean fechasCoinciden = fechaDeEntrega.getTime() == fechaEsperada.getTime();		
		assertTrue(fechasCoinciden);	
	}
}

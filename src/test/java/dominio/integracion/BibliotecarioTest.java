package dominio.integracion;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.Prestamo;
import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.LibroTestDataBuilder;

public class BibliotecarioTest {

	private static final String CRONICA_DE_UNA_MUERTA_ANUNCIADA = "Cronica de una muerta anunciada";
	private static final String DAVID_JARAMILLO_BOLIVAR = "David Jaramillo Bolivar";
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioLibro repositorioLibros;
	private RepositorioPrestamo repositorioPrestamo;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioLibros = sistemaPersistencia.obtenerRepositorioLibros();
		repositorioPrestamo = sistemaPersistencia.obtenerRepositorioPrestamos();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void prestarLibroTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		repositorioLibros.agregar(libro);
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
				
		// act
		blibliotecario.prestar(libro.getIsbn(), DAVID_JARAMILLO_BOLIVAR);

		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn()));
		
		String usuarioDelPrestamo = repositorioPrestamo.obtener(libro.getIsbn()).getNombreUsuario(); 
		Assert.assertEquals(usuarioDelPrestamo, DAVID_JARAMILLO_BOLIVAR);
	}

	@Test
	public void prestarLibroNoDisponibleTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		repositorioLibros.agregar(libro);	
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		blibliotecario.prestar(libro.getIsbn(), DAVID_JARAMILLO_BOLIVAR);
		try {
			
			blibliotecario.prestar(libro.getIsbn(), DAVID_JARAMILLO_BOLIVAR);
			fail();
			
		} catch (PrestamoException e) {
			// assert
			System.out.println(e.getMessage());
			Assert.assertEquals(Bibliotecario.EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE, e.getMessage());
		}
	}
	
	@Test
	public void prestarLibroDeExhibicionTest() {
		
		// arrange
		Libro libro = new LibroTestDataBuilder()				
				.conIsbn("4224")
				.build();
			
		repositorioLibros.agregar(libro);	
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		try {
			
			// act
			blibliotecario.prestar(libro.getIsbn(), DAVID_JARAMILLO_BOLIVAR);
			fail();
			
		} catch (PrestamoException e) {
			
			// assert
			System.out.println(e.getMessage());
			Assert.assertEquals(Bibliotecario.EL_LIBRO_ES_PALINDROMO, e.getMessage());
		}
	}
	
	@Test
	public void prestarLibroSinFechaLimiteTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder()
				.conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA)
				.conIsbn("A489Z429A").build();
		
		repositorioLibros.agregar(libro);
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
				
		// act
		blibliotecario.prestar(libro.getIsbn(), DAVID_JARAMILLO_BOLIVAR);

		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn()));
		
		Prestamo prestamo = repositorioPrestamo.obtener(libro.getIsbn());
		
		String usuarioDelPrestamo = prestamo.getNombreUsuario(); 
		Assert.assertEquals(usuarioDelPrestamo, DAVID_JARAMILLO_BOLIVAR);
		
		Date fechaLimite = prestamo.getFechaEntregaMaxima();
		Assert.assertNull(fechaLimite);
	}
}

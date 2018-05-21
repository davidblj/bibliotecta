package dominio.integracion;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.Prestamo;
import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.LibroTestDataBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bibliotecario.class)
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
				.conIsbn("A48B").build();
		
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
		
		Date fechaDeEntrega = prestamo.getFechaEntregaMaxima();
		Assert.assertNull(fechaDeEntrega);
	}
	
	
	@Test
	public void prestarLibroConFechaLimiteTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder()
				.conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA)
				.conIsbn("A489Z429A").build();
		
		repositorioLibros.agregar(libro);
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);
		
		Calendar defaultDate = Calendar.getInstance();
		defaultDate.set(2017, Calendar.MAY, 24);
		
		PowerMockito.mockStatic(Calendar.class);
		Mockito.when(Calendar.getInstance()).thenReturn(defaultDate);
				
		// act
		blibliotecario.prestar(libro.getIsbn(), DAVID_JARAMILLO_BOLIVAR);

		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn()));
		
		Prestamo prestamo = repositorioPrestamo.obtener(libro.getIsbn());
		
		String usuarioDelPrestamo = prestamo.getNombreUsuario(); 
		Assert.assertEquals(usuarioDelPrestamo, DAVID_JARAMILLO_BOLIVAR);
		
		Date fechaDeEntrega = prestamo.getFechaEntregaMaxima();		
		Calendar fechaEsperadaHelper = Calendar.getInstance();
		fechaEsperadaHelper.set(2017, Calendar.JUNE, 9);		
		Date fechaEsperada = fechaEsperadaHelper.getTime();		
		
		boolean fechasCoinciden = fechaDeEntrega.getTime() == fechaEsperada.getTime();		
		assertTrue(fechasCoinciden);
	}
}

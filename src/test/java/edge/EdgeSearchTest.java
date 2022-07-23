package edge;

import org.example.CrearReporte;
import org.example.Listado;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe;

public class EdgeSearchTest {

    private List<Listado> listadoList = new ArrayList<>();
    private WebDriver driver;
    private AtomicInteger nroPagina;
    private static final String URL_LISTADO_COMPAÑIAS = "https://mercadodevalores.supercias.gob.ec/reportes/directorioCompanias.jsf";
    private final int CAMPO_IDENTIFICACION = 3;
    private final int CAMPO_NOMBRE = 4;
    private final int CAMPO_EXPEDIENTE = 2;
    private final int PAGINA_LISTADO = 0;
    private final int PAGINA_CONSULTA = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeSearchTest.class);

    @Test
    public void testEdgePage() {

        System.setProperty("webdriver.edge.driver", "./src/main/resources/edgeDriver/msedgedriver.exe");
        //System.setProperty("webdriver.chrome.driver", "./src/main/resources/chromeDriver/chromedriver.exe");
        driver = new EdgeDriver();
        //driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(URL_LISTADO_COMPAÑIAS);

        nroPagina = new AtomicInteger(1);
        //int totalPaginas = 1726;
        int totalPaginas = 2;

        for (int i = 1; i < totalPaginas; i++) {
            System.out.println("Nro Pagina: " + i);
            recorrerLista();
            cambiarPagina();
        }

        CrearReporte crearReporte = new CrearReporte();
        crearReporte.crearExcel(listadoList);

    }

    private void recorrerLista(){

        String identificacion;
        String nombre;

        List<WebElement> rowElements = driver.findElements(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_data\"]/tr"));
        int rowSize = rowElements.size();

        System.out.println("Total registros: " + rowSize);

        for (int i = 1; i <= rowSize; i++) {

            identificacion = driver.findElement(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_data\"]/tr["+i+"]/td["+CAMPO_IDENTIFICACION+"]")).getText();
            nombre = driver.findElement(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_data\"]/tr["+i+"]/td["+CAMPO_NOMBRE+"]")).getText();
            abrir(driver.findElement(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_data\"]/tr["+i+"]/td["+CAMPO_EXPEDIENTE+"]")));
            reidreccionarAPaginaConsulta(identificacion, nombre);

            System.out.println("Registo: " + i +" - " + rowSize);

        }

    }

    private void abrir(WebElement webElement){
        String s = Keys.chord(Keys.CONTROL, Keys.ENTER);
        webElement.findElements(By.tagName("a")).get(0).sendKeys(s);
    }

    private void reidreccionarAPaginaConsulta(String identificacion, String nombre){

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120, 1));
        wait.until(numberOfWindowsToBe(2));

        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(PAGINA_CONSULTA));

        String correo1 = obtenerCorreo(identificacion, nombre, "//*[@id=\"frmInformacionCompanias:j_idt85:j_idt253\"]");
        String correo2 = obtenerCorreo(identificacion, nombre, "//*[@id=\"frmInformacionCompanias:j_idt85:j_idt258\"]");

        listadoList.add(new Listado(identificacion, nombre, correo1, correo2));

        driver.close();

        driver.switchTo().window(tabs.get(PAGINA_LISTADO));

    }

    private void cambiarPagina(){
        driver.findElement(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_paginator_bottom\"]/a[3]")).click();
    }

    private String obtenerCorreo(String identificacion, String nombre, String componente){
        try{
            return driver.findElement(By.xpath(componente)).getAttribute("value");
        }catch (Exception e){
            LOGGER.error("Identificacion: " + identificacion + " Nombre: " + nombre + " - ", e);
            return "";
        }
    }

}

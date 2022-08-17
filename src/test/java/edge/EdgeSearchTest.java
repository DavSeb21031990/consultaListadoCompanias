package edge;

import org.example.CrearReporte;
import org.example.Listado;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe;

public class EdgeSearchTest {

    private final List<Listado> listadoList = new ArrayList<>();
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String URL_LISTADO_COMPAÑIAS = "https://mercadodevalores.supercias.gob.ec/reportes/directorioCompanias.jsf";

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeSearchTest.class);

    @Test
    public void testEdgePage() throws InterruptedException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));

        System.setProperty("webdriver.edge.driver", "./src/main/resources/edgeDriver/msedgedriver.exe");
        //System.setProperty("webdriver.chrome.driver", "./src/main/resources/chromeDriver/chromedriver.exe");
        //System.setProperty("webdriver.gecko.driver", "./src/main/resources/firefoxDriver/geckodriver.exe");
        driver = new EdgeDriver();
        //driver = new ChromeDriver();
        //driver = new FirefoxDriver();
        driver.manage().window().minimize();
        driver.get(URL_LISTADO_COMPAÑIAS);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Select dropdown = new Select(find("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias:j_id20\"]"));
        dropdown.selectByVisibleText("1000");

        Thread.sleep(5000);

        AtomicInteger nroPagina = new AtomicInteger(1);
        int totalPaginas = 9;
        int paginaInicio = 8;

        try {
            setPaginaInicio(paginaInicio);

            for (int i = paginaInicio; i < totalPaginas; i++) {
                System.out.println("Nro Pagina: " + i);
                recorrerLista();
                cambiarPagina();
                Thread.sleep(5000);
            }
        } catch (Exception e) {

            e.printStackTrace();
            CrearReporte crearReporte = new CrearReporte();
            crearReporte.crearExcel(listadoList, paginaInicio);

            dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            System.out.println(dateFormat.format(cal));

        }

        CrearReporte crearReporte = new CrearReporte();
        crearReporte.crearExcel(listadoList, paginaInicio);

        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        date = new Date();
        System.out.println(dateFormat.format(date));

    }

    private WebElement find(String locator){
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
    }

    private void recorrerLista() throws InterruptedException {

        String id;
        String identificacion;
        String nombre;

        List<WebElement> rowElements = driver.findElements(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_data\"]/tr"));
        int rowSize = rowElements.size();

        System.out.println("Total registros: " + rowSize);

        for (int i = 1; i <= rowSize; i++) {

            id = driver.findElement(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_data\"]/tr[" + i + "]/td[" + 2 + "]")).getText();
            identificacion = driver.findElement(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_data\"]/tr[" + i + "]/td[" + 3 + "]")).getText();
            nombre = driver.findElement(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_data\"]/tr[" + i + "]/td[" + 4 + "]")).getText();
            abrir(driver.findElement(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_data\"]/tr[" + i + "]/td[" + 2 + "]")));
            reidreccionarAPaginaConsulta(id, identificacion, nombre);

            System.out.println("Registo: " + i + " - " + rowSize);

        }

    }

    private void abrir(WebElement webElement) {
        String s = Keys.chord(Keys.CONTROL, Keys.ENTER);
        webElement.findElements(By.tagName("a")).get(0).sendKeys(s);
    }

    private void reidreccionarAPaginaConsulta(String id, String identificacion, String nombre) throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120, 1));
        wait.until(numberOfWindowsToBe(2));

        Thread.sleep(4000);

        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        int PAGINA_CONSULTA = 1;
        driver.switchTo().window(tabs.get(PAGINA_CONSULTA));

        String correo1 = obtenerCorreo(identificacion, nombre, "//*[@id=\"frmInformacionCompanias:j_idt85:j_idt253\"]");
        String correo2 = obtenerCorreo(identificacion, nombre, "//*[@id=\"frmInformacionCompanias:j_idt85:j_idt258\"]");

        //String selector = "#frmInformacionCompanias\\:j_idt106\\:j_idt373";
        String selector = "#frmInformacionCompanias\\:j_idt85\\:j_idt253";
        String selector1 = "#frmInformacionCompanias\\:j_idt85\\:j_idt258";

        correo1 = obtenerCorreo(identificacion, nombre, selector);
        correo2 = obtenerCorreo(identificacion, nombre, selector1);

        Listado listado = new Listado(identificacion, nombre, correo1, correo2);

        System.out.println(listado.toString());

        listadoList.add(listado);

        //agregarLinea(id, listado);

        driver.close();

        int PAGINA_LISTADO = 0;
        driver.switchTo().window(tabs.get(PAGINA_LISTADO));

    }

    private void setPaginaInicio(int paginaInicio) throws InterruptedException {

        for (int i = 1; i < paginaInicio; i++) {
            cambiarPagina();
            Thread.sleep(4000);
        }

    }

    private void cambiarPagina() {
        WebElement webElement = driver.findElements(By.xpath("//*[@id=\"j_id404092557_1815f626:tblDirectorioCompanias_paginator_bottom\"]/a[3]")).get(0);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click()", webElement);
    }

    private String obtenerCorreo(String identificacion, String nombre, String componente) {
        try {
            //return driver.findElement(By.xpath(componente)).getAttribute("value");
            return driver.findElement(By.cssSelector(componente)).getAttribute("value");
        } catch (Exception e) {
            LOGGER.error("Identificacion: " + identificacion + " Nombre: " + nombre + " - ", e);
            return "";
        }
    }

}

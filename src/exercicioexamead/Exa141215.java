package exercicioexamead;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class Exa141215 {

     public static Connection conexion=null;
    
     public static Connection getConexion() throws SQLException  {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost"; // tambien puede ser una ip como "192.168.1.14"
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;
            conexion = DriverManager.getConnection(ulrjdbc);
            return conexion;
        }

    public static void closeConexion() throws SQLException {
        conexion.close();
    }

    /**
     * Imprime os valores correspondentes a cada pedido gardados no XML e chama os metodos
     * stockPro(), gastoCli() e isnsireVentas()
     */
    public void leeXML() {
        int cantidade = 0;
        String data = "";
        String codCli = "";
        String codPro = "";
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader read = factory.createXMLStreamReader(new FileReader("pedidos.xml"));

            while (read.hasNext()) {

                if (read.getEventType() == XMLStreamConstants.START_ELEMENT) {

                    if (read.getLocalName().equals("Pedido")) {
                        codCli = read.getAttributeValue(0);
                        codPro = read.getAttributeValue(1);
                        System.out.println("Datos pedido:\n" + "Código del cliente: " + read.getAttributeValue(0)
                                + "\nCódigo del producto: " + read.getAttributeValue(1));
                    }

                    if (read.getLocalName().equals("Cantidade")) {
                        cantidade = Integer.parseInt(read.getElementText());
                        System.out.println("Cantidade: " + cantidade);
                        stockPro(codPro, cantidade);
                    }

                    if (read.getLocalName().equals("Data")) {
                        data = read.getElementText();
                        System.out.println("Data: " + data);
                        insireVentas(codCli, codPro, data, cantidade);
                    }
                }

                gastoCli(codCli);
                read.next();

                conexion.setAutoCommit(true);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Fai unha consulta a BD e con ela calcula o stock total de cada produto e
     * a modifica
     *
     * @param codPro Codigo do producto recibido do arquivo XML
     * @param cantidade Cantidade de produtos recibido do arquivo XML
     * @throws SQLException
     */
    public void stockPro(String codPro, int cantidade) throws SQLException {
        int stockProd = 0;
        String buscaStock = "SELECT stock FROM produtos WHERE codigop = '" + codPro + "'";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(buscaStock);
        while (rs.next()) {
            stockProd = rs.getInt(1);
            stockProd = stockProd - cantidade;

            try {
                String update = "UPDATE produtos set stock =" + stockProd + " WHERE codigop = '" + codPro + "'";
                PreparedStatement stUp = conexion.prepareStatement(update);
                stUp.executeUpdate();
                System.out.println("Taboa produtos modificada");
                conexion.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println("Non se pode modificar a taboa produtos");
            }
        }
    }

    /**
     * Suma o gasto total de cada cliente e modifica o campo gasto da taboa
     * clientes
     *
     * @param codCli Recibe o codigo do cliente lido no XML
     * @throws SQLException
     */
    public void gastoCli(String codCli) throws SQLException {

        String buscaGasto = "SELECT sum(total) FROM vendas WHERE codigoc = '" + codCli + "'";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(buscaGasto);

        while (rs.next()) {
            int total = rs.getInt(1);

            try {
                String update = "UPDATE clientes set gasto = " + total + " WHERE codigoc = '" + codCli + "'";
                PreparedStatement stUp = conexion.prepareStatement(update);
                stUp.executeUpdate();
                System.out.println("Taboa clientes modificada");
                conexion.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println("Non se pode modificar a taboa clientes");
            }
        }
    }

    /**
     * Calcula a cantidade total e insire todos os datos na taboa vendas
     *
     * @param codCli Codigo do cliente sacado do XML
     * @param codPro Codigo do produto sacado do XML
     * @param data Data de compra sacado do XML
     * @param cantidade Cantidade comprada sacada do XML
     * @throws SQLException
     */
    public void insireVentas(String codCli, String codPro, String data, int cantidade) throws SQLException {

        String buscaStock = "SELECT prezo FROM produtos WHERE codigop = '" + codPro + "'";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(buscaStock);
        while (rs.next()) {
            int total = rs.getInt(1);
            total = cantidade * total;

            try {
                String insert = "INSERT INTO vendas VALUES(?,?,?,?)";
                PreparedStatement ps = conexion.prepareStatement(insert);
                ps.setString(1, codCli);
                ps.setString(2, codPro);
                ps.setString(3, data);
                ps.setInt(4, total);
                ps.executeUpdate();

                System.out.println("Venta inserida");
                conexion.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println("Non se pode inserir a venta");
            }
        }
    }

    /**
     * Mostra as tres taboas despois das modificacions feitas anteriormente
     */
    public void verTaboas() {
        try {
            PreparedStatement st = conexion.prepareStatement("SELECT * FROM clientes");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                System.out.println("CodigoC: " + rs.getString(1)
                        + ", NomeC: " + rs.getString(2)
                        + ", DIRec: " + rs.getString(3)
                        + ", NomeC: " + rs.getInt(4));
            }
        } catch (SQLException ex) {
            System.out.println("Non se pode mostrar a taboa clientes");
        }
        try {
            PreparedStatement st = conexion.prepareStatement("SELECT * FROM produtos");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                System.out.println("CodigoP: " + rs.getString(1)
                        + ", NomeP: " + rs.getString(2)
                        + ", Prezo: " + rs.getInt(3)
                        + ", Stock: " + rs.getInt(4));
            }
        } catch (SQLException ex) {
            System.out.println("Non se pode mostrar a taboa produtos");
        }
        try {
            PreparedStatement st = conexion.prepareStatement("SELECT * FROM vendas");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                System.out.println("CodigoC: " + rs.getString(1)
                        + ", CodigoP: " + rs.getString(2)
                        + ", Data: " + rs.getString(3)
                        + ", Total: " + rs.getInt(4));
            }
        } catch (SQLException ex) {
            System.out.println("Non se pode mostrar a taboa vendas");
        }
    }

    public static void main(String[] args) throws FileNotFoundException, XMLStreamException, SQLException {

        Exa141215 e = new Exa141215();
        e.getConexion();
        e.leeXML();
        e.verTaboas();
        e.closeConexion();

    }

}

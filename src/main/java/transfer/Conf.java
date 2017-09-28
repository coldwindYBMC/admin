package transfer;

import java.io.IOException;
import java.util.Properties;

public class Conf {
    
    public String host = "192.168.1.14";
    public String port = "3306";
    public String user = "root";
    public String pass = "root";
    public String name = "db_templates_luzj_001";
    
    public String transferTablesFile;
    public String ignoredColumnsFile;
    
    public static Conf parseFrom(Properties pp) throws IOException {
        Conf conf = new Conf();
        conf.host = pp.getProperty("host");
        conf.port = pp.getProperty("port");
        conf.user = pp.getProperty("user");
        conf.pass = pp.getProperty("pass");
        conf.name = pp.getProperty("name");
        
        conf.transferTablesFile = pp.getProperty("transfer.tables");
        conf.ignoredColumnsFile = pp.getProperty("ignored.columns");

        Environment.parseFrom(conf);
        
        return conf;
    }

	public static Conf parseFrom(Properties pp, String s) throws IOException {
		Conf conf = new Conf();
        conf.host = pp.getProperty("host");
        conf.port = pp.getProperty("port");
        conf.user = pp.getProperty("user");
        conf.pass = pp.getProperty("pass");
        conf.name = pp.getProperty("name");
        
        conf.transferTablesFile = s+"transfer.tables";
        conf.ignoredColumnsFile = s+"ignored.columns";

        Environment.parseFrom(conf);
        
        return conf;
	}
}

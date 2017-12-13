package cn.model;

public class DBConf {
	public final String host;//主机
	public final int port;	//端口
	public final String name;//模板数据库名
	public final String user;//用户
	public final String pass;//密码
	public final int minconn;
	public final int maxconn;
//	private static final int default_max_conn = 1;
//	private static final int default_min_conn = 2;
	public DBConf(String host, int port, String name, String user, String pass) {
		this(host, port, name, user, pass, 2, 1);
	}
	public DBConf(String host, int port, String name, String user, String pass, int minconn, int maxconn) {
		this.host = host;	
		this.port = port;	
		this.name = name;
		this.user = user;
		this.pass = pass;
		this.minconn = minconn;
		this.maxconn = maxconn;
	}
}

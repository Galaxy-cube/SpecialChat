package cc.specialchat.specialchatserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database:    msg_cache.db
 * table:       msg_cache
 * columns:
 *      msg_index       INTEGER,primary key,autoincrement   //index
 *      from_id         INTEGER,NOT NULL
 *      to_id           INTEGER,NOT NULL
 *      msg_content     TEXT,NOT NULL
 *      send_time       INTEGER,NOT NULL
 *      is_read         INTEGER     // -1 is not fetch;0 is unread;1 is read
 *
 * */


public class MsgCacheSQLite{
	private static Connection getConnection() throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		//c.setAutoCommit(false);
		return DriverManager.getConnection("jdbc:sqlite:user_info.db");
	}
	
	/**
	 * Table init method, create table user_info
	 * @throws SQLException ...
	 * @throws ClassNotFoundException ...
	 */
	void init() throws SQLException, ClassNotFoundException{
		Connection connection=getConnection();
		Statement statement=connection.createStatement();
		String CREATE_TABLE_SQL=
				"create table msg_cache (" +
						"msg_index INTEGER primary key autoincrement," +
						"from_id INTEGER NOT NULL," +
						"to_id INTEGER NOT NULL," +
						"msg_content TEXT NOT NULL," +
						"send_time INTEGER NOT NULL," +
						"is_read INTEGER" +
						")";
		statement.executeUpdate(CREATE_TABLE_SQL);
		statement.close();
		connection.close();
		System.out.println("---- init table msg_cache done! ----\n");
	}
	
	//todo: insert new message
	static int[] insertNewMsg(String from_id,String to_id,String msg_content){
		try{
			int[] msg_info=new int[2];
			msg_info[0]=MyTools.getCurrentTime();
			Connection con=getConnection();
			Statement st=con.createStatement();
			String INSERT_SQL="insert into msg_cache" +
					"";
			return msg_info;
		}catch(SQLException|ClassNotFoundException e){
			e.printStackTrace();
			return null;
		}
	}
	
	//todo: check whether msg is read by msg_index
	static int checkIsRead(String msg_index){
		return 1;
	}
	
	/**
	 * Fetch new messages by user_id.
	 * @param user_id String
	 * @return String[][] fetched msg and info
	 */
	static String[][] fetchMsg(String user_id){
		try{
			Connection connection=getConnection();
			Statement statement=connection.createStatement();
			
			String QUERY_SQL="select * from msg_cache where to_id="+user_id+";";
			ResultSet resultSet=statement.executeQuery(QUERY_SQL);
			String[][] msg=new String[50][4]; // 50 pieces for max each time.
			int index=0;
			while(resultSet.next() && index<50){
				index++;
				msg[index][0]=resultSet.getInt("msg_index")+"";
				msg[index][1]=resultSet.getInt("from_id")+"";
				msg[index][2]=resultSet.getString("msg_content");
				msg[index][3]=resultSet.getInt("send_time")+"";
				//todo: update is_read;
			}
			msg[0][0]=index+"";
			return msg;
		}catch(SQLException|ClassNotFoundException e){
			e.printStackTrace();
			return null;
		}
	}
}

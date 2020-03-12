package cc0x29a.specialchat;

/*

  author:  Zhong Wenliang
  mail:    cuberwenliang@163.com
  date:    March, 2020
 
  **/

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
	
	static Timer checkLoginTimer;
	static Timer refreshMsgTimer;
	
	Handler toastHandler=new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// todo: this can set a lunch page !!
		setContentView(R.layout.activity_main);
		
		
		//test code
		ChatListSQLiteHelper cp=new ChatListSQLiteHelper(this,"chat_list.db",1);
		//cp.insertNewChatListItem(cp.getReadableDatabase(),MyTools.getRandomNum(12000,2),"ha pi",123);
		//test code
		init();
	}
	
	//todo what about this??
	
	// todo： this still empty
	@Override
	protected void onStart(){
		super.onStart();
		//redirect();
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		normalMode();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		cancelRefreshTimers();
	}
	
	//todo
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.main_top_bar, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	//todo complete this
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.app_bar_search:
				Toast.makeText(this, "Search! ", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.app_bar_settings:
				Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
				return true;
			case R.id.app_bar_about:
				Toast.makeText(this,"Special Chat-1.0! \n" +
						"Developed by Zhong Wenliang. \n" +
						"Email: CuberWenliang@0x29a.cc",Toast.LENGTH_LONG).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	// todo : edit the text on the right(完善注释)    next method -> normalMode()
	private void redirect(){
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		if(preferences.getInt("is_login",0)!=1){
			changeView(1);
		}else if(preferences.getInt("is_login",0)==1){
			normalMode();
		}
	}
	
	/**
	 * init views
	 */
	private void init(){
		LinearLayout main_linear_layout=findViewById(R.id.main_linear_layout);
		int width=MyTools.getViewHeight(main_linear_layout,false);
//		int height=MyTools.getViewHeight(main_linear_layout,true);
		{
			LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)findViewById(R.id.menu_btn_chats).getLayoutParams();
			params.width=width/4;
			findViewById(R.id.menu_btn_chats).setLayoutParams(params);
			params=(LinearLayout.LayoutParams)findViewById(R.id.menu_btn_contacts).getLayoutParams();
			params.width=width/4;
			findViewById(R.id.menu_btn_contacts).setLayoutParams(params);
			params=(LinearLayout.LayoutParams)findViewById(R.id.menu_btn_moments).getLayoutParams();
			params.width=width/4;
			findViewById(R.id.menu_btn_moments).setLayoutParams(params);
			params=(LinearLayout.LayoutParams)findViewById(R.id.menu_btn_me).getLayoutParams();
			params.width=width/4;
			findViewById(R.id.menu_btn_me).setLayoutParams(params);
		}
		{
			findViewById(R.id.menu_btn_chats).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					findViewById(R.id.main_chats_listView).setVisibility(View.VISIBLE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					loadChatList();
				}
			});
			findViewById(R.id.menu_btn_contacts).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					findViewById(R.id.main_chats_listView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.VISIBLE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					loadContactsList();
				}
			});
			findViewById(R.id.menu_btn_moments).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					findViewById(R.id.main_chats_listView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.VISIBLE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					//todo load moments
				}
			});
			findViewById(R.id.menu_btn_me).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					findViewById(R.id.main_chats_listView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.VISIBLE);
					//todo load 'me'
				}
			});
		}
	}
	
	// todo complete
	/**
	 * Normal mode perform.
	 */
	private void normalMode(){
		cancelRefreshTimers();
		
		// set timer tasks
		checkLoginTimer=new Timer();
		refreshMsgTimer=new Timer();
		
		// Check login status per 2 minutes.
		checkLoginTimer.schedule(new TimerTask(){
			@Override public void run(){
				try{
					if(checkLogin()==2){
						changeView(1);
					}else if(checkLogin()==1){
						showToast("Ohh! Poor Network... :(",Toast.LENGTH_LONG);
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		},17,120000);
		
		// Refresh new message(s) per 5.888 seconds.
		refreshMsgTimer.schedule(new TimerTask(){
			@Override
			public void run(){
				try{
					if(refreshNewMsg()==1){
						// if network is not so fine...
						// 套娃就很皮..哈哈
						showToast("!Poor Network... :(",Toast.LENGTH_SHORT);
						refreshMsgTimer.cancel();
						refreshMsgTimer=new Timer();
						refreshMsgTimer.schedule(new TimerTask(){
							@Override
							public void run(){
								try{
									if(refreshNewMsg()==1){
										showToast("): ...Network Poor!",Toast.LENGTH_LONG);
									}else{
										normalMode();
									}
								}catch(JSONException e){
									e.printStackTrace();
								}
							}
						},1700,23333);
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		},1700,5888);
		
		loadChatList();
	}
	
	/**
	 * Load Chat list ListView by Adapter
	 * */
	private void loadChatList(){
		ChatListSQLiteHelper chatListSQLiteHelper=
				new ChatListSQLiteHelper(MainActivity.this,"chat_list.db",1);
		final String[][] chatList=chatListSQLiteHelper.fetchChatList(chatListSQLiteHelper.getReadableDatabase());
		
		// Fetch last one message.
		String[] lastMsg=new String[50];
		for(int i=1;i<= (Integer.parseInt(chatList[0][0])) && i<=50;i++){
			MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(MainActivity.this,
					"msg_"+chatList[i][1]+".db",1);
			lastMsg[i]=msgSQLiteHelper.getLastMsg(msgSQLiteHelper.getReadableDatabase());
		}
		
		ListView ml_view=findViewById(R.id.main_chats_listView);
		ChatListItemAdapter cli_adapter=new ChatListItemAdapter(MainActivity.this);
		cli_adapter.chatListInfo=chatList;
		cli_adapter.lastMsg=lastMsg;
		cli_adapter.count=Integer.parseInt(chatList[0][0]); // item 数量
		ml_view.setAdapter(cli_adapter);
		
		ml_view.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent,View view,int position,long id){
				position++;
				Intent intent=new Intent(MainActivity.this,ChatActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("user_id", chatList[position][1]);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		
		ml_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
				position++;
				// todo: position (int) ,open a little menu,to delete chat or so on.
				return true;
			}
		});
	}
	
	/**
	 * Load Contacts List ListView by Adapter
	 */
	private void loadContactsList(){
//		MainActivity.this.runOnUiThread(
//			new Runnable(){
//				public void run(){
					ContactsListSQLiteHelper contactsListSQLiteHelper=
							new ContactsListSQLiteHelper(MainActivity.this,"contacts_list.db",1);
					final String[][] contactsList=
							contactsListSQLiteHelper.fetchContactsList(contactsListSQLiteHelper.getReadableDatabase());
					
					ListView contacts_listView=findViewById(R.id.main_contacts_listView);
					ContactsListItemAdapter contactsListItemAdapter=
							new ContactsListItemAdapter(MainActivity.this);
					contactsListItemAdapter.contactsInfo=contactsList;
					contactsListItemAdapter.count=Integer.parseInt(contactsList[0][0]);
					contacts_listView.setAdapter(contactsListItemAdapter);
					
					contacts_listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
						@Override
						public void onItemClick(AdapterView<?> parent,View view,int position,long id){
							position++;
							//todo open contacts details page
						}
					});
					
					contacts_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
							position++;
							// todo: at next ver. , add new function here. (a little menu?)
							return true;
						}
					});
//				}
//			}
//		);
	}
	
	/**
	 * Refresh New Message(s)
	 * send{
	 *     client:SCC-1.0,
	 *     action:0003,
	 *     user_id:[user_id],
	 *     token_key:[token_key]
	 * }
	 *
	 * return{
	 *     is_new_msg:[true|false],
	 *     new_msg_num:[new_message_number],    // 50 pieces MAX !
	 *     // below data sort by time, the oldest on top !!
	 *     index_1:{      //里面用单引号！！
	 *         user_id:[user_id],
	 *         send_time:[send_time],
	 *         msg_content:[msg_content]
	 *     }
	 *     index_2:{
	 *         user_id:[user_id],
	 *         send_time:[send_time],
	 *         msg_content:[msg_content]
	 *     }
	 *     index_...:{
	 *         ...
	 *     }
	 *     ...
	 * }
	 *
	 * @return int,
	 *      0->Have new msg
	 *      1->Network error
	 *      2->No new msg
	 *      3->Unknown Error..
	 *
	 * Todo: this can be optimized !! (1.combine msg, 2. filter some char!)
 	 */
	private int refreshNewMsg() throws JSONException{
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		String user_id=preferences.getString("user_id",null);
		String token_key=preferences.getString("token_key",null);
		
		String jsonMsg;
		if(user_id != null && token_key != null){
			jsonMsg="{" +
					"\"client\":\"SCC-1.0\"," +
					"\"action\":\"0003\"," +
					"\"user_id\":\""+MyTools.filterSpecialChar(user_id)+"\"," +
					"\"token_key\":\""+MyTools.filterSpecialChar(token_key)+"\"," +
					"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
					"}";
		}else{
			jsonMsg="{}";
		}
		SocketWithServer SWS=new SocketWithServer();
		SWS.DataSend=jsonMsg;
		SWS.startSocket();
		
		if(SWS.DataJsonReturn==null){
			System.out.println("Network ERROR! ");
			return 1;
		}else if(SWS.DataJsonReturn.getString("is_new_msg").equals("true")){
			int new_msg_num=Integer.parseInt(SWS.DataJsonReturn.getString("new_msg_num"));
			for(int i=1;i<=new_msg_num;i++){
				JSONObject jsonTemp=SWS.DataJsonReturn.getJSONObject(SWS.DataJsonReturn.getString("index_"+i));
				int friend_id=Integer.parseInt(jsonTemp.getString("user_id"));
				int send_time=Integer.parseInt(jsonTemp.getString("send_time"));
				
				MsgSQLiteHelper mh=new MsgSQLiteHelper(MainActivity.this,"msg_"+friend_id+".db",1);
				mh.insertNewMsg(mh.getReadableDatabase(),friend_id,0,send_time,jsonTemp.getString("msg_content"));
				
				ChatListSQLiteHelper clh=new ChatListSQLiteHelper(MainActivity.this,"chat_list.db",1);
				clh.updateChatList(clh.getReadableDatabase(),friend_id,send_time);
			}
			loadChatList();
			return 0;
		}else if(SWS.DataJsonReturn.getString("is_new_msg").equals("false")){
			return 2;
		}else{
			return 3;
		}
	}
	
	/**
	 * Check login status.
	 * @return int 0->good;1->network;2->bad.
	 *
	 * Server return:
	 *      {"status":"true"|"false"}
	 * **/
	private int checkLogin() throws JSONException{
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		String user_id=preferences.getString("user_id",null);
		String token_key=preferences.getString("token_key",null);
		
		String jsonMsg="{" +
				"\"client\":\"SCC-1.0\"," +
				"\"action\":\"0001\"," +
				"\"user_id\":\""+user_id+"\"," +
				"\"token_key\":\""+token_key+"\"," +
				"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
				"}";
		SocketWithServer SWS=new SocketWithServer();
		SWS.DataSend=jsonMsg;
		SWS.startSocket();
		
		if(SWS.DataJsonReturn==null){
			return 1;
		}else if(SWS.DataJsonReturn.getString("status").equals("true")){
			if(preferences.getInt("is_login",0)!=1){
				SharedPreferences.Editor editor=preferences.edit();
				editor.putInt("is_login",1);
				editor.apply();
			}
			return 0;
		}else{
			SharedPreferences.Editor editor=preferences.edit();
			editor.putInt("is_login",0);
			editor.apply();
			return 2;
		}
	}
	
	/**
	 * Cancel unused timers.
	 */
	private void cancelRefreshTimers(){
		if(checkLoginTimer!=null){checkLoginTimer.cancel();}
		if(refreshMsgTimer!=null){refreshMsgTimer.cancel();}
	}
	
	/**
	 * Change content layout
	 * @param toPage
	 *        0 -> main page
	 *        1 -> font login page
	 * **/
	private void changeView(final int toPage){
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				if(toPage==0){ //main page
					findViewById(R.id.font_login_linear_layout).setVisibility(View.GONE);
					findViewById(R.id.main_linear_layout).setVisibility(View.VISIBLE);
				}else if(toPage==1){ //login
					findViewById(R.id.font_login_linear_layout).setVisibility(View.VISIBLE);
					findViewById(R.id.main_linear_layout).setVisibility(View.GONE);
					findViewById(R.id.btn_front_login).setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View v){
							goToLogin();
						}
					});
					cancelRefreshTimers();
					Toast.makeText(MainActivity.this,"Maybe you haven't login yet? ",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	/**
	 * Start LoginActivity
	 */
	private void goToLogin(){
		cancelRefreshTimers();
		startActivity(new Intent(MainActivity.this,LoginActivity.class));
	}
	
	/**
	 * make a simple toast
	 * @param info information to show
	 */
	public void showToast(final String info,final int duration) {
		toastHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), info,
						+duration).show();
			}
		});
	}
	
}


/*
* 		RSATools RSAT=new RSATools();
		byte[] raw_data=new byte[]{9,12,3};
		byte[] encrypted=RSAT.encryptData(raw_data);
		byte[] decrypted=RSAT.decryptData(encrypted);
		System.out.println("raw_data:"+raw_data+"\nencrypted:"+encrypted);
		System.out.println("\ndecrypted:"+decrypted);
* */
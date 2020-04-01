package cc0x29a.specialchat;

/*
* MainActivity.class
*
* Author:       Zhong Wenliang
* mail:         CuberWenliang@0x29a.cc
* start date:   March, 2020
*
* */


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
	
	static String user_id;
	static String token_key;
	static String user_name;
	static String user_phone;
	
	MainBroadcastReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		// next ver to do: this can set a lunch page !!
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		
		// check Application upgrade.
		checkAppUpgrade();
		
		// This would run at the very first lunch.
		welcomePage();
		
		// UI views init
		init();
		
		
		//test codes
		
		// nohup java -jar SpecialChatServer.jar > /dev/null 2> log &
		
//		String a="{\"a1\":\"abc1\",\"b\":\"{'c1':'cde1'}\"}";
//		try{
//			JSONObject json=new JSONObject(a);
//			System.out.println(json.getString("a1"));
//			System.out.println(json.getString("b"));
//			String b=json.getString("b");
//			JSONObject json2=new JSONObject(b);
//			System.out.println(json2.getString("c1"));
//		}catch(JSONException e){
//			e.printStackTrace();
//		}
//user_id="12365";
//		Bundle bundle=new Bundle();
//		bundle.putString("login_id",user_id);
//		Intent intent=new Intent(MainActivity.this,LoginActivity.class);
//		intent.putExtras(bundle);
//		startActivity(intent,bundle);
		//		MsgSQLiteHelper h=new MsgSQLiteHelper(this,"msg_1123592075.db",1);
//		for(int i=1;i<=23;i++){
//			h.insertNewMsg(h.getReadableDatabase(),1123592075+"",i+"",i+" I love you.");
//			ChatListSQLiteHelper c=new ChatListSQLiteHelper(this,"chat_list.db",1);
//			c.insertNewChatListItem(c.getReadableDatabase(),"4091"+i,"Little hao","2"+MyTools.getCurrentTime());
			
//			c.insertNewChatListItem(c.getReadableDatabase(),"1123592075","Little hao",""+MyTools.getCurrentTime());
//		}
//		ContactListSQLiteHelper c=new ContactListSQLiteHelper(MainActivity.this,"contact_list.db",1);
//		c.insertNewContact(c.getReadableDatabase(),"1123592075","Apple2","Haaaa pi","13360417480");
//		String[][] a=new String[][]{{"a","b","a","s"},{"a","b","c"},{"a","b","c"}};
//		System.out.println(a.length);
//		finish();
		//test codes end
		
		
	}
	
	
	@Override
	protected void onRestart(){
		super.onRestart();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		// choose whether to redirect page
		
		//todo test
		redirect();
		
		normalMode();
		
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		user_id=preferences.getString("user_id",null);
		token_key=preferences.getString("token_key",null);
		user_name=preferences.getString("user_name",null);
		user_phone=preferences.getString("user_phone",null);
		 
		// listen to messages from background task service
		receiver= new MainBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("backgroundTask.action");
		registerReceiver(receiver, filter);
		
	}
	
	// stop background tasks service
	@Override
	protected void onStop(){
		super.onStop();
	}
	
	// clear timers & stop background tasks service
	@Override
	protected void onDestroy(){
		super.onDestroy();
		cancelRefreshTimers();
		stopService(new Intent(this,BackgroundTaskService.class));
		try{
			unregisterReceiver(receiver);
		}catch(Exception e){
			//
		}
	}
	
	// Communicate with BackgroundTaskService.
	public class MainBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent) {
			String intentAction = intent.getAction();
			if(null!=intentAction && intentAction.equals("backgroundTask.action")){
				if("reLoadChatList".equals(intent.getStringExtra("todo_action"))){
					reloadChatList();
				}else if("reLoadContactList".equals(intent.getStringExtra("todo_action"))){
					loadContactList();
				}
			}
		}
	}
	
	/**
	 * Load Welcome page at the first run of app.
	 */
	private void welcomePage(){
		SharedPreferences preferences=getSharedPreferences("init_info",MODE_PRIVATE);
		String is_firstRun=preferences.getString("first_run","yes");
		
		if(is_firstRun.equals("yes")){
			startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
			finish();
		}
	}
	
	/**
	 * Check Application upgrade
	 */
	private void checkAppUpgrade(){
		//todo check app upgrade.
	}
	
	/**
	 * init views & some settings
	 */
	private void init(){
		loadChatList();
		
		// init title bar
		{
			// show or hide top_menu
			findViewById(R.id.main_menu_btn).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					LinearLayout top_menu=findViewById(R.id.main_top_menu_cover);
					if(top_menu.getVisibility()==View.GONE){
						Animation animation=AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_top_bar_show);
						top_menu.setAnimation(animation);
						top_menu.setVisibility(View.VISIBLE);
					}else{
						Animation animation=AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_top_bar_hide);
						top_menu.setAnimation(animation);
						top_menu.setVisibility(View.GONE);
					}
				}
			});
			
			View.OnClickListener top_menu_listener=new View.OnClickListener(){
				@Override
				public void onClick(View v){
					switch(v.getId()){
						case R.id.main_menu_add_contact:
							startActivity(new Intent(MainActivity.this,SearchNewContact.class));
							cancelRefreshTimers();
							break;
						case R.id.main_menu_stop_refresh:
							cancelRefreshTimers();
							stopService(new Intent(MainActivity.this,BackgroundTaskService.class));
							stopService(new Intent(MainActivity.this,NetworkService.class));
							unregisterReceiver(receiver);
							Toast.makeText(MainActivity.this,"Auto refresh stopped.",Toast.LENGTH_LONG).show();
							break;
						case R.id.main_menu_about:
							startActivity(new Intent(MainActivity.this,AboutActivity.class));
							break;
						default:
							Toast.makeText(MainActivity.this,"Error occur in Menu bar!",Toast.LENGTH_SHORT).show();
							break;
					}
					LinearLayout top_menu=findViewById(R.id.main_top_menu_cover);
					Animation animation=AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_top_bar_hide);
					top_menu.setAnimation(animation);
					top_menu.setVisibility(View.GONE);
				}
			};
			// bind.
			findViewById(R.id.main_menu_add_contact).setOnClickListener(top_menu_listener);
			findViewById(R.id.main_menu_stop_refresh).setOnClickListener(top_menu_listener);
			findViewById(R.id.main_menu_about).setOnClickListener(top_menu_listener);
		}
		
		// click main_linear_layout to hide top_menu
		{
			findViewById(R.id.main_top_menu_cover).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					LinearLayout top_menu=findViewById(R.id.main_top_menu_cover);
					if(top_menu.getVisibility()!=View.GONE){
						Animation animation=AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_top_bar_hide);
						top_menu.setAnimation(animation);
						top_menu.setVisibility(View.GONE);
					}
				}
			});
		}
		
		// init bottom_menu buttons
		{
			findViewById(R.id.menu_btn_chats).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					setMainTitleBarVisibility(1);
					setTitle("Special Chat");
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.VISIBLE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					new Thread(){
						@Override
						public void run(){
							super.run();
							reloadChatList();
						}
					}.start();
					
				}
			});
			findViewById(R.id.menu_btn_contacts).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					setMainTitleBarVisibility(1);
					setTitle("Contacts");
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.VISIBLE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					new Thread(){
						@Override
						public void run(){
							super.run();
							loadContactList();
						}
					}.start();
				}
			});
			findViewById(R.id.menu_btn_moments).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					setMainTitleBarVisibility(1);
					setTitle("Moments");
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.VISIBLE);
					findViewById(R.id.main_me).setVisibility(View.GONE);
					// next ver
				}
			});
			findViewById(R.id.menu_btn_me).setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					setMainTitleBarVisibility(0);
					setTitle("Me");
					findViewById(R.id.main_chat_recyclerView).setVisibility(View.GONE);
					findViewById(R.id.main_contacts).setVisibility(View.GONE);
					findViewById(R.id.main_moments).setVisibility(View.GONE);
					findViewById(R.id.main_me).setVisibility(View.VISIBLE);
					
					loadMePage();
				}
			});
		}
		
	}
	
	/**
	 * Set title bar text
	 * @param text title
	 */
	private void setTitle(String text){
		TextView main_title=findViewById(R.id.main_title);
		main_title.setText(text);
	}
	
	/**
	 * Set Main Title Bar Visibility
	 * 0 -> Gone
	 * 1 -> Visible
	 * @param visibility Integer
	 */
	private void setMainTitleBarVisibility(int visibility){
		LinearLayout MainTitleBar = findViewById(R.id.main_title_bar);
		if(visibility==0){
			if(MainTitleBar.getVisibility()==View.GONE){
				return;
			}
			Animation animation=AnimationUtils.loadAnimation(this,R.anim.anim_title_bar_hide);
			MainTitleBar.setAnimation(animation);
			MainTitleBar.setVisibility(View.GONE);
		}else{
			if(MainTitleBar.getVisibility()==View.VISIBLE){
				return;
			}
			Animation animation=AnimationUtils.loadAnimation(this,R.anim.anim_title_bar_show);
			MainTitleBar.setAnimation(animation);
			MainTitleBar.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Whether redirect to front login page
 	 */
	private void redirect(){
		SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
		if(preferences.getInt("is_login",0)!=1){
			changeViewToFontLogin();
		}
	}
	
	// def 2 Timer(s)
	static Timer checkLoginTimer;
	/**
	 * Normal mode perform.
	 */
	private void normalMode(){
		startService(new Intent(this,BackgroundTaskService.class));
		startService(new Intent(this,NetworkService.class));
		
		// to clear timers in front
		cancelRefreshTimers();
		
		// set (or reset) timer tasks
		checkLoginTimer=new Timer();
		
		// Check login status per 2 minutes.
		checkLoginTimer.schedule(new TimerTask(){
			@Override public void run(){
				try{
					int status=checkLogin();
					if(status==2){
						changeViewToFontLogin();
					}
//					else if(status==1){
//						//MyTools.showToast(MainActivity.this,"Ohh! Poor Network... :(",Toast.LENGTH_LONG);
//					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		},17,90000);
		
	}
	
	static ChatListItemAdapter adapterChatList;
	/**
	 * Load Chat list ListView by Adapter
	 * */
	void loadChatList(){
		MainActivity.this.runOnUiThread(
			new Runnable(){
				public void run(){
					ChatListSQLiteHelper chatListSQLiteHelper=
							new ChatListSQLiteHelper(MainActivity.this,"chat_list.db",1);
					
					/*
					* chatList[0][0]    -> total number
					* chatList[index][0] -> index (index>0)
					* chatList[index][1] -> user_id
					* chatList[index][2] -> nickname
					* chatList[index][3] -> last_chat_time
					 * */
					
					final List<String[]> chatList=chatListSQLiteHelper.fetchChatList(chatListSQLiteHelper.getReadableDatabase());
					
					final RecyclerView chatList_recycleView=findViewById(R.id.main_chat_recyclerView);
					LinearLayoutManager layoutManager=new LinearLayoutManager(MainActivity.this);
					chatList_recycleView.setLayoutManager(layoutManager);
					
					adapterChatList=new ChatListItemAdapter(chatList);
					adapterChatList.count=chatList.size();
					
					chatList_recycleView.setAdapter(adapterChatList);
					chatList_recycleView.setItemAnimator(new DefaultItemAnimator());
					
					// Fetch last one message.
//					String[] lastMsg=new String[51];
//					for(int i=1;i<= (Integer.parseInt(chatList[0][0])) && i<=50;i++){
//						MsgSQLiteHelper msgSQLiteHelper=new MsgSQLiteHelper(MainActivity.this,
//								"msg_"+chatList[i][1]+".db",1);
//						lastMsg[i]=msgSQLiteHelper.getLatestMsg(msgSQLiteHelper.getReadableDatabase());
//					}
					
					/*
					ChatListItemAdapter cli_adapter=new ChatListItemAdapter(MainActivity.this);
					cli_adapter.chatListInfo=chatList;
					cli_adapter.count=Integer.parseInt(chatList[0][0]); // item number
					
					final ListView ml_view=findViewById(R.id.main_chat_recyclerView);
					ml_view.setAdapter(cli_adapter);
					
					ml_view.setOnItemClickListener(new AdapterView.OnItemClickListener(){
						@Override
						public void onItemClick(AdapterView<?> parent,View view,int position,long id){
							position++;
							Intent intent=new Intent(MainActivity.this,ChatActivity.class);
							Bundle bundle=new Bundle();
							bundle.putString("user_id", chatList[position][1]);
							bundle.putString("nickname",chatList[position][2]);
							intent.putExtras(bundle);
							startActivity(intent);
							cancelRefreshTimers();
						}
					});
					
					ml_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
							position++;
							final int finalPosition=position;
							AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this)
									.setTitle("Notices")
									.setMessage("Sure to delete this chat? \n'"+chatList[position][1]+"'")
									.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {
											//to do delete the chat item! by position
											ChatListSQLiteHelper chatListSQLiteHelper=
													new ChatListSQLiteHelper(MainActivity.this,"chat_list.db",1);
											chatListSQLiteHelper.deleteChatListItem(chatListSQLiteHelper.getReadableDatabase(),chatList[finalPosition][1]);
											loadChatList();
											ml_view.scrollTo(0,finalPosition*80); // bugs
											Toast.makeText(MainActivity.this, "Deleted. "+finalPosition, Toast.LENGTH_SHORT).show();
										}
									})
									.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i){}
									})
									.create();
							alertDialog2.show();
							return true;
						}
					});*/
					
					
				}
			}
		);
	}
	
	/**
	 * Reload the chat list
	 */
	void reloadChatList(){
		MainActivity.this.runOnUiThread(
				new Runnable(){
					public void run(){
						ChatListSQLiteHelper chatListSQLiteHelper=
								new ChatListSQLiteHelper(MainActivity.this,"chat_list.db",1);
						/*
						 * [0] -> index (index>0)
						 * [1] -> user_id
						 * [2] -> nickname
						 * [3] -> last_chat_time
						 * */
						final List<String[]> chatList=chatListSQLiteHelper.fetchChatList(chatListSQLiteHelper.getReadableDatabase());
						adapterChatList.updateData(chatList);
					}
				}
		);
	}
	
	static ContactsListItemAdapter contactsListItemAdapter;
	/**
	 * Load Contacts List ListView by Adapter
	 */
	void loadContactList(){
		MainActivity.this.runOnUiThread(
			new Runnable(){
				public void run(){
					ContactListSQLiteHelper contactListSQLiteHelper=
							new ContactListSQLiteHelper(MainActivity.this,"contact_list.db",1);
					final String[][] contactsList=
							contactListSQLiteHelper.fetchContactsList(contactListSQLiteHelper.getReadableDatabase());
					
					ListView contacts_listView=findViewById(R.id.main_contacts_listView);
					contactsListItemAdapter=
							new ContactsListItemAdapter(MainActivity.this);
					contactsListItemAdapter.contactsInfo=contactsList;
					contactsListItemAdapter.count=Integer.parseInt(contactsList[0][0]);
					contacts_listView.setAdapter(contactsListItemAdapter);
					
					contacts_listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
						@Override
						public void onItemClick(AdapterView<?> parent,View view,int position,long id){
							position++;
							Intent intent=new Intent(MainActivity.this,ContactDetailActivity.class);
							Bundle bundle=new Bundle();
							bundle.putString("user_id",contactsList[position][0]);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					});
					
					contacts_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
							position++;
							AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this)
									.setTitle("Notices")
//									.setMessage("Sure to delete this contact? \n'"+contactsList[position][0]+"'")
									.setMessage("Cannot delete contact yet. \n'"+contactsList[position][0]+"'")
									.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {
											// next ver to do delete the item! by index
											Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
										}
									})
									.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {}
									})
									.create();
							alertDialog2.show();
							return true;
						}
					});
				}
			}
		);
	}
	
	@SuppressLint("SetTextI18n")
	void loadMePage(){
		TextView my_name=findViewById(R.id.main_my_name);
		TextView my_id=findViewById(R.id.main_my_id);
		TextView my_phone=findViewById(R.id.main_my_phone);
		
		my_name.setText(user_name);
		my_id.setText("id:"+user_id);
		my_phone.setText("phone:"+user_phone);
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

		if(token_key==null || user_id==null){
			return 2;
		}
		String jsonMsg="{" +
				"\"client\":\"SCC-1.0\"," +
				"\"action\":\"0001\"," +
				"\"user_id\":\""+user_id+"\"," +
				"\"token_key\":\""+token_key+"\"," +
				"\"timestamp\":\""+MyTools.getCurrentTime()+"\"" +
				"}";
		SocketWithServer SWS=new SocketWithServer();
		SWS.delay=6;
		SWS.DataSend=jsonMsg;
		JSONObject data=SWS.startSocket();
		
		if(data==null){
			return 1;
		}else if(data.getString("status").equals("true")){
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
	}
	
	/**
	 * Change content layout
	 * **/
	private void changeViewToFontLogin(){
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				// to font login page
				findViewById(R.id.font_login_linear_layout).setVisibility(View.VISIBLE);
				findViewById(R.id.main_linear_layout).setVisibility(View.GONE);
				findViewById(R.id.main_relative_layout).setVisibility(View.GONE);
				findViewById(R.id.btn_front_login).setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v){
						goToLogin();
					}
				});
				cancelRefreshTimers();
				stopService(new Intent(MainActivity.this,BackgroundTaskService.class));
				Toast.makeText(MainActivity.this,"Maybe you haven't login yet? ",
				Toast.LENGTH_LONG).show();
			}
		});
	}
	
	/**
	 * Start LoginActivity
	 */
	private void goToLogin(){
		cancelRefreshTimers();
		stopService(new Intent(MainActivity.this,BackgroundTaskService.class));
		startActivity(new Intent(MainActivity.this,LoginActivity.class));
		finish();
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
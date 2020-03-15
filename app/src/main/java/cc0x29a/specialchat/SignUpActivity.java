package cc0x29a.specialchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.IntBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

public class SignUpActivity extends AppCompatActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run(){
				refreshNewID();
			}
		}, 888);
		
		findViewById(R.id.signUp_btn_new_user_id).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				refreshNewID();
			}
		});
		
		findViewById(R.id.btn_signUp).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if(checkInfo() && checkInviteCode()){
					Toast.makeText(SignUpActivity.this,"Plz wait a second...",Toast.LENGTH_SHORT).show();
					// TODO: continue sign up.
				}
				
			}
		});
	}
	
	/**
	 * Check information
	 * @return boolean , whether info is legal
	 */
	private boolean checkInfo(){
		try{
			EditText user_name=findViewById(R.id.sign_user_name);
			EditText password=findViewById(R.id.sign_password);
			EditText password_confirm=findViewById(R.id.sign_confirm_password);
			
			Pattern pattern_password=Pattern.compile("^[a-zA-Z0-9._]{8,20}$");
			Matcher m_pass=pattern_password.matcher(password.getText().toString());
			
			Pattern pattern_user_name=Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5_\\-:：.。?？!！()（）]{1,10}$");
			Matcher m_um=pattern_user_name.matcher(user_name.getText().toString());
			
			if(!m_um.matches()){
				Toast.makeText(SignUpActivity.this,"User name should only be \n"+"(a-zA-Z0-9\\u4e00-\\u9fa5_\\-:：.。?？!！()（）)\n"+"and 1~10 bits !",Toast.LENGTH_LONG).show();
				return false;
			}else if(!m_pass.matches()){
				Toast.makeText(SignUpActivity.this,"Password should only be '0-9a-zA-Z._' and 8~20 bits !",Toast.LENGTH_LONG).show();
				return false;
			}else if(!password.getText().toString().equals(password_confirm.getText().toString())){
				Toast.makeText(SignUpActivity.this,"Two password do not match! ! ",Toast.LENGTH_SHORT).show();
				return false;
			}
			return true;
		}catch(NullPointerException e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Verify invite code
	 * @return boolean
	 */
	private boolean checkInviteCode(){
		try{
			EditText ic=findViewById(R.id.sign_invite_code);
			String i_code=ic.getText().toString();
			if(!i_code.equals("0x29a.cc")){
				Toast.makeText(SignUpActivity.this,"Your Invite Code is not correct! \n"+
						"For further Info., Plz contact the admin! ",Toast.LENGTH_SHORT).show();
				return false;
			}
			return true;
		}catch(NullPointerException e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Refresh a new user id
	 */
	private void refreshNewID(){
		String new_user_id;
		TextView textView=findViewById(R.id.signUp_user_id);
		if((new_user_id=createNewId(0))!=null){
			textView.setText(new_user_id);
		}
	}
	
	/**
	 * Create a new ID , 8 bits
	 * @param t execute times (promise not up to 5)
	 * @return String , new ID
	 */
	private String createNewId(int t) {
		t++;
		if(t<4){
			try{
				StringBuilder sb=new StringBuilder();
				int[] pool=new int[]{0,1,2,3,4,5,6,7,8,9};
				sb.append(1);
				for(int i=1;i<=7;i++){
					Thread.sleep(128);
					sb.append(pool[MyTools.getRandomNum(10,1)-1]);
				}
				String user_id=sb.toString();
				
				SocketWithServer socket=new SocketWithServer();
				socket.DataSend="{'action':'0005','user_id':'"+user_id+"'}";
				JSONObject data=socket.startSocket();
				
				if(data==null){
					Toast.makeText(SignUpActivity.this,"Perhaps the server is lazy..\nRetry will start soon. ",Toast.LENGTH_LONG).show();
				}else if(data.getString("status").equals("true")){
					return user_id;
				}
				Thread.sleep(8888);
				return createNewId(t);
			}catch(InterruptedException|JSONException e){
				e.printStackTrace();
				return null;
			}
		}else{
			Toast.makeText(SignUpActivity.this,"Perhaps the server is to lazy...\nRetry for new id! ",Toast.LENGTH_LONG).show();
			return null;
		}
	}
	
}
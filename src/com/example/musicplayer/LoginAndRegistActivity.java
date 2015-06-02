package com.example.musicplayer;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import com.example.musicplayer.db.SQLiteHelper;
import com.example.musicplayer.util.Person;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteAccessPermException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginAndRegistActivity extends Activity {
	private Button tecentLogin, weiboLogin, login, regist;
	private ImageButton back;
	private EditText userNumber, passWord;
	// database parameter
	SQLiteHelper databasehelp;
	List<Person> allusers;
	SQLiteDatabase mydatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_regist);
		initView();
		setLsitener();
		getOrCReateDataBase();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == event.KEYCODE_BACK)
			return false;   //forbidden back key
		else
			return super.onKeyDown(keyCode, event);
	}
	
	private void initView() {
		tecentLogin = (Button) findViewById(R.id.loginByQQ);
		weiboLogin = (Button) findViewById(R.id.loginByWeiBo);
		login = (Button) findViewById(R.id.login_login);
		regist = (Button) findViewById(R.id.regist_login);
		back = (ImageButton) findViewById(R.id.backToMianActivity_login);
		userNumber = (EditText) findViewById(R.id.userName_login);
		passWord = (EditText) findViewById(R.id.passWord_login);
	}

	private void setLsitener() {
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				load();
			}
		});
		regist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				registUser();
			}
		});
		back.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(LoginAndRegistActivity.this,
//						MainActivity.class);
//				LoginAndRegistActivity.this.startActivity(intent);
//				LoginAndRegistActivity.this.overridePendingTransition(
//						R.anim.in_from_bottom, R.anim.out_to_top);
//				finish();
				goBackMianActivity();
			}
		});
	}
	@SuppressLint("NewApi")
	private void goBackMianActivity(){
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("whichActivity", 1);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
		finish();
	}
	private void registUser() {
		String number = userNumber.getText().toString().trim();
		String password = passWord.getText().toString().trim();
		if (number == null || password == null || number.equals("")
				|| password.equals("")) {
			Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();
		} else {
			if (userIsEXist(number)) {
				Toast.makeText(this, "该用户已存在", Toast.LENGTH_LONG).show();
				userNumber.setText("");
				passWord.setText("");
			} else {
				addUser(number, password);
				Toast.makeText(this, "注册成功", Toast.LENGTH_LONG).show();
				passWord.setText("");
			}
		}

	}
	private void load(){
		String number = userNumber.getText().toString().trim();
		String password = passWord.getText().toString().trim();
		if (number == null || password == null || number.equals("")
				|| password.equals("")) {
			Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();
		} else {
			if (userIsEXist(number)) {
				if(checkUser(number, password)){
					Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
					goBackMianActivity();
				}	    
				else
					Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "用户不存在，请先注册！", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void getOrCReateDataBase() {
		databasehelp = new SQLiteHelper(this, "user_db");
		SQLiteDatabase db = databasehelp.getWritableDatabase();
//		mydatabase = openOrCreateDatabase("user_db", Context.MODE_PRIVATE, null);
//		mydatabase.execSQL("DROP TABLE IF EXISTS users");
//		mydatabase.execSQL("create table users(usernumber varchar(20),password varchar(20))");
//		mydatabase.execSQL("INSERT INTO users VALUES(?,?);", new Object[]{"Yy","123"});
	}

	private boolean userIsEXist(String username) {
		getAllUser();
		if (allusers != null && allusers.size() != 0) {
			for (int i = 0; i < allusers.size(); i++) {
				if (username.equals(allusers.get(i).getUserNumber()))
					return true;
			}
			return false;
		} else
			return false;
	}
	
	private boolean checkUser(String number,String pass){
		for (int i = 0; i < allusers.size(); i++) {
			if (number.equals(allusers.get(i).getUserNumber()) &&
				pass.equals(allusers.get(i).getPassWord())){
				return true;
			}
		}
		return false;
	}
	
	private void getAllUser() {
		allusers = selectData("users",
				new String[] { "usernumber", "password" });
	}

	private void addUser(String name, String pass) {
		insertData("users", new String[] { "usernumber", "password" }, new String[] {name,pass});
	}

	// database operator
	private void insertData(String tabname, String[] key, String[] value) {

		try {
			SQLiteDatabase db = databasehelp.getWritableDatabase();
			ContentValues values = new ContentValues();
			for (int i = 0; i < key.length; i++) {
				values.put(key[i], value[i]);
			}
			long i = db.insert(tabname, "dont know", values);
			System.out.println("******add user :" + db.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private List<Person> selectData(String tabname, String[] key) {
		List<Person> ps = new ArrayList<Person>();
		int k = -1;
		try {
			SQLiteDatabase db = databasehelp.getReadableDatabase();
			Cursor cursor = db.query(tabname, key, null, null, null, null, null);
			while (cursor.moveToNext()) {
				Person p = new Person();
				p.setUserNumber(cursor.getString(cursor.getColumnIndex(key[0])));
				p.setPassWord(cursor.getString(cursor.getColumnIndex(key[1])));
				ps.add(p);
				System.out.println("******people:" + p.getUserNumber()+","+p.getPassWord());
			}
			k = cursor.getCount();
			cursor.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println("******k:"+ k);
		return ps;
	}
	
	private void delectData(String tabname,String number){
		try {
			SQLiteDatabase db = databasehelp.getWritableDatabase();
			db.delete(tabname, "usernumber=?", new String[ ] {number});
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

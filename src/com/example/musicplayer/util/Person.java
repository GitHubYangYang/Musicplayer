package com.example.musicplayer.util;

public class Person {
	private String userNumber;
	private String passWord;
	public Person(){
		userNumber = "���û���";
		passWord = "������";
	}
	public Person(String un,String pd){
		userNumber = un;
		passWord = pd;
	}
	public String getUserNumber() {
		return userNumber;
	}
	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
}

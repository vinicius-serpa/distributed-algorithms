package ex6.to;

import java.io.Serializable;

public class MyMessage implements Serializable {

	private int id;
	private String string;

	public MyMessage(int id, String string) {
		this.id = id;
		this.string = string;
	}

	public int getId() {
		return id;
	}

	public String getString() {
		return string;
	}
}
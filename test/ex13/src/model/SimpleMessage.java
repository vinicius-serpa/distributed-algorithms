package model;

import java.io.Serializable;


public class SimpleMessage implements Serializable{
  
  private static final long serialVersionUID = -7930361006470891430L;
  private int id;
  private String string;
  
  public SimpleMessage(int id, String string) {
    this.id = id;
    this.string = string;
  }

  public int getId(){
	  return id;
  }
  
  public String getString(){
	  return string;
  }
}

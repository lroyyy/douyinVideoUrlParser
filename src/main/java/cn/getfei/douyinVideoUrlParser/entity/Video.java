package cn.getfei.douyinVideoUrlParser.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Video implements Serializable{

	private static final long serialVersionUID = 9034454163646618697L;

	private String name;
	private String srcUrl;
	private String desUrl;
	
}

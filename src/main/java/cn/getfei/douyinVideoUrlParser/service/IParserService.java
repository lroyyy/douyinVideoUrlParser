package cn.getfei.douyinVideoUrlParser.service;

import java.util.List;

import cn.getfei.douyinVideoUrlParser.entity.Video;

public interface IParserService {

	List<Video> parse(List<Video> videos);
	
}

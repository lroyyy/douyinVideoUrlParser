package cn.getfei.douyinVideoUrlParser.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.getfei.douyinVideoUrlParser.entity.Video;
import cn.getfei.douyinVideoUrlParser.service.IParserService;
import cn.getfei.douyinVideoUrlParser.util.ResponseResult;

@RestController
@RequestMapping("parser")
public class VideoController {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IParserService parserService;
	
	@GetMapping()
	public ResponseResult<String> parse(String str){
		List<Video> videos=new ArrayList<>();
		String[] urls=str.split("\n");
		for (String url : urls) {
//			logger.info("url="+url);
			Video video=new Video();
			video.setSrcUrl(url);
			videos.add(video);
		}
		ResponseResult<String> rr=new ResponseResult<>();
		List<Video> parsedVideos=parserService.parse(videos);
		StringBuilder desUrls=new StringBuilder();
		parsedVideos.forEach(video->{
			desUrls.append(video.getDesUrl()+"\n");
			logger.info("url="+video.getDesUrl());
		});
		rr.setData(desUrls.toString());
		rr.setState(200);
		return rr;
		
	}
}

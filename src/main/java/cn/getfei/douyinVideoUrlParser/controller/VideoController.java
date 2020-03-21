package cn.getfei.douyinVideoUrlParser.controller;

import java.util.ArrayList;
import java.util.List;

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

	@Autowired
	private IParserService parserService;
	
	@GetMapping()
	public ResponseResult<String> parse(String str){
		List<Video> videos=new ArrayList<>();
		String[] urls=str.split("\n");
		for (String url : urls) {
			Video video=new Video();
			video.setSrcUrl(url);
			videos.add(video);
		}
		ResponseResult<String> rr=new ResponseResult<>();
		List<Video> parsedVideos=parserService.parse(videos);
		StringBuilder desUrl=new StringBuilder();
		parsedVideos.forEach(url->{
			desUrl.append(url+"\n");
		});
		rr.setData(desUrl.toString());
		rr.setState(200);
		return rr;
		
	}
}

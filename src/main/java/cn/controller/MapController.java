package cn.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cn.service.MergeMapService;
import cn.utils.utils;

@Controller
public class MapController {
	@Autowired
	private MergeMapService mergeMapService;

	@RequestMapping("/selectMap")
	public String list(Model model) {
		return "mergeselectmap";
	}

	@RequestMapping(value = "/mergemap", method = RequestMethod.POST, produces = "application/octet-stream;charset=UTF-8")
	public synchronized void download(Model model, @RequestParam("file1") MultipartFile[] file1) throws IOException {
		utils.delAllFile("D:/temp");
		StringBuffer sb = new StringBuffer();
		String s = file1[0].getOriginalFilename().substring(0, file1[0].getOriginalFilename().length() - 4);//提取文件名
		String tableName = s.split("-")[0];
		
		for (int i = 0; i < file1.length; i++) {
			System.out.println(
					file1[i].getName() + "---" + file1[i].getContentType() + "---" + file1[i].getOriginalFilename());
			try {
				if (!file1[i].isEmpty()) {
					Streams.copy(file1[i].getInputStream(), new FileOutputStream("D:/temp/fubiao" + i + ".csv"), true);
				}
			} catch (IOException e) {
				System.out.println("IO读取异常!");
			}
		}
		
		File finalFile = null;
		String dfileName;

		if (!mergeMapService.merge(file1.length, sb)) {
			finalFile = new File("D:/temp/errorlog.txt");
			dfileName = "errorlog.txt";
		} else {
			finalFile = new File("D:/temp/merge.xls");
			dfileName = tableName + "Merge.xls";
		}
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//		headers.setContentDispositionFormData("attachment", dfileName);
//		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(finalFile), headers, HttpStatus.CREATED);
	}
}
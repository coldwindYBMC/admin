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

import cn.service.MergeService;
import cn.utils.utils;

@Controller
public class MerageController {
	@Autowired
	private MergeService mergeService;
	
	@RequestMapping("/select")
	public String list(Model model){
		return "mergeselect";
	}
	
	@RequestMapping(value = "/mergeexcel",method = RequestMethod.POST, produces = "application/octet-stream;charset=UTF-8")
	public synchronized ResponseEntity<byte[]> download( Model model,@RequestParam("file") MultipartFile file,@RequestParam("file1") MultipartFile[] file1) throws IOException {
		utils.delAllFile("D:/temp");
		StringBuffer sb=new StringBuffer();
		String tableName="";
		try {
	            if (!file.isEmpty()){
	            	tableName=file.getOriginalFilename();
	                Streams.copy(file.getInputStream(),new FileOutputStream("D:/temp/zhubiao.xls"),true);
	            }
	        } catch (IOException e) {
	        }
	        for (int i=0;i<file1.length;i++){
	        	try {
	        		if (!file1[i].isEmpty()){
	        			Streams.copy(file1[i].getInputStream(),new FileOutputStream("D:/temp/fubiao"+i+".xls"),true);
	        		}
	        	} catch (IOException e) {
	        		System.out.println("IO读取异常!");
	        	}
	        }
	        File finalFile=null;
	        String dfileName;
		if(!mergeService.merge(tableName.substring(0, tableName.length()-4),file1.length,sb)){
			finalFile = new File("D:/temp/errorlog.txt");
			dfileName = "errorlog.txt";
		}else{
			finalFile = new File("D:/temp/merge.xls");
			dfileName = "merge.xls";
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", dfileName);
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(finalFile), headers, HttpStatus.CREATED);
	}
}
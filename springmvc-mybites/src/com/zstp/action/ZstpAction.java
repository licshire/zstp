package com.zstp.action;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zstp.entity.Zstp;
import com.zstp.service.ZstpService;

/**
 * TODO 图谱Action
 * @author 周俊林
 * @Date 2018-1-5 03:34:35
 */
@Controller
@RequestMapping("/zstp")
public class ZstpAction {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private ZstpService zstpService;
	
	/**
	 * TODO 知识图谱的主页面
	 * @author 周俊林
	 * @Date 2018-1-13 下午05:20:28
	 * @return
	 */
	@RequestMapping(value = "/zstpView", method = RequestMethod.GET)
	public String zstpView() {
		return "zstp/zstp";
	}

	/**
	 * TODO 添加文件夹
	 * 在需要校验的pojo前边加@Validated，在需要校验的pojo后边添加BindingResult bindingResult接收校验出错信息  
	 * 注意：@Validatedh和BindingResult bindingResult是配对出现，并且在形参中出现的顺序是固定的(一前一后)  
	 * @author 周俊林
	 * @Date 2018-1-27 下午4:55:46
	 * @param treeNode
	 * @param response
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public String save(@Validated Zstp zstp, BindingResult bindingResult, HttpServletResponse response) {
		ObjectNode json = new JsonNodeFactory(false).objectNode();
		//获取校验错误信息  
        if(bindingResult.hasErrors()){  
        	List<ObjectError> listError = bindingResult.getAllErrors();
        	String error = "";
        	for (ObjectError objectError : listError) {
				error += objectError.getDefaultMessage() + "<br>";
			}
            json.put("error", error);
            json.put("success", false);
        } else {
        	try {
				zstp = zstpService.save(zstp);// 保存
				json.put("success", true);
				zstp.setContent(null);
				ObjectMapper mapper = new ObjectMapper();
				json.set("node", mapper.readTree(mapper.writeValueAsString(zstp)));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				json.put("error", "添加失败！");
				json.put("success", false);
			}
        }
        if (!json.findValue("success").asBoolean()) {
        	// 如果保存失败，则判断 是新建文件夹还是修改文件夹，如果是新建flag=add，如果是修改flag=update
        	if (zstp.getId() == null) {
        		json.put("flag", "add");
        	} else {
        		if (zstpService.isExist(zstp.getId())) {
        			json.put("flag", "update");
        		} else {
        			json.put("flag", "add");
        		}
        	}
        }
        log.info(json.toString());
		return json.toString();
	}
	
	
	/**
	 * TODO 初始化zNodes
	 * @author 周俊林
	 * @Date 2018-1-29 下午4:15:04
	 * @return
	 */
	@RequestMapping(value = "/initNodes", method = RequestMethod.GET)
	@ResponseBody
	public String initNodes() {
		ObjectNode json = new JsonNodeFactory(false).objectNode();
		try {
			String zNodes = zstpService.initNodes();
			json.put("success", true);
			json.put("zNodes", zNodes);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			json.put("success", false);
		}
		return json.toString();
	}
	
	/**
	 * TODO 删除
	 * @author 周俊林
	 * @Date 2018-1-30 下午2:52:59
	 * @param nodeId
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public String deleteNode(String nodeId) {
		ObjectNode json = new JsonNodeFactory(false).objectNode();
		if (StringUtils.isBlank(nodeId)) {
			json.put("success", false);
		} else {
			try {
				json.put("success", zstpService.delete(nodeId));
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				json.put("success", false);
			}
		}
		log.info(json.toString());
		return json.toString();
	}
	
	/**
	 * TODO 根据文件夹的id分页查询该文件夹下的文件
	 * @author 周俊林
	 * @Date 2018-1-31 下午4:56:16
	 * @param nodeId
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = "/loadNote", method = RequestMethod.GET)
	@ResponseBody
	public String loadNote(String nodeId, int page, int limit) {
		JsonNodeFactory factory = new JsonNodeFactory(false);
		ObjectNode json = factory.objectNode();
		if (StringUtils.isBlank(nodeId)) {
			json.put("code", 500);
			json.put("msg", "查询失败");
			json.put("count", 0);
			json.set("data", factory.arrayNode());
		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				Page<Zstp> notes = zstpService.queryNotes(nodeId, page, limit);
				json.put("code", 0);
				json.put("msg", "查询成功");
				json.put("count", notes.getNumber());
				json.set("data", mapper.readTree(mapper.writeValueAsString(notes.getContent())));
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				json.put("code", 500);
				json.put("msg", "查询失败");
				json.put("count", 0);
				json.set("data", factory.arrayNode());
			}
		}
		log.info(json.toString());
		return json.toString();
	}
	
	/**
	 * TODO 跳转到添加笔记的页面
	 * @author 周俊林
	 * @Date 2018-2-1 下午2:03:52
	 * @param nodeId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "addNote", method = RequestMethod.GET)
	public String addNote(Integer nodeId, Model model) {
		if (nodeId != null && zstpService.isExist(nodeId)) {
			model.addAttribute("nodeId", nodeId);
		}
		return "zstp/addNote";
	}
	
	/**
	 * TODO 跳转到添加文件夹的页面
	 * @author 周俊林
	 * @Date 2018-2-14 下午5:57:41
	 * @param nodeId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "addNode", method = RequestMethod.GET)
	public String addNode(Integer nodeId, Model model) {
		if (nodeId != null && zstpService.isExist(nodeId)) {
			model.addAttribute("nodeId", nodeId);
		}
		return "zstp/addNode";
	}
}
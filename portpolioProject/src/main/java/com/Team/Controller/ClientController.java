package com.Team.Controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.Team.Dao.ClientDao;
import com.Team.Dao.ReViewDAO;
import com.Team.Dao.ShopDAO;
import com.Team.Service.ClientService;
import com.Team.Service.ReViewService;
import com.Team.Service.ShopService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ClientController {
	
	@Autowired
	public SqlSession sqlSession;
	
	
	//메인화면 노출
	@RequestMapping(value="/")
	public String homehome(Model model) {
		return "redirect:index";

	}
	//메인화면 노출시 리뷰(좋아요),상품(좋아요) 순 최대 3개 노출
	@RequestMapping("/index")
	public String home(Model model,HttpServletRequest request) {
		ReViewDAO ReViewmapper = sqlSession.getMapper(ReViewDAO.class);
		ShopDAO Shopmapper = sqlSession.getMapper(ShopDAO.class);
		ClientDao Clientmapper = sqlSession.getMapper(ClientDao.class);		
		model.addAttribute("request", request);
		ReViewService.getInstance().ReViewGoodKing(model,ReViewmapper);
		ShopService.getInstance().ShopGoodKing(model,Shopmapper);
		ClientService.getInstance().UserIpCheck(model,Clientmapper);

		
		return "views/index";

	}
	
}

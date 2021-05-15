package com.Team.Controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Team.Dao.ClientDao;
import com.Team.Dao.ReViewDAO;
import com.Team.Dao.ShopDAO;
import com.Team.Service.ClientService;
import com.Team.Service.ReViewService;
import com.Team.Service.ShopService;
import com.google.gson.Gson;

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
	// 회원가입 페이지 이동
		@RequestMapping("JoinViewDo")
		public String JoinViewDo(HttpServletRequest request, Model model) {
			System.out.println("JoinViewDo()");
			return "ClientJoin/JoinView";
		}

		// 회원가입 결과 페이지
		@RequestMapping("JoinResultViewDo")
		public String JoinResultViewDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("JoinViewDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().join(model,response, mapper);

			return "ClientJoin/JoinResultView";
		}

		//이메일 체크 결과
		@RequestMapping("JoinEmailResultViewDo")
		public String JoinEmailResultViewDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("JoinEmailResultViewDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().emailCheckAction(model,response ,mapper);

			return "ClientJoin/JoinEmailResultView";
		}

		//ID 중복 체크 AJAX
		@ResponseBody
		@RequestMapping("IdoverlapcheckLogicDo")
		public void IdoverlapcheckLogicDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("IdoverlapcheckLogicDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			String client_id = request.getParameter("client_id");
			System.out.println(client_id);

			response.getWriter().write(getJSON(client_id, mapper));

		}
		//ID중복체크 AJAX의 메소드
		private String getJSON(String client_id, ClientDao mapper) {
			int daoresult = -1;
			StringBuffer result = new StringBuffer("");
			System.out.println("제이슨함수");

			if (client_id.equals("")) {
				result.append("아이디를 입력해주세요");
			} else {
				daoresult = mapper.idoverlapcheck(client_id);
				if (daoresult == 0) {
					result.append("사용 가능한 아이디입니다.");
				}
				if (daoresult == 1) {
					result.append("이미 사용중인 아이디입니다.");
				}
			}

			return result.toString();
		}
		//로그인 뷰로 이동
		@RequestMapping("LoginViewDo")
		public String LoginViewDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("LoginViewDo()");
			return "Login/LoginView";
		}
		//로그인 뷰 -> 로그인 시도
		@RequestMapping("LoginDo")
		public String LoginDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().Login(model,response,mapper);

			return "Login/LoginResultView";
		}
		//로그아웃
		@RequestMapping("LogoutViewDo")
		public String LogoutViewDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("LogoutViewDo()");
			model.addAttribute("request", request);
			ClientService.getInstance().LogoutViewDo(model,response);

			return "Login/LogoutView";
		}
		//ID&PW 찾기
		@RequestMapping("SearchMyIdPwDo")
		public String SearchMyIdPwDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("SearchMyIdPwDo()");

			return "MyPage/SearchMyIdPw";
		}
		
		@RequestMapping("SearchIdByEmailDo")
		public String SearchIdByEmailDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("SearchIdByEmailDo()");

			return "MyPage/SearchIdByEmail";
		}
		
		
		@RequestMapping("SearchPwViewDo")
		public String SearchPwViewDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("SearchPwViewDo()");

			return "MyPage/SearchPwView";
		}
		
		@RequestMapping("SearchMyIdByEmailDo")
		public String SearchMyIdByEmailDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("SearchMyIdByEmailDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().SearchMyIdByEmailDo(model,mapper);

			return "MyPage/SearchMyIdByEmailResult";
		}
		
		@RequestMapping("SearchMyPwDo")
		public String SearchMyPwDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("SearchMyPwDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().SearchMyPwDo(model,mapper,response);

			return "MyPage/SearchMyPwResult";
		}
		
		@RequestMapping("MyPasswordChangeDo")
		public String MyPasswordChangeDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("MyPasswordChangeDo()");
			String id = request.getParameter("id");
			request.setAttribute("id",id);
			return "MyPage/MyPasswordChangeView";
		}
		
		@RequestMapping("PasswordChangeDo")
		public String PasswordChangeDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("PasswordChangeDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().MyPasswordChangeDo(model,mapper);
			return "MyPage/PasswordChangeResult";
		}
		//(장진호) 마이페이지로 이동
		@RequestMapping("MyPageViewDo")
		public String MyPageViewDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("MyPageViewDo()");
			
			return "MyPage/MyPageMainView";
		}
		
		@RequestMapping("MyClientWithdrawalDo")
		public String MyClientWithdrawalDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("MyClientWithdrawalDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().MyClientWithdrawalDo(model,mapper);
			return "MyPage/MyClientWithdrawal";
		}
		
		@RequestMapping("MyEditViewPasswordCheckDo")
		public String MyEditViewPasswordCheckDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("MyEditViewPasswordCheckDo()");
			return "MyPage/ClientMyEditPasswordView";
		}
		
		@RequestMapping("ClientEditViewDo")
		public String ClientEditViewDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("ClientEditViewDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().ClientEditViewDo(model,mapper,response);
			return "MyPage/ClientEditView";
		}

		@RequestMapping("EditResultViewDo")
		public String EditResultViewDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("EditResultViewDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().EditResultViewDo(model,mapper,response);
			return "MyPage/EditResultView";
		}
		
		//(장진호) 마이페이지 -> 출석체크 포인트 지급
		@ResponseBody
		@RequestMapping(value="depositPoint",produces="application/json;charset=utf8")
		public String depositPoint(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("EditResultViewDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			Gson gson = new Gson();
			int flag = ClientService.getInstance().AttentionCheck(model,mapper,response);
			StringBuffer result = new StringBuffer("");
			if(flag==1) {
				result.append(gson.toJson("출석포인트 50Point 적립 되었습니다!"));
			}else if(flag==-1) {
				result.append(gson.toJson("오늘 이미 출석포인트를 적립 받으셨습니다!"));
			}
			return result.toString();

		}
		@RequestMapping("MyQnAviewPageDo")
		public String MyQnAviewPageDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("MyQnAviewPageDo()");
			ClientDao mapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().MyQnAviewPageDo(model,mapper,response);
			return "MyPage/MyQnAviewPage";
		}
		//(장진호) 마이페이지 -> 내가쓴 리뷰 글 확인
		@RequestMapping("MyListViewPage")
		public String MyListViewPage(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("MyQnAviewPageDo()");
			ClientDao Clientmapper = sqlSession.getMapper(ClientDao.class);
			ReViewDAO ReViewmapper = sqlSession.getMapper(ReViewDAO.class);
			model.addAttribute("request", request);
			ClientService.getInstance().reviewSelect(model,Clientmapper,ReViewmapper,response);
			return "MyPage/MyListViewPage";
		}
		//(장진호) 마이페이지 -> 포인트 적립내역확인
		@RequestMapping("MyPointSelect")
		public String MyPointSelect(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("MyQnAviewPageDo()");
			ClientDao Clientmapper = sqlSession.getMapper(ClientDao.class);
			model.addAttribute("request", request);
			ClientService.getInstance().SelectMyPointDeposit(model,Clientmapper,response);
			return "MyPage/MyPagePointLogView";
		}
		
		@RequestMapping("MyBaguniViewDo")
		public String MyBaguniViewDo(HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
			System.out.println("MyBaguniViewDo()");
			ShopDAO mapper = (ShopDAO) sqlSession.getMapper(ShopDAO.class);
			model.addAttribute("request", request);
			ClientService.getInstance().MyBaguniViewDo(model,mapper,response);
			return "MyPage/MyBaguniMainView";
		}
	
}

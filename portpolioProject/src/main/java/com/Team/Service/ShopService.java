package com.Team.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.ui.Model;

import com.Team.Dao.ShopDAO;
import com.Team.List.ShopList;
import com.Team.Vo.ShopVO;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class ShopService {
	
	private static ShopService instance = new ShopService();
	private ShopService() {}
	public static ShopService getInstance() {return instance;}
	DecimalFormat priceFm = new DecimalFormat("#,##0");
	
//	전체 상품 보기
	public void selectAllProduct(Model model, ShopDAO mapper) {
		DecimalFormat priceFm = new DecimalFormat("#,##0");
		System.out.println("service => selectAllProduct() 메서드 들어옴");
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:shopCTX.xml");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("session_id");
		
		int currentPage = 1;
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (NumberFormatException e) {
		}
		int pageSize = 8;
		int totalCount = mapper.selectCount(mapper);

		ShopList shopList = ctx.getBean("ShopList", ShopList.class);
		
		String category = "";
		try {
			category = request.getParameter("category"); // 이전 페이지에서 카테고리를 가져온다. ex) 신발, 상의, 하의
		} catch (Exception e) {
		}			
		
		shopList.shopList_category(pageSize, totalCount, currentPage, category);
		shopList.setList(mapper.selectAllProduct(shopList));
		
//		가격에 , 찍기
		for (ShopVO vo : shopList.getList()) {
			vo.setSh_priceFM(priceFm.format(vo.getSh_price()));
			vo.setSh_salePriceFM(priceFm.format(vo.getSh_salePrice()));
		}
		
		model.addAttribute("shopList", shopList);
		model.addAttribute("userId", userId);
	}
	
	public void ShopGoodKing(Model model, ShopDAO shopmapper) {
		DecimalFormat priceFm = new DecimalFormat("#,##0");
		System.out.println("service => selectAllProduct() 메서드 들어옴");
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:shopCTX.xml");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("session_id");
		
		int currentPage = 1;
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (NumberFormatException e) {
		}
		int pageSize = 8;
		int totalCount = shopmapper.selectCount(shopmapper);

		ShopList shopList = ctx.getBean("ShopList", ShopList.class);
		
		String category = "";
		try {
			category = request.getParameter("category"); // 이전 페이지에서 카테고리를 가져온다. ex) 신발, 상의, 하의
		} catch (Exception e) {
		}			
		
		shopList.shopList_category(pageSize, totalCount, currentPage, category);
		shopList.setList(shopmapper.goodKingShop());
		
		int ranking = 0;
//		가격에 , 찍기
		for (ShopVO vo : shopList.getList()) {
			ranking++;
			vo.setSh_priceFM(priceFm.format(vo.getSh_price()));
			vo.setSh_salePriceFM(priceFm.format(vo.getSh_salePrice()));
			vo.setSh_ranking(ranking);
		}
		
		model.addAttribute("shopList", shopList);
		model.addAttribute("userId", userId);
	}
	
//	상품 등록
	public void insertProduct(Model model, ShopDAO mapper) throws IOException {
		System.out.println("service => insertProduct()");
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:shopCTX.xml");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		
//		ServletContext application = request.getServletContext();
		ShopVO vo = ctx.getBean("ShopVO", ShopVO.class);
		String userId = (String) session.getAttribute("session_id");
		
		try {
			MultipartRequest mr = new MultipartRequest(
					request, 
					"C:/upload", 
					5 * 1024 * 1024,
					"UTF-8",
					new DefaultFileRenamePolicy()
			);

//			업로드할 파일이름 여러개를 받는다.
				Enumeration<String> fileNames = mr.getFileNames();

				int imgCount = 0;
				
//			이미지파일 이름 넣는 반복문
				while (fileNames.hasMoreElements()) {
					imgCount++;
					String parameter = (String) fileNames.nextElement();
//					String fileName = mr.getFile(parameter).getName();
					String fileRealName = mr.getFilesystemName(parameter);
					
					if (fileRealName == null) {
						continue;
					}
					
					String imgNames = "http://localhost:8010/korea/upload/" + fileRealName;// img 파일 이름
					
					if(imgCount == 1) {
						vo.setSh_img1(imgNames);
					} else if(imgCount == 2) {
						vo.setSh_img2(imgNames);
					}
				}
				
				String title = mr.getParameter("title");
				String name = mr.getParameter("name");
				String category = mr.getParameter("category");
				String categoryDetail = mr.getParameter("categoryDetail");
				String content = mr.getParameter("content");
				int price = Integer.parseInt(mr.getParameter("price"));
				double salePercent = Double.parseDouble(mr.getParameter("salePercent"));
				double salePrice = price - (price*(salePercent/100));
				vo.setSh_title(title);
				vo.setSh_name(name);
				vo.setSh_category(category);
				vo.setSh_categoryDetail(categoryDetail);
				vo.setSh_content(content);
				vo.setSh_price(price);
				vo.setSh_salePercent(salePercent);
				vo.setSh_salePrice(salePrice);
				vo.setSh_seller(userId);
				
		} catch (Exception e) {
			System.out.println("파일업로드 에러");
			e.printStackTrace();
		}
			
			System.out.println(vo);
			if(vo.getSh_img1() != null && vo.getSh_img2() != null) {
				mapper.shopInsertProduct(vo);
				System.out.println("shopInsertProduct");
			} else if(vo.getSh_img1() != null && vo.getSh_img2() == null) {
				mapper.shopInsertProduct1(vo);
				System.out.println("shopInsertProduct1");
			} else if(vo.getSh_img1() == null && vo.getSh_img2() != null) {
				mapper.shopInsertProduct2(vo);
				System.out.println("shopInsertProduct2");
			} else {
				System.out.println("상품 등록 실패");
			}
	}
	
//	브랜드별 상품 목록
	public void selectCategoryDetail(Model model, HttpServletResponse response, ShopDAO mapper) throws IOException {
		System.out.println("service => selectCategoryDetail()");
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:shopCTX.xml");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("session_id");
		
		int currentPage = 1;
		try { currentPage = Integer.parseInt(request.getParameter("currentPage")); } catch (NumberFormatException e) {}
		
		int pageSize = 8;
		
		String categoryDetail = request.getParameter("categoryDetail");
		String category = request.getParameter("category");
		int totalCount = mapper.selectCount(mapper);
		
		ShopList shopList = ctx.getBean("ShopList", ShopList.class);
		shopList.shopList_category(pageSize, totalCount, currentPage, categoryDetail, category);
		shopList.setList(mapper.selectCategoryDetail(shopList));
		
//		가격에 , 찍기
		for (ShopVO vo : shopList.getList()) {
			vo.setSh_priceFM(priceFm.format(vo.getSh_price()));			// 정상 가격
			vo.setSh_salePriceFM(priceFm.format(vo.getSh_salePrice()));	// 할인된 가격
		}
		model.addAttribute("shopList", shopList);
		model.addAttribute("userId", userId);
	}
	
//	상품 상세보기
	public void ShopSelectProduct(Model model, HttpServletResponse response, ShopDAO mapper) throws IOException {
		System.out.println("service => ShopSelectProduct()");
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:shopCTX.xml");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		
		String userId = null;
		try {userId = (String) session.getAttribute("session_id");} catch (Exception e1) {System.out.println("아이디를 찾을 수 없음");}
		int sh_idx = Integer.parseInt(request.getParameter("sh_idx"));
		
		int currentPage = 1;
		try { currentPage = Integer.parseInt(request.getParameter("currentPage")); } catch (NumberFormatException e) { }
		
		ShopVO vo = ctx.getBean("ShopVO", ShopVO.class);
		vo = mapper.selectProduct(sh_idx);
		int likeCount = mapper.likeCount(sh_idx);
		int likeCheck = 0;
		if(userId != null) {
			Map<String , Object> likeMap = new HashMap<String, Object>();
			likeMap.put("like_idx",  sh_idx);
			likeMap.put("like_id",  userId);
			likeCheck = mapper.likeCheck(likeMap);
		}
		
		mapper.hitUp(sh_idx);	//	조회수 증가
		
		vo.setSh_priceFM(priceFm.format(vo.getSh_price()));
		vo.setSh_salePriceFM(priceFm.format(vo.getSh_salePrice()));
		request.setAttribute("likeCheck", likeCheck);		// 좋아요를 누른 유저인지 판별
		request.setAttribute("likeCount", likeCount);		// 좋아요 개수
		request.setAttribute("userId", userId);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("vo", vo);
		System.out.println("ShopSelectProduct의 vo : " + vo);
	}
	
//	상품 삭제
	public void ShopDeleteProduct(Model model, HttpServletResponse response, ShopDAO mapper) throws IOException {
		System.out.println("service => ShopDeleteProduct()");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		String userPw = session.getAttribute("session_password") +"";		//	로그인시 사용되는 비밀번호
		String inputPw = request.getParameter("sh_password");				//	삭제페이지에서 입력한 비밀번호
		
		String flag = "";
		//	마이페이지에서 왔으면 flag = mypage
		try { flag = request.getParameter("flag"); } catch (Exception e1) {System.out.println("flag 안받아옴");} 					
		
		int sh_idx = Integer.parseInt(request.getParameter("sh_idx"));		// 	삭제할 상품 번호
		System.out.println("inputPw : " + inputPw);
		System.out.println("sh_idx : " + sh_idx);
		System.out.println(flag);
		PrintWriter script = response.getWriter();
		
		try {
			if(flag.equals("mypage")) {
				mapper.deleteProduct(sh_idx);
				mapper.deleteProduct(sh_idx);
				script.println("<script>");
				script.println("alert('delete OK');");
				script.println("location.href = 'MyProductViewPage';");
				script.println("</script>");
				script.close();
			}else if(flag.equals("adminPage")){
				mapper.deleteProduct(sh_idx);
				script.println("<script>");
				script.println("alert('delete OK');");
				script.println("location.href = 'AdminProductManagement';");
				script.println("</script>");
				script.close();
			}else if(userPw.trim().equals(inputPw.trim())) {
				mapper.deleteProduct(sh_idx);
				System.out.println("삭제됨");
				script.println("<script>");
				script.println("alert('delete OK');");
				script.println("location.href = 'ShopAllProduct';");
				script.println("</script>");
				script.close();
			}else {
				script.println("<script>");
				script.println("alert('비밀번호가 맞지 않습니다.');");
				script.println("history.back();");
				script.println("</script>");
				script.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	상품 수정
	public void ShopUpdateProduct(Model model, HttpServletResponse response, ShopDAO mapper) throws IOException {
		System.out.println("service => ShopUpdateProduct()");
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:shopCTX.xml");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		ShopVO vo = ctx.getBean("ShopVO", ShopVO.class);
		
		MultipartRequest mr = new MultipartRequest(
				request,
//				application.getRealPath("./upload/"),
				"C:/upload/",
				5 * 1024 * 1024,
				"UTF-8",
				new DefaultFileRenamePolicy()
			);
		Enumeration<String> fileNames = mr.getFileNames();
		System.out.println("fileNames : " + fileNames);
		int imgCount = 0;
		while (fileNames.hasMoreElements()) {
			imgCount++;
			String parameter = (String) fileNames.nextElement();
//			String fileName = mr.getOriginalFileName(parameter);
			String fileRealName = mr.getFilesystemName(parameter);
			
			if (fileRealName == null) {
				continue;
			}
			String imgNames = "http://localhost:8010/korea/upload/" + fileRealName;// img 파일 이름
			
			if(imgCount == 1) {
				vo.setSh_img1(imgNames);
				vo.setSh_img2(imgNames);
			} else if(imgCount == 2) {
				vo.setSh_img2(imgNames);
			}
		}
		
		String originalImg1 = null;
		String originalImg2 = null;
		try { originalImg1 = mr.getParameter("originalImg1"); } catch(Exception e) { }
		try { originalImg2 = mr.getParameter("originalImg2"); } catch(Exception e) { }
		
		if(originalImg1 != null) {
			System.out.println("originalImg1 :" + originalImg1);
			vo.setSh_img1(originalImg1);
		}
		if(originalImg2 != null) {
			System.out.println("originalImg2 :" + originalImg2);
			vo.setSh_img2(originalImg2);
		}
		
		int idx = Integer.parseInt(mr.getParameter("idx"));
		String title = mr.getParameter("title");
		String name = mr.getParameter("name");
		String category = mr.getParameter("category");
		String categoryDetail = mr.getParameter("categoryDetail");
		String content = mr.getParameter("content");
		Double price = Double.parseDouble(mr.getParameter("price"));
		double salePercent = Double.parseDouble(mr.getParameter("salePercent"));
		double salePrice = price - (price*(salePercent/100));
		
		vo.setSh_idx(idx);
		vo.setSh_title(title);
		vo.setSh_name(name);
		vo.setSh_category(category);
		vo.setSh_categoryDetail(categoryDetail);
		vo.setSh_content(content);
		vo.setSh_price(price);
		vo.setSh_salePercent(salePercent);
		vo.setSh_salePrice(salePrice);
		
		if(vo.getSh_img1() != null && vo.getSh_img2() != null) {
			System.out.println("updateProduct");
			mapper.updateProduct(vo);
		} else if(vo.getSh_img1() != null && vo.getSh_img2() == null) {
			System.out.println("updateProduct1");
			mapper.updateProduct1(vo);
		} else if(vo.getSh_img1() == null && vo.getSh_img2() != null) {
			System.out.println("updateProduct2");
			mapper.updateProduct2(vo);
		}
	}
	
//	idx로 상품 한 개 가져오기
	public void ShopSelectByIdx(Model model, ShopDAO mapper) throws IOException {
		System.out.println("service => ShopSelectByIdx()");
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:shopCTX.xml");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		
		ShopVO vo = ctx.getBean("ShopVO", ShopVO.class);
		int sh_idx = Integer.parseInt(request.getParameter("sh_idx"));
		
		vo = mapper.selectProduct(sh_idx);
		request.setAttribute("vo", vo);
		
	}
	
//	좋아요  업데이트
	public void likeUpdate(Model model, HttpServletResponse response, ShopDAO mapper) throws IOException {
		System.out.println("service => likeUpdate()");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		Map<String, Object> likeMap = new HashMap<String, Object>();
		likeMap.put("like_idx", request.getParameter("like_idx"));
		likeMap.put("like_id", request.getParameter("like_id"));
		int likeCheck = mapper.likeCheck(likeMap); 		// 좋아요 누른 유저인지 체크
		
		System.out.println(likeCheck);
		if(likeCheck == 0) {
			mapper.likeUpdate(likeMap);
		} else {
			mapper.likeDelete(likeMap);
		}
		out.println(likeCheck);
		
	}

//	좋아요 수 카운트
	public void likeCount(Model model, HttpServletResponse response, ShopDAO mapper) throws IOException {
		System.out.println("service => likeCount()");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		Map<String, Object> likeMap = new HashMap<String, Object>();
		int like_idx = Integer.parseInt(request.getParameter("like_idx"));
		int count = mapper.likeCount(like_idx);
		
		likeMap.put("sh_idx", like_idx);
		likeMap.put("count", count);
		
		mapper.productLikeInsert(likeMap);	// 상품 db에 좋아요 수 넣는 작업
		out.println(count);		// ajax 리턴값
	}
	
//	장바구니
	public void addcart(Model model, HttpServletResponse response, ShopDAO mapper) throws IOException {
		System.out.println("service => addcart()");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		PrintWriter script = response.getWriter();
		
		String sh_idx = request.getParameter("sh_idx");
//		System.out.println("장바구니에 담을 상품 번호 : " + sh_idx);
//		System.out.println("장바구니에 담긴 상품 번호 : " + session.getAttribute("sh_idx_" + sh_idx));
//		System.out.println("sh_idx : " + sh_idx);
		try {
			if(sh_idx.equals(session.getAttribute("sh_idx_" + sh_idx)+"")) {
				script.println("이미 장바구니에 있음");
				script.close();
			} else {
				session.setAttribute("sh_idx_" + sh_idx , sh_idx);
				script.println("장바구니 담기 성공");
				script.close();
			}
		} catch (NumberFormatException e) {
			System.out.println("장바구니에 없음");
		}
	}
	
//	마이페이지 상품 관리
	public void MyProductViewPage(Model model, ShopDAO mapper) throws IOException {
		System.out.println("service => MyProductViewPage()");
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:shopCTX.xml");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		
		int currentPage = 1;
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (NumberFormatException e) {
		}
		int pageSize = 8;
		int totalCount = mapper.selectCount(mapper);

		ShopList list = ctx.getBean("ShopList", ShopList.class);
		list.shopList_category(pageSize, totalCount, currentPage);
		
		String userId = (String) session.getAttribute("session_id");
		list.setList(mapper.myProductViewPage(userId));
		System.out.println("MyProductViewPage : " + list);
		model.addAttribute("shoplist", list);
	}
	
	public void AdminProductManagement(Model model, ShopDAO mapper) throws IOException {
		System.out.println("service => AdminProductManagement()");
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:shopCTX.xml");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		
		int currentPage = 1;
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (NumberFormatException e) {
		}
		int pageSize = 8;
		int totalCount = mapper.selectCount(mapper);

		ShopList list = ctx.getBean("ShopList", ShopList.class);
		list.shopList_category(pageSize, totalCount, currentPage);
		
		String userId = (String) session.getAttribute("session_id");
		list.setList(mapper.myProductViewPage(userId));
		System.out.println("MyProductViewPage : " + list);
		model.addAttribute("shoplist", list);
	}
	
}

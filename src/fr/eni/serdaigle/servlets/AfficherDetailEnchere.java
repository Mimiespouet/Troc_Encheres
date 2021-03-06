package fr.eni.serdaigle.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fr.eni.serdaigle.bll.EnchereManager;
import fr.eni.serdaigle.bo.Enchere;
import fr.eni.serdaigle.bo.Utilisateur;
import fr.eni.serdaigle.exception.GeneralException;

/**
 * Servlet implementation class AfficherDetailEnchere
 */
/**
 * Classe en charge de pouvoir renvoyer le détail d'un article avec son enchère selon l'état de l'utilisateur 
 * (detailMaVente.jsp | detailMaVenteFinEnchere.jsp | acquisition.jsp | encherir.jsp)
 * @author serdaigle
 * @version troc_encheres - v1.0
 * @date 1 avr. 2020
 */
@WebServlet("/afficherDetailEnchere")
public class AfficherDetailEnchere extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private EnchereManager emger;
	
    /**
	 * {@inheritDoc}
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config); 
		emger = new EnchereManager();	
		
	}
	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session =  request.getSession();
		Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
		
		LocalDateTime date = LocalDateTime.now();
		
		int noArticle = 0;

		
		if (request.getParameter("noArticle")!=null) {
			noArticle = Integer.parseInt(request.getParameter("noArticle"));
		} else { 
			noArticle = (int) request.getAttribute("noArticle");
		}
		
		Enchere enchere = null;
		System.out.println("numero article :" + noArticle);
		try {			
			enchere = emger.select(noArticle);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		request.setAttribute("enchere",enchere);
		
		// enchère non terminée	
		if (date.isBefore(enchere.getArticle().getDateFinEncheres())) {
			if (utilisateur==null) {			
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/encherir.jsp");
				rd.forward(request, response);
			} else {
				// 	utilisateur connecté mais pas le vendeur	
				if (utilisateur.getNoUtilisateur()!=enchere.getArticle().getVendeur().getNoUtilisateur()) {					
					RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/encherir.jsp");
					rd.forward(request, response);
				// utilisateur connecté est le vendeur					
				} else {
					RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/detailMaVente.jsp");
					rd.forward(request, response);
				}
			}
		// enchère terminée		
		} else{
			if (utilisateur==null) {				
				RequestDispatcher rd = request.getRequestDispatcher("accueil");
				rd.forward(request, response);
			} else {
				// si l'utilisateur est l'acheteur			
				if (utilisateur.getNoUtilisateur()==enchere.getArticle().getAcheteur().getNoUtilisateur()) {				
					RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/acquisition.jsp");
					rd.forward(request, response);
				// l'utilisateur est le vendeur	
				} else if (utilisateur.getNoUtilisateur()==enchere.getArticle().getVendeur().getNoUtilisateur()){			
					RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/detailMaVenteFinEnchere.jsp"); 
					rd.forward(request, response);
				//ni l'un ni l'autre
				} else{			
				RequestDispatcher rd = request.getRequestDispatcher("accueil");
				rd.forward(request, response);
				}
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

package fr.eni.serdaigle.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fr.eni.serdaigle.bll.UtilisateurManager;
import fr.eni.serdaigle.bo.Utilisateur;
import fr.eni.serdaigle.exception.GeneralException;

/**
 * Servlet implementation class Accueil
 */
/**
 * Classe en charge de renvoyer la suppression des informations et données d'un utilisateur
 * @author serdaigle
 * @version troc_encheres - v1.0
 * @date 31 mars 2020
 */
@WebServlet("/suppression")
public class SuppressionUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/connexion.jsp");
		rd.forward(request, response);	
		}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
				HttpSession session = request.getSession();
				Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
				UtilisateurManager mger = new UtilisateurManager();
				
				mger.supprimerUtilisateur(utilisateur);
				
				request.setAttribute("success", "Compte supprimé avec succès");
				doGet(request, response);
				
		} catch (GeneralException be) {
			request.setAttribute("error", be.getMessage());
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/modifierProfil.jsp");
			rd.forward(request, response);	
		}
	}

}

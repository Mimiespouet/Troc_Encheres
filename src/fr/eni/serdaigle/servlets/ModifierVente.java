package fr.eni.serdaigle.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import fr.eni.serdaigle.bll.ArticleManager;
import fr.eni.serdaigle.bll.CategorieManager;
import fr.eni.serdaigle.bo.ArticleVendu;
import fr.eni.serdaigle.bo.Categorie;
import fr.eni.serdaigle.bo.Utilisateur;
import fr.eni.serdaigle.exception.BusinessException;

/**
 * Servlet implementation class modifArticle
 */
@WebServlet("/modifierVente")
public class ModifierVente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ArticleManager mger;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
		CategorieManager catMger = new CategorieManager();
		List<Categorie> listeCategorie = new ArrayList<Categorie>();
		int noArticle = Integer.parseInt(request.getParameter("noArticle"));
		mger = new ArticleManager();
		ArticleVendu article = null;
		try {
			listeCategorie = catMger.selectAll();
			request.setAttribute("listeCategorie", listeCategorie);
		} catch (BusinessException ex) {
			request.setAttribute("error", ex.getMessage());
		}
		try {
			article = mger.select(noArticle);
			request.setAttribute("articleVendu", article);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/modificationVente.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Récupération de l'utilisateur en session qui est le vendeur
		HttpSession session = request.getSession();
		Utilisateur vendeur = (Utilisateur) session.getAttribute("utilisateur");

		// Récupération des données du formulaire
		String nomArticle = request.getParameter("nom");
		String description = request.getParameter("description");
		String dateDebutEncheresStr = request.getParameter("dateDebut");
		String dateFinEncheresStr = request.getParameter("dateFin");
		String categorieStr = request.getParameter("categorie");

		// Récupération n°Article
		int noArticle = Integer.parseInt(request.getParameter("noArticle"));

		// Initialisation variables
		LocalDateTime dateFinEncheres = null;
		LocalDateTime dateDebutEncheres = null;
		int prixInitial = 0;
		try {
			ArticleManager artMger = new ArticleManager();
			BusinessException be = new BusinessException();
			CategorieManager catMger = new CategorieManager();
			// Conversion pour les dates
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
				dateDebutEncheres = LocalDateTime.parse(dateDebutEncheresStr, formatter);
				dateFinEncheres = LocalDateTime.parse(dateFinEncheresStr, formatter);
			} catch (DateTimeParseException e) {
				e.printStackTrace();
				request.setAttribute("error", "Erreur de saisie dans les données Date de début et/ou fin enchère");
			}
			// Conversion pour les entiers
			try {
				prixInitial = Integer.parseInt(request.getParameter("prixInitial").trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				request.setAttribute("error", "Erreur de saisie dans les données de type numérique");
			}
			
			Categorie categorie = catMger.selectByLibelle(categorieStr);

			// Construction de l'objet et requete d'update
			ArticleVendu art = new ArticleVendu(nomArticle, description, dateDebutEncheres, dateFinEncheres,
					prixInitial, vendeur, categorie);
			art.setNoArticle(noArticle);
			artMger.modifierArticle(art);
			request.setAttribute("success", "La vente a bien été modifié");
			RequestDispatcher rd = request.getRequestDispatcher("/afficherDetailEnchere");
			rd.forward(request, response);
		} catch (BusinessException be) {
			System.out.println(be.getMessage());
			request.setAttribute("error", be.getMessage());
			doGet(request, response);
		}
	}
}

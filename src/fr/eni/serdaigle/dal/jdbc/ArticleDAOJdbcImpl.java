package fr.eni.serdaigle.dal.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.eni.serdaigle.bo.ArticleVendu;
import fr.eni.serdaigle.dal.ArticleDAO;
import fr.eni.serdaigle.dal.CodesResultatDAL;
import fr.eni.serdaigle.dal.ConnectionProvider;
import fr.eni.serdaigle.exception.BusinessException;

public class ArticleDAOJdbcImpl implements ArticleDAO{

	private static final String INSERT = "INSERT INTO ARTICLE_VENDU(nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_vendeur, no_acheteur, no_categorie, etat_vente) VALUES (?,?,?,?,?,?,?,?,?,?);";

	@Override
	public void insert(ArticleVendu article) throws BusinessException {
		Connection cnx = null;
		BusinessException be = new BusinessException();
		try {
			cnx = ConnectionProvider.getConnection();
			PreparedStatement psmt = cnx.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
			psmt.setString(1, article.getNomArticle());
			psmt.setString(2, article.getDescription());
			psmt.setDate(3, Date.valueOf(article.getDateDebutEncheres()));
			psmt.setDate(4, Date.valueOf(article.getDateFinEncheres()));
			psmt.setInt(5, article.getPrixInitial());
			psmt.setInt(6, article.getPrixVente());
			psmt.setInt(7, article.getNoVendeur());
			psmt.setInt(8, article.getNoAcheteur());
			psmt.setInt(9, article.getNoCategorie());
			psmt.setBoolean(10, article.getEtatVente());

			int nombreArticleInsere = psmt.executeUpdate();
			if (nombreArticleInsere == 1) {
				ResultSet rs = psmt.getGeneratedKeys();
				if (rs.next()) {
					article.setNoArticle(rs.getInt(1));
				}
				rs.close();

			} else {
				be.ajouterErreur(CodesResultatDAL.INSERT_ARTICLE_ECHEC);
			}
			psmt.close();

		} catch (Exception e) {
			e.printStackTrace();
			be.ajouterErreur(CodesResultatDAL.INSERT_ARTICLE_ECHEC);
		} finally {
			try {
				cnx.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (be.hasErreurs()) {
				throw be;
			}
		}

	}
}
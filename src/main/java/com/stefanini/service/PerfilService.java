package com.stefanini.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.stefanini.entidade.Perfil;
import com.stefanini.util.JPAUtil;
import com.stefanini.util.Mensagem;

public class PerfilService {

	public boolean save(Perfil perfil) {
		EntityManager manager = JPAUtil.getEntityManager();
		manager.getTransaction().begin();
		if (verificaDiaUtil(perfil.getRegistroValidadeInicio())) {
			manager.persist(perfil);
			manager.getTransaction().commit();
			manager.close();
			return true;
		} else {
			Mensagem.add("Data informada n�o � um dia util!");
			manager.close();
			return false;
		}

	}

	public boolean update(Perfil perfil) {
		EntityManager manager = JPAUtil.getEntityManager();
		manager.getTransaction().begin();

		if (verificaDiaUtil(perfil.getDataManipulacao())) {
			Perfil perfilMerge = getPerfilById(perfil.getId());
			perfilMerge.setRegistroValidadeFim(perfil.getDataManipulacao());
			manager.merge(perfilMerge);
			manager.getTransaction().commit();
			manager.close();

			Perfil perfilPersist = new Perfil();
			perfilPersist.setNome(perfil.getNome());
			perfilPersist.setRegistroValidadeInicio(perfil.getDataManipulacao());
			save(perfilPersist);
			return true;
		} else {
			Mensagem.add("Data informada n�o � um dia util!");
			manager.close();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Perfil> listarAtivos() {
		EntityManager manager = JPAUtil.getEntityManager();
		Query q = manager.createNativeQuery(
				"SELECT * FROM sgr_perfil WHERE REGISTRO_VALIDADE_FIM IS NULL ORDER BY REGISTRO_VALIDADE_INICIO ASC",
				Perfil.class);
		List<Perfil> perfis = q.getResultList();
		return perfis;
	}

	public Perfil getPerfilById(Long id) {
		EntityManager manager = JPAUtil.getEntityManager();
		Query q = manager.createNativeQuery("SELECT * FROM sgr_perfil WHERE ID_PERFIL = :idPerfil", Perfil.class);
		q.setParameter("idPerfil", id);
		Perfil perfil = (Perfil) q.getSingleResult();
		manager.close();
		return perfil;
	}

	public void desativar(Long id) throws ConverterException {
		EntityManager manager = JPAUtil.getEntityManager();
		manager.getTransaction().begin();
		Perfil perfilMerge = getPerfilById(id);
		perfilMerge.setRegistroValidadeFim(new Date());
		manager.merge(perfilMerge);
		manager.getTransaction().commit();
		manager.close();
	}

	public boolean verificaDiaUtil(Date data) {
		GregorianCalendar calendar = new GregorianCalendar();

		calendar.setTime(data);

		if (calendar.get(GregorianCalendar.DAY_OF_WEEK) == 1 || calendar.get(GregorianCalendar.DAY_OF_WEEK) == 7) {
			return false;
		} else {
			return true;

		}
	}
}
package com.stefanini.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.stefanini.entidade.Equipe;
import com.stefanini.util.JPAUtil;
import com.stefanini.util.Mensagem;

public class EquipeService {

	public boolean save(Equipe equipe) {
		EntityManager manager = JPAUtil.getEntityManager();
		manager.getTransaction().begin();
		if (verificaDiaUtil(equipe.getRegistroValidadeInicio())) {
			manager.persist(equipe);
			manager.getTransaction().commit();
			manager.close();
			return true;
		}
		Mensagem.add("Data informada n�o � um dia util!");
		manager.close();
		return false;
	}

	public boolean update(Equipe equipe) {
		EntityManager manager = JPAUtil.getEntityManager();
		manager.getTransaction().begin();
		if (verificaDiaUtil(equipe.getDataManipulacao())) {
			Equipe equipeMerge = getEquipeById(equipe.getId());
			equipeMerge.setRegistroValidadeFim(equipe.getDataManipulacao());
			manager.merge(equipeMerge);
			manager.getTransaction().commit();
			manager.close();

			Equipe equipePersist = new Equipe();
			equipePersist.setNome(equipe.getNome());
			equipePersist.setRegistroValidadeInicio(equipe.getDataManipulacao());
			save(equipePersist);
			return true;
		} else {
			Mensagem.add("Data informada n�o � um dia util!");
			manager.close();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Equipe> listar() {
		EntityManager manager = JPAUtil.getEntityManager();
		Query q = manager.createNativeQuery(
				"SELECT * FROM sgr_equipe WHERE REGISTRO_VALIDADE_FIM IS NULL ORDER BY REGISTRO_VALIDADE_INICIO ASC",
				Equipe.class);
		List<Equipe> equipes = q.getResultList();
		manager.close();
		return equipes;
	}

	public Equipe getEquipeById(Long id) {
		EntityManager manager = JPAUtil.getEntityManager();
		Query q = manager.createNativeQuery("SELECT * FROM sgr_equipe WHERE ID_EQUIPE = :idEquipe", Equipe.class);
		q.setParameter("idEquipe", id);
		Equipe equipe = (Equipe) q.getSingleResult();
		manager.close();
		return equipe;
	}

	public void desativar(Long id) throws ConverterException {
		EntityManager manager = JPAUtil.getEntityManager();

		Equipe equipeMerge = getEquipeById(id);
		manager.getTransaction().begin();
		equipeMerge.setRegistroValidadeFim(equipeMerge.getDataManipulacao());
		manager.merge(equipeMerge);
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

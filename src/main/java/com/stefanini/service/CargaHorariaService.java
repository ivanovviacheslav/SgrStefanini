package com.stefanini.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.stefanini.entidade.CargaHoraria;
import com.stefanini.util.JPAUtil;
import com.stefanini.util.Mensagem;

public class CargaHorariaService {

	public boolean save(CargaHoraria cargaHoraria) {
		EntityManager manager = JPAUtil.getEntityManager();
		manager.getTransaction().begin();
		if (verificaDiaUtil(cargaHoraria.getRegistroValidadeInicio())) {
			manager.persist(cargaHoraria);
			manager.getTransaction().commit();
			manager.close();
			return true;
		}
		Mensagem.add("Data informada n�o � um dia util!");
		manager.close();
		return false;
	}

	public boolean update(CargaHoraria cargaHoraria) {
		EntityManager manager = JPAUtil.getEntityManager();
		manager.getTransaction().begin();

		if(verificaDiaUtil(cargaHoraria.getDataManipulacao())){
		CargaHoraria cargaHorariaAntiga = getCargaHorariaById(cargaHoraria.getId());
		cargaHorariaAntiga.setRegistroValidadeFim(cargaHoraria.getDataManipulacao());
		manager.merge(cargaHorariaAntiga);
		manager.getTransaction().commit();
		manager.close();

		CargaHoraria cargaHorariaNova = new CargaHoraria();
		cargaHorariaNova.setCargaHoraria(cargaHoraria.getCargaHoraria());
		cargaHorariaNova.setRegistroValidadeInicio(cargaHoraria.getDataManipulacao());
		save(cargaHorariaNova);
		return true;
		}
		Mensagem.add("Data informada n�o � um dia util!");
		manager.close();
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<CargaHoraria> listarAtivos() {
		EntityManager manager = JPAUtil.getEntityManager();
		Query q = manager.createNativeQuery(
				"SELECT * FROM sgr_carga_horaria WHERE REGISTRO_VALIDADE_FIM IS NULL ORDER BY REGISTRO_VALIDADE_INICIO ASC",
				CargaHoraria.class);
		List<CargaHoraria> cargaHorarias = q.getResultList();
		manager.close();
		return cargaHorarias;
	}

	public CargaHoraria getCargaHorariaById(Long id) {
		EntityManager manager = JPAUtil.getEntityManager();
		Query q = manager.createNativeQuery("SELECT * FROM sgr_carga_horaria WHERE ID_CARGA_HORARIA = :id",
				CargaHoraria.class);
		q.setParameter("id", id);
		CargaHoraria cargaHoraria = (CargaHoraria) q.getSingleResult();
		manager.close();
		return cargaHoraria;
	}

	public void desativar(Long id) throws ConverterException {
		EntityManager manager = JPAUtil.getEntityManager();
		CargaHoraria cargaHoraria = getCargaHorariaById(id);
		cargaHoraria.setRegistroValidadeFim(new Date());
		manager.getTransaction().begin();
		manager.merge(cargaHoraria);
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

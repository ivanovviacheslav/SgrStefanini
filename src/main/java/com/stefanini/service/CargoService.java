package com.stefanini.service;

import java.util.Date;
import java.util.List;

import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.stefanini.entidade.Cargo;
import com.stefanini.util.JPAUtil;

public class CargoService {
	
	public void save(Cargo cargo){
		EntityManager manager = JPAUtil.getEntityManager();
		manager.getTransaction().begin();
		manager.persist(cargo);
		manager.getTransaction().commit();
		manager.close();
	}
	
	public void update(Cargo cargo){	
		EntityManager manager = JPAUtil.getEntityManager();
		manager.getTransaction().begin();	
		
		cargo.setRegistroValidadeFim(new Date());	
		System.out.println("########## Cargo:: " + cargo.getNome() + "Cargo:: "+ cargo.getRegistroValidadeFim());
		Cargo cargoNovo = new Cargo();
		cargoNovo.setNome(cargo.getNome());		
		cargoNovo.setRegistroValidadeInicio(cargo.getRegistroValidadeFim());
		manager.persist(cargoNovo);
		
		Query q = manager.createNativeQuery("UPDATE sgr_cargo SET REGISTRO_VALIDADE_FIM = :dataFim WHERE ID_CARGO = :id");
		q.setParameter("dataFim", cargo.getRegistroValidadeFim());
		q.setParameter("id", cargo.getId());
		q.executeUpdate();
		manager.close();
	}
	
	@SuppressWarnings("unchecked")
	public List<Cargo> listarAtivos(){
		EntityManager manager = JPAUtil.getEntityManager();
		Query q = manager.createNativeQuery("SELECT * FROM sgr_cargo WHERE REGISTRO_VALIDADE_FIM IS NULL ORDER BY REGISTRO_VALIDADE_INICIO ASC", Cargo.class);
		List<Cargo> cargos = q.getResultList();
		return cargos;
	}
	
	public Cargo getCargoById(Long id){
		EntityManager manager = JPAUtil.getEntityManager();
		Query q = manager.createNativeQuery("SELECT * FROM sgr_cargo WHERE ID_CARGO = :idCargo", Cargo.class);
		q.setParameter("idCargo", id);
		Cargo cargo = (Cargo) q.getSingleResult();
		manager.close();
		return cargo;
	}

	public void desativar(Long id) throws ConverterException{
		EntityManager manager = JPAUtil.getEntityManager();
		
		Query q = manager.createNativeQuery("UPDATE sgr_cargo SET REGISTRO_VALIDADE_FIM = ':dataFim' WHERE ID_CARGO = :id");
		q.setParameter("dataFim", new Date());
		q.setParameter("id", id);
		q.executeUpdate();
		manager.close();		
	}
}

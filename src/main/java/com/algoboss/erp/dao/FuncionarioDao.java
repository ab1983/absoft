/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algoboss.erp.dao;

import com.algoboss.erp.entity.Funcionario;
import com.algoboss.erp.entity.JpaUtil;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author Agnaldo
 */
public class FuncionarioDao {
 public static EntityManager entityManager;
    public static EntityTransaction transacao;
    public static List<Funcionario> findAll(){
        List<Funcionario> objList = null;
        try {
            entityManager = JpaUtil.getEntityManager();
            //transacao = entityManager.getTransaction();
            //transacao.begin();
            objList = entityManager.createQuery("select t from Funcionario t").getResultList();            
            
            //transacao.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //transacao.rollback();
        } finally {
            if (entityManager!= null && entityManager.isOpen())
                entityManager.close();
                return objList;
        }        
    }        
}

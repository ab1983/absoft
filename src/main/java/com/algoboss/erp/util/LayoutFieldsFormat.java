package com.algoboss.erp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.algoboss.erp.entity.DevEntityClass;
import com.algoboss.erp.entity.DevEntityObject;
import com.algoboss.erp.entity.DevEntityPropertyDescriptor;
import com.algoboss.erp.entity.DevEntityPropertyDescriptorConfig;
import com.algoboss.erp.entity.DevEntityPropertyValue;
import com.algoboss.erp.entity.DevReportFieldContainer;
import com.algoboss.erp.entity.DevReportFieldOptions;
import com.algoboss.erp.entity.DevReportFieldOptionsMap;
import com.algoboss.erp.entity.DevReportRequirement;
import com.algoboss.erp.entity.DevRequirement;

public class LayoutFieldsFormat {
	private DevRequirement requirement;

	private LayoutFieldsFormat(DevRequirement requirement) throws Exception {
		this.requirement = requirement;
		//populateContainerField();
	}

	public static LayoutFieldsFormat getInstance(DevRequirement requirement) throws Exception {		
		return new LayoutFieldsFormat(requirement);
	}
	
	public static void onConstruction(DevRequirement requirement) throws Exception {
		if(requirement.getRequirementScript()!=null && requirement.getRequirementScript().contains("function onConstruction()")){
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine jsEngine;  
			jsEngine = manager.getEngineByExtension("js");
			jsEngine.put("requirement", requirement);
			jsEngine.eval("importPackage(java.util);");
			jsEngine.eval("importPackage(com.algoboss.erp.util);var fmt =LayoutFieldsFormat.getInstance(requirement);");
			jsEngine.eval(requirement.getRequirementScript());
			jsEngine.eval("onConstruction();");			
		}else{
			new LayoutFieldsFormat(requirement).showAll();
		}
	}	

	public static void onConstruction(DevReportRequirement requirement) throws Exception {
		if(requirement.getReportRequirementId()!=null){
			if(requirement.getReportRequirementId()==7){
				requirement.getFieldContainerList().clear();
				writeOptArray(new String[] { "head" }, "Date", "Data Inicial", new String[] { "fil" }, requirement, "startdate");
				writeOptArray(new String[] { "head" }, "Date", "Data Final", new String[] { "fil" }, requirement, "enddate");
				writeOptArray(new String[] { "head" }, "String", "Número NF", new String[] { "fil" }, requirement, "numeronf");
				writeOptArray(new String[] { "param" }, "String", "P_ROTA", new String[] { "fil" }, requirement, "rota");		
			}
			if(requirement.getReportRequirementId()==8){
				requirement.getFieldContainerList().clear();
				writeOptArray(new String[] { "head" }, "Date", "Data Inicial", new String[] { "fil" }, requirement, "startdate");
				writeOptArray(new String[] { "head" }, "Date", "Data Final", new String[] { "fil" }, requirement, "enddate");				
				writeOptArray(new String[] { "head" }, "String", "Número NF", new String[] { "fil" }, requirement, "numeronf");
				writeOptArray(new String[] { "param" }, "String", "P_ROTA", new String[] { "fil" }, requirement, "rota");		
			}			
		}
	}		
	
	public DevRequirement populateContainerField() throws Exception {
		List<String> fieldNames = new ArrayList<String>();
		if (requirement.getRequirementId() == null) {
			return requirement;
		}
		if (requirement.getRequirementId() == 232) {// PRÉ-VENDAS
			putTypeConfig(requirement.getEntityClass(), "cliente", "objectList", "1236_clifor[clifor|equals|Cliente];nome;nome;cgc;comple");
			putTypeConfig(requirement.getEntityClass(), "vendedor", "objectList", "1236_vendedor[funcao|equals|VENDEDOR];nome;nome");
			putTypeConfig(requirement.getEntityClass(), "operacao", "objectList", "1236_icm;nome;cfop,nome");
			putTypeConfig(requirement.getEntityClass(), "placa", "objectList", "1236_transpor;placa;placa,nome");
			fieldNames.clear();
			fieldNames.add("numeronf");
			fieldNames.add("vendedor");
			fieldNames.add("operacao");
			fieldNames.add("cliente");
			fieldNames.add("identificador1");
			fieldNames.add("desconto");
			fieldNames.add("emissao");
			fieldNames.add("saidad");
			fieldNames.add("saidah");
			fieldNames.add("placa");
			fieldNames.add("total");
			fieldNames.add("volumes");
			fieldNames.add("produtos");
			clear(requirement);
			List<String> fieldNamesList = new ArrayList<String>(fieldNames);
			fieldNamesList.remove("identificador1");
			fieldNamesList.remove("desconto");
			fieldNamesList.remove("operacao");
			fieldNamesList.remove("placa");
			fieldNamesList.remove("produtos");
			fieldNamesList.remove("saidad");
			fieldNamesList.remove("saidah");
			show("list", fieldNamesList.toArray(new String[] {}));
			show("form", fieldNames.toArray(new String[] {}));
			readOnly("form", new String[] { "numeronf", "total", "volumes" });
			// putTypeConfig(requirement.getEntityClass(),"volumes","value","#{app.count('produtos')}");
			putTypeConfig(requirement.getEntityClass(), "produtos.codigo", "objectList", "1236_estoque;codigo;codigo;descricao");
			// putTypeConfig(requirement.getEntityClass(),"produtos.total","value","#{(app.$('produtos.quantidade')).val*app.$('produtos.unitario').val)}");
			fieldNames.clear();
			// fieldNames.add("produtos.registro");
			fieldNames.add("produtos.codigo");
			// fieldNames.add("produtos.descricao");
			fieldNames.add("produtos.quantidade");
			fieldNames.add("produtos.unitario");
			fieldNames.add("produtos.total");
			fieldNamesList = new ArrayList<String>(fieldNames);
			fieldNamesList.add(1,"produtos.descricao");
			// clear(requirement);
			show("form-form-1236_itens001", fieldNames.toArray(new String[] {}));
			show("form-list-1236_itens001", fieldNamesList.toArray(new String[] {}));

			style("list", "cliente", "width:350px;");

			readOnly("form-form-1236_itens001", new String[] { "produtos.total" });

			label("numeronf", "Nº PRÉ-VENDA", "form", "list");
			label("identificador1", "TIPO PREÇO", "form", "list");
			// label("transporta", "TRANSPORTADORA","form","list");
			label("placa", "TRANSPORTADORA", "form", "list");
			label("operacao", "OPERAÇÃO", "form", "list");
			label("emissao", "EMISSÃO", "form", "list");
			label("saidad", "DATA SAÍDA", "form", "list");
			label("saidah", "HORA SAÍDA", "form", "list");
			label("volumes", "QUANTIDADE", "form", "list");

			label("produtos.codigo", "CÓDIGO/DESCRIÇÃO", "form-form-1236_itens001");
			label("produtos.unitario", "UNITÁRIO", "form-form-1236_itens001", "form-list-1236_itens001");
			label("produtos.codigo", "CÓDIGO", "form-list-1236_itens002");
			label("produtos.descricao", "DESCRIÇÃO", "form-list-1236_itens002");
			
			eventField("form-form-1236_itens001", new String[] { "produtos.codigo" }, "eventBean('com.algoboss.integration.small.business.PreVendaBo.precoProdutoGen');focusBean('.c_quantidade.c_input');", "onchange");
			eventField("form-form-1236_itens001", new String[] { "produtos.quantidade", "produtos.unitario" }, "eventBean('com.algoboss.integration.small.business.PreVendaBo.produtoCalc')", "onblur");
			eventField("form", new String[] { "identificador1" }, "eventBean('com.algoboss.integration.small.business.PreVendaBo.produtoTotalCalc')", "onchange");
			eventField("form", new String[] { "cliente" }, "eventBean('com.algoboss.integration.small.business.PreVendaBo.descontoCliente')", "onchange");
			eventField("form", new String[] { "placa" }, "eventBean('com.algoboss.integration.small.business.PreVendaBo.setTransportadoraPlaca')", "onchange");
			propertyStyleClass("form", "data-form", "onload", "if($('.c_numeronf.c_input').val()===''){eventPage('com.algoboss.integration.small.business.PreVendaBo.numeroNfGen');clearFormChanged();}else{/*eventPage('com.algoboss.integration.small.business.PreVendaBo.produtoTotalCalc');*/}");
			propertyStyleClass("form", "c_save_button", "value", "Gravar Pré-Venda");
			propertyStyleClass("form", "c_save_and_back_button", "value", "Finalizar Pré-Venda e Voltar");
			propertyStyleClass("form-form-1236_itens001", "c_save_button", "value", "Confirmar Produto");
			propertyStyleClass("form-form-1236_itens001", "c_save_and_back_button", "value", "Confirmar Produto e Voltar");
			// propertyStyleClass("form","c_save_and_back"
			// ,"value","Finalizar Pré-Venda e Voltar");
			// Nome botões
			// evento botões
			// label pagina
			// ui-commandButton
			
			DevRequirement reqCloned = DevRequirement.clone(requirement);

			// returnReq.setFieldContainerList(fieldContainerList);
			return reqCloned;
		}
		if (requirement.getRequirementId() == 238) {// ORDEM SERVIÇO
			clear(requirement);
			putTypeConfig(requirement.getEntityClass(), "identifi1", "list", "Interno,Externo");
			putTypeConfig(requirement.getEntityClass(), "cliente", "objectList", "1236_clifor;nome;nome;cgc");
			putTypeConfig(requirement.getEntityClass(), "tecnico", "objectList", "1236_vendedor[funcao|equals|TECNICO];nome;nome");
			fieldNames.clear();
			fieldNames.add("numero");
			fieldNames.add("identifi1");
			fieldNames.add("tecnico");
			fieldNames.add("data");
			fieldNames.add("hora");
			fieldNames.add("situacao");
			fieldNames.add("cliente");
			fieldNames.add("problema");
			fieldNames.add("totalos");
			fieldNames.add("produtos");
			List<String> fieldNamesList = new ArrayList<String>(fieldNames);
			fieldNamesList.remove("identifi1");
			fieldNamesList.remove("produtos");
			show("list", fieldNamesList.toArray(new String[] {}));
			show("form", fieldNames.toArray(new String[] {}));
			readOnly("form", new String[] { "numero", "totalos" });

			label("numero", "NÚMERO OS", "form", "list");
			label("identifi1", "ORIGEM", "form", "list");
			label("tecnico", "ATENDENTE", "form", "list");
			label("situacao", "SITUAÇÃO", "form", "list");
			label("totalos", "TOTAL OS", "form", "list");

			style("list", "cliente", "width:200px;");
			style("list", "hora", "width:50px;");
			style("list", "data", "width:80px;");
			style("list", "identifi1", "width:60px;");
			style("list", "problema", "width:120px;");

			propertyStyleClass("form", "data-form", "onload", "if($('.c_numero.c_input').val()===''){eventPage('com.algoboss.integration.small.business.OrdemServicoBo.numeroOsGen');};");

			putTypeConfig(requirement.getEntityClass(), "produtos.codigo", "objectList", "1236_estoque;codigo;codigo;descricao");
			// putTypeConfig(requirement.getEntityClass(),"produtos.total","value","#{(app.$('produtos.quantidade')).val*app.$('produtos.unitario').val)}");
			fieldNames.clear();
			// fieldNames.add("produtos.registro");
			fieldNames.add("produtos.codigo");
			// fieldNames.add("produtos.descricao");
			fieldNames.add("produtos.quantidade");
			fieldNames.add("produtos.unitario");
			fieldNames.add("produtos.total");
			fieldNamesList = new ArrayList<String>(fieldNames);
			fieldNamesList.add(1,"produtos.descricao");
			// clear(requirement);
			show("form-form-1236_itens001", fieldNames.toArray(new String[] {}));
			show("form-list-1236_itens001", fieldNamesList.toArray(new String[] {}));
			return requirement;
		}
		if (requirement.getRequirementId() == 239) {// COMPRAS
			clear(requirement);
			putTypeConfig(requirement.getEntityClass(), "fornecedor", "objectList", "1236_clifor[clifor|equals|Fornecedor];nome;nome;cgc;comple");
			putTypeConfig(requirement.getEntityClass(), "operacao", "objectList", "1236_icm[cfop|lessThan|5000];nome;cfop,nome");
			putTypeConfig(requirement.getEntityClass(), "descricao3", "objectList", "1236_transpor;placa;placa,nome");
			fieldNames.clear();
			fieldNames.add("numeronf");
			// fieldNames.add("vendedor");
			fieldNames.add("operacao");
			fieldNames.add("fornecedor");
			// fieldNames.add("identificador1");
			// fieldNames.add("desconto");
			fieldNames.add("emissao");
			fieldNames.add("saidad");
			fieldNames.add("saidah");
			fieldNames.add("descricao3");
			fieldNames.add("total");
			fieldNames.add("volumes");
			fieldNames.add("modelo");
			fieldNames.add("produtos");
			clear(requirement);
			List<String> fieldNamesList = new ArrayList<String>(fieldNames);
			fieldNamesList.remove("operacao");
			fieldNamesList.remove("descricao3");
			fieldNamesList.remove("produtos");
			fieldNamesList.remove("saidad");
			fieldNamesList.remove("saidah");
			fieldNamesList.remove("modelo");
			show("list", fieldNamesList.toArray(new String[] {}));
			show("form", fieldNames.toArray(new String[] {}));
			readOnly("form", new String[] { "total", "volumes" });
			// putTypeConfig(requirement.getEntityClass(),"volumes","value","#{app.count('produtos')}");
			putTypeConfig(requirement.getEntityClass(), "produtos.codigo", "objectList", "1236_estoque;codigo;codigo;descricao");
			// putTypeConfig(requirement.getEntityClass(),"produtos.total","value","#{(app.$('produtos.quantidade')).val*app.$('produtos.unitario').val)}");
			fieldNames.clear();
			// fieldNames.add("produtos.registro");
			fieldNames.add("produtos.codigo");
			// fieldNames.add("produtos.descricao");
			fieldNames.add("produtos.quantidade");
			fieldNames.add("produtos.unitario");
			fieldNames.add("produtos.total");
			fieldNames.add("produtos.icm");
			fieldNames.add("produtos.vicms");
			fieldNames.add("produtos.ipi");
			fieldNames.add("produtos.vipi");
			fieldNamesList = new ArrayList<String>(fieldNames);
			fieldNamesList.add(1,"produtos.descricao");
			// clear(requirement);
			show("form-form-1236_itens002", fieldNames.toArray(new String[] {}));
			show("form-list-1236_itens002", fieldNamesList.toArray(new String[] {}));

			style("list", "fornecedor", "width:350px;");

			readOnly("form-form-1236_itens002", new String[] { "produtos.total","produtos.vicms","produtos.vipi" });

			label("numeronf", "Nº NOTA FISCAL", "form", "list");
			// label("identificador1", "TIPO PREÇO","form","list");
			// label("transporta", "TRANSPORTADORA","form","list");
			label("descricao3", "TRANSPORTADORA", "form", "list");
			label("operacao", "OPERAÇÃO", "form", "list");
			label("emissao", "EMISSÃO", "form", "list");
			label("saidad", "DATA ENTRADA", "form", "list");
			label("saidah", "HORA ENTRADA", "form", "list");
			label("volumes", "QUANTIDADE", "form", "list");

			label("produtos.codigo", "CÓDIGO/DESCRIÇÃO", "form-form-1236_itens002");
			label("produtos.codigo", "CÓDIGO", "form-list-1236_itens002");
			label("produtos.descricao", "DESCRIÇÃO", "form-list-1236_itens002");
			label("produtos.unitario", "UNITÁRIO", "form-form-1236_itens002", "form-list-1236_itens002");
			label("produtos.icm", "% ICMS", "form-form-1236_itens002", "form-list-1236_itens002");
			label("produtos.vicms", "ICMS", "form-form-1236_itens002", "form-list-1236_itens002");
			label("produtos.ipi", "% IPI", "form-form-1236_itens002", "form-list-1236_itens002");
			label("produtos.vipi", "IPI", "form-form-1236_itens002", "form-list-1236_itens002");

			eventField("form-form-1236_itens002", new String[] { "produtos.codigo" }, "eventBean('com.algoboss.integration.small.business.CompraBo.onChangeProduto');focusBean('.c_quantidade.c_input');", "onchange");
			eventField("form-form-1236_itens002", new String[] { "produtos.quantidade", "produtos.unitario","produtos.icm","produtos.ipi" }, "eventBean('com.algoboss.integration.small.business.CompraBo.produtoCalc')", "onblur");
			
			eventField("form", new String[]{"numeronf"}, "eventBean('com.algoboss.integration.small.business.CompraBo.onBlurNumeroNf')","onblur");
			// eventField("form", new String[]{"cliente"},
			// "eventBean('com.algoboss.integration.small.business.PreVendaBo.descontoCliente')",
			// "onchange");
			eventField("form", new String[] { "descricao3" }, "eventBean('com.algoboss.integration.small.business.CompraBo.onChangeTransportadoraPlaca')", "onchange");
			propertyStyleClass("form", "data-form", "onload", "if($('.c_saidah.c_input').val()===''){eventPage('com.algoboss.integration.small.business.CompraBo.onNew');clearFormChanged();};eventPage('com.algoboss.integration.small.business.CompraBo.produtoTotalCalc');");
			propertyStyleClass("form", "c_save_button", "value", "Gravar NF Compra");
			propertyStyleClass("form", "c_save_and_back_button", "value", "Finalizar NF Compra e Voltar");
			propertyStyleClass("form-form-1236_itens002", "c_save_button", "value", "Confirmar Produto");
			propertyStyleClass("form-form-1236_itens002", "c_save_and_back_button", "value", "Confirmar Produto e Voltar");
			return requirement;
		}if (requirement.getRequirementId() == 235) {// ESTOQUE
			clear(requirement);
			//putTypeConfig(requirement.getEntityClass(), "fornecedor", "objectList", "1236_clifor[clifor|equals|Fornecedor];nome;nome;cgc;comple");
			//putTypeConfig(requirement.getEntityClass(), "operacao", "objectList", "1236_icm[cfop|lessThan|5000];nome;cfop,nome");
			//putTypeConfig(requirement.getEntityClass(), "transporta", "objectList", "1236_transpor;placa;placa,nome");
			fieldNames.clear();
			fieldNames.add("codigo");
			// fieldNames.add("vendedor");
			fieldNames.add("descricao");
			fieldNames.add("preco");
			fieldNames.add("custocompr");
			fieldNames.add("livre1");
			fieldNames.add("peso");
			fieldNames.add("comissao");
			fieldNames.add("ipi");
			fieldNames.add("cf");
			clear(requirement);
			List<String> fieldNamesList = new ArrayList<String>(fieldNames);
			//fieldNamesList.remove("dataentrada");
			show("list", fieldNamesList.toArray(new String[] {}));
			show("form", fieldNames.toArray(new String[] {}));
			//readOnly("form", new String[] { "codigo" });
			// putTypeConfig(requirement.getEntityClass(),"volumes","value","#{app.count('produtos')}");
			//putTypeConfig(requirement.getEntityClass(), "produtos.codigo", "objectList", "1236_estoque;codigo;codigo;descricao");
			// putTypeConfig(requirement.getEntityClass(),"produtos.total","value","#{(app.$('produtos.quantidade')).val*app.$('produtos.unitario').val)}");
			return requirement;
		} if (requirement.getRequirementId() == 216) {// SOLICITAÇÃO (HOSPEDAGEM)
			clear(requirement);
			//putTypeConfig(requirement.getEntityClass(), "fornecedor", "objectList", "1236_clifor[clifor|equals|Fornecedor];nome;nome;cgc;comple");
			//putTypeConfig(requirement.getEntityClass(), "operacao", "objectList", "1236_icm[cfop|lessThan|5000];nome;cfop,nome");
			//putTypeConfig(requirement.getEntityClass(), "transporta", "objectList", "1236_transpor;placa;placa,nome");
			putTypeConfig(requirement.getEntityClass(), "acomodacao", "list", "Single,Duplo Casal,Duplo Twin,Triplo,Quadruplo");
			putTypeConfig(requirement.getEntityClass(), "status", "list", "Pendente,Respondido,Disponível");
			fieldNames.clear();
			fieldNames.add("seqnum");
			// fieldNames.add("vendedor");
			fieldNames.add("datasolicitacao");
			fieldNames.add("local");
			// fieldNames.add("identificador1");
			// fieldNames.add("desconto");
			fieldNames.add("dataentrada");
			fieldNames.add("datasaida");
			fieldNames.add("qtdediarias");
			fieldNames.add("qtdehospedes");
			fieldNames.add("acomodacao");
			fieldNames.add("status");
			fieldNames.add("concluido");
			fieldNames.add("observacoes");
			fieldNames.add("opcoesdehospedagem");
			clear(requirement);
			List<String> fieldNamesList = new ArrayList<String>(fieldNames);
			fieldNamesList.remove("dataentrada");
			fieldNamesList.remove("datasaida");
			fieldNamesList.remove("qtdediarias");
			fieldNamesList.remove("produtos");
			fieldNamesList.remove("qtdehospedes");
			fieldNamesList.remove("observacoes");
			show("list", fieldNamesList.toArray(new String[] {}));
			show("form", fieldNames.toArray(new String[] {}));
			readOnly("form", new String[] { "seqnum" });
			// putTypeConfig(requirement.getEntityClass(),"volumes","value","#{app.count('produtos')}");
			//putTypeConfig(requirement.getEntityClass(), "produtos.codigo", "objectList", "1236_estoque;codigo;codigo;descricao");
			// putTypeConfig(requirement.getEntityClass(),"produtos.total","value","#{(app.$('produtos.quantidade')).val*app.$('produtos.unitario').val)}");
			fieldNames.clear();
			// fieldNames.add("produtos.registro");
			fieldNames.add("opcoesdehospedagem.hotel");
			// fieldNames.add("produtos.descricao");
			fieldNames.add("opcoesdehospedagem.categoria");
			fieldNames.add("opcoesdehospedagem.tarifa");
			fieldNames.add("opcoesdehospedagem.taxa");
			fieldNames.add("opcoesdehospedagem.observacao");
			fieldNamesList = new ArrayList<String>(fieldNames);
			//fieldNamesList.add(1,"produtos.descricao");
			// clear(requirement);
			show("form-form-1146_opcoesdehospedagem", fieldNames.toArray(new String[] {}));
			show("form-list-1146_opcoesdehospedagem", fieldNamesList.toArray(new String[] {}));
			
			label("acomodacao", "ACOMODAÇÃO", "form", "list");
			/*
			style("list", "fornecedor", "width:350px;");
			
			readOnly("form-form-1236_itens002", new String[] { "produtos.total","produtos.vicms","produtos.vipi" });

			label("transporta", "TRANSPORTADORA", "form", "list");
			label("operacao", "OPERAÇÃO", "form", "list");
			label("emissao", "EMISSÃO", "form", "list");
			label("saidad", "DATA ENTRADA", "form", "list");
			label("saidah", "HORA ENTRADA", "form", "list");
			label("volumes", "QUANTIDADE", "form", "list");

			label("produtos.codigo", "CÓDIGO/DESCRIÇÃO", "form-form-1236_itens002");
			label("produtos.codigo", "CÓDIGO", "form-list-1236_itens002");
			label("produtos.descricao", "DESCRIÇÃO", "form-list-1236_itens002");
			label("produtos.unitario", "UNITÁRIO", "form-form-1236_itens002", "form-list-1236_itens002");
			label("produtos.icm", "% ICMS", "form-form-1236_itens002", "form-list-1236_itens002");
			label("produtos.vicms", "ICMS", "form-form-1236_itens002", "form-list-1236_itens002");
			label("produtos.ipi", "% IPI", "form-form-1236_itens002", "form-list-1236_itens002");
			label("produtos.vipi", "IPI", "form-form-1236_itens002", "form-list-1236_itens002");

			eventField("form-form-1236_itens002", new String[] { "produtos.codigo" }, "eventBean('com.algoboss.integration.small.business.CompraBo.onChangeProduto');focusBean('.c_quantidade.c_input');", "onchange");
			eventField("form-form-1236_itens002", new String[] { "produtos.quantidade", "produtos.unitario","produtos.icm","produtos.ipi" }, "eventBean('com.algoboss.integration.small.business.CompraBo.produtoCalc')", "onblur");
			
			eventField("form", new String[]{"numeronf"}, "eventBean('com.algoboss.integration.small.business.CompraBo.onBlurNumeroNf')","onblur");
			// eventField("form", new String[]{"cliente"},
			// "eventBean('com.algoboss.integration.small.business.PreVendaBo.descontoCliente')",
			// "onchange");
			//eventField("form", new String[] { "transporta" }, "eventBean('com.algoboss.integration.small.business.CompraBo.onChangeTransportadoraPlaca')", "onchange");
			propertyStyleClass("form", "c_save_button", "value", "Gravar NF Compra");
			propertyStyleClass("form", "c_save_and_back_button", "value", "Finalizar NF Compra e Voltar");
			propertyStyleClass("form-form-1236_itens002", "c_save_button", "value", "Confirmar Produto");
			propertyStyleClass("form-form-1236_itens002", "c_save_and_back_button", "value", "Confirmar Produto e Voltar");
			*/
			propertyStyleClass("form", "data-form", "onload", "if($('.c_seqnum.c_input').val()===''){eventPage('com.algoboss.erp.business.SolicitacaoHospedagemBo.onNew');clearFormChanged();}else{eventPage('com.algoboss.erp.business.SolicitacaoHospedagemBo.onOpen');}");
			return requirement;
		} else if (requirement.getRequirementName().equals("PRODUTOS")) {
			fieldNames.clear();
			fieldNames.add("codigo");
			fieldNames.add("descricao");
			fieldNames.add("quantidade");
			fieldNames.add("unitario");
			fieldNames.add("total");
			clear(requirement);
			show("form", fieldNames.toArray(new String[] {}));
			show("list", fieldNames.toArray(new String[] {}));
			return requirement;
		} else {
			clear(requirement);
			/*
			 * List<DevEntityPropertyDescriptor> entityPropertyDescriptorList =
			 * requirement.getEntityClass().getEntityPropertyDescriptorList();
			 * for (DevEntityPropertyDescriptor devEntityPropertyDescriptor :
			 * entityPropertyDescriptorList) {
			 * fieldNames.add(devEntityPropertyDescriptor.getPropertyName()); }
			 * show("form", fieldNames.toArray(new String[]{})); show("list",
			 * fieldNames.toArray(new String[]{}));
			 */
		}
		return requirement;

	}
	public String[] array(String... args){
		return args;
	}	
	public String[] array(List<String> list){
		return list.toArray(new String[] {});
	}
	public void style(String container, String fieldName, String style) {
		writeOptArray(new String[] { "style" }, "String", style, new String[] { container }, requirement, new String[] { fieldName });
	}

	public void propertyStyleClass(String container, String clazz, String property, String value) {
		writeOptArray(new String[] { property }, "String", value, new String[] { container }, requirement, clazz);
	}

	public DevReportFieldOptions create(String container, String parentStyleClass, String templateStyleClass, String newStyleClass) {
		writeOptArray(new String[] { "create" }, "String", parentStyleClass+";"+templateStyleClass, new String[] { container }, requirement, newStyleClass);
		DevReportFieldContainer fieldContainer = getFieldContainer(requirement.getFieldContainerList(), container);	
		DevEntityObject entObj = new DevEntityObject(requirement.getEntityClass());
		DevReportFieldOptions fieldOptions = load(entObj, fieldContainer, newStyleClass);
		return fieldOptions;
	}	
	
	public void eventField(String container, String[] fieldNames, String function, String... events) {
		writeOptArray(events, "String", function, new String[] { container }, requirement, fieldNames);
	}

	public void label(String fieldName, String label, String... containers) {
		writeOptArray(new String[] { "label" }, "String", label, containers, requirement, new String[] { fieldName });
	}
	
	public void readOnly(String container, String[] fieldNames) {
		writeOptArray(new String[] { "readonly" }, "Boolean", "true", new String[] { container }, requirement, fieldNames);
		writeOptArray(new String[] { "disabled" }, "Boolean", "true", new String[] { container }, requirement, fieldNames);
	}

	public void putTypeConfig(DevEntityClass devEntityClass, String property, String key, String value) {
		DevEntityObject entObj = new DevEntityObject(devEntityClass);
		DevEntityPropertyDescriptorConfig devEntityPropertyDescriptorConfigAux = null;
		DevEntityPropertyValue propVal = entObj.getPropObj(property);
		if (propVal != null) {
			List<DevEntityPropertyDescriptorConfig> DevEntityPropertyDescriptorConfigList = propVal.getEntityPropertyDescriptor().getEntityPropertyDescriptorConfigList();
			if(DevEntityPropertyDescriptorConfigList==null){
				DevEntityPropertyDescriptorConfigList = new ArrayList<DevEntityPropertyDescriptorConfig>();
			}
			for (DevEntityPropertyDescriptorConfig devEntityPropertyDescriptorConfig : DevEntityPropertyDescriptorConfigList) {
				if (devEntityPropertyDescriptorConfig.getConfigName().equals(key)) {
					devEntityPropertyDescriptorConfigAux = devEntityPropertyDescriptorConfig;
					break;
				}
				// devEntityPropertyDescriptorConfig.
			}
			if (devEntityPropertyDescriptorConfigAux == null) {
				devEntityPropertyDescriptorConfigAux = new DevEntityPropertyDescriptorConfig();
				devEntityPropertyDescriptorConfigAux.setConfigName(key);
				DevEntityPropertyDescriptorConfigList.add(devEntityPropertyDescriptorConfigAux);
			}
			devEntityPropertyDescriptorConfigAux.setConfigValue(value);
		} else {
			throw new IllegalArgumentException("Property '" + property + "' not found in LayoutFieldFormat for Entity '" + devEntityClass.getName() + "'.");
		}
	}

	public static List<DevReportFieldOptions> filter(DevRequirement requirement, String parentContainer, String optionName, String optionValue) {

		DevReportFieldContainer fieldContainer = LayoutFieldsFormat.getFieldContainer(requirement.getFieldContainerList(), parentContainer);

		List<DevReportFieldOptions> fieldOptionsList = new ArrayList<DevReportFieldOptions>();
		List<DevReportFieldOptions> fieldOptionsListTmp = fieldContainer.getFieldOptionsList();
		for (DevReportFieldOptions devReportFieldOptions : fieldOptionsListTmp) {
			if (String.valueOf(LayoutFieldsFormat.loadOpt(devReportFieldOptions, optionName).getOptionsValue()).equals(optionValue)) {
				fieldOptionsList.add(devReportFieldOptions);
			}
		}
		return fieldOptionsList;
	}

	public static DevReportFieldContainer getFieldContainer(List<DevReportFieldContainer> fieldContainerList, String container) {
		DevReportFieldContainer fieldContainer = null;
		for (DevReportFieldContainer devReportFieldContainer : fieldContainerList) {
			if (devReportFieldContainer.getName().equals(container)) {
				fieldContainer = devReportFieldContainer;
				break;
			}
		}
		if (fieldContainer == null) {
			fieldContainer = new DevReportFieldContainer();
			fieldContainer.setName(container);
			fieldContainerList.add(fieldContainer);
		}
		return fieldContainer;
	}

	public static void clear(DevRequirement requirement) {
		requirement.getFieldContainerList().clear();
		DevEntityClass entitySelected = requirement.getEntityClass();
		if(entitySelected!=null){
			requirement.getFieldContainerList().addAll(generateFieldContainerHide(entitySelected,"form","list"));
		}
		
	}
	
	public static List<DevReportFieldContainer> generateFieldContainerHide(DevEntityClass entitySelected, String... containers){		
		return generateFieldContainer(false, entitySelected, containers);
	}
	
	public static List<DevReportFieldContainer> generateFieldContainerShow(DevEntityClass entitySelected, String... containers){		
		return generateFieldContainer(true, entitySelected, containers);
	}
	
	public static List<DevReportFieldContainer> generateFieldContainer(Boolean show, DevEntityClass entitySelected, String... containers){
		List<DevReportFieldContainer> fieldContainerList = new ArrayList<DevReportFieldContainer>();
		for (int i = 0; i < containers.length; i++) {
			String container = 	containers[i];
			DevReportFieldContainer fieldContainer = new DevReportFieldContainer();
			fieldContainer.setName(container);	
			fieldContainerList.add(fieldContainer);
			List<DevReportFieldOptions> fieldOptionsList = fieldContainer.getFieldOptionsList();
			List<DevEntityPropertyDescriptor> entityPropertyDescriptorList = entitySelected.getEntityPropertyDescriptorList();
			DevEntityObject entObj = new DevEntityObject(entitySelected);
			for (DevEntityPropertyDescriptor devEntityPropertyDescriptor : entityPropertyDescriptorList) {
				DevReportFieldOptions devReportFieldOptions = new DevReportFieldOptions();
				devReportFieldOptions.setEntityPropertyDescriptor(devEntityPropertyDescriptor);
				devReportFieldOptions.setName(devEntityPropertyDescriptor.getPropertyName());
				fieldOptionsList.add(devReportFieldOptions);
				loadOpt(entObj, fieldContainer, devEntityPropertyDescriptor.getPropertyName(), "visible", "Boolean").setOptionsValue(show.toString());
				if(container.startsWith("form") && devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")){
					fieldContainerList.addAll(generateFieldContainer(show, devEntityPropertyDescriptor.getPropertyClass(),"form-form-"+entitySelected.getName(),"form-list-"+entitySelected.getName()));
				}
			}			
		}
		return fieldContainerList;
	}	

	public static DevReportFieldOptions load(DevEntityClass entCls, DevReportFieldContainer fieldContainer, String fieldName) {
		DevEntityObject entObj = new DevEntityObject(entCls);
		return load(entObj, fieldContainer, fieldName);

	}

	public static DevReportFieldOptions load(DevEntityObject entObj, DevReportFieldContainer fieldContainer, String fieldName) {
		DevReportFieldOptions fieldOptions = null;
		List<DevReportFieldOptions> fieldOptionsList = fieldContainer.getFieldOptionsList();
		for (DevReportFieldOptions fieldOptionsTmp : fieldOptionsList) {
			if (fieldName.equals(fieldOptionsTmp.getName()) || fieldName.endsWith("."+fieldOptionsTmp.getName())) {
				fieldOptions = fieldOptionsTmp;
				break;
			}
		}
		if (fieldOptions == null) {
			fieldOptions = new DevReportFieldOptions();
			DevEntityPropertyValue propField = entObj!=null?entObj.getPropObj(fieldName):null;
			if (propField != null) {
				fieldOptions.setEntityPropertyDescriptor(propField.getEntityPropertyDescriptor());
				fieldOptions.setName(fieldOptions.getEntityPropertyDescriptor().getPropertyName());
			} else {
				fieldOptions.setName(fieldName);
			}
			fieldOptions.setFieldContainerParent(fieldContainer);
			fieldContainer.getFieldOptionsList().add(fieldOptions);
		}
		return fieldOptions;
	}

	public static DevReportFieldOptionsMap loadOpt(DevEntityClass entCls, DevReportFieldContainer fieldContainer, String fieldName, String optionName) {
		DevEntityObject entObj = new DevEntityObject(entCls);
		return loadOpt(entObj, fieldContainer, fieldName, optionName, "String");
	}

	public static DevReportFieldOptionsMap loadOpt(DevEntityObject entObj, DevReportFieldContainer fieldContainer, String fieldName, String optionName) {
		return loadOpt(entObj, fieldContainer, fieldName, optionName, "String");
	}

	public static DevReportFieldOptionsMap loadOpt(DevEntityObject entObj, DevReportFieldContainer fieldContainer, String fieldName, String optionName, String optionType) {
		if(fieldContainer.getFieldOptionsList().isEmpty()){
			//fieldContainer.getFieldOptionsList().addAll(generateOptionsForAll(entObj.getEntityClass()));
		}
		DevReportFieldOptions fieldOptions = load(entObj, fieldContainer, fieldName);
		return loadOpt(fieldOptions, optionName, optionType);
	}

	public static DevReportFieldOptionsMap loadOpt(DevReportFieldOptions fieldOptions, String optionName) {
		return loadOpt(fieldOptions, optionName, null);
	}

	public static DevReportFieldOptionsMap loadOpt(DevReportFieldOptions fieldOptions, String optionName, String optionType) {
		DevReportFieldOptionsMap fieldOptionsMap = null;
		for (DevReportFieldOptionsMap fieldOptionsMapTmp : fieldOptions.getFieldOptionsMapList()) {
			if (fieldOptionsMapTmp.getOptionsName().equals(optionName)) {
				fieldOptionsMap = fieldOptionsMapTmp;
				break;
			}
		}
		if (fieldOptionsMap == null) {
			fieldOptionsMap = new DevReportFieldOptionsMap();
			if (optionType != null) {
				fieldOptionsMap.setOptionsName(optionName);
				fieldOptionsMap.setOptionsType(optionType);
				fieldOptions.getFieldOptionsMapList().add(fieldOptionsMap);
			}
		}
		return fieldOptionsMap;
	}
	/**
	 * Show fields in form or list. <br>
	 * Example: <br>
	 * 		var fieldNamesList = new ArrayList(fieldNames);<br>
			fieldNamesList.remove("operacao");<br>
			fieldNamesList.remove("descricao3");<br>
			fieldNamesList.remove("produtos");<br>
			fmt.show("list", fmt.array(fieldNamesList));<br>
	 * @param container
	 * @param fieldNames
	 */	
	public void show(String container, String... fieldNames) {
		if(fieldNames == null){
			clear(requirement);
		}else{
			DevReportFieldContainer fieldContainer = getFieldContainer(requirement.getFieldContainerList(), container);
			fieldContainer.getFieldOptionsList().clear();
			writeOptArray(new String[] { "visible" }, "Boolean", "true", new String[] { container }, requirement, fieldNames);
		}
	}
	
	public void showAll() {
		requirement.getFieldContainerList().clear();
		DevEntityClass entitySelected = requirement.getEntityClass();
		if(entitySelected!=null){
			requirement.getFieldContainerList().addAll(generateFieldContainerShow(entitySelected,"form","list"));			
		}

	}	
	
	public void hidden(String container, String... fieldNames) {
		writeOptArray(new String[] { "visible" }, "Boolean", "false", new String[] { container }, requirement, fieldNames);
	}	
	
	public static void writeOptArray(String[] optionNames, String optionType, String optionValue, String[] containers, DevReportRequirement requirementReport, String... fieldNames) {
		writeOptArray(optionNames, optionType, optionValue, containers, null, requirementReport, fieldNames);
	}
	
	public static void writeOptArray(String[] optionNames, String optionType, String optionValue, String[] containers, DevRequirement requirement, String... fieldNames) {
		writeOptArray(optionNames, optionType, optionValue, containers, requirement, null, fieldNames);
	}
	
	public static DevReportFieldContainer writeOptArray(String[] optionNames, String optionType, String optionValue, String[] containers, DevRequirement requirement, DevReportRequirement requirementReport, String... fieldNames) {
		List<DevReportFieldContainer> fieldContainerList = null;
		DevEntityClass entityClass = null;
		if(requirement!=null){
			fieldContainerList = requirement.getFieldContainerList();
			entityClass = requirement.getEntityClass();			
		}else{
			fieldContainerList = requirementReport.getFieldContainerList();
			entityClass = requirementReport.getEntityClass();				
		}
		DevReportFieldContainer fieldContainer = null;
		for (int i = 0; i < containers.length; i++) {
			String container = containers[i];
			fieldContainer = getFieldContainer(fieldContainerList, container);
			DevEntityObject entObj = new DevEntityObject(entityClass);
			for (int j = 0; j < fieldNames.length; j++) {
				String fieldName = fieldNames[j];
				for (int k = 0; k < optionNames.length; k++) {
					String optionName = optionNames[k];
					loadOpt(entObj, fieldContainer, fieldName, optionName, optionType).setOptionsValue(optionValue);
				}
			}
		}
		return fieldContainer;
	}	

	public static List<DevReportFieldOptions> getVisibleFields(DevRequirement requirement, String parent, DevEntityClass entitySelected) {
		DevReportFieldContainer fieldContainer = LayoutFieldsFormat.getFieldContainer(requirement.getFieldContainerList(), parent);
		List<DevReportFieldOptions> fieldOptionsListTmp = fieldContainer.getFieldOptionsList();
		List<DevReportFieldOptions> fieldOptionsList = new ArrayList<DevReportFieldOptions>();
		if (fieldOptionsListTmp.isEmpty()) {
			fieldOptionsList = generateOptionsForAll(entitySelected);
		}
		for (DevReportFieldOptions devReportFieldOptions : fieldOptionsListTmp) {
			Object obj = LayoutFieldsFormat.loadOpt(devReportFieldOptions, "visible").getOptionsValue();
			if (obj != null && Boolean.valueOf(obj.toString())) {
				fieldOptionsList.add(devReportFieldOptions);
			}
		}
		return fieldOptionsList;
	}

	private static List<DevReportFieldOptions> generateOptionsForAll(DevEntityClass entitySelected) {
		List<DevReportFieldOptions> fieldOptionsList = new ArrayList<DevReportFieldOptions>();
		List<DevEntityPropertyDescriptor> entityPropertyDescriptorList = entitySelected.getEntityPropertyDescriptorList();
		for (DevEntityPropertyDescriptor devEntityPropertyDescriptor : entityPropertyDescriptorList) {
			DevReportFieldOptions devReportFieldOptions = new DevReportFieldOptions();
			devReportFieldOptions.setEntityPropertyDescriptor(devEntityPropertyDescriptor);
			devReportFieldOptions.setName(devEntityPropertyDescriptor.getPropertyName());
			fieldOptionsList.add(devReportFieldOptions);
		}
		return fieldOptionsList;
	}

	public static List<DevReportFieldOptions> getContainerOptions(List<DevReportFieldContainer> fieldContainerList, String parent, DevEntityClass entitySelected) {
		DevReportFieldContainer fieldContainer = LayoutFieldsFormat.getFieldContainer(fieldContainerList, parent);
		List<DevReportFieldOptions> fieldOptionsListTmp = fieldContainer.getFieldOptionsList();
		List<DevReportFieldOptions> fieldOptionsList = new ArrayList<DevReportFieldOptions>();
		for (DevReportFieldOptions devReportFieldOptions : fieldOptionsListTmp) {
			if (devReportFieldOptions.getEntityPropertyDescriptor() == null) {
				fieldOptionsList.add(devReportFieldOptions);
			}
		}
		return fieldOptionsListTmp;
	}

	public static List<String> getVisibleFieldsNames(DevRequirement requirement, String containerPage, DevEntityClass entity) {
		List<DevReportFieldOptions> listOptions = getVisibleFields(requirement, containerPage, entity);
		List<String> names = new ArrayList<String>();
		for (DevReportFieldOptions devReportFieldOptions : listOptions) {
			names.add(devReportFieldOptions.getName());
		}
		return names;
	}
}

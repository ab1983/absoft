/**
 * SMALL COMPRAS
 */
var SmallUtil = com.algoboss.integration.small.business.SmallUtil;

function Compras() {
	// var nfexml;
	this.nfexml = null;
	this.numeronf = null;
	this.operacao = null;
	this.fornecedor = null;
	this.emissao = null;
	this.saidad = null;
	this.saidah = null;
	/**
	 * Placa transportadora
	 */
	this.descricao3 = null;
	this.mercadoria = null;
	this.baseicm = null;
	this.icms = null;
	this.ipi = null;
	this.total = null;
	this.volumes = null;
	this.modelo = null;
	this.produtos = new ArrayList();
	this.especie = null;
	this.marca = null;
	this.pesoliqui = null;
	this.baseicm = null;
}

function Produto() {
	this.codigo = null;
	this.quantidade = null;
	this.unitario = null;
	this.total = null;
	this.cfop = null;
	this.base = null;
	this.icm = null;
	this.vicms = null;
	this.ipi = null;
	this.vipi = null;
	this.descricao = null;

}

function onConstruction() {
	try {
		var fieldNames = new ArrayList();
		fmt.clear(requirement);
		fmt.putTypeConfig(requirement.getEntityClass(), "fornecedor", "objectList", "1236_clifor[clifor|equals|Fornecedor];nome;nome;cgc;comple");
		fmt.putTypeConfig(requirement.getEntityClass(), "operacao", "objectList", "1236_icm[cfop|lessThan|5000];nome;cfop,nome");
		fmt.putTypeConfig(requirement.getEntityClass(), "descricao3", "objectList", "1236_transpor;placa;placa,nome");
		fieldNames.clear();
		fieldNames.add("nfexml");
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
		fieldNames.add("mercadoria");
		fieldNames.add("baseicm");
		fieldNames.add("icms");
		fieldNames.add("ipi");
		fieldNames.add("total");
		fieldNames.add("volumes");
		fieldNames.add("modelo");
		fieldNames.add("produtos");
		fmt.clear(requirement);
		var fieldNamesList = new ArrayList(fieldNames);
		fieldNamesList.remove("nfexml");
		fieldNamesList.remove("operacao");
		fieldNamesList.remove("descricao3");
		fieldNamesList.remove("produtos");
		fieldNamesList.remove("saidad");
		fieldNamesList.remove("saidah");
		fieldNamesList.remove("modelo");
		fieldNamesList.remove("baseicm");
		fieldNamesList.remove("icms");
		fieldNamesList.remove("ipi");
		fmt.show("list", fmt.array(fieldNamesList));
		fmt.show("form", fmt.array(fieldNames));
		fmt.readOnly("form", fmt.array("total", "volumes", "baseicm", "icms", "ipi", "mercadoria"));
		// putTypeConfig(requirement.getEntityClass(),"volumes","value","#{app.count('produtos')}");
		fmt.putTypeConfig(requirement.getEntityClass(), "produtos.codigo", "objectList", "1236_estoque;codigo;codigo;descricao;custocompr;preco");
		fmt.putTypeConfig(requirement.getEntityClass(), "produtos.cfop", "objectList", "1236_icm[cfop|lessThan|5000];cfop;cfop,nome");
		// putTypeConfig(requirement.getEntityClass(),"produtos.total","value","#{(app.$('produtos.quantidade')).val*app.$('produtos.unitario').val)}");
		fieldNames.clear();
		// fieldNames.add("produtos.registro");
		fieldNames.add("produtos.codigo");
		// fieldNames.add("produtos.descricao");
		fieldNames.add("produtos.quantidade");
		fieldNames.add("produtos.unitario");
		fieldNames.add("produtos.total");
		fieldNames.add("produtos.cfop");
		fieldNames.add("produtos.base");
		fieldNames.add("produtos.icm");
		fieldNames.add("produtos.vicms");
		fieldNames.add("produtos.ipi");
		fieldNames.add("produtos.vipi");
		fieldNamesList = new ArrayList(fieldNames);
		fieldNamesList.add(1, "produtos.descricao");
		fieldNamesList.remove("produtos.base");
		// clear(requirement);
		fmt.show("form-form-1236_itens002", fmt.array(fieldNames));
		fmt.show("form-list-1236_itens002", fmt.array(fieldNamesList));

		fmt.style("list", "fornecedor", "width:350px;");
		fmt.style("form", "fornecedor", "width:350px;");
		fmt.style("form", "operacao", "width:350px;");
		fmt.style("form", "descricao3", "width:350px;");

		fmt.readOnly("form-form-1236_itens002", fmt.array("produtos.total", "produtos.vicms", "produtos.vipi"));

		fmt.label("nfexml", "IMPORTAR NFe", "form");
		fmt.label("numeronf", "Nº NOTA FISCAL", "form", "list");
		// label("identificador1", "TIPO PREÇO","form","list");
		// label("transporta", "TRANSPORTADORA","form","list");
		fmt.label("descricao3", "TRANSPORTADORA", "form", "list");
		fmt.label("operacao", "OPERAÇÃO", "form", "list");
		fmt.label("emissao", "EMISSÃO", "form", "list");
		fmt.label("saidad", "DATA ENTRADA", "form", "list");
		fmt.label("saidah", "HORA ENTRADA", "form", "list");
		fmt.label("volumes", "QUANTIDADE", "form", "list");
		fmt.label("total", "TOTAL NFE", "form", "list");
		fmt.label("baseicm", "BASE ICMS", "form");
		fmt.label("mercadoria", "VALOR PRODUTOS", "form", "list");

		fmt.label("produtos.codigo", "CÓDIGO/DESCRIÇÃO", "form-form-1236_itens002");
		fmt.label("produtos.codigo", "CÓDIGO", "form-list-1236_itens002");
		fmt.label("produtos.descricao", "DESCRIÇÃO", "form-list-1236_itens002");
		fmt.label("produtos.unitario", "UNITÁRIO", "form-form-1236_itens002", "form-list-1236_itens002");
		fmt.label("produtos.icm", "% ICMS", "form-form-1236_itens002", "form-list-1236_itens002");
		fmt.label("produtos.vicms", "VALOR ICMS", "form-form-1236_itens002", "form-list-1236_itens002");
		fmt.label("produtos.ipi", "% IPI", "form-form-1236_itens002", "form-list-1236_itens002");
		fmt.label("produtos.vipi", "VALOR IPI", "form-form-1236_itens002", "form-list-1236_itens002");
		fmt.label("produtos.base", "% BASE ICMS", "form-form-1236_itens002", "form-list-1236_itens002");
		fmt.label("produtos.quantidade", "QTDE", "form-list-1236_itens002");

		fmt.style("form-form-1236_itens002", "produtos.codigo", "width:350px;");
		fmt.style("form-form-1236_itens002", "produtos.cfop", "width:350px;");
		fmt.style("form-list-1236_itens002", "produtos.descricao", "width:300px;");

		fmt.eventField("form-form-1236_itens002", fmt.array("produtos.codigo"), "eventBean('onChangeProduto()');focusBean('.c_quantidade.c_input');",
				"onchange");
		fmt.eventField("form-form-1236_itens002", fmt.array("produtos.cfop"), "eventBean('onChangeProduto()');", "onchange");

		fmt.eventField("form-form-1236_itens002", fmt.array("produtos.quantidade", "produtos.unitario", "produtos.icm", "produtos.ipi", "produtos.base"),
				"eventBeanWait('produtoCalc()')", "onkeyup");

		fmt.eventField("form", fmt.array("numeronf"), "if($('.c_numeronf.c_input').val()!==''){eventBeanWait('onBlurNumeroNf()')}", "onblur");
		fmt.eventField("form", fmt.array("fornecedor"), "eventBeanWait('if(onBlurNumeroNf()){onChangeProduto();}',1000);", "onchange");
		// eventField("form", new String[]{"cliente"},
		// "eventBean('descontoCliente()')",
		// "onchange");
		fmt.eventField("form", fmt.array("descricao3"), "eventBean('onChangeTransportadoraPlaca()')", "onchange");
		fmt
				.propertyStyleClass(
						"form",
						"data-form",
						"onload",
						"var hasToImportXml = '#{app.$g('1236_compras.nfexml')}'!='' && '#{app.$g('1236_compras.numeronf')}'==''; var hasProdutos = #{app.count('1236_compras.produtos')}>0;if(hasToImportXml){eventPage('importXmlNfe()');} if($('.c_saidah.c_input').val()===''){eventPage('onNew();produtoTotalCalc();');restartFormChanged();}else if(hasProdutos){eventPage('produtoTotalCalc()');}");
		// fmt.propertyStyleClass("form", "data-form", "onload",
		// "eventPage('onLoad()')");
		fmt.propertyStyleClass("form", "c_save_button", "value", "Gravar NF Compra");
		fmt.propertyStyleClass("form", "c_save_and_back_button", "value", "Finalizar NF Compra e Voltar");
		fmt.propertyStyleClass("form-form-1236_itens002", "c_save_button", "value", "Confirmar Produto");
		fmt.propertyStyleClass("form-form-1236_itens002", "c_save_and_back_button", "value", "Confirmar Produto e Voltar");
	} catch (e) {
		stack(e);
	}
}

function produtoCalc() {
	produtoTotalCalcImpl(app, false);
}

function produtoTotalCalc() {
	if(atualizarCadastroEstoque(false)){
		map.put("callback", "if(confirm('Deseja atualizar vinculação de estoque?')){eventCall('atualizarCadastroEstoque(true)');}");
	}
	produtoTotalCalcImpl(app, false);
}

function produtoTotalCalcImpl(app, precoRecalc) {
	try {
		var obj = app.getBean();
		if (obj != null && obj.getEntityClass().getName().equals("1236_compra")) {
			if(obj.getProp("nfexml") != null){
				//return;
			}
			// volumesCalc(obj);
			var objChild = app.getChildBean();
			if (objChild != null && !objChild.getEntityPropertyValueList().isEmpty()) {
				if (!putProdutoTotal(app, objChild)) {
					return;
				}
			} else {
				objChild = null;
			}
			var objChildList = obj.getProp("produtos");
			if (objChild != null && !objChildList.contains(objChild)) {
				objChildList.add(objChild);
			}
			var volumes = parseFloat(0);
			var total = parseFloat(0);
			var pesoLiquido = parseFloat(0);
			var totalIcms = parseFloat(0);
			var totalIpi = parseFloat(0);
			var totalBaseIcms = parseFloat(0);
			if (objChildList != null) {
				for (var x = 0; x < objChildList.size(); x++) {
					var devEntityObject = objChildList.get(x);
					if (precoRecalc) {
						precoProdutoGenImpl(app, devEntityObject, false);
					}
					putProdutoTotal(app, devEntityObject);
					volumes++;
					var totalObj = devEntityObject.getPropObj("produtos.total").getPropertyValue();
					if (totalObj != null) {
						total += parseFloat(totalObj);
					}
					var pesoLiquidoObj = devEntityObject.getPropObj("produtos.peso").getPropertyValue();
					if (pesoLiquidoObj != null) {
						pesoLiquido += parseFloat(pesoLiquidoObj);
					}
					var ipiObj = devEntityObject.getPropObj("produtos.vipi").getPropertyValue();
					if (ipiObj != null) {
						totalIpi += parseFloat(ipiObj);
					}
					var icmsObj = devEntityObject.getPropObj("produtos.vicms").getPropertyValue();
					if (icmsObj != null) {
						totalIcms += parseFloat(icmsObj);
					}
					var icmsVbcObj = devEntityObject.getProp("produtos.vbc");
					if (icmsVbcObj != null) {
						totalBaseIcms += parseFloat(icmsVbcObj);
					}
				}
			} else {
				println('### INCONSISTÊNCIA NO objChildList EM produtoTotalCalcImpl: ');
			}
			obj.getPropObj("volumes").setPropertyValue(volumes);
			obj.getPropObj("total").setPropertyValue(
					BigDecimal.valueOf(parseFloat(total) + parseFloat(totalIpi)).setScale(2, RoundingMode.HALF_UP).doubleValue());
			obj.getPropObj("mercadoria").setPropertyValue(BigDecimal.valueOf(parseFloat(total)).setScale(2, RoundingMode.HALF_UP).doubleValue());
			obj.getPropObj("pesoliqui").setPropertyValue(BigDecimal.valueOf(parseFloat(pesoLiquido)).setScale(2, RoundingMode.HALF_UP).doubleValue());
			obj.getPropObj("icms").setPropertyValue(BigDecimal.valueOf(parseFloat(totalIcms)).setScale(2, RoundingMode.HALF_UP).doubleValue());
			obj.getPropObj("baseicm").setPropertyValue(BigDecimal.valueOf(parseFloat(totalBaseIcms)).setScale(2, RoundingMode.HALF_UP).doubleValue());
			obj.getPropObj("ipi").setPropertyValue(BigDecimal.valueOf(parseFloat(totalIpi)).setScale(2, RoundingMode.HALF_UP).doubleValue());
			// obj.getPropObj("desconto").setPropertyValue(BigDecimal.valueOf(valorDesconto).setScale(2,
			// RoundingMode.HALF_UP).doubleValue());
		}
	} catch (e) {
		stack(e);
	}
}

function putProdutoTotal(app, objChild) {
	try {
		var isOk = true;
		if(objChild.getPropObj(campoVinculo)!==null){
			//return isOk;
		}
		var obj = app.getBean();
		var quantidade = objChild.getProp("produtos.quantidade");
		var unitario = objChild.getProp("produtos.unitario");

		var base = 0;
		var icms = 0;
		var ipi = 0;
		var totalProduto = 0;
		var pesoProduto = 0;
		var valorBase = 0;
		var totalIcms = 0;
		var totalIpi = 0;
		var ipiUnitario = 0;
		var peso = 0;

		if (quantidade === null || quantidade == 'null' || unitario === null || unitario == 'null') {
			isOk = false;
		}
		var codigo = objChild.getProp("produtos.codigo");
		var produto = SmallUtil.getProduto(codigo, app);
		if (produto == null) {
			if(obj.getPropObj("nfexml")!==null){
				isOk = true;
			}else{
				isOk = false;
			}
		}else{
			peso = produto.getProp("peso")|0;
		}
		if (isOk) {			
			base = objChild.getProp("produtos.base", 0);
			icms = objChild.getProp("produtos.icm", 0);
			ipi = objChild.getProp("produtos.ipi", 0);
			totalProduto = BigDecimal.valueOf(quantidade * unitario).setScale(2, RoundingMode.HALF_UP).doubleValue();
			pesoProduto = BigDecimal.valueOf(quantidade * peso).setScale(2, RoundingMode.HALF_UP).doubleValue();
			valorBase = totalProduto * parseFloat(base) / 100;
			totalIcms = BigDecimal.valueOf(valorBase * icms / 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
			totalIpi = BigDecimal.valueOf(totalProduto * ipi / 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
			ipiUnitario = BigDecimal.valueOf(totalIpi / quantidade).setScale(2, RoundingMode.HALF_UP).doubleValue();
			if (!objChild.getProp("produtos.numeronf", "").toString().isEmpty()
					&& !objChild.getProp("produtos.numeronf", "").toString().equals(obj.getProp("numeronf", ""))) {
				objChild.getPropObj("produtos.numeronf").setPropertyValue(obj.getProp("numeronf"));
			}
		}
		var nomeOperacao = obj.getProp("operacao");
		var operacao = SmallUtil.getOperacao(nomeOperacao, app);
		// objChild.getPropObj("produtos.base").setPropertyValue(100);
		objChild.getPropObj("produtos.total").setPropertyValue(totalProduto);
		objChild.getPropObj("produtos.peso").setPropertyValue(pesoProduto);
		objChild.getPropObj("produtos.vbc").setPropertyValue(valorBase);
		// objChild.getPropObj("produtos.cfop").setPropertyValue(operacao.getProp('cfop'));
		objChild.getPropObj("produtos.vicms").setPropertyValue(totalIcms);
		objChild.getPropObj("produtos.vipi").setPropertyValue(totalIpi);
		objChild.getPropObj("produtos.fornecedor").setPropertyValue(app.getBean().getProp("fornecedor"));

		objChild.getPropObj("produtos.custo").setPropertyValue(parseFloat(unitario) + parseFloat(ipiUnitario));
	} catch (e) {
		stack(e);
	}
	return isOk;
}

function onChangeTransportadoraPlaca() {
	try {
		var obj = app.getBean();
		if (obj != null && obj.getEntityClass().getName().equals("1236_compra")) {
			// DevEntityObject objChild = app.getChildBean();
			var placa = obj.getProp("descricao3");
			var veiculo = SmallUtil.getVeiculo(placa, app);
			if (veiculo != null) {
				obj.getPropObj("transporta").setVal(veiculo.getProp("nome"));
			}
		}
	} catch (e) {
		stack(e);
	}
}

function onLoad() {
	try {
		var obj = app.getBean();
		if (obj != null && obj.getEntityClass().getName().equals("1236_compra")) {
			if (obj.getProp('saidah') == null) {
				onNew();
			} else {
				produtoTotalCalc();
			}
		}
	} catch (e) {
		stack(e);
	}
}

function onNew() {
	try {
		var obj = app.getBean();
		if (obj != null && obj.getEntityClass().getName().equals("1236_compra")) {
			var now = new java.util.Date();
			obj.getPropObj("emissao").setPropertyValue(now);
			obj.getPropObj("saidad").setPropertyValue(now);
			obj.getPropObj("saidah").setPropertyValue(new java.text.SimpleDateFormat("HH:mm:ss").format(now));
			obj.getPropObj("volumes").setPropertyValue(0);
			obj.getPropObj("total").setPropertyValue(0);
			obj.getPropObj("pesoliqui").setPropertyValue(0);
			obj.getPropObj("especie").setPropertyValue("Caixa");
			obj.getPropObj("marca").setPropertyValue("Volumes");
			obj.getPropObj("modelo").setPropertyValue("55");
			produtoTotalCalc();
		}
	} catch (e) {
		stack(e);
	}
}

function hasNumeronf(numeronf, nomeFornecedor){
	var hasnumeronf = "";
	try {
		if (numeronf !== "" && nomeFornecedor !== "") {
			try {
				hasnumeronf = app.getBaseDao().getEntityManagerSmall().createNativeQuery(
						"select NUMERONF from COMPRAS WHERE NUMERONF LIKE '%" + numeronf + "' AND FORNECEDOR LIKE '" + nomeFornecedor + "';")
						.getSingleResult();
			} catch (e) {
				// println(e);
			}
		}
	} catch (e) {
		println(e);
		// numeronf = 0;
	}	
	return (hasnumeronf !== ""); 
}

function onBlurNumeroNf() {
	try {
		var obj = app.getBean();
		if (obj != null && obj.getEntityClass().getName().equals("1236_compra")) {
			var numeronf = "";
			var nomeFornecedor = "";
			var hasnumeronf = "";
			try {
				nomeFornecedor = obj.getProp("fornecedor", "");
				println(nomeFornecedor);
				numeronf = obj.getProp("numeronf", "");
				if (numeronf !== "" && nomeFornecedor !== "") {
					try {
						hasnumeronf = app.getBaseDao().getEntityManagerSmall().createNativeQuery(
								"select NUMERONF from COMPRAS WHERE NUMERONF LIKE '%" + numeronf + "' AND FORNECEDOR LIKE '" + nomeFornecedor + "';")
								.getSingleResult();
					} catch (e) {
						// println(e);
					}
				}
			} catch (e) {
				println(e);
				// numeronf = 0;
			}
			if (hasNumeronf(numeronf, nomeFornecedor)) {
				obj.getPropObj("numeronf").setPropertyValue("");
				var message = "NFe já cadastrada para este fornecedor!";
				map.put("message", message);
				map.put("callback", "restartFormChanged();focusBean('.c_numeronf .c_input');");
				println(message);
				// var FacesMessage = javax.faces.application.FacesMessage;
				// var FacesContext = javax.faces.context.FacesContext;
				// var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
				// msgTmp, "");
				// FacesContext.getCurrentInstance().addMessage(null, msg);
			} else if (numeronf != "") {
				numeronf = Long.valueOf(numeronf);
				println(numeronf);
				obj.getPropObj("numeronf").setPropertyValue(java.lang.String.format("%012d", numeronf));
				return true;
			} else {
				obj.getPropObj("numeronf").setPropertyValue("");
			}
		}
	} catch (e) {
		stack(e);
	}
	return false;
}

function onChangeProduto() {
	try {
		var obj = app.getBean();
		if (obj != null && obj.getEntityClass().getName().equals("1236_compra")) {
			var objChild = app.getChildBean();

			var nomeFornecedor = obj.getProp("fornecedor");
			var fornecedor = SmallUtil.getCliente(nomeFornecedor, app);

			var hasFornecedor = fornecedor != null;
			var ufEmissor = "ES";
			var ufFornecedor = "";
			if (hasFornecedor) {
				ufFornecedor = fornecedor.getProp("estado");
				populateOperacao(ufEmissor, ufFornecedor);
			}
			var codigo = objChild.getProp("produtos.codigo");
			if (!Objects.toString(codigo, "").isEmpty()) {
				var produto = SmallUtil.getProduto(codigo, app);
				objChild.getPropObj("descricao").setVal(produto.getProp("descricao"));
				if(objChild.getPropObj(campoVinculo)!==null){
					return;
				}
				var aliqIcms = produto.getProp("livre1");
				if (!Objects.toString(aliqIcms, "").isEmpty()) {
					objChild.getPropObj("icm").setVal(aliqIcms);
				}
				
			}
			var nomeOperacao = obj.getProp("operacao");
			var operacao = SmallUtil.getOperacao(nomeOperacao, app);
			if (operacao != null) {
				if (hasFornecedor) {
					objChild.getPropObj("icm").setVal(operacao.getProp(Objects.toString(ufFornecedor, "").toLowerCase()));
				}
				var cfopItem = objChild.getProp("produtos.cfop");
				if (cfopItem === null || cfopItem === "") {
					objChild.getPropObj("produtos.cfop").setVal(operacao.getProp('cfop'));
				}

			}
			precoProdutoGen(app);
		}
	} catch (e) {
		stack(e);
	}
}
function checkDentroEstado(){
	var obj = app.getBean();
	var nomeFornecedor = obj.getProp("fornecedor");
	var fornecedor = SmallUtil.getCliente(nomeFornecedor, app);

	var hasFornecedor = fornecedor != null;
	var ufEmissor = "ES";
	var ufFornecedor = "";
	if (hasFornecedor) {
		ufFornecedor = fornecedor.getProp("estado");
		//populateOperacao(ufEmissor, ufFornecedor);
		return ufEmissor == ufFornecedor;
	}
	return null;
}
function populateOperacao() {
	try {
		var obj = app.getBean();
		if (obj != null && obj.getEntityClass().getName().equals("1236_compra")) {

			var beanListMap = app.getBeanListMap();
			var icmListDE = beanListMap.get('icmListDE');
			var icmListFE = beanListMap.get('icmListFE');
			var isDentroEstado = checkDentroEstado();
			if (isDentroEstado !== null) {
				// println('#### ufEmissor/ufFornecedor do
				// estado!!'+ufEmissor+"/"+ufFornecedor);
				var icmList = app.doBeanList('1236_icm');
				//var isDentroEstado = (ufEmissor == ufFornecedor);
				if (icmListDE === null || icmListFE === null) {
					var icmListOriginal = icmList;
					beanListMap.put("icmListOriginal", app.doBeanList('1236_icm'));
					icmListDE = new ArrayList();
					icmListFE = new ArrayList();
					for (var x = 0; x < icmList.size(); x++) {
						var devEntityObject = icmList.get(x);
						var cfopItem = devEntityObject.getProp("cfop");
						if (Objects.toString(cfopItem).startsWith("1")) {
							icmListDE.add(devEntityObject);
						} else if (Objects.toString(cfopItem).startsWith("2")) {
							icmListFE.add(devEntityObject);
						}
					}
					beanListMap.put("icmListDE", icmListDE);
					beanListMap.put("icmListFE", icmListFE);
				}
				if (isDentroEstado) {
					// println('#### Dentro do estado!!'+icmListDE.size());
					beanListMap.put("1236_icm", icmListDE);
				} else {
					// println('#### Fora do estado!!'+icmListFE.size());
					beanListMap.put("1236_icm", icmListFE);
				}
				if (app.doBeanList('1236_icm') != icmList) {
					// println('#### Restart Cache!!');
					map.put("callback", "updateCache();");
				}
			}
		}
	} catch (e) {
		stack(e);
	}
}

function precoProdutoGen() {
	precoProdutoGenImpl(app, app.getChildBean(), true);
}

function precoProdutoGenImpl(app, objChild, totalCalc) {
	try {
		var obj = app.getBean();
		if (obj != null && obj.getEntityClass().getName().equals("1236_compra")) {
			// DevEntityObject objChild = app.getChildBean();
			var codigo = objChild.getProp("produtos.codigo");
			if (!Objects.toString(codigo, "").isEmpty()) {
				var devEntityObject = SmallUtil.getProduto(codigo, app);
				// Object tipoPreco =
				// obj.getPropObj("identificador1").getVal();

				var preco = devEntityObject.getProp("custocompr");

				if (preco == null) {
					preco = 0;
				}
				// DevEntityObject clienteObj =
				// SmallUtil.getCliente(obj.getProp("cliente"), app);
				// Number desconto = AlgoUtil.toNumber(clienteObj != null ?
				// clienteObj.getProp("identificador2") : null);
				// Double valorDesconto = total *
				// (desconto!=null?desconto.doubleValue()/100:0);
				objChild.getPropObj("unitario").setVal(BigDecimal.valueOf(preco).setScale(2, RoundingMode.HALF_UP).doubleValue());
				objChild.getPropObj("descricao").setVal(devEntityObject.getProp("descricao"));
				if (objChild.getProp("base") == null) {
					var baseProduto = devEntityObject.getProp("livre2", "").toString();
					if (!baseProduto.isEmpty()) {
						objChild.getPropObj("base").setVal(baseProduto);
					} else {
						objChild.getPropObj("base").setVal(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP).doubleValue());
					}
				}

				objChild.getPropObj("ipi").setVal(devEntityObject.getProp("ipi"));

				// println(devEntityObject.getProp("ipi"));
				// objChild.getPropObj("peso").setVal(devEntityObject.getProp("peso"));
				// objChild.getPropObj("cfop").setVal(devEntityObject.getProp("cfop"));
				if (totalCalc) {
					produtoTotalCalc();
				}
			}
		}
	} catch (e) {
		stack(e);
	}
}

function jsonToEntObj(json, entObj) {
	for ( var key in json) {
		try {			
			entObj.getPropObj(key).setPropertyValue(json[key]);
		} catch (e) {
			stack("Error in jsonToEntObj: "+key);
			throw e;
		}
	}
}
function convertCfop(codOper, isDentroEstado){
	var suffixCfop = codOper.toString().substring(1,4);
	if(isDentroEstado){
		return "1"+suffixCfop;
	}else{
		return "2"+suffixCfop;
	}	
}
var campoVinculo = SmallUtil.CAMPO_VINCULO_PRODUTO;
function importXmlNfe() {
	try {
		var obj = app.getBean();
		if (obj != null && obj.getEntityClass().getName().equals("1236_compra")) {
			// DevEntityObject objChild = app.getChildBean();
			var nfeHelp = com.algoboss.integration.fazenda.gov.NFeXmlHelp;
			var tNfeProc = nfeHelp.generate(new java.lang.String(obj.getPropObj("nfexml").getPropertyFile()));
			var infNFe = tNfeProc.getNFe().getInfNFe();
			var detalhe = infNFe.getDet();
			var ideNFe = infNFe.getIde();
			var emitNFe = infNFe.getEmit();
			var tot = infNFe.getTotal().getICMSTot();
			var nfeId = infNFe.getId().replace('NFe','');
			var numeronf = ideNFe.getNNF();
			var serienf = java.lang.String.format("%03d", Long.valueOf(ideNFe.getSerie()));
			var natOpeNf = "";
			var dhEmiNf = ideNFe.getDhEmi()!=null?new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(ideNFe.getDhEmi()):null;
			var dhSaiEnt = ideNFe.getDhSaiEnt()!=null?new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(ideNFe.getDhSaiEnt()):null;
			var transp = infNFe.getTransp();
			var volArray = transp.getVol();

			var compras = new Compras();
			compras.nfeid = nfeId;
			compras.numeronf = java.lang.String.format("%012d", Long.valueOf(numeronf + new java.lang.String(serienf)));
			compras.modelo = ideNFe.getMod();
			compras.emissao = dhEmiNf;
			compras.saidad = dhSaiEnt;
			compras.saidah = dhSaiEnt!=null?new java.text.SimpleDateFormat("HH:mm:ss").format(dhSaiEnt):null;
			
			//###TOTAL
			compras.total = tot.getVNF();
			// compras.descricao3
			compras.icms = tot.getVICMS();
			compras.ipi = tot.getVIPI();
			compras.baseicm = tot.getVBC();
			compras.mercadoria = tot.getVNF();
			
			for (var volIdx = 0; volIdx < volArray.size(); volIdx++) {
				var vol = volArray.get(volIdx);
				//compras.volumes = new Double(Objects.toString(compras.volumes,"0")) + new Double(vol.getQVol());
				compras.pesoliqui = new Double(Objects.toString(compras.pesoliqui,"0")) + new Double(Objects.toString(vol.getPesoL(),"0"));				
				
			}
		
			compras.volumes = detalhe.size();//new Double(Objects.toString(compras.volumes,"0")) + new Double(vol.getQVol());
			compras.operacao = ideNFe.getNatOp();
			compras.fornecedor = emitNFe.getXNome();
			var cnpj = emitNFe.getCNPJ();
			compras.produtos = new ArrayList();
			var fornecedor = SmallUtil.getClienteByInscricao(cnpj, app);
			if(fornecedor != null){
				nomeFornecedor = fornecedor.getProp("nome");
				if (hasNumeronf(compras.numeronf, nomeFornecedor)) {
					var message = "NFe "+compras.numeronf+" do Fornecedor "+nomeFornecedor+" já cadastrada!";
					map.put("message", message);
					map.put("callback", "restartFormChanged();focusBean('.c_numeronf .c_input');");
					println(message);				
					return;
				}
			}
			jsonToEntObj(compras, obj);
			var msgRetorno = "";
			var isDentroEstado = null;
			if (fornecedor == null) {
				msgRetorno += "Fornecedor não encontrado! Nome: " + compras.fornecedor + "\\n";
			}else{
				obj.getPropObj("fornecedor").setVal(fornecedor.getProp("nome"));
				isDentroEstado = checkDentroEstado();
			}

			
			// produto.codigo
			var objChildList = obj.getProp("produtos");
			var produtos = new ArrayList();
			obj.getPropObj("produtos").setVal(produtos);
			for (var x = 0; x < detalhe.size(); x++) {
				var detItem = detalhe.get(x);
				var produto = new Produto();
				var prod = detItem.getProd();
				var imposto = detItem.getImposto();

				produto.quantidade = prod.getQCom();
				produto.unitario = prod.getVUnCom();
				produto.codigo = prod.getCProd();
				produto.cfop = convertCfop(prod.getCFOP(), isDentroEstado);
				produto.total = prod.getVProd();
				produto.numeronf = compras.numeronf;
				
				//CÁLCULO ICMS
				var icms = imposto.getICMS();
				
				var icms00 = icms.getICMS00();
				if(icms00){
					produto.vicms = icms00.getVICMS();
					produto.icm = icms00.getPICMS();
					produto.base = icms00.getVBC() / produto.total * 100;
					produto.vbc = icms00.getVBC();
				}
				
				var icms10 = icms.getICMS10();
				if(icms10){
					produto.vicms = icms10.getVICMS();
					produto.icm = icms10.getPICMS();
					produto.base = icms10.getVBC() / produto.total * 100;	
					produto.vbc = icms10.getVBC();
				}
				var icms20 = icms.getICMS20();
				if(icms20){
					produto.vicms = icms20.getVICMS();
					produto.icm = icms20.getPICMS();
					produto.base = icms20.getVBC() / produto.total * 100;	
					produto.vbc = icms20.getVBC();
				}
				var icms30 = icms.getICMS30();
				if(icms30){
					produto.vicmss = icms30.getVICMSST();
					produto.icm = icms30.getPICMSST();
					produto.vbcst = icms30.getVBCST();
					produto.base = icms30.getVBCST() / produto.total * 100;		
				}				
				var icms40 = icms.getICMS40();
				if(icms40){
					//msgRetorno += "Tributação do ICMS não suportado! Código: 40 \\n";
					//Imunidade, Não Incidência ou Desoneração
				}						
				var icms50 = icms.getICMS51();
				if(icms50){
					//Diferimento (Adiamento)
					//msgRetorno += "Tributação do ICMS não suportado! Código: 50 \\n";
				}						
				var icms60 = icms.getICMS60();
				if(icms60){
					msgRetorno += "Tributação do ICMS não suportado! Código: 60 \\n";
				}						
				var icms70 = icms.getICMS70();
				if(icms70){
					msgRetorno += "Tributação do ICMS não suportado! Código: 70 \\n";
				}						
				var icms90 = icms.getICMS90();
				if(icms90){
					msgRetorno += "Tributação do ICMS não suportado! Código: 90 \\n";
				}		
				//nfe id
				//pis
				//cofins
				
				/*
                    "icms00",
                    "icms10",
                    "icms20",
                    "icms30",
                    "icms40",
                    "icms51",
                    "icms60",
                    "icms70",
                    "icms90",
                    "icmsPart",
                    "icmsst",
                    "icmssn101",
                    "icmssn102",
                    "icmssn201",
                    "icmssn202",
                    "icmssn500",
                    "icmssn900"				 
				 * 
				 */
				//OPEN CÁLCULO IPI
				var ipi = imposto.getIPI();
				var IPITrib = ipi.getIPITrib();
				if(IPITrib){
					produto.ipi = IPITrib.getPIPI();
					produto.vipi = IPITrib.getVIPI();
				}else{
					produto.ipi = 0;
					produto.vipi = 0;					
				}
				
				var IPINT = ipi.getIPINT();
				//CLOSE CÁLCULO IPI
				
				
				//OPEN CÁLCULO PIS
				var pis = imposto.getPIS();
				var pisAliq = pis.getPISAliq();
				var pisOutr = pis.getPISOutr();
				if(pisAliq){
					produto.aliqpis = pisAliq.getPPIS();
				}
				if(pisOutr){
					produto.aliqpis = pisOutr.getPPIS();
				}	
				//CLOSE CÁLCULO PIS
				
				//OPEN CÁLCULO COFINS
				var cofins = imposto.getCOFINS();
				var cofinsAliq = cofins.getCOFINSAliq();
				var cofinsOutr = cofins.getCOFINSOutr();
				if(cofinsAliq){
					produto.aliqcofins = cofinsAliq.getPCOFINS();
				}
				if(cofinsOutr){
					produto.aliqcofins = cofinsOutr.getPCOFINS();
				}	
				//CLOSE CÁLCULO COFINS				
				
				
				produto.descricao = prod.getXProd();

				if(isDentroEstado != null){
					var operacao = SmallUtil.getOperacaoByCodigo(produto.cfop, app);
					if(x == 0){
						if (operacao == null) {
							msgRetorno += "Operação não encontrada! Nome: " + compras.operacao + "\\n";
						}else{
							obj.getPropObj("operacao").setVal(operacao.getProp("nome"));
						}							
					}
					if (operacao == null) {
						msgRetorno += "CFOP não encontrado! Código: " + produto.cfop + "\\n";
					}
				}				
				var bool = app.newChildBean("1236_compra.produtos");
				if (bool) {
					var vinculoProduto = cnpj + ":" + produto.codigo;
					var devEntityObjectProd = SmallUtil.getProdutoByVinculo(vinculoProduto, app);
					var objChild = app.getChildBean();
					jsonToEntObj(produto, objChild);
					produtos.add(objChild);
					if (devEntityObjectProd == null) {
						// onChangeProduto();
						objChild.setProp(campoVinculo, vinculoProduto);
						objChild.getPropObj("codigo").setVal("");
						msgRetorno += "Produto não cadastrado ou não vinculado! Código: " + produto.codigo +" | Descrição: "+produto.descricao+ "\\n";
					}else{
						objChild.getPropObj("codigo").setVal(devEntityObjectProd.getProp("codigo"));
						objChild.getPropObj("descricao").setVal(devEntityObjectProd.getProp("descricao"));
					}
				}
			}

			
			//produtoTotalCalc();
			// compras.volumes =
			if(msgRetorno){
				map.put("callback", "alert('" + msgRetorno + "');");
			}
			//obj.getPropObj("nfexml").setVal(null);
			// var placa = obj.getProp("descricao3");
			// var veiculo = SmallUtil.getVeiculo(placa, app);
			// if (veiculo != null) {
			// obj.getPropObj("transporta").setVal(veiculo.getProp("nome"));
			// }
		}
	} catch (e) {
		stack(e);
	}
}

function stack(e){
	println(e);
	println(e.stack);
	println('Line Number:' + e.lineNumber);
	println('Column Number:' + e.columnNumber);
	println('File Name:' + e.fileName);	
}



function atualizarCadastroEstoque(update){ 
	var obj = app.getBean();
	var produtos = obj.getProp("produtos");
	var hasToUpdate = false;
	for (var x = 0; x < produtos.size(); x++) {
		var produto = produtos.get(x);
		var vinculoProduto = produto.getPropObj(campoVinculo);
		var codigo = produto.getProp("codigo");
		try {
			if(vinculoProduto!==null && vinculoProduto!='' && codigo!='' && codigo!==null){
				hasToUpdate = true;
				if(update){
					vinculoProduto = vinculoProduto.getVal();
					app.getBaseDao().getSmallDao().executeQuery("ALTER TABLE ESTOQUE ALTER "+campoVinculo+" TYPE Varchar(2000);");
					app.getBaseDao().getSmallDao().executeQuery(
							"update ESTOQUE set "+campoVinculo+" = coalesce(replace("+campoVinculo+",'"+vinculoProduto+";',''),'')||'"+vinculoProduto+";' where CODIGO = '" + codigo + "';");
					produto.getPropObj(campoVinculo).setVal("");
					var devEntityObject = app.doBeanRefresh(SmallUtil.getProduto(codigo, app));
					
					devEntityObject.getPropObj(campoVinculo).setVal(Objects.toString(devEntityObject.getProp(campoVinculo,"")).replaceAll(vinculoProduto+";","")+vinculoProduto+";");
					map.put("callback", "updateStartPage();");
				}
			}
		} catch (e) {
			
			stack(e);
		}		
	}
	return hasToUpdate;
}

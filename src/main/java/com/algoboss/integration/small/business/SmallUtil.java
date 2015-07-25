package com.algoboss.integration.small.business;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

import org.simpleframework.xml.core.Persister;

import com.algoboss.erp.entity.DevEntityClass;
import com.algoboss.erp.entity.DevEntityObject;
import com.algoboss.erp.entity.DevEntityPropertyDescriptor;
import com.algoboss.erp.entity.DevEntityPropertyDescriptorConfig;
import com.algoboss.erp.entity.DevEntityPropertyValue;
import com.algoboss.erp.entity.DevReportFieldContainer;
import com.algoboss.erp.entity.DevReportFieldOptions;
import com.algoboss.erp.entity.DevReportFieldOptionsMap;
import com.algoboss.erp.entity.DevRequirement;
import com.algoboss.erp.face.AdmAlgoappBean;
import com.algoboss.erp.face.AdmAlgoreportBean;
import com.algoboss.erp.util.AlgoUtil;
import com.algoboss.erp.util.AlgodevUtil;
import com.algoboss.erp.util.nfe.ChaveAcessoNFe;
import com.algoboss.integration.fazenda.gov.TEnderEmi;
import com.algoboss.integration.fazenda.gov.TEndereco;
import com.algoboss.integration.fazenda.gov.TIpi;
import com.algoboss.integration.fazenda.gov.TIpi.IPITrib;
import com.algoboss.integration.fazenda.gov.TNFe;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Dest;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det.Imposto;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det.Imposto.COFINS;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det.Imposto.COFINS.COFINSOutr;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det.Imposto.ICMS;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det.Imposto.ICMS.ICMS00;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det.Imposto.PIS;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det.Imposto.PIS.PISAliq;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det.Imposto.PIS.PISOutr;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Det.Prod;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Emit;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Ide;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Total;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Total.ICMSTot;
import com.algoboss.integration.fazenda.gov.TNFe.InfNFe.Transp;
import com.algoboss.integration.fazenda.gov.TNfeProc;
import com.algoboss.integration.fazenda.gov.TUf;
import com.algoboss.integration.fazenda.gov.TUfEmi;
import com.algoboss.integration.ssl.DynamicSocketFactory;
import com.fincatto.nfe310.FabricaDeObjetosFake;
import com.fincatto.nfe310.NFeConfig;
import com.fincatto.nfe310.classes.NFAmbiente;
import com.fincatto.nfe310.classes.NFProtocolo;
import com.fincatto.nfe310.classes.NFTipoEmissao;
import com.fincatto.nfe310.classes.NFUnidadeFederativa;
import com.fincatto.nfe310.classes.lote.consulta.NFLoteConsultaRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvio;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.nfe310.classes.nota.NFNota;
import com.fincatto.nfe310.classes.nota.NFNotaProcessada;
import com.fincatto.nfe310.transformers.NFRegistryMatcher;
import com.fincatto.nfe310.webservices.WSFacade;

public class SmallUtil {
	public static void preRender(AdmAlgoappBean app) {

		// totalCalc(obj);
	}
	public static void produtoCalc(AdmAlgoappBean app) {
		produtoTotalCalcImpl(app,false);
	}
	public static void produtoTotalCalc(AdmAlgoappBean app) {
		produtoTotalCalcImpl(app,false);
	}
	public static void produtoTotalCalcImpl(AdmAlgoappBean app,boolean precoRecalc) {
		try {
			DevEntityObject obj = app.getBean();
			if (obj != null && obj.getEntityClass().getName().equals("1236_venda")) {
				// volumesCalc(obj);
				DevEntityObject objChild = app.getChildBean();
				if(objChild!=null && !objChild.getEntityPropertyValueList().isEmpty()){
					if(!putProdutoTotal(app, objChild)){
						return;
					}
				}else{
					objChild = null;
				}
				List<DevEntityObject> objChildList = (List<DevEntityObject>) obj.getProp("produtos");
				if (objChild!=null && !objChildList.contains(objChild)) {
					objChildList.add(objChild);
				}
				Integer volumes = 0;
				Double total = 0d;
				Double pesoLiquido = 0d;	
				for (DevEntityObject devEntityObject : objChildList) {
					if(precoRecalc){
						precoProdutoGenImpl(app,devEntityObject,false);						
					}
					putProdutoTotal(app,devEntityObject);
					volumes++;
					Object totalObj = devEntityObject.getPropObj("produtos.total").getPropertyValue();
					if (totalObj != null) {
						total += (Double) totalObj;
					}
					Object pesoLiquidoObj = devEntityObject.getPropObj("produtos.peso").getPropertyValue();
					if (pesoLiquidoObj != null) {
						pesoLiquido += (Double) pesoLiquidoObj;
					}					
				}
				obj.getPropObj("volumes").setPropertyValue(volumes);
				obj.getPropObj("total").setPropertyValue(BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).doubleValue());
				obj.getPropObj("pesoliqui").setPropertyValue(BigDecimal.valueOf(pesoLiquido).setScale(2, RoundingMode.HALF_UP).doubleValue());
				//obj.getPropObj("desconto").setPropertyValue(BigDecimal.valueOf(valorDesconto).setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean putProdutoTotal(AdmAlgoappBean app, DevEntityObject objChild) {
		//DevEntityObject obj = app.getBean();
		Double quantidade = (Double) objChild.getProp("produtos.quantidade");
		Double unitario = (Double) objChild.getProp("produtos.unitario");
		if (quantidade == null || unitario == null) {
			return false;
		}		
		Object codigo = objChild.getProp("produtos.codigo");
		DevEntityObject produto = SmallUtil.getProduto(codigo, app);
		Double peso = (Double) produto.getProp("peso");
		if(peso==null){
			peso = 0d;
		}
		Double totalProduto = BigDecimal.valueOf(quantidade * unitario).setScale(2, RoundingMode.HALF_UP).doubleValue();
		Double pesoProduto = BigDecimal.valueOf(quantidade * peso).setScale(2, RoundingMode.HALF_UP).doubleValue();
		objChild.getPropObj("produtos.total").setPropertyValue(totalProduto);	
		objChild.getPropObj("produtos.peso").setPropertyValue(pesoProduto);
		return true;
	}

	public static void descontoCliente(AdmAlgoappBean app) {
		try {
			DevEntityObject obj = app.getBean();
			if (obj != null && obj.getEntityClass().getName().equals("1236_venda")) {
				//DevEntityObject objChild = app.getChildBean();
				Object cliente = obj.getProp("cliente");
				if (!Objects.toString(cliente, "").isEmpty()) {
					DevEntityObject clienteObj = getCliente(cliente, app);
					obj.getPropObj("duplicatas").setVal(Objects.toString(clienteObj.getProp("identificador1"), "").split(Pattern.quote("/")).length );
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void setTransportadoraPlaca(AdmAlgoappBean app) {
		try {
			DevEntityObject obj = app.getBean();
			if (obj != null && obj.getEntityClass().getName().equals("1236_venda")) {
				//DevEntityObject objChild = app.getChildBean();
				Object placa = obj.getProp("placa");
				if (!Objects.toString(placa, "").isEmpty()) {
					List<DevEntityObject> transportadoras = app.doBeanList("1236_transpor");
					for (DevEntityObject devEntityObject : transportadoras) {
						Object nomeTransportadora = devEntityObject.getProp("nome");
						Object placaTmp = devEntityObject.getProp("placa");
						if (placaTmp!=null && placaTmp.equals(placa)) {
							obj.getPropObj("transporta").setVal(nomeTransportadora);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		
	
	public static void numeroNfGen(AdmAlgoappBean app) {
		try {
			DevEntityObject obj = app.getBean();
			if (obj != null && obj.getEntityClass().getName().equals("1236_venda")) {
				Object numeronf = obj.getPropObj("numeronf").getPropertyValue();
				if (Objects.toString(numeronf, "").isEmpty()) {
					
					Object nextnumeronf = null;
					try {
						nextnumeronf = app.getBaseDao().getEntityManagerSmall().createNativeQuery("SELECT GEN_ID(G_SERIE1, 0 )||'001' FROM RDB$DATABASE where not exists(select NUMERONF from VENDAS WHERE NUMERONF LIKE '%'||(GEN_ID(G_SERIE1, 0 ))||'001');").getSingleResult();
					} catch (NoResultException e) {
						nextnumeronf = app.getBaseDao().getEntityManagerSmall().createNativeQuery("SELECT NEXT VALUE FOR G_SERIE1 FROM RDB$DATABASE;").getSingleResult().toString().concat("001");
					}
					obj.getPropObj("numeronf").setPropertyValue(String.format ("%012d", Integer.valueOf(nextnumeronf.toString())));
					obj.getPropObj("especie").setPropertyValue("Caixa");
					obj.getPropObj("marca").setPropertyValue("Volumes");
					obj.getPropObj("frete12").setPropertyValue("0");
					
					obj.getPropObj("identificador1").setPropertyValue("PREÇO1");
					Date now = new Date();
					obj.getPropObj("emissao").setPropertyValue(now);
					obj.getPropObj("saidad").setPropertyValue(now);
					obj.getPropObj("saidah").setPropertyValue(new SimpleDateFormat("HH:mm:ss").format(now));
					obj.getPropObj("volumes").setPropertyValue(0d);
					obj.getPropObj("total").setPropertyValue(0d);
					obj.getPropObj("pesoliqui").setPropertyValue(0d);						
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public static void numeroOsGen(AdmAlgoappBean app) {
		try {
			DevEntityObject obj = app.getBean();
			if (obj != null && obj.getEntityClass().getName().equals("1236_ordemservico")) {
				Object numero = obj.getPropObj("numero").getPropertyValue();
				if (Objects.toString(numero, "").isEmpty()) {
					
					Object nextnumeroos = null;
					try {
						nextnumeroos = app.getBaseDao().getEntityManagerSmall().createNativeQuery("SELECT GEN_ID(G_NUMEROOS, 0 ) FROM RDB$DATABASE where not exists(select NUMERO from OS WHERE NUMERO LIKE '%'||(GEN_ID(G_NUMEROOS, 0 )));").getSingleResult();
					} catch (NoResultException e) {
						nextnumeroos = app.getBaseDao().getEntityManagerSmall().createNativeQuery("SELECT NEXT VALUE FOR G_NUMEROOS FROM RDB$DATABASE;").getSingleResult().toString();
					}
					obj.getPropObj("numero").setPropertyValue(String.format ("%010d", Integer.valueOf(nextnumeroos.toString())));
					//obj.getPropObj("especie").setPropertyValue("Caixa");
					//obj.getPropObj("marca").setPropertyValue("Volumes");
					//obj.getPropObj("frete12").setPropertyValue("0");
					
					//obj.getPropObj("tipopreco").setPropertyValue("PREÇO1");
					Date now = new Date();
					obj.getPropObj("data").setPropertyValue(now);
					obj.getPropObj("hora").setPropertyValue(new SimpleDateFormat("HH:mm").format(now));
					obj.getPropObj("datapro").setPropertyValue(now);
					obj.getPropObj("horapro").setPropertyValue(new SimpleDateFormat("HH:mm").format(now));
					obj.getPropObj("situacao").setPropertyValue("AGENDADA");
					obj.getPropObj("identifi1").setPropertyValue("Interno");				
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public static void precoProdutoGen(AdmAlgoappBean app) {
		precoProdutoGenImpl(app,app.getChildBean(), true);
	}

	public static void precoProdutoGenImpl(AdmAlgoappBean app, DevEntityObject objChild,  boolean totalCalc) {
		try {
			DevEntityObject obj = app.getBean();
			if (obj != null && obj.getEntityClass().getName().equals("1236_venda")) {
				//DevEntityObject objChild = app.getChildBean();
				Object codigo = objChild.getProp("produtos.codigo");
				if (!Objects.toString(codigo, "").isEmpty()) {
					DevEntityObject devEntityObject = SmallUtil.getProduto(codigo, app);
					Object tipoPreco = obj.getPropObj("identificador1").getVal();
					
					Double preco;
					if (tipoPreco == null || tipoPreco.equals("PREÇO1")) {
						preco = (Double)devEntityObject.getProp("preco");
					} else {
						preco = (Double)AlgoUtil.toNumber(devEntityObject.getProp("livre2"));
					}
					if(preco==null){
						preco = 0d;
					}
					DevEntityObject clienteObj = getCliente(obj.getProp("cliente"), app);
					Number desconto = AlgoUtil.toNumber(clienteObj!=null?clienteObj.getProp("identificador2"):null);
					//Double valorDesconto = total * (desconto!=null?desconto.doubleValue()/100d:0d);
					objChild.getPropObj("unitario").setVal(BigDecimal.valueOf(preco*(desconto!=null?1-desconto.doubleValue()/100d:1d)).setScale(2, RoundingMode.HALF_UP).doubleValue());
					objChild.getPropObj("descricao").setVal(devEntityObject.getProp("descricao"));
					
					//objChild.getPropObj("peso").setVal(devEntityObject.getProp("peso"));
					//objChild.getPropObj("cfop").setVal(devEntityObject.getProp("cfop"));
					if (totalCalc) {
						produtoTotalCalc(app);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DevEntityObject getProduto(Object codigo,AdmAlgoappBean app){
		DevEntityObject devEntityObjectReturn = null;
		if(codigo!=null){
			List<DevEntityObject> produtos = app.doBeanList("1236_estoque");
			for (DevEntityObject devEntityObject : produtos) {
				if (codigo.equals(devEntityObject.getProp("codigo"))) {
					devEntityObjectReturn = devEntityObject;
					break;
				}
			}	
		}
		return devEntityObjectReturn;
	}
	
	public static final String CAMPO_VINCULO_PRODUTO = "livre4";
	
	public static DevEntityObject getProdutoByVinculo(Object vinculoProduto,AdmAlgoappBean app){
		DevEntityObject devEntityObjectReturn = null;
		if(vinculoProduto!=null){
			List<DevEntityObject> produtos = app.doBeanList("1236_estoque");
			for (DevEntityObject devEntityObject : produtos) {
				if (Objects.toString(devEntityObject.getProp(CAMPO_VINCULO_PRODUTO)).contains(Objects.toString(vinculoProduto))) {
					devEntityObjectReturn = devEntityObject;
					break;
				}
			}	
		}
		return devEntityObjectReturn;
	}	
	public static DevEntityObject getCliente(Object codigo,AdmAlgoappBean app){
		DevEntityObject devEntityObjectReturn = null;
		if(codigo!=null){
			List<DevEntityObject> clientes = app.doBeanList("1236_clifor");
			for (DevEntityObject devEntityObject : clientes) {
				if (codigo.equals(devEntityObject.getProp("nome"))) {
					devEntityObjectReturn = devEntityObject;
					break;
				}
			}
		}
		return devEntityObjectReturn;
	}
	public static DevEntityObject getClienteByInscricao(Object CNPJ,AdmAlgoappBean app){
		DevEntityObject devEntityObjectReturn = null;
		if(CNPJ!=null){
			List<DevEntityObject> clientes = app.doBeanList("1236_clifor");
			for (DevEntityObject devEntityObject : clientes) {	
				//System.out.println(Objects.toString(devEntityObject.getProp("cgc")).replaceAll("\\D", ""));
				if (CNPJ.equals(Objects.toString(devEntityObject.getProp("cgc")).replaceAll("\\D", ""))) {
					devEntityObjectReturn = devEntityObject;
					break;
				}
			}
		}
		return devEntityObjectReturn;
	}	
	public static DevEntityObject getVeiculo(Object placa,AdmAlgoappBean app){
		DevEntityObject devEntityObjectReturn = null;
		if (!Objects.toString(placa, "").isEmpty()) {
			List<DevEntityObject> transportadoras = app.doBeanList("1236_transpor");
			for (DevEntityObject devEntityObject : transportadoras) {
				if (placa.equals(devEntityObject.getProp("placa"))) {
					devEntityObjectReturn = devEntityObject;
					break;
				}
			}
		}
		return devEntityObjectReturn;
	}
	public static DevEntityObject getOperacao(Object nomeOperacao, AdmAlgoappBean app) {
		DevEntityObject devEntityObjectReturn = null;
		if (!Objects.toString(nomeOperacao, "").isEmpty()) {
			List<DevEntityObject> cfops = app.doBeanList("1236_icm");
			for (DevEntityObject devEntityObject : cfops) {
				if (nomeOperacao.equals(devEntityObject.getProp("nome"))) {
					devEntityObjectReturn = devEntityObject;
					break;
				}
			}
		}
		return devEntityObjectReturn;
	}
	public static DevEntityObject getOperacaoByCodigo(Object codOperacao, AdmAlgoappBean app) {
		DevEntityObject devEntityObjectReturn = null;
		if (!Objects.toString(codOperacao, "").isEmpty()) {
			List<DevEntityObject> cfops = app.doBeanList("1236_icm");
			for (DevEntityObject devEntityObject : cfops) {
				if (codOperacao.equals(Objects.toString(devEntityObject.getProp("cfop")).trim())) {
					devEntityObjectReturn = devEntityObject;
					break;
				}
			}
		}
		return devEntityObjectReturn;
	}	
	
	public static List<DevEntityObject> municipioList = null;
	
	public static DevEntityObject getMunicipioByNomeAndUf(String nomeMunicipio, String ufMunicipio, AdmAlgoappBean app){
		DevEntityClass municioEnt = new DevEntityClass();
		municioEnt.setCanonicalClassName("com.algoboss.integration.small.entity.Municipio");
		AlgodevUtil.populateEntityClassByClassName(municioEnt);
		if(municipioList==null){
			municipioList = app.findListByClass(municioEnt);
		}
		for (DevEntityObject devEntityObject : municipioList) {
			if (nomeMunicipio.equals(Objects.toString(devEntityObject.getProp("nome"))) && ufMunicipio.equals(Objects.toString(devEntityObject.getProp("uf")))) {
				return devEntityObject;
			}
		}
		return null;
	}
	
	public static TNFe convertVendasToNFe(DevEntityObject venda, AdmAlgoappBean app){
		TNFe tNFe = new TNFe();
		//TNfeProc tNfeProc = new TNfeProc();
		//tNfeProc.setNFe(tNFe);
		//tNfeProc.setVersao("3.10");
		//var vendas = new Vendas();
		//INFORMAÇÃO
		InfNFe infNFe = new TNFe.InfNFe();
		infNFe.setVersao("3.10");




		
		//REMETENTE
		Object[] emitente = null;
		try {
			emitente = (Object[])app.getBaseDao().getSmallDao().getEntityManager().createNativeQuery("SELECT a.NOME, a.CONTATO, a.ENDERECO, a.COMPLE, a.MUNICIPIO, a.CEP, a.ESTADO, a.CGC, a.IE, a.TELEFO FROM EMITENTE a;").getSingleResult();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Emit emitNFe = new Emit();
		String ufEmit = Objects.toString(emitente[6]);
		String cnpjEmit = Objects.toString(emitente[7]);
		infNFe.setEmit(emitNFe);
		emitNFe.setXNome(Objects.toString(emitente[0]).trim());
		emitNFe.setCNPJ(cnpjEmit.replaceAll("[\\D]", ""));
		emitNFe.setIE(Objects.toString(emitente[8]).replaceAll("[\\D]", ""));
		emitNFe.setCRT("1");//1=Simples Nacional;
		TEnderEmi enderEmit = new TEnderEmi();
		enderEmit.setUF(TUfEmi.fromValue(ufEmit));
		enderEmit.setCEP(Objects.toString(emitente[5]).replaceAll("[\\D]", ""));
		enderEmit.setXLgr(Objects.toString(emitente[2]).trim());
		enderEmit.setXMun(Objects.toString(emitente[4]));
		DevEntityObject municipioEmit = getMunicipioByNomeAndUf(Objects.toString(emitente[4]),ufEmit,app);
		enderEmit.setCMun(municipioEmit.getProp("codigo").toString());
		enderEmit.setXBairro(Objects.toString(emitente[3]).trim());
		enderEmit.setFone(Objects.toString(emitente[9]).replaceAll("[\\D]", ""));
		enderEmit.setCPais("1058");
		enderEmit.setXPais("BRASIL");
		enderEmit.setNro("0");
		emitNFe.setEnderEmit(enderEmit);
		
		//DESTINATÁRIO
		Dest destNFe = new Dest();
		infNFe.setDest(destNFe);
		DevEntityObject cliente = getCliente(venda.getProp("cliente").toString(), app);
		String ufDest = Objects.toString(cliente.getProp("estado"));
		destNFe.setXNome(cliente.getProp("nome").toString().trim());
		destNFe.setCNPJ(cliente.getProp("cgc").toString().replaceAll("[\\D]", ""));
		destNFe.setIE(cliente.getProp("ie").toString().replaceAll("[\\D]", ""));
		destNFe.setIndIEDest("1");//1=Contribuinte ICMS (informar a IE do destinatário);
		TEndereco enderDest = new TEndereco();
		DevEntityObject municipioDest = getMunicipioByNomeAndUf(Objects.toString(cliente.getProp("cidade")),ufDest,app);
		enderDest.setCEP(cliente.getProp("cep").toString().replaceAll("[\\D]", ""));
		enderDest.setCMun(municipioDest.getProp("codigo").toString());
		enderDest.setCPais("1058");
		enderDest.setFone(cliente.getProp("fone","").toString().replaceAll("[\\D]", ""));
		enderDest.setUF(TUf.fromValue(municipioDest.getProp("uf").toString()));
		enderDest.setXBairro(cliente.getProp("comple","").toString().trim());
		enderDest.setXLgr(cliente.getProp("endere","").toString().trim());
		enderDest.setXMun(municipioDest.getProp("nome").toString());
		enderDest.setXPais("BRASIL");
		enderDest.setNro("0");
		destNFe.setEnderDest(enderDest);
		
		//PRODUTOS
		Double pisTot = 0d;
		Double cofinsTot = 0d;
		List<Det> detalhe = infNFe.getDet();
		List<DevEntityObject> produtos = venda.getPropObj("produtos").getPropertyChildrenList();
		for (int x = 0; x < produtos.size(); x++) {
			DevEntityObject devEntityObject = produtos.get(x);
			DevEntityObject produto = getProduto(devEntityObject.getProp("codigo").toString(), app);
			Det detItem = new Det();
			detalhe.add(detItem);
			Prod prod = new Prod();
			prod.setQCom(devEntityObject.getProp("quantidade").toString());
			prod.setCProd(devEntityObject.getProp("codigo").toString());
			prod.setVProd(new BigDecimal(devEntityObject.getProp("total").toString()).setScale(2,RoundingMode.HALF_UP).toString());
			prod.setCFOP(devEntityObject.getProp("cfop").toString());
			prod.setXProd(produto.getProp("descricao").toString());
			prod.setNCM(produto.getProp("cf").toString());
			prod.setUCom(devEntityObject.getProp("medida").toString());
			prod.setVUnCom(new BigDecimal(devEntityObject.getProp("unitario").toString()).setScale(2,RoundingMode.HALF_UP).toString());
			prod.setUTrib(devEntityObject.getProp("medida").toString());
			prod.setIndTot("1");
			prod.setQTrib(devEntityObject.getProp("quantidade").toString());
			prod.setVUnTrib(new BigDecimal(devEntityObject.getProp("unitario").toString()).setScale(2,RoundingMode.HALF_UP).toString());
			prod.setCEAN("00000000");
			prod.setCEANTrib("00000000");
			detItem.setProd(prod);
			detItem.setNItem(Objects.toString(detalhe.size()));
			
			Imposto imposto = new Imposto();
			detItem.setImposto(imposto);
			//ICMS
			ICMS icms = new ICMS();
			imposto.setICMS(icms);
			
			ICMS00 icms00 = new ICMS00();
			icms.setICMS00(icms00);
			icms00.setVICMS(new BigDecimal(devEntityObject.getProp("vicms").toString()).setScale(2,RoundingMode.HALF_UP).toString());
			icms00.setPICMS(new BigDecimal(devEntityObject.getProp("icm").toString()).setScale(2,RoundingMode.HALF_UP).toString());
			icms00.setVBC(BigDecimal.valueOf(Double.valueOf(devEntityObject.getProp("base").toString())/100*Double.valueOf(devEntityObject.getProp("total").toString())).setScale(2,RoundingMode.HALF_UP).toString());
			icms00.setOrig(produto.getProp("cst","1").toString());
			icms00.setCST("00");
			icms00.setModBC("3");
			//OPEN IPI
			TIpi ipi = new TIpi();
			imposto.setIPI(ipi);
			IPITrib ipiTrib = new IPITrib();
			ipiTrib.setPIPI(new BigDecimal(devEntityObject.getProp("ipi","0").toString()).setScale(2,RoundingMode.HALF_UP).toString());
			ipiTrib.setVIPI(new BigDecimal(devEntityObject.getProp("vipi","0").toString()).setScale(2,RoundingMode.HALF_UP).toString());
			ipiTrib.setCST("99");//99=Outras saídas
			ipiTrib.setVBC(new BigDecimal(devEntityObject.getProp("total").toString()).setScale(2,RoundingMode.HALF_UP).toString());;
			ipi.setIPITrib(ipiTrib);
			ipi.setCEnq("999");
			//CLOSE IPI
			
			//OPEN CÁLCULO PIS
			PIS pis = new PIS();
			imposto.setPIS(pis);
			PISAliq pisAliq = new PISAliq();
			pisAliq.setPPIS(new BigDecimal(devEntityObject.getProp("aliqpis","0").toString()).setScale(2,RoundingMode.HALF_UP).toString());
			pisAliq.setCST("01");
			pisAliq.setVBC(new BigDecimal(devEntityObject.getProp("total").toString()).setScale(2,RoundingMode.HALF_UP).toString());
			pisAliq.setVPIS(BigDecimal.valueOf(Double.valueOf(devEntityObject.getProp("total").toString())*Double.valueOf(devEntityObject.getProp("aliqpis","0").toString())).setScale(2,RoundingMode.HALF_UP).toString());
			pis.setPISAliq(pisAliq);
			PISOutr pisOutr = pis.getPISOutr();
			pisTot+=new Double(pisAliq.getVPIS());
			//CLOSE CÁLCULO PIS			
			
			//OPEN CÁLCULO COFINS
			COFINS cofins = new COFINS();
			imposto.setCOFINS(cofins);
			
			COFINSAliq cofinsAliq = new COFINSAliq();
			COFINSOutr cofinsOutr = new COFINSOutr();
			cofins.setCOFINSAliq(cofinsAliq);
			cofinsAliq.setPCOFINS(new Double(devEntityObject.getProp("aliqcofins","0").toString())>0?devEntityObject.getProp("aliqcofins","0").toString():"0");
			cofinsAliq.setCST("01");
			cofinsAliq.setVBC(new BigDecimal(devEntityObject.getProp("total").toString()).setScale(2,RoundingMode.HALF_UP).toString());
			cofinsAliq.setVCOFINS(BigDecimal.valueOf(Double.valueOf(devEntityObject.getProp("total").toString())*Double.valueOf(devEntityObject.getProp("aliqcofins","0").toString())).setScale(2,RoundingMode.HALF_UP).toString());
			cofinsTot+=new Double(cofinsAliq.getVCOFINS());
			//CLOSE CÁLCULO COFINS				
		}		
		
		
		//TOTAL
		Total total = new InfNFe.Total();
		infNFe.setTotal(total);
		ICMSTot icmsTot = new ICMSTot();
		total.setICMSTot(icmsTot);
		
		icmsTot.setVNF(new BigDecimal(venda.getProp("total","0").toString()).setScale(2,RoundingMode.HALF_UP).toString());
		icmsTot.setVICMS(new BigDecimal(venda.getProp("icms","0").toString()).setScale(2,RoundingMode.HALF_UP).toString());
		icmsTot.setVIPI(new BigDecimal(venda.getProp("ipi","0").toString()).setScale(2,RoundingMode.HALF_UP).toString());
		icmsTot.setVBC(new BigDecimal(venda.getProp("baseicm","0").toString()).setScale(2,RoundingMode.HALF_UP).toString());
		icmsTot.setVICMSDeson("0");
		icmsTot.setVBCST("0");//SUBSTITUIÇÃO TRIBUT
		icmsTot.setVProd(new BigDecimal(venda.getProp("total","0").toString()).setScale(2,RoundingMode.HALF_UP).toString());
		icmsTot.setVST("0");
		icmsTot.setVFrete("0");
		icmsTot.setVSeg("0");
		icmsTot.setVDesc("0");
		icmsTot.setVII("0");
		icmsTot.setVPIS(pisTot>0?BigDecimal.valueOf(pisTot).setScale(2,RoundingMode.HALF_UP).toString():"0");
		icmsTot.setVCOFINS(cofinsTot>0?BigDecimal.valueOf(cofinsTot).setScale(2,RoundingMode.HALF_UP).toString():"0");
		icmsTot.setVTotTrib(BigDecimal.valueOf(new Double(pisTot+cofinsTot+new Double(icmsTot.getVICMS())+new Double(icmsTot.getVIPI()))).setScale(2,RoundingMode.HALF_UP).toString());
		icmsTot.setVOutro("0");
		
		String cNf = new java.text.SimpleDateFormat("ddHHmmss").format(venda.getProp("emissao"));
		
		
		//IDENTIFICAÇÃO
		String serie = venda.getProp("numeronf").toString().substring(venda.getProp("numeronf").toString().length()-3);
		String numeronf = venda.getProp("numeronf").toString().substring(0,venda.getProp("numeronf").toString().length()-3);
		Ide ideNFe = new Ide();
		infNFe.setIde(ideNFe);
		infNFe.setId("NFe"+ChaveAcessoNFe.generate(Objects.toString(NFUnidadeFederativa.valueOfCodigo(ufEmit).getCodigoIbge()), new java.text.SimpleDateFormat("yyMM").format(venda.getProp("emissao")), cnpjEmit, "55", serie, "1", numeronf, cNf));
		Transp transp = new Transp();
		transp.setModFrete("9");//9=Sem frete. (V2.0)
		infNFe.setTransp(transp);
		tNFe.setInfNFe(infNFe);
		
		ideNFe.setCUF(NFUnidadeFederativa.valueOfCodigo(ufEmit).getCodigoIbge());
		//ideNFe.setNNF(venda.getProp("numeronf").toString());
		ideNFe.setDhEmi(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(venda.getProp("emissao")));
		ideNFe.setDhSaiEnt(new java.text.SimpleDateFormat("yyyy-MM-dd'T'"+venda.getProp("saidah")+"XXX").format(venda.getProp("saidad")));
		//vendas.saidah;
		ideNFe.setMod(venda.getProp("modelo").toString());
		ideNFe.setSerie(Long.valueOf(serie).toString());
		ideNFe.setNNF(Long.valueOf(numeronf).toString());
		ideNFe.setNatOp(venda.getProp("operacao").toString());
		ideNFe.setCNF(cNf);
		ideNFe.setIndPag("1");
		ideNFe.setTpNF("1");//SAÍDA
		ideNFe.setCMunFG(municipioEmit.getProp("codigo").toString());
		ideNFe.setIdDest(ufEmit==ufDest?"1":"2");
		ideNFe.setTpImp("1");//Formato impressão do DANFE
		ideNFe.setTpEmis("1");
		ideNFe.setCDV(infNFe.getId().substring(infNFe.getId().length()-1));
		ideNFe.setTpAmb("2");//1=Produção; 2=Homologação
		ideNFe.setFinNFe("1");//1=NF-e normal;	2=NF-e complementar;	3=NF-e de ajuste;	4=Devolução/Retorno.
		ideNFe.setIndFinal("1");//0=Não;	1=Consumidor final;
		//Indicador de presença do comprador no estabelecimento comercial no momento da operação
		/**
		 *0=Não se aplica (por exemplo, Nota Fiscal complementar ou de ajuste);		1=Operação presencial;		2=Operação não presencial, pela Internet;		
		 *3=Operação não presencial, Teleatendimento;		4=NFC-e em operação com entrega 
		 */
		ideNFe.setIndPres("1");
		ideNFe.setProcEmi("0");//0=Emissão de NF-e com aplicativo do contribuinte;
		ideNFe.setVerProc("0.0.14");
		
		
		return tNFe;
	}
	
	public static boolean enviarXmlNFe(String notaXML, DevEntityObject venda, AdmAlgoappBean app){
		final Persister persister = new Persister(new NFRegistryMatcher());
		try {
			final NFNota notaParser = persister.read(NFNota.class, new String(notaXML.getBytes(Charset.defaultCharset()),"UTF8"));
	        final NFLoteEnvio loteEnvio = new NFLoteEnvio();
	        loteEnvio.setIdLote("333972757970401");
	        loteEnvio.setVersao("3.10");
	        loteEnvio.setNotas(Arrays.asList(notaParser));
	        loteEnvio.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_ASSINCRONO);	
			NFeConfig config = new NFeConfig() {
				
				@Override
				public NFTipoEmissao getTipoEmissao() {
					return NFTipoEmissao.EMISSAO_NORMAL;
				}
				
				@Override
				public String getCertificadoSenha() {
					return "digo2000";
				}
				
				@Override
				public File getCertificado() throws IOException {
					return new File("D:\\Documents\\@fiscal\\Backup_Cert_e_Banco\\CertMais.pfx");
				}
				
				@Override
				public File getCadeiaCertificados() throws IOException {
					return new File("D:\\Documents\\@fiscal\\Backup_Cert_e_Banco\\NFeCacerts");
				}
				
				@Override
				public NFUnidadeFederativa getCUF() {
					return NFUnidadeFederativa.ES;
				}
				
				@Override
				public NFAmbiente getAmbiente() {
					return NFAmbiente.HOMOLOGACAO;
				}
			};
	        
			DynamicSocketFactory.generateSslByNFeConfig(config);
			WSFacade wsFacade = new WSFacade(config);
			final NFLoteEnvioRetorno retorno = wsFacade.enviaLote(loteEnvio);
			final NFLoteEnvio notaParserLoteEnviado = persister.read(NFLoteEnvio.class, config.xmlAssinado);
			List<NFNota> notas = notaParserLoteEnviado.getNotas();

			System.out.println(retorno.getInfoRecebimento());	        
			wsFacade.consultaNota(notaParser.getInfo().getIdentificador().replaceFirst("NFe",""));
			NFLoteConsultaRetorno conRet = wsFacade.consultaLote(retorno.getInfoRecebimento().getRecibo());
			List<NFProtocolo> nfprotList = conRet.getProtocolos();
			for (NFProtocolo nfProtocolo : nfprotList) {
				
				venda.getPropObj("nfeprotocolo").setVal(nfProtocolo.getProtocoloInfo().getNumeroProtocolo());
				venda.getPropObj("status").setVal(nfProtocolo.getProtocoloInfo().getMotivo());
				venda.getPropObj("nfeid").setVal(nfProtocolo.getProtocoloInfo().getIdentificador());
				venda.getPropObj("nferecibo").setVal(conRet.getNumeroRecibo());
				NFNotaProcessada nfeProc = new NFNotaProcessada();
				for (NFNota nfNota : notas) {
					if(nfNota.getInfo().getIdentificador().contains(nfProtocolo.getProtocoloInfo().getIdentificador())){
						nfeProc.setNota(nfNota);
						nfeProc.setProtocolo(nfProtocolo);
						nfeProc.setVersao(new BigDecimal(nfProtocolo.getVersao()));
					}
					System.out.println(nfNota.toString());
				}
				
				venda.getPropObj("nfexml").setVal("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+nfeProc.toString());
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	
		
	}
	
	public static void generateDanfe(DevEntityObject venda, AdmAlgoappBean app) {
		try {
            FacesContext fc = FacesContext.getCurrentInstance();
            ServletContext context = (ServletContext) fc.getExternalContext().getContext();
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            // parâmetros, se houver
            Map params = new HashMap();

            params.put(JRParameter.REPORT_LOCALE, Locale.forLanguageTag("pt-BR")/*FacesContext.getCurrentInstance().getExternalContext().getRequestLocale()*/);
            params.put(JRParameter.REPORT_TIME_ZONE, TimeZone.getTimeZone("GMT-3:00"));
            byte[] bytes = null;
            JRDataSource jrds = null;
            FacesContext ctx = FacesContext.getCurrentInstance();
	            JRXmlDataSource jrxmlds = new JRXmlDataSource(new ByteArrayInputStream(venda.getPropObj("nfexml").getPropertyFile()),"/nfeProc/NFe/infNFe/det");
	            jrxmlds.setDatePattern("yyyy/MM/dd HH:mm:ss");
	            jrxmlds.setTimeZone("GMT-3:00");
	            jrxmlds.setLocale(Locale.forLanguageTag("pt-BR"));
	            jrds = jrxmlds;
	            bytes = JasperRunManager.runReportToPdf(new File(context.getRealPath("/WEB-INF/classes/com/algoboss/erp/report/danfeR.jasper")).getPath(), params, jrds);
			
            if (bytes != null && bytes.length > 0) {
                try {
                    // Envia o relatório em formato PDF para o browser
                	/*
                    response.setContentType("application/pdf");
                    response.setContentLength(bytes.length);
                    response.setHeader("Content-disposition", "inline; filename="+bean.getReportRequirementName());
                    //response.getWriter().write("<head><title>Relatório Small</title></head>");
                    ServletOutputStream ouputStream = response.getOutputStream();
                    ouputStream.write(bytes, 0, bytes.length);
                    ouputStream.flush();
                    ouputStream.close();*/
                	//response.sendRedirect(request.getContextPath()+"?file=teste.pdf");
                    response.reset();
                    response.setHeader("Location", "http://www.google.com");
                    //response.setHeader("Refresh","5; URL=" + request.getContextPath()+"?file=teste.pdf");
                    //response.encodeURL(request.getContextPath()+"?file=teste");
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "inline;filename=\"file_danfe.pdf\";");
                    response.setCharacterEncoding("UTF-8");
                    response.setContentLength(bytes.length);
                    response.getOutputStream().write(bytes, 0, bytes.length);
                    //request.getRequestDispatcher(request.getContextPath()+"?file=teste.pdf").forward(request, response);
                    //response.sendRedirect(request.getContextPath()+"?file=teste.pdf");
                    FacesContext.getCurrentInstance().responseComplete();                    
                } catch (IOException ex) {
                    Logger.getLogger(AdmAlgoreportBean.class.getName()).log(Level.SEVERE, null, ex);
                }finally{
                    //fc.responseComplete();  
                }
            }			
		} catch (Exception e) {
			Logger.getLogger(AdmAlgoreportBean.class.getName()).log(Level.SEVERE, null, e);
		}
		
	}
    
}

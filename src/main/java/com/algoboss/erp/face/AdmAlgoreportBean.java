/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algoboss.erp.face;

import static com.algoboss.erp.util.ComponentFactory.getProperty;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

import org.primefaces.context.RequestContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.algoboss.erp.converter.ObjectConverter;
import com.algoboss.erp.entity.AdmContract;
import com.algoboss.erp.entity.AdmService;
import com.algoboss.erp.entity.AdmServiceContract;
import com.algoboss.erp.entity.AdmServiceModuleContract;
import com.algoboss.erp.entity.DevEntityClass;
import com.algoboss.erp.entity.DevEntityObject;
import com.algoboss.erp.entity.DevEntityPropertyDescriptor;
import com.algoboss.erp.entity.DevEntityPropertyValue;
import com.algoboss.erp.entity.DevReportFieldContainer;
import com.algoboss.erp.entity.DevReportFieldOptions;
import com.algoboss.erp.entity.DevReportFieldOptionsMap;
import com.algoboss.erp.entity.DevReportRequirement;
import com.algoboss.erp.entity.DevRequirement;
import com.algoboss.erp.entity.SecAuthorization;
import com.algoboss.erp.entity.SecUserAuthorization;
import com.algoboss.erp.util.AlgoUtil;
import com.algoboss.erp.util.AlgodevUtil;
import com.algoboss.erp.util.ComponentFactory;
import com.algoboss.erp.util.LayoutFieldsFormat;
import com.algoboss.erp.util.ComponentFactory.AppType;
import com.algoboss.integration.small.face.FinReportBean;

/**
 *
 * @author Agnaldo
 */
@Named("algoRep")
@SessionScoped
public class AdmAlgoreportBean extends GenericBean<DevReportRequirement> {

    private transient UIComponent algoContainer;
    private transient UIComponent algoPalette;
    private transient UIComponent elementSelected;
    private List<UIComponent> childrenElementSelected = new ArrayList<UIComponent>();
    private Map<String, String[]> elementPropertiesSelected = new TreeMap<String, String[]>();
    private transient List<UIComponent> elements = new ArrayList<UIComponent>();
    private DevEntityClass entity;
    private DevEntityObject entityObject;
    private DevRequirement requirement;
    private List<DevEntityObject> entityObjectList = new ArrayList<DevEntityObject>();
    private DevEntityClass entityNodeSelected;
    private DevEntityPropertyDescriptor entityPropertyDescriptor;
    private List<DevEntityClass> entityClassNodeList = new ArrayList<DevEntityClass>();
    private List<DevEntityClass> entityClassList = new ArrayList<DevEntityClass>();
    private Long autorizationUserIdTemp = null;
    private String containerPage = "form";
    private Map<String, List<UIComponent>> elementsContainerMap = new HashMap();
    private DevEntityClass entitySelected;
    private String strUIComponentId;
    private String html = "<table><tr><td>teste</td></tr></table>";
    private List<Map<String,String>> reportMap = new ArrayList<Map<String,String>>();
    //@Inject
    private AdmAlgoappBean app;
    @Inject
    private ObjectConverter objectConverter;
    private List<Map<String, Object>> fieldOptionsAll = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> fieldOptionsFil = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> fieldOptionsRow = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> fieldOptionsCol = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> fieldOptionsVal = new ArrayList<Map<String, Object>>();
    private Map<String, Map<String, Object>> fieldOptionsAllMap = new LinkedHashMap<String, Map<String, Object>>();
    private Map<String, Map<String, Object>> fieldOptionsFilMap = new LinkedHashMap<String, Map<String, Object>>();
    private Map<String, Map<String, Object>> fieldOptionsRowMap = new LinkedHashMap<String, Map<String, Object>>();
    private Map<String, Map<String, Object>> fieldOptionsColMap = new LinkedHashMap<String, Map<String, Object>>();
    private Map<String, Map<String, Object>> fieldOptionsValMap = new LinkedHashMap<String, Map<String, Object>>();
    private Date startDate;
    private Date endDate;
    private String param1;

    /**
     * Creates a new instance of TipoDespesaBean
     */
    public AdmAlgoreportBean() {
        super.url = "views/administration/algoreport/algoreportList.xhtml";
        super.namedFindAll = "findAllAdmRepresentative";
        super.type = DevReportRequirement.class;
        super.urlForm = "algoreportForm.xhtml";
        super.subtitle = "Administração | Algoreport | Projeto";
        initBean();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }           
    
    public AdmAlgoappBean getApp() {
		return app;
	}

	public void setApp(AdmAlgoappBean app) {
		this.app = app;
	}

	public DevRequirement getRequirement() {
		return requirement;
	}

	public void setRequirement(DevRequirement requirement) {
		this.requirement = requirement;
	}

	public List<Map<String, Object>> getFieldOptionsAll() {
        return fieldOptionsAll;
    }

    public void setFieldOptionsAll(List<Map<String, Object>> fieldOptionsAll) {
        this.fieldOptionsAll = fieldOptionsAll;
    }

    public List<Map<String, Object>> getFieldOptionsFil() {
        return fieldOptionsFil;
    }

    public void setFieldOptionsFil(List<Map<String, Object>> fieldOptionsFil) {
        this.fieldOptionsFil = fieldOptionsFil;
    }

    public List<Map<String, Object>> getFieldOptionsRow() {
        return fieldOptionsRow;
    }

    public void setFieldOptionsRow(List<Map<String, Object>> fieldOptionsRow) {
        this.fieldOptionsRow = fieldOptionsRow;
    }

    public List<Map<String, Object>> getFieldOptionsCol() {
        return fieldOptionsCol;
    }

    public void setFieldOptionsCol(List<Map<String, Object>> fieldOptionsCol) {
        this.fieldOptionsCol = fieldOptionsCol;
    }

    public List<Map<String, Object>> getFieldOptionsVal() {
        return fieldOptionsVal;
    }

    public void setFieldOptionsVal(List<Map<String, Object>> fieldOptionsVal) {
        this.fieldOptionsVal = fieldOptionsVal;
    }

    public Map<String, Map<String, Object>> getFieldOptionsMap() {
        return fieldOptionsAllMap;
    }

    public void setFieldOptionsMap(Map<String, Map<String, Object>> fieldOptionsMap) {
        this.fieldOptionsAllMap = fieldOptionsMap;
    }

    public Map<String, Map<String, Object>> getFieldOptionsFilMap() {
        return fieldOptionsFilMap;
    }

    public void setFieldOptionsFilMap(Map<String, Map<String, Object>> fieldOptionsFilMap) {
        this.fieldOptionsFilMap = fieldOptionsFilMap;
    }

    public Map<String, Map<String, Object>> getFieldOptionsRowMap() {
        return fieldOptionsRowMap;
    }

    public void setFieldOptionsRowMap(Map<String, Map<String, Object>> fieldOptionsRowMap) {
        this.fieldOptionsRowMap = fieldOptionsRowMap;
    }

    public Map<String, Map<String, Object>> getFieldOptionsColMap() {
        return fieldOptionsColMap;
    }

    public void setFieldOptionsColMap(Map<String, Map<String, Object>> fieldOptionsColMap) {
        this.fieldOptionsColMap = fieldOptionsColMap;
    }

    public Map<String, Map<String, Object>> getFieldOptionsValMap() {
        return fieldOptionsValMap;
    }

    public void setFieldOptionsValMap(Map<String, Map<String, Object>> fieldOptionsValMap) {
        this.fieldOptionsValMap = fieldOptionsValMap;
    }

    public List<Map<String, Object>> mapToList(Map map) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list.addAll(map.values());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
    private boolean testebol;

    public boolean isTestebol() {
        return testebol;
    }

    public void setTestebol(boolean testebol) {
        this.testebol = testebol;
    }

    public String getStrUIComponentId() {
        return strUIComponentId;
    }

    public void setStrUIComponentId(String strUIComponentId) {
        this.strUIComponentId = strUIComponentId;
    }

    public String getContainerPage() {
        return containerPage;
    }

    public void setContainerPage(String containerPage) {
        this.containerPage = containerPage;
        updateContainerPage();
    }

    public void updateContainerPage() {
        elements = elementsContainerMap.get(containerPage);
    }

    public void clearContainerPage() {
        elements.clear();
    }

    public Object[] doElementsConatainerList() {
        return elementsContainerMap.keySet().toArray();
    }

    public DevEntityClass getEntitySelected() {
        return entitySelected;
    }

    public void setEntitySelected(DevEntityClass entitySelected) {
        if (entitySelected != null) {
            this.entitySelected = entitySelected;
        }
    }

    public void resetForm(String id) {
        try {
            RequestContext context = RequestContext.getCurrentInstance();
            context.reset(id);
        } catch (Throwable ex) {
            Logger.getLogger(AdmAlgoreportBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DevEntityClass getEntity() throws Throwable {

        if (entityClassNodeList.isEmpty()) {
            entityClassNodeList.add(entity);
        } else {
            entity = entityClassNodeList.get(entityClassNodeList.size() - 1);
        }

        return entity;
    }

    private void prepareEntity(List<DevEntityClass> entList, String parentNode) {
        DevEntityClass ent = entList.get(entList.size() - 1);
        if (ent.getEntityPropertyDescriptorParentList() != null) {
            DevEntityPropertyDescriptor propParentAux = null;
            for (int i = 0; i < ent.getEntityPropertyDescriptorParentList().size(); i++) {
                DevEntityPropertyDescriptor propParent = ent.getEntityPropertyDescriptorParentList().get(i);
                if (propParent.getEntityClassParent() != null) {
                    if (propParent.getPropertyType().equals("ENTITYCHILDREN") && propParent.getEntityClassParent().getName().equals(parentNode)) {
                        propParentAux = propParent;
                        break;
                    }
                    if (propParent.getPropertyClass() != null && propParent.getPropertyClass().getEntityPropertyDescriptorParentList().size() > i) {
                        DevEntityPropertyDescriptor propParent2 = propParent.getPropertyClass().getEntityPropertyDescriptorParentList().get(i);
                    }
                }
            }
            if (propParentAux != null && !entList.contains(propParentAux.getEntityClassParent())) {
                entList.add(propParentAux.getEntityClassParent());
                prepareEntity(entList, parentNode + "." + propParentAux.getEntityClassParent().getName());

            }
        }
        for (DevEntityPropertyDescriptor prop : ent.getEntityPropertyDescriptorList()) {
            Map<String, Object> obj = new HashMap<String, Object>();
            obj.put("property", prop);
            obj.put("visible", true);
            obj.put("used", false);
            obj.put("node", parentNode + "." + prop.getPropertyName());
            obj.put("head", prop.getPropertyLabel() + (parentNode.contains(".") ? ":" + ent.getLabel() : ""));
            //fieldOptions.add(obj);
            fieldOptionsAllMap.put(String.valueOf(obj.get("property")), obj);
           
            if (prop.getPropertyClass() != null) {
                entList.add(prop.getPropertyClass());
                prepareEntity(entList, parentNode + "." + prop.getPropertyName());
            }
        }
    }

    public void setEntity(DevEntityClass entity) {
        if (entity != null) {
            initBean();
            entitySelected = entity;
            fieldOptionsAll.clear();
            fieldOptionsAllMap.clear();
            DevReportFieldContainer field = new DevReportFieldContainer();
            List<DevEntityClass> entList = new ArrayList<DevEntityClass>();
            entList.add(entity);
            prepareEntity(entList, entity.getName());
            fieldOptionsAll.addAll(fieldOptionsAllMap.values());
            //fieldOptionsAllMap.put("all", new ArrayList<Map<String, Object>>(fieldOptionsAllMap.values()));
        }
        this.entity = entity;
    }

    public DevEntityClass getEntityNodeSelected() {
        return entityNodeSelected;
    }

    public void setEntityNodeSelected(DevEntityClass entityNodeSelected) {
        this.entityNodeSelected = entityNodeSelected;
    }

    public List<DevEntityClass> getEntityClassList() {
        return entityClassList;
    }

    public List<DevEntityClass> getEntityClassNodeList() {
        return entityClassNodeList;
    }

    public DevEntityPropertyDescriptor getEntityPropertyDescriptor() {
        return entityPropertyDescriptor;
    }

    public void setEntityPropertyDescriptor(DevEntityPropertyDescriptor entityPropertyDescriptor) {
        this.entityPropertyDescriptor = entityPropertyDescriptor;
    }

    public UIComponent getAlgoContainer() {
        return algoContainer;
    }

    public void setAlgoContainer(UIComponent algoContainer) throws Throwable {
        if (elements != null && algoContainer!=null) {
            algoContainer.getChildren().clear();
            algoContainer.getChildren().addAll(elements);
        }
        this.algoContainer = algoContainer;
    }

    public UIComponent getAlgoPalette() {
        return algoPalette;
    }

    public void setAlgoPalette(UIComponent algoPalette) {
        this.algoPalette = algoPalette;
    }

    public UIComponent getElementSelected() {
        return elementSelected;
    }

    public void setElementSelected(UIComponent elementSelected) {
        this.elementSelected = elementSelected;
    }

    public List<UIComponent> getChildrenElementSelected() throws Throwable {
        if (elementSelected != null) {
            childrenElementSelected = elementSelected.getChildren();
        }
        return childrenElementSelected;
    }

    public void setChildrenElementSelected(List<UIComponent> childrenElementSelected) throws Throwable {
        this.childrenElementSelected = childrenElementSelected;
        if (elementSelected != null && elementSelected.getChildren() != null) {
            List<UIComponent> childrenElementSelectedValid = new ArrayList<UIComponent>();
            for (UIComponent uIComponent : childrenElementSelected) {
                if (uIComponent.getParent().getClientId().equals(elementSelected.getClientId())) {
                    childrenElementSelectedValid.add(uIComponent);
                }
            }
            if (!childrenElementSelectedValid.isEmpty()) {
                elementSelected.getChildren().clear();
                elementSelected.getChildren().addAll(childrenElementSelectedValid);
            }
        }
    }

    public Map<String, String[]> getElementPropertiesSelected() {
        return elementPropertiesSelected;
    }

    public void setElementPropertiesSelected(Map<String, String[]> elementPropertiesSelected) {
        this.elementPropertiesSelected = elementPropertiesSelected;
    }

    public void cleanUpApp() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(false);
        session.removeAttribute("org.jboss.weld.context.http.HttpSessionContext#org.jboss.weld.bean-web-ManagedBean-class com.erp.face.AdmAlgoappBean");
    }

    private void setCreateDateFilter() {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.getActualMinimum(Calendar.DATE);

        Calendar c3 = Calendar.getInstance();
        //c3.add(Calendar.DATE, -1);
        //c3.add(Calendar.MONTH, -11);
        c3.set(c3.get(Calendar.YEAR), c3.get(Calendar.MONTH), 1);

        Calendar c2 = Calendar.getInstance();
        //c2.add(Calendar.DATE, -1);
        c2.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DATE));

        startDate = c3.getTime();
        endDate = c2.getTime();
    }

    public void indexBeanRep(Long nro) {
        try {
            super.indexBeanNoList(nro);
            setCreateDateFilter();
            if (super.userAuth != null && super.userAuth.getServiceContract() != null) {
                AdmService service = (AdmService) super.userAuth.getServiceContract().getService();
                if (service != null && service.getReportRequirement() == null) {
                    service = (AdmService) doBeanRefresh(service);
                }
                if (service != null && service.getReportRequirement() != null) {
                    DevReportRequirement requirement = (DevReportRequirement) doBeanRefresh(service.getReportRequirement());
                    if (requirement != null) {
                        entity = requirement.getEntityClass();
                        bean = requirement;
                    }
                }
            }
            initBean();
            updateFormFilter();
            formRendered = false;
        } catch (Throwable ex) {
            Logger.getLogger(AdmAlgoreportBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateFormFilter(){
        if (bean != null) {
            entity = bean.getEntityClass();
            entitySelected = entity;
            DevEntityClass entFilter = new DevEntityClass("filter");
            entFilter.setLabel("Filtro:");
            for (DevReportFieldOptions devReportFieldOptions : LayoutFieldsFormat.getContainerOptions(bean.getFieldContainerList(), "fil", entitySelected)) {
    			if(devReportFieldOptions.getEntityPropertyDescriptor()==null){
    				DevReportFieldOptionsMap fieldOptionsMap = LayoutFieldsFormat.loadOpt(devReportFieldOptions, "head");
    				Object value = fieldOptionsMap.getOptionsValue();
    				String type = fieldOptionsMap.getOptionsType();
    				String name = devReportFieldOptions.getName();
    				if(value!=null){
    					entityPropertyDescriptor = new DevEntityPropertyDescriptor();
    					entityPropertyDescriptor.setPropertyName(name);
    					entityPropertyDescriptor.setPropertyLabel(value.toString());
    					entityPropertyDescriptor.setPropertyType(type);
		                if (!entFilter.getEntityPropertyDescriptorList().contains(entityPropertyDescriptor)) {
		                	entFilter.getEntityPropertyDescriptorList().add(entityPropertyDescriptor);
		                }
    				}
    			}
            }
            UIComponent tocloned = ComponentFactory.findComponentByStyleClass("ui-algo-element ui-algo-element-container ui-panel", BaseBean.algoPalette);
            UIComponent clonedContainer = ComponentFactory.componentClone(tocloned, false); 
            UIComponent cloned = ComponentFactory.componentClone(tocloned, false);  
            clonedContainer.getChildren().add(cloned);
            ComponentFactory.updateElementProperties(clonedContainer, Collections.singletonMap("header", ""));
            //app.setAlgoContainerView(cloned);
            requirement = new DevRequirement();
            requirement.setEntityClass(entFilter);
            entityObject = new DevEntityObject();
            entityObject.setEntityClass(entFilter);
            UIComponent container = cloned;
            try {

    			//if (!urlList.isEmpty() && urlList.get(containerIndex)[0].equals(subtitle)) {
   
    			//}            	

				//app.indexBeanNoList(null);
            	createByConstructor(Collections.singletonMap("dataform", "true"), container, elementsContainerMap, requirement, entFilter);
            	requirement.setRequirementId(-bean.getReportRequirementId());
            	app = AlgodevUtil.getAlgoAppInstance(requirement, baseBean, baseDao);
            	//app.initBean();
            	app.loginBean = loginBean;
                app.setBean(entityObject);
                //app.setBaseBean(baseBean);
                //app.setBaseDao(baseDao);
                //cloned.getChildren().clear();
                //cloned.getChildren().addAll(elementsContainerMap.get("form"));
                //app.getAlgoContainerView().getChildren().clear();
                //app.setRequirement(requirement);
                //app.setAlgoContainerViewWidget(cloned);
                //app.generateElementsContainerMap(requirement);
                app.doForm();
                app.setAppBean("app_"+ requirement.getRequirementId());
            	app.setSubtitle("report|filter|"+bean.getReportRequirementId());
            	app.getAppBean().setAppBean(BaseBean.copyObject(app));	
            	//app.setUrl("views/administration/algoreport/algoreportList.xhtml?report-filter="+bean.getReportRequirementId());
    			String beansubtitle = "";
    			boolean clonable = false;
    			if (app != null) {
    				clonable = Arrays.asList(app.getClass().getGenericInterfaces()).contains(Cloneable.class);
    				beansubtitle = app.getSubtitle();
    				if (!baseBean.getBeanLoadMap().containsKey(app.getSubtitle()) && clonable) {
    					GenericBean actualBeanClone = app.getClass().newInstance();
    					baseBean.copyObject(app, actualBeanClone);
    					baseBean.getBeanLoadMap().put(beansubtitle, actualBeanClone);
    				}else{
    	 				GenericBean<Object> bean = baseBean.getBeanLoadMap().get(app.getSubtitle());
        				if (bean != null) {
        					if (bean instanceof GenericBean && !bean.getSubtitle().equals(beansubtitle) && clonable) {
        						baseBean.resetObject(app);
        						baseBean.copyObject(bean, app);
        					}
        				}
    				}
    			}               	
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
         
            //ComponentFactory.updateElementProperties(comp3, mapParam);
            //setEntity(entity);
            reportDeserializer();
        }    	
    }

    @Override
    public void indexBean(Long nro) {
        try {
            super.indexBean(nro);
            setCreateDateFilter();
            doBeanList();
            elements.clear();
            entityClassList = (List<DevEntityClass>) (Object) baseDao.findAll("findAllDevEntityClass");
            entitySelected = new DevEntityClass();
            formRendered = true;
            //propertySelectedListCollection = new ArrayList<DevEntityPropertyDescriptor>();
            //propertySelectedFormCollection = new ArrayList<DevEntityPropertyDescriptor>();
            //Collection<Object[]> componentList = (HashSet<Object[]>) objIn.readObject();
        } catch (Throwable ex) {
            Logger.getLogger(AdmAlgoreportBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
    }
    private void createByConstructor(Map map, UIComponent compTarget, Map<String,List<UIComponent>> elementsContainerMap, DevRequirement bean, DevEntityClass entity) {
 		elementsContainerMap.clear();
         UIComponent elementPanel = BaseBean.algoPalette;
         UIComponent cloned = null;
         try {
         	LayoutFieldsFormat.onConstruction(bean);
             if (map.containsKey("dataform") && !String.valueOf(map.get("dataform")).isEmpty()) {
                 compTarget.getChildren().clear();
                 if (cloned == null) {
                     cloned = ComponentFactory.findComponentByStyleClass("ui-algo-element-container data-form", elementPanel);
                     cloned = ComponentFactory.componentClone(cloned, false);
                     AlgodevUtil.updateChildren(compTarget.getChildren(), cloned);
                     setContainerPage("form");
                 }
                 cloned = new ComponentFactory(cloned, elementPanel, "", "", elementsContainerMap, entity, bean, AppType.get(bean.getInterfaceType())).getComponentCreated();
                 //elements.clear();
                 //elements.addAll(compTarget.getChildren());
                 AlgodevUtil.generateElementsContainerMap(elementsContainerMap, bean);
                 updateContainerPage();
                 cloned = null;
             }          
         } catch (Throwable ex) {
             Logger.getLogger(AdmAlgodevBean.class.getName()).log(Level.SEVERE, null, ex);
         }
     }

    public void updateEntityObject() {
        try {
            //setEntity(entitySelected);
            if(bean.getReportDatasource()==null){
            	entityObjectList = baseDao.findEntityObjectByClass(entity,new ArrayList<String>(), Collections.singletonList(site.getInstantiatesSiteId()), startDate, endDate, true);
            	updateReport();
            	generateXml();            	            	
            }
        } catch (Throwable ex) {
            Logger.getLogger(AdmAlgoreportBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Long getAutorizationUserIdTemp() throws Throwable {
        setAutorizationUserIdTemp();
        return autorizationUserIdTemp;
    }

    public void setAutorizationUserIdTemp() throws Throwable {
        SecUserAuthorization userAuth = new SecUserAuthorization();
        userAuth.setUserAuthorizationId(autorizationUserIdTemp);

        if (autorizationUserIdTemp == null || !loginBean.getUserAuthorizationList().contains(userAuth)) {
            AdmService service = new AdmService("Aplicativo>Rep>Teste", "Relatório Teste", "Relatório", "#{admAlgoappBean.indexBean()}", null, "1", new Date());
            service.setReportRequirement(bean);
            SecUserAuthorization userAuthorization = new SecUserAuthorization();
            AdmServiceContract serviceContract = new AdmServiceContract();
            serviceContract.setService(service);
            userAuthorization.setServiceContract(serviceContract);
            SecAuthorization authorization = new SecAuthorization();
            authorization.setAllowed(true);
            AdmServiceModuleContract module = new AdmServiceModuleContract();
            module.setName("Relatório");
            module.setExpectedAmount(2);
            serviceContract.setServiceModuleContract(module);
            autorizationUserIdTemp = Double.valueOf((Math.random() * 1000d) * (Math.random() * 1000d)).longValue();
            userAuthorization.setUserAuthorizationId(autorizationUserIdTemp);
            userAuthorization.setAuthorization(authorization);
            loginBean.getUserAuthorizationList().add(userAuthorization);
        }
    }

    @Override
    public void initBean() {
        html = "";
        
        elementPropertiesSelected = new TreeMap<String, String[]>();
        elements = new ArrayList();
        entity = new DevEntityClass();
        entitySelected = new DevEntityClass();
        entityPropertyDescriptor = new DevEntityPropertyDescriptor();
        elementsContainerMap.clear();
        elementsContainerMap.put("form", elements);
        elementsContainerMap.put("list", new ArrayList<UIComponent>());

        fieldOptionsAllMap.clear();
        fieldOptionsFilMap.clear();
        fieldOptionsRowMap.clear();
        fieldOptionsColMap.clear();
        fieldOptionsValMap.clear();

        fieldOptionsAll.clear();
        fieldOptionsVal = new ArrayList(fieldOptionsValMap.values());
        fieldOptionsRow = new ArrayList(fieldOptionsRowMap.values());
        fieldOptionsCol = new ArrayList(fieldOptionsColMap.values());
        fieldOptionsFil = new ArrayList(fieldOptionsFilMap.values());

        childrenElementSelected = new ArrayList();
        elementSelected = null;
        autorizationUserIdTemp = null;
        super.initBean();
    }

    @Override
    public void doBeanList() {
        super.doBeanList();
        List<DevReportRequirement> beanListAux = new ArrayList<DevReportRequirement>();
        for (DevReportRequirement devRequirement : beanList) {
            List<SecUserAuthorization> userAuthList = loginBean.getUserAuthorizationList();
            for (SecUserAuthorization secUserAuthorization : userAuthList) {
                if (devRequirement.getService() != null && secUserAuthorization.getServiceContract() != null
                        && devRequirement.getService().getServiceId().equals(secUserAuthorization.getServiceContract().getService().getServiceId())) {
                    beanListAux.add(devRequirement);
                    break;
                }
            }
        }
        beanList = beanListAux;
    }

    private Map<String, Object> getFieldOptionsItem(List<Map<String, Object>> fieldOptionsList, String fieldObject) {
        Map<String, Object> foReturn = null;
        for (int i = 0; i < fieldOptionsList.size(); i++) {
            Map<String, Object> fo = fieldOptionsList.get(i);
            if (fo.get("property").equals(fieldObject)) {
                foReturn = fo;
                break;
            }
        }
        return foReturn;
    }

    private void fieldOptionListToMap(List<Map<String, Object>> list, Map<String, Map<String, Object>> map) {
        map.clear();
        for (Map<String, Object> item : list) {
            map.put(String.valueOf(item.get("property")), item);
        }
        list.clear();
    }

    public void updateFieldConfig() {
        Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String fieldObject = String.valueOf(map.get("fieldObject"));
        String fieldDestin = String.valueOf(map.get("fieldDestin"));
        fieldOptionListToMap(fieldOptionsAll, fieldOptionsAllMap);
        fieldOptionListToMap(fieldOptionsVal, fieldOptionsValMap);
        fieldOptionListToMap(fieldOptionsRow, fieldOptionsRowMap);
        fieldOptionListToMap(fieldOptionsCol, fieldOptionsColMap);
        fieldOptionListToMap(fieldOptionsFil, fieldOptionsFilMap);

        Map<String, Object> ob = fieldOptionsAllMap.get(fieldObject);

        if (ob != null) {
            boolean used = false;
            if (fieldDestin.equals("fil")) {
                if (!fieldOptionsFilMap.containsKey(fieldObject)) {
                    fieldOptionsFilMap.put(fieldObject, ob);
                }
                //used = true;
            } else {
                fieldOptionsFilMap.remove(fieldObject);
            }
            if (fieldDestin.equals("row")) {
                if (!fieldOptionsRowMap.containsKey(fieldObject)) {
                    fieldOptionsRowMap.put(fieldObject, ob);
                }
                used = true;
            } else {
                fieldOptionsRowMap.remove(fieldObject);
            }
            if (fieldDestin.equals("col")) {
                if (!fieldOptionsColMap.containsKey(fieldObject)) {
                    fieldOptionsColMap.put(fieldObject, ob);
                }
                used = true;
            } else {
                fieldOptionsColMap.remove(fieldObject);
            }
            if (fieldDestin.equals("val")) {
                if (!fieldOptionsValMap.containsKey(fieldObject)) {
                    fieldOptionsValMap.put(fieldObject, ob);
                }
                //used = true;
            } else {
                fieldOptionsValMap.remove(fieldObject);
            }
            ob.put("used", used);
            fieldOptionsAll.addAll(fieldOptionsAllMap.values());
            fieldOptionsVal.addAll(fieldOptionsValMap.values());
            fieldOptionsRow.addAll(fieldOptionsRowMap.values());
            fieldOptionsCol.addAll(fieldOptionsColMap.values());
            fieldOptionsFil.addAll(fieldOptionsFilMap.values());
        } else {
            String c = "";
        }
    }

    public void updateFieldShow() {
        //if (fo != null) {
        //  fo[1] = !((Boolean) fo[1]);
        //} 
        Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String fieldObject = String.valueOf(map.get("fieldObject"));
        String fieldShow = String.valueOf(map.get("fieldShow"));
        String fieldShowAll = String.valueOf(map.get("fieldShowAll"));
        if (!"null".equals(fieldShowAll) && !fieldShowAll.isEmpty()) {
            Boolean boolShow = Boolean.parseBoolean(fieldShowAll);
            for (Map<String, Object> fo : fieldOptionsAll) {
                fo.put("visible", boolShow);
            }
            for (Map<String, Object> fo : fieldOptionsFil) {
                fo.put("visible", boolShow);
            }
            for (Map<String, Object> fo : fieldOptionsRow) {
                fo.put("visible", boolShow);
            }
            for (Map<String, Object> fo : fieldOptionsCol) {
                fo.put("visible", boolShow);
            }
            for (Map<String, Object> fo : fieldOptionsVal) {
                fo.put("visible", boolShow);
            }
        } else {
            Boolean boolShow = Boolean.parseBoolean(fieldShow);
            if (fieldOptionsAllMap.containsKey(fieldObject)) {
                Map<String, Object> obAll = fieldOptionsAllMap.get(fieldObject);//objectConverter.getAsObject(FacesContext.getCurrentInstance(), null, fieldObject);
                obAll.put("visible", boolShow);
            }
            if (fieldOptionsFilMap.containsKey(fieldObject)) {
                Map<String, Object> obFil = fieldOptionsFilMap.get(fieldObject);//objectConverter.getAsObject(FacesContext.getCurrentInstance(), null, fieldObject);
                obFil.put("visible", boolShow);
            }
            if (fieldOptionsRowMap.containsKey(fieldObject)) {
                Map<String, Object> obRow = fieldOptionsRowMap.get(fieldObject);//objectConverter.getAsObject(FacesContext.getCurrentInstance(), null, fieldObject);
                obRow.put("visible", boolShow);
            }
            if (fieldOptionsColMap.containsKey(fieldObject)) {
                Map<String, Object> obCol = fieldOptionsColMap.get(fieldObject);//objectConverter.getAsObject(FacesContext.getCurrentInstance(), null, fieldObject);
                obCol.put("visible", boolShow);
            }
            if (fieldOptionsValMap.containsKey(fieldObject)) {
                Map<String, Object> obVal = fieldOptionsValMap.get(fieldObject);//objectConverter.getAsObject(FacesContext.getCurrentInstance(), null, fieldObject);
                obVal.put("visible", boolShow);
            }
        }
        String a = "";
        updateReport();
    }

    private String generateRow(DevEntityObject entObj, Map<String, Map<String, BigDecimal>> valCalc, String styleDeCol) {
        String row = "";
        Map<String,String> rowMap = new HashMap<String,String>();
        for (int j = 0; j < fieldOptionsAll.size(); j++) {
            Map<String, Object> fo = fieldOptionsAll.get(j);
            if ((Boolean) fo.get("visible") && !(Boolean) fo.get("used")) {
                Object ret = null;
                row += "<td style='" + styleDeCol + "'>";
                try {
                    //String nodeSufix = ((DevEntityPropertyDescriptor) fo.get("property")).getPropertyName();
                    String nodeSufix = String.valueOf(fo.get("node"));
                    ret = app.$(nodeSufix, entObj).internalPropertyValue();
                    if (ret != null && !String.valueOf(ret).isEmpty() && !String.valueOf(ret).equals("null") && !String.valueOf(ret).equals("false")) {
                        row += ret;
                        rowMap.put(nodeSufix,String.valueOf(ret));
                        boolean exist = false;
                        for (Map<String, Object> foItem : fieldOptionsVal) {
                            if (foItem.get("property").equals(fo.get("property"))) {
                                exist = true;
                                break;
                            }
                        }
                        if (exist) {

                            if (!valCalc.containsKey(nodeSufix)) {
                                Map<String, BigDecimal> valCalcTot = new HashMap<String, BigDecimal>();

                                valCalcTot.put("sum-subtotal", BigDecimal.ZERO);
                                valCalcTot.put("sum-total", BigDecimal.ZERO);

                                valCalcTot.put("count-subtotal", BigDecimal.ZERO);
                                valCalcTot.put("count-total", BigDecimal.ZERO);
                                valCalc.put(nodeSufix, valCalcTot);
                            }
                            DevEntityPropertyDescriptor prop = ((DevEntityPropertyDescriptor) fo.get("property"));
                            String propType = prop.getPropertyType();
                            if (propType.equals("FLOAT")) {
                                valCalc.get(nodeSufix).put("sum-subtotal", valCalc.get(nodeSufix).get("sum-subtotal").add(new BigDecimal(String.valueOf(ret).replaceAll(",", "."))));
                                valCalc.get(nodeSufix).put("sum-total", valCalc.get(nodeSufix).get("sum-total").add(new BigDecimal(String.valueOf(ret).replaceAll(",", "."))));
                            }
                            valCalc.get(nodeSufix).put("count-subtotal", valCalc.get(nodeSufix).get("count-subtotal").add(BigDecimal.ONE));
                            valCalc.get(nodeSufix).put("count-total", valCalc.get(nodeSufix).get("count-total").add(BigDecimal.ONE));

                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                row += "</td>";
            }
        }
        row += "";
        if(!rowMap.isEmpty()){
        	reportMap.add(rowMap);
        }
        return row;
    }

    private List<String> generateGroup(boolean rowGroup, Map<String, Object> rowMap, Map<String, Integer> rowCount, Map<String, Map<String, BigDecimal>> valCalc, String styleDeCol, String styleDefTable) {
        List<String> tbody = new ArrayList();
        int rowCountAux = 0;
        int rowCountAux2 = 0;
        int colCountAux = 0;
        rowCountAux = rowCount.get("rowCount");
        rowCount.put("colCountTmp", rowCount.get("colCountTmp") + 1);
        boolean subtotalThis = false;
        for (Map.Entry<String, Object> object : rowMap.entrySet()) {
            Map<String, Map<String, BigDecimal>> valCalcAux = new HashMap<String, Map<String, BigDecimal>>();
            List<DevEntityObject> entObjList = null;
            List<String> tbodyAux = new ArrayList();
            boolean showTotal = (object.getKey() == null || !object.getKey().isEmpty());
            int rowCountInt = 1;

            if (object.getValue() instanceof Map) {
                Map mapVal = (Map) object.getValue();
                Map<String, Integer> rowCountChild = new HashMap<String, Integer>();
                rowCountChild.put("rowCount", 0);
                rowCountChild.put("colCount", rowCount.get("colCount"));
                rowCountChild.put("colCountTmp", rowCount.get("colCountTmp"));
                tbodyAux = generateGroup(rowGroup, mapVal, rowCountChild, valCalcAux, styleDeCol, styleDefTable);
                //colCountAux = rowCountChild.get("colCount") + rowCount.get("colCount") + 1;
                rowCountAux = rowCountChild.get("rowCount") + rowCountAux + rowCountInt;
                rowCountInt = rowCountAux - rowCountAux2;
                subtotalThis = tbodyAux.size() > 0;
            }
            if (object.getValue() instanceof List) {
                entObjList = (List<DevEntityObject>) object.getValue();
                for (int i = 0; i < entObjList.size(); i++) {
                    DevEntityObject entObj = entObjList.get(i);
                    String row = generateRow(entObj, valCalcAux, styleDeCol);
                    if (i == 0) {
                        tbodyAux.add("<tr>" + row + "</tr>");
                    } else {
                        tbodyAux.add("<tr>" + row + "</tr>");
                    }
                    rowCountInt++;
                }
                rowCountAux = rowCountAux + rowCountInt;
                subtotalThis = tbodyAux.size() > 0;
            }

            String bgcolor = "background-color:rgb(255," + 51 * rowCount.get("colCountTmp") + "," + 51 * rowCount.get("colCountTmp") + ");";
            if (rowGroup && !valCalcAux.isEmpty() && subtotalThis) {
                rowCountInt++;
                rowCountAux++;
                String gfoot = "";
                gfoot += "<table style='" + styleDefTable + bgcolor + ";width:100%;'>";
                for (int j = 0; j < fieldOptionsVal.size(); j++) {
                    Map<String, Object> fo = fieldOptionsVal.get(j);

                    if ((Boolean) fo.get("visible")) {
                        DevEntityPropertyDescriptor prop = ((DevEntityPropertyDescriptor) fo.get("property"));
                        String propType = prop.getPropertyType();
                        String operation = "";
                        String value = "";
                        if (propType.equals("FLOAT")) {
                            operation = "Soma";
                            value = "sum-total";
                        } else {
                            operation = "Contagem";
                            value = "count-total";
                        }
                        gfoot += "<tr>";
                        gfoot += "<td style='border-bottom: 1px solid #DDDDDD;font-weight: bold;width:80%;overflow: hidden;white-space:nowrap;'>";
                        gfoot += "Total (" + operation + " - " + fo.get("head") + "):";
                        gfoot += "</td>";
                        gfoot += "<td style='border-bottom: 1px solid #DDDDDD;font-weight: bold;width:20%;text-align:center;'>";
                        if (valCalcAux.containsKey(String.valueOf(fo.get("node")))) {
                            gfoot += valCalcAux.get(String.valueOf(fo.get("node"))).get(value);
                        } else {
                            gfoot += 0;
                        }
                        gfoot += "</td>";
                        gfoot += "</tr>";
                    }
                }

                gfoot += "</table>";
                if (showTotal) {
                    tbodyAux.add("<tr><td colspan='" + (rowCount.get("colCount") - rowCount.get("colCountTmp") + 1) + "' style='padding:0px;" + styleDeCol + "'>" + gfoot + "</td></tr>");
                }
            }
            if (rowGroup && showTotal) {
                tbody.add("<tr><td rowspan='" + (rowCountInt) + "' style='padding:0px;" + styleDeCol + bgcolor + "'>" + object.getKey() + "</td></tr>");
            }
            tbody.addAll(tbodyAux);
            //rowCountInt = rowCountInt*2;
            rowCountAux2 += rowCountInt;
            if (colCountAux > 0) {
                //rowCount.put("colCount", colCountAux);
            }
            rowCount.put("rowCount", rowCountAux);
            for (Map.Entry<String, Map<String, BigDecimal>> valObj : valCalcAux.entrySet()) {
                if (valCalc.containsKey(valObj.getKey())) {
                    valCalc.get(valObj.getKey()).put("sum-total", valCalc.get(valObj.getKey()).get("sum-total").add(valObj.getValue().get("sum-total")));
                    valCalc.get(valObj.getKey()).put("count-total", valCalc.get(valObj.getKey()).get("count-total").add(valObj.getValue().get("count-total")));
                } else {
                    valCalc.put(valObj.getKey(), valObj.getValue());
                }
            }
        }
        rowCount.put("colCountTmp", rowCount.get("colCountTmp") - 1);
        return tbody;
    }

    private String generateHead(Map<String, Integer> colCount, String styleDeCol) {
        String thead = "";
        for (int j = 0; j < fieldOptionsRow.size(); j++) {
            Map<String, Object> fo = fieldOptionsRow.get(j);
            if ((Boolean) fo.get("visible")) {
                colCount.put("colCount", colCount.get("colCount") + 1);
                thead += "<th style='" + styleDeCol + "' class=''>";
                try {
                    //initTable += "<col style='" + styleDeCol + "' />";
                    //thead += "<span class='ui-column-resizer ui-draggable' style='position: relative;'> </span>";
                    thead += "<div>" + fo.get("head") + "</div>";
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                thead += "</th>";
            }
        }
        for (int j = 0; j < fieldOptionsAll.size(); j++) {
            Map<String, Object> fo = fieldOptionsAll.get(j);
            if ((Boolean) fo.get("visible") && !(Boolean) fo.get("used")) {
                colCount.put("colCount", colCount.get("colCount") + 1);
                thead += "<th style='" + styleDeCol + "' class=''>";
                try {
                    //initTable += "<col style='" + styleDeCol + "' />";
                    //thead += "<span class='ui-column-resizer ui-draggable' style='position: relative;'> </span>";
                    thead += "<div>" + fo.get("head") + "</div>";
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                thead += "</th>";
            }
        }
        return thead;
    }

    public void updateReport() {
    	reportMap.clear();
        String tbody = "";
        String thead = "";
        String tfoot = "";
        String styleDefTable = "border-collapse: collapse;font-size:10px;table-layout: fixed; overflow: hidden;";
        String styleDeCol = "border: 1px solid #DDDDDD;overflow: hidden;white-space:nowrap;width: 80px;max-width:80px;min-width:80px;height: 15px;max-height: 15px;min-height:15px;";
        html = "<table style='" + styleDefTable + "' class='resizable'>";
        String initTable = "<caption>" + bean.getReportRequirementName() + "</caption><colgroup>";
        Map<String, Integer> rowCount = new HashMap<String, Integer>();
        rowCount.put("rowCount", 0);
        rowCount.put("colCount", 0);
        rowCount.put("colCountTmp", 0);
        int colCount = 0;
        thead = "<tr>";
        thead += generateHead(rowCount, styleDeCol);
        thead += "</tr>";
        initTable += "</colgroup>";
        Map<String, Map<String, BigDecimal>> valCalc = new HashMap<String, Map<String, BigDecimal>>();
        //fieldOptionsRow
        Map<String, Object> rowMap = new HashMap<String, Object>();
        List<Object[]> fieldOptionsRowTemp = new ArrayList<Object[]>();
        if (fieldOptionsRow.isEmpty()) {
            rowMap.put("", entityObjectList);
        } else {
            rowMap = listToGroup(fieldOptionsRow, 0, entityObjectList);
            //fieldOptionsRowTemp.add(listToGroup(Listfo, rowMap));
            //fieldOptionsRowTemp.addAll(fieldOptionsRow);
        }
        StringBuilder sb = new StringBuilder();
        List<String> strList = generateGroup(!fieldOptionsRow.isEmpty(), rowMap, rowCount, valCalc, styleDeCol, styleDefTable);
        for (String s : strList) {
            sb.append(s);
            //sb.append("\t");
        }
        tbody += sb;
        StringBuilder stb = new StringBuilder();

        tfoot += "<tr><td colspan='" + rowCount.get("colCount") + "' style='padding:0px;'><table style='" + styleDefTable + ";width:100%;'>";
        for (int j = 0; j < fieldOptionsVal.size(); j++) {
            Map<String, Object> fo = fieldOptionsVal.get(j);

            if ((Boolean) fo.get("visible")) {
                DevEntityPropertyDescriptor prop = ((DevEntityPropertyDescriptor) fo.get("property"));
                String propType = prop.getPropertyType();
                String operation = "";
                String value = "";
                if (propType.equals("FLOAT")) {
                    operation = "Soma";
                    value = "sum-total";
                } else {
                    operation = "Contagem";
                    value = "count-total";
                }
                tfoot += "<tr>";
                tfoot += "<td style='border-bottom: 1px solid #DDDDDD;font-weight: bold;width:80%;'>";
                tfoot += "Total (" + operation + " - " + fo.get("head") + "):";
                tfoot += "</td>";
                tfoot += "<td style='border-bottom: 1px solid #DDDDDD;font-weight: bold;width:20%;'>";
                tfoot += valCalc.get(String.valueOf(fo.get("node"))).get(value);
                tfoot += "</td>";
                tfoot += "</tr>";
            }
        }

        tfoot += "</table></td></tr>";
        html += initTable + "<thead>" + thead + "</thead>" + "<tbody>" + tbody + "</tbody>" + "<tfoot>" + tfoot + "</tfoot>";
        html += "</table>";
        //html +="<div class='ui-column-resizer-helper ui-state-highlight' style='height: 215px; display: none; top: 232.867px; left: 763.383px;'></div";
        if (algoContainer != null) {
            algoContainer.getChildren().clear();
        }
        elements.clear();
        for (int j = 0; j < fieldOptionsFil.size(); j++) {
            Map<String, Object> fo = fieldOptionsFil.get(j);
            if ((Boolean) fo.get("visible") && algoContainer != null && algoContainer.getChildren() != null) {
                UIComponent elementPanel = algoPalette;
                try {
                    UIComponent cloned = null;
                    UIComponent label = null;
                    label = ComponentFactory.findComponentByStyleClass("ui-textLabel", elementPanel);
                    DevEntityPropertyDescriptor prop = ((DevEntityPropertyDescriptor) fo.get("property"));
                    String propType = prop.getPropertyType();

                    if (propType.equals("FLOAT")) {
                        cloned = ComponentFactory.findComponentByStyleClass("float", elementPanel);
                    }
                    if (propType.equals("DATE")) {
                        cloned = ComponentFactory.findComponentByStyleClass("ui-calendar", elementPanel);
                    }
                    if (propType.equals("STRING")) {
                        cloned = ComponentFactory.findComponentByStyleClass("ui-textField", elementPanel);
                    }

                    if (label != null && cloned != null) {
                        label = ComponentFactory.componentClone(label, false);
                        Map<String, Object> paramMap = new HashMap<String, Object>();
                        paramMap.put("value", "| " + prop.getPropertyLabel() + ":" + prop.getEntityClassParent().getLabel());
                        ComponentFactory.updateElementProperties(label, paramMap);

                        cloned = ComponentFactory.componentClone(cloned, false);
                        paramMap.clear();
                        paramMap.put("autocomplete", "off");
                        paramMap.put("value", "");
                        ComponentFactory.updateElementProperties(cloned, paramMap);
                        label.getChildren().add(cloned);
                        AlgodevUtil.updateChildren(elements, label);
                        //algoContainer.getChildren().add(cloned);
                    }
                } catch (Throwable ex) {
                    Logger.getLogger(AdmAlgodevBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    private Map<String, Object> listToGroup(List<Map<String, Object>> fieldOption, Integer actualNode, List<DevEntityObject> entityObjectListSrc) {
        //retMap.put("label", "");
        //retMap.put("value", "");
        //retMap.put("child", "");
        Map<String, Object> fo = new HashMap<String, Object>();
        for (int i = actualNode; i < fieldOption.size(); i++) {
            if ((Boolean) fieldOption.get(i).get("visible")) {
                fo = fieldOption.get(i);
                actualNode = i;
                break;
            }
        }
        Map<String, Object> retMap = new HashMap<String, Object>();
        if (fo.isEmpty()) {
            retMap.put("", entityObjectListSrc);
        } else {
            String retStr = null;
            for (int i = 0; i < entityObjectListSrc.size(); i++) {
                DevEntityObject entObj = entityObjectListSrc.get(i);
                DevEntityPropertyValue propVal = app.$(String.valueOf(fo.get("node")), entObj);
                Object ret = propVal != null ? propVal.getVal() : null;
                retStr = String.valueOf(ret);
                if (!retMap.containsKey(retStr)) {
                    retMap.put(retStr, new ArrayList<DevEntityObject>());
                }
                if (retMap.get(retStr) instanceof List) {
                    List<DevEntityObject> objList = (List<DevEntityObject>) retMap.get(retStr);
                    objList.add(entObj);
                }
            }
            if (actualNode < fieldOption.size() - 1) {
                for (Map.Entry<String, Object> object : retMap.entrySet()) {
                    if (object.getValue() instanceof List) {
                        List<DevEntityObject> entityObjectList = (List) object.getValue();
                        Map<String, Object> childMap = listToGroup(fieldOption, actualNode + 1, entityObjectList);
                        object.setValue(childMap);
                    }
                }
            }
        }
        return retMap;
    }
    public Document generateXml(){
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement(AlgoUtil.normalizerName(bean.getReportRequirementName()));
        doc.appendChild(rootElement);
        	
        for (int i = 0; i < reportMap.size(); i++) {
        	Map<String,String> objMap = reportMap.get(i);
        	Element item = doc.createElement("item");
        	rootElement.appendChild(item);
        	for (Map.Entry<String, String> object : objMap.entrySet()) {
        		Attr attr = doc.createAttribute(object.getKey());
        		attr.setValue(object.getValue());
        		item.setAttributeNode(attr);            					
			}      				
		}
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		//transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.ENCODING, Charset.defaultCharset().name());
		doc.normalizeDocument();
        DOMSource source = new DOMSource(doc);

        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter );
        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);
        try {
			transformer.transform(source, result);
			System.out.println("\n"+stringWriter.toString());    	
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return doc;
    }
    @Override
    public void doBeanForm() {
        autorizationUserIdTemp = null;
        elementsContainerMap.clear();
        fieldOptionsAll.clear();
        fieldOptionsFil.clear();
        fieldOptionsRow.clear();
        fieldOptionsCol.clear();
        fieldOptionsVal.clear();
        fieldOptionsAllMap.clear();

        initBean();
        if (bean != null) {
            entity = bean.getEntityClass();
            entitySelected = entity;
            //setEntity(entity);
            reportDeserializer();
            updateEntityObject();                     	
            //updateReport();
            entityNodeSelected = entity;
            entityClassNodeList.clear();
            try {
                //List<DevComponentContainer> componentContainerList = bean.getComponentContainerList();
                //for (DevComponentContainer devComponentContainer : componentContainerList) {
                //elements = generateComponentList(devComponentContainer);
                //elementsContainerMap.put(devComponentContainer.getName(), elements);
                //containerPage = devComponentContainer.getName();
                //}
                //setAutorizationUserIdTemp();
                //updateTreeExpression();
            } catch (Throwable ex) {
                Logger.getLogger(AdmAlgoreportBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Selecione o Projeto.", ""));
        }
        super.doBeanForm(); //To change body of generated methods, choose Tools | Templates.
    }

    public void doBeanNew() {
        initBean();
        bean = new DevReportRequirement();
        bean.setEntityClass(entity);
    }

    @Override
    public void doBeanRemove() {
        super.doBeanRemove();
        initBean();
        bean.setEntityClass(entity);
    }

    @Override
    public void doBeanSave() {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            //Collection<Object[]> componentList = new HashSet<Object[]>();
            String msgStr = "";
            if (bean.getReportRequirementName() == null || bean.getReportRequirementName().isEmpty()) {
                msgStr = "Informe um nome em propriedade do projeto.";
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msgStr, ""));
            }
            if (bean.getService().getName() == null || bean.getService().getName().isEmpty()) {
                msgStr = "Informe um serviço em propriedade do projeto.";
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msgStr, ""));
            }
            if (msgStr.isEmpty()) {
                //generateElementsContainerMap();
                reportSerializer();
                LayoutFieldsFormat.onConstruction(bean);
                bean.setEntityClass(getEntity());
                bean.getService().setMainAddress("#{algoRep.indexBeanRep()}");
                bean.getService().setModule("REP");
                List<Object> toSaveList = new ArrayList();
                toSaveList.add(bean.getService());
                //toSaveList.add(entity);
                toSaveList.add(bean);
                AdmContract contract = loginBean.getUser().getContract();
                AdmService service = null;
                AdmServiceContract admServiceContractAux = null;
                AdmServiceModuleContract admServiceModuleContractAux = null;
                if (contract != null) {
                    bean.setContract(contract);
                    List<AdmServiceModuleContract> serviceModuleContractList = contract.getServiceModuleContractList();
                    for (AdmServiceModuleContract admServiceModuleContract : serviceModuleContractList) {
                        if (admServiceModuleContract.getName().equals(bean.getService().getModule())) {
                            admServiceModuleContractAux = admServiceModuleContract;
                            break;
                        }
                    }
                    if (admServiceModuleContractAux != null) {
                        //.add(serviceContract);
                        List<AdmServiceContract> admServiceContractList = admServiceModuleContractAux.getServiceContractList();
                        for (AdmServiceContract admServiceContract : admServiceContractList) {
                            if (admServiceContract.getService().getServiceId().equals(bean.getService().getServiceId())) {
                                admServiceContractAux = admServiceContract;
                                break;
                            }
                        }
                    } else {
                        admServiceModuleContractAux = new AdmServiceModuleContract();
                        admServiceModuleContractAux.setName(bean.getService().getModule());
                        admServiceModuleContractAux.setExpectedAmount(1);
                        admServiceModuleContractAux.setCurrentAmount(0);
                        serviceModuleContractList.add(admServiceModuleContractAux);
                    }
                    if (admServiceContractAux == null) {
                        admServiceContractAux = new AdmServiceContract();
                        admServiceContractAux.setService(bean.getService());
                        admServiceModuleContractAux.getServiceContractList().add(admServiceContractAux);
                        service = bean.getService();
                        toSaveList.add(contract);
                    }
                }
                super.doBeanSaveAndList(false, false, true, toSaveList.toArray());
                if (service != null) {
                    service.setReportRequirement(bean);
                } else {
                    service = bean.getService();
                    if (admServiceContractAux != null) {
                        admServiceContractAux.setService(service);
                    }
                    service.setReportRequirement(bean);
                }
                if (service.getServiceId() != null) {
                    if (admServiceContractAux != null) {
                        admServiceContractAux.setServiceModuleContract(admServiceModuleContractAux);
                        admServiceContractAux.setService((AdmService) super.doBeanRefresh(service));
                    }
                }
                loginBean.doAuthorization();
                //setAutorizationUserIdTemp();
            }
        } catch (Throwable ex) {
            Logger.getLogger(AdmAlgoreportBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
    }



    public void updateElement() {
        try {

            Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String parameter = map.get("param_id").toString();
            Object parameter2 = map.get("style");
            //FacesContext context = FacesContext.getCurrentInstance();
            String draggedId = parameter.split(":")[parameter.split(":").length - 1];
            elementSelected = ComponentFactory.doFindComponent(getAlgoContainer(), draggedId);
            Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
            f2.setAccessible(true);
            if (elementSelected != null) {
                ComponentFactory.updateElementProperties(f2, elementSelected, map, elementPropertiesSelected);
            } else {
                String t = "";
            }

            String s = "";
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void removeComponent() throws Throwable {
        Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        UIComponent toRemove = null;
        if (!map.containsKey("param_id")) {
            if (elementSelected != null) {
                toRemove = elementSelected;
                elementPropertiesSelected.clear();
            }
        } else {
            String parameter = map.get("param_id").toString();
            String draggedId = parameter.split(":")[parameter.split(":").length - 1];
            toRemove = ComponentFactory.doFindComponent(getAlgoContainer(), draggedId);
        }
        if (toRemove != null) {
            if (!removeElement(toRemove)) {
                toRemove.getParent().getChildren().remove(toRemove);
            }
        }
    }

    private boolean removeElement(UIComponent toRemove) {
        Integer idxToRemove = null;
        for (int i = 0; i < elements.size(); i++) {
            UIComponent comp = elements.get(i);
            if (comp.getId().equals(toRemove.getId())) {
                idxToRemove = i;
                break;
            }
        }
        if (idxToRemove != null) {
            elements.remove(idxToRemove.intValue());
            return true;
        } else {
            return false;
        }
    }
    public List<DevReportFieldContainer> getFieldContainerList(){
    	reportSerializer();
    	return bean.getFieldContainerList();
    }
    public void setFieldContainerList(List<DevReportFieldContainer> fieldContainerList){
    	initBean();
    	bean.setFieldContainerList(fieldContainerList);
    	reportDeserializer();
    }
    private void reportSerializer() {
        //fieldOptionsRow
        DevReportFieldContainer repContainerAll = new DevReportFieldContainer("all");
        bean.getFieldContainerList().add(repContainerAll);
        for (Map<String, Object> fieldOptionsObj : fieldOptionsAll) {
            DevReportFieldOptions fieldOptionsEntObj = new DevReportFieldOptions();
            fieldOptionsEntObj.setName("");
            fieldOptionsEntObj.setEntityPropertyDescriptor((DevEntityPropertyDescriptor) fieldOptionsObj.get("property"));

            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("Boolean", "used", fieldOptionsObj.get("used")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("Boolean", "visible", fieldOptionsObj.get("visible")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("String", "node", fieldOptionsObj.get("node")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("String", "head", fieldOptionsObj.get("head")));

            repContainerAll.getFieldOptionsList().add(fieldOptionsEntObj);
        }

        DevReportFieldContainer repContainerFil = new DevReportFieldContainer("fil");
        bean.getFieldContainerList().add(repContainerFil);
        for (Map<String, Object> fieldOptionsObj : fieldOptionsFil) {
            DevReportFieldOptions fieldOptionsEntObj = new DevReportFieldOptions();
            fieldOptionsEntObj.setName("");
            fieldOptionsEntObj.setEntityPropertyDescriptor((DevEntityPropertyDescriptor) fieldOptionsObj.get("property"));

            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("Boolean", "used", fieldOptionsObj.get("used")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("Boolean", "visible", fieldOptionsObj.get("visible")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("String", "node", fieldOptionsObj.get("node")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("String", "head", fieldOptionsObj.get("head")));

            repContainerFil.getFieldOptionsList().add(fieldOptionsEntObj);
        }

        DevReportFieldContainer repContainerRow = new DevReportFieldContainer("row");
        bean.getFieldContainerList().add(repContainerRow);
        for (Map<String, Object> fieldOptionsObj : fieldOptionsRow) {
            DevReportFieldOptions fieldOptionsEntObj = new DevReportFieldOptions();
            fieldOptionsEntObj.setName("");
            fieldOptionsEntObj.setEntityPropertyDescriptor((DevEntityPropertyDescriptor) fieldOptionsObj.get("property"));

            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("Boolean", "used", fieldOptionsObj.get("used")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("Boolean", "visible", fieldOptionsObj.get("visible")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("String", "node", fieldOptionsObj.get("node")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("String", "head", fieldOptionsObj.get("head")));

            repContainerRow.getFieldOptionsList().add(fieldOptionsEntObj);
        }

        DevReportFieldContainer repContainerCol = new DevReportFieldContainer("col");
        bean.getFieldContainerList().add(repContainerCol);
        for (Map<String, Object> fieldOptionsObj : fieldOptionsCol) {
            DevReportFieldOptions fieldOptionsEntObj = new DevReportFieldOptions();
            fieldOptionsEntObj.setName("");
            fieldOptionsEntObj.setEntityPropertyDescriptor((DevEntityPropertyDescriptor) fieldOptionsObj.get("property"));

            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("Boolean", "used", fieldOptionsObj.get("used")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("Boolean", "visible", fieldOptionsObj.get("visible")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("String", "node", fieldOptionsObj.get("node")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("String", "head", fieldOptionsObj.get("head")));

            repContainerCol.getFieldOptionsList().add(fieldOptionsEntObj);
        }

        DevReportFieldContainer repContainerVal = new DevReportFieldContainer("val");
        bean.getFieldContainerList().add(repContainerVal);
        for (Map<String, Object> fieldOptionsObj : fieldOptionsVal) {
            DevReportFieldOptions fieldOptionsEntObj = new DevReportFieldOptions();
            fieldOptionsEntObj.setName("");
            fieldOptionsEntObj.setEntityPropertyDescriptor((DevEntityPropertyDescriptor) fieldOptionsObj.get("property"));

            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("Boolean", "used", fieldOptionsObj.get("used")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("Boolean", "visible", fieldOptionsObj.get("visible")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("String", "node", fieldOptionsObj.get("node")));
            fieldOptionsEntObj.getFieldOptionsMapList().add(new DevReportFieldOptionsMap("String", "head", fieldOptionsObj.get("head")));

            repContainerVal.getFieldOptionsList().add(fieldOptionsEntObj);
        }

    }

    private void reportDeserializer() {
        //
        for (DevReportFieldContainer repContainer : bean.getFieldContainerList()) {
            Map<String, Object> fieldOptionsObj = new HashMap<String, Object>();
            if (repContainer.getName().equals("all")) {
                for (DevReportFieldOptions options : repContainer.getFieldOptionsList()) {
                    fieldOptionsObj = new HashMap<String, Object>();
                    fieldOptionsObj.put("property", options.getEntityPropertyDescriptor());
                    for (DevReportFieldOptionsMap optionsMap : options.getFieldOptionsMapList()) {
                        fieldOptionsObj.put(optionsMap.getOptionsName(), optionsMap.getOptionsValue());
                    }
                    fieldOptionsAllMap.put(String.valueOf(options.getEntityPropertyDescriptor()), fieldOptionsObj);
                }
            }
            if (repContainer.getName().equals("fil")) {
                for (DevReportFieldOptions options : repContainer.getFieldOptionsList()) {
                    fieldOptionsObj = new HashMap<String, Object>();
                    fieldOptionsObj.put("property", options.getEntityPropertyDescriptor());
                    for (DevReportFieldOptionsMap optionsMap : options.getFieldOptionsMapList()) {
                        fieldOptionsObj.put(optionsMap.getOptionsName(), optionsMap.getOptionsValue());
                    }
                    fieldOptionsFilMap.put(String.valueOf(options.getEntityPropertyDescriptor()), fieldOptionsObj);
                }
            }
            if (repContainer.getName().equals("row")) {
                for (DevReportFieldOptions options : repContainer.getFieldOptionsList()) {
                    fieldOptionsObj = new HashMap<String, Object>();
                    fieldOptionsObj.put("property", options.getEntityPropertyDescriptor());
                    for (DevReportFieldOptionsMap optionsMap : options.getFieldOptionsMapList()) {
                        fieldOptionsObj.put(optionsMap.getOptionsName(), optionsMap.getOptionsValue());
                    }
                    fieldOptionsRowMap.put(String.valueOf(options.getEntityPropertyDescriptor()), fieldOptionsObj);
                }
            }
            if (repContainer.getName().equals("col")) {
                for (DevReportFieldOptions options : repContainer.getFieldOptionsList()) {
                    fieldOptionsObj = new HashMap<String, Object>();
                    fieldOptionsObj.put("property", options.getEntityPropertyDescriptor());
                    for (DevReportFieldOptionsMap optionsMap : options.getFieldOptionsMapList()) {
                        fieldOptionsObj.put(optionsMap.getOptionsName(), optionsMap.getOptionsValue());
                    }
                    fieldOptionsColMap.put(String.valueOf(options.getEntityPropertyDescriptor()), fieldOptionsObj);
                }
            }
            if (repContainer.getName().equals("val")) {
                for (DevReportFieldOptions options : repContainer.getFieldOptionsList()) {
                    fieldOptionsObj = new HashMap<String, Object>();
                    fieldOptionsObj.put("property", options.getEntityPropertyDescriptor());
                    for (DevReportFieldOptionsMap optionsMap : options.getFieldOptionsMapList()) {
                        fieldOptionsObj.put(optionsMap.getOptionsName(), optionsMap.getOptionsValue());
                    }
                    fieldOptionsValMap.put(String.valueOf(options.getEntityPropertyDescriptor()), fieldOptionsObj);
                }
            }
        }
        fieldOptionsAll.addAll(fieldOptionsAllMap.values());
        fieldOptionsVal.addAll(fieldOptionsValMap.values());
        fieldOptionsRow.addAll(fieldOptionsRowMap.values());
        fieldOptionsCol.addAll(fieldOptionsColMap.values());
        fieldOptionsFil.addAll(fieldOptionsFilMap.values());
    }
    
    public void doPdfReport() {
        try {
            Calendar cIni = Calendar.getInstance();
            cIni.setTime(startDate);
            cIni.add(Calendar.MONTH, -3);

            Calendar cEnd = Calendar.getInstance();
            cEnd.setTime(endDate);
            cEnd.add(Calendar.MONTH, +3);
            
            try {
            //super.doBeanList();
            //Class.forName("org.firebirdsql.jdbc.FBDriver");
            //String url = "jdbc:firebirdsql:algomaster.zapto.org/3050:C:\\Program Files (x86)\\SmallSoft\\Small Commerce\\SMALL.GDB";
            //Connection con = DriverManager.getConnection(url, "SYSDBA", "masterkey");
            FacesContext fc = FacesContext.getCurrentInstance();
            ServletContext context = (ServletContext) fc.getExternalContext().getContext();
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            File relatorioJasper = new File(context.getRealPath(bean.getReportFile()));
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            // parâmetros, se houver
            Map params = new HashMap();
            List<DevEntityPropertyValue> propValList = entityObject.getEntityPropertyValueList();
            for (DevEntityPropertyValue devEntityPropertyValue : propValList) {
            	String key = devEntityPropertyValue.getEntityPropertyDescriptor().getPropertyName();
            	Object value = devEntityPropertyValue.getVal();
            	params.put(key, String.valueOf(value).isEmpty()?null:value);
			}
            params.put(JRParameter.REPORT_LOCALE, Locale.forLanguageTag("pt-BR")/*FacesContext.getCurrentInstance().getExternalContext().getRequestLocale()*/);
            params.put(JRParameter.REPORT_TIME_ZONE, TimeZone.getTimeZone("GMT-3:00"));
            byte[] bytes = null;
            JRDataSource jrds = null;
            FacesContext ctx = FacesContext.getCurrentInstance();
            if(bean.getReportDatasource()==null){
            	entityObjectList = baseDao.findEntityObjectByClass(entity, new ArrayList<String>(),getSiteIdList(), cIni.getTime(), cEnd.getTime(), true);
            	updateReport();
	            JRXmlDataSource jrxmlds = new JRXmlDataSource(generateXml(),"/"+AlgoUtil.normalizerName(bean.getReportRequirementName())+"/item");
	            jrxmlds.setDatePattern("yyyy/MM/dd HH:mm:ss");
	            jrxmlds.setTimeZone("GMT-3:00");
	            jrxmlds.setLocale(Locale.forLanguageTag("pt-BR"));
	            jrds = jrxmlds;
	            bytes = JasperRunManager.runReportToPdf(relatorioJasper.getPath(), params, jrds);
            }else{
            	Connection conn = baseDao.getConnectionSmall();
            	if(conn!=null){
            		bytes = JasperRunManager.runReportToPdf(relatorioJasper.getPath(), params, conn);            		
            	}else{
                    //Collection<Object[]> componentList = new HashSet<Object[]>();
                    String msgStr = "";
                    msgStr = "Não foi possível obter conexão com o banco de dados. Verifique se o acesso está disponível.";
                    ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msgStr, ""));
            	}
			      //try {
			    	  //ic.createSubcontext("java:");
			    	  //ic.createSubcontext("java:/comp");
			    	  //ic.createSubcontext("java:/comp/env");
			    	  //ic.createSubcontext("java:/comp/env/jdbc");
				//} catch (javax.naming.NamingException e) {
					// TODO: handle exception
				//}
            	
            	//JRResultSetDataSource jrrsds = new JRResultSetDataSource();
            }

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
                    response.setHeader("Content-Disposition", "inline;filename=\"file_" + AlgoUtil.escapeURL(bean.getReportRequirementName()) + ".pdf\";");
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
            } catch (Throwable e) {
            	e.printStackTrace();
            } finally {
            }
        } catch (Throwable e) {
        	e.printStackTrace();
        } finally {
        }
    }    
    
}

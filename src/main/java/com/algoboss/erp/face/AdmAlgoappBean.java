/*
 * and open the template in the editor.
 */
package com.algoboss.erp.face;

import groovy.json.StringEscapeUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.el.ValueExpression;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.algoboss.erp.dao.UsuarioDao;
import com.algoboss.erp.entity.AdmService;
import com.algoboss.erp.entity.DevComponentContainer;
import com.algoboss.erp.entity.DevEntityClass;
import com.algoboss.erp.entity.DevEntityObject;
import com.algoboss.erp.entity.DevEntityPropertyDescriptor;
import com.algoboss.erp.entity.DevEntityPropertyDescriptorConfig;
import com.algoboss.erp.entity.DevEntityPropertyValue;
import com.algoboss.erp.entity.DevRequirement;
import com.algoboss.erp.entity.SecUser;
import com.algoboss.erp.util.AlgodevUtil;
import com.algoboss.erp.util.ComponentFactory;
import com.algoboss.erp.util.LayoutFieldsFormat;
import com.algoboss.erp.util.report.PDFExporter;
import com.algoboss.erp.util.report.PDFExporter2;
import com.algoboss.erp.validations.LoginValidator;

/**
 *
 * @author Agnaldo
 */
@Named("app")
@RequestScoped
public class AdmAlgoappBean extends GenericBean<DevEntityObject> implements Cloneable{

	@Inject
	private AdmAlgodevBean algodevBean;
	@Inject
	private SecUserBean userBean;
	private SessionUtilBean appBean;
	private transient UIComponent algoContainerForm;
	private transient UIComponent algoContainer;
	private transient UIComponent algoContainerView;
	protected String elementSelectedId;
	protected transient UIComponent elementSelected;
	protected Map<String, Object> elementPropertiesSelected = new TreeMap<String, Object>();
	protected DevEntityClass entity;
	protected DevEntityPropertyDescriptor entityPropertyDescriptor;
	protected List<DevEntityPropertyDescriptor> entityPropertyDescriptorList = new ArrayList<DevEntityPropertyDescriptor>();
	protected List<DevEntityClass> entityClassList = new ArrayList<DevEntityClass>();
	protected DevRequirement requirement;
	protected String containerPage = "form";
	protected String nextContainerPage = "";
	protected Map<String, List<DevEntityObject>> beanListMap = new HashMap();
	protected Map<String, BeanList<DevEntityObject>> beanListFilteredMap = new HashMap();
	protected Object beanSel;
	protected transient UIComponent dataTableSelected;
	
	protected List<DevEntityObject> beanListChild;
	protected DevEntityObject childBean = new DevEntityObject();
	protected DevEntityClass childEntity;
	protected DevEntityPropertyValue childValue;
	protected DevEntityPropertyValue fileValue;
	protected StreamedContent fileDownload;
	protected Date startDate;
	protected Date endDate;
	protected Set<String> beanMapBrowserCached = new TreeSet<String>(); 
	protected String idxBrowserCached = "";
	protected ScriptEngineManager manager = new ScriptEngineManager();
	protected ScriptEngine jsEngine = manager.getEngineByExtension("js");
	protected String PREFIX_BEAN = "app_";
	protected String containerClass = "ui-algo-container-app";
	protected PhaseListener phaseListener;
	protected boolean firstRender = true;

	

	public String $date(String attr, String format, DevEntityObject entObj) {
		return entObj.$date(attr, format);
	}

	public String $date(String attr, String format) {
		return $date(attr, format, bean);
	}

	public DevEntityPropertyValue $(String attr) {
		return $(attr, bean);
	}

	public DevEntityPropertyValue $(String attr, DevEntityObject entObj) {
		return entObj.$(attr);
	}
	
	public Object $g(String attr) {
		return $(attr, bean).getStr();
	}

	public Object $g(String attr, DevEntityObject entObj) {
		return entObj.$(attr).getStr();
	}	

	public int count(String attr, DevEntityObject entObj) {
		int c = 0;
		List<DevEntityObject> list = $(attr, entObj).getPropertyChildrenList();
		if (list != null) {
			c = list.size();
		}
		return c;
	}

	public int count(String node) {
		int c = 0;
		List<DevEntityObject> list = null;
		if (node.contains(".")) {
			list = doBeanListChild(node);
		} else {
			list = doBeanList(node);
		}
		if (list != null) {
			c = list.size();
		}
		return c;
	}

	public void updateEntityObject() {
		try {
			// beanList = (List<DevEntityObject>) (Object)
			// baseDao.findAll(DevEntityObject.class, getSiteIdList(),
			// startDate, endDate);
			// beanList = (List<DevEntityObject>) (Object)
			// baseDao.findAll(DevEntityObject.class, getSiteIdList(), null,
			// null);
		} catch (Throwable ex) {
			Logger.getLogger(AdmAlgoreportBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public DevEntityObject getChildBean() {
		if (childBean == null) {
			childBean = new DevEntityObject();
		}
		return childBean;
	}

	public void setChildBean(DevEntityObject childBean) {
		this.childBean = childBean;
	}

	public Map<String, List<DevEntityObject>> getBeanListMap() {
		return beanListMap;
	}

	public void setBeanListMap(Map<String, List<DevEntityObject>> beanListMap) {
		this.beanListMap = beanListMap;
	}

	public Map<String, BeanList<DevEntityObject>> getBeanListFilteredMap() {
		return beanListFilteredMap;
	}

	public void setBeanListFilteredMap(Map<String, BeanList<DevEntityObject>> beanListFilteredMap) {
		this.beanListFilteredMap = beanListFilteredMap;
	}
	class BeanList<T> extends ArrayList{
		private List beanList;
		private String clazz;

		

		public BeanList(List list) {
			super();
			
			this.addAll(list);
		}

		public BeanList(String clazz) {
			super();
			this.clazz = clazz;
		}

		public List getBeanList() {
			return beanList;
		}

		public void setBeanList(List beanList) {
			this.beanList = beanList;
		}
		
	}
	public DevRequirement getRequirement() {
		return requirement;
	}

	public void setRequirement(DevRequirement requirement) {
		this.requirement = requirement;
	}	
	
	public String getContainerClass() {
		return containerClass;
	}

	public void setContainerClass(String containerClass) {
		this.containerClass = containerClass;
	}

	public SessionUtilBean getAppBean() {
		if(appBean == null && requirement != null){
			setAppBean(PREFIX_BEAN+requirement.getRequirementId());
		}
		return appBean;
	}
	
	public void setAppBean(String id){
		if(id.startsWith(PREFIX_BEAN)){
			if(!getBaseBean().getAppBeanMap().containsKey(id)){			
				getBaseBean().getAppBeanMap().put(id, new SessionUtilBean());
			}
			appBean = getBaseBean().getAppBeanMap().get(id);
		}
	}

	public StreamedContent getFileDownload() {
		return fileDownload;
	}

	public void setFileDownload(StreamedContent fileDownload) {
		this.fileDownload = fileDownload;
	}

	public String getIdxBrowserCached() {
		return idxBrowserCached;
	}

	public void setIdxBrowserCached(String idxBrowserCached) {
		this.idxBrowserCached = idxBrowserCached;
	}

	/**
	 * Creates a new instance of TipoDespesaBean
	 */
	public AdmAlgoappBean() {
		super.url = "views/administration/algodev/algodevView.xhtml";
		super.namedFindAll = "findAllAdmRepresentative";
		super.type = DevEntityObject.class;
		super.urlForm = "algodevForm.xhtml";
		super.subtitle = "Administração | Algoapp | Aplicação";
		//appBean = new SessionUtilBean();
	}

	@Override
	public void indexBean(Long nro) {
		try {
			//includePhaseListener();
			//DataSourceContextHolder.setTargetDataSource("SMALL1");
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.getActualMinimum(Calendar.DATE);

			Calendar c3 = Calendar.getInstance();
			// c3.add(Calendar.DATE, -1);
			// c3.add(Calendar.MONTH, -11);
			c3.set(c3.get(Calendar.YEAR), c3.get(Calendar.MONTH), 1);

			Calendar c2 = Calendar.getInstance();
			// c2.add(Calendar.DATE, -1);
			c2.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DATE));

			startDate = c3.getTime();
			endDate = c2.getTime();
			long startTime1 = Calendar.getInstance().getTimeInMillis();
			super.indexBeanNoList(nro);
			long endTime1 = Calendar.getInstance().getTimeInMillis();
			if(requirement != null){
				initJsEngine();
				
				//getAppBean().setRequirement(requirement);
				//getAppBean().setElementsContainerMap(requirement,new HashMap<String, List<UIComponent>>());	
				
				if(requirement.getInterfaceType().startsWith("list")){
					doList();			
				}else{
					doForm();
				}
			}else{
				Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, "#### REQUIREMENT IS NULL. nro: "+nro);
			}
			
			long endTime2 = Calendar.getInstance().getTimeInMillis();
			System.out.println("Time1 Bean " + this.subtitle + ": " + (endTime1 - startTime1));
			System.out.println("Time2 Bean " + this.subtitle + ": " + (endTime2 - startTime1));
		} catch (Throwable ex) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
		}
	}

	@Override
	public void indexBeanNewWin(Long nro) throws Throwable {
		super.indexBeanNewWin(nro); // To change body of generated methods,
		//includePhaseListener();		
		initJsEngine();
		getAppBean().setRequirement(requirement);
		//getAppBean().setElementsContainerMap(requirement,new HashMap<String, List<UIComponent>>());		
		if(requirement.getInterfaceType().startsWith("list")){
			doList();			
		}else{
			doForm();
		}

	}
	private void initJsEngine(){
		try {
			//jsEngine.put("FacesMessage", javax.faces.application.FacesMessage);
			jsEngine = manager.getEngineByExtension("js");
			jsEngine.eval("importPackage(java.util);");
			jsEngine.eval("importPackage(java.lang);");
			jsEngine.eval("importPackage(java.math);");
			jsEngine.eval("importPackage(javax.faces);");
		 
			if(requirement != null && requirement.getRequirementScript()!=null){
				jsEngine.eval(requirement.getRequirementScript());		
				jsEngine.put("app", this);
			}		
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@PostConstruct	
	public void onConstruct(){
		try {
			
		Object actualBeanAux = getBaseBean().getActualBean(); 
		AdmAlgoappBean actualBean = null;
		if(actualBeanAux instanceof AdmAlgoappBean){
			actualBean = (AdmAlgoappBean)actualBeanAux;
		}
		if(actualBeanAux instanceof AdmAlgoreportBean){
			AdmAlgoreportBean actualBeanAux2 = (AdmAlgoreportBean)actualBeanAux;
			actualBean = actualBeanAux2.getApp();
		}
		if(actualBean != null){
			setAppBean(PREFIX_BEAN+actualBean.getRequirement().getRequirementId());
		}
		//appBean = getAppBean();
		if(appBean !=null && appBean.getAppBean()!=null){
			BaseBean.copyObject(getAppBean().getAppBean(), this);
			//userAuth = getAppBean().getAppBean().userAuth;			
		}
		if(loginBean!=null && loginBean.isReloadView()){
			//beanListMap = new HashMap();
			//beanListFilteredMap = new HashMap();			
			firstRender = true;
		}
		//updateContainerPage();
		jsEngine.put("app", this);
		includePhaseListener();
		} catch (Exception e) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	@PreDestroy
	public void onDestroy(){
		try {
			if(requirement!=null){
				getAppBean().setAppBean(BaseBean.copyObject(this));			
			}
			removePhaseListener();			
		} catch (Exception e) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	
	
	@Override
	public void onReload() {	
		super.onReload();
		includePhaseListener();
	}

	@Override
	public void initBean() {
		long startTime1 = Calendar.getInstance().getTimeInMillis();
		try {		
			idxBrowserCached = "";
			beanMapBrowserCached = new TreeSet<String>(); 
			fileValue = null;
			fileDownload = null;
			elementPropertiesSelected = new TreeMap<String, Object>();
			//sessionUtilBean.setElements(new ArrayList());
			
			//elements = new ArrayList();
			entity = new DevEntityClass();
			entityPropertyDescriptor = new DevEntityPropertyDescriptor();
			//elementsContainerMap = new HashMap();		
			
			//sessionUtilBean.setElementsContainerMap(new HashMap());
			//algoContainer = getAlgoContainer();
			if (algoContainer != null) {
				//algoContainer.getChildren().clear();
				// algoContainer = algoContainer.getClass().newInstance();
				// algoContainerView =
				// algoContainerView.getClass().newInstance();
				// algoContainer =
				// ComponentFactory.componentClone(algoContainer, false);
				// algoContainerView =
				// ComponentFactory.componentClone(algoContainerView, false);
			}
			refreshElementsContainer();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		super.initBean();
		bean = new DevEntityObject();
		bean.setEntityClass(entity);
		firstRender = true;
		long endTime2 = Calendar.getInstance().getTimeInMillis();
		// System.out.println("Time initBean " + this.subtitle + ": " +
		// (endTime2 - startTime1));
	}

	public void refreshElementsContainer() {
		if (userAuth != null && userAuth.getServiceContract() != null) {
			AdmService service = (AdmService) userAuth.getServiceContract().getService();
			generateElementsContainerMap(service);
			if (containerPage != null) {
				updateContainerPage();
			}
		}
	}

	public void generateElementsContainerMap(AdmService service) {
		try {
			if (service != null && service.getRequirement() == null) {
				service = (AdmService) doBeanRefresh(service);
			}
			if (service != null && service.getRequirement() != null) {
				requirement = (DevRequirement) doBeanRefresh(service.getRequirement());
				generateElementsContainerMap(requirement);
				//getAppBean().setRequirement(requirement);
			}
		} catch (Throwable ex) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void generateElementsContainerMap(DevRequirement requirement) {
		if (requirement != null) {			
			getAppBean().getElementsContainerMap(requirement).clear();
			entity = requirement.getEntityClass();
			//getAppBean().setEntity(entity);
			List<DevComponentContainer> componentContainerList = requirement.getComponentContainerList();
			if(componentContainerList.isEmpty()){
				String msgStr = "Esta Aplicação precisa ser gerada novamente. Informe ao administrador do sistema.(generateElementsContainerMap)";
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, msgStr, "");
				FacesContext.getCurrentInstance().addMessage(null, msg);
				Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, msgStr);				
			}
			for (DevComponentContainer devComponentContainer : componentContainerList) {
				if(devComponentContainer.getPrototypeComponentChildrenList()!=null && !devComponentContainer.getPrototypeComponentChildrenList().isEmpty()){
					//Application app = FacesContext.getCurrentInstance().getApplication();

					//UIComponent panel = app.createComponent(HtmlPanelGroup.COMPONENT_TYPE);
					List<UIComponent> elements;
					elements = AlgodevUtil.generateComponentList(devComponentContainer);
					
					UIComponent panel = new HtmlPanelGroup();
					FacesContext facesContext = FacesContext.getCurrentInstance();
					ValueExpression val = facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), String.valueOf("#{'"+devComponentContainer.getName()+"'== app.containerPage}"), Boolean.class);					
					panel.setValueExpression("rendered",val);
					panel.setTransient(true);
					panel.getChildren().addAll(elements);
					panel.setId(devComponentContainer.getName().trim()+Long.toHexString(System.nanoTime()));
					elements = Collections.singletonList(panel);
					
					getAppBean().getElementsContainerMap(requirement).put(devComponentContainer.getName(), elements);	
				}
			}
		}else{
			String msgStr = "Esta Aplicação precisa ser carregada novamente. Informe ao administrador do sistema.(generateElementsContainerMap)";
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, msgStr, "");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, msgStr);				
		}
	}
	public void initBrowserCache(){
		beanMapBrowserCached = new TreeSet<String>();
	}
	public UIComponent getAlgoContainer() {
		/*
		 * if(algoContainer!=null && algoContainer.getChildren().isEmpty() &&
		 * elements != null){ algoContainer.getChildren().addAll(elements); }
		 */
		
		UIComponent algoContainerTabView = ComponentFactory.findComponentByStyleClass("tabViewAlgoBoss", FacesContext.getCurrentInstance().getViewRoot());
		UIComponent algoContainer = null;
		try {
			if(getBaseBean() != null && algoContainerTabView!=null){
				algoContainer = ComponentFactory.findComponentByStyleClass("ui-algo-container", algoContainerTabView.getChildren().get(getBaseBean().getContainerIndex()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return algoContainer;
	}
	public void setAlgoContainer() {		
		UIComponent algoContainer = getAlgoContainer();
		if(algoContainer!=null){
			this.algoContainer = algoContainer;
			this.algoContainerView = algoContainer;					
			if (getAppBean().getElementsContainerMap(requirement).isEmpty()) {
				refreshElementsContainer();
			}else{
				updateContainerPage();				
			}
			//refresh();
		}		
	}
	public void setAlgoContainer(UIComponent algoContainer) {
		String x = "";
	}
	public void setAlgoContainerx(UIComponent algoContainer) {

		// draggedClientId = String.valueOf(map.get("elementModelId"));
	try {
/*

		if (!isInvalidUpdateContainer && isToUpdate(algoContainer)) {
			if (getAppBean().getElements().isEmpty()) {
				refreshElementsContainer();
			}
			if(algoContainer!=null && algoContainer.getChildren().isEmpty()){
				this.algoContainer = algoContainer;
				this.algoContainerView = algoContainer;					
				updateContainerPage();
			}
				if(!algoContainer.getChildren().containsAll(getAppBean().getElements())){
				
				if(this.algoContainer != null && !algoContainer.equals(this.algoContainer)){
					//algoContainer.setInView(false);
					//this.algoContainer.markInitialState();
				}
				
				
				
				
						
				
				//algoContainer.getChildren().clear();
				ArrayList<ComponentStruct> actions = (ArrayList<ComponentStruct>)StateContext.getStateContext(FacesContext.getCurrentInstance()).getDynamicActions();
				//componentMap.clear();
				HashMap<String, UIComponent> componentMap = StateContext.getStateContext(FacesContext.getCurrentInstance()).getDynamicComponents();
				//StateContext.release(FacesContext.getCurrentInstance());
				if (actions != null && false) {
					            List<Object> toRemoveActions = new ArrayList<Object>();
					            for (ComponentStruct action : actions) {
					            	UIComponent elFound = null;
					                for (UIComponent el : getAppBean().getElements()) {
					                	try {
					                		elFound = ComponentFactory.doFindComponent(el, action.id);
					                		if (el.getId().equals(action.id) ||  elFound != null) {
					                			elFound = el;
					                			break;
					                		}
			
										} catch (Throwable e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
					                }
					           		if (elFound != null) {
			                			toRemoveActions.add(action);
			                		}
					            }
					            actions.removeAll(toRemoveActions);
				}
				
				
				//elements = algoContainer.getChildren();

				//algoContainer.setInView(true);
			}
		}*/
	} catch (Exception e) {
		e.printStackTrace();
	}
	}

	public void setAlgoContainer(ComponentSystemEvent algoContainerEvt) {
		UIComponent algoContainer = algoContainerEvt.getComponent();
		setAlgoContainer(algoContainer);
	}

	public UIComponent getAlgoContainerView() {
		isToUpdate(algoContainerView);
		return algoContainerView;
	}

	public void setAlgoContainerView(UIComponent algoContainerView) {
		if (isToUpdate(algoContainerView)) {
			this.algoContainerView = algoContainerView;
		}
	}
	
	public void setAlgoContainerViewWidget(UIComponent algoContainerView) {
		this.algoContainerView = null; 
		this.algoContainer = algoContainerView;
	}	

	private boolean isToUpdate(UIComponent algoContainerView) {
		boolean toUpdate = false;
		if (algoContainerView != null) {
			if (algoContainerView.getChildren().isEmpty()) {
				toUpdate = true;
			}/* else if (ComponentFactory.findComponentByStyleClass("c_" + requirement.getRequirementId(), algoContainerView) != null) {
				toUpdate = true;
			} else if (ComponentFactory.findComponentByStyleClass("c_requirement_tab", algoContainerView) == null) {
				// toUpdate = true;
				UIComponent tab = baseBean.getTabView().getChildren().get(baseBean.getContainerIndex());
				UIComponent container = ComponentFactory.findComponentByStyleClass("ui-algo-container", tab);
				if (container != null && container.getId().equals(algoContainerView.getId())) {
					Map<String, Object> mapParam = new HashMap<String, Object>();
					mapParam.put("styleClass", "ui-algo-container c_requirement_tab c_" + requirement.getRequirementId());
					// mapParam.put("styleClass", "");
					ComponentFactory.updateElementProperties(algoContainerView, mapParam);
					toUpdate = true;
				}
			}*/else{
				UIComponent tab = getBaseBean().getTabView()!=null?getBaseBean().getTabView().getChildren().get(getBaseBean().getContainerIndex()):null;
				UIComponent container = ComponentFactory.findComponentByStyleClass("ui-algo-container", tab);
				if (container != null && container.getId().equals(algoContainerView.getId())) {
					Map<String, Object> mapParam = new HashMap<String, Object>();
					mapParam.put("styleClass", "ui-algo-container c_requirement_tab c_" + requirement.getRequirementId());
					// mapParam.put("styleClass", "");
					ComponentFactory.updateElementProperties(algoContainerView, mapParam);
					toUpdate = true;
				}				
			}
		}
		return toUpdate;
	}

	public void updateContainerPage() {
		fileValue = null;
		fileDownload = null;
		boolean start = false;
		algoContainerView = getAlgoContainer();
        SessionUtilBean.set(FacesContext.getCurrentInstance());//NEEDED TO APPBEAN
		if(algoContainerView!=null && requirement!=null && getAppBean().getElementsContainerMap(requirement)!=null && !isInvalidUpdateContainer){
			if(!algoContainerView.getId().equals(PREFIX_BEAN+requirement.getRequirementId())){
				algoContainerView.getChildren().clear();
			}
			if(algoContainerView.getChildren().isEmpty()){
				algoContainerView.setId(PREFIX_BEAN+requirement.getRequirementId());
				containerClass = Objects.toString(ComponentFactory.getProperty(algoContainerView, "styleClass"));
				setAppBean(PREFIX_BEAN+getRequirement().getRequirementId());
				start = true;
			}
				
			if(start){
				Map<String, List<UIComponent>> elementsContainerMap = getAppBean().getElementsContainerMap(requirement);
				if(elementsContainerMap.isEmpty()){
					
				}
				for(Map.Entry<String, List<UIComponent>> object : elementsContainerMap.entrySet()){
					try {
						algoContainerView.getChildren().addAll(object.getValue());													
					} catch (javax.faces.FacesException e) {
						Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, "EXCEPTION updateContainerPage:"+e.getMessage());
					}
				}
				System.out.println("### UpdateContainerPage!");
				initJsEngine();				
			}				
		}
	}

	public String getContainerPage() {
		return containerPage;
	}
	
	public void setContainerPage(String containerPage){
		this.containerPage = containerPage;
	}

	public UIComponent getElementSelected() {
		return elementSelected;
	}

	public void setElementSelected(UIComponent elementSelected) {
		this.elementSelected = elementSelected;
	}	
	
	public UIComponent getAlgoContainerForm() {
		return algoContainerForm;
	}

	public void setAlgoContainerForm(UIComponent algoContainerForm) {
		/*
		 * if(algoContainerForm!=null && algoContainerView!=null){ UIComponent
		 * compTab = algoContainerForm.getParent(); UIComponent comp1 = null;
		 * List<UIComponent> compList1 = compTab.getChildren(); while ((comp1 =
		 * ComponentFactory.findComponentByStyleClass("ui-algo-container-form",
		 * compTab))!=null) { compList1.remove(comp1); }
		 * compList1.add(algoContainerForm); UIComponent comp = null;
		 * List<UIComponent> compList = algoContainerForm.getChildren(); while
		 * ((comp =
		 * ComponentFactory.findComponentByStyleClass("ui-algo-container",
		 * algoContainerForm))!=null) { compList.remove(comp); }
		 * compList.add(algoContainerView); }
		 */
		this.algoContainerForm = algoContainerForm;
	}

	@Override
	public void doBeanList() {
		beanList = findListByClass(entity);
		bean = new DevEntityObject();
		bean.setEntityClass(entity);	
		//beanMapBrowserCached.clear();
		// super.doBeanList(); //To change body of generated methods, choose
		// Tools | Templates.
	}

	public void doList() {
		doList("list", "");
	}

	public void doList(String container, String node) {
		//updateContainerPage();
		nextContainerPage = container;
		boolean listOk = false;
		if (!container.equals("list")) {
			doBeanListChild(node);
			listOk = beanListChild != null;
		} else {
			doBeanList();
			//int val = 3/0;
			listOk = beanList!=null;
		}
		if(listOk){
			containerPage = container;
			getAppBean().setContainerPage(containerPage);	
		}	
	}

	@Override
	public void doBeanForm() {
		super.doBeanForm(); // To change body of generated methods, choose Tools
							// | Templates.
	}

	public void doForm() {	
		doForm("form");
	}

	public void doForm(String container) {
		//updateContainerPage();
		if (!container.equals("form")) {
			// String node =
			// container.split("-")[container.split("-").length-1];
			// childBean = new DevEntityObject();
		} else {
			doBeanForm();
		}
		containerPage = container;
		getAppBean().setContainerPage(containerPage);	
	}

	public void doRemove(DevEntityObject obj) {
		bean = obj;
		doBeanRemove();
		System.out.println("### REMOVE "+entity.getName() +"!");
	}

	public void doRemoveChild(DevEntityObject obj) {
		childBean = obj;
		if (childBean != null) {
			try {
				// super.doBeanSave(childBean);
				if (childValue.getPropertyChildrenList().contains(childBean)) {
					childValue.getPropertyChildrenList().remove(childBean);
				}
				childBean = new DevEntityObject();
				childBean.setEntityClass(childEntity);
				System.out.println("### REMOVE "+childEntity.getName() +"!");
			} catch (Exception ex) {
				Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
			}
		}
		// bean = obj;
		// doBeanRemove();
	}

	public void doEdit(DevEntityObject obj) {
		bean = doBeanRefresh(obj);
		if(bean!=null){
			getAppBean().setBean(bean);
			doForm();
		}
	}
	
	public DevEntityObject doBeanRefresh(DevEntityObject obj){
		return (DevEntityObject)super.doBeanRefresh(obj);
	}

	public void doSelect(DevEntityObject obj) {
		bean = doBeanRefresh(obj);
	}	
	
	public void doCopy(DevEntityObject obj) {
		bean = new DevEntityObject(obj);
		doForm();
	}

	public void doCopy() {
		if (bean != null) {
			bean = new DevEntityObject(bean);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro Copiado com Sucesso! Faça as alterações se necessário e salve.", "");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public void doCopyChild() {
		if (childBean != null) {
			childBean = new DevEntityObject(childBean);
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro Copiado com Sucesso! Faça as alterações se necessário e salve.", "");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public void doEditChild(DevEntityObject obj, String container) {
		childBean = obj;
		doForm(container);
	}
	
	public void doSelectChild(DevEntityObject obj) {
		childBean = obj;
	}
	
	public void setBeanSel(Object obj) {
		doEdit((DevEntityObject) obj);
		super.setBean((DevEntityObject) obj); 
	}

	public Object getBeanSel() {
		return beanSel;
	}

	public void doSave() throws Throwable {
		doSave(false);
	}

	public void doSaveChild() {
		if (childBean != null) {
			try {
				// super.doBeanSave(childBean);
				if (!childValue.getPropertyChildrenList().contains(childBean)) {
					childValue.getPropertyChildrenList().add(childBean);
				}
				childBean.prePersist();
				childBean = new DevEntityObject();
				childBean.setEntityClass(childEntity);
				System.out.println("### SAVE "+childEntity.getName() +"!");
			} catch (Exception ex) {
				Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
			}
		}
	}

	public void doSaveAndListChild(String container, String node) {
		doSaveChild();
		doList(container, node);
	}

	public void doSaveAndList() {
		doSave(true);
	}

	public void doSave(boolean hasList) {
		try {
			if (bean != null) {
				super.doBeanSaveAndList(true, true, false, bean);					
				bean.setEntityClass(entity);
				//beanMapBrowserCached.clear();
				System.out.println("### SAVE "+entity.getName() +"!");
			}
			if (hasList) {
				doList();
			}
		} catch (Throwable ex) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void doSaveItem(DevEntityObject obj) {
		doBeanSaveAndList(true, false, false, obj);
	}

	public Map beanMap(String key) {
		return beanMap(key, "");
	}
	public Map beanMap(String className, String keysStr) {
		return beanMap(className, keysStr, "");
	}
	public Map beanMap(String className, String keysStr, String valsStr) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		String configValue = AlgodevUtil.formatDescription(className+";"+valsStr+";"+keysStr);
		String[] keys = keysStr.split(";");
		String[] vals = valsStr.split(";");
		String field1 = "";
		String criteria = "";
		String field2 = "";
		String[] classArray = className.split("\\]|\\[|\\|",-1);
		if(classArray.length>1){
			className = classArray[0];
			field1 = classArray[1];
			criteria = classArray[2];
			field2 = classArray[3];
		}		
		List<DevEntityObject> list = doBeanList(className);	
		try {
			if (list != null) {
				for (DevEntityObject devEntityObject : list) {
					String field1Value = Objects.toString($(className + "." + field1, devEntityObject).getVal(),"");
					if(criteria.equals("contains")){
						if(!field1Value.contains(field2)){
							continue;
						}
					}
					if(criteria.equals("equals") || criteria.equals("eq")){
						if(!field1Value.equals(field2)){
							continue;
						}
					}			
					if(criteria.equals("lessThan") || criteria.equals("lt")){
						if(!(field1Value.compareTo(field2)<0)){
							continue;
						}
					}	
					if(criteria.equals("greaterThan") || criteria.equals("gt")){
						if(!(field1Value.compareTo(field2)>0)){
							continue;
						}
					}							
					Object keyMap = devEntityObject;
					Object valMap = devEntityObject;
					String valStr = "";
					for (int i = 0; i < vals.length && !valsStr.isEmpty(); i++) {
						String val = vals[i];
						DevEntityPropertyValue value = $(className + "." + val, devEntityObject);
						if (i > 0) {
							valStr += " - ";
						}
						valStr += String.valueOf(value.getPropertyValue());
					}
					String keyStr = "";
					for (int i = 0; i < keys.length && !keysStr.isEmpty(); i++) {
						String key = keys[i];
						DevEntityPropertyValue value = $(className + "." + key, devEntityObject);
						if (i > 0) {
							keyStr += " - ";
						}
						keyStr += String.valueOf(value.getStr());
					}					
					if (!keyStr.isEmpty()) {
						keyMap = keyStr;
					}
					if (!valStr.isEmpty()) {
						valMap = valStr;
					}					
					map.put(keyMap, valMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean isCacheble = !keysStr.isEmpty() && !valsStr.isEmpty();
		if(!beanMapBrowserCached.contains(configValue) || isInvalidUpdateContainer){
			beanMapBrowserCached.add(configValue);
			idxBrowserCached = idxBrowserCached.concat(isCacheble+";");
		}else{
			if(isCacheble){
				List<Map.Entry<Object,Object>> listMapTmp = new ArrayList<Map.Entry<Object,Object>>();
				for(Map.Entry<Object, Object> object : map.entrySet()){
					listMapTmp.add(object);
				}
				for(DevEntityObject obj:new DevEntityObject[]{bean,childBean}){
					DevEntityClass ent = 	obj.getEntityClass();			
					if(ent!=null){
						List<DevEntityPropertyDescriptor> propList = ent.getEntityPropertyDescriptorList();
						for (DevEntityPropertyDescriptor devEntityPropertyDescriptor : propList) {
							List<DevEntityPropertyDescriptorConfig> propConfigList = devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList();
							for (DevEntityPropertyDescriptorConfig devEntityPropertyDescriptorConfig : propConfigList) {
								if(devEntityPropertyDescriptorConfig.getConfigName().equalsIgnoreCase("objectList") && AlgodevUtil.formatDescription(devEntityPropertyDescriptorConfig.getConfigValue()).equals(configValue)){
									String field1Value = Objects.toString($(ent.getName() + "." + devEntityPropertyDescriptor.getPropertyName(), obj).getVal(),"");
									for(Map.Entry<Object,Object> object : listMapTmp){
										if(object.getValue().equals(field1Value)){
											return Collections.singletonMap(object.getKey(), field1Value);
										}
									}
								}
							}
						}
					}else{						
						String msgStr = "FALHA AO CARREGAR LISTA DE "+className+" EM CACHE. FECHE O APLICATIVO E ABRA NOVAMENTE.";
						FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgStr, "");
						FacesContext.getCurrentInstance().addMessage(null, msg);
						Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, msgStr);
					}
				}
				if(!listMapTmp.isEmpty()){
					return Collections.singletonMap(listMapTmp.get(0).getKey(), listMapTmp.get(0).getValue());				
				}
			}	
		}
		return map;
	}

	public List<DevEntityObject> doBeanList(String className) {
		List<DevEntityObject> entityObjectList = null;
		if (className != null && !className.isEmpty()) {
			if (beanListMap.containsKey(className) && !className.contains(".")) {
				entityObjectList = beanListMap.get(className);
			} else {
				entityObjectList = findListByClass((entity.getName()!=null && entity.getName().equals(className))?entity:baseDao.findEntityClass(className));
				if(entityObjectList!=null){
					beanListMap.put(className, entityObjectList);
					beanListFilteredMap.put(className, new BeanList(entityObjectList));
					
					getAppBean().setBeanListMap(beanListMap);
					getAppBean().setBeanListFilteredMap(beanListFilteredMap);
				}
				formRendered = false;
			}
		}
		return entityObjectList; // To change body of generated methods, choose
									// Tools | Templates.
	}

	public List<DevEntityObject> doBeanListChild(String node) {
		List<DevEntityObject> childrenList = null;
		if (node != null && !node.isEmpty() && node.contains(".")) {
			boolean newOk = newChildBean(node);
			if (newOk) {
				//baseDao.entityObjectSyncList(className, siteIdList, objectList, objectListSmallRest, Class.forName(childEntity.getCanonicalClassName()));
				childrenList = childValue.getPropertyChildrenList();
				if(childrenList == null){
					baseDao.entityObjectSyncPopulate(bean);	
					childValue = $(node.toLowerCase());
					childrenList = childValue.getPropertyChildrenList();
					if(childrenList == null){
						childrenList = new ArrayList();						
						childValue.setPropertyChildrenList(childrenList);
					}
				}
				beanListMap.put(node.toLowerCase(), childrenList);
				beanListFilteredMap.put(node.toLowerCase(), new BeanList(childrenList));
				getAppBean().setBeanListMap(beanListMap);
				getAppBean().setBeanListFilteredMap(beanListFilteredMap);				

			}
		}
		beanListChild = childrenList; 
		return childrenList;
	}
	public boolean newChildBean(String node){
		try {
			if (node != null && !node.isEmpty() && node.contains(".")) {
				childValue = $(node.toLowerCase());
				if (childValue != null && childValue.getEntityPropertyDescriptor() != null) {
					childEntity = childValue.getEntityPropertyDescriptor().getPropertyClass();
					getAppBean().setBeanListMap(beanListMap);
					getAppBean().setBeanListFilteredMap(beanListFilteredMap);				
					childBean = new DevEntityObject();
					childBean.setEntityClass(childEntity);
					for (DevEntityPropertyDescriptor propDesc : childEntity.getEntityPropertyDescriptorList()) {
						if (propDesc.getPropertyClass() != null && propDesc.getPropertyClass().equals(entity)) {
							try {
								$(childEntity.getName() + "." + propDesc.getPropertyName(), childBean).setVal(bean);
							} catch (Exception ex) {
								Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
							}
							break;
						}
					}	
					return true;
				}
			}			
		} catch (Exception e) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, e);
		}
		return false;

		
	
	}
	public List<DevEntityObject> findListByClass(DevEntityClass className) {
		List<DevEntityObject> entityObjectList = null;
		if (className != null) {
			String msgStr = "";
			try {
				if (className.equals("sec_user")) {
					userBean.doBeanList();
					List<SecUser> userList = userBean.getBeanList();
					DevEntityClass entClass = new DevEntityClass();
					entClass.setEntityClassId(1L);

					entityObjectList = new ArrayList<DevEntityObject>();
					for (SecUser secUser : userList) {
						DevEntityObject entObj = new DevEntityObject();
						entObj.setEntityClass(entClass);
						entObj.setEntityObjectId(Double.valueOf(Math.pow(secUser.getUserId(), 5)).longValue());
						DevEntityPropertyValue propVal = new DevEntityPropertyValue();
						propVal.setUser(secUser);
						DevEntityPropertyDescriptor propDesc = new DevEntityPropertyDescriptor();
						propDesc.setPropertyIdentifier(true);
						propVal.setEntityPropertyDescriptor(propDesc);
						entObj.getEntityPropertyValueList().add(propVal);
						entityObjectList.add(entObj);
					}
				} else {
					if (className.getName().contains(".")) {
						DevEntityPropertyValue childValueProp = $(className.getName());
						if (childValueProp != null && childValueProp.getEntityPropertyDescriptor() != null) {
							entityObjectList = childValueProp.getPropertyChildrenList();
						}
					} else {
						List<String> cols = new ArrayList<String>();
						if(entity.getName()!=null && entity.getName().equals(className.getName())){
							cols = LayoutFieldsFormat.getVisibleFieldsNames(requirement, nextContainerPage, entity);							
						}
						entityObjectList = baseDao.findEntityObjectByClass(className,cols, getSiteIdList(), null, null);													
					}
				}
				// entityObjectList = baseDao.findEntityObjectByClass(className,
				// getSiteIdList(), startDate, endDate);
				// bean = new DevEntityObject();
				beanListMap.put(className.getName(), entityObjectList);
				beanListFilteredMap.put(className.getName(), new BeanList(entityObjectList));
				getAppBean().setBeanListMap(beanListMap);
				getAppBean().setBeanListFilteredMap(beanListFilteredMap);				
				formRendered = false;
				// return "/f/cadastro/usuario/usuarioList.xhtml";
			} catch (Throwable ex) {
				msgStr = "Falha no processamento. Motivo: " + ex.getClass() +"-"+ex.getMessage();
				ex.printStackTrace();
				//FacesContext.getCurrentInstance().getExternalContext().responseReset();
				//FacesContext.getCurrentInstance().responseComplete();
			} finally {
				if (!msgStr.isEmpty()) {
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgStr, "");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
		}
		return entityObjectList; // To change body of generated methods, choose
									// Tools | Templates.
	}

	public void prepareUpload(Object obj) {
		try {
			if (obj != null) {
				fileValue = (DevEntityPropertyValue) obj;
				setFilePreview(null);
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("isValid", true);
			}
		} catch (Throwable ex) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void handleFileUpload() throws Exception {

	}

	public void handleFileUpload(FileUploadEvent event) throws Exception {
		UploadedFile fileAux = event.getFile();
		if(fileAux!=null && fileValue!=null){
			fileValue.setVal(fileAux.getFileName());
			fileValue.setPropertyFile(fileAux.getContents());
		}
	}

	public void exportDataTable() {
		try {
			Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String source = String.valueOf(map.get("javax.faces.source"));
			String draggedId = source.split(":")[source.split(":").length - 1];
			UIComponent comp3 = null;
			algoContainer = getAlgoContainer();
			if (algoContainer != null && !draggedId.isEmpty()) {
				comp3 = ComponentFactory.doFindComponent(algoContainer.getParent().getParent(), draggedId);
			}
			if (comp3 != null) {
				UIComponent parent = comp3.getParent();
				dataTableSelected = parent;
				elementSelectedId = dataTableSelected.getId();
				FacesContext facesContext = FacesContext.getCurrentInstance();
				PartialViewContext partialViewContext = facesContext.getPartialViewContext();
				UIViewRoot viewRoot = facesContext.getViewRoot();
				Collection<String> renderIds = partialViewContext.getRenderIds();
				renderIds.add(algoContainer.getClientId());
			}
			String x = "";
		} catch (Throwable ex) {
			Logger.getLogger(AdmAlgodevBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void exportHtmlToPdf() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().setRequestCharacterEncoding("utf-8");
			System.out.println(FacesContext.getCurrentInstance().getExternalContext().getRequestCharacterEncoding());
			Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String html = String.valueOf(map.get("html"));
			String css = String.valueOf(map.get("css"));
			System.out.println(html);
			PDFExporter2 exp = new PDFExporter2();
			// exp.export(FacesContext.getCurrentInstance(), ((DataTable)
			// dataTableSelected), requirement.getRequirementName()+"_Report");
			exp.export(FacesContext.getCurrentInstance(), css, html, requirement.getRequirementName() + "_Report");
		} catch (IOException ex) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void exportPdf() throws IOException {
		try {
			PDFExporter exp = new PDFExporter();
			// exp.export(FacesContext.getCurrentInstance(), ((DataTable)
			// dataTableSelected), requirement.getRequirementName()+"_Report");
			if(elementSelected != null){
				exp.export(FacesContext.getCurrentInstance(), ((DataTable) elementSelected), requirement.getRequirementName() + "_Report", "iso-8859-1");
			}
			// ContentCaptureServletResponse capContent = new
			// ContentCaptureServletResponse(dataTableSelected);

		} catch (Exception e) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	protected UIComponent filePreview;
	protected int imgRandon;
	private Map<String, Object> methodMap;

	public int getImgRandon() {
		imgRandon = Double.valueOf(Math.random() * 10000).intValue();
		return imgRandon;
	}

	public void setImgRandon(int imgRandon) {
		this.imgRandon = imgRandon;
	}

	public StreamedContent getFileDownload(int rand) {
		return fileDownload;
	}

	public UIComponent getFilePreview() {
		return filePreview;
	}
	public void setFilePreviewEvt(ComponentSystemEvent filePreviewEvent) {
		if (filePreviewEvent != null) {
			filePreview = filePreviewEvent.getComponent();
			setFilePreview(filePreview);
		}
	}
	public void setFilePreview(UIComponent filePreview) {
		if (filePreview != null) {
			String action = "#{app.getFileDownload(" + getImgRandon() + ")}";
			ValueExpression value = FacesContext.getCurrentInstance().getApplication().getExpressionFactory().createValueExpression(FacesContext.getCurrentInstance().getELContext(), action, Object.class);
			filePreview.setValueExpression("value", value);
			this.filePreview = filePreview;
		}
		if (fileValue != null && fileValue.getPropertyValue() != null) {
			try {
				setFileDownload(prepareStreamedContent(fileValue));
			} catch (Exception e) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao Carregar Arquivo!", "");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}
	}

	public StreamedContent prepareStreamedContent(DevEntityPropertyValue fileValue) {
		if (fileValue != null && fileValue.getPropertyFile() != null) {
			try {
				ByteArrayInputStream bis = new ByteArrayInputStream(fileValue.getPropertyFile());
				String extension = String.valueOf(fileValue.getPropertyValue()).substring(String.valueOf(fileValue.getPropertyValue()).lastIndexOf("."));
				String contentType = "";
				if (extension.contains(".jpg")) {
					contentType = "image/jpg";
				} else if (extension.contains(".gif")) {
					contentType = "image/gif";
				} else if (extension.contains(".jpeg")) {
					contentType = "image/jpeg";
				} else if (extension.contains(".png")) {
					contentType = "image/png";
				} else if (extension.contains(".bmp")) {
					contentType = "image/bmp";
				} else if (extension.contains(".pdf")) {
					contentType = "application/pdf";
				} else {
					contentType = "image/jpg";
				}
				StreamedContent sc = new DefaultStreamedContent(bis, contentType, String.valueOf(fileValue.getPropertyValue()));
				return sc;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	public String getFile(DevEntityPropertyValue fileValue) {
		String urlFileMap = "";
		try {
			if (fileValue != null) {
				StreamedContent sc = prepareStreamedContent(fileValue);
				ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
				// HttpSession session = (HttpSession)
				// context.getSession(false);
				Map<String, HashMap<String, Object>> fileDataBaseMap = loginBean.getFileDataBaseMap();
				// if (session.getAttribute("fileDataBaseMap") != null) {
				// fileDataBaseMap = (HashMap<String, HashMap<String, Object>>)
				// session.getAttribute("fileDataBaseMap");
				// } else {
				// fileDataBaseMap = new HashMap<String, HashMap<String,
				// Object>>();
				// session.setAttribute("fileDataBaseMap", fileDataBaseMap);
				// }
				HashMap<String, Object> fileDataBasePropMap = new HashMap<String, Object>();
				fileDataBasePropMap.put("fileName", sc.getName());
				fileDataBasePropMap.put("content", sc.getStream());
				UUID uuid = UUID.randomUUID();
				String strUuid = uuid.toString();
				fileDataBaseMap.put(strUuid, fileDataBasePropMap);
				urlFileMap = context.getRequestServletPath() + "/file?uuid=" + strUuid;
				// getFile(strUuid);
			}
		} catch (Exception ex) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
		}
		return urlFileMap;
	}

	protected void getFileX(String id) {
		FacesContext context = loginBean.getFacesContext();
		ExternalContext externalContext = context.getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(false);
		Map<String, HashMap<String, Object>> fileDataBaseMap = null;
		if (session.getAttribute("fileDataBaseMap") != null) {

			try {
				fileDataBaseMap = (HashMap<String, HashMap<String, Object>>) session.getAttribute("fileDataBaseMap");
				HashMap<String, Object> propMap = fileDataBaseMap.get(id);
				InputStream inputStream = (InputStream) propMap.get("content");
				String fileName = String.valueOf(propMap.get("fileName"));
				// externalContext.responseReset();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(inputStream.toString().getBytes());
				// ByteArrayOutputStream baos2 = new
				// ByteArrayOutputStream(1000000);
				OutputStreamWriter writer = new OutputStreamWriter(baos, "iso-8859-1");
				HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
				context.setResponseWriter(context.getRenderKit().createResponseWriter(writer, null, "iso-8859-1"));
				context.setResponseStream(context.getRenderKit().createResponseStream(baos));
				// response.reset();
				response.setStatus(200);
				response.setContentType(externalContext.getMimeType(fileName));
				response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Expires", "Mon, 8 Aug 1980 10:00:00 GMT");
				response.setContentLength(baos.size());
				response.getOutputStream().write(baos.toByteArray(), 0, baos.size());
				FacesContext.getCurrentInstance().responseComplete();
				// PrintWriter writer = response.getWriter();
				// JspFactory.getDefaultFactory().getPageContext(null, null,
				// response, "", true, 1, false);
				// OutputStream out = externalContext.getResponseOutputStream();
				// externalContext.setResponseStatus(200);
				// externalContext.setResponseContentType(externalContext.getMimeType(fileName));

				// externalContext.setResponseHeader("Cache-Control",
				// "no-cache, no-store, must-revalidate");
				// externalContext.setResponseHeader("Pragma", "no-cache");
				// externalContext.setResponseHeader("Expires",
				// "Mon, 8 Aug 1980 10:00:00 GMT");
				// baos.writeTo(out);
				// externalContext.getResponseOutputStream().write(baos.toByteArray(),
				// 0, baos.size());
				byte[] buffer = new byte[8192];

				int length;
				// externalContext.getResponse()ResponseOutputStream().close();
				while ((length = (inputStream.read(buffer))) >= 0) {
					// externalContext.getResponseOutputStream().write(buffer,
					// 0, length);
				}
				externalContext.responseFlushBuffer();
				context.responseComplete();
			} catch (Exception ex) {
				ex.printStackTrace();
				// Logger.getLogger(GerAuthFilter.class.getName()).log(Level.SEVERE,
				// null, ex);
			}
		} else {
			System.out.println("Não encontrou fileDataBaseMap na sessão.");
		}
	}

	public class ContentCaptureServletResponse extends HttpServletResponseWrapper {

		protected ByteArrayOutputStream contentBuffer;
		protected PrintWriter writer;

		public ContentCaptureServletResponse(HttpServletResponse resp) {
			super(resp);
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			if (writer == null) {
				contentBuffer = new ByteArrayOutputStream();
				writer = new PrintWriter(contentBuffer);
			}
			return writer;
		}

		public String getContent() {
			writer.flush();
			String xhtmlContent = new String(contentBuffer.toByteArray());
			xhtmlContent = xhtmlContent.replaceAll("<thead>|</thead>|" + "<tbody>|</tbody>", "");
			return xhtmlContent;
		}
	}

	public LoginValidator loginValidator() {
		return new LoginValidator();
	}

	public void validateLogin(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		SecUser user = new UsuarioDao(baseDao).findByEmailAndPassword(loginBean.getUser().getEmail().toUpperCase(), String.valueOf(value));
		boolean isValidate = false;
		if (user != null) {
			if (!user.isInactive()) {
				isValidate = true;
			}
		}
		if (!isValidate) {

			FacesMessage msg = new FacesMessage(BaseBean.getBundle("incorrectPassword", "msg"), "");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);

		}

	}

	public void sendEmail() {
		try {
			Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String dest = String.valueOf(map.get("email_destin"));
			String name = String.valueOf(map.get("name_destin"));
			String msg = String.valueOf(map.get("email_msg"));
			String title = String.valueOf(map.get("email_title"));
			List<String[]> destin = new ArrayList<String[]>();
			destin.add(new String[] { dest, name });
			sendEmail(destin, "atendimento@algoboss.com", "AlgoBoss", title, msg, null);
			FacesMessage msgRet = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sua mensagem foi enviada com sucesso.", "");
			FacesContext.getCurrentInstance().addMessage(null, msgRet);
		} catch (Exception ex) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void randomSort() {
		try {
			Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String amountField = String.valueOf(map.get("amount_field"));
			String listField = String.valueOf(map.get("list_field"));
			String targetField = String.valueOf(map.get("target_field"));

			List<DevEntityObject> childrenList = null;
			if (listField != null && !listField.isEmpty() && listField.contains(".")) {
				DevEntityPropertyValue childValue2 = $(listField.toLowerCase());
				if (childValue2 != null && childValue2.getEntityPropertyDescriptor() != null) {
					childrenList = childValue2.getPropertyChildrenList();
				}
			}
			List<DevEntityObject> entToSort = new ArrayList<DevEntityObject>();
			for (int i = 0; i < childrenList.size(); i++) {
				DevEntityObject devEntityObject = childrenList.get(i);
				DevEntityPropertyValue childValue2 = $(amountField.toLowerCase(), devEntityObject);
				Integer amountSort = Integer.valueOf(String.valueOf(childValue2.getPropertyValue()));
				for (int j = 0; j < amountSort; j++) {
					entToSort.add(devEntityObject);
				}
			}
			Random rand = new Random();
			Integer sortInt = rand.nextInt(entToSort.size());
			DevEntityObject devEntityObjectSort = entToSort.get(sortInt);
			DevEntityPropertyValue childValueTarget = $(targetField.toLowerCase());
			if (childValueTarget != null) {
				childValueTarget.setPropertyObject(devEntityObjectSort);
				// String title = String.valueOf(map.get("email_title"));
				// List<String[]> destin = new ArrayList<String[]>();
				// destin.add(new String[]{dest, name});
				// sendEmail(destin, "atendimento@algoboss.com", "AlgoBoss",
				// title, msg, null);
				FacesMessage msgRet = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sorteio realizado com sucesso.", "");
				FacesContext.getCurrentInstance().addMessage(null, msgRet);
			}
		} catch (Exception ex) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void resetForm(String id) {
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			context.reset(id);
		} catch (Throwable ex) {
			Logger.getLogger(AdmAlgodevBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	public void eventBean(){
		Map callMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();	
		String call = "";
		try {
			call = String.valueOf(callMap.get("call"));
			if(!call.contains(".")){
				actionCall(call);
			}else{
				AlgodevUtil.event(callMap, this);					
			}		
			boolean update = Boolean.parseBoolean(Objects.toString(callMap.get("update"),"false"));
			if(update){
			}
		} catch (Exception e) {
			Logger.getLogger(AdmAlgoappBean.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	public void actionCall(String call){
		String message = null;
		try {
			firstRender = true;
			System.out.println("### EVENT CALL: "+call);
			Map<String,String> map = new HashMap<String,String>();
			jsEngine.put("map", map);
			jsEngine.eval(call);
			message = map.get("message");
			String callback = map.get("callback");				
			if(callback!=null){
				//callback = StringEscapeUtils.escapeJavaScript(callback);
				RequestContext reqCtx = RequestContext.getCurrentInstance();        
				reqCtx.addCallbackParam("callback", callback);				
				System.out.println("### EVENT CALLBACK: "+callback);
			}
			
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message = e.getMessage();
		}	
		if(message!=null){
			String[] messageArray = message.split("\n");
			for (int i = 0; i < messageArray.length; i++) {
				message = messageArray[i];
				if(!message.isEmpty()){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message.toString(), "");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
		}

		//setAlgoContainer();
		ComponentFactory.resetComponent();
		//String listField = String.valueOf(map.get("list_field"));
		//String targetField = String.valueOf(map.get("target_field"));		
	}
	protected boolean isInvalidUpdateContainer = false;
	
	class AppPhaseListener implements PhaseListener{
		/**
		 * 
		 */
		private static final long serialVersionUID = 540211169676512128L;
		private AdmAlgoappBean bean;
		private int lastPhaseId;
		private AppComponentListener appComponentListener;
		private boolean isAlgoapp = false;
		
		public AdmAlgoappBean getBean() {
			return bean;
		}

		public void setBean(AdmAlgoappBean bean) {
			this.bean = bean;
		}		
		
		public AppPhaseListener(AdmAlgoappBean bean) {
			super();
			this.bean = bean;
		}

		@Override
		public void beforePhase(PhaseEvent event) {
			try {

				if (event.getPhaseId().equals(PhaseId.PROCESS_VALIDATIONS)) {
					bean.isInvalidUpdateContainer = true;
				}
				if (bean.containerClass.endsWith("-app") || bean.containerClass.endsWith("-rep")) {
					if (event.getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {
						//firstRender = false;
						UIComponent algoContainerX = bean.getAlgoContainer();
						isAlgoapp = algoContainerX != null;
						if (isAlgoapp && !bean.firstRender) {
							Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
							String source = String.valueOf(map.get("javax.faces.source"));
							String draggedId = source.split(":")[source.split(":").length - 1];
							//algoContainerX.unsubscribeFromEvent(arg0, arg1);
							//bean.generateElementsContainerMap(bean.requirement);
							isAlgoapp = !draggedId.equals("updateActiveSession") && !draggedId.equals("tabView") ;
							if(isAlgoapp){
								System.out.println(draggedId);
								bean.setAlgoContainer();
								ComponentFactory.resetComponent(algoContainerX);
							}
						}
					}
					/*if ((bean.firstRender || PhaseId.APPLY_REQUEST_VALUES.getOrdinal() != lastPhaseId) && PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
						bean.updateContainerPage();
						System.out.println("### First Render " + event.getPhaseId());
					}*/
					/*if ((bean.firstRender && PhaseId.APPLY_REQUEST_VALUES.getOrdinal() == lastPhaseId) && PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
						UIComponent algoContainerX = bean.getAlgoContainer();
						if (algoContainerX != null) {
							//algoContainerX.unsubscribeFromEvent(arg0, arg1);
							//appComponentListener = new AppComponentListener(bean);
							//algoContainerX.subscribeToEvent(PreRenderComponentEvent.class, appComponentListener);
						}						
					}*/
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println("Before Executing " + event.getPhaseId()+":"+(bean.getAlgoContainer()!=null?"not null":"null"));
		}

		@Override
		public void afterPhase(PhaseEvent event) {
			try {
				
			if (event.getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES) || event.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
				bean.isInvalidUpdateContainer = false;
			}
			if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
				/*UIComponent algoContainerX = bean.getAlgoContainer();
				if (algoContainerX != null) {
					List<SystemEventListener> se = algoContainerX.getListenersForEventClass(PreRenderComponentEvent.class);
					if(se!=null){
						se.clear();
					}
				}	
				bean.firstRender = false;*/
			}	
			if (bean.containerClass.endsWith("-app") || bean.containerClass.endsWith("-rep")) {
				if (event.getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {
					if(isAlgoapp){
						//ComponentFactory.resetComponent();						
					}
					//UIComponent algoContainerX = bean.getAlgoContainer();
					//if (algoContainerX != null) {
						//algoContainerX.unsubscribeFromEvent(arg0, arg1);
						//bean.updateContainerPage();
						//algoContainerX.getChildren().clear();
						//ComponentFactory.resetComponent(algoContainerX);
					//}			
				}
				/*if ((bean.firstRender || PhaseId.APPLY_REQUEST_VALUES.getOrdinal() != lastPhaseId) && PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
					bean.updateContainerPage();
					System.out.println("### First Render " + event.getPhaseId());
				}*/
				/*if ((bean.firstRender && PhaseId.APPLY_REQUEST_VALUES.getOrdinal() == lastPhaseId) && PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
					UIComponent algoContainerX = bean.getAlgoContainer();
					if (algoContainerX != null) {
						//algoContainerX.unsubscribeFromEvent(arg0, arg1);
						//appComponentListener = new AppComponentListener(bean);
						//algoContainerX.subscribeToEvent(PreRenderComponentEvent.class, appComponentListener);
					}						
				}*/
				if (event.getPhaseId().equals(PhaseId.RENDER_RESPONSE) && bean.elementSelectedId!=null && !bean.elementSelectedId.isEmpty()) {
					try {
						UIComponent algoContainer = bean.getAlgoContainer();
						if(algoContainer!=null){
							bean.elementSelected = ComponentFactory.doFindComponent(algoContainer, bean.elementSelectedId);
							bean.elementSelected.setTransient(true);
							bean.elementSelectedId = null;
						}
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}			
			} catch (Exception e) {
				e.printStackTrace();
			}
			lastPhaseId = event.getPhaseId().getOrdinal();
			//System.out.println("After Executing " + event.getPhaseId()+":"+(bean.getAlgoContainer()!=null?"not null":"null"));
		}

		@Override
		public PhaseId getPhaseId() {
			return PhaseId.ANY_PHASE;
		}
		
	}
	public PhaseListener getPhaseListener(){
		return new AppPhaseListener(this);
	}
	public void includePhaseListener() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if(ctx!=null){
			AppPhaseListener phaseListener = null;
			if(ctx.getViewRoot() != null){
				List<PhaseListener> phaseListeners = ctx.getViewRoot().getPhaseListeners();
				for (int i = 0; i < phaseListeners.size(); i++) {
					PhaseListener element = phaseListeners.get(i);
					if(element instanceof AppPhaseListener){
						phaseListener = (AppPhaseListener)element;
						break;
					}
				}
				if(phaseListener==null){
					//ctx.getViewRoot().removePhaseListener(phaseListener);	
					ctx.getViewRoot().addPhaseListener(new AppPhaseListener(this));				
				}else{
					phaseListener.setBean(this);
				}
			}
		}	
	}

	public void removePhaseListener(){
		FacesContext ctx = FacesContext.getCurrentInstance();
		if(ctx!=null){
			boolean exist = false;
			List<PhaseListener> phaseListeners = ctx.getViewRoot().getPhaseListeners();
			int phaseListener = 0;
			for (int i = 0; i < phaseListeners.size(); i++) {
				PhaseListener element = phaseListeners.get(i);
				if(element instanceof AppPhaseListener){
					exist = true;
					phaseListener = i;
					break;
				}
			}
			if(exist){
				ctx.getViewRoot().getPhaseListeners().remove(phaseListener);		
			}
			//ctx.getViewRoot().removePhaseListener(this.phaseListener);
		}			
	}		
	public void preRenderComponent(ComponentSystemEvent event){
		if(firstRender && requirement !=null){
			//updateContainerPage();
			//ComponentFactory.resetComponent(algoContainer);
			setAlgoContainer();		
			firstRender = false;
			
		}
	}
	class AppComponentListener implements ComponentSystemEventListener, Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4627738666198805424L;
		private AdmAlgoappBean bean;
		
		public AdmAlgoappBean getBean() {
			return bean;
		}

		public void setBean(AdmAlgoappBean bean) {
			this.bean = bean;
		}		
		
		public AppComponentListener(AdmAlgoappBean bean) {
			super();
			this.bean = bean;
		}		
		@Override
		public void processEvent(ComponentSystemEvent arg0) throws AbortProcessingException {
			try {
				
				bean.updateContainerPage();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("### COMPONENT RENDER!!!");							
		}
				
	}
	public void refresh() {
		  FacesContext context = FacesContext.getCurrentInstance();
		  Application application = context.getApplication();
		  ViewHandler viewHandler = application.getViewHandler();
		  UIViewRoot viewRoot = viewHandler.createView(context, context
		   .getViewRoot().getViewId());
		  context.setViewRoot(viewRoot);
		  //context.renderResponse(); //Optional
		}

}

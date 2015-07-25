/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algoboss.erp.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.xml.registry.infomodel.User;

import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import com.algoboss.erp.entity.DevEntityClass;
import com.algoboss.erp.entity.DevEntityClassConfig;
import com.algoboss.erp.entity.DevEntityPropertyDescriptor;
import com.algoboss.erp.entity.DevEntityPropertyDescriptorConfig;
import com.algoboss.erp.entity.DevPrototypeComponentBehaviors;
import com.algoboss.erp.entity.DevPrototypeComponentChildren;
import com.algoboss.erp.entity.DevPrototypeComponentFacets;
import com.algoboss.erp.entity.DevPrototypeComponentProperty;
import com.algoboss.erp.entity.DevReportFieldContainer;
import com.algoboss.erp.entity.DevReportFieldOptions;
import com.algoboss.erp.entity.DevReportFieldOptionsMap;
import com.algoboss.erp.entity.DevRequirement;
import com.algoboss.erp.face.AdmAlgodevBean;
import com.algoboss.erp.face.BaseBean;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.faces.facelets.compiler.XMLInstruction;
import com.sun.faces.facelets.el.ELText;

/**
 *
 * @author Agnaldo
 */
public class ComponentFactory {

	private UIComponent componentCreated;

	public UIComponent getComponentCreated() {
		return componentCreated;
	}

	public ComponentFactory(UIComponent comp, UIComponent elementPanel, String parentContainer, String parentEntity, Map<String, List<UIComponent>> elementsContainerMap, DevEntityClass entitySelected, DevRequirement requirement, AppType appType) {
		try {
			if (entitySelected != null) {
				UIComponent compClone = comp;// ComponentFactory.componentClone(comp,
												// false);
				Object styleClass = getProperty(compClone, "styleClass");
				// if (styleClass != null &&
				// String.valueOf(styleClass).contains("ui-integer")) {
				UIComponent compFinded = null;
				if ((compFinded = findComponentByClassName("javax.faces.component.html.HtmlSelectOneMenu", compClone)) != null) {
					if ((compFinded = findComponentByClassName("javax.faces.component.UISelectItems", compFinded)) != null) {
						this.doSelectItemsFactory(compFinded, parentContainer, entitySelected);
					}
				}
				if (styleClass != null && String.valueOf(styleClass).contains("data-list")) {
					if ((compFinded = findComponentByClassName("javax.faces.component.UIColumn", compClone)) != null) {
						this.doUIColumnFactory(compFinded, elementPanel, parentContainer, parentEntity, entitySelected, requirement, appType);
						elementsContainerMap.put("list", new ArrayList<UIComponent>(compClone.getParent().getChildren()));
					}
				}
				if (styleClass != null && String.valueOf(styleClass).contains("data-form")) {
					this.doUIFieldSetFactory(compClone, elementPanel, parentContainer, parentEntity, elementsContainerMap, entitySelected, requirement, appType);
					elementsContainerMap.put("form", new ArrayList<UIComponent>(compClone.getParent().getChildren()));
				}
				if (styleClass != null && String.valueOf(styleClass).contains("data-grid")) {
					this.doUIDataGridFactory(compClone, elementPanel, parentContainer, parentEntity, elementsContainerMap, entitySelected, requirement, appType);
				}
				if (styleClass != null && String.valueOf(styleClass).contains("data-carousel")) {
					this.doUICarouselFactory(compClone, elementPanel, parentContainer, parentEntity, elementsContainerMap, entitySelected, requirement, appType);
				}
				/*
				 * if
				 * (Class.forName("javax.faces.component.html.HtmlSelectOneMenu"
				 * ).isAssignableFrom(compClone.getClass())) { for (UIComponent
				 * child : compClone.getChildren()) { if
				 * (Class.forName("javax.faces.component.UISelectItems"
				 * ).isAssignableFrom(child.getClass())) {
				 * this.doSelectItemsFactory(child, parentContainer,
				 * entitySelected, propertySelectedList, appType); break; } } }
				 * if (styleClass != null &&
				 * String.valueOf(styleClass).contains("data-list")) { for
				 * (UIComponent child : compClone.getChildren()) { if
				 * (Class.forName
				 * ("javax.faces.component.UIColumn").isAssignableFrom
				 * (child.getClass())) { this.doUIColumnFactory(child,
				 * elementPanel, parentContainer, parentEntity, entitySelected,
				 * propertySelectedList, appType); break; } } } if (styleClass
				 * != null && String.valueOf(styleClass).contains("data-form"))
				 * { this.doUIFieldSetFactory(compClone, elementPanel,
				 * parentContainer, parentEntity, elementsContainerMap,
				 * entitySelected, propertySelectedList, appType); } if
				 * (styleClass != null &&
				 * String.valueOf(styleClass).contains("data-grid")) { for
				 * (UIComponent child : compClone.getChildren()) { if
				 * (Class.forName
				 * ("javax.faces.component.UIColumn").isAssignableFrom
				 * (child.getClass())) { //this.doUIColumnFactory(child,
				 * elementPanel, parentContainer, parentEntity, entitySelected,
				 * propertySelectedList, appType); break; } }
				 * this.doUIDataGridFactory(compClone, elementPanel,
				 * parentContainer, parentEntity, elementsContainerMap,
				 * entitySelected, propertySelectedList, appType); } if
				 * (styleClass != null &&
				 * String.valueOf(styleClass).contains("data-carousel")) {
				 * this.doUICarouselFactory(compClone, elementPanel,
				 * parentContainer, parentEntity, elementsContainerMap,
				 * entitySelected, propertySelectedList, appType); }
				 */
				componentCreated = compClone;
			}
		} catch (Exception ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private void setValue(UIComponent comp, String field, String strExpression) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ValueExpression o = facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), strExpression, String.class);
		comp.setValueExpression(field, (ValueExpression) o);
	}

	private void doSelectItemsFactory(UIComponent comp, String field, DevEntityClass entitySelected) {
		String propertyArrayStr = "";
		/*
		for (int i = 0; false && i < propertySelectedList.size(); i++) {
			DevEntityPropertyDescriptor devEntityPropertyDescriptor = propertySelectedList.get(i);
			if (devEntityPropertyDescriptor.isPropertyIdentifier()) {
				if (!propertyArrayStr.isEmpty()) {
					propertyArrayStr += ";";
				}
				propertyArrayStr += devEntityPropertyDescriptor.getPropertyName();
			}
		}*/
		// String str = "#{app.beanMap('" + entitySelected.getName() + "','" +
		// propertyArrayStr + "')}";
		String str = "#{app.beanMap('" + entitySelected.getName() + "')}";
		setValue(comp, field, str);
	}

	private void doUIColumnFactory(UIComponent compClone, UIComponent elementPanel, String parentContainer, String parentEntity, DevEntityClass entitySelected, DevRequirement requirement, AppType appType) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			boolean hasEdit = appType == AppType.LIST_EDIT_FORM;
			UIComponent cloned = null;
			UIComponent parent = compClone.getParent();
			UIComponent rowEditor = compClone.getChildren().remove(1);
			parentContainer = (parentContainer != null && !parentContainer.isEmpty()) ? parentContainer : "list";
			List<DevReportFieldOptions> fieldOptionsList = LayoutFieldsFormat.getVisibleFields(requirement, parentContainer, entitySelected);	
			for (int i = 0; i < fieldOptionsList.size(); i++) {
				DevReportFieldOptions fieldOptions = fieldOptionsList.get(i);
				DevEntityPropertyDescriptor devEntityPropertyDescriptor = fieldOptionsList.get(i).getEntityPropertyDescriptor();
				if (i == 0) {
					parent.getChildren().clear();
				}
				cloned = ComponentFactory.componentClone(compClone, false);
				parent.getChildren().add(cloned);
				
				boolean center = false;
				Map<String, Object> mapParam = new HashMap<String, Object>();
				// mapParam.put("filterMatchMode", "");
				// mapParam.put("filterBy", "");
				// mapParam.put("filterStyle", "");
				Map<String, String> configMap = new HashMap<String, String>();
				if (devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList() != null) {
					for (DevEntityPropertyDescriptorConfig devEntityPropertyDescriptorConfig : devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList()) {
						if (!String.valueOf(devEntityPropertyDescriptorConfig.getConfigName()).isEmpty() && !String.valueOf(devEntityPropertyDescriptorConfig.getConfigName()).equals("null")) {
							configMap.put(devEntityPropertyDescriptorConfig.getConfigName(), devEntityPropertyDescriptorConfig.getConfigValue());
						}
					}
				}

				try {
					UIComponent childAux = cloned.getChildren().get(0).getFacet("output");

					for (UIComponent child : childAux.getChildren()) {
						if (Class.forName("javax.faces.component.html.HtmlOutputText").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("INTEGER")) {
								mapParam.put("sortBy", "getProp('" + devEntityPropertyDescriptor.getPropertyName() + "')");
								// mapParam.put("filterBy", "#{app.$('" +
								// entitySelected.getName() + "." +
								// devEntityPropertyDescriptor.getPropertyName()
								// + "',item).val}");
								// mapParam.put("filterMatchMode", "contains");
								// mapParam.put("filterStyle", "display:none;");
								setValue(child, "value", "#{(app.$g('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item))}");
							} else if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("FLOAT")) {
								mapParam.put("sortBy", "getProp('" + devEntityPropertyDescriptor.getPropertyName() + "')");
								// mapParam.put("filterBy", "#{app.$('" +
								// entitySelected.getName() + "." +
								// devEntityPropertyDescriptor.getPropertyName()
								// + "',item).val}");
								// mapParam.put("filterMatchMode", "contains");
								// mapParam.put("filterStyle", "display:none;");
								setValue(child, "value", "#{(app.$g('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item))}");
							} else if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING")) {
								mapParam.put("sortBy", "getProp('" + devEntityPropertyDescriptor.getPropertyName() + "')");
								mapParam.put("filterBy", "getProp('" + devEntityPropertyDescriptor.getPropertyName() + "')");
								mapParam.put("filterMatchMode", "contains");
								mapParam.put("filterStyle", "display:none;");
								setValue(child, "value", "#{(app.$g('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item))}");
							} else if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("DATE")) {
								String pattern = BaseBean.getBundle("calendar.pattern", "msg");
								if (configMap.containsKey("pattern")) {
									pattern = configMap.get("pattern");
								}
								mapParam.put("sortBy", "getProp('" + devEntityPropertyDescriptor.getPropertyName() + "')");
								mapParam.put("filterBy", "$date('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "','" + pattern + "')");
								mapParam.put("filterMatchMode", "contains");
								mapParam.put("filterStyle", "display:none;");
								setValue(child, "value", "#{app.$date('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "','" + pattern + "',item)}");
							} else if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("BOOLEAN")) {
								mapParam.put("filterBy", "getProp('" + devEntityPropertyDescriptor.getPropertyName() + "')");
								mapParam.put("sortBy", devEntityPropertyDescriptor.getPropertyName());
								mapParam.put("filterMatchMode", "contains");
								mapParam.put("filterStyle", "display:none;");
								Map<String, Object> mapParam2 = new HashMap<String, Object>();
								mapParam2.put("value", "#{(app.$g('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item))}");
								mapParam2.put("styleClass", "#{(app.$g('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item)?' ui-icon ui-icon-check ':' ui-icon ui-icon-cancel ')}");
								mapParam2.put("style", "display:inline-block;");
								ComponentFactory.updateElementProperties(f2, child, mapParam2, null);
								center = true;
							} else if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCLASS")) {
								mapParam.put("filterMatchMode", "contains");
								mapParam.put("filterStyle", "display:none;");
								mapParam.put("filterBy", "getProp('" + devEntityPropertyDescriptor.getPropertyName() + "')");
								mapParam.put("sortBy", "getProp('" + devEntityPropertyDescriptor.getPropertyName() + "')");
								setValue(child, "value", "#{(app.$g('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item))}");
							} else if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")) {
								mapParam.put("filterMatchMode", "contains");
								mapParam.put("filterStyle", "display:none;");
								mapParam.put("filterBy", "getProp('" + devEntityPropertyDescriptor.getPropertyName() + "')");
								mapParam.put("sortBy", "getProp('" + devEntityPropertyDescriptor.getPropertyName() + "')");
								setValue(child, "value", "#{(app.count('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item))}");
								center = true;
							} else {
								setValue(child, "value", "#{(app.$g('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item))}");
							}
							if(hasEdit){
								cloned.getChildren().get(0).getFacets().put("input", createInputComponent(elementPanel, entitySelected, devEntityPropertyDescriptor, configMap, parentEntity, appType, parentContainer, fieldOptions));
							}else{
								cloned.getChildren().set(0, child);
							}
						}
					}
				} catch (ClassNotFoundException ex) {
					Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
				}
				Object label2 = LayoutFieldsFormat.loadOpt(fieldOptions, "label").getOptionsValue();
				if(label2!=null){
					mapParam.put("header", label2.toString());
					mapParam.put("headerText", label2.toString());
				}else{
					mapParam.put("header", devEntityPropertyDescriptor.getPropertyLabel());
					mapParam.put("headerText", devEntityPropertyDescriptor.getPropertyLabel());
				}
				generateComponentStyleClassName(mapParam, "list", entitySelected, devEntityPropertyDescriptor, "","");
				if (center) {
					mapParam.put("style", "text-align:center;");
				}
				Object style = LayoutFieldsFormat.loadOpt(fieldOptions, "style").getOptionsValue();
				if(style!=null){
					mapParam.put("style", Objects.toString(mapParam.get("style"), "").concat(style.toString()));
				}					
				mapParam.put("style", Objects.toString(mapParam.get("style"), "").concat("overflow: hidden;white-space:nowrap;"));
				ComponentFactory.updateElementProperties(f2, cloned, mapParam, null);
				// propertyArrayStr +=
				// devEntityPropertyDescriptor.getPropertyName();
			}
			try {
				cloned = ComponentFactory.componentClone(compClone, false);
				parent.getChildren().add(cloned);
				String styleAction = getProperty(cloned, "styleClass")+" c_"+parentContainer;
				Map<String, Object> mapParam = new HashMap<String, Object>();
				mapParam.put("headerText", "#{msg['action']}");
				mapParam.put("exportable", "false");
				mapParam.put("styleClass", styleAction+" c_action_col");
				mapParam.put("style", "text-align:center;width:" + (hasEdit ? "150px;" : "100px;"));
				// mapParam.put("filterMatchMode", "");
				// mapParam.put("filterBy", "");
				// mapParam.put("sortBy", "");
				ComponentFactory.updateElementProperties(f2, cloned, mapParam, null);
				UIComponent link1 = null;
				UIComponent link2 = null;
				UIComponent label = null;
				UIComponent childAux = cloned.getChildren().get(0).getFacet("output");
				for (UIComponent child : childAux.getChildren()) {
					Map<String, Object> mapParam2 = new HashMap<String, Object>();
					if (Class.forName("javax.faces.component.html.HtmlCommandLink").isAssignableFrom(child.getClass())) {
						String style = getProperty(child, "styleClass")+" c_"+parentContainer;
						String editAction = "#{app.doEdit(item)}";
						String removeAction = "#{app.doRemove(item)}";
						if (parentContainer.contains("-")) {
							editAction = "#{app.doEditChild(item,'" + parentContainerName(parentContainer) + "-form-" + entitySelected.getName() + "')}";
							removeAction = "#{app.doRemoveChild(item)}";
						}
						link1 = ComponentFactory.componentClone(child, false);
						mapParam2.put("actionListener", editAction);
						mapParam2.put("value", hasEdit ? "#{msg['open']}" : "#{msg['edit']}");
						mapParam2.put("styleClass", style+" c_"+(hasEdit ? "open" : "edit")+"_link");
						ComponentFactory.updateElementProperties(f2, link1, mapParam2, null);

						link2 = ComponentFactory.componentClone(child, false);

						mapParam2.clear();
						mapParam2.put("actionListener", removeAction);
						mapParam2.put("value", "#{msg['exclude']}");
						mapParam2.put("styleClass", style+" c_exclude_link");
						mapParam2.put("onclick", "if(!confirm('Tem certeza que deseja excluir esse registro?')){return false;}");
						ComponentFactory.updateElementProperties(f2, link2, mapParam2, null);
					}
					if (Class.forName("javax.faces.component.html.HtmlOutputText").isAssignableFrom(child.getClass())) {
						label = child;
						mapParam2.clear();
						mapParam2.put("value", " | ");
						ComponentFactory.updateElementProperties(f2, label, mapParam2, null);
					}
				}
				cloned.getChildren().clear();
				if (hasEdit) {
					cloned.getChildren().add(rowEditor);
					// mapParam.put("style", "float:left;");
					// ComponentFactory.updateElementProperties(f2, link1,
					// mapParam2, null);
				}
				cloned.getChildren().add(link1);
				cloned.getChildren().add(label);
				cloned.getChildren().add(link2);

			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
			}
			String node = "";
			if (parentEntity.isEmpty()) {
				node = entitySelected.getName();
			}else{
				node = parentEntity;
			}
			String str = "#{app.beanListMap.get('" + String.valueOf(node).toLowerCase() + "')}";
			String str3 = "#{app.beanListFilteredMap.get('" + String.valueOf(node).toLowerCase() + "').beanList}";
			String str2 = "#{app.beanSel}";
			Map<String, Object> mapParam = new HashMap<String, Object>();
			mapParam.put("value", str);
			mapParam.put("rowKey", "#{item.hashCode()}");
			mapParam.put("editable", "true");
			mapParam.put("filteredValue", str3);
			// mapParam.put("selection", str2);
			// mapParam.put("selectionMode", "single");
			mapParam.put("paginator", "true");
			mapParam.put("rows", "10");
			mapParam.put("sortMode", "multiple");
			mapParam.put("tableStyle", "width:990px;table-layout: fixed; overflow: hidden;");
			mapParam.put("style", "width:990px;table-layout: fixed; overflow: hidden;");
			mapParam.put("scrollable", "false");
			mapParam.put("scrollWidth", "1010");
			mapParam.put("scrollHeight", "210");
			mapParam.put("widgetVar", "c_" + entitySelected.getName() + "DataTable");

			componentCreated = cloned.getParent();
			ComponentFactory.updateElementProperties(f2, componentCreated, mapParam, null);
			if (componentCreated.getFacets().containsKey("footer")) {
				UIComponent export = componentCreated.getFacets().get("footer");
				if (export instanceof UICommand) {
					mapParam.clear();
					mapParam.put("value", "#{msg['export']}");
					mapParam.put("action", "#{app.exportDataTable()}");
					ComponentFactory.updateElementProperties(f2, export, mapParam, null);
				}
			}
			if (componentCreated.getFacets().containsKey("header")) {
				UIComponent labelCloned = null;
				UIComponent label2Cloned = null;
				UIComponent inputCloned = null;
				UIComponent panelCloned = null;
				UIComponent panelCloned2 = null;

				panelCloned = ComponentFactory.componentClone(findComponentByStyleClass("ui-panel", elementPanel), false);
				mapParam.clear();
				mapParam.put("header", "");
				ComponentFactory.updateElementProperties(f2, panelCloned, mapParam, null);
				for (UIComponent child : elementPanel.getChildren()) {

					if (panelCloned2 == null && child instanceof javax.faces.component.html.HtmlOutputLabel) {
						// panelCloned2 = ComponentFactory.componentClone(
						// child, context);
						mapParam.clear();
						// mapParam.put("header", "");
						// ComponentFactory.updateElementProperties(f2,
						// panelCloned2, mapParam, null);
					}
					if (labelCloned == null && child instanceof javax.faces.component.html.HtmlOutputLabel) {
						labelCloned = ComponentFactory.componentClone(child, false);
						mapParam.clear();
						mapParam.put("value", "#{msg['listing']}: " + entitySelected.getLabel());
						ComponentFactory.updateElementProperties(f2, labelCloned, mapParam, null);
					}
					if (label2Cloned == null && child instanceof javax.faces.component.html.HtmlOutputLabel) {
						label2Cloned = ComponentFactory.componentClone(child, false);
						mapParam.clear();
						mapParam.put("value", "#{msg['search']}: ");
						mapParam.put("style", "float:right;top:-5px;position: relative;");
						ComponentFactory.updateElementProperties(f2, label2Cloned, mapParam, null);
					}
					if (inputCloned == null && child instanceof javax.faces.component.html.HtmlInputText) {
						inputCloned = ComponentFactory.componentClone(child, false);
						mapParam.clear();
						mapParam.put("onkeyup", "c_" + entitySelected.getName() + "DataTable" + ".filter()");
						// mapParam.put("style",
						// "float:right;position:relative;top:-20% !important;");
						ComponentFactory.updateElementProperties(f2, inputCloned, mapParam, null);
						inputCloned.setId("globalFilter");
					}
				}

				panelCloned.getChildren().add(labelCloned);
				panelCloned.getChildren().add(label2Cloned);
				label2Cloned.getChildren().add(inputCloned);

				componentCreated.getFacets().put("header", panelCloned);
			}
			doUICommandButtonListFactory("c_" + entitySelected.getName() + "DataTable", componentCreated.getParent(), elementPanel, entitySelected, parentContainer, appType);						
			updateComponentContainerUI(requirement, parentContainer, entitySelected, parent, elementPanel);
		} catch (NoSuchFieldException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static void updateComponentContainerUI(DevRequirement requirement, String parentContainer, DevEntityClass entitySelected, UIComponent parent, UIComponent elementPanel){
		Map<String, Object> mapParam = new HashMap<String, Object>();
		javax.faces.component.html.HtmlOutputText outtext = new HtmlOutputText();
		for (DevReportFieldOptions devReportFieldOptions : LayoutFieldsFormat.getContainerOptions(requirement.getFieldContainerList(), parentContainer, entitySelected)) {
			if(devReportFieldOptions.getEntityPropertyDescriptor()==null){
				Object obj = LayoutFieldsFormat.loadOpt(devReportFieldOptions, "onload").getOptionsValue();
				if(obj!=null){
					outtext.setValue("<script>"+obj+"</script>");
					outtext.setEscape(false);
					parent.getParent().getChildren().add(outtext);
					//break;
				}	
				
				Object create = LayoutFieldsFormat.loadOpt(devReportFieldOptions, "create").getOptionsValue();
				if(create!=null){
					String styleClassName = devReportFieldOptions.getName();
					String[] styleClassArray = create.toString().split(";");
					String parentStyleClass = styleClassArray[0];
					String templateStyleClass = styleClassArray[1];
					UIComponent c_comp_to_clone = null;
					if(!templateStyleClass.startsWith(".")){
						try {						
							c_comp_to_clone = (UIComponent)Class.forName(templateStyleClass).newInstance();
						} catch (ClassNotFoundException e) {
						} catch (InstantiationException e) {
						} catch (IllegalAccessException e) {
						}
					}
					if(c_comp_to_clone == null){
						c_comp_to_clone = findComponentByStyleClass(templateStyleClass,elementPanel);
					}
					UIComponent c_comp_parent = parentStyleClass.isEmpty()?parent.getParent():findComponentByStyleClass(parentStyleClass,parent.getParent());
					UIComponent c_comp_new = ComponentFactory.componentClone(c_comp_to_clone, false);
					mapParam.clear();
					
					String style = Objects.toString(getProperty(c_comp_to_clone, "styleClass"),"")+" c_"+parentContainer;
					mapParam.put("styleClass", style+" "+styleClassName);					
					ComponentFactory.updateElementProperties(c_comp_new, mapParam);
					c_comp_parent.getChildren().add(c_comp_new);
				}					
	
				UIComponent c_comp_to_update = null;
				if(devReportFieldOptions.getName().contains(";")){
					String[] styleClassArray = create.toString().split(";");
					String parentStyleClass = styleClassArray[0];
					String clazzName = styleClassArray[1];
					UIComponent c_comp_parent = parentStyleClass.isEmpty()?parent.getParent():findComponentByStyleClass(parentStyleClass,parent.getParent());
					List<UIComponent> children = c_comp_parent.getChildren();
					for (UIComponent uiComponent : children) {
						if(uiComponent.getClass().getCanonicalName().equals(clazzName)){
							c_comp_to_update = uiComponent;
						}
					}
				}else{
					c_comp_to_update = findComponentByStyleClass(devReportFieldOptions.getName(),parent.getParent());
				}
				if(c_comp_to_update!=null){
					mapParam.clear();
					for (DevReportFieldOptionsMap fieldOptionsMap : devReportFieldOptions.getFieldOptionsMapList()) {
						//Object value = LayoutFieldsFormat.loadOpt(devReportFieldOptions, "value").getOptionsValue();
						//Object actionListener = LayoutFieldsFormat.loadOpt(devReportFieldOptions, "actionListener").getOptionsValue();
						Object optionsValue = fieldOptionsMap.getOptionsValue();
						String optionsName = fieldOptionsMap.getOptionsName();
						if(optionsValue!=null){
							mapParam.put(optionsName, optionsValue);						
						}
					}
					ComponentFactory.updateElementProperties(c_comp_to_update, mapParam);
				}		
			}
		}			
	}
	
	private void doUICommandButtonListFactory(String widgetList, UIComponent container, UIComponent elementPanel, DevEntityClass entitySelected, String parentContainer, AppType appType) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			UIComponent button = null;
			UIComponent buttonCloned = null;
			UIComponent buttonCloned2 = null;
			Map<String, Object> mapParam = new HashMap<String, Object>();
			String stylePanel = getProperty(container, "styleClass")+" c_"+parentContainer;
			mapParam.put("header", "");
			mapParam.put("styleClass", stylePanel+" c_button_panel");
			ComponentFactory.updateElementProperties(f2, container, mapParam, null);
			//c_"parentContainer" c_include
			button = findComponentByClassName("javax.faces.component.html.HtmlCommandButton", elementPanel);
			buttonCloned = ComponentFactory.componentClone(button, false);
			String style = getProperty(buttonCloned, "styleClass")+" c_"+parentContainer;
			String action = "#{app.doForm()}";
			if (parentContainer.contains("-") && entitySelected != null) {
				action = "#{app.doForm('" + parentContainerName(parentContainer) + "-form-" + entitySelected.getName() + "')}";
				buttonCloned2 = ComponentFactory.componentClone(button, false);
			}
			
			if (buttonCloned != null) {
				mapParam.put("value", "#{msg['include']}");
				mapParam.put("action", action);
				mapParam.put("update", "@form");
				mapParam.put("immediate", "true");
				mapParam.put("styleClass", style+" c_include_button");
				ComponentFactory.updateElementProperties(f2, buttonCloned, mapParam, null);
				container.getChildren().add(0, buttonCloned);
			}
			if (buttonCloned2 != null) {
				mapParam.clear();
				mapParam.put("value", "#{msg['back']}");
				mapParam.put("action", "#{app.doForm('" + parentContainerName(parentContainer) + "')}");
				mapParam.put("update", "@form");
				mapParam.put("immediate", "true");
				mapParam.put("styleClass", style+" c_back_button");
				ComponentFactory.updateElementProperties(f2, buttonCloned2, mapParam, null);
				container.getChildren().add(1, buttonCloned2);
			}

		} catch (NoSuchFieldException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void doUICommandButtonFormFactory(UIComponent container, UIComponent elementPanel, DevEntityClass entitySelected, String parentContainer, String parentEntity, AppType appType) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			UIComponent button = null;
			UIComponent buttonCloned = null;
			UIComponent panelCloned = null;
			boolean hasList = appType.hasList();
			String node = "";
			if (parentEntity.isEmpty()) {
				node = entitySelected.getName();
			}else{
				node = parentEntity;
			}
			Map<String, Object> mapParam = new HashMap<String, Object>();
			Map<String, Object> mapParamBasic = new HashMap<String, Object>();
			panelCloned = ComponentFactory.componentClone(findComponentByStyleClass("ui-panel", elementPanel), false);
			String stylePanel = getProperty(panelCloned, "styleClass")+" c_"+parentContainer;
			mapParam.put("header", "");
			mapParam.put("styleClass", stylePanel+" c_button_panel");
			ComponentFactory.updateElementProperties(f2, panelCloned, mapParam, null);

			button = findComponentByClassName("javax.faces.component.html.HtmlCommandButton", elementPanel);
			String style = getProperty(button, "styleClass")+" c_"+parentContainer;
			String save = "#{app.doSave()}";
			String saveAndBack = "#{app.doSaveAndList()}";
			String back = "#{app.doList()}";
			String copy = "#{app.doCopy()}";
			if (parentContainer.contains("-")) {
				save = "#{app.doSaveChild()}";
				if (hasList) {
					saveAndBack = "#{app.doSaveAndListChild('" + parentContainerName(parentContainer) + "-list-" + entitySelected.getName() + "','" + node + "')}";
					back = "#{app.doList('" + parentContainerName(parentContainer) + "-list-" + entitySelected.getName() + "','" + node + "')}";
				} else {
					saveAndBack = "#{app.doSaveAndListChild('" + parentContainerName(parentContainer) + "','" + node + "')}";
					back = "#{app.doList('" + parentContainerName(parentContainer) + "','" + node + "')}";
				}
				copy = "#{app.doCopyChild()}";
			}
			mapParamBasic.put("update", "@form");
			mapParamBasic.put("immediate", "false");
			if (hasList) {
				buttonCloned = ComponentFactory.componentClone(button, false);
				mapParam.putAll(mapParamBasic);
				mapParam.put("value", "#{msg['save']}");
				mapParam.put("action", save);
				mapParam.put("styleClass", style+" c_save_button");
				ComponentFactory.updateElementProperties(f2, buttonCloned, mapParam, null);
				panelCloned.getChildren().add(buttonCloned);
			}
			buttonCloned = ComponentFactory.componentClone(button, false);
			mapParam.putAll(mapParamBasic);
			mapParam.put("value", "#{msg['saveAndBack']}");
			mapParam.put("action", saveAndBack);
			mapParam.put("styleClass", style+" c_save_and_back_button");
			ComponentFactory.updateElementProperties(f2, buttonCloned, mapParam, null);
			panelCloned.getChildren().add(buttonCloned);

			if (hasList) {
				buttonCloned = ComponentFactory.componentClone(button, false);
				mapParam.putAll(mapParamBasic);
				mapParam.put("value", "#{msg['copy']}");
				mapParam.put("action", copy);
				mapParam.put("styleClass", style+" c_copy_button");
				ComponentFactory.updateElementProperties(f2, buttonCloned, mapParam, null);
				panelCloned.getChildren().add(buttonCloned);
			}

			buttonCloned = ComponentFactory.componentClone(button, false);
			mapParam.putAll(mapParamBasic);
			mapParam.put("value", "#{msg['back']}");
			mapParam.put("action", back);
			mapParam.put("immediate", "true");
			mapParam.put("styleClass", style+" c_back_button");
			ComponentFactory.updateElementProperties(f2, buttonCloned, mapParam, null);
			panelCloned.getChildren().add(buttonCloned);
			container.getChildren().add(0, panelCloned);

		} catch (NoSuchFieldException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private UIComponent createInputComponent(UIComponent elementPanel, DevEntityClass entitySelected, DevEntityPropertyDescriptor devEntityPropertyDescriptor, Map<String, String> configMap, String parentEntity, AppType appType, String parentContainer, DevReportFieldOptions fieldOptions) {
		UIComponent input = null;
		UIComponent inputCloned = null;
		UIComponent inputWrapper = null;
		String item = "";
		boolean editableList = appType == AppType.LIST_EDIT_FORM && parentContainer.contains("list");
		if (editableList) {
			item = ",item";
		} else {
			item = !parentEntity.isEmpty() ? ",app.childBean" : "";
		}
		Object styleClass = null;
		Map<String, Object> mapParam = new HashMap<String, Object>();
		if (devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList() != null) {
			for (DevEntityPropertyDescriptorConfig devEntityPropertyDescriptorConfig : devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList()) {
				if (!String.valueOf(devEntityPropertyDescriptorConfig.getConfigName()).isEmpty() && !String.valueOf(devEntityPropertyDescriptorConfig.getConfigName()).equals("null")) {
					configMap.put(devEntityPropertyDescriptorConfig.getConfigName(), devEntityPropertyDescriptorConfig.getConfigValue());
				}
			}
		}
		try {
			boolean isList = false;
			boolean isObjectList = false;
			boolean isMask = false;
			boolean isShort = false;
			boolean isLong = false;
			if (configMap.containsKey("list") && !configMap.get("list").isEmpty()) {
				isList = true;
			} else if (configMap.containsKey("objectList") && !configMap.get("objectList").isEmpty()) {
				isObjectList = true;
			} else if (configMap.containsKey("mask") && configMap.get("mask") != null && !configMap.get("mask").isEmpty()) {
				isMask = true;
			} else if ((!configMap.containsKey("length") || configMap.get("length") == null || configMap.get("length").equals("short"))) {
				isShort = true;
			} else if (configMap.containsKey("length") && configMap.get("length").equals("long")) {
				isLong = true;
			}			
			for (UIComponent child : elementPanel.getChildren()) {
				styleClass = getProperty(child, "styleClass");
				if (styleClass != null && String.valueOf(styleClass).contains("ui-integer")) {
					if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("INTEGER")) {
						input = child;
						break;
					}
				}
				if (Class.forName("javax.faces.component.html.HtmlInputText").isAssignableFrom(child.getClass())) {
					if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("FLOAT")) {
						input = child;
						if (!configMap.containsKey("precision") || configMap.get("precision") == null) {
							configMap.put("precision", "2");
						}
						break;
					}
				}
				if (Class.forName("javax.faces.component.html.HtmlSelectOneMenu").isAssignableFrom(child.getClass())) {
					if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && (isList || isObjectList)) {
						input = child;
						break;
					}
				}
				if (styleClass != null && String.valueOf(styleClass).contains("ui-mask")) {
					if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && isMask) {
						input = child;
						break;
					}
				}
				if (Class.forName("javax.faces.component.html.HtmlInputText").isAssignableFrom(child.getClass())) {
					if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && isShort) {
						input = child;
						break;
					}
				}
				if (styleClass != null && String.valueOf(styleClass).contains("ui-textArea")) {
					if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && isLong) {
						input = child;
						break;
					}
				}
				if (Class.forName("javax.faces.component.html.HtmlSelectBooleanCheckbox").isAssignableFrom(child.getClass())) {
					if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("BOOLEAN")) {
						input = child;
						break;
					}
				}
				if (Class.forName("javax.faces.component.html.HtmlCommandButton").isAssignableFrom(child.getClass())) {
					if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("FILE")) {
						input = child;
						// mapParam.put("update", "@form");
						mapParam.put("immediate", "true");
						String propValObj = "app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "'" + item + ")";
						String str = "#{(" + propValObj+".val" + "==null?msg['select']:" + propValObj+".val" + ")}";
						mapParam.put("value", str);
						mapParam.put("action", "#{app.prepareUpload(" + propValObj + ")}");
						mapParam.put("oncomplete", "(handleFileUploadDlg(xhr, status, args))");
						// mapParam.put("onclick",
						// "function () {uploadFileWv.show();return true;}");
						// mapParam.put("onclick",
						// "javascript:(function(){uploadFileWv.show();return false;})");
						ComponentFactory.updateElementProperties(input, mapParam);
						break;
					}
				}
				if (Class.forName("javax.faces.component.html.HtmlSelectOneMenu").isAssignableFrom(child.getClass())) {
					if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCLASS") && devEntityPropertyDescriptor.getPropertyClass() != null) {
						input = child;
						break;
					}
				}
				if (Class.forName("javax.faces.component.html.HtmlCommandButton").isAssignableFrom(child.getClass())) {
					if ((devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ONEINTERNALOBJECT") || devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")) && devEntityPropertyDescriptor.getPropertyClass() != null) {
						input = ComponentFactory.componentClone(child, false);
						break;
					}
				}
				/*
				 * if (false &&
				 * Class.forName("javax.faces.component.UIData").isAssignableFrom
				 * (child.getClass())) { if
				 * (devEntityPropertyDescriptor.getPropertyType
				 * ().equalsIgnoreCase("ENTITYCHILDREN") &&
				 * devEntityPropertyDescriptor.getPropertyClass() != null) { if
				 * (
				 * Class.forName("javax.faces.component.UIPanel").isAssignableFrom
				 * (child.getChildren().get(0).getClass())) { if (containerChild
				 * == null) { containerChild =
				 * ComponentFactory.componentClone(child, false); tab =
				 * containerChild.getChildren().get(0);
				 * comp.getParent().getParent
				 * ().getChildren().add(containerChild); } else { tab =
				 * ComponentFactory
				 * .componentClone(containerChild.getChildren().get(0), false);
				 * containerChild.getChildren().add(tab); } break; } } }
				 */
				if (styleClass != null && String.valueOf(styleClass).contains("ui-calendar")) {
					if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("DATE")) {
						input = child;
						break;
					}
				}
			}
			inputCloned = ComponentFactory.componentClone(input, false);
			mapParam.clear();
			boolean isEntList = false;
			String entClassName = "";
			if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && isList) {
				UIComponent childAux = findComponentByClassName("javax.faces.component.UISelectItem", inputCloned);
				UIComponent childAux2 = findComponentByClassName("javax.faces.component.UISelectItems", inputCloned);
				inputCloned.getChildren().remove(childAux2);
				String[] strList = AlgodevUtil.formatDescription(configMap.get("list")).split(",");
				for (int j = 0; j < strList.length; j++) {
					String string = strList[j];
					UIComponent childCloned = ComponentFactory.componentClone(childAux, false);
					mapParam.put("itemValue", string);
					mapParam.put("itemLabel", string);
					ComponentFactory.updateElementProperties(childCloned, mapParam);
					inputCloned.getChildren().add(childCloned);
				}
				mapParam.clear();
			} else if (isObjectList) {
				UIComponent compFinded = null;
				String[] objectList = AlgodevUtil.formatDescription(configMap.get("objectList")).split(",");
				String className = objectList[0];
				if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING")) {
					String value = objectList[1];
					String label[] = Arrays.copyOfRange(objectList, 2, objectList.length);
					String labelStr = "";
					for (int j = 0; j < label.length; j++) {
						if(j>0){
							labelStr+=";";
						}
						labelStr+=label[j];
					}				
					if ((compFinded = findComponentByClassName("javax.faces.component.UISelectItems", inputCloned)) != null) {
						//this.doSelectItemsFactory(compFinded, "value", new DevEntityClass(className));
						String str = "#{app.beanMap('" + className + "','"+labelStr+"','"+value+"')}";
						//String str = "#{app.beanMap('" + className + "','"+labelStr+"')}";
						setValue(compFinded, "value", str);
					}
					//mapParam.put("var", "item");
					////mapParam.put("itemValue", "#{item.getProp('"+value+"')}");
					//mapParam.put("itemLabel", "#{item.getProp('"+label[0]+"')}");
					//String[] label = configMap.get("list").split("\n");

					ComponentFactory.updateElementProperties(compFinded, mapParam);
					mapParam.clear();
					isEntList = true;
					entClassName = className;
				} else {
					if (Class.forName("javax.faces.component.html.HtmlSelectOneMenu").isAssignableFrom(input.getClass())) {
						DevEntityClass ent = devEntityPropertyDescriptor.getPropertyClass();
						// new ComponentFactory(inputCloned, elementPanel, "value",
						// entitySelected.getName(), elementsContainerMap, ent,
						// ent.getEntityPropertyDescriptorList(), appType);
						if ((compFinded = findComponentByClassName("javax.faces.component.UISelectItems", inputCloned)) != null) {
							String str = "";
							if(!className.startsWith("#{app.beanMap(")){
								str = "#{app.beanMap('" + className + "')}";								
							}else{
								str = className;
							}
							//String str = "#{app.beanMap('" + className + "','"+labelStr+"')}";
							setValue(compFinded, "value", str);
							//this.doSelectItemsFactory(compFinded, "value", ent);
						}
						isEntList = true;
						entClassName = ent.getName();
						// mapParam.put("converter", "EntityObjectConverter");
					}
				}
				
			}
			if(isEntList){
				if (!editableList) {
					UIComponent labelSelect = ComponentFactory.componentClone(findComponentByStyleClass("ui-textOutput", elementPanel), false);
					// mapParam.put("style", "vertical-align:middle;");
					// ComponentFactory.updateElementProperties(labelSelect,
					// mapParam);
					UIComponent openCrud = ComponentFactory.componentClone(findComponentByStyleClass("ui-commandButton", elementPanel), false);
					mapParam.put("icon", "ui-icon-newwin");
					mapParam.put("style", "position:absolute;margin-left:20px;#{!gerLoginBean.appEntityMap.containsKey('" + entClassName + "')?'display:none':''};");
					mapParam.put("value", "");
					mapParam.put("update", ":basePanel");
					mapParam.put("immediate", "true");
					mapParam.put("action", "#{app.indexBeanNewWin(gerLoginBean.appEntityMap.get('" + entClassName + "'))}");
					mapParam.put("disabled", "#{!gerLoginBean.appEntityMap.containsKey('" + entClassName + "')}");
					mapParam.put("readonly", "#{!gerLoginBean.appEntityMap.containsKey('" + entClassName + "')}");
					ComponentFactory.updateElementProperties(openCrud, mapParam);
					labelSelect.getChildren().add(inputCloned);
					labelSelect.getChildren().add(openCrud);
					inputWrapper = labelSelect;
					mapParam.put("style", "#{gerLoginBean.appEntityMap.containsKey('" + entClassName + "')?'margin-right:80px':''};");
					ComponentFactory.updateElementProperties(inputWrapper, mapParam);
				} else {
					Object o = getProperty(inputCloned, "clientBehaviors");
					if (o != null) {
						for (Map.Entry<String, List<ClientBehavior>> objectFacets : ((Map<String, List<ClientBehavior>>) o).entrySet()) {
							String objectMap1 = objectFacets.getKey();
							List<ClientBehavior> l = objectFacets.getValue();
							l.clear();
							// ((UIComponentBase)inputCloned).addClientBehavior(objectMap1,
							// null);
							// List<ClientBehavior> objectMap2List =
							// objectFacets.getValue();
							// ClientBehavior objectMap2 =
							// objectMap2List.get(0);
							// behaviorsArrayList.add(new
							// DevPrototypeComponentBehaviors(objectMap1,
							// objectMap2.getClass().getName(),
							// componentSerializerBehavior(objectMap2)));
						}
						// ((Map<String,List<ClientBehavior>>)o).clear();
					}
				}				
			}
			// node += entitySelected.getName() + "." +
			// devEntityPropertyDescriptor.getPropertyName();
			String str = "";
			if (inputCloned.getValueExpression("value") == null) {
				str = "#{(app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "'" + item + ")).str}";
				mapParam.put("value", str);
			}
			// mapParam.put("styleClass", "");
			mapParam.put("required", devEntityPropertyDescriptor.isPropertyRequired());
			mapParam.put("requiredMessage", "#{msg.required}: " + devEntityPropertyDescriptor.getPropertyLabel());
			// mapParam.put("tabindex", String.valueOf(tabindex));
			mapParam.put("tabindex", String.valueOf("1"));

			if (configMap.containsKey("mask")) {
				mapParam.put("mask", configMap.get("mask"));
			}
			if (configMap.containsKey("precision")) {
				mapParam.put("styleClass", Objects.toString(styleClass, "").concat(" decimalFormat".concat(configMap.get("precision"))));
			}
			if (configMap.containsKey("xhtml") && configMap.get("xhtml").equals("true")) {
				mapParam.put("styleClass", Objects.toString(styleClass, "").concat(" no-upper"));
			}
			if (configMap.containsKey("length")) {
				// mapParam.put("mask", configMap.get("mask"));
			}
			if (configMap.containsKey("pattern")) {
				mapParam.put("pattern", configMap.get("pattern"));
				mapParam.put("styleClass", Objects.toString(styleClass, "").replaceAll(" dateFormat", "").concat(" timeFormat"));
				if (configMap.get("pattern").equals("HH:mm")) {
					mapParam.put("timeOnly", true);
				}
			}
			generateComponentStyleClassName(mapParam, "form", entitySelected, devEntityPropertyDescriptor, "input",styleClass);
			Object readonly = LayoutFieldsFormat.loadOpt(fieldOptions, "readonly").getOptionsValue();
			Object disabled = LayoutFieldsFormat.loadOpt(fieldOptions, "disabled").getOptionsValue();
			
			if(readonly!=null && disabled!=null){
				mapParam.put("readonly", readonly.toString());
				mapParam.put("disabled", disabled.toString());
				mapParam.put("immediate", "true");
				str = "#{(app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "'" + item + ")).read}";
				mapParam.put("value", str);
			}	
			Object value = LayoutFieldsFormat.loadOpt(fieldOptions, "value").getOptionsValue();
			if(value!=null){
				mapParam.put("value", value.toString());
			}
			Object onblur = LayoutFieldsFormat.loadOpt(fieldOptions, "onblur").getOptionsValue();
			if(onblur!=null){
				mapParam.put("onblur", onblur.toString());
			}	
			Object onchange = LayoutFieldsFormat.loadOpt(fieldOptions, "onchange").getOptionsValue();
			if(onchange!=null){
				mapParam.put("onchange", onchange.toString());
			}	
			Object onkeyup = LayoutFieldsFormat.loadOpt(fieldOptions, "onkeyup").getOptionsValue();
			if(onkeyup!=null){
				mapParam.put("onkeyup", onkeyup.toString());
			}
			Object style = LayoutFieldsFormat.loadOpt(fieldOptions, "style").getOptionsValue();
			if(style!=null){
				Object styleOld = getProperty(inputCloned, "style");
				mapParam.put("style", Objects.toString(styleOld,"")+style.toString());
				String styleClassTemp = Objects.toString(mapParam.get("styleClass"),"");
				mapParam.put("styleClass", styleClassTemp+" style");
			}			
			// setValue(inputCloned, "value", str);
			ComponentFactory.updateElementProperties(inputCloned, mapParam);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (inputWrapper != null) {
			inputCloned = inputWrapper;
		}
		return inputCloned;
	}

	private void generateComponentStyleClassName(Map<String, Object> mapParam, String parent, DevEntityClass entitySelected, DevEntityPropertyDescriptor devEntityPropertyDescriptor, String suffix, Object styleClass) {
		String preffix = " c_";
		try {
			mapParam.put("styleClass",Objects.toString(styleClass).concat(" ").concat(Objects.toString(mapParam.get("styleClass"), "")).concat(preffix + parent + preffix + entitySelected.getName() + preffix + devEntityPropertyDescriptor.getPropertyName() + (suffix.isEmpty() ? "" : preffix + suffix)));			
		} catch (NullPointerException e) {
			System.err.println("Error in generateComponentStyleClassName to field"+e.getMessage());
		}
	}

	private void doUIFieldSetFactory(UIComponent comp, UIComponent elementPanel, String parentContainer, String parentEntity, Map<String, List<UIComponent>> elementsContainerMap, DevEntityClass entitySelected, DevRequirement requirement, AppType appType) {
		try {
			String propertyArrayStr = "";
			FacesContext context = FacesContext.getCurrentInstance();
			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			UIComponent labelCloned = null;
			UIComponent inputCloned = null;
			UIComponent label = null;
			UIComponent input = null;
			UIComponent tab = null;
			UIComponent containerChild = ComponentFactory.componentClone(comp.getParent(), false);
			containerChild.getChildren().clear();
			UIComponent algoContainer = ComponentFactory.componentClone(comp.getParent().getParent(), false);
			algoContainer.getChildren().clear();
			UIComponent algoContainerCloned = null;
			UIComponent panelInternalObject = null;
			UIComponent panelAuth = null;
			// Map<String, Object> mapParam1 = new HashMap<String, Object>();
			// mapParam1.put("legend", "");
			// ComponentFactory.updateElementProperties(f2, containerChild,
			// mapParam1, null);
			int tabindex = 1;
			UIComponent buttom = null;
			UIComponent panel = null;
			try {
				Object styleClass = null;
				for (UIComponent child : elementPanel.getChildren()) {
					styleClass = getProperty(child, "styleClass");
					if (styleClass != null && String.valueOf(styleClass).contains("data-panel")) {
						panel = child;
						break;
					}
				}
				for (UIComponent child : elementPanel.getChildren()) {
					if (Class.forName("javax.faces.component.html.HtmlOutputLabel").isAssignableFrom(child.getClass())) {
						label = child;
						break;
					}
				}
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
			}
			Map<String, Object> mapParam = new HashMap<String, Object>();
			UIComponent panelTableParent = null;
			panelTableParent = ComponentFactory.componentClone(panel, false);
			mapParam.put("style", "display:table");
			mapParam.put("layout", "block");
			ComponentFactory.updateElementProperties(panelTableParent, mapParam);
			UIComponent panelScroll = ComponentFactory.componentClone(findComponentByStyleClass("ui-algo-element ui-algo-element-container ui-scrollPanel", elementPanel), false);
			mapParam.put("style", "width:965px;height:335px");
			mapParam.put("mode", "native");
			ComponentFactory.updateElementProperties(panelScroll, mapParam);
			//List<DevEntityPropertyDescriptor> propertySelectedList = new ArrayList<DevEntityPropertyDescriptor>();
			String parent = (parentContainer != null && !parentContainer.isEmpty()) ? parentContainer : "form";
			List<DevReportFieldOptions> fieldOptionsList = LayoutFieldsFormat.getVisibleFields(requirement, parent, entitySelected);
			if (fieldOptionsList.size() > 14) {
				comp.getChildren().add(panelScroll);
				panelScroll.getChildren().add(panelTableParent);
			} else {
				comp.getChildren().add(panelTableParent);
			}
			UIComponent panelTableChild = null;
			for (int i = 0; i < fieldOptionsList.size(); i++) {
				if (i % 8 == 0) {
					UIComponent panelTableColumn = ComponentFactory.componentClone(panel, false);
					mapParam.clear();
					mapParam.put("style", "display:table-cell;vertical-align:top;");
					// mapParam.put("styleClass", "sortable connectedSortable");
					// mapParam.put("layout", "block");
					ComponentFactory.updateElementProperties(panelTableColumn, mapParam);
					panelTableParent.getChildren().add(panelTableColumn);

					panelTableChild = ComponentFactory.componentClone(panel, false);
					mapParam.clear();
					mapParam.put("style", "display:table");
					mapParam.put("styleClass", "sortable connectedSortable");
					// mapParam.put("layout", "block");
					ComponentFactory.updateElementProperties(panelTableChild, mapParam);
					panelTableColumn.getChildren().add(panelTableChild);
				}
				DevReportFieldOptions fieldOptions = fieldOptionsList.get(i);

				DevEntityPropertyDescriptor devEntityPropertyDescriptor = fieldOptions.getEntityPropertyDescriptor();
				UIComponent panelTableRow = ComponentFactory.componentClone(panel, false);
				mapParam.clear();
				mapParam.put("style", "display:table-row");
				generateComponentStyleClassName(mapParam, "form", entitySelected, devEntityPropertyDescriptor, "","");
				ComponentFactory.updateElementProperties(panelTableRow, mapParam);
				panelTableChild.getChildren().add(panelTableRow);
				Map<String, String> configMap = new HashMap<String, String>();

				mapParam.clear();
				// Map<String, Object> mapParam = new HashMap<String, Object>();
				try {
					input = null;
					buttom = null;
				
					// Object styleClass;
					input = createInputComponent(elementPanel, entitySelected, devEntityPropertyDescriptor, configMap, parentEntity, appType, parent,fieldOptions);
					// styleClass = getProperty(input, "styleClass");
					String node = "";
					if (parentEntity.isEmpty()) {
						node = entitySelected.getName();
					}else{
						node = parentEntity;
					}
					boolean isButtonInputChildren = input instanceof javax.faces.component.html.HtmlCommandButton && devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN");
					if (input != null && !isButtonInputChildren) {						
						UIComponent panelTableCellIcon = ComponentFactory.componentClone(panel, false);
						mapParam.clear();
						mapParam.put("style", "display:table-cell;padding:5px;vertical-align:middle;");
						ComponentFactory.updateElementProperties(panelTableCellIcon, mapParam);
						mapParam.clear();
						labelCloned = ComponentFactory.componentClone(label, false);
						mapParam.put("value", "");
						mapParam.put("styleClass", "ui-icon-sortable");
						// mapParam.put("styleClass", "");
						ComponentFactory.updateElementProperties(labelCloned, mapParam);
						panelTableCellIcon.getChildren().add(labelCloned);
						panelTableRow.getChildren().add(panelTableCellIcon);

						UIComponent panelTableCellLabel = ComponentFactory.componentClone(panel, false);
						mapParam.clear();
						mapParam.put("style", "display:table-cell;padding:5px;vertical-align:middle;");
						ComponentFactory.updateElementProperties(panelTableCellLabel, mapParam);
						mapParam.clear();
						labelCloned = ComponentFactory.componentClone(label, false);
						Object label2 = LayoutFieldsFormat.loadOpt(fieldOptions, "label").getOptionsValue();
						if(label2!=null){
							mapParam.put("value", label2.toString());
						}else{
							mapParam.put("value", devEntityPropertyDescriptor.getPropertyLabel());
						}
						// mapParam.put("styleClass", "");
						ComponentFactory.updateElementProperties(labelCloned, mapParam);
						panelTableCellLabel.getChildren().add(labelCloned);
						panelTableRow.getChildren().add(panelTableCellLabel);

						UIComponent panelTableCellInput = ComponentFactory.componentClone(panel, false);
						mapParam.clear();
						mapParam.put("style", "display:table-cell;padding:5px;min-width:200px");
						ComponentFactory.updateElementProperties(panelTableCellInput, mapParam);

						panelTableCellInput.getChildren().add(input);
						panelTableRow.getChildren().add(panelTableCellInput);
						tabindex++;
					} else if (input != null && isButtonInputChildren) {
						UIComponent fieldSet = null;
						for (UIComponent child : elementPanel.getChildren()) {
							Object styleClassEntField = getProperty(child, "styleClass");
							if (styleClassEntField != null && String.valueOf(styleClassEntField).contains("data-form") && Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getClass())) {
								fieldSet = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						UIComponent table = null;
						for (UIComponent child : elementPanel.getChildren()) {
							Object styleClassEntField = getProperty(child, "styleClass");
							if (styleClassEntField != null && String.valueOf(styleClassEntField).contains("data-list") && Class.forName("javax.faces.component.UIData").isAssignableFrom(child.getClass())) {
								table = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						// String parent = (parentContainer != null &&
						// !parentContainer.isEmpty()) ? parentContainer :
						// "form";
						DevEntityClass ent = devEntityPropertyDescriptor.getPropertyClass();
						mapParam.clear();
						if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")) {
							mapParam.put("value", devEntityPropertyDescriptor.getPropertyLabel() + " (#{app.count('" + String.valueOf(node + "." + devEntityPropertyDescriptor.getPropertyName()).toLowerCase() + "')})");
							mapParam.put("action", "#{app.doList('" + parent + "-list-" + ent.getName() + "','" + node + "." + devEntityPropertyDescriptor.getPropertyName() + "')}");
						} else {
							mapParam.put("value", devEntityPropertyDescriptor.getPropertyLabel());
							mapParam.put("action", "#{app.doList('" + parent + "-form-" + ent.getName() + "','" + node + "." + devEntityPropertyDescriptor.getPropertyName() + "')}");
						}
						mapParam.put("update", "@form");
						mapParam.put("immediate", "false");
						ComponentFactory.updateElementProperties(input, mapParam);
						if (panelInternalObject == null) {
							panelInternalObject = ComponentFactory.componentClone(fieldSet, false);
							mapParam.clear();
							mapParam.put("legend", "");
							ComponentFactory.updateElementProperties(panelInternalObject, mapParam);
						}
						panelInternalObject.getChildren().add(input);

						algoContainerCloned = ComponentFactory.componentClone(algoContainer, false);
						UIComponent containerChildClone;
						// String prefix = (parentContainer != null &&
						// !parentContainer.isEmpty()) ? parentContainer + "-" :
						// "form-";
						if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")) {
							containerChildClone = ComponentFactory.componentClone(containerChild, false);
							containerChildClone.getChildren().clear();
							containerChildClone.getChildren().add(table);
							algoContainerCloned.getChildren().clear();
							algoContainerCloned.getChildren().add(containerChildClone);
							String parentList = parent + "-list-" + ent.getName();
							new ComponentFactory(table, elementPanel, parentList, node + "." + devEntityPropertyDescriptor.getPropertyName(), elementsContainerMap, ent, requirement, appType).getComponentCreated();
							elementsContainerMap.put(parentList, new ArrayList<UIComponent>(containerChildClone.getChildren()));
						}
						containerChildClone = ComponentFactory.componentClone(containerChild, false);
						containerChildClone.getChildren().clear();
						containerChildClone.getChildren().add(fieldSet);
						algoContainerCloned.getChildren().clear();
						algoContainerCloned.getChildren().add(containerChildClone);
						String parentForm = parent + "-form-" + ent.getName();
						new ComponentFactory(fieldSet, elementPanel, parentForm, node + "." + devEntityPropertyDescriptor.getPropertyName(), elementsContainerMap, ent, requirement, (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ONEINTERNALOBJECT") ? AppType.FORM : appType)).getComponentCreated();
						elementsContainerMap.put(parentForm, new ArrayList<UIComponent>(containerChildClone.getChildren()));
						mapParam.clear();
						mapParam.put("legend", "");
						// mapParam.put("styleClass", "");
						// ComponentFactory.updateElementProperties(f2,
						// fieldSet, mapParam, null);
						mapParam.clear();
						int colSise = BigDecimal.valueOf(ent.getEntityPropertyDescriptorList().size() / 8f).setScale(0, RoundingMode.UP).intValue() * 2;
						mapParam.put("columns", String.valueOf(colSise));
						// ComponentFactory.updateElementProperties(f2,
						// fieldSet.getC hildren().get(0), mapParam, null);
					} else if (tab != null) {
						DevEntityClass ent = devEntityPropertyDescriptor.getPropertyClass();

						mapParam.put("title", devEntityPropertyDescriptor.getPropertyLabel());
						ComponentFactory.updateElementProperties(tab, mapParam);
						UIComponent fieldSet = null;
						for (UIComponent child : elementPanel.getChildren()) {
							if (Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getClass())) {
								if (!child.getChildren().isEmpty() && Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getChildren().get(0).getClass())) {
									fieldSet = ComponentFactory.componentClone(child, false);
									break;
								}
							}
						}
						UIComponent table = null;
						for (UIComponent child : elementPanel.getChildren()) {
							if (Class.forName("javax.faces.component.UIData").isAssignableFrom(child.getClass())) {
								if (!child.getChildren().isEmpty() && Class.forName("javax.faces.component.UIColumn").isAssignableFrom(child.getChildren().get(0).getClass())) {
									table = ComponentFactory.componentClone(child, false);
									break;
								}
							}
						}
						tab.getChildren().add(table);
						new ComponentFactory(table, elementPanel, "", parentEntity, elementsContainerMap, ent, requirement, appType).getComponentCreated();
						/*
						 * tab.getChildren().add(fieldSet); new
						 * ComponentFactory(fieldSet, elementPanel, "",
						 * elementsContainerMap, ent,
						 * ent.getEntityPropertyDescriptorList
						 * ()).getComponentCreated(); List<UIComponent> comList
						 * = new ArrayList<UIComponent>();
						 * comList.add(fieldSet);
						 * elementsContainerMap.put("form-"+ent.getName(),
						 * comList); mapParam.clear(); mapParam.put("legend",
						 * ""); ComponentFactory.updateElementProperties(f2,
						 * fieldSet, mapParam, null); mapParam.clear();
						 * mapParam.put("columns", "4");
						 * ComponentFactory.updateElementProperties(f2,
						 * fieldSet.getChildren().get(0), mapParam, null);
						 */
					}
				} catch (ClassNotFoundException ex) {
					Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
				}
				// propertyArrayStr +=
				// devEntityPropertyDescriptor.getPropertyName();
			}
			mapParam.clear();
			int colSize = BigDecimal.valueOf(fieldOptionsList.size() / 8f).setScale(0, RoundingMode.UP).intValue() * 2;
			mapParam.put("columns", String.valueOf(colSize));
			// ComponentFactory.updateElementProperties(f2, comp, mapParam,
			// null);
			mapParam.clear();
			if(entitySelected.getLabel().contains(":")){
				mapParam.put("legend", entitySelected.getLabel());	
			}else{
				mapParam.put("legend", "#{msg['registration']}: " + entitySelected.getLabel());				
			}
			// mapParam.put("styleClass", "");
			ComponentFactory.updateElementProperties(f2, comp, mapParam, null);
			if (panelInternalObject != null && !panelInternalObject.getChildren().isEmpty()) {
				comp.getParent().getChildren().add(panelInternalObject);
			}
			if (entitySelected.getEntityClassConfigList() != null && !entitySelected.getEntityClassConfigList().isEmpty()) {
				Map<String, String> entConfigMap = new HashMap<String, String>();
				for (DevEntityClassConfig entConfig : entitySelected.getEntityClassConfigList()) {
					entConfigMap.put(entConfig.getConfigName(), String.valueOf(entConfig.getConfigValue()));
				}
				if (entConfigMap.containsKey("loginAuth") && Boolean.valueOf(entConfigMap.get("loginAuth"))) {
					UIComponent fieldSet = null;
					UIComponent password = null;
					try {
						for (UIComponent child : elementPanel.getChildren()) {
							Object styleClassEntField = getProperty(child, "styleClass");
							if (styleClassEntField != null && String.valueOf(styleClassEntField).contains("data-form") && Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getClass())) {
								fieldSet = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						UIComponent pwlabel = ComponentFactory.componentClone(label, false);
						mapParam.clear();
						mapParam.put("value", String.valueOf(BaseBean.getBundle("password", "msg")).toUpperCase() + ": ");
						ComponentFactory.updateElementProperties(pwlabel, mapParam);
						for (UIComponent child : elementPanel.getChildren()) {
							Object styleClassEntField = getProperty(child, "styleClass");
							if (styleClassEntField != null && String.valueOf(styleClassEntField).contains("ui-password")) {
								password = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						mapParam.clear();
						mapParam.put("validator", "#{app.validateLogin}");
						mapParam.put("value", "");
						mapParam.put("redisplay", "false");
						mapParam.put("autocomplete", "off");
						mapParam.put("required", true);
						mapParam.put("requiredMessage", "#{msg.required}: " + String.valueOf(BaseBean.getBundle("password", "msg")).toUpperCase());
						mapParam.put("tabindex", String.valueOf("1"));
						ComponentFactory.updateElementProperties(f2, password, mapParam, null);
						if (panelAuth == null) {
							panelAuth = ComponentFactory.componentClone(fieldSet, false);
							mapParam.clear();
							mapParam.put("legend", "");
							ComponentFactory.updateElementProperties(panelAuth, mapParam);
						}
						panelAuth.getChildren().add(pwlabel);
						panelAuth.getChildren().add(password);
						comp.getParent().getChildren().add(panelAuth);
					} catch (ClassNotFoundException ex) {
						Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
			if(appType!=null){
				doUICommandButtonFormFactory(comp.getParent(), elementPanel, entitySelected, parentContainer, parentEntity, appType);
			}
			updateComponentContainerUI(requirement, parent, entitySelected, comp, elementPanel);			
		} catch (NoSuchFieldException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void doUIDataGridFactory(UIComponent comp, UIComponent elementPanel, String parentContainer, String parentEntity, Map<String, List<UIComponent>> elementsContainerMap, DevEntityClass entitySelected, DevRequirement requirement, AppType appType) {
		try {
			UIComponent column = null;
			for (UIComponent child : comp.getChildren()) {
				if (Class.forName("javax.faces.component.UIColumn").isAssignableFrom(child.getClass())) {
					column = child;
					break;
				}
			}
			String propertyArrayStr = "";
			FacesContext context = FacesContext.getCurrentInstance();
			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			UIComponent labelCloned = null;
			UIComponent inputCloned = null;
			UIComponent label = null;
			UIComponent input = null;
			UIComponent image = null;
			UIComponent tab = null;
			UIComponent containerChild = ComponentFactory.componentClone(comp.getParent(), false);
			containerChild.getChildren().clear();
			UIComponent algoContainer = ComponentFactory.componentClone(comp.getParent().getParent(), false);
			algoContainer.getChildren().clear();
			UIComponent algoContainerCloned = null;
			UIComponent panelInternalObject = null;
			UIComponent panelAuth = null;
			// Map<String, Object> mapParam1 = new HashMap<String, Object>();
			// mapParam1.put("legend", "");
			// ComponentFactory.updateElementProperties(f2, containerChild,
			// mapParam1, null);
			int tabindex = 1;
			UIComponent buttom = null;
			UIComponent panel = null;
			UIComponent uipanel = null;
			UIComponent output = null;

			try {
				Object styleClass = null;
				for (UIComponent child : elementPanel.getChildren()) {
					styleClass = getProperty(child, "styleClass");
					if (styleClass != null && String.valueOf(styleClass).contains("data-panel")) {
						panel = child;
						break;
					}
				}
				for (UIComponent child : elementPanel.getChildren()) {
					if (Class.forName("javax.faces.component.html.HtmlOutputLabel").isAssignableFrom(child.getClass())) {
						label = child;
						break;
					}
				}
				for (UIComponent child : elementPanel.getChildren()) {
					styleClass = getProperty(child, "styleClass");
					if (styleClass != null && String.valueOf(styleClass).contains("ui-panel")) {
						uipanel = child;
						break;
					}
				}
				for (UIComponent child : elementPanel.getChildren()) {
					styleClass = getProperty(child, "styleClass");
					if (styleClass != null && String.valueOf(styleClass).contains("data-output")) {
						output = child;
						break;
					}
				}
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
			}
			Map<String, Object> mapParam = new HashMap<String, Object>();
			mapParam.clear();
			mapParam.put("headerText", "");
			mapParam.put("sortBy", "toString()");
			mapParam.put("filterBy", "toString()");
			mapParam.put("filterMatchMode", "contains");
			mapParam.put("filterStyle", "display:none;");
			ComponentFactory.updateElementProperties(f2, column, mapParam, null);
			UIComponent panelTableParent = null;
			panelTableParent = ComponentFactory.componentClone(panel, false);
			mapParam.clear();
			mapParam.put("style", "display:table");
			mapParam.put("layout", "block");
			ComponentFactory.updateElementProperties(f2, panelTableParent, mapParam, null);
			// uipanel.getChildren().add(panelTableParent);
			column.getChildren().clear();
			column.getChildren().add(panelTableParent);
			// comp.getChildren().add(panelTableParent);
			UIComponent panelTableChild = null;			
			
			List<DevReportFieldOptions> fieldOptionsList = LayoutFieldsFormat.filter(requirement, parentContainer, "visible", "true");
			
			for (int i = 0; i < fieldOptionsList.size(); i++) {
				if (i % 7 == 0) {
					UIComponent panelTableColumn = ComponentFactory.componentClone(panel, false);
					mapParam.clear();
					mapParam.put("style", "display:table-cell;");
					// mapParam.put("styleClass", "sortable connectedSortable");
					// mapParam.put("layout", "block");
					ComponentFactory.updateElementProperties(f2, panelTableColumn, mapParam, null);
					panelTableParent.getChildren().add(panelTableColumn);

					panelTableChild = ComponentFactory.componentClone(panel, false);
					mapParam.clear();
					mapParam.put("style", "display:table");
					mapParam.put("styleClass", "sortable connectedSortable");
					// mapParam.put("layout", "block");
					ComponentFactory.updateElementProperties(f2, panelTableChild, mapParam, null);
					panelTableColumn.getChildren().add(panelTableChild);
				}
				UIComponent panelTableRow = ComponentFactory.componentClone(panel, false);
				mapParam.clear();
				mapParam.put("style", "display:table-row");
				ComponentFactory.updateElementProperties(f2, panelTableRow, mapParam, null);
				panelTableChild.getChildren().add(panelTableRow);
				DevEntityPropertyDescriptor devEntityPropertyDescriptor = fieldOptionsList.get(i).getEntityPropertyDescriptor();
				Map<String, String> configMap = new HashMap<String, String>();
				if (devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList() != null) {
					for (DevEntityPropertyDescriptorConfig devEntityPropertyDescriptorConfig : devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList()) {
						if (!String.valueOf(devEntityPropertyDescriptorConfig.getConfigName()).isEmpty() && !String.valueOf(devEntityPropertyDescriptorConfig.getConfigName()).equals("null")) {
							configMap.put(devEntityPropertyDescriptorConfig.getConfigName(), devEntityPropertyDescriptorConfig.getConfigValue());
						}
					}
				}
				mapParam.clear();
				// Map<String, Object> mapParam = new HashMap<String, Object>();
				try {
					input = null;
					buttom = null;
					Object styleClass = null;
					for (UIComponent child : elementPanel.getChildren()) {
						styleClass = getProperty(child, "styleClass");
						if (styleClass != null && String.valueOf(styleClass).contains("ui-integer")) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("INTEGER")) {
								input = child;
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlInputText").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("FLOAT")) {
								input = child;
								if (!configMap.containsKey("precision")) {
									configMap.put("precision", "2");
								}
								break;
							}
						}
						if (styleClass != null && String.valueOf(styleClass).contains("ui-mask")) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && configMap.containsKey("mask")) {
								input = child;
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlInputText").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && configMap.isEmpty()) {
								input = child;
								break;
							}
						}
						if (styleClass != null && String.valueOf(styleClass).contains("ui-textArea")) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && configMap.containsKey("length")) {
								input = child;
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlSelectBooleanCheckbox").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("BOOLEAN")) {
								input = child;
								break;
							}
						}
						if (styleClass != null && String.valueOf(styleClass).contains("data-image")) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("FILE")) {
								UIComponent imageCloned = ComponentFactory.componentClone(child, false);
								String propValObj = "app.prepareStreamedContent(app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item))";
								// String str = "#{" + propValObj +
								// ".val==null?msg['select']:" + propValObj +
								// ".val}";
								// String str = "#{" + propValObj + "}";
								String str = "#{app.getFile(app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item))}";
								mapParam.put("value", str);
								mapParam.put("height", "100");
								mapParam.put("width", "150");
								ComponentFactory.updateElementProperties(f2, imageCloned, mapParam, null);
								String action = str;// "#{app.getFileDownload("
													// + getImgRandon() + ")}";
								ValueExpression value = FacesContext.getCurrentInstance().getApplication().getExpressionFactory().createValueExpression(FacesContext.getCurrentInstance().getELContext(), action, Object.class);
								// imageCloned.setValueExpression("value",
								// value);
								Object obj = imageCloned.getValueExpression("value");
								panelTableRow.getChildren().add(imageCloned);
								break;
							}
						}
						if (false && Class.forName("javax.faces.component.html.HtmlCommandButton").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("FILE")) {
								input = child;
								// mapParam.put("update", "@form");
								mapParam.put("immediate", "true");
								String propValObj = "app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "')";
								String str = "#{" + propValObj + ".val==null?msg['select']:" + propValObj + ".val}";
								mapParam.put("value", str);
								mapParam.put("action", "#{app.prepareUpload(" + propValObj + ")}");
								mapParam.put("oncomplete", "(handleFileUploadDlg(xhr, status, args))");
								// mapParam.put("onclick",
								// "function () {uploadFileWv.show();return true;}");
								// mapParam.put("onclick",
								// "javascript:(function(){uploadFileWv.show();return false;})");
								ComponentFactory.updateElementProperties(f2, input, mapParam, null);
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlSelectOneMenu").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCLASS") && devEntityPropertyDescriptor.getPropertyClass() != null) {
								input = child;
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlCommandButton").isAssignableFrom(child.getClass())) {
							if ((devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ONEINTERNALOBJECT") || devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")) && devEntityPropertyDescriptor.getPropertyClass() != null) {
								buttom = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						if (false && Class.forName("javax.faces.component.UIData").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN") && devEntityPropertyDescriptor.getPropertyClass() != null) {
								if (Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getChildren().get(0).getClass())) {
									if (containerChild == null) {
										containerChild = ComponentFactory.componentClone(child, false);
										tab = containerChild.getChildren().get(0);
										comp.getParent().getParent().getChildren().add(containerChild);
									} else {
										tab = ComponentFactory.componentClone(containerChild.getChildren().get(0), false);
										containerChild.getChildren().add(tab);
									}
									break;
								}
							}
						}
						if (styleClass != null && String.valueOf(styleClass).contains("ui-calendar")) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("DATE")) {
								input = child;
								break;
							}
						}
					}
					String node = "";
					if (!parentEntity.isEmpty()) {
						node = parentEntity + ".";
					}
					if (input != null) {
						UIComponent panelTableCellIcon = ComponentFactory.componentClone(panel, false);
						mapParam.clear();
						mapParam.put("style", "display:table-cell;padding:5px;vertical-align:middle;");
						ComponentFactory.updateElementProperties(f2, panelTableCellIcon, mapParam, null);
						mapParam.clear();
						labelCloned = ComponentFactory.componentClone(label, false);
						mapParam.put("value", "");
						mapParam.put("styleClass", "ui-icon-sortable");
						// mapParam.put("styleClass", "");
						ComponentFactory.updateElementProperties(f2, labelCloned, mapParam, null);
						panelTableCellIcon.getChildren().add(labelCloned);
						panelTableRow.getChildren().add(panelTableCellIcon);

						UIComponent panelTableCellLabel = ComponentFactory.componentClone(panel, false);
						mapParam.clear();
						mapParam.put("style", "display:table-cell;padding:5px;vertical-align:middle;");
						ComponentFactory.updateElementProperties(f2, panelTableCellLabel, mapParam, null);
						mapParam.clear();
						labelCloned = ComponentFactory.componentClone(label, false);
						if (!configMap.containsKey("xhtml") || configMap.get("xhtml").equals("false")) {
							mapParam.put("value", devEntityPropertyDescriptor.getPropertyLabel());
						} else {
							mapParam.put("value", "");
						}
						// mapParam.put("styleClass", "");
						ComponentFactory.updateElementProperties(f2, labelCloned, mapParam, null);
						panelTableCellLabel.getChildren().add(labelCloned);
						panelTableRow.getChildren().add(panelTableCellLabel);

						UIComponent panelTableCellInput = ComponentFactory.componentClone(panel, false);
						mapParam.clear();
						mapParam.put("style", "display:table-cell;padding:5px;");
						ComponentFactory.updateElementProperties(f2, panelTableCellInput, mapParam, null);
						UIComponent outputCloned = ComponentFactory.componentClone(output, false);
						// inputCloned = ComponentFactory.componentClone(input,
						// false);
						mapParam.clear();
						if (Class.forName("javax.faces.component.html.HtmlSelectOneMenu").isAssignableFrom(input.getClass())) {
							DevEntityClass ent = devEntityPropertyDescriptor.getPropertyClass();
							new ComponentFactory(inputCloned, elementPanel, "value", entitySelected.getName(), elementsContainerMap, ent, requirement, appType);
							mapParam.put("converter", "EntityObjectConverter");
						}
						// node += entitySelected.getName() + "." +
						// devEntityPropertyDescriptor.getPropertyName();
						String str = "";
						if (outputCloned.getValueExpression("value") == null) {
							if (!parentEntity.isEmpty()) {
								str = "#{app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',app.childBean).val}";
							} else {
								// str = "#{app.$('" + entitySelected.getName()
								// + "." +
								// devEntityPropertyDescriptor.getPropertyName()
								// + "').val}";
								str = "#{app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item).val}";
							}
							mapParam.put("value", str);
						}
						// mapParam.put("styleClass", "");
						mapParam.put("required", devEntityPropertyDescriptor.isPropertyRequired());
						mapParam.put("requiredMessage", "#{msg.required}: " + devEntityPropertyDescriptor.getPropertyLabel());
						// mapParam.put("tabindex", String.valueOf(tabindex));
						mapParam.put("tabindex", String.valueOf("1"));
						if (configMap.containsKey("mask")) {
							mapParam.put("mask", configMap.get("mask"));
						}
						if (configMap.containsKey("precision")) {
							mapParam.put("styleClass", String.valueOf(styleClass).concat(" decimalFormat".concat(configMap.get("precision"))));
						}
						if (configMap.containsKey("length")) {
							// mapParam.put("mask", configMap.get("mask"));
						}
						if (configMap.containsKey("pattern")) {
							mapParam.put("pattern", configMap.get("pattern"));
							mapParam.put("styleClass", String.valueOf(styleClass).replaceAll(" dateFormat", "").concat(" timeFormat"));
							if (configMap.get("pattern").equals("HH:mm")) {
								mapParam.put("timeOnly", true);
							}
						}
						mapParam.put("escape", false);
						// setValue(inputCloned, "value", str);
						ComponentFactory.updateElementProperties(f2, outputCloned, mapParam, null);
						panelTableCellInput.getChildren().add(outputCloned);
						panelTableRow.getChildren().add(panelTableCellInput);
						tabindex++;
					} else if (buttom != null) {
						UIComponent fieldSet = null;
						for (UIComponent child : elementPanel.getChildren()) {
							Object styleClassEntField = getProperty(child, "styleClass");
							if (styleClassEntField != null && String.valueOf(styleClassEntField).contains("data-form") && Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getClass())) {
								fieldSet = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						UIComponent table = null;
						for (UIComponent child : elementPanel.getChildren()) {
							Object styleClassEntField = getProperty(child, "styleClass");
							if (styleClassEntField != null && String.valueOf(styleClassEntField).contains("data-list") && Class.forName("javax.faces.component.UIData").isAssignableFrom(child.getClass())) {
								table = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						String parent = (parentContainer != null && !parentContainer.isEmpty()) ? parentContainer : "form";
						DevEntityClass ent = devEntityPropertyDescriptor.getPropertyClass();
						mapParam.clear();
						if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")) {
							mapParam.put("value", devEntityPropertyDescriptor.getPropertyLabel() + " (#{app.count('" + String.valueOf(node + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName()).toLowerCase() + "')})");
							mapParam.put("action", "#{app.doList('" + parent + "-list-" + ent.getName() + "','" + node + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "')}");
						} else {
							mapParam.put("value", devEntityPropertyDescriptor.getPropertyLabel());
							mapParam.put("action", "#{app.doList('" + parent + "-form-" + ent.getName() + "','" + node + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "')}");
						}
						mapParam.put("update", "@form");
						mapParam.put("immediate", "false");
						ComponentFactory.updateElementProperties(f2, buttom, mapParam, null);
						if (panelInternalObject == null) {
							panelInternalObject = ComponentFactory.componentClone(fieldSet, false);
							mapParam.clear();
							mapParam.put("legend", "");
							ComponentFactory.updateElementProperties(f2, panelInternalObject, mapParam, null);
						}
						panelInternalObject.getChildren().add(buttom);

						algoContainerCloned = ComponentFactory.componentClone(algoContainer, false);
						UIComponent containerChildClone;
						// String prefix = (parentContainer != null &&
						// !parentContainer.isEmpty()) ? parentContainer + "-" :
						// "form-";
						if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")) {
							containerChildClone = ComponentFactory.componentClone(containerChild, false);
							containerChildClone.getChildren().clear();
							containerChildClone.getChildren().add(table);
							algoContainerCloned.getChildren().clear();
							algoContainerCloned.getChildren().add(containerChildClone);
							new ComponentFactory(table, elementPanel, parent, entitySelected.getName(), elementsContainerMap, ent, requirement, appType).getComponentCreated();
							elementsContainerMap.put(parent + "-list-" + ent.getName(), containerChildClone.getChildren());
						}
						containerChildClone = ComponentFactory.componentClone(containerChild, false);
						containerChildClone.getChildren().clear();
						containerChildClone.getChildren().add(fieldSet);
						algoContainerCloned.getChildren().clear();
						algoContainerCloned.getChildren().add(containerChildClone);
						new ComponentFactory(fieldSet, elementPanel, parent, entitySelected.getName(), elementsContainerMap, ent, requirement, (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ONEINTERNALOBJECT") ? AppType.FORM : appType)).getComponentCreated();
						elementsContainerMap.put(parent + "-form-" + ent.getName(), containerChildClone.getChildren());
						mapParam.clear();
						mapParam.put("legend", "");
						// mapParam.put("styleClass", "");
						// ComponentFactory.updateElementProperties(f2,
						// fieldSet, mapParam, null);
						mapParam.clear();
						int colSise = BigDecimal.valueOf(ent.getEntityPropertyDescriptorList().size() / 8f).setScale(0, RoundingMode.UP).intValue() * 2;
						mapParam.put("columns", String.valueOf(colSise));
						// ComponentFactory.updateElementProperties(f2,
						// fieldSet.getC hildren().get(0), mapParam, null);

					} else if (tab != null) {
						DevEntityClass ent = devEntityPropertyDescriptor.getPropertyClass();
						mapParam.put("title", devEntityPropertyDescriptor.getPropertyLabel());
						ComponentFactory.updateElementProperties(f2, tab, mapParam, null);
						UIComponent fieldSet = null;
						for (UIComponent child : elementPanel.getChildren()) {
							if (Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getClass())) {
								if (!child.getChildren().isEmpty() && Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getChildren().get(0).getClass())) {
									fieldSet = ComponentFactory.componentClone(child, false);
									break;
								}
							}
						}
						UIComponent table = null;
						for (UIComponent child : elementPanel.getChildren()) {
							if (Class.forName("javax.faces.component.UIData").isAssignableFrom(child.getClass())) {
								if (!child.getChildren().isEmpty() && Class.forName("javax.faces.component.UIColumn").isAssignableFrom(child.getChildren().get(0).getClass())) {
									table = ComponentFactory.componentClone(child, false);
									break;
								}
							}
						}
						tab.getChildren().add(table);
						new ComponentFactory(table, elementPanel, "", parentEntity, elementsContainerMap, ent, requirement, appType).getComponentCreated();
						/*
						 * tab.getChildren().add(fieldSet); new
						 * ComponentFactory(fieldSet, elementPanel, "",
						 * elementsContainerMap, ent,
						 * ent.getEntityPropertyDescriptorList
						 * ()).getComponentCreated(); List<UIComponent> comList
						 * = new ArrayList<UIComponent>();
						 * comList.add(fieldSet);
						 * elementsContainerMap.put("form-"+ent.getName(),
						 * comList); mapParam.clear(); mapParam.put("legend",
						 * ""); ComponentFactory.updateElementProperties(f2,
						 * fieldSet, mapParam, null); mapParam.clear();
						 * mapParam.put("columns", "4");
						 * ComponentFactory.updateElementProperties(f2,
						 * fieldSet.getChildren().get(0), mapParam, null);
						 */
					}
				} catch (ClassNotFoundException ex) {
					Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
				}
				// propertyArrayStr +=
				// devEntityPropertyDescriptor.getPropertyName();
			}
			mapParam.clear();
			int colSise = BigDecimal.valueOf(fieldOptionsList.size() / 8f).setScale(0, RoundingMode.UP).intValue() * 2;
			mapParam.put("columns", String.valueOf(colSise));
			// ComponentFactory.updateElementProperties(f2, comp, mapParam,
			// null);
			// mapParam.clear();
			// mapParam.put("header", "#{msg['registration']}: " +
			// entitySelected.getLabel());
			// mapParam.put("styleClass", "");
			// ComponentFactory.updateElementProperties(f2, comp, mapParam,
			// null);

			String node = "";
			if (!parentEntity.isEmpty()) {
				node = parentEntity + ".";
			}
			String str = "#{app.beanListMap.get('" + String.valueOf(node + entitySelected.getName()).toLowerCase() + "')}";
			String str3 = "#{app.beanListFilteredMap.get('" + String.valueOf(node + entitySelected.getName()).toLowerCase() + "')}";
			String str2 = "#{app.beanSel}";
			mapParam.clear();
			mapParam.put("value", str);
			mapParam.put("rowKey", "#{item.hashCode()}");
			mapParam.put("filteredValue", str3);
			// mapParam.put("selection", str2);
			// mapParam.put("selectionMode", "single");
			mapParam.put("paginator", "true");
			mapParam.put("rows", "2");
			mapParam.put("sortMode", "multiple");
			mapParam.put("tableStyle", "width:990px;");
			mapParam.put("style", "width:990px;");
			mapParam.put("scrollable", "true");
			mapParam.put("scrollWidth", "1010");
			mapParam.put("scrollHeight", "280");
			mapParam.put("widgetVar", entitySelected.getName() + "DataTable");

			componentCreated = comp;
			ComponentFactory.updateElementProperties(f2, componentCreated, mapParam, null);

			if (panelInternalObject != null && !panelInternalObject.getChildren().isEmpty()) {
				comp.getParent().getChildren().add(panelInternalObject);
			}
			if (componentCreated.getFacets().containsKey("footer")) {
				UIComponent export = componentCreated.getFacets().get("footer");
				if (export instanceof UICommand) {
					mapParam.clear();
					mapParam.put("value", "");
					mapParam.put("action", "");
					ComponentFactory.updateElementProperties(f2, export, mapParam, null);
				}
			}
			if (componentCreated.getFacets().containsKey("header")) {
				UIComponent labelCloned2 = null;
				UIComponent label2Cloned = null;
				UIComponent inputCloned2 = null;
				UIComponent panelCloned = null;
				UIComponent panelCloned2 = null;

				panelCloned = ComponentFactory.componentClone(findComponentByStyleClass("ui-panel", elementPanel), false);
				mapParam.clear();
				mapParam.put("header", "");
				mapParam.put("legend", "");
				ComponentFactory.updateElementProperties(f2, panelCloned, mapParam, null);
				for (UIComponent child : elementPanel.getChildren()) {

					if (panelCloned2 == null && child instanceof javax.faces.component.html.HtmlOutputLabel) {
						// panelCloned2 = ComponentFactory.componentClone(
						// child, context);
						mapParam.clear();
						// mapParam.put("header", "");
						// ComponentFactory.updateElementProperties(f2,
						// panelCloned2, mapParam, null);
					}
					if (labelCloned2 == null && child instanceof javax.faces.component.html.HtmlOutputLabel) {
						labelCloned2 = ComponentFactory.componentClone(child, false);
						mapParam.clear();
						mapParam.put("value", "#{msg['listing']}: " + entitySelected.getLabel());
						ComponentFactory.updateElementProperties(f2, labelCloned2, mapParam, null);
					}
					if (label2Cloned == null && child instanceof javax.faces.component.html.HtmlOutputLabel) {
						label2Cloned = ComponentFactory.componentClone(child, false);
						mapParam.clear();
						mapParam.put("value", "#{msg['search']}: ");
						mapParam.put("style", "float:right;top:-5px;position: relative;");
						ComponentFactory.updateElementProperties(f2, label2Cloned, mapParam, null);
					}
					if (inputCloned2 == null && child instanceof javax.faces.component.html.HtmlInputText) {
						inputCloned2 = ComponentFactory.componentClone(child, false);
						mapParam.clear();
						mapParam.put("onkeyup", entitySelected.getName() + "DataTable" + ".filter()");
						// mapParam.put("style",
						// "float:right;position:relative;top:-20% !important;");
						ComponentFactory.updateElementProperties(f2, inputCloned2, mapParam, null);
						inputCloned2.setId("globalFilter");
					}
				}

				panelCloned.getChildren().add(labelCloned2);
				panelCloned.getChildren().add(label2Cloned);
				label2Cloned.getChildren().add(inputCloned2);

				componentCreated.getFacets().put("header", panelCloned);

			}
			// doUICommandButtonFormFactory(comp.getParent(), elementPanel,
			// entitySelected, parentContainer, parentEntity, appType);
		} catch (NoSuchFieldException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void doUICarouselFactory(UIComponent comp, UIComponent elementPanel, String parentContainer, String parentEntity, Map<String, List<UIComponent>> elementsContainerMap, DevEntityClass entitySelected, DevRequirement requirement, AppType appType) {
		try {
			UIComponent column = null;
			for (UIComponent child : comp.getChildren()) {
				if (Class.forName("javax.faces.component.UIColumn").isAssignableFrom(child.getClass())) {
					column = child;
					break;
				}
			}
			String propertyArrayStr = "";
			FacesContext context = FacesContext.getCurrentInstance();
			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			UIComponent labelCloned = null;
			UIComponent inputCloned = null;
			UIComponent label = null;
			UIComponent input = null;
			UIComponent tab = null;
			UIComponent containerChild = ComponentFactory.componentClone(comp.getParent(), false);
			containerChild.getChildren().clear();
			UIComponent algoContainer = ComponentFactory.componentClone(comp.getParent().getParent(), false);
			algoContainer.getChildren().clear();
			UIComponent algoContainerCloned = null;
			UIComponent panelInternalObject = null;
			UIComponent panelAuth = null;
			// Map<String, Object> mapParam1 = new HashMap<String, Object>();
			// mapParam1.put("legend", "");
			// ComponentFactory.updateElementProperties(f2, containerChild,
			// mapParam1, null);
			int tabindex = 1;
			UIComponent buttom = null;
			UIComponent panel = null;
			UIComponent uipanel = null;
			try {
				Object styleClass = null;
				for (UIComponent child : elementPanel.getChildren()) {
					styleClass = getProperty(child, "styleClass");
					if (styleClass != null && String.valueOf(styleClass).contains("data-panel")) {
						panel = child;
						break;
					}
				}
				for (UIComponent child : elementPanel.getChildren()) {
					if (Class.forName("javax.faces.component.html.HtmlOutputLabel").isAssignableFrom(child.getClass())) {
						label = child;
						break;
					}
				}
				for (UIComponent child : elementPanel.getChildren()) {
					styleClass = getProperty(child, "styleClass");
					if (styleClass != null && String.valueOf(styleClass).contains("ui-panel")) {
						uipanel = child;
						break;
					}
				}
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
			}
			Map<String, Object> mapParam = new HashMap<String, Object>();
			UIComponent panelTableParent = null;
			panelTableParent = ComponentFactory.componentClone(panel, false);
			mapParam.clear();
			mapParam.put("style", "display:table");
			mapParam.put("layout", "block");
			ComponentFactory.updateElementProperties(f2, panelTableParent, mapParam, null);
			// uipanel.getChildren().add(panelTableParent);
			// column.getChildren().add(uipanel);
			comp.getChildren().add(panelTableParent);
			UIComponent panelTableChild = null;
			List<DevReportFieldOptions> fieldOptionsList = LayoutFieldsFormat.filter(requirement, parentContainer, "visible", "true");
			for (int i = 0; i < fieldOptionsList.size(); i++) {
				if (i % 7 == 0) {
					UIComponent panelTableColumn = ComponentFactory.componentClone(panel, false);
					mapParam.clear();
					mapParam.put("style", "display:table-cell;");
					// mapParam.put("styleClass", "sortable connectedSortable");
					// mapParam.put("layout", "block");
					ComponentFactory.updateElementProperties(f2, panelTableColumn, mapParam, null);
					panelTableParent.getChildren().add(panelTableColumn);

					panelTableChild = ComponentFactory.componentClone(panel, false);
					mapParam.clear();
					mapParam.put("style", "display:table");
					mapParam.put("styleClass", "sortable connectedSortable");
					// mapParam.put("layout", "block");
					ComponentFactory.updateElementProperties(f2, panelTableChild, mapParam, null);
					panelTableColumn.getChildren().add(panelTableChild);
				}
				UIComponent panelTableRow = ComponentFactory.componentClone(panel, false);
				mapParam.clear();
				mapParam.put("style", "display:table-row");
				ComponentFactory.updateElementProperties(f2, panelTableRow, mapParam, null);
				panelTableChild.getChildren().add(panelTableRow);
				DevEntityPropertyDescriptor devEntityPropertyDescriptor = fieldOptionsList.get(i).getEntityPropertyDescriptor();
				Map<String, String> configMap = new HashMap<String, String>();
				if (devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList() != null) {
					for (DevEntityPropertyDescriptorConfig devEntityPropertyDescriptorConfig : devEntityPropertyDescriptor.getEntityPropertyDescriptorConfigList()) {
						if (!String.valueOf(devEntityPropertyDescriptorConfig.getConfigName()).isEmpty() && !String.valueOf(devEntityPropertyDescriptorConfig.getConfigName()).equals("null")) {
							configMap.put(devEntityPropertyDescriptorConfig.getConfigName(), devEntityPropertyDescriptorConfig.getConfigValue());
						}
					}
				}
				mapParam.clear();
				// Map<String, Object> mapParam = new HashMap<String, Object>();
				try {
					input = null;
					buttom = null;
					Object styleClass = null;
					for (UIComponent child : elementPanel.getChildren()) {
						styleClass = getProperty(child, "styleClass");
						if (styleClass != null && String.valueOf(styleClass).contains("ui-integer")) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("INTEGER")) {
								input = child;
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlInputText").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("FLOAT")) {
								input = child;
								if (!configMap.containsKey("precision")) {
									configMap.put("precision", "2");
								}
								break;
							}
						}
						if (styleClass != null && String.valueOf(styleClass).contains("ui-mask")) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && configMap.containsKey("mask")) {
								input = child;
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlInputText").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && configMap.isEmpty()) {
								input = child;
								break;
							}
						}
						if (styleClass != null && String.valueOf(styleClass).contains("ui-textArea")) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("STRING") && configMap.containsKey("length")) {
								input = child;
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlSelectBooleanCheckbox").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("BOOLEAN")) {
								input = child;
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlCommandButton").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("FILE")) {
								input = child;
								// mapParam.put("update", "@form");
								mapParam.put("immediate", "true");
								String propValObj = "app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "')";
								String str = "#{" + propValObj + ".val==null?msg['select']:" + propValObj + ".val}";
								mapParam.put("value", str);
								mapParam.put("action", "#{app.prepareUpload(" + propValObj + ")}");
								mapParam.put("oncomplete", "(handleFileUploadDlg(xhr, status, args))");
								// mapParam.put("onclick",
								// "function () {uploadFileWv.show();return true;}");
								// mapParam.put("onclick",
								// "javascript:(function(){uploadFileWv.show();return false;})");
								ComponentFactory.updateElementProperties(f2, input, mapParam, null);
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlSelectOneMenu").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCLASS") && devEntityPropertyDescriptor.getPropertyClass() != null) {
								input = child;
								break;
							}
						}
						if (Class.forName("javax.faces.component.html.HtmlCommandButton").isAssignableFrom(child.getClass())) {
							if ((devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ONEINTERNALOBJECT") || devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")) && devEntityPropertyDescriptor.getPropertyClass() != null) {
								buttom = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						if (false && Class.forName("javax.faces.component.UIData").isAssignableFrom(child.getClass())) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN") && devEntityPropertyDescriptor.getPropertyClass() != null) {
								if (Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getChildren().get(0).getClass())) {
									if (containerChild == null) {
										containerChild = ComponentFactory.componentClone(child, false);
										tab = containerChild.getChildren().get(0);
										comp.getParent().getParent().getChildren().add(containerChild);
									} else {
										tab = ComponentFactory.componentClone(containerChild.getChildren().get(0), false);
										containerChild.getChildren().add(tab);
									}
									break;
								}
							}
						}
						if (styleClass != null && String.valueOf(styleClass).contains("ui-calendar")) {
							if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("DATE")) {
								input = child;
								break;
							}
						}
					}
					String node = "";
					if (!parentEntity.isEmpty()) {
						node = parentEntity + ".";
					}
					if (input != null) {
						UIComponent panelTableCellIcon = ComponentFactory.componentClone(panel, false);
						mapParam.clear();
						mapParam.put("style", "display:table-cell;padding:5px;vertical-align:middle;");
						ComponentFactory.updateElementProperties(f2, panelTableCellIcon, mapParam, null);
						mapParam.clear();
						labelCloned = ComponentFactory.componentClone(label, false);
						mapParam.put("value", "");
						mapParam.put("styleClass", "ui-icon-sortable");
						// mapParam.put("styleClass", "");
						ComponentFactory.updateElementProperties(f2, labelCloned, mapParam, null);
						panelTableCellIcon.getChildren().add(labelCloned);
						panelTableRow.getChildren().add(panelTableCellIcon);

						UIComponent panelTableCellLabel = ComponentFactory.componentClone(panel, false);
						mapParam.clear();
						mapParam.put("style", "display:table-cell;padding:5px;vertical-align:middle;");
						ComponentFactory.updateElementProperties(f2, panelTableCellLabel, mapParam, null);
						mapParam.clear();
						labelCloned = ComponentFactory.componentClone(label, false);
						mapParam.put("value", devEntityPropertyDescriptor.getPropertyLabel());
						// mapParam.put("styleClass", "");
						ComponentFactory.updateElementProperties(f2, labelCloned, mapParam, null);
						panelTableCellLabel.getChildren().add(labelCloned);
						panelTableRow.getChildren().add(panelTableCellLabel);

						UIComponent panelTableCellInput = ComponentFactory.componentClone(panel, false);
						mapParam.clear();
						mapParam.put("style", "display:table-cell;padding:5px;");
						ComponentFactory.updateElementProperties(f2, panelTableCellInput, mapParam, null);
						inputCloned = ComponentFactory.componentClone(input, false);
						mapParam.clear();
						if (Class.forName("javax.faces.component.html.HtmlSelectOneMenu").isAssignableFrom(input.getClass())) {
							DevEntityClass ent = devEntityPropertyDescriptor.getPropertyClass();
							new ComponentFactory(inputCloned, elementPanel, "value", entitySelected.getName(), elementsContainerMap, ent, requirement, appType);
							mapParam.put("converter", "EntityObjectConverter");
						}
						// node += entitySelected.getName() + "." +
						// devEntityPropertyDescriptor.getPropertyName();
						String str = "";
						if (inputCloned.getValueExpression("value") == null) {
							if (!parentEntity.isEmpty()) {
								str = "#{app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',app.childBean).val}";
							} else {
								// str = "#{app.$('" + entitySelected.getName()
								// + "." +
								// devEntityPropertyDescriptor.getPropertyName()
								// + "').val}";
								str = "#{app.$('" + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "',item).val}";
							}
							mapParam.put("value", str);
						}
						// mapParam.put("styleClass", "");
						mapParam.put("required", devEntityPropertyDescriptor.isPropertyRequired());
						mapParam.put("requiredMessage", "#{msg.required}: " + devEntityPropertyDescriptor.getPropertyLabel());
						// mapParam.put("tabindex", String.valueOf(tabindex));
						mapParam.put("tabindex", String.valueOf("1"));
						if (configMap.containsKey("mask")) {
							mapParam.put("mask", configMap.get("mask"));
						}
						if (configMap.containsKey("precision")) {
							mapParam.put("styleClass", String.valueOf(styleClass).concat(" decimalFormat".concat(configMap.get("precision"))));
						}
						if (configMap.containsKey("length")) {
							// mapParam.put("mask", configMap.get("mask"));
						}
						if (configMap.containsKey("pattern")) {
							mapParam.put("pattern", configMap.get("pattern"));
							mapParam.put("styleClass", String.valueOf(styleClass).replaceAll(" dateFormat", "").concat(" timeFormat"));
							if (configMap.get("pattern").equals("HH:mm")) {
								mapParam.put("timeOnly", true);
							}
						}
						mapParam.put("disabled", true);
						// setValue(inputCloned, "value", str);
						ComponentFactory.updateElementProperties(f2, inputCloned, mapParam, null);
						panelTableCellInput.getChildren().add(inputCloned);
						panelTableRow.getChildren().add(panelTableCellInput);
						tabindex++;
					} else if (buttom != null) {
						UIComponent fieldSet = null;
						for (UIComponent child : elementPanel.getChildren()) {
							Object styleClassEntField = getProperty(child, "styleClass");
							if (styleClassEntField != null && String.valueOf(styleClassEntField).contains("data-form") && Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getClass())) {
								fieldSet = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						UIComponent table = null;
						for (UIComponent child : elementPanel.getChildren()) {
							Object styleClassEntField = getProperty(child, "styleClass");
							if (styleClassEntField != null && String.valueOf(styleClassEntField).contains("data-list") && Class.forName("javax.faces.component.UIData").isAssignableFrom(child.getClass())) {
								table = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						String parent = (parentContainer != null && !parentContainer.isEmpty()) ? parentContainer : "form";
						DevEntityClass ent = devEntityPropertyDescriptor.getPropertyClass();
						mapParam.clear();
						if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")) {
							mapParam.put("value", devEntityPropertyDescriptor.getPropertyLabel() + " (#{app.count('" + String.valueOf(node + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName()).toLowerCase() + "')})");
							mapParam.put("action", "#{app.doList('" + parent + "-list-" + ent.getName() + "','" + node + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "')}");
						} else {
							mapParam.put("value", devEntityPropertyDescriptor.getPropertyLabel());
							mapParam.put("action", "#{app.doList('" + parent + "-form-" + ent.getName() + "','" + node + entitySelected.getName() + "." + devEntityPropertyDescriptor.getPropertyName() + "')}");
						}
						mapParam.put("update", "@form");
						mapParam.put("immediate", "false");
						ComponentFactory.updateElementProperties(f2, buttom, mapParam, null);
						if (panelInternalObject == null) {
							panelInternalObject = ComponentFactory.componentClone(fieldSet, false);
							mapParam.clear();
							mapParam.put("legend", "");
							ComponentFactory.updateElementProperties(f2, panelInternalObject, mapParam, null);
						}
						panelInternalObject.getChildren().add(buttom);

						algoContainerCloned = ComponentFactory.componentClone(algoContainer, false);
						UIComponent containerChildClone;
						// String prefix = (parentContainer != null &&
						// !parentContainer.isEmpty()) ? parentContainer + "-" :
						// "form-";
						if (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ENTITYCHILDREN")) {
							containerChildClone = ComponentFactory.componentClone(containerChild, false);
							containerChildClone.getChildren().clear();
							containerChildClone.getChildren().add(table);
							algoContainerCloned.getChildren().clear();
							algoContainerCloned.getChildren().add(containerChildClone);
							new ComponentFactory(table, elementPanel, parent, entitySelected.getName(), elementsContainerMap, ent, requirement, appType).getComponentCreated();
							elementsContainerMap.put(parent + "-list-" + ent.getName(), containerChildClone.getChildren());
						}
						containerChildClone = ComponentFactory.componentClone(containerChild, false);
						containerChildClone.getChildren().clear();
						containerChildClone.getChildren().add(fieldSet);
						algoContainerCloned.getChildren().clear();
						algoContainerCloned.getChildren().add(containerChildClone);
						new ComponentFactory(fieldSet, elementPanel, parent, entitySelected.getName(), elementsContainerMap, ent, requirement, (devEntityPropertyDescriptor.getPropertyType().equalsIgnoreCase("ONEINTERNALOBJECT") ? AppType.FORM : appType)).getComponentCreated();
						elementsContainerMap.put(parent + "-form-" + ent.getName(), containerChildClone.getChildren());
						mapParam.clear();
						mapParam.put("legend", "");
						// mapParam.put("styleClass", "");
						// ComponentFactory.updateElementProperties(f2,
						// fieldSet, mapParam, null);
						mapParam.clear();
						int colSise = BigDecimal.valueOf(ent.getEntityPropertyDescriptorList().size() / 8f).setScale(0, RoundingMode.UP).intValue() * 2;
						mapParam.put("columns", String.valueOf(colSise));
						// ComponentFactory.updateElementProperties(f2,
						// fieldSet.getC hildren().get(0), mapParam, null);

					} else if (tab != null) {
						DevEntityClass ent = devEntityPropertyDescriptor.getPropertyClass();
						mapParam.put("title", devEntityPropertyDescriptor.getPropertyLabel());
						ComponentFactory.updateElementProperties(f2, tab, mapParam, null);
						UIComponent fieldSet = null;
						for (UIComponent child : elementPanel.getChildren()) {
							if (Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getClass())) {
								if (!child.getChildren().isEmpty() && Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getChildren().get(0).getClass())) {
									fieldSet = ComponentFactory.componentClone(child, false);
									break;
								}
							}
						}
						UIComponent table = null;
						for (UIComponent child : elementPanel.getChildren()) {
							if (Class.forName("javax.faces.component.UIData").isAssignableFrom(child.getClass())) {
								if (!child.getChildren().isEmpty() && Class.forName("javax.faces.component.UIColumn").isAssignableFrom(child.getChildren().get(0).getClass())) {
									table = ComponentFactory.componentClone(child, false);
									break;
								}
							}
						}
						tab.getChildren().add(table);
						new ComponentFactory(table, elementPanel, "", parentEntity, elementsContainerMap, ent, requirement, appType).getComponentCreated();
						/*
						 * tab.getChildren().add(fieldSet); new
						 * ComponentFactory(fieldSet, elementPanel, "",
						 * elementsContainerMap, ent,
						 * ent.getEntityPropertyDescriptorList
						 * ()).getComponentCreated(); List<UIComponent> comList
						 * = new ArrayList<UIComponent>();
						 * comList.add(fieldSet);
						 * elementsContainerMap.put("form-"+ent.getName(),
						 * comList); mapParam.clear(); mapParam.put("legend",
						 * ""); ComponentFactory.updateElementProperties(f2,
						 * fieldSet, mapParam, null); mapParam.clear();
						 * mapParam.put("columns", "4");
						 * ComponentFactory.updateElementProperties(f2,
						 * fieldSet.getChildren().get(0), mapParam, null);
						 */
					}
				} catch (ClassNotFoundException ex) {
					Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
				}
				// propertyArrayStr +=
				// devEntityPropertyDescriptor.getPropertyName();
			}
			mapParam.clear();
			int colSise = BigDecimal.valueOf(fieldOptionsList.size() / 8f).setScale(0, RoundingMode.UP).intValue() * 2;
			mapParam.put("columns", String.valueOf(colSise));
			// ComponentFactory.updateElementProperties(f2, comp, mapParam,
			// null);
			mapParam.clear();
			mapParam.put("header", "#{msg['registration']}: " + entitySelected.getLabel());
			// mapParam.put("styleClass", "");
			ComponentFactory.updateElementProperties(f2, comp, mapParam, null);

			String node = "";
			if (!parentEntity.isEmpty()) {
				node = parentEntity + ".";
			}
			String str = "#{app.beanListMap.get('" + String.valueOf(node + entitySelected.getName()).toLowerCase() + "')}";
			String str3 = "#{app.beanListFilteredMap.get('" + String.valueOf(node + entitySelected.getName()).toLowerCase() + "')}";
			String str2 = "#{app.beanSel}";
			mapParam.clear();
			mapParam.put("value", str);
			mapParam.put("rowKey", "#{item.hashCode()}");
			mapParam.put("filteredValue", str3);
			// mapParam.put("selection", str2);
			// mapParam.put("selectionMode", "single");
			mapParam.put("paginator", "true");
			mapParam.put("rows", "10");
			mapParam.put("sortMode", "multiple");
			mapParam.put("tableStyle", "width:990px;");
			mapParam.put("style", "width:990px;");
			mapParam.put("scrollable", "true");
			mapParam.put("scrollWidth", "1010");
			mapParam.put("scrollHeight", "210");
			mapParam.put("widgetVar", entitySelected.getName() + "DataTable");

			componentCreated = comp;
			ComponentFactory.updateElementProperties(f2, componentCreated, mapParam, null);

			if (panelInternalObject != null && !panelInternalObject.getChildren().isEmpty()) {
				comp.getParent().getChildren().add(panelInternalObject);
			}
			if (entitySelected.getEntityClassConfigList() != null && !entitySelected.getEntityClassConfigList().isEmpty()) {
				Map<String, String> entConfigMap = new HashMap<String, String>();
				for (DevEntityClassConfig entConfig : entitySelected.getEntityClassConfigList()) {
					entConfigMap.put(entConfig.getConfigName(), String.valueOf(entConfig.getConfigValue()));
				}
				if (entConfigMap.containsKey("loginAuth") && Boolean.valueOf(entConfigMap.get("loginAuth"))) {
					UIComponent fieldSet = null;
					UIComponent password = null;
					try {
						for (UIComponent child : elementPanel.getChildren()) {
							Object styleClassEntField = getProperty(child, "styleClass");
							if (styleClassEntField != null && String.valueOf(styleClassEntField).contains("data-form") && Class.forName("javax.faces.component.UIPanel").isAssignableFrom(child.getClass())) {
								fieldSet = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						UIComponent pwlabel = ComponentFactory.componentClone(label, false);
						mapParam.clear();
						mapParam.put("value", String.valueOf(BaseBean.getBundle("password", "msg")).toUpperCase() + ": ");
						ComponentFactory.updateElementProperties(f2, pwlabel, mapParam, null);
						for (UIComponent child : elementPanel.getChildren()) {
							Object styleClassEntField = getProperty(child, "styleClass");
							if (styleClassEntField != null && String.valueOf(styleClassEntField).contains("ui-password")) {
								password = ComponentFactory.componentClone(child, false);
								break;
							}
						}
						mapParam.clear();
						mapParam.put("validator", "#{app.validateLogin}");
						mapParam.put("value", "");
						mapParam.put("redisplay", "false");
						mapParam.put("autocomplete", "off");
						mapParam.put("required", true);
						mapParam.put("requiredMessage", "#{msg.required}: " + String.valueOf(BaseBean.getBundle("password", "msg")).toUpperCase());
						mapParam.put("tabindex", String.valueOf("1"));
						ComponentFactory.updateElementProperties(f2, password, mapParam, null);
						if (panelAuth == null) {
							panelAuth = ComponentFactory.componentClone(fieldSet, false);
							mapParam.clear();
							mapParam.put("legend", "");
							ComponentFactory.updateElementProperties(f2, panelAuth, mapParam, null);
						}
						panelAuth.getChildren().add(pwlabel);
						panelAuth.getChildren().add(password);
						comp.getParent().getChildren().add(panelAuth);
					} catch (ClassNotFoundException ex) {
						Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
			// doUICommandButtonFormFactory(comp.getParent(), elementPanel,
			// entitySelected, parentContainer, parentEntity, appType);
		} catch (NoSuchFieldException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static UIComponent cloneComp(Field f2, UIComponent comp3, FacesContext context) {
		UIComponent comp2 = null;
		try {
			if (comp3 instanceof com.sun.faces.facelets.compiler.UIInstructions) {
				comp2 = new HtmlOutputText();
				((HtmlOutputText) comp2).setValue(comp3);
			} else {
				comp2 = comp3.getClass().newInstance();
			}

			HashMap<String, PropertyDescriptor> hm = (HashMap) f2.get(comp3);
			for (Map.Entry<String, PropertyDescriptor> object : hm.entrySet()) {
				String object1 = object.getKey();
				PropertyDescriptor object2 = object.getValue();
				if (object2.getReadMethod() != null) {
					try {
						object2.getReadMethod().setAccessible(true);
						Object o = object2.getReadMethod().invoke(comp3);
						if (object1.equals("children")) {
							List<UIComponent> childrenList = (List<UIComponent>) o;
							for (int i = 0; i < childrenList.size(); i++) {
								UIComponent comp3a = (UIComponent) childrenList.get(i);
								if (comp3a instanceof com.sun.faces.facelets.compiler.UIInstructions) {
									HtmlOutputText out = new HtmlOutputText();
									out.setValue(comp3a);
									comp3a = out;
								}
								comp2.getChildren().add(cloneComp(f2, comp3a, context));
							}
						} else if (object1.equals("facets")) {
							for (Map.Entry<String, UIComponent> objectFacets : ((HashMap<String, UIComponent>) o).entrySet()) {
								String objectMap1 = objectFacets.getKey();
								UIComponent objectMap2 = objectFacets.getValue();
								if (objectMap2 instanceof com.sun.faces.facelets.compiler.UIInstructions) {
									HtmlOutputText out = new HtmlOutputText();
									out.setValue(objectMap2);
									objectMap2 = out;
								}
								comp2.getFacets().put(objectMap1, cloneComp(f2, objectMap2, context));
							}
						} else {
							Class c = object2.getReadMethod().getDeclaringClass();
							if (object2.getWriteMethod() != null) {
								if (o != null && !c.getName().equals("javax.faces.component.UIComponentBase") && !c.getName().equals("javax.faces.component.UIComponent") && !c.getName().equals("com.sun.faces.facelets.compiler.UILeaf")) {
									object2.getWriteMethod().invoke(comp2, o);
								}
							}
						}
					} catch (java.lang.reflect.InvocationTargetException e) {
						System.out.println("Exception:" + e.getMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (comp2.getId() == null) {
				comp2.setId("ad_" + context.getViewRoot().createUniqueId());
			}
			comp2.setParent(null);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(AdmAlgodevBean.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			return comp2;
		}
	}

	public static void updateElementProperties(UIComponent comp3, Map mapParam) {
		try {
			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			updateElementProperties(f2, comp3, mapParam, null);
		} catch (NoSuchFieldException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void updateElementProperties(Field f2, UIComponent comp3, Map mapParam, Map<String, String[]> elementPropertiesSelected) {
		try {
			if (elementPropertiesSelected == null) {
				elementPropertiesSelected = new TreeMap<String, String[]>();
			}
			elementPropertiesSelected.clear();
			HashMap<String, PropertyDescriptor> hm = (HashMap) f2.get(comp3);
			for (Map.Entry<String, PropertyDescriptor> object : hm.entrySet()) {
				String object1 = object.getKey();
				PropertyDescriptor object2 = object.getValue();
				if (object2.getReadMethod() != null) {
					try {
						object2.getReadMethod().setAccessible(true);
						Object o;
						ValueExpression valExp = comp3.getValueExpression(object1);
						if (valExp != null) {
							o = valExp;
						} else {
							try {
								o = object2.getReadMethod().invoke(comp3);
							} catch (java.lang.reflect.InvocationTargetException e) {
								continue;
							}
						}
						String str = "";
						if (object1.equals("children")) {
							List<UIComponent> childrenList = (List<UIComponent>) o;
							// for (int i = 0; i < childrenList.size(); i++) {
							// UIComponent comp3a = (UIComponent)
							// childrenList.get(i);
							// comp2.getChildren().add(cloneComp(f2, comp3a,
							// context, comp2));
							// /}
						} else {
							Class c = object2.getReadMethod().getDeclaringClass();
							if (object2.getWriteMethod() != null) {
								String className = object2.getWriteMethod().getParameterTypes()[0].getName();
								if (!c.getName().equals("javax.faces.component.UIComponentBase") && !c.getName().equals("javax.faces.component.UIComponent")) {
									Object param = mapParam.get(object1);
									if (object1.equals("tabindex") && !(Class.forName("javax.faces.component.UICommand").isAssignableFrom(comp3.getClass()) || Class.forName("javax.faces.component.UIInput").isAssignableFrom(comp3.getClass()))) {
										continue;
									}
									if (mapParam.containsKey(object1)) {
										String nameClass = object2.getWriteMethod().getParameterTypes()[0].getSimpleName();
										if (String.valueOf(param).isEmpty()) {
											o = null;
										} else {
											o = param;
										}
										try {
											comp3.setValueExpression(object1, (ValueExpression) null);
										} catch (Exception e) {
											System.err.println(e.getMessage());
										}

										if (className.equals("javax.faces.component.UIComponent")) {
											if (o != null) {
												if (o instanceof UIComponent) {
												}
												HtmlOutputText out = new HtmlOutputText();
												out.setValue(o);
												o = out;
											} else {
												comp3.getFacets().remove(object1);
											}
											// elementPropertiesSelected.put(object1,
											// o);
											// continue;
										}
										if (o != null) {
											if (className.equals("javax.faces.convert.Converter")) {
												FacesContext facesContext = FacesContext.getCurrentInstance();
												if (String.valueOf(o).contains("#")) {
													o = (javax.faces.convert.Converter) facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), String.valueOf(o), Converter.class).getValue(facesContext.getELContext());
													o = facesContext.getApplication().createConverter(o.getClass());
												} else {
													o = facesContext.getApplication().createConverter(String.valueOf(o));
												}
											} else if (className.equals("javax.faces.validator.Validator")) {
												FacesContext facesContext = FacesContext.getCurrentInstance();
												if (String.valueOf(o).contains("#")) {
													o = (javax.faces.validator.Validator) facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), String.valueOf(o), Validator.class).getValue(facesContext.getELContext());
													// o =
													// facesContext.getApplication().createValidator(o.getClass());
												} else {
													o = facesContext.getApplication().createValidator(String.valueOf(o));
												}
											} else if (className.equals("javax.faces.el.MethodBinding")) {
												FacesContext facesContext = FacesContext.getCurrentInstance();
												if (object1.equals("validator")) {
													o = Application.class.getMethod("createMethodBinding", String.class, new Class<?>[] {}.getClass()).invoke(facesContext.getApplication(), String.valueOf(o), new Class<?>[] { FacesContext.class, UIComponent.class, Object.class });
												} else {
													o = Application.class.getMethod("createMethodBinding", String.class, new Class<?>[] {}.getClass()).invoke(facesContext.getApplication(), String.valueOf(o), new Class<?>[] {});
												}
												// value = new
												// MethodExpression();
											} else if (className.equals("javax.el.MethodExpression")) {
												FacesContext facesContext = FacesContext.getCurrentInstance();
												o = facesContext.getApplication().getExpressionFactory().createMethodExpression(facesContext.getELContext(), String.valueOf(o), null, new Class<?>[] {});
											} else if (className.equals("java.util.List")) {
											} else if (String.valueOf(o).contains("#")) {
												FacesContext facesContext = FacesContext.getCurrentInstance();
												o = facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), String.valueOf(o), String.class);
												comp3.setValueExpression(object1, (ValueExpression) o);
											} else if (nameClass.equals("boolean")) {
												o = Boolean.valueOf(param.toString());
											} else if (nameClass.equals("int")) {
												o = Integer.valueOf(param.toString());
											} else if (nameClass.equals("double")) {
												o = Double.valueOf(param.toString());
											}
										}
										if (!(o instanceof ValueExpression)) {
											try {
												object2.getWriteMethod().invoke(comp3, o);
											} catch (java.lang.reflect.InvocationTargetException e) {
												String msg = e.getMessage();
											} catch (java.lang.IllegalArgumentException e) {
												String msg = e.getMessage();
											} catch (Throwable e) {
												e.printStackTrace();
											}
										}
									}
									str = getPropertyString(o, className);
									elementPropertiesSelected.put(object1, new String[] { str, className });
								}
							}
						}
					} catch (java.lang.reflect.InvocationTargetException e) {
						System.out.println("Exception:" + e.getMessage());
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
			if (mapParam instanceof java.util.AbstractMap) {
				try {
					mapParam.clear();					
				} catch (UnsupportedOperationException e) {
					// TODO: handle exception
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static UIComponent findComponentByStyleClass(String styleClass, UIComponent elementContainer) {
		Object styleClassObj = null;
		UIComponent returnComp = null;
		if(elementContainer==null){
			return null;
		}
		styleClassObj = ComponentFactory.getProperty(elementContainer, "styleClass");
		if (styleClassObj instanceof javax.el.ValueExpression) {
			styleClassObj = ((javax.el.ValueExpression) styleClassObj).getValue(FacesContext.getCurrentInstance().getELContext());
		}
		String[] styleArray = String.valueOf(styleClassObj).split(" ");
		String[] styleArrayArg = String.valueOf(styleClass).split(" ");
		boolean hasClass = true;
		if (styleClassObj != null) {
			for (int i = 0; i < styleArrayArg.length; i++) {
				String itemClass = styleArrayArg[i];
				if (!Arrays.asList(styleArray).contains(itemClass)) {
					hasClass = false;
					break;
				}

			}
			if (hasClass) {
				return elementContainer;
			}
		}
		for (UIComponent child : elementContainer.getChildren()) {
			returnComp = findComponentByStyleClass(styleClass, child);
			if (returnComp != null) {
				return returnComp;
			}
		}
		return null;
	}

	public static UIComponent findComponentByClassName(String className, UIComponent elementContainer) {
		UIComponent returnComp = null;
		try {
			if (Class.forName(className).isAssignableFrom(elementContainer.getClass())) {
				return elementContainer;
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (UIComponent child : elementContainer.getChildren()) {
			// styleClassObj = ComponentFactory.getProperty(child,
			// "styleClass");
			returnComp = findComponentByClassName(className, child);
			if (returnComp != null) {
				return returnComp;
			}
		}
		return null;
	}

	public static Object getProperty(UIComponent comp, String key) {
		Object o = null;
		try {
			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			HashMap<String, PropertyDescriptor> hm = (HashMap) f2.get(comp);
			for (Map.Entry<String, PropertyDescriptor> object : hm.entrySet()) {
				String object1 = object.getKey();
				PropertyDescriptor object2 = object.getValue();
				if (object1.equals(key) && object2.getReadMethod() != null) {
					try {
						object2.getReadMethod().setAccessible(true);

						ValueExpression valExp = comp.getValueExpression(object1);
						if (valExp != null) {
							o = valExp;
						} else {
							o = object2.getReadMethod().invoke(comp);
						}
					} catch (Exception e) {
					}
					break;
				}
			}
		} catch (NoSuchFieldException ex) {
			// Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE,
			// null, ex);
		} catch (SecurityException ex) {
			// Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE,
			// null, ex);
		} catch (IllegalArgumentException ex) {
			// Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE,
			// null, ex);
		} catch (IllegalAccessException ex) {
			// Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE,
			// null, ex);
		}
		return o;
	}

	public static boolean hasExpression(UIComponent comp) {
		//Object o = null;
		try {
			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			HashMap<String, PropertyDescriptor> hm = (HashMap) f2.get(comp);
			for (Map.Entry<String, PropertyDescriptor> object : hm.entrySet()) {
				String object1 = object.getKey();
				PropertyDescriptor object2 = object.getValue();
				if (object2.getReadMethod() != null) {
					Object o = null;
					ValueExpression valEx = comp.getValueExpression(object1);
					if (valEx != null) {
						o = valEx;
					} else {
						try {
							o = object2.getReadMethod().invoke(comp);
						} catch (java.lang.reflect.InvocationTargetException e) {
							// System.out.println("Exception:" +
							// e.getMessage());
							continue;
						}
					}				
					if (!(o instanceof String) && object2.getReadMethod() != null) {
						try {
							object2.getReadMethod().setAccessible(true);
							String className = object2.getWriteMethod().getParameterTypes()[0].getName();
							o = getPropertyString(o, className);
						} catch (Exception e) {
							continue;
						}
					}
					if(String.valueOf(o).startsWith("#{")){							
						return true;
					}
				}
			}
		} catch (NoSuchFieldException ex) {
			// Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE,
			// null, ex);
		} catch (SecurityException ex) {
			// Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE,
			// null, ex);
		} catch (IllegalArgumentException ex) {
			// Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE,
			// null, ex);
		} catch (IllegalAccessException ex) {
			// Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE,
			// null, ex);
		}
		return false;
	}
	
	
	public static String getPropertyString(Object o, String className) {
		if (o != null) {
			try {
				if (className.equals("javax.faces.component.UIComponent")) {
					if (o.getClass().isAssignableFrom(HtmlOutputText.class)) {
						o = ((HtmlOutputText) o).getValue();
					}
				}
				if (className.equals("javax.faces.convert.Converter")) {
					o = ((javax.faces.convert.Converter) o).getClass().getAnnotation(FacesConverter.class).value();
				}
				if (className.equals("javax.faces.validator.Validator")) {
					o = ((javax.faces.validator.Validator) o).getClass().getAnnotation(FacesValidator.class).value();
				}
				if (className.equals("javax.faces.el.MethodBinding")) {
					// o = ((javax.faces.el.MethodBinding)
					// o).getExpressionString();
					o = Class.forName("javax.faces.el.MethodBinding").getMethod("getExpressionString", new Class<?>[] {}).invoke(o, new Object[] {});
					// value = new MethodExpression();
				}
				if ((o instanceof javax.el.MethodExpression || className.equals("javax.el.MethodExpression")) && !String.valueOf(o).contains("#")) {
					o = ((javax.el.MethodExpression) o).getExpressionString();
				}
				if (o instanceof javax.el.ValueExpression) {
					o = ((javax.el.ValueExpression) o).getExpressionString();
				}
				if (o instanceof Collection) {
					o = "";
				}
			} catch (Exception ex) {
				Logger.getLogger(AdmAlgodevBean.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return String.valueOf(o);
	}

	public static UIComponent doFindComponent(UIComponent container, String id) throws Throwable {
		UIComponent found = null;
		for (Iterator<UIComponent> it = container.getFacetsAndChildren(); it.hasNext();) {
			UIComponent comp = it.next();
			if (comp.getId().equals(id)) {
				found = comp;
				break;
			} else if (comp.getFacetsAndChildren().hasNext()) {
				found = doFindComponent(comp, id);
				if (found != null) {
					break;
				}
			}
		}

		return found;
	}

	public static List<DevPrototypeComponentProperty> componentSerializer(Field f2, UIComponent comp3, Map mapParam, boolean repeatId) {
		List<DevPrototypeComponentProperty> prototypeComponentPropertyList = new ArrayList();
		// DevPrototypeComponentChildren prototypeComponentPropertyList = new
		// DevPrototypeComponentChildren();

		try {
			HashMap<String, PropertyDescriptor> hm = (HashMap) f2.get(comp3);
			for (Map.Entry<String, PropertyDescriptor> object : hm.entrySet()) {
				String object1 = object.getKey();
				PropertyDescriptor object2 = object.getValue();
				if (object2.getReadMethod() != null) {
					try {
						Object o;
						Class c = object2.getReadMethod().getDeclaringClass();
						ValueExpression valEx = comp3.getValueExpression(object1);
						if (valEx != null) {
							o = valEx;
						} else {
							try {
								o = object2.getReadMethod().invoke(comp3);
							} catch (java.lang.reflect.InvocationTargetException e) {
								// System.out.println("Exception:" +
								// e.getMessage());
								continue;
							}
						}

						if (object1.equals("children")) {
							List<UIComponent> childrenList = (List<UIComponent>) o;
							List<DevPrototypeComponentChildren> childrenArrayList = new ArrayList<DevPrototypeComponentChildren>();
							for (int i = 0; i < childrenList.size(); i++) {
								UIComponent comp3a = (UIComponent) childrenList.get(i);
								// comp2.getChildren().add(cloneComp(f2, comp3a,
								// context, comp2));
								childrenArrayList.add(new DevPrototypeComponentChildren(comp3a.getClass().getName(), componentSerializer(f2, comp3a, mapParam, repeatId)));
							}
							if (!childrenArrayList.isEmpty()) {
								prototypeComponentPropertyList.add(new DevPrototypeComponentProperty(object1, childrenArrayList.getClass().getName(), null, childrenArrayList, null, null));
							}
						} else if (object1.equals("facets")) {
							List<DevPrototypeComponentFacets> facetsArrayList = new ArrayList<DevPrototypeComponentFacets>();
							for (Map.Entry<String, UIComponent> objectFacets : ((HashMap<String, UIComponent>) o).entrySet()) {
								String objectMap1 = objectFacets.getKey();
								UIComponent objectMap2 = objectFacets.getValue();
								if (objectMap2 instanceof com.sun.faces.facelets.compiler.UIInstructions) {
									HtmlOutputText out = new HtmlOutputText();
									out.setValue(objectMap2);
									objectMap2 = out;
								}
								facetsArrayList.add(new DevPrototypeComponentFacets(objectMap1, objectMap2.getClass().getName(), componentSerializer(f2, objectMap2, mapParam, repeatId)));
							}
							if (!facetsArrayList.isEmpty()) {
								prototypeComponentPropertyList.add(new DevPrototypeComponentProperty(object1, facetsArrayList.getClass().getName(), null, null, facetsArrayList, null));
							}
						} else if (object1.equals("clientBehaviors")) {
							List<DevPrototypeComponentBehaviors> behaviorsArrayList = new ArrayList<DevPrototypeComponentBehaviors>();

							for (Map.Entry<String, List<ClientBehavior>> objectFacets : ((Map<String, List<ClientBehavior>>) o).entrySet()) {
								String objectMap1 = objectFacets.getKey();
								List<ClientBehavior> objectMap2List = objectFacets.getValue();
								if (!objectMap2List.isEmpty()) {
									ClientBehavior objectMap2 = objectMap2List.get(0);
									behaviorsArrayList.add(new DevPrototypeComponentBehaviors(objectMap1, objectMap2.getClass().getName(), componentSerializerBehavior(objectMap2)));
								}
							}
							if (!behaviorsArrayList.isEmpty()) {
								prototypeComponentPropertyList.add(new DevPrototypeComponentProperty(object1, behaviorsArrayList.getClass().getName(), null, null, null, behaviorsArrayList));
							}
						} else {

							if (object2.getWriteMethod() != null) {
								String className = object2.getWriteMethod().getParameterTypes()[0].getName();
								if (!c.getName().equals("javax.faces.component.UIComponentBase") && !c.getName().equals("javax.faces.component.UIComponent")) {
									Object param = mapParam != null ? mapParam.get(object1) : null;
									String nameClass = object2.getWriteMethod().getParameterTypes()[0].getSimpleName();
									// if (param != null) {
									if (nameClass.equals("boolean")) {
										o = Boolean.valueOf(Objects.toString(param, Objects.toString(o, null)));
										if (Objects.deepEquals(o, false)) {
											o = null;
										}
									} else if (nameClass.equals("int")) {
										o = Integer.valueOf(Objects.toString(param, Objects.toString(o, null)));
										if (Objects.deepEquals(o, 0)) {
											o = null;
										}
									} else {
										o = param != null ? param : o;
									}
									// object2.getWriteMethod().invoke(comp3,
									// o);
									// }
									if (o != null) {
										o = ComponentFactory.getPropertyString(o, className);
										prototypeComponentPropertyList.add(new DevPrototypeComponentProperty(object1, object2.getWriteMethod().getParameterTypes()[0].getName(), String.valueOf(o), null, null, null));
									}
									// elementPropertiesSelected.put(object1,
									// new String[]{String.valueOf(o),
									// className});
								} else if (object1.equals("id") && (String.valueOf(o).isEmpty() || !String.valueOf(o).contains("j_id") || repeatId)) {
									prototypeComponentPropertyList.add(new DevPrototypeComponentProperty(object1, object2.getWriteMethod().getParameterTypes()[0].getName(), String.valueOf(o), null, null, null));
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (IllegalArgumentException ex) {
			Logger.getLogger(AdmAlgodevBean.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(AdmAlgodevBean.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			return prototypeComponentPropertyList;
		}
	}

	public static UIComponent componentDeserializer(DevPrototypeComponentChildren component) {
		UIComponent comp2 = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			if (String.valueOf(component.getPrototypeComponentType()).equals("com.sun.faces.facelets.compiler.UIInstructions")) {
				comp2 = new com.sun.faces.facelets.compiler.UIInstructions(ELText.parse(""), new XMLInstruction[] { new XMLInstruction(ELText.parse("")) });
			} else {
				comp2 = (UIComponent) Class.forName(String.valueOf(component.getPrototypeComponentType())).newInstance();
			}
			// HashMap<String, Object[]> property = (HashMap<String, Object[]>)
			// component[1];
			List<DevPrototypeComponentProperty> property = component.getPrototypeComponentPropertyList();

			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			Map<String, DevPrototypeComponentProperty> objectAuxMap = new HashMap();
			for (DevPrototypeComponentProperty objectAux : property) {
				objectAuxMap.put(objectAux.getPropertyName(), objectAux);
			}

			HashMap<String, PropertyDescriptor> hm = (HashMap) f2.get(comp2);
			for (Map.Entry<String, PropertyDescriptor> objectComp : hm.entrySet()) {
				Object value = null;
				String object1 = objectComp.getKey();
				DevPrototypeComponentProperty object = objectAuxMap.get(object1);
				PropertyDescriptor object2 = objectComp.getValue();
				if (object != null) {
					value = object.getPropertyValue();
				} else {
					object = new DevPrototypeComponentProperty();
				}
				if (object2.getReadMethod() != null) {
					try {
						if (object1.equals("selected")) {
							String d = "";
							// continue;
						}
						object2.getReadMethod().setAccessible(true);
						// Object value = object2.getReadMethod().invoke(comp3);
						if (object1.equals("children")) {
							List<DevPrototypeComponentChildren> children = object.getChildren();
							// Collection<Object[]> childrenArrayCol =
							// (HashSet<Object[]>) value;
							for (Iterator<DevPrototypeComponentChildren> it = children.iterator(); it.hasNext();) {
								DevPrototypeComponentChildren objects = it.next();
								comp2.getChildren().add(componentDeserializer(objects));
							}
						} else if (object1.equals("facets")) {
							List<DevPrototypeComponentFacets> facets = object.getFacets();
							// Collection<Object[]> childrenArrayCol =
							// (HashSet<Object[]>) value;
							for (Iterator<DevPrototypeComponentFacets> it = facets.iterator(); it.hasNext();) {
								DevPrototypeComponentFacets objects = it.next();
								comp2.getFacets().put(objects.getPrototypeComponentName(), componentDeserializer(new DevPrototypeComponentChildren(objects.getPrototypeComponentType(), objects.getPrototypeComponentPropertyList())));
							}
						} else if (object1.equals("clientBehaviors")) {
							List<DevPrototypeComponentBehaviors> facets = object.getBehaviors();
							// Collection<Object[]> childrenArrayCol =
							// (HashSet<Object[]>) value;
							for (Iterator<DevPrototypeComponentBehaviors> it = facets.iterator(); it.hasNext();) {
								DevPrototypeComponentBehaviors objects = it.next();
								((UIComponentBase) comp2).addClientBehavior(objects.getPrototypeComponentName(), componentDeserializerBehavior(new DevPrototypeComponentBehaviors(objects.getPrototypeComponentName(), objects.getPrototypeComponentType(), objects.getPrototypeComponentPropertyList())));
							}
						} else {
							Class c = object2.getReadMethod().getDeclaringClass();
							if (facesContext != null && object2.getWriteMethod() != null) {
								String className = object2.getWriteMethod().getParameterTypes()[0].getName();
								if (!c.getName().equals("javax.faces.component.UIComponentBase") && !c.getName().equals("javax.faces.component.UIComponent")) {
									if (className.equals("boolean")) {
										value = Boolean.valueOf(Objects.toString(value, "false"));
									} else if (className.equals("int")) {
										value = Integer.parseInt(Objects.toString(value, "0"));
									} else if (value == null || String.valueOf(value).equals("null")) {
										continue;
									} else if (className.equals("double")) {
										value = Double.parseDouble(String.valueOf(value));
									} else if (className.equals("java.lang.Long")) {
										value = Long.parseLong(String.valueOf(value));
									} else if (className.equals("java.util.Map")) {
										continue;
									} else if (className.equals("java.util.List")) {
										continue;
									} else if (className.equals("javax.faces.convert.Converter")) {
										if (String.valueOf(value).contains("#")) {
											value = (javax.faces.convert.Converter) facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), String.valueOf(value), Converter.class).getValue(facesContext.getELContext());
											value = facesContext.getApplication().createConverter(value.getClass());
										} else {
											value = facesContext.getApplication().createConverter(String.valueOf(value));
										}
									} else if (className.equals("javax.faces.validator.Validator")) {
										if (String.valueOf(value).contains("#")) {
											value = (javax.faces.validator.Validator) facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), String.valueOf(value), Validator.class).getValue(facesContext.getELContext());
											value = facesContext.getApplication().createConverter(value.getClass());
										} else {
											value = facesContext.getApplication().createValidator(String.valueOf(value));
										}
									} else if (className.equals("javax.faces.component.UIComponent")) {
										/*
										 * if (value != null) { HtmlOutputText
										 * out = new HtmlOutputText();
										 * out.setValue(value); value = out; }
										 * else {
										 * comp2.getFacets().remove(object1); }
										 */
										continue;
									} else if (className.equals("javax.faces.el.MethodBinding")) {
										if (object1.equals("validator")) {
											value = Application.class.getMethod("createMethodBinding", String.class, new Class<?>[] {}.getClass()).invoke(facesContext.getApplication(), String.valueOf(value), new Class<?>[] { FacesContext.class, UIComponent.class, Object.class });
										} else {
											value = Application.class.getMethod("createMethodBinding", String.class, new Class<?>[] {}.getClass()).invoke(facesContext.getApplication(), String.valueOf(value), new Class<?>[] {});
										}
										// value = new MethodExpression();
									} else if (className.equals("javax.el.MethodExpression")) {
										value = facesContext.getApplication().getExpressionFactory().createMethodExpression(facesContext.getELContext(), String.valueOf(value), null, new Class<?>[] {});
									} else if (String.valueOf(value).contains("#")) {
										value = facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), String.valueOf(value), Object.class);
										comp2.setValueExpression(object1, (ValueExpression) value);
										continue;
									}

									try {
										object2.getWriteMethod().invoke(comp2, value);
									} catch (java.lang.IllegalArgumentException e) {
										System.out.println("EXCEPTION IN WRITE METHOD: " + e.getMessage());
									}
								} else if (object1.equals("id") && value != null) {
									comp2.setId(String.valueOf(value));
								}
							}
						}
					} catch (java.lang.reflect.InvocationTargetException e) {
						System.out.println("Exception:" + e.getMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (facesContext != null && comp2.getId() == null) {
				SecureRandom prng = SecureRandom.getInstance("SHA1PRNG","SUN");
				//prng.setSeed(Integer.MAX_VALUE);
				comp2.setId("j_id" +comp2.getClass().getSimpleName()+Long.toHexString(System.nanoTime()) );
			}
			comp2.setParent(null);
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			return comp2;
		}
	}
	
	private static List<DevPrototypeComponentProperty> componentSerializerBehavior(ClientBehavior behavior) {
		// DevPrototypeComponentBehaviors devPrototypeComponentBehaviors = new
		// DevPrototypeComponentBehaviors();
		List<DevPrototypeComponentProperty> devPrototypeComponentPropertyList = new ArrayList<DevPrototypeComponentProperty>();
		DevPrototypeComponentProperty property = new DevPrototypeComponentProperty();
		// Object[] objs =
		// (Object[])behavior.saveState(FacesContext.getCurrentInstance());
		// List<BehaviorListener> listeners =
		// (List<BehaviorListener>)ClientBehaviorBase.class.getDeclaredField("listeners").get(behavior);
		// BehaviorListener bl = listeners.get(0);

		// HashMap<String, PropertyDescriptor> props =
		// generatePropertyDescriptorMap(behavior);
		HashMap<String, HashMap<String, Object>> hm = generatePropertyDescriptorMap(behavior);
		;
		for (Map.Entry<String, HashMap<String, Object>> object : hm.entrySet()) {
			String object1 = object.getKey();
			HashMap<String, Object> object2 = object.getValue();
			Method readMethod = (Method) object2.get("readMethod");
			Method writeMethod = (Method) object2.get("writeMethod");
			if (readMethod != null) {
				property = new DevPrototypeComponentProperty();
				Object o = null;
				try {
					o = readMethod.invoke(behavior);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (o != null) {
					String className = readMethod.getReturnType().getName();
					property.setPropertyName(object1);
					property.setPropertyType(className);
					property.setPropertyValue(ComponentFactory.getPropertyString(o, className));
					devPrototypeComponentPropertyList.add(property);
				}
			}
		}

		try {
			Field f = behavior.getClass().getSuperclass().getSuperclass().getDeclaredField("listeners");
			f.setAccessible(true);
			ArrayList obj = (ArrayList) f.get(behavior);
			if (obj != null) {
				Object o = obj.get(0);
				property = new DevPrototypeComponentProperty();
				property.setPropertyName("listeners");
				property.setPropertyType(obj.getClass().getName());
				property.setPropertyValue(objectToJson(o).toString());
				devPrototypeComponentPropertyList.add(property);
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			throw new IllegalArgumentException("Object null in 201407061837.", e);
		}
		return devPrototypeComponentPropertyList;
	}

	private static ClientBehavior componentDeserializerBehavior(DevPrototypeComponentBehaviors devPrototypeComponentBehaviors) {
		ClientBehavior comp2 = null;
		try {
			comp2 = (ClientBehavior) Class.forName(String.valueOf(devPrototypeComponentBehaviors.getPrototypeComponentType())).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// behavior = new AjaxBehavior();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// ((AjaxBehavior)behavior).getClass().getMethods()[0].
		List<DevPrototypeComponentProperty> devPrototypeComponentPropertyList = devPrototypeComponentBehaviors.getPrototypeComponentPropertyList();
		for (int i = 0; i < devPrototypeComponentPropertyList.size(); i++) {
			DevPrototypeComponentProperty object = devPrototypeComponentPropertyList.get(i);
			HashMap<String, HashMap<String, Object>> hm = generatePropertyDescriptorMap(comp2);
			// if(property.getPropertyName().equals("listener")){
			// ValueExpression value =
			// facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(),
			// String.valueOf(property.getPropertyValue()), Object.class);
			// ((AjaxBehavior)behavior).setValueExpression("listener", value);
			// }
			String object1 = object.getPropertyName();
			HashMap<String, Object> object2 = hm.get(object1);
			Method readMethod = (Method) object2.get("readMethod");
			Method writeMethod = (Method) object2.get("writeMethod");
			String className = String.valueOf(object.getPropertyType());
			Object value = object.getPropertyValue();
			if (object1.equals("listeners")) {
				Object obj = jsonToObject(value);
				List l = new ArrayList();
				l.add(obj);
				Field f = null;
				try {
					f = comp2.getClass().getSuperclass().getSuperclass().getDeclaredField("listeners");
					f.setAccessible(true);
					f.set(comp2, l);
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			if (readMethod != null) {
				Class c = readMethod.getDeclaringClass();
				if (facesContext != null && writeMethod != null) {
					if (value != null && !String.valueOf(value).equals("null") && !c.getName().equals("javax.faces.component.UIComponentBase") && !c.getName().equals("javax.faces.component.UIComponent")) {
						if (className.equals("boolean")) {
							value = Boolean.valueOf(String.valueOf(value));
						} else if (className.equals("int")) {
							value = Integer.parseInt(String.valueOf(value));
						} else if (className.equals("double")) {
							value = Double.parseDouble(String.valueOf(value));
						} else if (className.equals("java.lang.Long")) {
							value = Long.parseLong(String.valueOf(value));
						}
						try {
							writeMethod.invoke(comp2, value);
						} catch (java.lang.IllegalArgumentException e) {
							System.out.println("EXCEPTION IN WRITE METHOD: " + e.getMessage());
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}
		}
		return comp2;
	}

	private static Object jsonToObject(Object json) {
		JSONObject rootNode = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			if (json instanceof String) {
				rootNode = new JSONObject(String.valueOf(json));
			}
			if (json instanceof JSONObject) {
				rootNode = (JSONObject) json;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// rootNode.keys();
		// for (Iterator iterator = rootNode.keys(); iterator.hasNext();) {
		// String type = (String) iterator.next();
		try {
			// Object ob = rootNode.get("type");
			String tpe = (String) rootNode.get("type");
			Class clazz = Class.forName(tpe);
			Object val = rootNode.get("value");

			if (val instanceof JSONObject) {
				Object o = null;
				if (MethodExpression.class.isAssignableFrom(clazz)) {
					String valStr = val.toString();
					int start = valStr.indexOf("#{");
					int end = valStr.indexOf("}", start) + 1;
					String expr = valStr.substring(start, end);
					if(fc!=null){
						o = fc.getApplication().getExpressionFactory().createMethodExpression(fc.getELContext(), expr, null, new Class<?>[] {});						
					}else{
						o = expr;
					}
					return o;
				} else {
					o = clazz.newInstance();
				}
				JSONObject child = (JSONObject) val;
				for (Iterator iterator = child.keys(); iterator.hasNext();) {
					String fieldName = (String) iterator.next();
					Field f = clazz.getDeclaredField(fieldName);
					f.setAccessible(true);
					try {
						f.set(o, jsonToObject(child.get(fieldName)));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return o;
			} else {
				return clazz.getConstructor(String.class).newInstance(val);
			}

			// if (ob instanceof JSONObject) {
			// JSONObject new_Json = (JSONObject) ob;
			// String tpe = (String) new_Json.get("type");
			// Object val = new_Json.get("value");
			// }else{

			// }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i = 0;
		// }
		return null;
	}

	private static Object objectToJson(Object o) {
		JSONObject rootNode = new JSONObject();
		JSONObject childNode = new JSONObject();
		try {
			Field[] fs = null;
			if (o == null) {
				return null;
			}
			rootNode.put("type", o.getClass().getName());
			if ((fs = o.getClass().getDeclaredFields()).length == 0 || o instanceof Number || o instanceof String) {
				rootNode.put("value", o);
				return rootNode;
			}
			rootNode.put("value", childNode);
			for (int i = 0; i < fs.length; i++) {
				Field field = fs[i];
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);
				Object of = null;
				try {
					of = field.get(o);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					// JSONObject object = new JSONObject();
					// object.put("type", field.getType().getName());
					// object.put("value", objectToJson(of));
					childNode.putOpt(field.getName(), objectToJson(of));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return rootNode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static HashMap<String, HashMap<String, Object>> generatePropertyDescriptorMap(ClientBehavior obj) {
		Field[] fields = null;
		Method[] methods = null;
		Object value = null;
		HashMap<String, Object> prop = new HashMap<String, Object>();
		HashMap<String, HashMap<String, Object>> propMap = new HashMap<String, HashMap<String, Object>>();
		Class clazz = obj.getClass();
		while (true) {
			fields = clazz.getDeclaredFields();
			methods = clazz.getDeclaredMethods();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				try {
					field.get(obj);
					String fieldName = field.getName();
					Method setMethod = null;
					Method getMethod = null;
					// prop = new PropertyDescriptor(field.getName(), clazz);
					prop = new HashMap<String, Object>();
					prop.put("propertyName", field.getName());
					new PropertyDescriptor(field.getName(), clazz, null, null);
					for (int j = 0; j < methods.length; j++) {
						Method method = methods[j];
						String methodLc = method.getName().toLowerCase();
						if (method.getName().toLowerCase().endsWith(fieldName)) {
							if (("get" + fieldName).equals(methodLc) || ("is" + fieldName).equals(methodLc)) {
								getMethod = method;
								prop.put("readMethod", method);
							} else if (("set" + fieldName).equals(methodLc)) {
								prop.put("writeMethod ", method);
							}
						}
						if (prop.containsKey("readMethod") && prop.containsKey("writeMethod")) {
							break;
						}
					}
					// prop = new PropertyDescriptor(field.getName(), clazz,
					// getMethod, setMethod);
					propMap.put(field.getName(), prop);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IntrospectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (clazz.getSuperclass() != null) {
				clazz = clazz.getSuperclass();
			} else {
				break;
			}
		}
		return propMap;
	}

	public static UIComponent componentClone(UIComponent elementSelected, boolean repeatId) {
		UIComponent cloned = null;
		try {
			Field f2 = UIComponentBase.class.getDeclaredField("pdMap");
			f2.setAccessible(true);
			List<DevPrototypeComponentProperty> property = ComponentFactory.componentSerializer(f2, elementSelected, null, repeatId);
			cloned = ComponentFactory.componentDeserializer(new DevPrototypeComponentChildren(elementSelected.getClass().getName(), property));
		} catch (NoSuchFieldException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(ComponentFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
		return cloned;
	}

	public static String parentContainerName(String currentContainer) {
		String parentContainer = "";
		String[] array = currentContainer.split("-");
		for (int i = 0; i < array.length - 2; i++) {
			parentContainer = (parentContainer.isEmpty() ? "" : parentContainer.concat("-")).concat(array[i]);
		}
		return parentContainer;
	}
	
	public static Collection<String> getRenderIds(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		PartialViewContext partialViewContext = facesContext.getPartialViewContext();
		Collection<String> renderIds = partialViewContext.getRenderIds();
		return renderIds;
	}
	
	public static void resetComponent(UIComponent ...components){
		try {
			if(components==null || components.length == 0){
				List<UIComponent> componentsList = new ArrayList<UIComponent>();
				FacesContext facesContext = FacesContext.getCurrentInstance();
				UIViewRoot viewRoot = facesContext.getViewRoot();
				Collection<String> renderIds = getRenderIds();
				for (String renderId : renderIds) {
					UIComponent component = viewRoot.findComponent(renderId);
					componentsList.add(component);
				}	
				components = componentsList.toArray(new UIComponent[]{});
			}
			
			for (UIComponent uiComponent : components) {
				if(uiComponent!=null){
					if (uiComponent.isRendered() && uiComponent instanceof EditableValueHolder) {
						EditableValueHolder input = (EditableValueHolder) uiComponent;
						input.resetValue();
					}else{
						List<UIComponent> componentsChildrenList = uiComponent.getChildren();
						if(!componentsChildrenList.isEmpty()){						
							resetComponent(componentsChildrenList.toArray(new UIComponent[]{}));
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public enum AppType {

		LIST_FORM("listForm"), FORM_LIST("formList"), FORM("form"), SUMM("summ"), LIST_EDIT_FORM("listEditForm"),EMPTY("empty");
		private String appTypeName;

		AppType(String appTypeName) {
			this.appTypeName = appTypeName;
		}

		public String getRegistrationTypeName() {
			return appTypeName;
		}

		public void setRegistrationTypeName(String appTypeName) {
			this.appTypeName = appTypeName;
		}

		private static final Map<String, AppType> lookup = new HashMap();

		// Populate the lookup table on loading time
		static {
			for (AppType s : EnumSet.allOf(AppType.class))
				lookup.put(s.getRegistrationTypeName(), s);
		}

		// This method can be used for reverse lookup purpose
		public static AppType get(String type) {
			return lookup.get(type);
		}

		public boolean hasList() {
			return appTypeName.toLowerCase().contains("list");
		}
	}
}

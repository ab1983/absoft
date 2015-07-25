package com.algoboss.integration.fazenda.gov;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Created by Alvarenga Fabiano on 12/05/2015.
 * http://www.guj.com.br/java/261084-leitura-de-xml-de-nfe-com-jdom
 */
public class NFeXmlHelp {

	public static TNfeProc generate(String xml) {

		// Caminho do arquivo XML da NFe
		String xmlFilePathNFe3 = "src//33150400106206000109550000000221031470060710-procNfe.xml";
		JAXBContext context = null;
		TNfeProc tNfeProc = null;

		try {

			// Realizando o parser do XML da NFe para Objeto Java
			context = JAXBContext.newInstance(TNfeProc.class.getPackage().getName());
			Unmarshaller unmarshaller1 = context.createUnmarshaller();
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
			// Este é o seu Objeto Java da NFe (tNfeProc)
			tNfeProc = (TNfeProc) unmarshaller1.unmarshal(bais);

			// Iterando na lista de produtos da NFe
			for (TNFe.InfNFe.Det item : tNfeProc.getNFe().getInfNFe().getDet())
				System.out.println("Item: " + item.getNItem() + " Codigo: " + item.getProd().getCProd() + " Descricao: " + item.getProd().getXProd() + " Valor: " + item.getProd().getVProd());

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return tNfeProc;
	}
	
	public static <T> String generate(T obj){
		  try {
			  
				//File file = new File("C:\\file.xml");
				JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		 
				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		 
				//jaxbMarshaller.marshal(customer, file);
				StringWriter sw = new StringWriter(); 
				jaxbMarshaller.marshal(obj, sw);
				return new String(sw.toString().getBytes(Charset.defaultCharset()),"UTF8");
		      } catch (JAXBException e) {
		    	  e.printStackTrace();
		      } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return null;
	}

}

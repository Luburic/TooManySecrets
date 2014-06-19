//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5.1 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.18 at 12:55:44 AM CEST 
//


package crlBanks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


import util.NSPrefixMapper;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="firm" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="date">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}date">
 *                         &lt;minInclusive value="1900-01-01"/>
 *                         &lt;maxInclusive value="2200-01-01"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="certificateID" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "firm"
})
@XmlRootElement(name = "crlBank")
public class CrlBank {

    protected List<CrlBank.Firm> firm;

    /**
     * Gets the value of the firm property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the firm property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFirm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CrlBank.Firm }
     * 
     * 
     */
    public List<CrlBank.Firm> getFirm() {
        if (firm == null) {
            firm = new ArrayList<CrlBank.Firm>();
        }
        return this.firm;
    }
    
    public void setFirm(List<CrlBank.Firm> firms) {
    	this.firm = firms;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="date">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}date">
     *               &lt;minInclusive value="1900-01-01"/>
     *               &lt;maxInclusive value="2200-01-01"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="certificateID" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "date",
        "certificateID"
    })
    public static class Firm {

        @XmlElement(required = true)
        protected String name;
        @XmlElement(required = true, type = String.class)
        @XmlJavaTypeAdapter(Adapter1 .class)
        protected Date date;
        @XmlElement(required = true)
        protected List<String> certificateID = new ArrayList<>();;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the date property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public Date getDate() {
            return date;
        }

        /**
         * Sets the value of the date property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDate(Date value) {
            this.date = value;
        }

        /**
         * Gets the value of the certificateID property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the certificateID property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCertificateID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getCertificateID() {
            if (certificateID == null) {
                certificateID = new ArrayList<String>();
            }
            return this.certificateID;
        }
        
        public void addCertificateID(String id) {
        	if(!this.certificateID.contains(id))
        		this.certificateID.add(id);
        }

    }
    
    public Firm getFirmByName(String name){
		Firm ret = null;
		for(Firm f: getFirm()){
			if(f.getName().equals(name))
				ret = f;				
		}
		return ret;
	}

	public Firm removeFirm(String name){
		Firm ret = getFirmByName(name);
		if(ret != null) {
			firm.remove(ret);
			return ret;
		}
		return null;
	}
	
	public void addFirm(Firm firmTemp){
		Firm temp = null;
		for(Firm f: getFirm()){
			if(f.getName().equals(firmTemp.getName())){
				temp = f;
			}				
		}
		if(temp == null) {
			getFirm().add(firmTemp);
		} else {
			removeFirm(temp.getName());
			temp.addCertificateID(firmTemp.getCertificateID().get(0));
			getFirm().add(temp);
		}
	}
    
	public static CrlBank load(String name) {
		
		//XML -> Objektni model
		
		try {
			//Definisemo kontekst, tj. paket(e) u kome se nalaze bean-ovi
			JAXBContext context1 = JAXBContext.newInstance("crlBanks");
			//Klasa za XML -> objektni model
			Unmarshaller unmarshaller = context1.createUnmarshaller();
			//Transformacija u objektni model
			CrlBank crlBanka = (CrlBank) unmarshaller.unmarshal(new File("./xsdSchemas/crl"+name+".xml"));
			//ispis na ekran
			System.out.println(crlBanka);
			return crlBanka;
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		//Sad moze da se menja objeknti model
	}
	
	public void store(List<CrlBank.Firm> firms, String name) {
		
		//Objeknti model -> XML
		
		//Definisemo kontekst, tj. paket(e) u kome se nalaze bean-ovi
		//Posto je isti kontekst moze se koristiti i klasa context1
		try {
			JAXBContext context2 = JAXBContext.newInstance("crlBanks");
			//Klasa za transformisanje objektnog modela u XML
			Marshaller marshaller = context2.createMarshaller();
			//na ovaj naci se setuje koji prefiks se koristi za koji namespace
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NSPrefixMapper());
			//da se radi identacija
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			//serijaliacija na System.out stream
			CrlBank crlBank = new CrlBank();
			crlBank.setFirm(firms);
			marshaller.marshal(crlBank, new BufferedWriter(new FileWriter(new File("./xsdSchemas/crl"+name+".xml"))));
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

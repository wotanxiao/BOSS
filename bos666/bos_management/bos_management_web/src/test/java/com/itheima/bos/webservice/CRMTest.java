package com.itheima.bos.webservice;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;

import com.itheima.crm.domain.Customer;

public class CRMTest {

	public static void main(String[] args) {
		List<Customer> list = (List<Customer>) WebClient
				.create("http://localhost:8180/crm/webService/customerService/findAll").type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).getCollection(Customer.class);

		for (Customer customer : list) {
			System.out.println(customer);

		}

	}

}

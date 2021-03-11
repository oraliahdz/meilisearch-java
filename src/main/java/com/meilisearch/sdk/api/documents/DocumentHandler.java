package com.meilisearch.sdk.api.documents;

import com.meilisearch.sdk.ServiceTemplate;
import com.meilisearch.sdk.exceptions.MeiliSearchRuntimeException;
import com.meilisearch.sdk.http.factory.RequestFactory;
import com.meilisearch.sdk.http.request.HttpMethod;

import java.util.Collections;
import java.util.List;

public class DocumentHandler<T> {
	private final ServiceTemplate serviceTemplate;
	private final RequestFactory requestFactory;
	private final String indexName;
	private final Class<?> indexModel;

	public DocumentHandler(ServiceTemplate serviceTemplate, RequestFactory requestFactory, String indexName, Class<?> indexModel) {
		this.serviceTemplate = serviceTemplate;
		this.requestFactory = requestFactory;
		this.indexName = indexName;
		this.indexModel = indexModel;
	}

	/**
	 * Retrieve a document with a specific identifier
	 *
	 * @param identifier the identifier of the document you are looking for
	 * @return the document specified by the identifier
	 * @throws com.meilisearch.sdk.exceptions.MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public T getDocument(String identifier) throws MeiliSearchRuntimeException {
		String requestQuery = "/indexes/" + indexName + "/documents/" + identifier;
		return serviceTemplate.execute(
			requestFactory.create(HttpMethod.GET, requestQuery, Collections.emptyMap(), null),
			indexModel
		);
	}

	/**
	 * Retrieve a list of documents
	 *
	 * @return a list of Documents from the index.
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public List<T> getDocuments() throws MeiliSearchRuntimeException {
		return getDocuments(20);
	}

	/**
	 * Retrieve a list of documents
	 *
	 * @param limit maximum number of documents to be returned
	 * @return a list of Documents from the index.
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public List<T> getDocuments(int limit) throws MeiliSearchRuntimeException {
		String requestQuery = "/indexes/" + indexName + "/documents?limit=" + limit;
		return serviceTemplate.execute(
			requestFactory.create(HttpMethod.GET, requestQuery, Collections.emptyMap(), null),
			List.class,
			indexModel
		);
	}

	/**
	 * Add or replace a document
	 *
	 * @param data an already serialized document
	 * @return an Update object with the updateId
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public Update addDocument(String data) throws MeiliSearchRuntimeException {
		String requestQuery = "/indexes/" + indexName + "/documents";
		return serviceTemplate.execute(
			requestFactory.create(HttpMethod.POST, requestQuery, Collections.emptyMap(), data),
			Update.class
		);
	}

	/**
	 * Add or replace a batch of documents
	 *
	 * @param data a list of document objects
	 * @return an Update object with the updateId
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public Update addDocument(List<T> data) throws MeiliSearchRuntimeException {
		try {
			String dataString = serviceTemplate.getProcessor().encode(data);
			return addDocument(dataString);
		} catch (Exception e) {
			throw new MeiliSearchRuntimeException(e);
		}
	}

	/**
	 * Add or replace a document
	 *
	 * @param data the serialized document
	 * @return an Update object with the updateId
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public Update replaceDocument(String data) throws MeiliSearchRuntimeException {
		return addDocument(data);
	}

	/**
	 * Add or replace a batch of documents
	 *
	 * @param data a list of document objects
	 * @return an Update object with the updateId
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public Update replaceDocument(List<T> data) throws MeiliSearchRuntimeException {
		try {
			String dataString = serviceTemplate.getProcessor().encode(data);
			return replaceDocument(dataString);
		} catch (Exception e) {
			throw new MeiliSearchRuntimeException(e);
		}
	}

	/**
	 * @param data the serialized document
	 * @return an Update object with the updateId
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public Update updateDocument(String data) throws MeiliSearchRuntimeException {
		String requestQuery = "/indexes/" + indexName + "/documents";
		return serviceTemplate.execute(
			requestFactory.create(HttpMethod.PUT, requestQuery, Collections.emptyMap(), data),
			Update.class
		);
	}

	/**
	 * Delete a document with a specific identifier
	 *
	 * @param identifier the id of the document
	 * @return an Update object with the updateId
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public Update deleteDocument(String identifier) throws MeiliSearchRuntimeException {
		String requestQuery = "/indexes/" + indexName + "/documents/" + identifier;
		return serviceTemplate.execute(
			requestFactory.create(HttpMethod.DELETE, requestQuery, Collections.emptyMap(), null),
			Update.class
		);
	}

	/**
	 * Delete a batch of documents
	 * @return an Update object with the updateId
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public Update deleteDocuments() throws MeiliSearchRuntimeException {
		String requestQuery = "/indexes/" + indexName + "/documents";
		return serviceTemplate.execute(
			requestFactory.create(HttpMethod.DELETE, requestQuery, Collections.emptyMap(), null),
			Update.class
		);
	}

	/**
	 * @param q the Querystring
	 * @return a SearchResponse with the Hits represented by the mapped Class for this index
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public SearchResponse<T> search(String q) throws MeiliSearchRuntimeException {
		try {
			String requestQuery = "/indexes/" + indexName + "/search";
			SearchRequest sr = new SearchRequest(q);
			return serviceTemplate.execute(
				requestFactory.create(HttpMethod.POST, requestQuery, Collections.emptyMap(), serviceTemplate.getProcessor().encode(sr)),
				SearchResponse.class,
				indexModel
			);
		} catch (Exception e) {
			throw new MeiliSearchRuntimeException(e);
		}
	}

	/**
	 * @param sr SearchRequest
	 * @return a SearchResponse with the Hits represented by the mapped Class for this index
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public SearchResponse<T> search(SearchRequest sr) throws MeiliSearchRuntimeException {
		try {
			String requestQuery = "/indexes/" + indexName + "/search";
			return serviceTemplate.execute(
				requestFactory.create(HttpMethod.POST, requestQuery,Collections.emptyMap(), serviceTemplate.getProcessor().encode(sr)),
				SearchResponse.class,
				indexModel
			);
		} catch (Exception e) {
			throw new MeiliSearchRuntimeException(e);
		}
	}

	/**
	 * Retrieve an update with a specific updated
	 *
	 * @param updateId the updateId
	 * @return the update belonging to the updateID
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public Update getUpdate(int updateId) throws MeiliSearchRuntimeException {
		String requestQuery = "/indexes/" + indexName + "/updates/" + updateId;
		return serviceTemplate.execute(
			requestFactory.create(HttpMethod.GET, requestQuery, Collections.emptyMap(), null),
			Update.class
		);
	}

	/**
	 * Retrieve a list containing all the updates
	 *
	 * @return a List of Updates
	 * @throws MeiliSearchRuntimeException in case something went wrong (http error, json exceptions, etc)
	 */
	public List<Update> getUpdates() throws MeiliSearchRuntimeException {
		String requestQuery = "/indexes/" + indexName + "/updates";
		return serviceTemplate.execute(
			requestFactory.create(HttpMethod.GET, requestQuery, Collections.emptyMap(), null),
			List.class,
			Update.class
		);
	}
}

begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
operator|.
name|METHOD
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|beans
operator|.
name|DocumentObjectBinder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|StreamingBinaryResponseParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|SolrPing
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|SolrPingResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|UpdateResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocumentList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Abstraction through which all communication with a Solr server may be routed  *  * @since 5.0, replaced {@code SolrServer}  */
end_comment

begin_class
DECL|class|SolrClient
specifier|public
specifier|abstract
class|class
name|SolrClient
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|binder
specifier|private
name|DocumentObjectBinder
name|binder
decl_stmt|;
comment|/**    * Adds a collection of documents    * @param docs  the collection of documents    * @throws IOException If there is a low-level I/O error.    */
DECL|method|add
specifier|public
name|UpdateResponse
name|add
parameter_list|(
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|add
argument_list|(
name|docs
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Adds a collection of documents, specifying max time before they become committed    * @param docs  the collection of documents    * @param commitWithinMs  max time (in ms) before a commit will happen     * @throws IOException If there is a low-level I/O error.    * @since solr 3.5    */
DECL|method|add
specifier|public
name|UpdateResponse
name|add
parameter_list|(
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
parameter_list|,
name|int
name|commitWithinMs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|req
operator|.
name|setCommitWithin
argument_list|(
name|commitWithinMs
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Adds a collection of beans    * @param beans  the collection of beans    * @throws IOException If there is a low-level I/O error.    */
DECL|method|addBeans
specifier|public
name|UpdateResponse
name|addBeans
parameter_list|(
name|Collection
argument_list|<
name|?
argument_list|>
name|beans
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|addBeans
argument_list|(
name|beans
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Adds a collection of beans specifying max time before they become committed    * @param beans  the collection of beans    * @param commitWithinMs  max time (in ms) before a commit will happen     * @throws IOException If there is a low-level I/O error.    * @since solr 3.5    */
DECL|method|addBeans
specifier|public
name|UpdateResponse
name|addBeans
parameter_list|(
name|Collection
argument_list|<
name|?
argument_list|>
name|beans
parameter_list|,
name|int
name|commitWithinMs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|DocumentObjectBinder
name|binder
init|=
name|this
operator|.
name|getBinder
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|beans
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|bean
range|:
name|beans
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|binder
operator|.
name|toSolrInputDocument
argument_list|(
name|bean
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|add
argument_list|(
name|docs
argument_list|,
name|commitWithinMs
argument_list|)
return|;
block|}
comment|/**    * Adds a single document    * @param doc  the input document    * @throws IOException If there is a low-level I/O error.    */
DECL|method|add
specifier|public
name|UpdateResponse
name|add
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|add
argument_list|(
name|doc
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Adds a single document specifying max time before it becomes committed    * @param doc  the input document    * @param commitWithinMs  max time (in ms) before a commit will happen     * @throws IOException If there is a low-level I/O error.    * @since solr 3.5    */
DECL|method|add
specifier|public
name|UpdateResponse
name|add
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|int
name|commitWithinMs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|req
operator|.
name|setCommitWithin
argument_list|(
name|commitWithinMs
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Adds a single bean    * @param obj  the input bean    * @throws IOException If there is a low-level I/O error.    */
DECL|method|addBean
specifier|public
name|UpdateResponse
name|addBean
parameter_list|(
name|Object
name|obj
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
return|return
name|addBean
argument_list|(
name|obj
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Adds a single bean specifying max time before it becomes committed    * @param obj  the input bean    * @param commitWithinMs  max time (in ms) before a commit will happen     * @throws IOException If there is a low-level I/O error.    * @since solr 3.5    */
DECL|method|addBean
specifier|public
name|UpdateResponse
name|addBean
parameter_list|(
name|Object
name|obj
parameter_list|,
name|int
name|commitWithinMs
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
return|return
name|add
argument_list|(
name|getBinder
argument_list|()
operator|.
name|toSolrInputDocument
argument_list|(
name|obj
argument_list|)
argument_list|,
name|commitWithinMs
argument_list|)
return|;
block|}
comment|/**    * Performs an explicit commit, causing pending documents to be committed for indexing    *<p>    * waitFlush=true and waitSearcher=true to be inline with the defaults for plain HTTP access    * @throws IOException If there is a low-level I/O error.    */
DECL|method|commit
specifier|public
name|UpdateResponse
name|commit
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Performs an explicit optimize, causing a merge of all segments to one.    *<p>    * waitFlush=true and waitSearcher=true to be inline with the defaults for plain HTTP access    *<p>    * Note: In most cases it is not required to do explicit optimize    * @throws IOException If there is a low-level I/O error.    */
DECL|method|optimize
specifier|public
name|UpdateResponse
name|optimize
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|optimize
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/**    * Performs an explicit commit, causing pending documents to be committed for indexing    * @param waitFlush  block until index changes are flushed to disk    * @param waitSearcher  block until a new searcher is opened and registered as the main query searcher, making the changes visible     * @throws IOException If there is a low-level I/O error.    */
DECL|method|commit
specifier|public
name|UpdateResponse
name|commit
parameter_list|(
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|setAction
argument_list|(
name|UpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Performs an explicit commit, causing pending documents to be committed for indexing    * @param waitFlush  block until index changes are flushed to disk    * @param waitSearcher  block until a new searcher is opened and registered as the main query searcher, making the changes visible    * @param softCommit makes index changes visible while neither fsync-ing index files nor writing a new index descriptor    * @throws IOException If there is a low-level I/O error.    */
DECL|method|commit
specifier|public
name|UpdateResponse
name|commit
parameter_list|(
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|,
name|boolean
name|softCommit
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|setAction
argument_list|(
name|UpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
name|softCommit
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Performs an explicit optimize, causing a merge of all segments to one.    *<p>    * Note: In most cases it is not required to do explicit optimize    * @param waitFlush  block until index changes are flushed to disk    * @param waitSearcher  block until a new searcher is opened and registered as the main query searcher, making the changes visible     * @throws IOException If there is a low-level I/O error.    */
DECL|method|optimize
specifier|public
name|UpdateResponse
name|optimize
parameter_list|(
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|optimize
argument_list|(
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/**    * Performs an explicit optimize, causing a merge of all segments to one.    *<p>    * Note: In most cases it is not required to do explicit optimize    * @param waitFlush  block until index changes are flushed to disk    * @param waitSearcher  block until a new searcher is opened and registered as the main query searcher, making the changes visible     * @param maxSegments  optimizes down to at most this number of segments    * @throws IOException If there is a low-level I/O error.    */
DECL|method|optimize
specifier|public
name|UpdateResponse
name|optimize
parameter_list|(
name|boolean
name|waitFlush
parameter_list|,
name|boolean
name|waitSearcher
parameter_list|,
name|int
name|maxSegments
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|setAction
argument_list|(
name|UpdateRequest
operator|.
name|ACTION
operator|.
name|OPTIMIZE
argument_list|,
name|waitFlush
argument_list|,
name|waitSearcher
argument_list|,
name|maxSegments
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Performs a rollback of all non-committed documents pending.    *<p>    * Note that this is not a true rollback as in databases. Content you have previously    * added may have been committed due to autoCommit, buffer full, other client performing    * a commit etc.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|rollback
specifier|public
name|UpdateResponse
name|rollback
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|rollback
argument_list|()
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Deletes a single document by unique ID    * @param id  the ID of the document to delete    * @throws IOException If there is a low-level I/O error.    */
DECL|method|deleteById
specifier|public
name|UpdateResponse
name|deleteById
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|deleteById
argument_list|(
name|id
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Deletes a single document by unique ID, specifying max time before commit    * @param id  the ID of the document to delete    * @param commitWithinMs  max time (in ms) before a commit will happen     * @throws IOException If there is a low-level I/O error.    * @since 3.6    */
DECL|method|deleteById
specifier|public
name|UpdateResponse
name|deleteById
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|commitWithinMs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|req
operator|.
name|setCommitWithin
argument_list|(
name|commitWithinMs
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Deletes a list of documents by unique ID    * @param ids  the list of document IDs to delete     * @throws IOException If there is a low-level I/O error.    */
DECL|method|deleteById
specifier|public
name|UpdateResponse
name|deleteById
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|deleteById
argument_list|(
name|ids
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Deletes a list of documents by unique ID, specifying max time before commit    * @param ids  the list of document IDs to delete     * @param commitWithinMs  max time (in ms) before a commit will happen     * @throws IOException If there is a low-level I/O error.    * @since 3.6    */
DECL|method|deleteById
specifier|public
name|UpdateResponse
name|deleteById
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|,
name|int
name|commitWithinMs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|deleteById
argument_list|(
name|ids
argument_list|)
expr_stmt|;
name|req
operator|.
name|setCommitWithin
argument_list|(
name|commitWithinMs
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Deletes documents from the index based on a query    * @param query  the query expressing what documents to delete    * @throws IOException If there is a low-level I/O error.    */
DECL|method|deleteByQuery
specifier|public
name|UpdateResponse
name|deleteByQuery
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|deleteByQuery
argument_list|(
name|query
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Deletes documents from the index based on a query, specifying max time before commit    * @param query  the query expressing what documents to delete    * @param commitWithinMs  max time (in ms) before a commit will happen     * @throws IOException If there is a low-level I/O error.    * @since 3.6    */
DECL|method|deleteByQuery
specifier|public
name|UpdateResponse
name|deleteByQuery
parameter_list|(
name|String
name|query
parameter_list|,
name|int
name|commitWithinMs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|deleteByQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|req
operator|.
name|setCommitWithin
argument_list|(
name|commitWithinMs
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Issues a ping request to check if the server is alive    * @throws IOException If there is a low-level I/O error.    */
DECL|method|ping
specifier|public
name|SolrPingResponse
name|ping
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|SolrPing
argument_list|()
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Performs a query to the Solr server    * @param params  an object holding all key/value parameters to send along the request    */
DECL|method|query
specifier|public
name|QueryResponse
name|query
parameter_list|(
name|SolrParams
name|params
parameter_list|)
throws|throws
name|SolrServerException
block|{
return|return
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Performs a query to the Solr server    * @param params  an object holding all key/value parameters to send along the request    * @param method  specifies the HTTP method to use for the request, such as GET or POST    */
DECL|method|query
specifier|public
name|QueryResponse
name|query
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|METHOD
name|method
parameter_list|)
throws|throws
name|SolrServerException
block|{
return|return
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|,
name|method
argument_list|)
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Query solr, and stream the results.  Unlike the standard query, this will     * send events for each Document rather then add them to the QueryResponse.    *    * Although this function returns a 'QueryResponse' it should be used with care    * since it excludes anything that was passed to callback.  Also note that    * future version may pass even more info to the callback and may not return     * the results in the QueryResponse.    *    * @since solr 4.0    */
DECL|method|queryAndStreamResponse
specifier|public
name|QueryResponse
name|queryAndStreamResponse
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|StreamingResponseCallback
name|callback
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|ResponseParser
name|parser
init|=
operator|new
name|StreamingBinaryResponseParser
argument_list|(
name|callback
argument_list|)
decl_stmt|;
name|QueryRequest
name|req
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|req
operator|.
name|setStreamingResponseCallback
argument_list|(
name|callback
argument_list|)
expr_stmt|;
name|req
operator|.
name|setResponseParser
argument_list|(
name|parser
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Retrieves the SolrDocument associated with the given identifier.    *    * @return retrieved SolrDocument, null if no document is found.    */
DECL|method|getById
specifier|public
name|SolrDocument
name|getById
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|SolrServerException
block|{
return|return
name|getById
argument_list|(
name|id
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Retrieves the SolrDocument associated with the given identifier and uses    * the SolrParams to execute the request.    *    * @return retrieved SolrDocument, null if no document is found.    */
DECL|method|getById
specifier|public
name|SolrDocument
name|getById
parameter_list|(
name|String
name|id
parameter_list|,
name|SolrParams
name|params
parameter_list|)
throws|throws
name|SolrServerException
block|{
name|SolrDocumentList
name|docs
init|=
name|getById
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|id
argument_list|)
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|docs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|docs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Retrieves the SolrDocuments associated with the given identifiers.    * If a document was not found, it will not be added to the SolrDocumentList.    */
DECL|method|getById
specifier|public
name|SolrDocumentList
name|getById
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
throws|throws
name|SolrServerException
block|{
return|return
name|getById
argument_list|(
name|ids
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Retrieves the SolrDocuments associated with the given identifiers and uses    * the SolrParams to execute the request.    * If a document was not found, it will not be added to the SolrDocumentList.    */
DECL|method|getById
specifier|public
name|SolrDocumentList
name|getById
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|,
name|SolrParams
name|params
parameter_list|)
throws|throws
name|SolrServerException
block|{
if|if
condition|(
name|ids
operator|==
literal|null
operator|||
name|ids
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must provide an identifier of a document to retrieve."
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|reqParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|reqParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
argument_list|)
condition|)
block|{
name|reqParams
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/get"
argument_list|)
expr_stmt|;
block|}
name|reqParams
operator|.
name|set
argument_list|(
literal|"ids"
argument_list|,
operator|(
name|String
index|[]
operator|)
name|ids
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|query
argument_list|(
name|reqParams
argument_list|)
operator|.
name|getResults
argument_list|()
return|;
block|}
comment|/**    * SolrServer implementations need to implement how a request is actually processed    */
DECL|method|request
specifier|public
specifier|abstract
name|NamedList
argument_list|<
name|Object
argument_list|>
name|request
parameter_list|(
specifier|final
name|SolrRequest
name|request
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
function_decl|;
DECL|method|getBinder
specifier|public
name|DocumentObjectBinder
name|getBinder
parameter_list|()
block|{
if|if
condition|(
name|binder
operator|==
literal|null
condition|)
block|{
name|binder
operator|=
operator|new
name|DocumentObjectBinder
argument_list|()
expr_stmt|;
block|}
return|return
name|binder
return|;
block|}
comment|/**    * Release allocated resources.    *    * @since solr 4.0    */
DECL|method|shutdown
specifier|public
specifier|abstract
name|void
name|shutdown
parameter_list|()
function_decl|;
block|}
end_class

end_unit


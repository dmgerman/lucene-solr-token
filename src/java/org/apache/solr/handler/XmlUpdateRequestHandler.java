begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|common
operator|.
name|SolrException
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
name|MapSolrParams
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
name|XML
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequestBase
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
name|request
operator|.
name|SolrQueryResponse
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessorChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_comment
comment|/**  * Add documents to solr using the STAX XML parser.  */
end_comment

begin_class
DECL|class|XmlUpdateRequestHandler
specifier|public
class|class
name|XmlUpdateRequestHandler
extends|extends
name|ContentStreamHandlerBase
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XmlUpdateRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|UPDATE_PROCESSOR
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_PROCESSOR
init|=
literal|"update.processor"
decl_stmt|;
comment|// XML Constants
DECL|field|ADD
specifier|public
specifier|static
specifier|final
name|String
name|ADD
init|=
literal|"add"
decl_stmt|;
DECL|field|DELETE
specifier|public
specifier|static
specifier|final
name|String
name|DELETE
init|=
literal|"delete"
decl_stmt|;
DECL|field|OPTIMIZE
specifier|public
specifier|static
specifier|final
name|String
name|OPTIMIZE
init|=
literal|"optimize"
decl_stmt|;
DECL|field|COMMIT
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT
init|=
literal|"commit"
decl_stmt|;
DECL|field|ROLLBACK
specifier|public
specifier|static
specifier|final
name|String
name|ROLLBACK
init|=
literal|"rollback"
decl_stmt|;
DECL|field|WAIT_SEARCHER
specifier|public
specifier|static
specifier|final
name|String
name|WAIT_SEARCHER
init|=
literal|"waitSearcher"
decl_stmt|;
DECL|field|WAIT_FLUSH
specifier|public
specifier|static
specifier|final
name|String
name|WAIT_FLUSH
init|=
literal|"waitFlush"
decl_stmt|;
DECL|field|OVERWRITE
specifier|public
specifier|static
specifier|final
name|String
name|OVERWRITE
init|=
literal|"overwrite"
decl_stmt|;
DECL|field|COMMIT_WITHIN
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_WITHIN
init|=
literal|"commitWithin"
decl_stmt|;
DECL|field|OVERWRITE_COMMITTED
specifier|public
specifier|static
specifier|final
name|String
name|OVERWRITE_COMMITTED
init|=
literal|"overwriteCommitted"
decl_stmt|;
comment|// @Deprecated
DECL|field|OVERWRITE_PENDING
specifier|public
specifier|static
specifier|final
name|String
name|OVERWRITE_PENDING
init|=
literal|"overwritePending"
decl_stmt|;
comment|// @Deprecated
DECL|field|ALLOW_DUPS
specifier|public
specifier|static
specifier|final
name|String
name|ALLOW_DUPS
init|=
literal|"allowDups"
decl_stmt|;
DECL|field|inputFactory
name|XMLInputFactory
name|inputFactory
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|inputFactory
operator|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
try|try
block|{
comment|// The java 1.6 bundled stax parser (sjsxp) does not currently have a thread-safe
comment|// XMLInputFactory, as that implementation tries to cache and reuse the
comment|// XMLStreamReader.  Setting the parser-specific "reuse-instance" property to false
comment|// prevents this.
comment|// All other known open-source stax parsers (and the bea ref impl)
comment|// have thread-safe factories.
name|inputFactory
operator|.
name|setProperty
argument_list|(
literal|"reuse-instance"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Other implementations will likely throw this exception since "reuse-instance"
comment|// isimplementation specific.
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to set the 'reuse-instance' property for the input chain: "
operator|+
name|inputFactory
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newLoader
specifier|protected
name|ContentStreamLoader
name|newLoader
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|)
block|{
return|return
operator|new
name|XMLLoader
argument_list|(
name|processor
argument_list|,
name|inputFactory
argument_list|)
return|;
block|}
comment|/**    * A Convenience method for getting back a simple XML string indicating    * success or failure from an XML formated Update (from the Reader)    *    * @since solr 1.2    * @deprecated Direct updates fro ma Reader, as well as the response     *             format produced by this method, have been deprecated     *             and will be removed in future versions.  Any code using    *             this method should be changed to use {@link #handleRequest}     *             method with a ContentStream.     */
annotation|@
name|Deprecated
DECL|method|doLegacyUpdate
specifier|public
name|void
name|doLegacyUpdate
parameter_list|(
name|Reader
name|input
parameter_list|,
name|Writer
name|output
parameter_list|)
block|{
try|try
block|{
name|SolrCore
name|core
init|=
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
decl_stmt|;
comment|// Old style requests do not choose a custom handler
name|UpdateRequestProcessorChain
name|processorFactory
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|SolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
block|{       }
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
comment|// ignored
name|XMLStreamReader
name|parser
init|=
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|processorFactory
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
name|XMLLoader
name|loader
init|=
operator|(
name|XMLLoader
operator|)
name|newLoader
argument_list|(
name|req
argument_list|,
name|processor
argument_list|)
decl_stmt|;
name|loader
operator|.
name|processUpdate
argument_list|(
name|processor
argument_list|,
name|parser
argument_list|)
expr_stmt|;
name|processor
operator|.
name|finish
argument_list|()
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
literal|"<result status=\"0\"></result>"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
try|try
block|{
name|SolrException
operator|.
name|logOnce
argument_list|(
name|log
argument_list|,
literal|"Error processing \"legacy\" update command"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|XML
operator|.
name|writeXML
argument_list|(
name|output
argument_list|,
literal|"result"
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|ex
argument_list|)
argument_list|,
literal|"status"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ee
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error writing to output stream: "
operator|+
name|ee
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Add documents with XML"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class

end_unit


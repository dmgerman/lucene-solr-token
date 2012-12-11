begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|response
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
name|util
operator|.
name|plugin
operator|.
name|PluginInfoInitialized
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
name|core
operator|.
name|PluginInfo
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
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * Manages a chain of UpdateRequestProcessorFactories.  *<p>  * Chain can be configured via solrconfig.xml:  *</p>  *<pre class="prettyprint">  *&lt;updateRequestProcessors name="key" default="true"&gt;  *&lt;processor class="PathToClass1" /&gt;  *&lt;processor class="PathToClass2" /&gt;  *&lt;processor class="solr.LogUpdateProcessorFactory"&gt;  *&lt;int name="maxNumToLog"&gt;100&lt;/int&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.RunUpdateProcessorFactory" /&gt;  *&lt;/updateRequestProcessors&gt;  *</pre>  *<p>  * Allmost all processor chains should end with an instance of   * {@link RunUpdateProcessorFactory} unless the user is explicitly   * executing the update commands in an alternative custom   *<code>UpdateRequestProcessorFactory</code>.  *</p>  *  * @see UpdateRequestProcessorFactory  * @see #init  * @see #createProcessor  * @since solr 1.3  */
end_comment

begin_class
DECL|class|UpdateRequestProcessorChain
specifier|public
specifier|final
class|class
name|UpdateRequestProcessorChain
implements|implements
name|PluginInfoInitialized
block|{
DECL|field|log
specifier|public
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UpdateRequestProcessorChain
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|chain
specifier|private
name|UpdateRequestProcessorFactory
index|[]
name|chain
decl_stmt|;
DECL|field|solrCore
specifier|private
specifier|final
name|SolrCore
name|solrCore
decl_stmt|;
DECL|method|UpdateRequestProcessorChain
specifier|public
name|UpdateRequestProcessorChain
parameter_list|(
name|SolrCore
name|solrCore
parameter_list|)
block|{
name|this
operator|.
name|solrCore
operator|=
name|solrCore
expr_stmt|;
block|}
comment|/**    * Initializes the chain using the factories specified by the<code>PluginInfo</code>.    * if the chain includes the<code>RunUpdateProcessorFactory</code>, but     * does not include an implementation of the     *<code>DistributingUpdateProcessorFactory</code> interface, then an     * instance of<code>DistributedUpdateProcessorFactory</code> will be     * injected immediately prior to the<code>RunUpdateProcessorFactory</code>.    *    * @see DistributingUpdateProcessorFactory    * @see RunUpdateProcessorFactory    * @see DistributedUpdateProcessorFactory    */
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
specifier|final
name|String
name|infomsg
init|=
literal|"updateRequestProcessorChain \""
operator|+
operator|(
literal|null
operator|!=
name|info
operator|.
name|name
condition|?
name|info
operator|.
name|name
else|:
literal|""
operator|)
operator|+
literal|"\""
operator|+
operator|(
name|info
operator|.
name|isDefault
argument_list|()
condition|?
literal|" (default)"
else|:
literal|""
operator|)
decl_stmt|;
comment|// wrap in an ArrayList so we know we know we can do fast index lookups
comment|// and that add(int,Object) is supported
name|List
argument_list|<
name|UpdateRequestProcessorFactory
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|(
name|solrCore
operator|.
name|initPlugins
argument_list|(
name|info
operator|.
name|getChildren
argument_list|(
literal|"processor"
argument_list|)
argument_list|,
name|UpdateRequestProcessorFactory
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|infomsg
operator|+
literal|" require at least one processor"
argument_list|)
throw|;
block|}
name|int
name|numDistrib
init|=
literal|0
decl_stmt|;
name|int
name|runIndex
init|=
operator|-
literal|1
decl_stmt|;
comment|// hi->lo incase multiple run instances, add before first one
comment|// (no idea why someone might use multiple run instances, but just in case)
for|for
control|(
name|int
name|i
init|=
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
literal|0
operator|<=
name|i
condition|;
name|i
operator|--
control|)
block|{
name|UpdateRequestProcessorFactory
name|factory
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|instanceof
name|DistributingUpdateProcessorFactory
condition|)
block|{
name|numDistrib
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|factory
operator|instanceof
name|RunUpdateProcessorFactory
condition|)
block|{
name|runIndex
operator|=
name|i
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|1
operator|<
name|numDistrib
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|infomsg
operator|+
literal|" may not contain more then one "
operator|+
literal|"instance of DistributingUpdateProcessorFactory"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|0
operator|<=
name|runIndex
operator|&&
literal|0
operator|==
name|numDistrib
condition|)
block|{
comment|// by default, add distrib processor immediately before run
name|DistributedUpdateProcessorFactory
name|distrib
init|=
operator|new
name|DistributedUpdateProcessorFactory
argument_list|()
decl_stmt|;
name|distrib
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|runIndex
argument_list|,
name|distrib
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"inserting DistributedUpdateProcessorFactory into "
operator|+
name|infomsg
argument_list|)
expr_stmt|;
block|}
name|chain
operator|=
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|UpdateRequestProcessorFactory
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a chain backed directly by the specified array. Modifications to     * the array will affect future calls to<code>createProcessor</code>    */
DECL|method|UpdateRequestProcessorChain
specifier|public
name|UpdateRequestProcessorChain
parameter_list|(
name|UpdateRequestProcessorFactory
index|[]
name|chain
parameter_list|,
name|SolrCore
name|solrCore
parameter_list|)
block|{
name|this
operator|.
name|chain
operator|=
name|chain
expr_stmt|;
name|this
operator|.
name|solrCore
operator|=
name|solrCore
expr_stmt|;
block|}
comment|/**    * Uses the factories in this chain to creates a new     *<code>UpdateRequestProcessor</code> instance specific for this request.      * If the<code>DISTRIB_UPDATE_PARAM</code> is present in the request and is     * non-blank, then any factory in this chain prior to the instance of     *<code>{@link DistributingUpdateProcessorFactory}</code> will be skipped,     * and the<code>UpdateRequestProcessor</code> returned will be from that     *<code>DistributingUpdateProcessorFactory</code>    *    * @see UpdateRequestProcessorFactory#getInstance    * @see DistributingUpdateProcessorFactory#DISTRIB_UPDATE_PARAM    */
DECL|method|createProcessor
specifier|public
name|UpdateRequestProcessor
name|createProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|UpdateRequestProcessor
name|processor
init|=
literal|null
decl_stmt|;
name|UpdateRequestProcessor
name|last
init|=
literal|null
decl_stmt|;
specifier|final
name|String
name|distribPhase
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|skipToDistrib
init|=
operator|!
name|distribPhase
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|chain
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|processor
operator|=
name|chain
index|[
name|i
index|]
operator|.
name|getInstance
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|last
argument_list|)
expr_stmt|;
name|last
operator|=
name|processor
operator|==
literal|null
condition|?
name|last
else|:
name|processor
expr_stmt|;
if|if
condition|(
name|skipToDistrib
operator|&&
name|chain
index|[
name|i
index|]
operator|instanceof
name|DistributingUpdateProcessorFactory
condition|)
block|{
break|break;
block|}
block|}
return|return
name|last
return|;
block|}
comment|/**    * Returns the underlying array of factories used in this chain.      * Modifications to the array will affect future calls to     *<code>createProcessor</code>    */
DECL|method|getFactories
specifier|public
name|UpdateRequestProcessorFactory
index|[]
name|getFactories
parameter_list|()
block|{
return|return
name|chain
return|;
block|}
block|}
end_class

end_unit


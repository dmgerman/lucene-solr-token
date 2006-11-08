begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|core
operator|.
name|SolrInfoMBean
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
name|util
operator|.
name|StrUtils
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
name|NamedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Handler
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
name|ArrayList
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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

begin_comment
comment|/**    * A collection of params used in DisMaxRequestHandler,    both for Plugin initialization and * for Requests.    */
end_comment

begin_class
DECL|class|DisMaxParams
specifier|public
class|class
name|DisMaxParams
extends|extends
name|CommonParams
block|{
comment|/** query and init param for tiebreaker value */
DECL|field|TIE
specifier|public
specifier|static
name|String
name|TIE
init|=
literal|"tie"
decl_stmt|;
comment|/** query and init param for query fields */
DECL|field|QF
specifier|public
specifier|static
name|String
name|QF
init|=
literal|"qf"
decl_stmt|;
comment|/** query and init param for phrase boost fields */
DECL|field|PF
specifier|public
specifier|static
name|String
name|PF
init|=
literal|"pf"
decl_stmt|;
comment|/** query and init param for MinShouldMatch specification */
DECL|field|MM
specifier|public
specifier|static
name|String
name|MM
init|=
literal|"mm"
decl_stmt|;
comment|/** query and init param for Phrase Slop value */
DECL|field|PS
specifier|public
specifier|static
name|String
name|PS
init|=
literal|"ps"
decl_stmt|;
comment|/** query and init param for boosting query */
DECL|field|BQ
specifier|public
specifier|static
name|String
name|BQ
init|=
literal|"bq"
decl_stmt|;
comment|/** query and init param for boosting functions */
DECL|field|BF
specifier|public
specifier|static
name|String
name|BF
init|=
literal|"bf"
decl_stmt|;
comment|/** query and init param for filtering query      * @deprecated use SolrParams.FQ or SolrPluginUtils.parseFilterQueries      */
DECL|field|FQ
specifier|public
specifier|static
name|String
name|FQ
init|=
literal|"fq"
decl_stmt|;
comment|/** query and init param for field list */
DECL|field|GEN
specifier|public
specifier|static
name|String
name|GEN
init|=
literal|"gen"
decl_stmt|;
comment|/**      * the default tie breaker to use in DisjunctionMaxQueries      * @deprecated - use explicit default with SolrParams.getFloat      */
DECL|field|tiebreaker
specifier|public
name|float
name|tiebreaker
init|=
literal|0.0f
decl_stmt|;
comment|/**      * the default query fields to be used      * @deprecated - use explicit default with SolrParams.get      */
DECL|field|qf
specifier|public
name|String
name|qf
init|=
literal|null
decl_stmt|;
comment|/**      * the default phrase boosting fields to be used      * @deprecated - use explicit default with SolrParams.get      */
DECL|field|pf
specifier|public
name|String
name|pf
init|=
literal|null
decl_stmt|;
comment|/**      * the default min should match to be used      * @deprecated - use explicit default with SolrParams.get      */
DECL|field|mm
specifier|public
name|String
name|mm
init|=
literal|"100%"
decl_stmt|;
comment|/**      * the default phrase slop to be used       * @deprecated - use explicit default with SolrParams.getInt      */
DECL|field|pslop
specifier|public
name|int
name|pslop
init|=
literal|0
decl_stmt|;
comment|/**      * the default boosting query to be used      * @deprecated - use explicit default with SolrParams.get      */
DECL|field|bq
specifier|public
name|String
name|bq
init|=
literal|null
decl_stmt|;
comment|/**      * the default boosting functions to be used      * @deprecated - use explicit default with SolrParams.get      */
DECL|field|bf
specifier|public
name|String
name|bf
init|=
literal|null
decl_stmt|;
comment|/**      * the default filtering query to be used      * @deprecated - use explicit default with SolrParams.get      */
DECL|field|fq
specifier|public
name|String
name|fq
init|=
literal|null
decl_stmt|;
comment|/**      * Sets the params using values from a NamedList, usefull in the      * init method for your handler.      *      *<p>      * If any param is not of the expected type, a severe error is      * logged,and the param is skipped.      *</p>      *      *<p>      * If any param is not of in the NamedList, it is skipped and the      * old value is left alone.      *</p>      * @deprecated use SolrParams.toSolrParams      */
DECL|method|setValues
specifier|public
name|void
name|setValues
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|setValues
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Object
name|tmp
decl_stmt|;
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|TIE
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|Float
condition|)
block|{
name|tiebreaker
operator|=
operator|(
operator|(
name|Float
operator|)
name|tmp
operator|)
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a float: "
operator|+
name|TIE
argument_list|)
expr_stmt|;
block|}
block|}
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|QF
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
block|{
name|qf
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a str: "
operator|+
name|QF
argument_list|)
expr_stmt|;
block|}
block|}
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|PF
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
block|{
name|pf
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a str: "
operator|+
name|PF
argument_list|)
expr_stmt|;
block|}
block|}
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|MM
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
block|{
name|mm
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a str: "
operator|+
name|MM
argument_list|)
expr_stmt|;
block|}
block|}
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|PS
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|Integer
condition|)
block|{
name|pslop
operator|=
operator|(
operator|(
name|Integer
operator|)
name|tmp
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not an int: "
operator|+
name|PS
argument_list|)
expr_stmt|;
block|}
block|}
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|BQ
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
block|{
name|bq
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a str: "
operator|+
name|BQ
argument_list|)
expr_stmt|;
block|}
block|}
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|BF
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
block|{
name|bf
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a str: "
operator|+
name|BF
argument_list|)
expr_stmt|;
block|}
block|}
name|tmp
operator|=
name|args
operator|.
name|get
argument_list|(
name|FQ
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|tmp
condition|)
block|{
if|if
condition|(
name|tmp
operator|instanceof
name|String
condition|)
block|{
name|fq
operator|=
name|tmp
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
operator|.
name|log
operator|.
name|severe
argument_list|(
literal|"init param is not a str: "
operator|+
name|FQ
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit


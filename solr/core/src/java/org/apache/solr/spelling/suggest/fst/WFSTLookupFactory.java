begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
operator|.
name|fst
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|Lookup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
operator|.
name|*
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
name|spelling
operator|.
name|suggest
operator|.
name|LookupFactory
import|;
end_import

begin_comment
comment|/**  * Factory for {@link WFSTCompletionLookup}  * @lucene.experimental  */
end_comment

begin_class
DECL|class|WFSTLookupFactory
specifier|public
class|class
name|WFSTLookupFactory
extends|extends
name|LookupFactory
block|{
comment|/**    * If<code>true</code>, exact suggestions are returned first, even if they are prefixes    * of other strings in the automaton (possibly with larger weights).     */
DECL|field|EXACT_MATCH_FIRST
specifier|public
specifier|static
specifier|final
name|String
name|EXACT_MATCH_FIRST
init|=
literal|"exactMatchFirst"
decl_stmt|;
comment|/**    * File name for the automaton.    *     */
DECL|field|FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"wfst.bin"
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|Lookup
name|create
parameter_list|(
name|NamedList
name|params
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|boolean
name|exactMatchFirst
init|=
name|params
operator|.
name|get
argument_list|(
name|EXACT_MATCH_FIRST
argument_list|)
operator|!=
literal|null
condition|?
name|Boolean
operator|.
name|valueOf
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|EXACT_MATCH_FIRST
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
literal|true
decl_stmt|;
return|return
operator|new
name|WFSTCompletionLookup
argument_list|(
name|getTempDir
argument_list|()
argument_list|,
literal|"suggester"
argument_list|,
name|exactMatchFirst
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|storeFileName
specifier|public
name|String
name|storeFileName
parameter_list|()
block|{
return|return
name|FILENAME
return|;
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
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
name|Query
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
name|join
operator|.
name|ToChildBlockJoinQuery
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_class
DECL|class|BlockJoinChildQParser
specifier|public
class|class
name|BlockJoinChildQParser
extends|extends
name|BlockJoinParentQParser
block|{
DECL|method|BlockJoinChildQParser
specifier|public
name|BlockJoinChildQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|createQuery
specifier|protected
name|Query
name|createQuery
parameter_list|(
name|Query
name|parentListQuery
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
return|return
operator|new
name|ToChildBlockJoinQuery
argument_list|(
name|query
argument_list|,
name|getFilter
argument_list|(
name|parentListQuery
argument_list|)
operator|.
name|filter
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getParentFilterLocalParamName
specifier|protected
name|String
name|getParentFilterLocalParamName
parameter_list|()
block|{
return|return
literal|"of"
return|;
block|}
block|}
end_class

end_unit


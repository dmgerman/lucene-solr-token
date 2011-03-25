begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
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
name|solr
operator|.
name|search
operator|.
name|DocIterator
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_comment
comment|/**  * Environment variables for the transformed documents  *   * @version $Id: JSONResponseWriter.java 1065304 2011-01-30 15:10:15Z rmuir $  * @since solr 4.0  */
end_comment

begin_class
DECL|class|TransformContext
specifier|public
class|class
name|TransformContext
block|{
DECL|field|query
specifier|public
name|Query
name|query
decl_stmt|;
DECL|field|wantsScores
specifier|public
name|boolean
name|wantsScores
init|=
literal|false
decl_stmt|;
DECL|field|iterator
specifier|public
name|DocIterator
name|iterator
decl_stmt|;
DECL|field|searcher
specifier|public
name|SolrIndexSearcher
name|searcher
decl_stmt|;
block|}
end_class

end_unit


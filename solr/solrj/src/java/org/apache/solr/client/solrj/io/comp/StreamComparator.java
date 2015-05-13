begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.io.comp
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
operator|.
name|io
operator|.
name|comp
package|;
end_package

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
name|Comparator
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
name|io
operator|.
name|Tuple
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParameter
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Defines a comparator that can be expressed in an expression  */
end_comment

begin_class
DECL|class|StreamComparator
specifier|public
specifier|abstract
class|class
name|StreamComparator
implements|implements
name|Comparator
argument_list|<
name|Tuple
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|leftField
specifier|protected
name|String
name|leftField
decl_stmt|;
DECL|field|rightField
specifier|protected
name|String
name|rightField
decl_stmt|;
DECL|field|order
specifier|protected
specifier|final
name|ComparatorOrder
name|order
decl_stmt|;
DECL|method|StreamComparator
specifier|public
name|StreamComparator
parameter_list|(
name|String
name|field
parameter_list|,
name|ComparatorOrder
name|order
parameter_list|)
block|{
name|this
operator|.
name|leftField
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|rightField
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
block|}
DECL|method|StreamComparator
specifier|public
name|StreamComparator
parameter_list|(
name|String
name|leftField
parameter_list|,
name|String
name|rightField
parameter_list|,
name|ComparatorOrder
name|order
parameter_list|)
block|{
name|this
operator|.
name|leftField
operator|=
name|leftField
expr_stmt|;
name|this
operator|.
name|rightField
operator|=
name|rightField
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
block|}
block|}
end_class

end_unit

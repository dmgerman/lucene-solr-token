begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *   */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.eval
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
name|eval
package|;
end_package

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
name|util
operator|.
name|Locale
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
name|StreamExpression
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

begin_class
DECL|class|CoalesceEvaluator
specifier|public
class|class
name|CoalesceEvaluator
extends|extends
name|ComplexEvaluator
block|{
DECL|field|serialVersionUID
specifier|protected
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|CoalesceEvaluator
specifier|public
name|CoalesceEvaluator
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|expression
argument_list|,
name|factory
argument_list|)
expr_stmt|;
if|if
condition|(
name|subEvaluators
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid expression %s - expecting at least one value but found %d"
argument_list|,
name|expression
argument_list|,
name|subEvaluators
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|evaluate
specifier|public
name|Object
name|evaluate
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|StreamEvaluator
name|evaluator
range|:
name|subEvaluators
control|)
block|{
name|Object
name|result
init|=
name|evaluator
operator|.
name|evaluate
argument_list|(
name|tuple
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|result
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit
